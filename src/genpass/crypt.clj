(ns genpass.crypt
  (:require [clojure.java.io :as io]
            [genpass.randpass :as pass]
            [genpass.filehandler :as fileh]
            [snipsnap.core :as clipboard]
            [clojure.java.shell :only [sh] :as sh])
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

(defn gen-pw
  "User has authenticated, generate a new password,
  add the user:newpassword to the existing map to the file"
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

(defn get-pw
  "User has authenticated, get the password of a given user"
  [options password]
  (let [gotpassword (fileh/get-entry (filepath) password (:user options))]
    (if (:showpassword options)
      (println gotpassword)
      (do  (and (clipboard/set-text! gotpassword)
                 (println "Copied to clipboard"))
           (future (Thread/sleep (:time options)) 
                   (do (clipboard/set-text! " ")
                       (shutdown-agents)) nil))))
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
