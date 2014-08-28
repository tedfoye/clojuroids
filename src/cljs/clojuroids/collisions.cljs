(ns clojuroids.collisions
  (:require
   [clojuroids.shot :as shot]
   [clojuroids.roid :as roid]
   [clojuroids.flames :as flames]
   [clojuroids.explode :as explode]))

(defn collision [obj roid]
  (if (and (seq obj) (seq roid))
    (let [{:keys [x y] [_ _ hw hh] :rect} roid
          [l r] [(- x hw) (+ x hw)]
          [t b] [(- y hh) (+ y hh)]
          [x1 y1] [(:x obj) (:y obj)]]
      (if (not (seq (:rect roid))) 
        (.log js/console roid))
      (if (and (< l x1 r) (< t y1 b))
        [obj roid]))))

(defn collisions [objs roids]
  (if (and (seq objs) (seq roids))
    (sequence (comp (map (fn [obj]
                           (some (fn [roid]
                                   (collision obj roid))
                                 roids)))
                    (filter #(seq %)))
              objs)))

(defn collisions2 [objs roids]
  (if (and (seq objs) (seq roids))
    (loop [hits [] objs objs]
      (if (not (seq objs))
        hits
        (recur (concat hits
                       (some #(collision (first objs) %) roids))
               (rest objs))))))

(defn collisions1 [obj roids]
  (some #(collision (first obj) %) roids))

(defn shot-roid [ship shots roids flames explosions]
  (let [hits (collisions shots roids)

        ;shots (sequence (remove ))
        ;[shot roid] (collisions shots roids)
        ;shots (sequence (remove #(= % shot)) shots)
        ;flames (concat flames (flames/create-flames shot)) 
        ;roids (sequence (remove #(= % roid)) roids)
        ;roids (concat roids (roid/break-roid roid))
        ;explosions (concat explosions (explode/create-explosion roid))

                                        ;[ship-hit roid] (collisions ship roids)
                                        ;explosions (concat explosions (explode/create-explosion ship-hit 20))
                                        ;ship (if (seq ship-hit) nil ship)
        ] 
    (if (seq hits)
      (doseq [hit hits]
        (.log js/console (str hit))))
    [ship shots roids flames explosions]))

