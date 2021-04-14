(ns ^:figwheel-hooks hohmann-transfer.core
  (:require
    [goog.dom :as gdom]
    [quil.core :as q :include-macros true]
    [quil.middleware :as m]
    [reagent.core :as reagent :refer [atom]]
    [reagent.dom :as rdom]
    [hohmann-transfer.sketch :refer [render-sketch]]
    [hohmann-transfer.kepler-orbits]
    [hohmann-transfer.circular-orbits]))

;; available sketches:
;; :kepler-orbits
;; :circular-orbits
(def sketch :kepler-orbits)

;; define your app data so that it doesn't get over-written on reload
(defonce app-state (atom {:text "Some app state"}))

(defn get-app-element []
  (gdom/getElement "app"))

(defn slider []
  [:input {:type "range" :min 1 :max 100 :step 1
           :on-change (fn [e]
                        (let [new-value (js/parseInt (.. e -target -value))]
                          (println (str "called with new value change" new-value))))}])

(defn app []
  [:div.dt.mw6.center.pt0.pb5.pv5-m.pv6-ns
   [:div.db.dtc-ns.v-mid-ns
    #_[:img.w-100.mw7.w5-ns {:src "http://tachyons.io/img/super-wide.jpg" :alt "Something else"}]
    [:div {:id "sketch"}]]
   [:div.db.dtc-ns.v-mid.ph2.pr0-ns.pl3-ns
    [:div.lh-copy
     "Eccentricity"
     [slider]]
    [:div.lh-copy
     "Big Omega"
     [slider]]
    [:div.lh-copy
     "Mass of central body"
     [slider]]
    [:div.lh-copy
     "Length of the semimajor axis"
     [slider]]]])

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

(render-sketch sketch)                                      ;;TODO maybe give react-state wrapped as setup function to render-sketch for interactive use
;;TODO each sketch could offer a 'construct state function' or something similar also as multimethod


