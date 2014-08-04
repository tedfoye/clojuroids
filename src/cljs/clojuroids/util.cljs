(ns roids.util)

(def canvas (. js/document (getElementById "roids")))
(def ctx (. canvas (getContext "2d")))
(def width 1024)
(def height 768)
(def degree-max 512)
(def degree-mask 511)

(defn clear-canvas []
  (aset canvas "width" (aget canvas "width")))

(def cosm (memoize
           (fn [deg]
             ;(Math/cos (/ (* deg 6.2831855) degree-max))
             (-> deg (* 6.2831855) (/ degree-max) (Math/cos))
             )))

(defn cos
  ([deg] (cos deg 0))
  ([deg r] (cosm (-> r (+ deg) (bit-and degree-mask)))))

(def sinm (memoize (fn [deg] (Math/sin (/ (* deg 6.2831855) degree-max)))))

(defn sin
  ([deg] (sin deg 0))
  ([deg r] (sinm (-> r (+ deg) (bit-and degree-mask)))))


