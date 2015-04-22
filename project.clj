(defproject genpass "0.1.0-SNAPSHOT"
  :description "A Unix password generator storing the encrypted passwords locally."
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/tools.cli "0.3.1"]
                 [lock-key "1.1.0"]
                 [snipsnap "0.2.0" :exclusions [org.clojure/clojure]]
                 ]
  :main ^:skip-aot genpass.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
