(ns clojuroids.util)

(def width 1024)
(def height 768)
(def degree-max 512)
(def degree-mask 511)

(def cosm (memoize (fn [deg] (-> deg (* 6.2831855) (/ degree-max) (Math/cos)))))

(defn cos
  ([deg] (cos deg 0))
  ([deg r] (cosm (-> r (+ deg) (bit-and degree-mask)))))

(def sinm (memoize (fn [deg] (-> deg (* 6.2831855) (/ degree-max) (Math/sin)))))

(defn sin
  ([deg] (sin deg 0))
  ([deg r] (sinm (-> r (+ deg) (bit-and degree-mask)))))


