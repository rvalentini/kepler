(ns ^:figwheel-hooks hohmann-transfer.core
  (:require
    [goog.dom :as gdom]
    [quil.core :as q :include-macros true]
    [reagent.core :refer [atom]]
    [reagent.dom :as rdom]
    [hohmann-transfer.sketch :refer [fps render-sketch build-state]]
    [hohmann-transfer.kepler-orbits :refer [controls]]
    [hohmann-transfer.circular-orbits]))

;; available sketches:
;; :kepler-orbits
;; :circular-orbits
(def sketch :kepler-orbits)

;; define your app data so that it doesn't get over-written on reload
(defonce app-state (atom (build-state sketch)))

(defn get-app-element []
  (gdom/getElement "app"))


(defn app []
  [:div.dt.mw6.center.pt0.pb5.pv5-m.pv6-ns
   [:div.db.dtc-ns.v-mid-ns
    #_[:img.w-100.mw7.w5-ns {:src "http://tachyons.io/img/super-wide.jpg" :alt "Something else"}]
    [:div {:id "sketch"}]]
   (if (= sketch :kepler-orbits)
     (controls app-state))])

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


