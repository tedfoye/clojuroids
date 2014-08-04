(ns roids.core
  (:require
   [cljs.core.async :refer [timeout <!]]
   [goog.events :as ge]
   [roids.util :refer [ctx width height degree-max degree-mask cos sin clear-canvas]]
   [roids.ship :as ship]
   [roids.input :as input])
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

(defn update-roid [roid]
  (let [[vel-x vel-y] (:vel roid)
        posx (+ (:posx roid) vel-x)
        posy (+ (:posy roid) vel-y)
        posx (if (< posx -50) (+ width 50) posx)
        posx (if (> posx (+ width 50)) -50 posx)
        posy (if (< posy -50) (+ height 50) posy)
        posy (if (> posy (+ height 50)) -50 posy)
        rot-angle (bit-and (+ (:angle roid) (:rot roid)) degree-mask)]
    (assoc roid :posx posx :posy posy :angle rot-angle)))

(defn update-roids [roids]
  (vec (for [roid roids] (update-roid roid))))

(defn draw-points [roid]
  (let [points (:points roid) rot-angle (:angle roid)
        posx (:posx roid) posy (:posy roid)]
    (for [[theta len] points]
      [(+ posx (* len (cos theta rot-angle)))
       (- height (+ posy (* len (sin theta rot-angle))))])))

(defn draw [roid]
  (let [points (draw-points roid) [x y] (first points)]
    (doto ctx (.beginPath) (.moveTo x y))
    (doall (map (fn [[x y]] (. ctx (lineTo x y))) (rest points)))
    (doto ctx (.closePath) (aset "strokeStyle" "#ffffff") (.stroke))))

(defn render-roids [roids]
  (loop [roids roids]
    (draw (first roids))
    (if (not (empty? roids))
      (recur (rest roids)))))

(defn render [roids ship]
  (let [input-chan (input/user-input)]
    (go-loop [roids roids ship ship]
      (. js/window
         (requestAnimationFrame
          (fn [t]
            (clear-canvas)
            (render-roids roids)
            (draw ship))))
      (let [ship (alt! [input-chan (timeout 1)]
                            ([v c] (if (= c input-chan)
                                     (ship/handle-input ship v)
                                     ship)))]
        (<! (timeout 33))
        (recur (update-roids roids) (ship/update-position ship))))))

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
