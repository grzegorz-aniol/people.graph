Neo4j
RESULT PersonTimer   avg=130,437 ms, min=41,471 ms, max=306,532 ms, 
RESULT RelationTimer avg= 40,747 ms, min= 0,455 ms, max=136,625 ms, 

OrientDB with index for Person.name
RESULT PersonTimer avg=1,336 ms, min=1,132 ms, max=8,293 ms, 
RESULT RelationTimer avg=90,948 ms, min=78,615 ms, max=127,330 ms,

OrientDB with index for Person.name, index on Wiki.in and .out
RESULT PersonTimer avg=1,283 ms, min=1,058 ms, max=3,654 ms, 
RESULT RelationTimer avg=1,781 ms, min=0,773 ms, max=5,032 ms, 

