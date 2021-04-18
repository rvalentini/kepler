(ns ^:figwheel-hooks hohmann-transfer.core
  (:require
    [goog.dom :as gdom]
    [quil.core :as q :include-macros true]
    [quil.middleware :as m]
    [reagent.core :as reagent :refer [atom]]
    [reagent.dom :as rdom]
    [hohmann-transfer.sketch :refer [fps render-sketch build-state transform-scale]]
    [hohmann-transfer.kepler-orbits]
    [hohmann-transfer.circular-orbits]))

;; available sketches:
;; :kepler-orbits
;; :circular-orbits
(def sketch :kepler-orbits)                                 ;TODO fix circular-orbits

;; define your app data so that it doesn't get over-written on reload
(defonce app-state (atom (build-state sketch)))

(defn get-app-element []
  (gdom/getElement "app"))

(defn slider [param value scale]
  (let [slider-scale {:min 0 :max 100}]
    [:input {:type      "range" :value (transform-scale scale slider-scale value) :min 0 :max 100
             :on-change (fn [e]
                          (let [new-value (js/parseInt (.. e -target -value))
                                new-value-scaled (transform-scale slider-scale scale new-value)]
                            (swap! app-state assoc-in [:elliptical-orbit param] new-value-scaled)))}]))

(defn app []
  [:div.dt.mw6.center.pt0.pb5.pv5-m.pv6-ns
   [:div.db.dtc-ns.v-mid-ns
    #_[:img.w-100.mw7.w5-ns {:src "http://tachyons.io/img/super-wide.jpg" :alt "Something else"}]
    [:div {:id "sketch"}]]
   (let [{:keys [e a mass big-omega]} (:elliptical-orbit @app-state)] ;TODO make generic and move to :kepler-orbit
     [:div.db.dtc-ns.v-mid.ph2.pr0-ns.pl3-ns
      [:div.lh-copy
       "Eccentricity"
       [slider :e e {:min 0.01 :max 0.99}]]
      [:div.lh-copy
       "Big Omega"
       [slider :big-omega big-omega {:min 0 :max (* 2 Math/PI)}]]
      [:div.lh-copy
       "Mass of central body"
       [slider :mass mass {:min 1.40E24 :max 20.40E24}]]
      [:div.lh-copy
       "Length of the semimajor axis"
       [slider :a a {:min 0 :max 300}]]])])

(defn mount [el]
  (rdom/render [app] el))

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

(render-sketch sketch (fn []
                        (q/frame-rate fps)
                        app-state))


