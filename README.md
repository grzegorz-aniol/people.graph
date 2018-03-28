People.graph - builds a graph database of people connected via data available on the web 
=====

The application crawles Wiki pages, analyses the content 
using simple NLP methods and generates data in graph database. 

Currently, the application analyze only some Polish Wiki pages - this is the only source of data so far. 
Application supports two database outputs: Neo4j and OrientDB. 

The goals of the project:
1. Build graph database of people connected via information available on the network.
2. Learn and analyze data using available graph algorithms.
3. Compare the performance between different graph databases. 

Here are some performance results achieved during importing data:

** Neo4j 3.3.3 **

|----------------------------------------------------------------------|
|Creating persons   | avg=130,437 ms | min=41,471 ms | max=306,532 ms  |
|Creating relations | avg= 40,747 ms | min= 0,455 ms | max=136,625 ms  |

** OrientDB 3.0 RC2 **

OrientDB with index for Person.name

|----------------------------------------------------------------------|
| Creating persons   | avg=1,336 ms  | min=1,132 ms  | max=8,293 ms    |
| Creating relations | avg=90,948 ms | min=78,615 ms | max=127,330 ms  | 

OrientDB with index for Person.name, index on Wiki.in and .out

|----------------------------------------------------------------------|
| Creating persons   | avg=1,283 ms | min=1,058 ms | max=3,654 ms      |
| Creating relations | avg=1,781 ms | min=0,773 ms | max=5,032 ms      |


**Machine specification:**
Windows 10, CPU: AMD FX-6300 6 cores 3.5GHz, RAM 8 GB

