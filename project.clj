(defproject clojuroids "0.1.0-SNAPSHOT"
  :description "A ClojureScript verion of the classic Asteroids video game."
  :url "https://github.com/tedfoye/clojuroids"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :repositories {"sonatype-oss-public" "https://oss.sonatype.org/content/groups/public/"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [org.clojure/clojurescript "0.0-2850"]
                 [compojure "1.2.1"]
                 [javax.servlet/javax.servlet-api "3.1.0"]]
  :plugins [[lein-ring "0.9.1"]
            [lein-cljsbuild "1.0.4"]
            [cider/cider-nrepl "0.8.2"]]
  :ring {:handler clojuroids.handler/app}
  :source-paths ["src/clj"]
  :target-path "target/%s"
  :cljsbuild {:builds
              [{:id "dev"
                :cache-analysis true
                :source-paths ["src/cljs"]
                :compiler {:output-to "resources/public/js/clojuroids.js"
                           :optimizations :whitespace
                           :pretty-print true}}
               {:id "prod"
                :cache-analysis true
                :source-paths ["src/cljs"]
                :compiler {:output-to "resources/public/js/clojuroids.min.js"
                           :optimizations :advanced
                           :pretty-print false}}]})
