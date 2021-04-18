(ns hohmann-transfer.kepler-orbits
  (:require
    [quil.core :as q :include-macros true]
    [quil.middleware :as m]
    [hohmann-transfer.sketch :refer [render-sketch build-state draw-center-of-gravity]]
    [hohmann-transfer.orbital-elements :as orb]))

(defn draw-orbiting-body [state]
  (q/stroke 170)
  (q/fill 0 153 255)
  (q/with-translation [(/ (q/width) 2) (/ (q/height) 2)]
    (let [orbit (:elliptical-orbit state)
          [x y _] (orb/orbital-elements->position (:t orbit) (:mass orbit) (:a orbit) (:e orbit) (:i orbit) (:small-omega orbit) (:big-omega orbit))]
      (q/ellipse x y 10 10))))

(defn update-state [state]
  #_(println (str "STATE IS: " @state))
  (swap! state assoc-in [:elliptical-orbit :t] (+ (get-in @state [:elliptical-orbit :t]) 0.00001))
  state)

(defn draw-state [state]
  (q/background 240)
  (draw-center-of-gravity (:center-of-gravity @state))
  (draw-orbiting-body @state))

(defmethod build-state :kepler-orbits []
  {:center-of-gravity {:x (/ 500 2)                         ;;TODO make 500 configuraable without q/ call
                       :y (/ 500 2)}
   :elliptical-orbit  {:t           0
                       :mass        3.674E24
                       :a           150
                       :e           0.6
                       :i           0
                       :small-omega 0
                       :big-omega   (/ Math/PI 2)}})

(defmethod render-sketch :kepler-orbits [_ state-setup]
  (q/defsketch kepler-orbits
    :host "sketch"
    :size [500 500]
    :setup state-setup
    :update update-state
    :draw draw-state
    :settings #(q/smooth 2)
    :middleware [m/fun-mode]))
