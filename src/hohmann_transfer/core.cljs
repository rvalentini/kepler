(ns ^:figwheel-hooks hohmann-transfer.core
  (:require
    [goog.dom :as gdom]
    [quil.core :as q :include-macros true]
    [quil.middleware :as m]
    [reagent.core :as reagent :refer [atom]]
    [reagent.dom :as rdom]))

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
   :orbit-1           {:radius 200}
   :orbit-2           {:radius 400}
   :spacecraft        {:angle  0.0
                       :radius 200
                       :revolutions-per-sec 0.1}}) ;use '-' for counter clock-wise

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
      (q/arc x y radius radius start stop :open))))

(defn draw-center-of-gravity [{:keys [x y]}]
  (q/fill 0 0 0)
  (q/ellipse x y 20 20))

(defn draw-spacecraft [{:keys [angle radius]}]
  (q/stroke 170)
  (q/fill 0 153 255)
  (let [x (* radius (Math/cos angle))
        y (* radius (Math/sin angle))]
    (q/with-translation [(/ (q/width) 2) (/ (q/height) 2)]
      (q/ellipse x y 10 10))))

(defn draw-state [state]
  (q/background 240)
  (draw-center-of-gravity (:center-of-gravity state))
  (draw-dotted-orbit (:orbit-1 state) (:center-of-gravity state))
  (draw-dotted-orbit (:orbit-2 state) (:center-of-gravity state))
  (draw-spacecraft (:spacecraft state)))


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
