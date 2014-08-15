(ns clojuroids.core
  (:require
   [cljs.core.async :refer [timeout <!]]
   [clojuroids.render :as render]
   [clojuroids.ship :as ship]
   [clojuroids.shot :as shot]
   [clojuroids.roid :as roid]
   [clojuroids.input :as input]
   [clojuroids.collisions :as collision])
  (:require-macros
   [cljs.core.async.macros :refer [alt! go go-loop]]))

(defn game [roids ship shots]
  (let [input-chan (input/user-input)]
    (go-loop [roids roids ship ship shots shots]
      (render/animate-frame (concat roids shots [ship]))
      (let [input (alt! [input-chan] ([v] v) :default [])
            ship (-> ship (ship/handle-input input) (ship/update))
            shots (shot/handle-input shots ship input)
            shots (shot/update shots)
            roids (roid/update roids)
            [shots roids] (collision/shot-roid shots roids)]
        (<! (timeout 33))
        (recur roids ship shots)))))


; create some asteroids, the ship, and enter the game loop

(.log js.console (count (roid/create-roids)))
(game (roid/create-roids)
      (ship/create)
      [])
 
