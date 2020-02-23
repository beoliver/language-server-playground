(defproject language-server-playground "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [cheshire "5.10.0"]
                 [org.eclipse.jgit/org.eclipse.jgit "5.6.1.202002131546-r"]
                 [org.slf4j/slf4j-simple "1.7.30"]]
  :main ^:skip-aot language-server-playground.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
