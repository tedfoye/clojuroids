(ns clojuroids.core
  (:require
   [cljs.core.async :refer [chan put! take! timeout <!]]
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
   [cljs.core.async.macros :refer [alt! go-loop]]))

(def frame-rate 33)
(def init-roid-count 4)

(defn timestamp [] (.getTime (js/Date.)))

(defn animate-frame [{objects :objects}]
  (render/animate-frame (reduce concat (vals objects))))

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

(defn calc-timeout [start] (- frame-rate (- (timestamp) start))) 

(defn game-loop [init-state]
  (let [input-chan (input/user-input)]
    (go-loop [state init-state start (timestamp)]
      (let [state (merge state{:input (alt! [input-chan] ([v] v) :default nil)})
            state (update-state state)]
        (animate-frame state)
        (<! (timeout (calc-timeout start)))
        (recur state (timestamp))))))

;; create some asteroids, the ship, a saucer which doesn't do much yet
;; and enter the game loop
(game-loop {:objects {:roids (roid/create-roids init-roid-count)
                      :ship (ship/create)
                      :saucer (saucer/create 100 100 0)}
            :last-roid-count init-roid-count})



