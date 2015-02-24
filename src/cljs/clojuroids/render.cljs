(ns clojuroids.render)

(def white "#ffffff")
(def canvas (. js/document (getElementById "roids")))
(def ctx (. canvas (getContext "2d")))

(set! (.-lineWidth ctx) 1.25)

(defn move-to [[x y]]
  (. ctx (beginPath))
  (. ctx (moveTo x y)))

(defn line-to-seq [pts]
  (doseq [[x y] pts]
    (. ctx (lineTo x y))))

(defn close-path [color]
  (. ctx (closePath))
  (set! (.-strokeStyle ctx) color)
  (. ctx (stroke)))

(defn draw [{p "points" c "color"}]
  (move-to (first p)) 
  (line-to-seq (rest p))
  (close-path (or c white)))

(defn clear-canvas []
  (. ctx (clearRect 0 0 1024 768)))

(defn animate-frame [objects]
  (clear-canvas) 
  (doseq [object objects]
    (draw object)))

(. canvas focus)
