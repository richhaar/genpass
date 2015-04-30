(ns genpass.core
  (:require [clojure.string :as s]
            [clojure.tools.cli :refer [parse-opts]]
            [lock-key.core :refer [decrypt decrypt-as-str encrypt]]
            [genpass.crypt :as gen])
  (:gen-class))



(def cli-options
  ;; An option with a required argument
  [["-l" "--length LENGTH" "Password length"
    :default 20
    :parse-fn #(Integer/parseInt %)
    :validate [#(< 0 % 100) "Must be a number between 0 and 100"]]
   ["-t" "--time TIME" "Clipboard time (ms)"
    :default 8000
    :parse-f #(Integer/parseInt %)
    :validate [#(< 0 % 60000) "Must be between 0 and 60,000 ms"]]
   ;; A non-idempotent option
   ["-v" nil "Verbosity level"
    :id :verbosity
    :default 1
    :assoc-fn (fn [m k _] (update-in m [k] inc))]
   ;; A boolean option defaulting to nil
   ["-h" "--help"]
   ;; Print password to output if true, otherwise copy to clipboard
   ["-s" "--showpassword"]])

(defn usage [options-summary]
  (->> [""
        "Usage: program-name [options] action [login]"
        ""
        "Options:"
        options-summary
        ""
        "Actions:"
        "  gen       Generate a new password"
        "  get       Get a password"
        "  rem       Remove a user:password mapping"
        "  list      List all users"
        "  changepw  Change the encryption password"
        ""]
       (s/join \newline)))

(defn error-msg [errors]
  (str "The following errors occurred while parsing your command:\n\n"
       (s/join \newline errors)))

(defn exit [status msg]
  (println msg)
  (System/exit status)
  )

;;Mapping from cmdname -> [required-cmdline-args  command-location]
(def cmd-map {"get" ['2 gen/get-pw]
              "gen" ['2 gen/gen-pw]
              "rem" ['2 gen/rem-pw]
              "list" ['1 gen/list-pw]
              "changepw" ['1 gen/change-pw]})

(defn get-cmd-args
  "Get the required arguments of a command"
  [cmd]
  (get (get cmd-map cmd) 0)
  )

(defn get-cmd-cmd
  "Get the actual command of a cmd"
  [cmd]
  (get (get cmd-map cmd) 1)
  )

(defn -main [& args]
  (let [{:keys [options arguments errors summary]} (parse-opts args cli-options)
        first-arg (get arguments 0)
        second-arg (get arguments 1)]
    ;; Handle help and error conditions
    (cond
     (:help options) (exit 0 (usage summary))
     (not= (count arguments) (get-cmd-args first-arg))(exit 1 (usage summary))
     errors (exit 1 (error-msg errors)))
    
    ;; Handle command
    (let [cmd (get-cmd-cmd first-arg)
          options (into options {:user second-arg})]
      (if (nil? cmd)
        (exit 1 (usage summary))
        (gen/exec-command options cmd))))
  )
