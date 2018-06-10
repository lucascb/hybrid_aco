(defproject cvrplib-solver "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [net.mikera/vectorz-clj "0.47.0"]
                 [cheshire "5.8.0"]
                 [random-seed "1.0.0"]
                 [amalloy/ring-buffer "1.2.1"]
                 [com.taoensso/timbre "4.10.0"]
                 [org.clojure/tools.cli "0.3.7"]]
  :main ^:skip-aot hybrid-aco.core
  :target-path "target/%s"
  :jvm-opts ["-Xmx1G"]
  :profiles {:uberjar {:aot :all}})
