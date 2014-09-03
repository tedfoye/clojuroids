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
   [clojuroids.saucer :as saucer])
  (:require-macros
   [cljs.core.async.macros :refer [alt! go-loop]]))

(def init-roid-count 4)
(def time-to-next-level 100)

(defn timestamp [] (.getTime (js/Date.)))

(defn update-shots [state]
  (-> state (shot/handle-input) (shot/update)))

(defn animate-frame [objs]
  (render/animate-frame (reduce concat (vals objs))))

(defn level-check [state]
  (let [roids (get-in state [:objects :roids])]
    (if (= 0 (count roids))
      (let [timer (:level-timer state)]
        (cond
         (= nil timer) (assoc state :level-timer time-to-next-level)
         (= 0 timer) (-> state
                         (assoc-in
                          [:objects :roids]
                          (roid/create-roids init-roid-count))
                         (assoc :level-timer nil))
         (> timer 0) (assoc state :level-timer (dec timer))
         :else state))
      state)))

(defn update-state [state c]
  (-> state
      (ship/update)
      (update-shots)
      (roid/update)
      (flames/update)
      (collisions/collisions)
      (explode/update)
      (level-check)
      (saucer/update)))

(defn game-loop [init-state]
  (let [input-chan (input/user-input)]
    (go-loop [state init-state start (timestamp)]
      (let [state (merge state{:input (alt! [input-chan] ([v] v) :default nil)})
            state (update-state state input-chan) 
            end (- (timestamp) start)]
        (animate-frame (:objects state))
        (<! (timeout (- 33 end)))
        (recur state (timestamp))))))

;; create some asteroids, the ship, a saucer which doesn't do much yet
;; and enter the game loop
(game-loop {:objects {:roids (roid/create-roids init-roid-count)
                      :ship (ship/create)
                      :saucer (saucer/create 100 100 0)}})



