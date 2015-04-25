(ns genpass.filehandler
  (:require [lock-key.core :refer [decrypt decrypt-as-str encrypt]]
            [clojure.string :as string]
            [clojure.test :refer :all])
  (:gen-class))

(defn retrieve-map
  "Generates a mapping of logins to passwords from un-encrypted text"
  [text]
  (into {} 
        (filter 
         #(= (count %) 2) 
         (map #(string/split % #",") (string/split text #"\n"))))
  )

(defn write-file 
  "Write encrypted-data at filepath"
  [encrypted-data filepath]
   (with-open [w (clojure.java.io/output-stream filepath)]
     (.write w encrypted-data))
   )

(defn write-empty-file
  "Write an empty .genpass file"
  [filepath password]
  (write-file (encrypt "" password) filepath)
  )

(defn read-file
  "Read an encrypted file at filepath"
  [filepath]
  (with-open [out (java.io.ByteArrayOutputStream.)]
    (clojure.java.io/copy (clojure.java.io/input-stream filepath) out)
    (.toByteArray out))
  )

(defn valid-password?
  "Check if we can decrypt the given file with given password"
  [filepath password]
  (try 
    (and (decrypt (read-file filepath) password) true)
    (catch Exception e false))
  )

(defn get-raw-data
  "Get unencrypted file data at filepath"
  [filepath password]
  (retrieve-map (decrypt-as-str (read-file filepath) password))
  )

(defn get-entry
  "Get a specific entry on the rawdata"
  [filepath password login]
  (get (get-raw-data filepath password) login))

(defn csv-join-newline
  [str1 str2]
  (str str1 "," str2 "\n")
  )

(defn map-to-csv
  [mapdata]
  (reduce str "" (map csv-join-newline (keys mapdata) (vals mapdata)))
  )

(defn encrypt-write
  "Encrypt data and write"
  [data filepath password]
  (write-file (encrypt data password) filepath))

;;Handle adding entries
;;_____________________
(defn map-add-password
  "Get a map with the newlogin/password entry"
  [filepath password entrylogin entrypassword]
  (into (get-raw-data filepath password) {entrylogin entrypassword}))

(defn write-new-entry
  "Write over encrypted file with added newentry"
  [filepath password entrylogin entrypassword]
  (encrypt-write 
   (map-to-csv 
    (map-add-password filepath password entrylogin entrypassword))
                 filepath password)
  )

;;Hanlde removing entries
;;_______________________
(defn map-rem-password
  "remove entrylogin from the map"
  [filepath password entrylogin]
  (dissoc (get-raw-data filepath password) entrylogin)
  )

(defn write-rem-entry
  "Write the changes, with the removed map key"
  [filepath password entrylogin]
  (encrypt-write 
   (map-to-csv 
    (map-rem-password filepath password entrylogin)) 
                 filepath password)
  )
