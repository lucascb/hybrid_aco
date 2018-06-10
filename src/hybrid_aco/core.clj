(ns hybrid-aco.core
  (:use [hybrid-aco.instance])
  (:require [clojure.core.matrix :as matrix]
            [hybrid-aco.solution :as solution]
            [hybrid-aco.saco :as saco]
            [random-seed.core :as random]
            [taoensso.timbre :as log])
            ;[clojure.tools.cli :refer [parse-opts]]
            ;[clojure.test :as test]
  (:gen-class))

;; Default configs
(matrix/set-current-implementation :vectorz)
(log/merge-config! {:appenders {:spit (log/spit-appender {:fname "solver.log"})}})
                                ;:println {:enabled? false}}})
(log/set-level! :info)

#_(def a-n32-k5 "/home/lucas/Documents/tcc/cvrplib_solver/resources/set/set_A/A-n32-k5.in")
#_(def cmt2 "/home/lucas/Documents/tcc/run/CMT2.json")

(def ^:private param-file "resources/hybrid_aco_parameters.json")
(def ^:private initial-solution (struct solution/Solution [] Integer/MAX_VALUE))

(defn- run-with-parameters-on-instance
  "Run the algorithm with the specified parameters"
  [instance params]
  (log/info "Started instance" (:name instance))
  (binding [*instance* (:name instance)
            *dimension* (:dimension instance)
            *capacity* (:capacity instance)
            *distances* (matrix/matrix (:distances instance))
            *demands* (matrix/matrix (map second (:demand-section instance)))
            *optimal* (:cost (:optimal instance) (:best-known instance 0))
            *num-ants* (:dimension instance)
            *seed* (:seed params)
            *alpha* (:alpha params)
            *beta* (:beta params)
            *evaporation-rate* (- 1 (:evaporation-rate params))
            *pertubation-ratio* (:pertubation-ratio params)
            *num-elite-ants* (:num-elite-ants params)
            *initial-trail* (:initial-trail params)
            *initial-temperature* (:initial-temperature params)
            *final-temperature* (/ (:initial-temperature params) 50)
            *sa-max-tries* (max (* 4 (:dimension instance)) 250)
            *lambda* (:lambda params)
            *tabu-size* (:tabu-size params)
            *max-iter* (:max-iter params)
            *max-best-unchanged* (:dimension instance)
            *max-best-so-far-not-improved* (:max-best-so-far-not-improved params)
            *max-runtime* (:max-runtime params)
            *evaluations* (atom 0.0)
            *date-start* (java.util.Date.)
            *start* (System/nanoTime)]
    (random/set-random-seed! *seed*)
    (write-result! (saco/solve-instance initial-solution))))

(defn- run
  "Run the SACO metaheuristic on the given instance and generate a report file"
  [instance params]
  (try (run-with-parameters-on-instance instance params)
       (catch Exception e (log/error "ERROR ON INSTANCE"
                                     (:name instance)
                                     (.toString e)))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (let [params (read-file param-file)
        instances (read-instances (first args))]
    (doseq [instance instances]
      (run instance params))))

;(def d (matrix/matrix (:distances instance/inst)))
;(def dem (matrix/matrix (:demands instance/inst)))
;(def cap (:capacity instance/inst))
;(def h (construct-heuristics-matrix (:distances instance/inst)))
;(def p (pheromones/construct-pheromones-matrix))
;(def ants (aco/construct-solutions instance/inst instance/params h p))

;(def best-ants (take 5 (sort-by :cost ants)))
;(def p2 (pheromones/release-pheromones p (rest best-ants) (first best-ants) 0.9))
;(def p3 (pheromones/pertubate-pheromones p2 0.7))


