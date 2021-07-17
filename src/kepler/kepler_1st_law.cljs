(ns kepler.kepler-1st-law
  (:require
    [quil.core :as q :include-macros true]
    [quil.middleware :as m]
    [kepler.sketch :as s]
    [kepler.orbital-elements :as orb]
    [kepler.sketch :refer [gravitational-const draw-force-arrow]]))

(defn calculate-velocity-angle [{{:keys [a e]} :elliptical-orbit} focus center body]
  (let [center (s/with-translation center (s/invert focus))
        focus (s/with-translation focus (s/invert focus))
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

(defn update-state [state]
  (swap! state assoc-in [:elliptical-orbit :t]
    (+ (get-in @state [:elliptical-orbit :t]) s/time-scale-factor))
  state)

(defn calculate-relative-speed [{{:keys [mass a]} :elliptical-orbit}
                                {focus-x :x focus-y :y}
                                {:keys [x y]}]
  (let [distance (Math/sqrt
                   (+
                     (Math/pow (- focus-x (+ x focus-x)) 2)
                     (Math/pow (- focus-y (+ y focus-y)) 2)))]
    (Math/sqrt (* gravitational-const mass (- (/ 2 distance) (/ 1 a))))))

(defn draw-orbit [a e big-omega center]
  (let [height (* 2 a (Math/sqrt (- 1 (Math/pow e 2))))
        width (* 2 a)]
    (s/draw-dotted-orbit (:x center) (:y center) width height big-omega)))

(defn draw-state [state]
  (let [orbit (:elliptical-orbit @state)
        cog (get-in @state [:center-of-gravity])
        position (orb/orbital-elements->position orbit)
        velocity (* 0.0001 (calculate-relative-speed @state cog position))
        a (:a orbit)
        e (:e orbit)
        big-omega (+ Math/PI (get-in @state [:elliptical-orbit :big-omega]))
        focal-dist (* e a)
        center {:x (+ (:x cog) (* focal-dist (Math/cos big-omega)))
                :y (+ (:y cog) (* focal-dist (Math/sin big-omega)))}
        second-focus {:x (+ (:x center) (* focal-dist (Math/cos big-omega)))
                      :y (+ (:y center) (* focal-dist (Math/sin big-omega)))}]
    (q/scale (/ s/height 500))
    (q/background 240)
    (s/draw-center-of-gravity (:center-of-gravity @state) s/yellow)
    (s/draw-center-of-gravity (assoc second-focus :r 10) s/light-grey)
    (draw-orbit a e big-omega center)
    (let [angle (+ (get-in @state [:elliptical-orbit :big-omega])
                  (calculate-velocity-angle @state cog center position))]
      (s/draw-orbiting-body cog position)
      (q/with-translation [(:x cog) (:y cog)]
        (draw-force-arrow position angle velocity)))))

(defmethod s/build-state :kepler-1st-law []
  {:center-of-gravity {:x 150
                       :y 150
                       :r 10}
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


