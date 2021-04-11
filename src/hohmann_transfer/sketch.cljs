(ns hohmann-transfer.sketch
  (:require [quil.core :as q :include-macros true]))

(defmulti render-sketch identity)

(defn draw-center-of-gravity [{:keys [x y]}]
  (q/stroke 0 0 0)
  (q/fill 0 0 0)
  (q/ellipse x y 20 20))

