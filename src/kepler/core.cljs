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
  [:a.f5.f4-ns.fw6.lh-copy.underline-hover.mid-gray.link.dim {:href link} txt])

(def colors {:yellow "rgb(255 183 0)"
             :blue   "rgb(0 181 203)"
             :purple "rgb(224 101 255)"
             :red    "rgb(236 88 88)"})

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
       [:time.f6.mb2.dib.ttu.tracked [:small "10 July, 2021"]]
       [:h3.f2.f1-m.f-headline-l.measure-narrow.lh-title.mv0
        [:span.bg-black-90.lh-copy.white.pa1.ph2.tracked-tight.smooth-linebreak "Kepler laws of planetary motion"]]
       [:h4.f3-l.f5.fw1.georgia.i "explained with some nice animations"]
       [:h5.f6-l.f7.ttu.tracked.black-80 "By Riccardo Valentini"]]]
     [:div.pa4.ph7-l.georgia.mw9-l.center
      [:p.f4.f3-ns.lh-copy.measure.georgia "Hello stranger,"]
      [:p.f5.f4-ns.lh-copy.mb4 "welcome to my website about Johannes Kepler and his discoveries about how our universe works. I'm first and foremost a computer scientist and not a physicist, which means what you see here  is mostly the result of reading Wikipedia articles and watching online lectures about astrophysics.   \nI have a certain curiosity for astrophysics however and tried to make the animations on this site as physically accurate as possible. Hopefully, you will find this informative, and please excuse any physical inaccuracies you might discover, or better, tell me and I will try to fix them :)"]
      [:p.f4.f3-ns.lh-copy.measure.georgia "So why Kepler?"]
      [:p.f5.f4-ns.lh-copy.mb4 kepler " (1571 - 1630), a German astronomer and mathematician, made some considerable contributions in shaping the way we see and understand our universe today. Based on the idea of " heliocentrism " (the sun as the center of the universe instead of the earth, which caught on in Europe in the 16th century through " copernicus " (1473 - 1543) ), Kepler discovered a new way to explain the variations in speed and direction of motion of the planets that he observed. Until then, according to Copernicus' astronomical model, all planets were moving in circles around the sun, whereas those variations in speed and direction of motion were explained by epicycles, which is a small circle whose center moves around the circumference of a larger one. Kepler improved Copernicus' theory by using " elliptical-orbit " instead of circular orbits to explain the motion of the planets in our solar system. This new understanding of planetary motion developed by Kepler also proved to be essential for " newton "'s (1642 - 1726/27) discovery of " gravitation " half a century later. Based on his theory of gravitation, Newton derived the Kepler Laws of Planetary motion mathematically, adding a \"why\" to the \"how\" of planetary motion described by Kepler.   "]
      [:p.f4.f3-ns.lh-copy.measure.georgia "First law:"]
      [:p.f4.f2-ns.lh-copy.measure.i.pl4.bl.bw1.b--gold.mb4 "The orbit of a planet is an ellipse with the Sun at one of the two foci.\n"]
      [:div
       [:div.mw6.mw6-ns.center.pt1 {:id "first-law"}]]
      [:p.f5.f4-ns.lh-copy.mb4  "First of all, what's an ellipse? The shape is defined through the constant sum of distances between any point on the curve and the two focal points (in the animation marked in " (colorize "yellow" :yellow) " and grey). At least for me, that's not very intuitively accessible. Another way to look at it: it's the shape that you get when you cut through a cone. Depending on how you cut, you might end up with a circle, an ellipse, a parabola, or a hyperbola (have a look " parabola ", there you will find a very nice diagram illustrating this). There is now a parameter called " eccentricity ", which basically tells you how \"conic\" the shape is, on the scale from circle to hyperbola. If the eccentricity of the shape is between 0 and 1, the result is called an ellipse, which means it's a closed orbit on which a body can turn its rounds. When the eccentricity is 1 or even greater than 1, the shape forms an escape orbit, which means it is no longer a closed orbit and a body on such an orbit would simply move further and further away from the center of mass (the term \"center of mass\" is used here to refer to the heavier of the two bodies, a more detailed explanation comes in the next section). " [:br][:br] "The center of mass in this animation would be the \"sun\", the focal point of the ellipse marked in " (colorize "yellow" :yellow) ", and the orbiting body marked in " (colorize "blue" :blue)  " turns its rounds on a closed elliptical orbit around it. Note that the speed of the orbiting body (illustrated through the " (colorize "purple arrow" :purple) ") varies throughout the orbit. " [:br][:br] "Please also note that this animation already shows a special case of a " two-body-problem " called \"central-force-problem\", which has to do with the masses of the two bodies. As later explained by Newton's law of universal gravitation, any particle in the universe attracts any other particle in the universe, whereas the force of this attraction depends on the masses and distances of the particles. This animation shows a simplification, where one mass is significantly larger than the other mass (e.g. the difference between the masses of sun vs. earth), so we act here as if the larger of the two masses exerts an attraction on the smaller one, but not the other way round. We basically neglect the gravitational impact that the smaller mass has on the larger one, which is reasonable, as long as the difference in mass is significant enough. This also makes it a lot easier to calculate and draw some nice animation of it :)"]
      [:p.f4.f3-ns.lh-copy.measure.georgia "Second law:"]
      [:p.f4.f2-ns.lh-copy.measure.i.pl4.bl.bw1.b--gold.mb4 "A line segment joining a planet and the Sun sweeps out equal areas during equal intervals of time."]
      [:div
       [:div.mw6.mw6-ns.center.pt1 {:id "second-law"}]]
      [:p.f5.f4-ns.lh-copy.mb4 "The animation for the second law tries to illustrate what \"equal areas during equal intervals of time\" means. On first glance, it looks like there are " (colorize "two orbiting bodies" :blue) " now, but this should represent the same body at two different points in time " (colorize "t1" :blue) " and " (colorize "t2" :blue) ". The temporal distance between these two points always stays the same, but the velocities of the bodies change one their way around the orbit, and so their physical distance varies. If you connect the two positions at times " (colorize "t1" :blue) " and " (colorize "t2" :blue) " with the center of mass, they form what " (colorize "looks like a triangle" :purple) " with one curved side. Kepler's second law now states that the area of this \"triangle\" stays constant at all times." [:br][:br] "Looking at it from the other side: the closer the body is to the center of mass, the less area is enclosed between the two, which means the angle that this body must cover within the fixed time period is larger, which means the velocity of the body must be higher in order to cover the angle. For calculating the actual velocity of an orbiting body the " (linkify "Vis-Viva equation" "https://en.wikipedia.org/wiki/Vis-viva_equation") " can be used, where the term \"vis-viva\" is Latin for \"living force\" and was coined by " (linkify "Gottfried Leibniz" "https://en.wikipedia.org/wiki/Gottfried_Wilhelm_Leibniz") " (1676–1689) to describe what we call today kinetic energy." ]
      [:p.f4.f3-ns.lh-copy.measure.georgia "Third law:"]
      [:p.f4.f2-ns.lh-copy.measure.i.pl4.bl.bw1.b--gold.mb4 "The square of a planet's orbital period is proportional to the cube of the length of the semi-major axis of its orbit."]
      [:div.mw7.center.pt1
       [:div.dt.mw6.pb1.pv1-m.pv1-ns
        [:div.db.dtc-ns.v-mid-ns
         [:div {:id "third-law"}]]
        (controls third-law-state)]]
      [:p.f5.f4-ns.lh-copy.mb4 "Unfortunately, the third law is not as straightforward to explain as the first two laws, so I thought it might help to go a little interactive here. For the third animation, you can play around with the Kepler orbits yourself and change the individual parameters that define the shape of the orbit. Through that, you hopefully get a feeling for how changes in these parameters affect the shape of the orbit, as well as the movement of the orbiting body." [:br][:br] "The 3rd law describes the relationship between two of those parameters: the " (linkify "orbital period" "https://en.wikipedia.org/wiki/Orbital_period") " (= the time it takes the orbiting body to complete a full turn) and the " (linkify "semi-major axis" "https://en.wikipedia.org/wiki/Semi-major_and_semi-minor_axes") " (= half of the distance between the two furthest points of the ellipse). It's hard to get a feeling for this proportion, however, since it's not linear but involves the square and cube of these parameters. Nevertheless, this seems to be a surprisingly simple relationship between the two." [:br][:br] "You can use the sliders to change the semi-major axis, as well as the eccentricity and angle of the orbit. Additionally, you can increase the mass of the central body, which will also increase the attraction it exerts on the orbiting body. Some \"combinations\" of slider positions are blocked (you might notice the slider "(colorize "turning red" :red) "), which is only for visual aesthetics, meaning this combination of parameters would give the animation an odd configuration. "]
      [:p.f4.f3-ns.lh-copy.measure.georgia "Sayonara!"]
      [:p.f5.f4-ns.lh-copy.mb4 "Have fun with the animations and I hope this site gave you an interesting takeaway or two about Kepler orbits. " [:br][:br] "In case you are interested in how I created these animations: I wrote everything in " (linkify "ClojureScript" "https://clojurescript.org/") " using the awesome drawing and animations library " (linkify "Quil" "http://quil.info/?example=fireworks") ". Here is my " (linkify "Github repository" "https://github.com/rvalentini/kepler")" with the source. In case you liked this site, please leave a star :)"]]]))

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


