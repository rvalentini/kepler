(ns hohmann-transfer.sketch
  (:require [quil.core :as q :include-macros true]))

(def fps 60)
(def time-scale-factor (/ 1 100000))
(def width 500)
(def height 500)

;; G =  6.674×10−11 m^3⋅kg^−1⋅s^−2
(def gravitational-const 6.674E-11)

(def yellow [255 183 0])
(def pink [224 101 255])
(def pink-opaque [224 101 255 100])
(def blue [0 181 203])
(def dark-brown [79 69 56])
(def light-grey [223 224 223])

(defmulti render-sketch (fn [name _ _] name))
(defmulti build-state identity)

(defn to-radians [deg]
  (/ (* deg q/PI) 180))

(defn to-degree [rad]
  (/ (* rad 180) Math/PI))

(defn draw-center-of-gravity
  ([state] (draw-center-of-gravity state dark-brown))
  ([{:keys [x y radius]} color]
   (q/with-stroke color
     (q/with-fill color
       (q/ellipse x y (* radius 2) (* radius 2))))))

(defn draw-orbiting-body [{focus-x :x focus-y :y r :r}
                          {:keys [x y]}]
  (q/with-fill blue
    (q/with-stroke blue
      (q/with-translation [focus-x focus-y]
        (q/ellipse x y r r)))))

(defn draw-force-arrow [start angle magnitude]
  (q/with-stroke [pink]
    (q/stroke-weight 2)
    (let [end-x (+ (:x start) (* (Math/cos angle) magnitude))
          end-y (+ (:y start) (* (Math/sin angle) magnitude))
          arrow-angle (to-radians (- 180 25))]
      (q/line (:x start) (:y start) end-x end-y)
      (let [l-end-x (+ end-x (* (Math/cos (- angle arrow-angle)) 10))
            l-end-y (+ end-y (* (Math/sin (- angle arrow-angle)) 10))
            r-end-x (+ end-x (* (Math/cos (+ angle arrow-angle)) 10))
            r-end-y (+ end-y (* (Math/sin (+ angle arrow-angle)) 10))]
        (q/line end-x end-y l-end-x l-end-y)
        (q/line end-x end-y r-end-x r-end-y)))))

(defn transform-scale [scale1 scale2 value]
  (if-not
    (<= (:min scale1) value (:max scale1))
    (throw (new js/RangeError "Given value is not in range!")))
  (let [rel-scale1 (/ (- value (:min scale1)) (- (:max scale1) (:min scale1)))]
    (+ (* rel-scale1 (- (:max scale2) (:min scale2))) (:min scale2))))

(defn calculate-angle [center focus body]
  (let [v1 [(- (:x center) (:x focus)) (- (:y center) (:y focus))]
        v2 [(- (:x body) (:x center)) (- (:y body) (:y center))]
        det (- (* (first v1) (second v2)) (* (second v1) (first v2)))
        dot (+ (* (first v1) (first v2)) (* (second v1) (second v2)))]
    (Math/atan2 det dot)))

(defn draw-dotted-orbit [x y width height rotation ]
  (q/stroke-weight 1)
  (q/fill nil)
  (q/with-stroke dark-brown
    (q/with-fill [nil]
      (let [arc-steps (partition 2 (map to-radians (range 0 360 2)))]
        (q/with-translation [x y]
          (q/with-rotation [rotation]
            (doseq [[start stop] arc-steps]
              (q/arc 0 0 width height start stop :open)))))))
  )

