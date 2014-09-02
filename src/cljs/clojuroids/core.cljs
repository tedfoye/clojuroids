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

(defn level-check [objs game]
  (let [roid-count (count (:roids objs))
        timer (:level-timer game)
        [o g] (cond
               (and (= 0 roid-count) (= nil timer))
               [nil {:level-timer time-to-next-level}]
         
               (and (= 0 roid-count) (= 0 timer))
               [{:roids (roid/create-roids init-roid-count)} {:level-timer nil}]

               (and (= 0 roid-count) (> timer 0))
               [nil {:level-timer (dec timer)}]
         
               :else
               [nil nil])]
    [(merge objs o) (merge game g)]))

(defn game-loop [init-state]
  (let [input-chan (input/user-input)]
    (go-loop [state init-state start (timestamp)]
      (let [state (merge state {:input (alt! [input-chan] ([v] v) :default nil)})
            state (ship/update state)
            state (update-shots state)
            ;objs (merge objs {:roids (roid/update (:roids objs))})
            state (flames/update state)
            ;objs (merge objs (collisions/shot-roid objs))
            ;objs (merge objs (collisions/ship-roid objs))
            ;objs (merge objs {:explosions (explode/update (:explosions objs))})
            ;[objs game] (level-check objs game)
            ;[objs game] (ship-check objs game)
            ;state (ship/ship-check state)
            ;objs (merge objs (saucer/update objs))
            end (- (timestamp) start)]
        (animate-frame (:objects state))
        (<! (timeout (- 33 end)))
        (recur state (timestamp))))))

; create some asteroids, the ship, and enter the game loop
(game-loop {:objects {:roids (roid/create-roids init-roid-count)
                      :ship (ship/create)
                      :saucer (saucer/create 100 100 0)}})



