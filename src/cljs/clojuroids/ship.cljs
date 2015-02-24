(ns clojuroids.ship
  (:require
   [clojuroids.util :as u]
   [clojuroids.flames :as flames]
   [clojuroids.input :as input]))

(def time-to-next-ship 100)
(def max-forward 10)
(def max-reverse -10)
(def model [[0 12] [192 12] [256 3] [320 12] [0 12]])

(defn create []
  (let [ship {:model model 
             :rect (u/rect model)
             :x (/ u/width 2) 
             :y (/ u/height 2)
             :vel [0 0]
             :angle 128 
             :rot 0
             :thrust 0}]
   [ship]))

(defn update-velocity [ship]
  (let [{:keys [vel angle thrust]} ship
        x (+ (vel 0) (* thrust (u/cos angle))) 
        y (+ (vel 1) (* thrust (u/sin angle)))
        x (min x max-forward)
        x (max x max-reverse)
        y (min y max-forward)
        y (max y max-reverse)] 
    (assoc ship :vel [x y])))

(def update-xform (comp (map update-velocity)
                        (map u/translate)
                        (map u/model-to-points)))

(defn ship-flames [state]
  (let [ship (first (get-in state [:objects :ship]))]
    (if (not= 0 (:thrust ship))
      (let [flames (get-in state [:objects :flames])
            flames (concat flames (flames/create-ship-flames ship))]
        (assoc-in state [:objects :flames] flames))
      state)))

(defn rotation []
  (let [left (get @input/state 74)
        right (get @input/state 76)]
    (condp = :key-down
      left 10
      right -10
      0)))

(defn thrust []
  (let [forward (get @input/state 73)
        reverse (get @input/state 75)]
    (condp = :key-down
      forward 0.3
      reverse -0.3
      0)))

(defn control [state]
  (let [ship (first (get-in state [:objects :ship]))
        ship (assoc ship :rot (rotation))
        ship (assoc ship :thrust (thrust))]
    (assoc-in state [:objects :ship] [ship])))

(defn start-next-ship-timer [state]
  (assoc state :ship-timer time-to-next-ship))

(defn create-new-ship [state]
  (-> state
      (assoc-in [:objects :ship] (create))
      (assoc :ship-timer nil)))

(defn decrement-timer [state t]
  (assoc state :ship-timer (dec t)))

(defn ship-timer [state]
  (if (not (seq (get-in state [:objects :ship])))
    (let [t (:ship-timer state)]
      (cond
       (= nil t) (start-next-ship-timer state) 
       (= 0 t) (create-new-ship state) 
       (> t 0) (decrement-timer state t) 
       :else state))
    state))

(defn update [state]
  (let [ship (get-in state [:objects :ship])]
    (if (seq ship)
      (let [ship (sequence update-xform ship)
            state (assoc-in state [:objects :ship] ship)]
        (-> state (control) (ship-flames)))
      (ship-timer state))))

