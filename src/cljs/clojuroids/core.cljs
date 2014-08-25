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
   [clojuroids.collisions :as collision])
  (:require-macros
   [cljs.core.async.macros :refer [alt! go-loop]]))

(defn game [roids ship]
  (let [input-chan (input/user-input)]
    (go-loop [roids roids ship ship shots [] flames [] explosions [] st (.getTime (js/Date.))]
      (render/animate-frame (concat roids shots ship flames explosions))
      (let [input (alt! [input-chan] ([v] v) :default [])
            ship (ship/handle-input ship input)
            ship (ship/update ship)
            shots (shot/handle-input shots ship input)
            shots (shot/update shots)
            roids (roid/update roids)
            flames (ship/flames ship flames)
            flames (flames/update flames)
            explosions (explode/update explosions)
            [shots roids flames explosions] (collision/shot-roid shots roids flames explosions)
            et (.getTime (js/Date.))
            td (- et st)]
        (<! (timeout (- 33 td)))
        (recur roids ship shots flames explosions (.getTime (js/Date.)))))))


; create some asteroids, the ship, and enter the game loop
(game (roid/create-roids 4)
      (ship/create))
 
