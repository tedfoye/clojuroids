(ns clojuroids.render
  (:require
   [clojuroids.util :refer [width height cos sin]]))

(def canvas (. js/document (getElementById "roids")))
(. canvas focus)

(def ctx (. canvas (getContext "2d")))

(defn draw-points [roid]
  (let [{:keys [points angle posx posy]} roid]
    (for [[theta len] points]
      [(+ posx (* len (cos theta angle)))
       (- height (+ posy (* len (sin theta angle))))])))

(defn draw [object]
  (let [points (draw-points object) [x y] (first points)]
    (doto ctx (.beginPath) (.moveTo x y))
    (dorun (map (fn [[x y]] (. ctx (lineTo x y))) (rest points)))
    (doto ctx (.closePath) (aset "strokeStyle" "#ffffff") (.stroke))))

(defn animate-frame [objects]
  (. js/window
     (requestAnimationFrame
      (fn [t]
        (aset canvas "width" (aget canvas "width"))
        (dorun (map draw objects))))))

