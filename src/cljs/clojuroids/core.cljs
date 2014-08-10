(ns clojuroids.core
  (:require
   [cljs.core.async :refer [timeout <!]]
   [goog.events :as ge]
   [clojuroids.util :refer [ctx width height degree-max degree-mask cos sin clear-canvas]]
   [clojuroids.ship :as ship]
   [clojuroids.input :as input])
  (:require-macros
   [cljs.core.async.macros :refer [alt! go go-loop]]))

(defn createroid [sides size posx posy velocity direction rot]
  {:points (for [i (range sides)]
              [(-> i (* degree-max) (/ sides) (+ (rand-int 30)))
               (+ size (rand-int (int (/ size 2))))])
   :posx posx
   :posy posy
   :vel [(* velocity (cos direction)) (* velocity (sin direction))] 
   :dir direction
   :angle 0
   :rot rot})
(defn update-object [obj]
  (let [{:keys [posx posy angle rot] [velx vely] :vel} obj
        x (- (mod (+ posx 50 velx) (+ width 100)) 50) 
        y (- (mod (+ posy 50 vely) (+ height 100)) 50)
        rot (mod (+  angle rot) degree-mask)]
    (assoc obj :posx x :posy y :angle rot)))

(defn draw-points [roid]
  (let [{:keys [points angle posx posy]} roid]
    (for [[theta len] points]
      [(+ posx (* len (cos theta angle)))
       (- height (+ posy (* len (sin theta angle))))])))

(defn draw [roid]
  (let [points (draw-points roid) [x y] (first points)]
    (doto ctx (.beginPath) (.moveTo x y))
    (dorun (map (fn [[x y]] (. ctx (lineTo x y))) (rest points)))
    (doto ctx (.closePath) (aset "strokeStyle" "#ffffff") (.stroke))))

(defn request-animation-frame [roids ship]
  (. js/window
     (requestAnimationFrame
      (fn [t]
        (clear-canvas)
        (dorun (map draw roids))
        (draw ship)))))

(defn render [roids ship]
  (let [input-chan (input/user-input)]
    (go-loop [roids roids ship ship]
      (request-animation-frame roids ship)
      (let [ship (alt! [input-chan] ([v] (ship/handle-input ship v)) :default ship)
            ship (ship/update-velocity ship)]
        (<! (timeout 33))
        (recur (map update-object roids) (update-object ship))))))

(defn rotation [min max]
  (let [r1 (range (bit-not (dec max)) (bit-not (dec min)))
        r2 (range min (inc max))]
    (rand-nth (into r1 r2))))

; create some asteroids, the ship, and enter the render loop
(render [(createroid 9 50 (rand-int width) (rand-int height) (+ 1 (rand-int 3)) (rand-int 512) (rotation 1 2))
         (createroid 9 50 (rand-int width) (rand-int height) (+ 1 (rand-int 3)) (rand-int 512) (rotation 1 2))
         (createroid 9 50 (rand-int width) (rand-int height) (+ 1 (rand-int 3)) (rand-int 512) (rotation 1 2))
         (createroid 9 50 (rand-int width) (rand-int height) (+ 1 (rand-int 3)) (rand-int 512) (rotation 1 2))

         (createroid 7 20 (rand-int width) (rand-int height) (+ 3 (rand-int 3)) (rand-int 512) (rotation 3 4))
         (createroid 7 20 (rand-int width) (rand-int height) (+ 3 (rand-int 3)) (rand-int 512) (rotation 3 4))
         (createroid 7 20 (rand-int width) (rand-int height) (+ 3 (rand-int 3)) (rand-int 512) (rotation 3 4))
         (createroid 7 20 (rand-int width) (rand-int height) (+ 3 (rand-int 3)) (rand-int 512) (rotation 3 4))
           
         (createroid 5 6 (rand-int width) (rand-int height) (+ 5 (rand-int 3)) (rand-int 512) (rotation 11 15))
         (createroid 5 6 (rand-int width) (rand-int height) (+ 5 (rand-int 3)) (rand-int 512) (rotation 11 15))
         (createroid 5 6 (rand-int width) (rand-int height) (+ 5 (rand-int 3)) (rand-int 512) (rotation 11 15))
         (createroid 5 6 (rand-int width) (rand-int height) (+ 5 (rand-int 3)) (rand-int 512) (rotation 11 15))]
        (ship/create-ship (int (/ width 2)) (int (/ height 2)) 0 128 0)) 
