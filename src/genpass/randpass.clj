(ns genpass.randpass
  (:gen-class))

(def uppers "ABCDEFGHIJKLMNOPQRSTUVWXYZ")
(def lowers "abcdefghijklmnopqrstuvwxyz")
(def nums "0123456789")
(def other "!@£$%^&*()")
(def cryptkeys (vector uppers lowers nums other))

(defn rand-char  
  "Get a random char from a string"
  [string]
  (get string (rand-int (count string)))
  )

(defn rand-string
  "Generate a random sring of length <length> consisting of cryptkeys"
  [cryptkeys length]
  (loop [string ""]
    (if (= length (count string))
      string
      (recur (str string (rand-char cryptkeys)))))
  )

(defn create-password
  "Create a password of length <length>,
  using varying levels of cryptkeys based on verbosity level"
  [length verbosity]
  (rand-string (reduce str (take verbosity cryptkeys)) length)
  )
