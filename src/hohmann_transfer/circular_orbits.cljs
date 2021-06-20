(ns hohmann-transfer.circular-orbits
  (:require
    [quil.core :as q :include-macros true]
    [quil.middleware :as m]
    [hohmann-transfer.sketch :as s]))

;; TERMS
;; apoapsis = largest distance between two bodies orbiting around the same center of mass on elliptic curves
;; periapsis = shortest distance between two bodies orbiting around the same center of mass on elliptic curves

;; FORMULARS
;; E-kin = 1/2 mv^2
;; Thābit ibn Qurra formular for arbitraty triangles: a^2 + b^2 = c * (r + s)

(defn draw-spacecraft [{:keys [angle r]}]
  (q/stroke 170)
  (q/fill 0 153 255)
  (q/with-translation [(/ (q/width) 2) (/ (q/height) 2)]
    (let [x (* r (Math/cos angle))
          y (* r (Math/sin angle))]
      (q/ellipse x y 10 10)
      (s/draw-force-arrow {:x x :y y} (+ Math/PI angle) (* 0.6 r)))))

(defn draw-dotted-orbit [{:keys [r]} {:keys [x y]}]
  (q/stroke 170)
  (q/stroke-weight 1)
  (q/fill nil)
  (let [arc-steps (partition 2 (map s/to-radians (range 0 360 2)))]
    (doseq [[start stop] arc-steps]
      (q/arc x y (* r 2) (* r 2) start stop :open))))

(defn draw-color-trace [{:keys [r angle]} {:keys [x y]}]
  (q/stroke 255 165 0)
  (q/stroke-weight 3)
  (q/fill nil)
  (let [d (* r 2)
        start (- angle (/ Math/PI 2))
        end (- angle (/ 10 r))]
    (q/arc x y d d start end :open)))

(defn calculate-angle-dif [{:keys [revolutions-per-sec]}]
  (/ (* revolutions-per-sec 2 Math/PI) s/fps))

(defn update-state [state]
  (let [dif (calculate-angle-dif (:spacecraft @state))]
    (swap! state assoc-in [:spacecraft :angle] (+ (get-in @state [:spacecraft :angle]) dif))
    state))

(defn draw-state [state]
  (q/background 240)
  (s/draw-center-of-gravity (:center-of-gravity @state))
  (draw-dotted-orbit (:orbit-1 @state) (:center-of-gravity @state))
  (draw-dotted-orbit (:orbit-2 @state) (:center-of-gravity @state))
  (draw-spacecraft (:spacecraft @state))
  (draw-color-trace (:spacecraft @state) (:center-of-gravity @state)))

(defmethod s/build-state :circular-orbits []
  {:center-of-gravity {:x (/ s/width 2)
                       :y (/ s/height 2)
                       :r 10}
   :orbit-1           {:r 100}
   :orbit-2           {:r 200}
   :spacecraft        {:angle               0.0
                       :r                   200
                       :revolutions-per-sec 0.3}})          ;use '-' for counter clock-wise

(defmethod s/render-sketch :circular-orbits [_ state-setup host]
  (q/defsketch circular-orbits
    :host host
    :size [s/width s/height]
    :setup state-setup
    :update update-state
    :draw draw-state
    :settings #(q/smooth 2)
    :middleware [m/fun-mode]))
