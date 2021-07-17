(ns kepler.kepler-2nd-law
  (:require
    [quil.core :as q :include-macros true]
    [quil.middleware :as m]
    [kepler.sketch :as s]
    [kepler.orbital-elements :as orb]))

(defn update-state [state]
  (swap! state assoc-in [:elliptical-orbit :t]
    (+ (get-in @state [:elliptical-orbit :t]) s/time-scale-factor))
  state)

(defn draw-delta-arc [center focus big-omega width height pos-1 pos-2]
  (let [angle1 (s/calculate-angle center focus (s/with-translation pos-1 focus))
        angle2 (s/calculate-angle center focus (s/with-translation pos-2 focus))]
    (q/with-stroke nil
      (q/with-fill s/purple-opaque
        (q/with-translation [(:x focus) (:y focus)]
          (q/triangle 0 0 (:x pos-1) (:y pos-1) (:x pos-2) (:y pos-2)))))
    (q/with-translation [(:x center) (:y center)]
      (q/with-rotation [big-omega]
        (q/with-stroke s/purple-opaque
          (q/with-fill s/purple-opaque
            (q/arc 0 0 width height angle1 angle2 :open)))))))

(defn draw-orbit [state focus position-1 position-2]
  (let [a (get-in state [:elliptical-orbit :a])
        e (get-in state [:elliptical-orbit :e])
        big-omega (+ Math/PI (get-in state [:elliptical-orbit :big-omega]))
        focal-dist (* e a)
        height (* 2 a (Math/sqrt (- 1 (Math/pow e 2))))
        width (* 2 a)
        center {:x (+ (:x focus) (* focal-dist (Math/cos big-omega)))
               :y (+ (:y focus) (* focal-dist (Math/sin big-omega)))}]
    (s/draw-dotted-orbit (:x center) (:y center) width height  big-omega)
    (draw-delta-arc center focus big-omega width height position-1 position-2)))

(defn draw-state [state]
  (let [period (Math/sqrt (Math/pow (get-in @state [:elliptical-orbit :a]) 3))
        delta-t (* s/time-scale-factor period (/ 1 s/fps))  ;move forward 1s in animation time
        orbit (:elliptical-orbit @state)
        cog (:center-of-gravity @state)
        position-1 (orb/orbital-elements->position orbit)
        position-2 (orb/orbital-elements->position (update-in orbit [:t] #(+ % delta-t)))]
    (q/scale (/ s/height 500))
    (q/background 240)
    (s/draw-center-of-gravity (:center-of-gravity @state))
    (draw-orbit @state cog position-1 position-2)
    (s/draw-orbiting-body cog position-1)
    (s/draw-orbiting-body cog position-2)))

(defmethod s/build-state :kepler-2nd-law []
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

(defmethod s/render-sketch :kepler-2nd-law [_ state-setup host]
  (q/defsketch kepler-2nd-law
    :host host
    :size [s/width s/height]
    :setup state-setup
    :update update-state
    :draw draw-state
    :settings #(q/smooth 2)
    :middleware [m/fun-mode]))


