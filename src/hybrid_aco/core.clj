(ns hybrid-aco.core
  (:use [hybrid-aco.instance])
  (:require [clojure.core.matrix :as matrix]
            [hybrid-aco.solution :as solution]
            [hybrid-aco.saco :as saco])
  (:gen-class))

(matrix/set-current-implementation :vectorz)

(def inst (read-file "/home/lucas/Documents/tcc/hybrid-aco/resources/A-n32-k5.in"))
(def params (read-file "/home/lucas/Documents/tcc/hybrid-aco/resources/hybrid_aco_parameters.json"))

(def initial-solution (struct solution/Solution [] Integer/MAX_VALUE))

(defn run-on-instance
  "Run the SACO metaheuristic on the given instance and generate a report file"
  [instance params]
  (binding [*dimension* (:dimension instance)
            *capacity* (:capacity instance)
            *distances* (matrix/matrix (:distances instance))
            *demands* (matrix/matrix (:demands instance))
            *optimum* (:optimal-value (:comment instance))
            *num-ants* (:dimension instance)
            *alpha* (:alpha params)
            *beta* (:beta params)
            *evaporation-rate* (- 1 (:evaporation-rate params))
            *pertubation-rate* (:pertubation-rate params)
            *num-elite-ants* (:num-elite-ants params)
            *initial-trail* (:initial-trail params)
            *initial-temperature* (:initial-temperature params)
            *final-temperature* (/ (:initial-temperature params) 50)
            *sa-max-tries* (max (* 4 (:dimension instance) 250))
            *lambda* (:lambda params)
            *tabu-size* (:tabu-size params)
            *max-iter* (:max-iter params)
            *max-best-unchanged* (:dimension instance)
            *max-best-so-far-not-improved* (:max-best-so-far-not-improved params)]
    (saco/solve-instance initial-solution)))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  ())

;(def d (matrix/matrix (:distances instance/inst)))
;(def dem (matrix/matrix (:demands instance/inst)))
;(def cap (:capacity instance/inst))
;(def h (construct-heuristics-matrix (:distances instance/inst)))
;(def p (pheromones/construct-pheromones-matrix))
;(def ants (aco/construct-solutions instance/inst instance/params h p))

;(def best-ants (take 5 (sort-by :cost ants)))
;(def p2 (pheromones/release-pheromones p (rest best-ants) (first best-ants) 0.9))
;(def p3 (pheromones/pertubate-pheromones p2 0.7))

