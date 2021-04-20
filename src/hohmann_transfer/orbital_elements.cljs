(ns hohmann-transfer.orbital-elements)

;; This namespace contains utility functions for Kepler orbital elements computations

;; G =  6.674×10−11 m^3⋅kg^−1⋅s^−2
(def gravitational-const 6.674E-11)

(defn- compute-mean-anomaly [t a mass]
  "This function computes the mean anomaly, which is the fraction of the orbit represented as angle that the
  body already passed, measured from the periapsis. This is not the angle of the elliptic Kepler orbit.
  It is a projection onto a circular orbit with constant angle speed.
  The mean anomaly can be computed through: M = n * t
  where t is the time since t_pericenter and
  where t-pericenter is the point in time when the body is closest to the center of mass (= periapsis) and
  where n = 2 pi / T  (n can be seen as 'radian per unit of time')
  where T is the time the body needs to complete a full orbit and
  my = gravitational parameter  (my = GM)"
  (let [my (* gravitational-const mass)
        n (Math/sqrt (/ my (Math/pow a 3)))]
    #_(println (str "N: " n " and n*t: " (* n t)))
    (* n t)))

(defn- newton-raphson [curr mean-anomaly e]
  (- curr (/
            (+ curr (- (* e (Math/sin curr))) (- mean-anomaly))
            (- 1 (* e (Math/cos curr))))))

(defn- compute-eccentric-anomaly [mean-anomaly e]
  (loop [iter 0
         ecc-anomaly Math/PI]
    #_(println (str "ECC-anomaly: " ecc-anomaly))
    #_(println (str "iter: " iter))
    (if (>= iter 100)
      ecc-anomaly
      (recur (inc iter) (newton-raphson ecc-anomaly mean-anomaly e)))))

(defn- compute-true-anomaly [e ecc-anomaly]
  (let [ta (* 2 (Math/atan (* (Math/sqrt (/ (+ 1 e) (- 1 e)))
                          (Math/tan (/ ecc-anomaly 2)))))]
    #_(println (str "ecc-anomaly: " ecc-anomaly))
    #_(println "True anomaly: " ta)
    ta))

(defn- compute-radius [theta e a]
  (/
    (* a (- 1 (Math/pow e 2)))
    (+ 1 (* e (Math/cos theta)))))

(defn- compute-position [theta small-omega big-omega i r]
  (let [x (* r (-
                 (* (Math/cos big-omega) (Math/cos (+ small-omega theta)))
                 (* (Math/sin big-omega) (Math/sin (+ small-omega theta)) (Math/cos i))))
        y (* r (+
                 (* (Math/sin big-omega) (Math/cos (+ small-omega theta)))
                 (* (Math/cos big-omega) (Math/sin (+ small-omega theta)) (Math/cos i))))
        z (* r (* (Math/sin i) (Math/sin (+ small-omega theta))))]
    [x y z]))

(defn orbital-elements->position
  "This function converts from orbital elements and a point in time `t` to the cartesian position of the body
   on the elliptical Kepler orbit at the point in time `t`.

  Orbital elements:
  `a` = semimajor axis, (periapsis dist + apoapsis dist) / 2
  `e` = eccentricity of the elliptic orbit
  `i` = inclination - inlcination of the orbital plane compared to the reference direction
  `small-omega` = argument of the periapsis - angle that defines the 'rotation/orientation' of the ellipsis inside the orbital plane
                This can be seen as the angle between the line of nodes and the periapsis of the ellipsis (rotation around the perpendicular of the ellipsis)
  `big-omega` = longitude of the ascending node - angle between reference direction and the ascending node
              This can be seen as the rotation around the perpendicular of the reference plane
  `theta` = true anomaly - angle that defines the position of orbiting body at a specific time,
          The angle is measured from the direction of the ascending node within the orbital plane

  `t` = point in time since t-pericenter (body passed the periapsis of the orbit)
  `mass` = mass of the central body where mass >> mass of the orbiting body
  Note: when the inclination is zero ('2D case' where ellipsis lies within the reference plane)
  "
  [t mass a e i small-omega big-omega]
  (let [mean-anomaly (compute-mean-anomaly t a mass)
        ecc-anomaly (compute-eccentric-anomaly mean-anomaly e)
        theta (compute-true-anomaly e ecc-anomaly)
        radius (compute-radius theta e a)]
    (compute-position theta small-omega big-omega i radius)))


