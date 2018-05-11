echo "Running store.sh.."
#!/bin/bash
HOST=$1
PORT=$2
DB=$3
USER=$4
PASSWORD=$5
FILE=$6
FORMAT=$7
GRAPH=$8

java -cp $(for file in `ls -1 *.jar`; do myVar=$myVar./$file":";done;echo $myVar;) eu.earthobservatory.runtime.postgis.StoreOp $HOST $PORT $DB $USER $PASSWORD $FILE -f $FORMAT -g $GRAPH
