(ns hohmann-transfer.kepler-1st-law
  (:require
    [quil.core :as q :include-macros true]
    [quil.middleware :as m]
    [hohmann-transfer.sketch :as s]
    [hohmann-transfer.orbital-elements :as orb]
    [hohmann-transfer.sketch :refer [gravitational-const draw-force-arrow]]))


;TODO (1) make a distinction between all four sectors
;TODO (2) invertiere den Lauf für die oberen beiden Quadranten

(defn calculate-velocity-angle [{{:keys [a e t]} :elliptical-orbit} focus center body]
  (let [center {:x (- (:x center) (:x focus)) :y (- (:y center) (:y focus))}
        focus {:x (- (:x focus) (:x focus)) :y (- (:y focus) (:y focus))}
        b (Math/sqrt (* (Math/pow a 2) (- 1 (Math/pow e 2))))
        t-short-angle (s/calculate-angle center focus body)
        t-full-angle (if (neg? t-short-angle)
                       (+ Math/PI (- Math/PI (- t-short-angle)))
                       t-short-angle)
        slope (* (- (/ b a)) (/ 1 (Math/tan t-full-angle)))
        angle (Math/atan slope)
        velocity-angle (if (> (s/to-degree t-full-angle) 180)
                    (- angle Math/PI)
                    angle)]
    velocity-angle))

;TODO combine duplicated code with 2nd law implementation
(defn draw-orbiting-body [state focus [x y _] center velocity]
  (q/with-fill s/blue
    (q/with-stroke s/blue
      (q/with-translation [(:x focus) (:y focus)]
        (q/ellipse x y (:r focus) (:r focus))
        (let [raw (calculate-velocity-angle state focus center {:x x :y y})
              big-omega (get-in state [:elliptical-orbit :big-omega])
              angle (+ raw big-omega)]
          (draw-force-arrow {:x x :y y}
            angle
            velocity))))))

(defn update-state [state]
  (swap! state assoc-in [:elliptical-orbit :t]
    (+ (get-in @state [:elliptical-orbit :t]) s/time-scale-factor))
  state)

(defn calculate-relative-speed [state focus [x y _]]
  (let [mass (get-in state [:elliptical-orbit :mass])
        a (get-in state [:elliptical-orbit :a])
        focus_x (:x focus)
        focus_y (:y focus)
        distance (Math/sqrt (+
                              (Math/pow (- focus_x (+ x focus_x)) 2)
                              (Math/pow (- focus_y (+ y focus_y)) 2)))]
    (Math/sqrt (* gravitational-const mass (- (/ 2 distance) (/ 1 a))))))

;TODO make generic "dotted ellipsis" function and combine with circular use case
;TODO combine with 2nd law and circular orbits
(defn draw-dotted-kepler-orbit [big-omega center width height]
  (q/with-stroke s/dark-brown
    (q/with-fill [nil]
      (let [arc-steps (partition 2 (map s/to-radians (range 0 360 2)))]
        (q/with-translation [(:x center) (:y center)]
          (q/with-rotation [big-omega]
            (doseq [[start stop] arc-steps]
              (q/arc 0 0 width height start stop :open))))))))

(defn draw-orbit [a e big-omega center]
  (let [height (* 2 a (Math/sqrt (- 1 (Math/pow e 2))))
        width (* 2 a)]
    (draw-dotted-kepler-orbit big-omega center width height)))

(defn draw-state [state]
  (let [orbit (:elliptical-orbit @state)
        focus {:x (get-in @state [:center-of-gravity :x])
               :y (get-in @state [:center-of-gravity :y])
               :r (get-in @state [:center-of-gravity :radius])}
        position (orb/orbital-elements->position orbit)
        velocity (* 0.0001 (calculate-relative-speed @state focus position))
        a (get-in @state [:elliptical-orbit :a])
        e (get-in @state [:elliptical-orbit :e])
        big-omega (+ Math/PI (get-in @state [:elliptical-orbit :big-omega]))
        focal-dist (* e a)
        center {:x (+ (:x focus) (* focal-dist (Math/cos big-omega)))
                :y (+ (:y focus) (* focal-dist (Math/sin big-omega)))}]
    (q/background 240)
    (s/draw-center-of-gravity (:center-of-gravity @state))
    (draw-orbit a e big-omega center)
    (draw-orbiting-body @state focus position center velocity)))

(defmethod s/build-state :kepler-1st-law []
  {:center-of-gravity {:x      150
                       :y      150
                       :radius 10}
   :elliptical-orbit  {:t           0
                       :mass        4E23
                       :a           200
                       :e           0.5
                       :i           0
                       :small-omega 0
                       :big-omega   (- (/ (* 3 Math/PI) 4))}})

(defmethod s/render-sketch :kepler-1st-law [_ state-setup host]
  (q/defsketch kepler-1st-law
    :host host
    :size [s/width s/height]
    :setup state-setup
    :update update-state
    :draw draw-state
    :settings #(q/smooth 2)
    :middleware [m/fun-mode]))


