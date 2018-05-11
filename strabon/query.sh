echo "Running query.sh.."
#!/bin/bash
HOST=$1
PORT=$2
DB=$3
USER=$4
PASSWORD=$5
GRAPH1=$6
GRAPH2=$7
RELATION=$8

if("$RELATION" == "EQUALS")
then
QUERY="PREFIX geof: <http://www.opengis.net/def/function/geosparql/> 
PREFIX owl: <http://www.w3.org/2002/07/owl#> 
PREFIX tomtom: <http://www.tomtom.com/ontologies/traces#> 
PREFIX tomtomregions: <http://www.tomtom.com/ontologies/regions#> 
PREFIX spaten: <http://www.spaten.com/ontologies/traces#> 
PREFIX spatenregions: <http://www.spaten.com/ontologies/regions#> 
PREFIX strdf: <http://strdf.di.uoa.gr/ontology#> 
SELECT ?s1 ?s2
WHERE {GRAPH <$GRAPH1> {?s1 <http://strdf.di.uoa.gr/ontology#hasGeometry> ?o1}
GRAPH <$GRAPH2> {?s2 <http://strdf.di.uoa.gr/ontology#hasGeometry> ?o2}
FILTER(geof:sfEquals(?o1, ?o2)). 
}";

elif("$RELATION" == "DISJOINT")
then
QUERY="PREFIX geof: <http://www.opengis.net/def/function/geosparql/> 
PREFIX owl: <http://www.w3.org/2002/07/owl#> 
PREFIX tomtom: <http://www.tomtom.com/ontologies/traces#> 
PREFIX tomtomregions: <http://www.tomtom.com/ontologies/regions#> 
PREFIX spaten: <http://www.spaten.com/ontologies/traces#> 
PREFIX spatenregions: <http://www.spaten.com/ontologies/regions#> 
PREFIX strdf: <http://strdf.di.uoa.gr/ontology#>
SELECT ?s1 ?s2
WHERE { 
	GRAPH <$GRAPH1> {?s1 <http://strdf.di.uoa.gr/ontology#hasGeometry> ?o1}
	GRAPH <$GRAPH2> {?s2 <http://strdf.di.uoa.gr/ontology#hasGeometry> ?o2} 
	FILTER(geof:sfDisjoint(?o1, ?o2)).  
}";

elif("$RELATION" == "TOUCHES")
then
QUERY="PREFIX geof: <http://www.opengis.net/def/function/geosparql/> 
PREFIX owl: <http://www.w3.org/2002/07/owl#> 
PREFIX tomtom: <http://www.tomtom.com/ontologies/traces#> 
PREFIX tomtomregions: <http://www.tomtom.com/ontologies/regions#> 
PREFIX spaten: <http://www.spaten.com/ontologies/traces#> 
PREFIX spatenregions: <http://www.spaten.com/ontologies/regions#> 
PREFIX strdf: <http://strdf.di.uoa.gr/ontology#> 
SELECT ?s1 ?s2
WHERE { 
	GRAPH <$GRAPH1> {?s1 <http://strdf.di.uoa.gr/ontology#hasGeometry> ?o1}
	GRAPH <$GRAPH2> {?s2 <http://strdf.di.uoa.gr/ontology#hasGeometry> ?o2}
	FILTER(?s1 != ?s2).  
	FILTER(geof:sfTouches(?o1, ?o2)).  
}";

elif("$RELATION" == "CONTAINS")
then
QUERY="PREFIX geof: <http://www.opengis.net/def/function/geosparql/> 
PREFIX owl: <http://www.w3.org/2002/07/owl#> 
PREFIX tomtom: <http://www.tomtom.com/ontologies/traces#> 
PREFIX tomtomregions: <http://www.tomtom.com/ontologies/regions#> 
PREFIX spaten: <http://www.spaten.com/ontologies/traces#> 
PREFIX spatenregions: <http://www.spaten.com/ontologies/regions#> 
PREFIX strdf: <http://strdf.di.uoa.gr/ontology#>
SELECT ?s1 ?s2
WHERE { 
	GRAPH  <$GRAPH1> {?s1 <http://strdf.di.uoa.gr/ontology#hasGeometry> ?o1}
	GRAPH  <$GRAPH2> {?s2 <http://strdf.di.uoa.gr/ontology#hasGeometry> ?o2}
	FILTER(geof:sfContains(?o1, ?o2)).  
}";

elif("$RELATION" == "COVERS")
then
QUERY="PREFIX geof: <http://www.opengis.net/def/function/geosparql/> 
PREFIX owl: <http://www.w3.org/2002/07/owl#> 
PREFIX tomtom: <http://www.tomtom.com/ontologies/traces#> 
PREFIX tomtomregions: <http://www.tomtom.com/ontologies/regions#> 
PREFIX spaten: <http://www.spaten.com/ontologies/traces#> 
PREFIX spatenregions: <http://www.spaten.com/ontologies/regions#> 
PREFIX strdf: <http://strdf.di.uoa.gr/ontology#> 
SELECT ?s1 ?s2
WHERE { 
	GRAPH <$GRAPH1> {?s1 <http://strdf.di.uoa.gr/ontology#hasGeometry> ?o1}
	GRAPH <$GRAPH2> {?s2 <http://strdf.di.uoa.gr/ontology#hasGeometry> ?o2}
	FILTER(geof:ehCovers(?o1, ?o2)).  
}";

elif("$RELATION" == "INTERSECTS")
then
QUERY="PREFIX geof: <http://www.opengis.net/def/function/geosparql/> 
PREFIX owl: <http://www.w3.org/2002/07/owl#> 
PREFIX tomtom: <http://www.tomtom.com/ontologies/traces#> 
PREFIX tomtomregions: <http://www.tomtom.com/ontologies/regions#> 
PREFIX spaten: <http://www.spaten.com/ontologies/traces#> 
PREFIX spatenregions: <http://www.spaten.com/ontologies/regions#> 
PREFIX strdf: <http://strdf.di.uoa.gr/ontology#> 
SELECT ?s1 ?s2
WHERE { 
	GRAPH <$GRAPH1> {?s1 <http://strdf.di.uoa.gr/ontology#hasGeometry> ?o1}
	GRAPH <$GRAPH2> {?s2 <http://strdf.di.uoa.gr/ontology#hasGeometry> ?o2}
	FILTER(geof:sfIntersects(?o1, ?o2)).  
}";

elif("$RELATION" == "WITHIN")
then
QUERY="PREFIX geof: <http://www.opengis.net/def/function/geosparql/> 
PREFIX owl: <http://www.w3.org/2002/07/owl#> 
PREFIX tomtom: <http://www.tomtom.com/ontologies/traces#> 
PREFIX tomtomregions: <http://www.tomtom.com/ontologies/regions#> 
PREFIX spaten: <http://www.spaten.com/ontologies/traces#> 
PREFIX spatenregions: <http://www.spaten.com/ontologies/regions#> 
PREFIX strdf: <http://strdf.di.uoa.gr/ontology#>
SELECT ?s1 ?s2
WHERE { 
	GRAPH  <$GRAPH1> {?s1 <http://strdf.di.uoa.gr/ontology#hasGeometry> ?o1}
	GRAPH  <$GRAPH2> {?s2 <http://strdf.di.uoa.gr/ontology#hasGeometry> ?o2}
	FILTER(geof:sfWithin(?o1, ?o2)).  
}";

elif("$RELATION" == "COVERED_BY")
then
QUERY="PREFIX geof: <http://www.opengis.net/def/function/geosparql/> 
PREFIX owl: <http://www.w3.org/2002/07/owl#> 
PREFIX tomtom: <http://www.tomtom.com/ontologies/traces#> 
PREFIX tomtomregions: <http://www.tomtom.com/ontologies/regions#> 
PREFIX spaten: <http://www.spaten.com/ontologies/traces#> 
PREFIX spatenregions: <http://www.spaten.com/ontologies/regions#> 
PREFIX strdf: <http://strdf.di.uoa.gr/ontology#> 
SELECT ?s1 ?s2
WHERE { 
	GRAPH <$GRAPH1> {?s1 <http://strdf.di.uoa.gr/ontology#hasGeometry> ?o1}
	GRAPH <$GRAPH2> {?s2 <http://strdf.di.uoa.gr/ontology#hasGeometry> ?o2}
	FILTER(geof:ehCoveredBy(?o1, ?o2)).  
}";

elif("$RELATION" == "CROSSES")
then
QUERY="PREFIX geof: <http://www.opengis.net/def/function/geosparql/> 
PREFIX owl: <http://www.w3.org/2002/07/owl#> 
PREFIX tomtom: <http://www.tomtom.com/ontologies/traces#> 
PREFIX tomtomregions: <http://www.tomtom.com/ontologies/regions#> 
PREFIX spaten: <http://www.spaten.com/ontologies/traces#> 
PREFIX spatenregions: <http://www.spaten.com/ontologies/regions#> 
PREFIX strdf: <http://strdf.di.uoa.gr/ontology#>
SELECT ?s1 ?s2
WHERE { 
	GRAPH <$GRAPH1> {?s1 <http://strdf.di.uoa.gr/ontology#hasGeometry> ?o1}
	GRAPH <$GRAPH2> {?s2 <http://strdf.di.uoa.gr/ontology#hasGeometry> ?o2}
	FILTER(geof:sfCrosses(?o1, ?o2)).  
}";

elif("$RELATION" == "OVERLAPS")
then
QUERY="PREFIX geof: <http://www.opengis.net/def/function/geosparql/> 
PREFIX owl: <http://www.w3.org/2002/07/owl#> 
PREFIX tomtom: <http://www.tomtom.com/ontologies/traces#> 
PREFIX tomtomregions: <http://www.tomtom.com/ontologies/regions#> 
PREFIX spaten: <http://www.spaten.com/ontologies/traces#> 
PREFIX spatenregions: <http://www.spaten.com/ontologies/regions#> 
PREFIX strdf: <http://strdf.di.uoa.gr/ontology#>
SELECT ?s1 ?s2
WHERE { 
	GRAPH <$GRAPH1> {?s1 <http://strdf.di.uoa.gr/ontology#hasGeometry> ?o1}
	GRAPH <$GRAPH2> {?s2 <http://strdf.di.uoa.gr/ontology#hasGeometry> ?o2} 
	FILTER(geof:sfOverlaps(?o1, ?o2)).  
}";

fi


echo $QUERY

java -cp $(for file in `ls -1 *.jar`; do myVar=$myVar./$file":";done;echo $myVar;) eu.earthobservatory.runtime.postgis.QueryOp $HOST $PORT $DB $USER $PASSWORD "$QUERY" false 



