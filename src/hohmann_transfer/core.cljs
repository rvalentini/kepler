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

(render-sketch sketch) ;;TODO maybe give react-state wrapped as setup function to render-sketch for interactive use
                       ;;TODO each sketch could offer a 'construct state function' or something similar also as multimethod


