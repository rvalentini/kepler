(ns hohmann-transfer.kepler-orbits
  (:require
    [quil.core :as q :include-macros true]
    [quil.middleware :as m]
    [hohmann-transfer.sketch :refer [render-sketch draw-center-of-gravity]]
    [hohmann-transfer.orbital-elements :as orb]))

(def fps 30)

(defn draw-orbiting-body [state]
  (q/stroke 170)
  (q/fill 0 153 255)
  (q/with-translation [(/ (q/width) 2) (/ (q/height) 2)]
    (let [orbit (:elliptical-orbit state)
          [x y z] (orb/orbital-elements->position (:t orbit) (:mass orbit) (:a orbit) (:e orbit) (:i orbit) (:small-omega orbit) (:big-omega orbit))]
      #_(println (str "time: " (:t orbit)))
      #_(println (str "position: " x y z))
      (q/ellipse x y 10 10))))

(defn update-state [state]
  (update-in state [:elliptical-orbit :t] #(+ % 0.00001)))

(defn draw-state [state]
  #_(println (str "current time: " (get-in state [:elliptical-orbit :t])))
  (q/background 240)
  (draw-center-of-gravity (:center-of-gravity state))
  (draw-orbiting-body state))

(defn setup []
  (q/frame-rate fps)
  {:center-of-gravity {:x (/ (q/width) 2)
                       :y (/ (q/height) 2)}
   :elliptical-orbit  {:t           0
                       :mass        3.674E24
                       :a           150
                       :e           0.6
                       :i           0
                       :small-omega 0
                       :big-omega   (/ Math/PI 2)}})

(defmethod render-sketch :kepler-orbits []
  (q/defsketch kepler-orbits
    :host "sketch"
    :size [500 500]
    :setup setup
    :update update-state
    :draw draw-state
    :settings #(q/smooth 2)
    :middleware [m/fun-mode]))
