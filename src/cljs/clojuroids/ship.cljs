(ns clojuroids.ship
  (:require
   [clojuroids.util :as u]
   [clojuroids.flames :as flames]))

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

(defn control [state]
  (let [ship (first (get-in state [:objects :ship]))
        input (:input state)
        movement (condp = input 
                   [74 :key-down] {:rot 10} 
                   [74 :key-up]   {:rot 0} 
                   [76 :key-down] {:rot -10} 
                   [76 :key-up]   {:rot 0} 
                   [73 :key-down] {:thrust 0.5} 
                   [73 :key-up]   {:thrust 0} 
                   [75 :key-down] {:thrust -0.5}
                   [75 :key-up]   {:thrust 0}
                   {})
        ship (merge ship movement)]
    (assoc-in state [:objects :ship] [ship])))

(defn start-next-ship-timer [state]
  (assoc state :ship-timer time-to-next-ship))

(defn create-new-ship [state]
  (-> state
      (assoc-in [:objects :ship] (create))
      (assoc :ship-timer nil)))

(defn decrement-timer [state t]
  (assoc state :ship-timer (dec t)))

(defn ship-check [state]
  (let [n (count (get-in state [:objects :ship]))
        t (:ship-timer state)]
    (cond
     (and (= 0 n) (= nil t)) (start-next-ship-timer state) 
     (and (= 0 n) (= 0 t)) (create-new-ship state) 
     (and (= 0 n) (> t 0)) (decrement-timer state t) 
     :else state)))

(defn update [state]
  (let [ship (get-in state [:objects :ship])
        ship (sequence update-xform ship)
        state (assoc-in state [:objects :ship] ship)]
    (-> state (control) (ship-flames) (ship-check))))
