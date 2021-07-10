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

(def colors {:yellow "rgb(255 183 0)"
             :blue   "rgb(0 181 203)"
             :purple "rgb(224 101 255)"})

(defn colorize [txt color]
  [:span {:style {:color (color colors)}} txt])

(defn layout []
  (let [kepler (linkify "Johannes Kepler" "https://en.wikipedia.org/wiki/Johannes_Kepler")
        heliocentrism (linkify "heliocentrism" "https://en.wikipedia.org/wiki/Copernican_heliocentrism" )
        copernicus (linkify "Nicolaus Copernicus" "https://en.wikipedia.org/wiki/Nicolaus_Copernicus")
        elliptical-orbit (linkify "elliptical" "https://en.wikipedia.org/wiki/Elliptic_orbit")
        newton (linkify "Isaac Newton" "https://en.wikipedia.org/wiki/Isaac_Newton")
        gravitation (linkify "universal gravitation" "https://en.wikipedia.org/wiki/Gravity")
        eccentricity (linkify "eccentricity" "https://en.wikipedia.org/wiki/Orbital_eccentricity")
        parabola (linkify "here" "https://en.wikipedia.org/wiki/Parabola")
        two-body-problem (linkify "Two-body problem" "https://en.wikipedia.org/wiki/Two-body_jproblem")]
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
      [:p.f5.f4-ns.lh-copy.mb4 "welcome to my website about Johannes Kepler and his discoveries about how our universe works. I'm first and foremost a computer scientist and not a physicist, which means what you see here  is mostly the result of reading Wikipedia articles and watching online lectures about astrophysics.   \nI have a certain curiosity for astrophysics however and tried to make the animations on this site as physically accurate as possible. Hopefully you will find this informative and please excuse any physical inaccuracies you might discover, or better, tell me and I will try to fix them :)"]
      [:p.f5.f3-ns.lh-copy.measure.georgia "So why Kepler?"]
      [:p.f5.f4-ns.lh-copy.mb4 kepler " (1571 - 1630), a German astronomer and mathematician, made some considerable contributions in shaping the way we see and understand our universe today. Based on the idea of " heliocentrism " (the sun as the center of the universe instead of the earth, which caught on in Europe in the 16th century through " copernicus " (1473 - 1543) ), Kepler discovered a new way to explain the variations in speed and direction of motion of the planets that he observed. Until then, according to Copernicus' astronomical model, all planets were moving in circles around the sun, whereas those variations in speed and direction of motion were explained by epicycles, which is a small circle whose centre moves round the circumference of a larger one. Kepler improved Copernicus' theory by using " elliptical-orbit " instead of circular orbits to explain the motion of the planets in our solar system. This new understanding of planetary motion developed by Kepler also proved to be essential for " newton "'s (1642 - 1726/27) discovery of " gravitation " half a century later. Based on his theory of gravitation, Newton derived the Kepler Laws of Planetary motion mathematically, adding a \"why\" to the \"how\" of planetary motion described by Kepler.   "]
      [:p.f5.f3-ns.lh-copy.measure.georgia "First law:"]
      [:p.f6.f2-ns.lh-copy.measure.i.pl4.bl.bw1.b--gold.mb4 "The orbit of a planet is an ellipse with the Sun at one of the two foci.\n"]
      [:p
       [:div {:id "first-law"}]]
      [:p.f5.f4-ns.lh-copy.mb4  "First of all what's an ellipse? The shape is defined through the constant sum of distances between any point on the curve and the two focal points (in the animation marked in " (colorize "yellow" :yellow) " and grey). At least for me, that's not very intuitively accessible. Another way to look at it: it's the shape that you get when you cut through a cone. Depending on how you cut, you might end up with a circle, an ellipse, a parabola or a hyperbola (have a look " parabola ", there you will find a very nice diagram illustrating this). There is now a parameter called " eccentricity ", which basically tells you how \"conic\" the shape is, on the scale from circle to hyperbola. If the eccentricity of the shape is between 0 and 1, the result is called an ellipse, which means it's a closed orbit on which a body can turn its rounds. When the eccentricity is 1 or even greater than 1, the shape forms an escape orbit, which means it is no longer a closed orbit and a body on such an orbit would simply move further and further away from the center of mass. " [:br][:br] "The center of mass in this animation would be the \"sun\", the focal point of the ellipse marked in " (colorize "yellow" :yellow) ", and the orbiting body marked in " (colorize "blue" :blue)  " turns its rounds on a closed elliptical orbit around it. Note that the speed of the orbiting body (illustrated through the " (colorize "purple arrow" :purple) ") varies throughout the orbit. " [:br][:br] "Please also note that this animation, already shows a special case of a " two-body-problem " called \"central-force-problem\", which has to do with the masses of the two bodys. As later explained by Newton's law of universal gravitation, any partical in the universe attracts any other partical in the universe, whereas the force of this attraction depends on the masses and distances of the particles. This animation shows a simplification, where one mass is significantly larger than the other mass (e.g. the difference between the masses of sun vs. earth), so we act here as if the larger of the two masses exerts attraction on the smaller one, but not the other way round. We basically neglect the gravitational impact that the smaller mass has on the larger one, which is reasonable, as long as the difference in mass is significant enough. This also makes it a lot easier to calculate and draw some nice animation of it :)"]
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


