(ns ^:figwheel-hooks hohmann-transfer.core
  (:require
    [goog.dom :as gdom]
    [quil.core :as q :include-macros true]
    [quil.middleware :as m]
    [reagent.core :as reagent :refer [atom]]
    [reagent.dom :as rdom]))

;; TERMS
;; apoapsis = largest distance between two bodies orbiting around the same center of mass on elliptic curves
;; periapsis = shortest distance between two bodies orbiting around the same center of mass on elliptic curves
;;

;; FORMULARS
;; E-kin = 1/2 mv^2
;; Thābit ibn Qurra formular for arbitraty triangles: a^2 + b^2 = c * (r + s)

(def fps 30)

(println "This text is printed from src/hohmann_transfer/core.cljs. Go ahead and edit it and see reloading in action.")

(defn multiply [a b] (* a b))

;; define your app data so that it doesn't get over-written on reload
(defonce app-state (atom {:text "Some app state"}))

(defn get-app-element []
  (gdom/getElement "app"))

(defn hello-world []
  [:div
   [:h1 "Hohmann Transfer"]
   [:h3 (str "You are able to see the state from a reagent atom here: " @app-state)]])

(defn mount [el]
  (rdom/render [hello-world] el))

(defn mount-app-element []
  (when-let [el (get-app-element)]
    (mount el)))

;; conditionally start your application based on the presence of an "app" element
;; this is particularly helpful for testing this ns without launching the app
(mount-app-element)

;; specify reload hook with ^;after-load metadata
(defn ^:after-load on-reload []
  (mount-app-element)
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
  )

(defn setup []
  (q/frame-rate fps)
  ; Set color mode to HSB (HSV) instead of default RGB.
  {:center-of-gravity {:x (/ (q/width) 2)
                       :y (/ (q/height) 2)}
   :orbit-1           {:radius 100}
   :orbit-2           {:radius 200}
   :spacecraft        {:angle  0.0
                       :radius 200
                       :revolutions-per-sec 0.3}}) ;use '-' for counter clock-wise

(defn calculate-angle-dif [{:keys [revolutions-per-sec]}]
  (/ (* revolutions-per-sec 2 Math/PI) fps))

(defn to-radians [deg]
  (/ (* deg q/PI) 180))

(defn update-state [state]
  (let [dif (calculate-angle-dif (:spacecraft state))]
    (update-in state [:spacecraft :angle] #(+ % dif))))

(defn draw-dotted-orbit [{:keys [radius]} {:keys [x y]}]
  (q/stroke 170)
  (q/stroke-weight 1)
  (q/fill nil)
  (let [arc-steps (partition 2 (map to-radians (range 0 360 2)))]
    (doseq [[start stop] arc-steps]
      (q/arc x y (* radius 2) (* radius 2)  start stop :open))))

(defn draw-color-trace [{:keys [radius angle]} {:keys [x y]}]
  (q/stroke 255 165 0)
  (q/stroke-weight 3)
  (q/fill nil)
  (let [d (* radius 2)
        start (- angle (/ Math/PI 2))
        end (- angle (/ 10  radius))]
    (q/arc x y d d start end :open)))

(defn draw-center-of-gravity [{:keys [x y]}]
  (q/stroke 0 0 0)
  (q/fill 0 0 0)
  (q/ellipse x y 20 20))

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

(defn draw-state [state]
  (q/background 240)
  (draw-center-of-gravity (:center-of-gravity state))
  (draw-dotted-orbit (:orbit-1 state) (:center-of-gravity state))
  (draw-dotted-orbit (:orbit-2 state) (:center-of-gravity state))
  (draw-spacecraft (:spacecraft state))
  (draw-color-trace (:spacecraft state) (:center-of-gravity state)))


(q/defsketch hohmann-transfer
  :host "hohmann-transfer"
  :size [500 500]
  ; setup function called only once, during sketch initialization.
  :setup setup
  ; update-state is called on each iteration before draw-state.
  :update update-state
  :draw draw-state
  :settings #(q/smooth 2)
  ; This sketch uses functional-mode middleware.
  ; Check quil wiki for more info about middlewares and particularly
  ; fun-mode.
  :middleware [m/fun-mode])
