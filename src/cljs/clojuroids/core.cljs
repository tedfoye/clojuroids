(ns clojuroids.core
  (:require
   [cljs.core.async :refer [alts! chan timeout <! >!]]
   [clojuroids.render :as render]
   [clojuroids.ship :as ship]
   [clojuroids.shot :as shot]
   [clojuroids.flames :as flames]
   [clojuroids.explode :as explode]
   [clojuroids.roid :as roid]
   [clojuroids.input :as input]
   [clojuroids.collisions :as collisions]
   [clojuroids.level :as level]
   [clojuroids.saucer :as saucer])
  (:require-macros
   [cljs.core.async.macros :refer [go go-loop]]))

(def frame-rate 33)
(def init-roid-count 4)

(defn timestamp [] (.getTime (js/Date.)))

(defn update-state [state]
  (-> state
      (ship/update)
      (shot/update)
      (roid/update)
      (flames/update)
      (collisions/collisions)
      (explode/update)
      (level/check)
      (saucer/update)))

(declare game-loop)

(defn animate-frame [state time]
  (. js/window
    (requestAnimationFrame
      (fn [t]
        (render/animate-frame (reduce concat (vals (:objects state))))        
        (game-loop state time)))))

(defn game-loop [state time]
  (go
    (<! (timeout (- frame-rate (- (timestamp) time))))
    (-> state (update-state) (animate-frame (timestamp)))))

(def state {:objects {:roids (roid/create-roids init-roid-count)
                      :ship (ship/create)
                      :saucer (saucer/create 100 100 0)}
            :last-roid-count init-roid-count})

(trampoline game-loop state (timestamp))


