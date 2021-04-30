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
;;  :kepler-orbits
;;  :circular-orbits
(def sketch :kepler-orbits)

(defonce app-state (atom (build-state sketch)))

(defn app []
  [:div.dt.mw6.center.pt0.pb5.pv5-m.pv6-ns
   [:div.db.dtc-ns.v-mid-ns
    [:div {:id "sketch"}]]
   (if (= sketch :kepler-orbits)
     (controls app-state))])

(defn mount-app-element []
  (when-let [el (gdom/getElement "app")]
    (rdom/render [app] el)))

(mount-app-element)

;; specify reload hook with ^;after-load metadata
(defn ^:after-load on-reload []
  (mount-app-element))

(render-sketch sketch (fn []
                        (q/frame-rate fps)
                        app-state))


