(ns kepler.kepler_3rd_law
  (:require
    [quil.core :as q :include-macros true]
    [quil.middleware :as m]
    [kepler.sketch :as s]
    [kepler.orbital-elements :as orb]))

(defn update-state [state]
  (swap! state assoc-in [:elliptical-orbit :t] (+ (get-in @state [:elliptical-orbit :t]) 0.00001))
  state)

(defn draw-dotted-kepler-orbit [state]
  (let [a (get-in state [:elliptical-orbit :a])
        e (get-in state [:elliptical-orbit :e])
        big-omega (+ Math/PI (get-in state [:elliptical-orbit :big-omega]))
        x-center (get-in state [:center-of-gravity :x])
        y-center (get-in state [:center-of-gravity :y])
        focal-dist (* e a)
        height (* 2 a (Math/sqrt (- 1 (Math/pow e 2))))
        width (* 2 a)
        x (+ x-center (* focal-dist (Math/cos big-omega)))
        y (+ y-center (* focal-dist (Math/sin big-omega)))]
    (s/draw-dotted-orbit x y width height big-omega )))

(defn draw-state [state]
  (q/background 240)
  (s/draw-center-of-gravity (:center-of-gravity @state))
  (let [position (orb/orbital-elements->position (:elliptical-orbit @state))]
    (s/draw-orbiting-body
      (assoc (:center-of-gravity @state) :r 10) position))
  (draw-dotted-kepler-orbit @state))

(defmethod s/build-state :kepler-3rd-law []
  {:center-of-gravity {:x (/ s/width 2)
                       :y (/ s/height 2)
                       :r 10}
   :elliptical-orbit  {:t           0
                       :mass        3.674E23
                       :a           200
                       :e           0.6
                       :i           0
                       :small-omega 0
                       :big-omega   (/ Math/PI 2)}
   :view {:locked-controls []}})

(defmethod s/render-sketch :kepler-3rd-law [_ state-setup host]
  (q/defsketch kepler-3rd-law
    :host host
    :size [s/width s/height]
    :setup state-setup
    :update update-state
    :draw draw-state
    :settings #(q/smooth 2)
    :middleware [m/fun-mode]))

;; UI Components

(defn check-invariants [radius e a]
  (let [semi-minor-axis (* a (Math/sqrt (- 1 (Math/pow e 2))))
        invariant-a (< (+ 5 radius) (- a (* e a)))
        invariant-b (> semi-minor-axis (+ 5 radius))]
    (and invariant-a invariant-b)))

(defmulti invariants-satisfied? (fn [param & _] param))

(defmethod invariants-satisfied? :mass [_ app-state new-radius]
  (let [a (get-in app-state [:elliptical-orbit :a])
        e (get-in app-state [:elliptical-orbit :e])]
    (check-invariants new-radius e a)))

(defmethod invariants-satisfied? :e [_ app-state new-ecc]
  (let [a (get-in app-state [:elliptical-orbit :a])
        radius (get-in app-state [:center-of-gravity :r])]
    (check-invariants radius new-ecc a)))

(defmethod invariants-satisfied? :a [_ app-state new-a]
  (let [e (get-in app-state [:elliptical-orbit :e])
        radius (get-in app-state [:center-of-gravity :r])]
    (check-invariants radius e new-a)))

(defmethod invariants-satisfied? :big-omega [_] true)

(defmulti on-change (fn [param & _] (if (= param :mass) :mass :not-mass)))

(defmethod on-change :mass [_ app-state slider-scale scale locked-controls new-mass]
  (let [new-mass-scaled (s/transform-scale slider-scale scale new-mass)
        new-radius (s/transform-scale slider-scale {:min 3 :max 30} new-mass)
        invariants-ok (invariants-satisfied? :mass @app-state new-radius)]
    (swap! app-state #(if invariants-ok
                        (-> %
                          (assoc-in [:center-of-gravity :r] new-radius)
                          (assoc-in [:elliptical-orbit :mass] new-mass-scaled)
                          (assoc-in [:view :locked-controls] (remove #{:mass} locked-controls)))
                        (assoc-in % [:view :locked-controls] (conj locked-controls :mass))))))

(defmethod on-change :not-mass [param app-state slider-scale scale locked-controls new-value]
  (let [new-value-scaled (s/transform-scale slider-scale scale new-value)
        invariants-ok (invariants-satisfied? param @app-state new-value-scaled)]
    (swap! app-state #(if invariants-ok
                        (-> %
                          (assoc-in [:elliptical-orbit param] new-value-scaled)
                          (assoc-in [:view :locked-controls] (remove #{param} locked-controls)))
                        (assoc-in % [:view :locked-controls] (conj locked-controls param))))))

(defn slider [app-state param value scale]
  (let [slider-scale {:min 0 :max 100}
        locked-controls (get-in @app-state [:view :locked-controls])]
    [:input {:type      "range" :value (s/transform-scale scale slider-scale value) :min 0 :max 100
             :class     (when (some #(= param %) locked-controls) "red")
             :on-change (fn [e]
                          (let [new-value (js/parseInt (.. e -target -value))]
                            (on-change param app-state slider-scale scale locked-controls new-value)))}]))

(defn controls [app-state]
  (let [{:keys [e a mass big-omega]} (:elliptical-orbit @app-state)]
    [:div.db.dtc-ns.v-mid.ph2.pr0-ns.pl3-ns
     [:div.w4.f6.lh-copy
      "Eccentricity"
      [slider app-state :e e {:min 0.01 :max 0.90}]]
     [:div.w4.f6.lh-copy
      "Big Omega"
      [slider app-state :big-omega big-omega {:min 0 :max (* 2 Math/PI)}]]
     [:div.w4.f6.lh-copy
      "Mass of central body"
      [slider app-state :mass mass {:min 1.00E22 :max 2.00E24}]]
     [:div.w4.f6.lh-copy
      "Length of the semimajor axis"
      [slider app-state :a a {:min 37 :max 300}]]]))


