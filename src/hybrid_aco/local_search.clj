(ns hybrid-aco.local-search
  (:use [hybrid-aco.instance])
  (:require [hybrid-aco.solution :as solution]
            [hybrid-aco.utils :as utils]))

(defn make-swap-move
  "Selects two customers randomly and swaps their position"
  [tour]
  (let [customers (range 1 *dimension*)
        customer1 (rand-nth customers)
        customer2 (rand-nth (remove #{customer1} customers))]
    (replace {customer1 customer2, customer2 customer1} tour)))

(defn- make-inversion
  "Selects two customers in a route randomly and inverts all customer between them"
  [route]
  ;(println route)
  (if (< (count route) 2)
    route
    (let [route-length (count route)
          n (rand-int (dec route-length))
          m (rand-int (- route-length n 1))
          subroute1 (take n route)
          subroute2 (drop n (drop-last m route))
          subroute3 (take-last m route)]
      ;(println subroute1 (reverse subroute2) subroute3)
      (vec (concat subroute1 (reverse subroute2) subroute3)))))

(defn make-2opt-move
  "Select randomly a route in the tour to apply inversion move"
  [tour]
  (let [routes (solution/split-tour tour)]
    (solution/join-routes (utils/rand-map 1 make-inversion routes))))

(defn make-insertion-move
  "Select a customer randomly and insert it in a random position"
  [tour]
  (let [customers (range 1 *dimension*)
        customer (rand-nth customers)
        ;_ (println customer)
        new-tour (remove #{customer} tour)
        position (rand-nth (range 1 (dec (count new-tour))))]
    (vec (concat (take position new-tour) [customer] (drop position new-tour)))))

(defn search-neighborhood
  "Select randomly one of the operations to make a move on the neighborhood"
  [solution operations]
  (let [make-move (rand-nth operations)]
    ;(println "Chose to perform" make-move)
    (solution/create-solution (make-move (:tour solution)))))

(defn local-search
  "Perform a local search on the current solution"
  [solution operations]
  (let [new-sol (search-neighborhood solution operations)]
    (if (>= (:cost new-sol) (:cost solution))
      solution
      (recur new-sol operations))))

