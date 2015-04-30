(ns genpass.core-test
  (:require [clojure.test :refer :all]
            [genpass.core :refer :all]
            [genpass.crypt :refer :all]
            [genpass.filehandler :refer :all]))


;;Test core functions
(deftest test-get-cmd-args
  (is (= 2 (get-cmd-args "get")))
  (is (= 2 (get-cmd-args "gen")))
  (is (= 2 (get-cmd-args "rem")))
  (is (= 1 (get-cmd-args "list")))
  (is (= 1 (get-cmd-args "changepw")))
  (is (nil? (get-cmd-args "not-a-cmd")))
  (is (nil? (get-cmd-args ""))))

(deftest test-get-cmd-cmd
  (is (= get-pw (get-cmd-cmd "get")))
  (is (= gen-pw (get-cmd-cmd "gen")))
  (is (= rem-pw (get-cmd-cmd "rem")))
  (is (= list-pw (get-cmd-cmd "list")))
  (is (= change-pw (get-cmd-cmd "changepw")))
  (is (nil? (get-cmd-cmd "not-a-cmd")))
  (is (nil? (get-cmd-cmd ""))))

;;Test filehandler functions
(def input-text ["user1,pass1"  
                 "user1,pass1\n" 
                 "" 
                 "user1,pass1\nuser2,pass2\n"])

(def expected-maps [{"user1" "pass1"}
                    {"user1" "pass1"}
                    {}
                    {"user1" "pass1" "user2" "pass2"}])

(def output-text ["user1,pass1\n"
                  "user1,pass1\n"
                  ""
                  "user2,pass2\nuser1,pass1\n"])

(deftest test-retrieve-map
  (is (= expected-maps (map retrieve-map input-text))))

(deftest test map-to-csv
  (is (= output-text (map map-to-csv expected-maps))))

;;Test crupt functions
(deftest test-parse-y-n
  (is (true? (parse-y-n "y")))
  (is (true? (parse-y-n "Y")))
  (is (false? (parse-y-n "n")))
  (is (false? (parse-y-n "N")))
  )
