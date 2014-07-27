(ns clojuroids.handler
  (:require [compojure.core :refer :all]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.util.response :as resp]))

(defroutes app-routes
  (GET "/" [] (resp/resource-response "app.html" {:root "public/pages"}))
  (route/files "/")
  (route/resources "/")
  (route/not-found "Not Found"))

 (def app
   (-> app-routes handler/site))
 
