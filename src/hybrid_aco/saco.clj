(ns hybrid-aco.saco
  (:use [hybrid-aco.instance])
  (:require [hybrid-aco.pheromones :as pheromones]
            [hybrid-aco.aco :as aco]
            [hybrid-aco.local-search :as search]
            [hybrid-aco.sa :as sa]
            [clojure.core.matrix :as matrix]))

(def operations [search/make-2opt-move search/make-swap-move])
(def heuristic (fn [dist] (/ 1.0 (+ dist 0.1))))

(defn- construct-heuristics-matrix
  "Construct the heuristic matrix"
  []
  (matrix/emap heuristic *distances*))

(defn hybrid-aco
  ""
  [best-so-far last-best iter cnt-best cnt-best-so-far heuristics pheromones]
  ;(Thread/sleep 1000)
  ;(println)
  ;(println "ITER =" iter)
  ;(println best-so-far)
  (if (or (= (:cost best-so-far) *optimum*)
          (= iter *max-iter*))
    best-so-far
    (let [ants1 (aco/construct-solutions heuristics pheromones)
          ants2 (map #(search/local-search % operations) ants1)

          ants-ranked (sort-by :cost ants2)
          best-ant (first ants-ranked)
          elite-ants (take *num-elite-ants* ants-ranked)
          best-ant-changed? (not= best-ant last-best)
          best-so-far1 (if (< (:cost best-ant) (:cost best-so-far))
                            best-ant
                            best-so-far)
          best-so-far-improved? (not= best-so-far1 best-so-far)

          pheromones1 (pheromones/update-pheromones pheromones
                                                    elite-ants
                                                    best-so-far1)

          pertubate-pheromones? (= cnt-best *max-best-unchanged*)
          pheromones2 (if pertubate-pheromones?
                        (pheromones/pertubate-pheromones pheromones1)
                        pheromones1)

          improve-with-sa? (= cnt-best-so-far *max-best-so-far-not-improved*)
          time1 (System/nanoTime)
          [best-so-far2 pheromones3] (if improve-with-sa?
                                       (sa/improve-solution best-so-far1 pheromones2)
                                       [best-so-far1 pheromones2])
          time2 (System/nanoTime)]
      ;(cond improve-with-sa? (println "SA took" (/ (- time2 time1) 1e9) "seconds"))
      (recur best-so-far2
             best-ant
             (inc iter)
             (cond best-ant-changed? 0
                   pertubate-pheromones? (- cnt-best 2)
                   :else (inc cnt-best))
             (cond best-so-far-improved? 0
                   improve-with-sa? 0
                   :else (inc cnt-best-so-far))
             heuristics
             pheromones3))))

(defn solve-instance
  "Use SACO metaheuristic to find a solution closest to optimum"
  [best-so-far]
  (let [heuristics (construct-heuristics-matrix)
        pheromones (pheromones/construct-pheromones-matrix)]
    (hybrid-aco best-so-far best-so-far 0 0 0 heuristics pheromones)))
