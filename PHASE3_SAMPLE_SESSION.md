# Phase 3 Sample Console Session

Run command:

```bash
java -cp src Main
```

Sample input used:

```text
db3
0.30
0.70
```

Trimmed output:

```text
================ APRIORI MIDTERM PROJECT ================
How to run:
  1) Compile: javac src/*.java
  2) Run:     java -cp src Main
  3) Input:   database name (db1..db5), min support, min confidence
Example input values:
  db1
  0.20
  0.60
==========================================================

Choose database file (db1..db5): Enter minimum support (0.0 to 1.0): Enter minimum confidence (0.0 to 1.0): 
=== Run Configuration ===
Database file: db3.txt
Minimum support: 0.300
Minimum confidence: 0.700
Transaction count: 20
Item dictionary count: 30

=== Input Transactions ===
T1  {soap, shampoo, toothpaste, paper_towels}
T2  {soap, detergent, paper_towels}
T3  {soap, shampoo, detergent}
T4  {soap, shampoo, toothpaste}
T5  {soap, detergent, paper_towels}
T6  {diapers, baby_wipes, paper_towels}
T7  {soap, diapers, baby_wipes}
T8  {detergent, diapers, baby_wipes}
T9  {soap, shampoo, detergent, paper_towels}
T10 {soap, toothpaste, paper_towels}
T11 {soap, shampoo, paper_towels}
T12 {soap, toothpaste, detergent}
T13 {soap, diapers, baby_wipes, paper_towels}
T14 {soap, toothpaste, detergent, paper_towels}
T15 {soap, shampoo, toothpaste}
T16 {soap, detergent, paper_towels}
T17 {detergent, diapers, baby_wipes, paper_towels}
T18 {soap, shampoo, detergent, paper_towels}
T19 {soap, shampoo, toothpaste, paper_towels}
T20 {diapers, baby_wipes, paper_towels}

=== Apriori Intermediate Steps ===

=== Apriori Intermediate Steps ===
Minimum support count threshold = 6 out of 20

L1:
  {soap}                                   count=16 support=0.800
  {shampoo}                                count= 8 support=0.400
  {toothpaste}                             count= 7 support=0.350
  {detergent}                              count=10 support=0.500
  {diapers}                                count= 6 support=0.300
  {baby_wipes}                             count= 6 support=0.300
  {paper_towels}                           count=14 support=0.700

C2:
  {soap, shampoo}                          count= 8 support=0.400
  {soap, toothpaste}                       count= 7 support=0.350
  {soap, detergent}                        count= 8 support=0.400
  {soap, diapers}                          count= 2 support=0.100

=== Apriori Qualified Association Rules ===
  R1  {baby_wipes} -> {diapers}                               supportCount= 6 support=0.300 confidence=1.000
  R2  {detergent, paper_towels} -> {soap}                     supportCount= 6 support=0.300 confidence=0.857
  R3  {detergent} -> {paper_towels}                           supportCount= 7 support=0.350 confidence=0.700
  R4  {detergent} -> {soap}                                   supportCount= 8 support=0.400 confidence=0.800
  R5  {diapers} -> {baby_wipes}                               supportCount= 6 support=0.300 confidence=1.000
  R6  {paper_towels} -> {soap}                                supportCount=11 support=0.550 confidence=0.786
  R7  {shampoo} -> {soap}                                     supportCount= 8 support=0.400 confidence=1.000
  R8  {soap, detergent} -> {paper_towels}                     supportCount= 6 support=0.300 confidence=0.750
  R9  {toothpaste} -> {soap}                                  supportCount= 7 support=0.350 confidence=1.000


=== Brute Force Intermediate Summary ===
Minimum support count threshold = 6 out of 20
  k=1 -> candidates=7 frequent=7
  k=2 -> candidates=21 frequent=6
  k=3 -> candidates=35 frequent=1
  k=4 -> candidates=35 frequent=0


=== Brute Force Qualified Association Rules ===
  R1  {baby_wipes} -> {diapers}                               supportCount= 6 support=0.300 confidence=1.000
  R2  {detergent, paper_towels} -> {soap}                     supportCount= 6 support=0.300 confidence=0.857
  R3  {detergent} -> {paper_towels}                           supportCount= 7 support=0.350 confidence=0.700
  R4  {detergent} -> {soap}                                   supportCount= 8 support=0.400 confidence=0.800
  R5  {diapers} -> {baby_wipes}                               supportCount= 6 support=0.300 confidence=1.000
  R6  {paper_towels} -> {soap}                                supportCount=11 support=0.550 confidence=0.786
  R7  {shampoo} -> {soap}                                     supportCount= 8 support=0.400 confidence=1.000
  R8  {soap, detergent} -> {paper_towels}                     supportCount= 6 support=0.300 confidence=0.750
  R9  {toothpaste} -> {soap}                                  supportCount= 7 support=0.350 confidence=1.000


=== Timing (wall-clock) ===
Apriori:     38.192 ms
Brute force: 3.751 ms


=== Rule Set Comparison ===
PASS: Apriori and brute force produced identical qualified rule sets.
Apriori rule count: 9
Brute force rule count: 9
```
