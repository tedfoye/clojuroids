(ns clojuroids.collisions
  (:require
   [clojuroids.shot :as shot]
   [clojuroids.roid :as roid]
   [clojuroids.flames :as flames]
   [clojuroids.explode :as explode]))

(defn pt-in-rect [obj roid]
  (if (and (seq obj) (seq roid))
    (let [{:keys [x y] [_ _ hw hh] :rect} roid
          [l r] [(- x hw) (+ x hw)]
          [t b] [(+ y hh) (- y hh)]
          [x1 y1] [(:x obj) (:y obj)]]
      (if (and (< l x1 r) (< b y1 t))
        [obj roid]))))

(defn check-collisions [state key]
  (let [objs (get-in state [:objects key])
        roids (get-in state [:objects :roids])]
    (loop [hits [] objs objs]
      (if (not (seq objs))
        [(sequence (take-nth 2) hits)
         (sequence (take-nth 2) (rest hits))]
        (let [hit (some (fn [roid] (pt-in-rect (first objs) roid)) roids)
              hits (concat hits hit)]
          (recur hits (rest objs)))))))

(defn remove-shots [state shots-hit]
  (let [shots (get-in state [:objects :shots])
        shots (sequence (remove #(= % (first shots-hit))) shots)]
    (assoc-in state [:objects :shots] shots)))

(defn update-roids [state roids-hit]
  (let [roids (get-in state [:objects :roids])
        hit (first roids-hit)
        roids (sequence (remove #(= % hit)) roids)
        roids (concat roids (roid/break-roid hit))]
    (assoc-in state [:objects :roids] roids)))

(defn shot-roid [state]
  (let [[shots-hit roids-hit] (check-collisions state :shots)]
    (-> state
        (remove-shots shots-hit)
        (update-roids roids-hit)
        (flames/create-flames shots-hit)
        (explode/create-explosion roids-hit))))

(defn ship-hit [state ship-hits]
  (if (seq ship-hits)
    (assoc-in state [:objects :ship] nil)
    state))

(defn ship-roid [state]
  (let [[ship-hits roids-hit] (check-collisions state :ship)]
    (-> state
        (flames/create-flames ship-hits)
        (explode/create-explosion ship-hits 20)
        (ship-hit ship-hits))))

(defn collisions [state]
  (-> state (shot-roid) (ship-roid)))
