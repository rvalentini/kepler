(ns ^:figwheel-hooks hohmann-transfer.core
  (:require
    [goog.dom :as gdom]
    [quil.core :as q :include-macros true]
    [reagent.core :refer [atom]]
    [reagent.dom :as rdom]
    [hohmann-transfer.sketch :refer [fps render-sketch build-state]]
    [hohmann-transfer.kepler_3rd_law :refer [controls]]
    [hohmann-transfer.circular-orbits]
    [hohmann-transfer.kepler-2nd-law]
    [hohmann-transfer.kepler-1st-law]))

(defonce first-law-state (atom (build-state :kepler-1st-law)))
(defonce second-law-state (atom (build-state :kepler-2nd-law)))
(defonce third-law-state (atom (build-state :kepler-3rd-law)))

(defn layout []
  [:article
   [:header.bg-gold.sans-serif
    [:div.mw9.center.pa4.pt5-ns.ph7-l
     [:time.f6.mb2.dib.ttu.tracked [:small "01 June, 2021"]]
     [:h3.f2.f1-m.f-headline-l.measure-narrow.lh-title.mv0
      [:span.bg-black-90.lh-copy.white.pa1.tracked-tight "Kepler laws of planetary motion"]]
     [:h4.f3.fw1.georgia.i "with some nice animations"]
     [:h5.f6.ttu.tracked.black-80 "By Riccardo Valentini"]]]
   [:div.pa4.ph7-l.georgia.mw9-l.center
    [:p.f5.f3-ns.lh-copy.measure.georgia "Why does this page exist and what can you expect?"]
    [:p.f5.f3-ns.lh-copy.measure.georgia "First maybe a little history lesson"]
    [:p.f5.f3-ns.lh-copy.measure.georgia "First law:"]
    [:p.f6.f2-ns.lh-copy.measure.i.pl4.bl.bw1.b--gold.mb4 "The orbit of a planet is an ellipse with the Sun at one of the two foci.\n"]
    [:p
     [:div {:id "first-law"}]]
    [:p.f5.f3-ns.lh-copy.measure.georgia "Second law:"]
    [:p.f6.f2-ns.lh-copy.measure.i.pl4.bl.bw1.b--gold.mb4 "A line segment joining a planet and the Sun sweeps out equal areas during equal intervals of time."]
    [:p
     [:div {:id "second-law"}]]
    [:p.f5.f3-ns.lh-copy.measure.georgia "Third law:"]
    [:p.f6.f2-ns.lh-copy.measure.i.pl4.bl.bw1.b--gold.mb4 "The square of a planet's orbital period is proportional to the cube of the length of the semi-major axis of its orbit."]
    [:p
     [:div.dt.mw6.pb5.pv1-m.pv1-ns
      [:div.db.dtc-ns.v-mid-ns
       [:div {:id "third-law"}]]
      (controls third-law-state)]]
    [:p.f5.f4-ns.lh-copy.measure.mb4 "You don't have to bounce a tennis ball very far in San Francisco before it will hit two developers complaining about how many js tools/frameworks there are for development in 2015 and how much unneccessary complexity they add. Doing a search on twitter for 'too many js tools' or 'yet another js framework' returns... a lot of people lamenting the current state of affairs."]
    [:p.f5.f4-ns.lh-copy.measure "This is most likely, the wrong conversation for us as a community, to be having. The presence of bad tools - shouldn't discourage us from wanting more tools or frameworks. There are more books published in a single day than I will ever be able to read in my lifetime. But this does not make me sad. Or overwhelm me. Mostly I think about how excited I am to read the best books that are being published. And this is where we should push the conversation. How do we build better tools? What does that look like?"]]])

(defn mount-app-element []
  (when-let [el (gdom/getElement "app")]
    (rdom/render [layout] el)))

(mount-app-element)

;; specify reload hook with ^;after-load metadata
(defn ^:after-load on-reload []
  (mount-app-element))

(render-sketch :kepler-2nd-law (fn []
                        (q/frame-rate fps)
                        second-law-state) "second-law")
(render-sketch :kepler-3rd-law (fn []
                        (q/frame-rate fps)
                        third-law-state) "third-law")
(render-sketch :kepler-1st-law (fn []
                                (q/frame-rate fps)
                                first-law-state) "first-law")


