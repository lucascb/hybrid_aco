#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import glob
import json

# Alg 2
table = []
alg1 = []
alg2 = []
for filename in glob.glob("/home/lucas/Documents/tcc/cvrplib_solver/out/*.out"):
    with open(filename, "r") as f:
        instance_file = json.load(f)
        alg1.append(instance_file)

for filename in glob.glob("/home/lucas/Documents/tcc/out/*.out"):
    with open(filename, "r") as f:
        instance_file = json.load(f)
        alg2.append(instance_file)

for instance1 in alg1:
    for instance2 in alg2:
        if instance1["instance"] == instance2["instance"]:
            dev2 = ((instance2["total-cost"] - instance2["optimal"]) / instance2["optimal"]) * 100.0
            if instance1["dev"] >= 0 and dev2 >= 0:
                table_row = " & ".join([
                    instance1["instance"],
                    str(instance1["optimum"]),
                    str(instance1["solution"]["cost"]),
                    str("{:.1f}".format(instance1["dev"])),
                    str("{:.1f}".format(instance1["elapsed-time"])),
                    str(instance2["total-cost"]),
                    str("{:.1f}".format(dev2)),
                    str("{:.1f}".format(instance2["elapsed-time"]))
                ]) + " \\\\"
                table.append(table_row)

#print(table)
table.sort()
with open("table_1.tex", "w") as f:
    f.write("\n".join(table))
