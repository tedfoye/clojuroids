(ns clojuroids.util)

(def width 1024)
(def height 768)
(def degree-max 512)
(def degree-mask 511)

(def cosm (memoize (fn [deg] (-> deg (* 6.2831855) (/ degree-max) (Math/cos)))))

(defn cos
  ([deg] (cos deg 0))
  ([deg r] (cosm (-> r (+ deg) (bit-and degree-mask)))))

(def sinm (memoize (fn [deg] (-> deg (* 6.2831855) (/ degree-max) (Math/sin)))))

(defn sin
  ([deg] (sin deg 0))
  ([deg r] (sinm (-> r (+ deg) (bit-and degree-mask)))))

(defn rect [model]
  (let [pts (for [[a len] model] [(* len (cos a)) (* len (sin a))])
        xpts (for [pt pts] (pt 0))
        ypts (for [pt pts] (pt 1))
        [l r] [(reduce min width xpts) (reduce max 0 xpts)]
        [t b] [(reduce min height ypts) (reduce max 0 ypts)]
        [w h] [(* (- r l) 1.1) (* (- b t) 1.1)]]
    [w h (/ w 2) (/ h 2)]))

(defn model-to-points [roid]
  (let [{:keys [model angle x y]} roid
        pts (for [[theta len] model]
              [(+ x (* len (cos theta angle)))
               (- height (+ y (* len (sin theta angle))))])]
   (assoc roid :points pts)))

(defn translate [obj]
  (let [{:keys [x y angle rot] [vx vy] :vel [w h hw hh] :rect} obj
        x (- (mod (+ x vx hw) (+ width w)) hw) 
        y (- (mod (+ y vy hh) (+ height h)) hh)
        angle (mod (+ angle rot) degree-mask)]
    (assoc obj :x x :y y :angle angle)))

(defn velocity [v a]
  [(* (inc (rand-int v)) (cos a))
   (* (inc (rand-int v)) (sin a))])


(def plus-or-minus [(partial -) (partial +)])
(defn roid-rnd [r] ((rand-nth plus-or-minus) (inc (rand-int r))))
