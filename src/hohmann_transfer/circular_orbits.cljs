(ns hohmann-transfer.circular-orbits
  (:require
    [quil.core :as q :include-macros true]
    [quil.middleware :as m]
    [hohmann-transfer.sketch :refer [render-sketch draw-center-of-gravity]]))

;; TERMS
;; apoapsis = largest distance between two bodies orbiting around the same center of mass on elliptic curves
;; periapsis = shortest distance between two bodies orbiting around the same center of mass on elliptic curves

;; FORMULARS
;; E-kin = 1/2 mv^2
;; Thābit ibn Qurra formular for arbitraty triangles: a^2 + b^2 = c * (r + s)

(def fps 30)

(defn to-radians [deg]
  (/ (* deg q/PI) 180))

(defn draw-force-arrow [start angle magnitude]
  (q/stroke 255 0 0)
  (let [end-x (+ (:x start) (* (Math/cos angle) magnitude))
        end-y (+ (:y start) (* (Math/sin angle) magnitude))
        arrow-angle (to-radians (- 180 25))]
    (q/line (:x start) (:y start) end-x end-y)
    (let [l-end-x (+ end-x (* (Math/cos (- angle arrow-angle)) 10))
          l-end-y (+ end-y (* (Math/sin (- angle arrow-angle)) 10))
          r-end-x (+ end-x (* (Math/cos (+ angle arrow-angle)) 10))
          r-end-y (+ end-y (* (Math/sin (+ angle arrow-angle)) 10))]
      (q/line end-x end-y l-end-x l-end-y)
      (q/line end-x end-y r-end-x r-end-y))))

(defn draw-spacecraft [{:keys [angle radius]}]
  (q/stroke 170)
  (q/fill 0 153 255)
  (q/with-translation [(/ (q/width) 2) (/ (q/height) 2)]
    (let [x (* radius (Math/cos angle))
          y (* radius (Math/sin angle))]
      (q/ellipse x y 10 10)
      (draw-force-arrow {:x x :y y} (+ Math/PI angle) (* 0.6 radius)))))


(defn draw-dotted-orbit [{:keys [radius]} {:keys [x y]}]
  (q/stroke 170)
  (q/stroke-weight 1)
  (q/fill nil)
  (let [arc-steps (partition 2 (map to-radians (range 0 360 2)))]
    (doseq [[start stop] arc-steps]
      (q/arc x y (* radius 2) (* radius 2) start stop :open))))

(defn draw-color-trace [{:keys [radius angle]} {:keys [x y]}]
  (q/stroke 255 165 0)
  (q/stroke-weight 3)
  (q/fill nil)
  (let [d (* radius 2)
        start (- angle (/ Math/PI 2))
        end (- angle (/ 10 radius))]
    (q/arc x y d d start end :open)))

(defn calculate-angle-dif [{:keys [revolutions-per-sec]}]
  (/ (* revolutions-per-sec 2 Math/PI) fps))

(defn update-state [state]
  (let [dif (calculate-angle-dif (:spacecraft state))]
    (update-in state [:spacecraft :angle] #(+ % dif))))

(defn draw-state [state]
  (q/background 240)
  (draw-center-of-gravity (:center-of-gravity state))
  (draw-dotted-orbit (:orbit-1 state) (:center-of-gravity state))
  (draw-dotted-orbit (:orbit-2 state) (:center-of-gravity state))
  (draw-spacecraft (:spacecraft state))
  (draw-color-trace (:spacecraft state) (:center-of-gravity state)))

(defn setup []
  (q/frame-rate fps)
  {:center-of-gravity {:x (/ (q/width) 2)
                       :y (/ (q/height) 2)}
   :orbit-1           {:radius 100}
   :orbit-2           {:radius 200}
   :spacecraft        {:angle               0.0
                       :radius              200
                       :revolutions-per-sec 0.3}})          ;use '-' for counter clock-wise

(defmethod render-sketch :circular-orbits []
  (q/defsketch circular-orbits
    :host "sketch"
    :size [500 500]
    :setup setup
    :update update-state
    :draw draw-state
    :settings #(q/smooth 2)
    :middleware [m/fun-mode]))