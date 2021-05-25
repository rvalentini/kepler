(ns hohmann-transfer.sketch
  (:require [quil.core :as q :include-macros true]))

(def fps 60)
(def time-scale-factor (/ 1 100000))
(def width 500)
(def height 500)

(def yellow [255 183 0])
(def pink [224 101 255])
(def pink-opaque [224 101 255 100])
(def blue [0 181 203])
(def dark-brown [79 69 56])
(def light-grey [223 224 223])

(defmulti render-sketch (fn [name _] name))
(defmulti build-state identity)

(defn to-radians [deg]
  (/ (* deg q/PI) 180))

(defn draw-center-of-gravity [{:keys [x y radius]}]
  ;TODO not complete black!
  (q/with-stroke dark-brown
    (q/with-fill dark-brown
      (q/ellipse x y (* radius 2) (* radius 2)))))

(defn transform-scale [scale1 scale2 value]
  (if-not
    (<= (:min scale1) value (:max scale1))
    (throw (new js/RangeError "Given value is not in range!")))
  (let [rel-scale1 (/ (- value (:min scale1)) (- (:max scale1) (:min scale1)))]
    (+ (* rel-scale1 (- (:max scale2) (:min scale2))) (:min scale2))))

