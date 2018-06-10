#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import glob
import json

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
            dev1 = instance1["dev"]
            dev2 = ((instance2["total-cost"] - instance2["optimal"]) / instance2["optimal"]) * 100.0
            if instance1["dev"] >= 0 and dev2 >= 0:
                b1 = "\\textbf{{{:.1f}}}".format(dev1) if dev1 <= dev2 else "{:.1f}".format(dev1)
                b2 = "\\textbf{{{:.1f}}}".format(dev2) if dev2 <= dev1 else "{:.1f}".format(dev2)
                d1 = "\\underline{{{}}}".format(b1) if dev1 == 0.0 else b1
                d2 = "\\underline{{{}}}".format(b2) if dev2 == 0.0 else b2
                table_row = " & ".join([
                    instance1["instance"],
                    str(instance1["optimum"]),
                    str(instance1["solution"]["cost"]),
                    d1,
                    str("{:.1f}".format(instance1["elapsed-time"])),
                    str(instance2["total-cost"]),
                    d2,
                    str("{:.1f}".format(instance2["elapsed-time"]))
                ]) + " \\\\"
                table.append(table_row)

table.sort()
with open("tabela_resultados.tex", "w") as f:
    f.write("\n".join(table))
