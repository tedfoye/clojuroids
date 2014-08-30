(ns clojuroids.core
  (:require
   [cljs.core.async :refer [timeout <!]]
   [clojuroids.render :as render]
   [clojuroids.ship :as ship]
   [clojuroids.shot :as shot]
   [clojuroids.flames :as flames]
   [clojuroids.explode :as explode]
   [clojuroids.roid :as roid]
   [clojuroids.input :as input]
   [clojuroids.collisions :as collisions])
  (:require-macros
   [cljs.core.async.macros :refer [alt! go-loop]]))

(def init-roid-count 4)

(defn timestamp [] (.getTime (js/Date.)))

(defn update-ship [{:keys [ship flames input]}]
  (let [ship (-> (map (ship/control input) ship) (ship/update))
        flames (concat flames (ship/flames ship))]
    {:ship ship :flames flames}))

(defn update-shots [objs]
  {:shots (-> (shot/handle-input objs) (shot/update))})

(defn animate-frame [objs]
  (render/animate-frame (reduce concat (vals objs))))

(defn level-check [objs game]
  (let [roid-count (count (:roids objs))
        timer (:level-timer game)
        [o g] (cond
               (and (= 0 roid-count) (= nil timer))
               [nil {:level-timer 100}]
         
               (and (= 0 roid-count) (= 0 timer))
               [{:roids (roid/create-roids init-roid-count)} {:level-timer nil}]

               (and (= 0 roid-count) (> timer 0))
               [nil {:level-timer (dec timer)}]
         
               :else
               [nil nil])]
    [(merge objs o) (merge game g)]))

(defn ship-check [objs game]
  (let [ship-count (count (:ship objs))
        timer (:ship-timer game)
        [o g] (cond
               (and (= 0 ship-count) (= nil timer)) [nil {:ship-timer 100}]
               (and (= 0 ship-count) (= 0 timer)) [{:ship (ship/create)} {:ship-timer nil}]
               (and (= 0 ship-count) (> timer 0)) [nil {:ship-timer (dec timer)}]
               :else [nil nil])]
    [(merge objs o) (merge game g)]))

(defn game-loop [init-objs]
  (let [input-chan (input/user-input)]
    (go-loop [objs init-objs game nil start (timestamp)]
      (let [objs (merge objs
                        {:input (alt! [input-chan] ([v] v) :default [])}
                        (update-ship objs)
                        (update-shots objs))
            objs (merge objs {:roids (roid/update (:roids objs))})
            objs (merge objs {:flames (flames/update (:flames objs))})
            objs (merge objs (collisions/shot-roid objs))
            objs (merge objs (collisions/ship-roid objs))
            objs (merge objs {:explosions (explode/update (:explosions objs))})
            [objs game] (level-check objs game)
            [objs game] (ship-check objs game)
            end (- (timestamp) start)]
        (animate-frame objs)
        (<! (timeout (- 33 end)))
        (recur objs game (timestamp))))))

; create some asteroids, the ship, and enter the game loop
(game-loop {:roids (roid/create-roids init-roid-count)
            :ship (ship/create)})


