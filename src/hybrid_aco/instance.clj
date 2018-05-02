(ns hybrid-aco.instance
  (:require [cheshire.core :as json]
            [clojure.core.matrix :as matrix]))

;; Instance constants

(def ^:dynamic *dimension*)
(def ^:dynamic *distances*)
(def ^:dynamic *capacity*)
(def ^:dynamic *demands*)
(def ^:dynamic *optimum*)

;; Algorithm parameters

;; ACO
(def ^:dynamic *num-ants*)
(def ^:dynamic *alpha*)
(def ^:dynamic *beta*)
(def ^:dynamic *evaporation-rate*)
(def ^:dynamic *pertubation-rate*)
(def ^:dynamic *num-elite-ants*)
(def ^:dynamic *initial-trail*)

;; Simulated Annealing
(def ^:dynamic *initial-temperature*)
(def ^:dynamic *final-temperature*)
(def ^:dynamic *sa-max-tries*)
(def ^:dynamic *lambda*)
(def ^:dynamic *tabu-size*)

;; SACO
(def ^:dynamic *max-iter*)
(def ^:dynamic *max-best-unchanged*)
(def ^:dynamic *max-best-so-far-not-improved*)

(defn- to-keyword
  ""
  [string]
  (keyword (clojure.string/replace string "_" "-")))

(defn read-file
  "Read and parse the specified JSON file"
  [filename]
  (json/parse-string (slurp filename) to-keyword))

;(def inst (read-instance (slurp "/home/lucas/Documents/tcc/hybrid-aco/resources/A-n32-k5.in")))

;(def params (json/parse-string (slurp "/home/lucas/Documents/tcc/hybrid-aco/resources/hybrid_aco_parameters.json") to-keyword))

