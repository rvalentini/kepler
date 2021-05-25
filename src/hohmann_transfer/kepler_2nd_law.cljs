(ns hohmann-transfer.kepler-2nd-law
  (:require
    [quil.core :as q :include-macros true]
    [quil.middleware :as m]
    [hohmann-transfer.sketch :as s]
    [hohmann-transfer.orbital-elements :as orb]))

(defn draw-orbiting-body [focus [x y _]]
  (q/with-fill s/blue
    (q/with-stroke s/blue
      (q/with-translation [(:x focus) (:y focus)]
        (q/ellipse x y (:r focus) (:r focus))))))

(defn update-state [state]
  (swap! state assoc-in [:elliptical-orbit :t]
    (+ (get-in @state [:elliptical-orbit :t]) s/time-scale-factor))
  state)

(defn calculate-angle [center focus body]
  (let [v1 [(- (:x center) (:x focus)) (- (:y center) (:y focus))]
        v2 [(- (:x body) (:x center)) (- (:y body) (:y center))]
        det (- (* (first v1) (second v2)) (* (second v1) (first v2)))
        dot (+ (* (first v1) (first v2)) (* (second v1) (second v2)))]
    (Math/atan2 det dot)))

;TODO make generic "dotted ellipsis" function and combine with circular use case
(defn draw-dotted-kepler-orbit [big-omega center width height]
  (q/with-stroke s/dark-brown
    (q/with-fill [nil]
      (let [arc-steps (partition 2 (map s/to-radians (range 0 360 2)))]
        (q/with-translation [(:x center) (:y center)]
          (q/with-rotation [big-omega]
            (doseq [[start stop] arc-steps]
              (q/arc 0 0 width height start stop :open))))))))

(defn draw-delta-arc [center focus big-omega width height [x1 y1 _] [x2 y2 _]]
  (let [angle1 (calculate-angle center focus {:x (+ (:x focus) x1)
                                              :y (+ (:y focus) y1)})
        angle2 (calculate-angle center focus {:x (+ (:x focus) x2)
                                              :y (+ (:y focus) y2)})]
    (q/with-stroke nil
      (q/with-fill s/pink-opaque
        (q/triangle
          (:x focus) (:y focus)
          (+ (:x focus) x1) (+ (:y focus) y1)
          (+ (:x focus) x2) (+ (:y focus) y2))))
    (q/with-translation [(:x center) (:y center)]
      (q/with-rotation [big-omega]
        (q/with-stroke s/pink-opaque
          (q/with-fill s/pink-opaque
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
    (draw-dotted-kepler-orbit big-omega center width height)
    (draw-delta-arc center focus big-omega width height position-1 position-2)))

(defn draw-state [state]
  (let [period (Math/sqrt (Math/pow (get-in @state [:elliptical-orbit :a]) 3))
        delta-t (* s/time-scale-factor period (/ 1 s/fps))  ;move forward 1s in animation time
        orbit (:elliptical-orbit @state)
        focus {:x (get-in @state [:center-of-gravity :x])
               :y (get-in @state [:center-of-gravity :y])
               :r (get-in @state [:center-of-gravity :radius])}
        position-1 (orb/orbital-elements->position orbit)
        position-2 (orb/orbital-elements->position (update-in orbit [:t] #(+ % delta-t)))]
    (q/background 240)
    (s/draw-center-of-gravity (:center-of-gravity @state))
    (draw-orbit @state focus position-1 position-2)
    (draw-orbiting-body focus position-1)
    (draw-orbiting-body focus position-2)))

(defmethod s/build-state :kepler-2nd-law []
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

(defmethod s/render-sketch :kepler-2nd-law [_ state-setup]
  (q/defsketch kepler-2nd-law
    :host "sketch"
    :size [s/width s/height]
    :setup state-setup
    :update update-state
    :draw draw-state
    :settings #(q/smooth 2)
    :middleware [m/fun-mode]))


