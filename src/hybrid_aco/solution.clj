(ns hybrid-aco.solution
  (:use [hybrid-aco.instance])
  (:require [clojure.core.matrix :as matrix]))

(matrix/set-current-implementation :vectorz)

(defstruct Solution :tour :cost)

(defn split-tour
  "Split the tour into routes"
  [tour]
  (->> tour
       (partition-by zero?)
       (remove #(= % '(0)))
       (map vec)))

(defn join-routes
  "Join each route into a tour"
  [routes]
  (->> routes
       (map vec)
       (map #(conj % 0))
       (cons [0])
       flatten
       vec))

(defn construct-edge-matrix
  "Construct the edge matrix of the solution"
  [solution]
  (if (contains? solution :edges)
    solution
    (let [tour (:tour solution)
          size (count (distinct tour))
          edges (map vector tour (rest tour))]
      (matrix/set-indices (matrix/new-matrix size size)
                          edges
                          (repeat (count edges) 1.0)))))

(defn objetive-function
  "Calculate the total distance of the tour i.e. the cost of the solution"
  [tour]
  (let [dists (map #(matrix/mget *distances* %1 %2) tour (rest tour))]
    (matrix/esum dists)))

(defn calculate-load
  "Calculate total load of the tour for the vehicle"
  [tour]
  (matrix/esum (matrix/emap #(matrix/mget *demands* %) tour)))

(defn feasible?
  "Check if the solution is feasible"
  [solution]
  (not-any? #(> (calculate-load %) *capacity*)
            (split-tour (:tour solution))))

(defn create-solution
  ""
  [tour]
  (struct Solution tour (objetive-function tour)))

