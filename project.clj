(defproject clojuroids "0.1.0-SNAPSHOT"
  :description "A ClojureScript verion of the classic Asteroids video game."
  :url "https://github.com/tedfoye/clojuroids"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/core.async "0.1.303.0-886421-alpha"]
                 [org.clojure/clojurescript "0.0-2268"]
                 [om "0.6.4"]
                 [compojure "1.1.8"]
                 ;[ring/ring-jetty-adapter "1.2.2"]
                 [javax.servlet/javax.servlet-api "3.1.0"]]
  :plugins [[lein-ring "0.8.11"]
            [lein-cljsbuild "1.0.3"]
            [cider/cider-nrepl "0.7.0-SNAPSHOT"]]
  :ring {:handler clojuroids.handler/app}
  :source-paths ["src/clj"]
  ;:main ^:skip-aot clojuroids.core
  :target-path "target/%s"
  ;:profiles {:uberjar {:aot :all}}
  :cljsbuild {:builds
              [{:id "dev"
                :source-paths ["src/cljs"]
                :compiler {:output-to "resources/public/js/clojuroids.js"
                           :externs ["resources/development/js/externs.js"]
                           :optimizations :whitespace
                           :pretty-print true}}
               {:id "prod"
                :source-paths ["src/cljs"]
                :compiler {:output-to "resources/public/js/clojuroids.min.js"
                           :source-map "resources/public/js/clojuroids.min.js.map"
                           :externs ["resources/development/js/externs.js"]
                           :optimizations :advanced
                           :pretty-print false}}]})
