(ns hybrid-aco.pheromones
  (:use [hybrid-aco.instance])
  (:require [clojure.core.matrix :as matrix]
            [hybrid-aco.solution :as solution]))

(defn construct-pheromones-matrix
  "Construct a new pheromones matrix"
  []
  (matrix/fill (matrix/new-matrix *dimension* *dimension*) *initial-trail*))

(defn- evaporate-pheromones
  "Decrease pheromone on all edges"
  [pheromones]
  (matrix/emul pheromones *evaporation-rate*))

(defn- pheromone-amount
  "Calculate the pheromone amount of an ant on an edge"
  [ant weight]
  (/ weight (:cost ant)))

(defn release-ant-pheromone
  ""
  [ant weight]
  (let [edges (solution/construct-edge-matrix ant)
        deposit-amount (pheromone-amount ant weight)]
    (matrix/emul edges deposit-amount)))

(defn update-pheromones
  ""
  [pheromones elite-ants best-so-far-ant]
  ;(println "Updating pheromones...")
  (let [elites-weight (range *num-elite-ants* 0 -1)
        elites-pheromones (map #(release-ant-pheromone %1 %2)
                               elite-ants
                               elites-weight)
        best-so-far-pheromones (release-ant-pheromone best-so-far-ant 12)]
    (matrix/add (evaporate-pheromones pheromones)
                (apply matrix/add elites-pheromones)
                best-so-far-pheromones)))

(defn pertubate-pheromones
  "Apply the pertubation on the pheromones to avoid solution stagnation"
  [pheromones]
  ;(println "Pertubating pheromones...")
  (let [non-pertubation-ratio (- 1 *pertubation-ratio*)
        average (/ (matrix/esum pheromones) (matrix/ecount pheromones))
        pertubation (* *pertubation-ratio* average)]
    (matrix/emap #(+ pertubation (* non-pertubation-ratio %)) pheromones)))


