(ns hohmann-transfer.kepler-orbits
  (:require
    [quil.core :as q :include-macros true]
    [quil.middleware :as m]
    [hohmann-transfer.sketch :as s]
    [hohmann-transfer.orbital-elements :as orb]))

(defn draw-orbiting-body [state]
  (q/stroke 170)
  (q/fill 206 18 18)
  (q/with-translation [(/ (q/width) 2) (/ (q/height) 2)]
    (let [orbit (:elliptical-orbit state)
          [x y _] (orb/orbital-elements->position (:t orbit) (:mass orbit) (:a orbit) (:e orbit) (:i orbit) (:small-omega orbit) (:big-omega orbit))]
      (q/ellipse x y 10 10))))

(defn update-state [state]
  (swap! state assoc-in [:elliptical-orbit :t] (+ (get-in @state [:elliptical-orbit :t]) 0.00001))
  state)

;TODO make generic "dotted ellipsis" function and combine with circular use case
(defn draw-dotted-kepler-orbit [state]
  (q/stroke 170)
  (q/stroke-weight 1)
  (q/fill nil)
  (let [a (get-in state [:elliptical-orbit :a])
        e (get-in state [:elliptical-orbit :e])
        big-omega (+ Math/PI (get-in state [:elliptical-orbit :big-omega])) ;TODO why + PI necessary at all?
        x-center (get-in state [:center-of-gravity :x])
        y-center (get-in state [:center-of-gravity :y])
        focal-dist (* e a)
        height (* 2 a (Math/sqrt (- 1 (Math/pow e 2))))
        width (* 2 a)
        x (+ x-center (* focal-dist (Math/cos big-omega)))
        y (+ y-center (* focal-dist (Math/sin big-omega)))
        arc-steps (partition 2 (map s/to-radians (range 0 360 2)))]
    (q/translate x y)
    (q/rotate big-omega)
    (doseq [[start stop] arc-steps]
      (q/arc 0 0 width height start stop :open))))

(defn draw-state [state]
  (q/background 240)
  (s/draw-center-of-gravity (:center-of-gravity @state))
  (draw-orbiting-body @state)
  (draw-dotted-kepler-orbit @state))

(defmethod s/build-state :kepler-orbits []
  {:center-of-gravity {:x (/ s/width 2)
                       :y (/ s/height 2)
                       :radius 10}
   :elliptical-orbit  {:t           0
                       :mass        3.674E23
                       :a           200
                       :e           0.6
                       :i           0
                       :small-omega 0
                       :big-omega   (/ Math/PI 2)}})

(defmethod s/render-sketch :kepler-orbits [_ state-setup]
  (q/defsketch kepler-orbits
    :host "sketch"
    :size [s/width s/height]
    :setup state-setup
    :update update-state
    :draw draw-state
    :settings #(q/smooth 2)
    :middleware [m/fun-mode]))

;; UI Components

(defn slider [app-state param value scale]
  (let [slider-scale {:min 0 :max 100}]
    [:input {:type      "range" :value (s/transform-scale scale slider-scale value) :min 0 :max 100
             :on-change (fn [e]
                          (let [new-value (js/parseInt (.. e -target -value))
                                new-value-scaled (s/transform-scale slider-scale scale new-value)]
                            (if (= param :mass)
                              (let [new-radius (s/transform-scale slider-scale {:min 3 :max 30} new-value)]
                                (swap! app-state assoc-in [:center-of-gravity :radius] new-radius)))
                            (swap! app-state assoc-in [:elliptical-orbit param] new-value-scaled)))}]))

(defn controls [app-state]
  (let [{:keys [e a mass big-omega]} (:elliptical-orbit @app-state)] ;TODO make generic and move to :kepler-orbit
    [:div.db.dtc-ns.v-mid.ph2.pr0-ns.pl3-ns
     [:div.lh-copy
      "Eccentricity"
      [slider app-state :e e {:min 0.01 :max 0.90}]]
     [:div.lh-copy
      "Big Omega"
      [slider app-state :big-omega big-omega {:min 0 :max (* 2 Math/PI)}]]
     [:div.lh-copy
      "Mass of central body"
      [slider app-state :mass mass {:min 1.00E22 :max 2.00E24}]]
     [:div.lh-copy
      "Length of the semimajor axis"
      [slider app-state :a a {:min 37 :max 300}]]]))


