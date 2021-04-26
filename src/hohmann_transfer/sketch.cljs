(ns hohmann-transfer.sketch
  (:require [quil.core :as q :include-macros true]))

(def fps 30)
(def width 500)
(def height 500)

(defmulti render-sketch (fn [name _] name))
(defmulti build-state identity)

(defn to-radians [deg]
  (/ (* deg q/PI) 180))

(defn draw-center-of-gravity [{:keys [x y]}]
  (q/stroke 0 0 0)
  (q/fill 0 0 0)
  (q/ellipse x y 20 20))

(defn transform-scale [scale1 scale2 value]
  (if-not
    (<= (:min scale1) value (:max scale1))
    (throw (new js/RangeError "Given value is not in range!")))
  (let [rel-scale1 (/ (- value (:min scale1)) (- (:max scale1) (:min scale1)))]
    (+ (* rel-scale1 (- (:max scale2) (:min scale2))) (:min scale2))))

