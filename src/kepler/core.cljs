(ns ^:figwheel-hooks kepler.core
  (:require
    [goog.dom :as gdom]
    [quil.core :as q :include-macros true]
    [reagent.core :refer [atom]]
    [reagent.dom :as rdom]
    [kepler.sketch :refer [fps render-sketch build-state]]
    [kepler.kepler_3rd_law :refer [controls]]
    [kepler.circular-orbits]
    [kepler.kepler-2nd-law]
    [kepler.kepler-1st-law]))

(defonce first-law-state (atom (build-state :kepler-1st-law)))
(defonce second-law-state (atom (build-state :kepler-2nd-law)))
(defonce third-law-state (atom (build-state :kepler-3rd-law)))

(defn linkify [txt link]
  [:a.f5.f4-ns.fw6.lh-copy.gold.link.dim {:href link} txt])

(defn layout []
  (let [kepler (linkify "Johannes Kepler" "https://en.wikipedia.org/wiki/Johannes_Kepler")
        heliocentrism (linkify "heliocentrism" "https://en.wikipedia.org/wiki/Copernican_heliocentrism" )
        copernicus (linkify "Nicolaus Copernicus" "https://en.wikipedia.org/wiki/Nicolaus_Copernicus")
        elliptical-orbit (linkify "elliptical" "https://en.wikipedia.org/wiki/Elliptic_orbit")
        newton (linkify "Isaac Newton" "https://en.wikipedia.org/wiki/Isaac_Newton")
        gravitation (linkify "universal gravitation" "https://en.wikipedia.org/wiki/Gravity")]
    [:article
     [:header.bg-gold.sans-serif
      [:div.mw9.center.pa4.pt5-ns.ph7-l
       [:time.f6.mb2.dib.ttu.tracked [:small "01 June, 2021"]]
       [:h3.f2.f1-m.f-headline-l.measure-narrow.lh-title.mv0
        [:span.bg-black-90.lh-copy.white.pa1.tracked-tight "Kepler laws of planetary motion"]]
       [:h4.f3.fw1.georgia.i "explained with some nice animations"]
       #_[:h5.f6.ttu.tracked.black-80 "By Riccardo Valentini"]]]
     [:div.pa4.ph7-l.georgia.mw9-l.center
      [:p.f5.f3-ns.lh-copy.measure.georgia "Hello stranger,"]
      [:p.f5.f4-ns.lh-copy.mb4 "welcome to my website about Johannes Kepler and his discoveries about how our universe works. I'm first and foremost a computer scientist and not a physicist, which means what you see here is mostly the result of reading Wikipedia articles and watching online lectures about astrophysics.   \nI have a certain curiosity for astrophysics however and tried to make the animations on this site as physically accurate as possible. Hopefully you will find this informative and please excuse any physical inaccuracies you might discover, or better, tell me and I will try to fix them :)"]
      [:p.f5.f3-ns.lh-copy.measure.georgia "So why Kepler?"]

      [:p.f5.f4-ns.lh-copy.mb4 kepler " (1571 - 1630), a German astronomer and mathematician, made some considerable contributions in shaping the way we see and understand our universe today. Based on the idea of " heliocentrism " (the sun as the center of the universe instead of the earth, which caught on in Europe in the 16th century through " copernicus " (1473 - 1543) ), Kepler discovered a new way to explain the variations in speed and direction of motion of the planets that he observed. Until then, according to Copernicus' astronomical model, all planets were moving in circles around the sun, whereas those variations in speed and direction of motion were explained by epicycles. Kepler improved Copernicus' theory by using " elliptical-orbit " instead of circular orbits to explain the motion of the planets in our solar system. This new understanding of planetary motion developed by Kepler also proved to be essential for " newton "'s (1642 - 1726/27) discovery of " gravitation " half a century later. Based on his theory of gravitation, Newton derived the Kepler Laws of Planetary motion mathematically, adding a \"why\" to the \"how\" of planetary motion described by Kepler.   "]
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
      #_[:p.f5.f4-ns.lh-copy.measure.mb4 "You don't have to bounce a tennis ball very far in San Francisco before it will hit two developers complaining about how many js tools/frameworks there are for development in 2015 and how much unneccessary complexity they add. Doing a search on twitter for 'too many js tools' or 'yet another js framework' returns... a lot of people lamenting the current state of affairs."]
      #_[:p.f5.f4-ns.lh-copy.measure "This is most likely, the wrong conversation for us as a community, to be having. The presence of bad tools - shouldn't discourage us from wanting more tools or frameworks. There are more books published in a single day than I will ever be able to read in my lifetime. But this does not make me sad. Or overwhelm me. Mostly I think about how excited I am to read the best books that are being published. And this is where we should push the conversation. How do we build better tools? What does that look like?"]
      [:p.f5.f3-ns.lh-copy.measure.georgia "FAQs"]

      ]
     ])

  )

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


