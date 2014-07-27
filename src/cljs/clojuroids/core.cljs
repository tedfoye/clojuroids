(ns roids.core
  (:require
   [cljs.core.async :refer [timeout <!]])
  (:require-macros
   [cljs.core.async.macros :refer [go go-loop]]))

(def width 1024)
(def height 768)
(def canvas (. js/document (getElementById "roids")))
(def ctx (. canvas (getContext "2d")))

(def degree-max 512)
(def degree-mask 511)

(def cosm (memoize (fn [deg] (Math/cos (/ (* deg 6.2831855) degree-max)))))
(defn cos
  ([deg] (cos deg 0))
  ([deg r] (cosm (-> r (+ deg) (bit-and degree-mask)))))

(def sinm (memoize (fn [deg] (Math/sin (/ (* deg 6.2831855) degree-max)))))
(defn sin
  ([deg] (sin deg 0))
  ([deg r] (sinm (-> r (+ deg) (bit-and degree-mask)))))

(defn createroid [sides size posx posy velocity direction rot]

  {:points (for [i (range sides)]
              [(-> i (* degree-max) (/ sides) (+ (rand-int 30)))
               (+ size (rand-int (int (/ size 2))))])
   :posx posx
   :posy posy
   :vel velocity
   :dir direction
   :angle 0
   :rot rot})

(defn update-roid [roid]
  (let [posx (+ (:posx roid) (Math/round (* (:vel roid) (cos (:dir roid)))))
        posy (+ (:posy roid) (Math/round (* (:vel roid) (sin (:dir roid)))))
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
      [(+ posx (Math/round (* len (cos theta rot-angle))))
       (+ posy (Math/round (* len (sin theta rot-angle))))])))

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

(defn render [roids]
  (go-loop [roids roids]
    (. js/window
       (requestAnimationFrame
        (fn [t]
          (aset canvas "width" (aget canvas "width"))
          (render-roids roids))))
    (<! (timeout 33))
    (recur (update-roids roids))))

(defn rotation [min max]
  (let [r1 (range (bit-not (dec max)) (bit-not (dec min)))
        r2 (range min (inc max))]
    (rand-nth (into r1 r2))))

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
        (createroid 5 6 (rand-int width) (rand-int height) (+ 5 (rand-int 3)) (rand-int 512) (rotation 11 15))])

