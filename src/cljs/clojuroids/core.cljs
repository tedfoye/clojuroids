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

(defn timestamp [] (.getTime (js/Date.)))

(defn update-ship [{:keys [ship flames input]}]
  (let [ship (->> ship
                  (map (ship/control input))
                 (ship/update))
        flames (concat flames (ship/flames ship))]
    {:ship ship :flames flames}))

(defn update-shots [objs]
  {:shots (-> (shot/handle-input objs) (shot/update))})

(defn animate-frame [objs]
  (render/animate-frame (reduce concat (vals objs))))

(defn game-loop [init-objs]
  (let [input-chan (input/user-input)]
    (go-loop [objs init-objs start (timestamp)]
      (let [objs (merge objs
                        {:input (alt! [input-chan] ([v] v) :default [])}
                        (update-ship objs)
                        (update-shots objs))
            objs (merge objs {:roids (roid/update (:roids objs))})
            objs (merge objs {:flames (flames/update (:flames objs))})
            objs (merge objs (collisions/shot-roid objs))
            objs (merge objs (collisions/ship-roid objs))
            objs (merge objs {:explosions (explode/update (:explosions objs))})
            end (- (timestamp) start)]
        (animate-frame objs)
        (<! (timeout (- 33 end)))
        (recur objs (timestamp))))))

; create some asteroids, the ship, and enter the game loop
(game-loop {:roids (roid/create-roids 4)
            :ship (ship/create)})


