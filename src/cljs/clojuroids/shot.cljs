(ns clojuroids.shot
  (:require [clojuroids.util :as u]))

(def max-shots 4)
(def f-key 70)
(def shot-velocity 20)
(def offset-to-tip-of-ship 12)
(def time-to-live 30)
(def color-change-ttl 20)
(def color-1 "#ADD8E6")
(def color-2 "#4682B4")
(def model [[0 2] [128 2] [256 2] [384 2]])

(defn create [{:keys [x y angle]}]
  (let [cos-angle (u/cos angle) sin-angle (u/sin angle)
        shot {:model model
              :rect (u/rect model)
              :x (+ x (* offset-to-tip-of-ship cos-angle))
              :y (+ y (* offset-to-tip-of-ship sin-angle))
              :vel [(* shot-velocity cos-angle) (* shot-velocity sin-angle)]
              :angle angle
              :rot 0
              :ttl time-to-live}]
    [(u/model-to-points shot)]))

(defn color [{ttl :ttl :as obj}]
  (let [c (if (< ttl color-change-ttl)
            color-2
            color-1)]
    (assoc obj :color c)))

(defn dec-ttl [{ttl :ttl :as obj}]
  (assoc obj :ttl (dec ttl)))

(defn alive? [{ttl :ttl}] (> ttl 0))

(def xform (comp (map dec-ttl)
                 (map color)
                 (filter alive?)
                 (map u/translate)
                 (map u/model-to-points)))

(defn transform [state]
  (let [shots (get-in state [:objects :shots])]
    (assoc-in state [:objects :shots] (sequence xform shots))))

(defn handle-input [state]
  (let [ship (first (get-in state [:objects :ship]))
        input (:input state)
        shots (get-in state [:objects :shots])]
    (if (and (= input [f-key :key-down])
             (< (count shots) max-shots))
      (let [shots (concat shots (create ship))]
        (assoc-in state [:objects :shots] shots))
      state)))

(defn update [state]
  (-> state (handle-input) (transform)))
