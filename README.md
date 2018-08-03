# Hybrid-ACO

This repository contains the implementation written in Clojure of the method **Hybrid Ant Colony Optimization** proposed by [Sun et al. (2017)](http://dx.doi.org/10.1109/IAEAC.2017.8054067) to solve Capacitated Vehicle Routing (CVRP) instances. 
It is the product of the [Term Paper (in Portuguese)](https://gist.github.com/lucascb/f9fef4e3e70606592b236361dedeba24) of my Computer Science undergraduation in Federal University of Uberlândia.
The algorithm accepts instance files in JSON format and produces a JSON result file with the solution found. You can check examples of CVRP instances on _resources_ directory in this repository or check [CVRPlib-Parser](https://github.com/lucascb/cvrplib_parser) repository.

## Requirements

Java Runtime Environment 1.8+ is needed to run the JAR file. You may need Clojure 1.8+ and [Leiningen](https://leiningen.org) if you want to work in this code.

## Usage

You can run this algorithm using command line, passing the parameters file and the instance to be solved:

```$ java -jar hybrid_aco.jar hybrid_aco_parameters.json A-n32-k5.json```

Passing a directory will run the algorithm on all instances in it:

```$ java -jar hybrid_aco.jar hybrid_aco_parameters.json resources/```

The results can be found in the out/ directory.

## License

This project offers no warranty. You can clone it, download it or make your own version of this algorithm.

