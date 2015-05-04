(ns genpass.crypt
  (:require [clojure.java.io :as io]
            [genpass.randpass :as pass]
            [genpass.filehandler :as fileh]
            [snipsnap.core :as clipboard])
    (:gen-class))


(defn filepath
  "Return the full filepath of ~/.genpass"
  []
  (str (System/getProperty (str "user.home")) "/.genpass")
  )

(defn file-exists?
  "Check if the ~/.genpass file exists"
  []
  (.exists (io/as-file (filepath)))
  )

(defn parse-y-n
  "Return true on y/Y, false on n/N, nil on anything else"
  [ans]
  (cond (zero? (compare "y" (clojure.string/lower-case (str ans)))) true
        (zero? (compare "n" (clojure.string/lower-case (str ans)))) false
        :else nil)
  )

(defn confirm-overwrite
  "Accept a y/n input confirming to generate a new password over
  one that already exists"
  []
  (loop []
    (do (print "confirm overwrite y/n?> ") (flush))
    (let [reader (java.io.BufferedReader. *in*)
          ans (.readLine reader)
          res (parse-y-n ans)]
      (cond
       (true? res) true
       (false? res) false
       :else (recur))))
)  

(defn entry-exists?
  "Check if an entry exists"
 [options password]
 (let [gotpassword (fileh/get-entry (filepath) password (:user options))]
   (nil? gotpassword))
)

(defn exec-command
  "Exec a command"
  [options cmd]
  (do  (print "enter password>") (flush))
  (let [console (. System console)
        password (String. (.readPassword console))]
    (if (file-exists?)
      (if (fileh/valid-password? (filepath) password)
        (cmd options password)
        (println "Invalid password"))
      
      ;;If the ~/.genpass file does not exist
      ;;Then create it (data in the encrypted file is stored as CSV)
      ;;then the CSV is converted to a map.
      (do 
        (fileh/write-empty-file (filepath) password)
        (cmd options password))))
    )

(defn create-pw
  "Create a password, paste to clipboard or output to console"
  [options password]
  (let [genpass 
          (pass/create-password (:length options) (:verbosity options))]
      (fileh/write-new-entry (filepath) password (:user options) genpass)
      (if (:showpassword options)
        (println genpass)
        (do (and (clipboard/set-text! genpass)
                 (println "Copied to clipboard"))
            (future (Thread/sleep (:time options))
                    (do (clipboard/set-text! " ")
                        (shutdown-agents))))))
  )
(defn gen-pw
  "User has authenticated, generate a new password if none-exists or
  verify the user wants password to be overwritten and then
  add the user:newpassword to the existing map and write the file"
  [options password]
  (if (or (entry-exists? options password) (confirm-overwrite))
    (create-pw options password)
    (do (println "password not overwritten") (System/exit 0)))
    )

(defn get-pw
  "User has authenticated, get the password of a given user"
  [options password]
  (let [gotpassword (fileh/get-entry (filepath) password (:user options))]
    (if (empty? gotpassword)
      (println "No such password exists.")
      (if (:showpassword options)
        (println gotpassword)
        (do  (and (clipboard/set-text! gotpassword)
                  (println "Copied to clipboard"))
             (future (Thread/sleep (:time options)) 
                     (do (clipboard/set-text! " ")
                         (shutdown-agents)) nil)))))
  )

(defn rem-pw
  "User has authenticated, remove the user:password item and write changes"
  [options password]
  (fileh/write-rem-entry (filepath) password (:user options))
  (println "User removed"))

(defn list-pw
  "User has authenticated, list all mappings"
  [options password]
  (clojure.pprint/pprint (fileh/get-raw-data (filepath) password))
  )

(defn change-pw
  "User has authenticated, change the encryption password"
  [options password]
  (do  (print "enter new password>") (flush))
  (let [console (. System console)
        newpassword (String. (.readPassword console))]
    (fileh/change-encryption-write (filepath) password newpassword)
    )  
)
