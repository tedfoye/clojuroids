(ns clojuroids.level
  (:require
   [clojuroids.roid :as roid]))

(def time-to-next-level 100)

(defn create-roids [state n]
  (let [roid-count (inc n)]
    (-> state
        (assoc-in [:objects :roids] (roid/create-roids roid-count))
        (assoc :last-roid-count roid-count)
        (assoc :level-timer nil))))

(defn check [state]
  (let [roids (get-in state [:objects :roids])]
    (if (= 0 (count roids))
      (let [timer (:level-timer state)
            n (:last-roid-count state)]
        (cond
         (= nil timer) (assoc state :level-timer time-to-next-level)
         (= 0 timer)   (create-roids state n) 
         (> timer 0)   (assoc state :level-timer (dec timer))
         :else state))
      state)))
