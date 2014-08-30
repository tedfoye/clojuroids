(ns clojuroids.render)

(def white "#ffffff")
(def canvas (. js/document (getElementById "roids")))
(def ctx (. canvas (getContext "2d")))

(defn move-to [[x y]]
  (doto ctx
    (.beginPath)
    (.moveTo x y)))

(defn line-to-seq [pts]
  (doseq [[x y] pts]
    (. ctx (lineTo x y))))

(defn close-path [color]
  (doto ctx
    (.closePath)
    (aset "strokeStyle" color)
    (.stroke)))

(defn draw [{p :points c :color}]
  (move-to (first p)) 
  (line-to-seq (rest p)) 
  (close-path (or c white)))

(defn clear-canvas []
  (aset canvas "width" (aget canvas "width")))

(defn animate-frame [objects]
  (. js/window
     (requestAnimationFrame
      (fn [t]
        (clear-canvas) 
        (doseq [object objects]
          (draw object))))))

(. canvas focus)
