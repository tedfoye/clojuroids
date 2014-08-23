(ns clojuroids.render
  (:require
   [clojuroids.util :refer [width height cos sin]]))

(def canvas (. js/document (getElementById "roids")))
(. canvas focus)

(def ctx (. canvas (getContext "2d")))

(defn draw [object]
  (let [points (:points object)                       
        [x y] (first points)
        color (or (:color object) "#ffffff")]
    (doto ctx (.beginPath) (.moveTo x y))
    (dorun (map (fn [[x y]] (. ctx (lineTo x y))) (rest points)))
    (doto ctx (.closePath) (aset "strokeStyle" color) (.stroke))))

(defn animate-frame [objects]
  (. js/window
     (requestAnimationFrame
      (fn [t]
        (aset canvas "width" (aget canvas "width"))
        (doseq [object objects] (draw object))))))

