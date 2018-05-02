(ns hybrid-aco.aco
  (:use [hybrid-aco.instance])
  (:require [clojure.core.matrix :as matrix]
            [hybrid-aco.parameters :as params]
            [hybrid-aco.solution :as solution]))

(matrix/set-current-implementation :vectorz)

(defn- build-probabilities-matrix
  "Build the probability to select each customer"
  [heuristics pheromones]
  (matrix/emul (matrix/pow pheromones *alpha*)
               (matrix/pow heuristics *beta*)))

(defn- build-customers-probabilities
  "Build the probability to select the next customer to the ant from customer i"
  [customers i probs]
  (let [cust-probs (matrix/emap #(matrix/mget probs i %) customers)
        total (matrix/esum cust-probs)]
    (map #(/ % total) cust-probs)))

(defn- build-ant-wheel
  "Calculate the accumulate probability to the ant wheel"
  [customers probabilities]
  (map #(hash-map :customer %1 :probability %2)
       customers
       (conj (vec (butlast (reductions + probabilities))) 1.0))) ; Ensuring that the last accumulate probability will always be 1.0 can slow down the function but avoid problems caused by rounding

(defn- select-next-customer
  "Select the next customer to be added to the ant tour"
  [last-customer customers probs]
  (let [cust-probs (build-customers-probabilities customers last-customer probs)
        wheel (build-ant-wheel customers cust-probs)
        r (rand)] ; Generates a random number between the interval [0, 1.0)
    (some #(cond (> (:probability %) r) (:customer %)) wheel)))

(defn- construct-ant-tour
  "Construct the tour of an ant"
  [tour acc-dem customers probs]
  (if (empty? customers)
    (conj tour 0) ; If there is no more customer to add, return the vehicle to the depot and exit the function
    (let [next-cust (select-next-customer (last tour) customers probs)
          next-cust-dem (matrix/mget *demands* next-cust)
          total-dem (+ acc-dem next-cust-dem)
          return-to-depot (> total-dem *capacity*) ; If the total demand with the selected customer overloads the vehicle capacity, the vehicle should return to the depot first
          new-tour (if return-to-depot (conj tour 0 next-cust) (conj tour next-cust))
          new-acc-dem (if return-to-depot next-cust-dem total-dem)]
      (recur new-tour
             new-acc-dem
             (remove #(= % next-cust) customers)
             probs))))

(defn- construct-ant-solution
  "Construct the solution based of the probability to select each customer"
  [probabilities]
  (->> probabilities
       (construct-ant-tour [0] 0 (range 1 *dimension*))
       (solution/create-solution)))

(defn construct-solutions
  "Construct the solution for each ant of the colony"
  [heuristics pheromones]
  (println "Running ACO...")
  (let [probs (build-probabilities-matrix heuristics pheromones)]
    (map construct-ant-solution (repeat *num-ants* probs))))


