(ns hybrid-aco.sa
  (:use [hybrid-aco.instance])
  (:require [hybrid-aco.local-search :as search]
            [hybrid-aco.solution :as solution]
            [hybrid-aco.pheromones :as pheromones]
            [clojure.core.matrix :as matrix]
            [amalloy.ring-buffer :as ring]))

(def operations [search/make-swap-move
                 search/make-2opt-move
                 search/make-insertion-move])

(defn simulated-annealing
  "Perfom Simulated Annealing metaheuristic to search in solution neighborhood"
  [current-solution best-so-far pheromones tabu-list tries-cnt current-temp]
  ;(println)
  ;(println "CURRENT-SOLUTION:" current-solution)
  ;(println "BEST-SO-FAR:" best-so-far)
  ;(println "TABU-LIST:" tabu-list)
  ;(println "TRIES-CNT:" tries-cnt)
  ;(println "CURRENT-TEMP:" current-temp)
  ;(Thread/sleep 1000)
  ; If current temperature reached below final temperature
  (if (or (<= current-temp *final-temperature*)
          (termination-criteria-reached? best-so-far))
    ; return the best-so-far solution and the pheromones updated
    [best-so-far pheromones]
    ; otherwise perfom a search in the neighborhood of current-solution
    (let [new-solution (search/search-neighborhood current-solution operations)
          max-tries-reached? (= tries-cnt *sa-max-tries*)]
      ;(println "NEW-SOLUTION:" new-solution)
      ; If new-solution is not feasible or tabu-list contains new-solution
      (if (or (not (solution/feasible? new-solution))
              (.contains tabu-list new-solution))
        ; update tries-cnt and current-temperature if necessary, then recur to try again
        (recur current-solution
               best-so-far
               pheromones
               tabu-list
               (if max-tries-reached? 0 (inc tries-cnt))
               (if max-tries-reached? (* *lambda* current-temp) current-temp))
        ; otherwise recur processing the solution found and updating other parameters
        (let [delta (- (:cost new-solution) (:cost current-solution))
              accept-as-current? (or (< delta 0)
                                     (< (rand) (Math/pow Math/E
                                                         (- (/ delta current-temp)))))
              accept-as-best-so-far? (< (:cost new-solution) (:cost best-so-far))]
          ;(println "IMPROVED CURRENT?" accept-as-current?)
          ;(println "IMPROVED BEST?" accept-as-best-so-far?)
          ;(cond accept-as-best-so-far? (println new-solution))
          (recur
           (if accept-as-current?
             new-solution ; Accepts the new solution found
             current-solution) ; Keep the same current solution
           (if accept-as-best-so-far?
             new-solution ; Update the new solution found as best-so-far
             best-so-far) ; Keep the same current best-so-far solution
           (if accept-as-best-so-far?
             (matrix/add! pheromones
                          (pheromones/release-ant-pheromone new-solution 12))
             pheromones)
           (into tabu-list '(new-solution)) ; Add new-solution to tabu-list
           ; If maximum number of tries in the same temperature is reached, reset tries-cnt to zero and decrease temperature level:
           (if max-tries-reached? 0 (inc tries-cnt))
           (if max-tries-reached? (* *lambda* current-temp) current-temp)))))))

(defn improve-solution
  "Improve the current solution"
  [solution pheromones]
  ;(println "Running SA...")
  (simulated-annealing solution ; Current solution
                       solution ; Best-so-far solution
                       pheromones ; Current pheromone trail
                       (ring/ring-buffer *tabu-size*) ; Tabu list
                       0 ; Iteration counter
                       *initial-temperature*)) ; Set current-temp as initial-temp
