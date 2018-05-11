#!/bin/bash

IFS='-' read -r -a to_docker <<< "$1"
YELLOW='\033[0;33m'
NC='\033[0m'

docker stop $(docker ps -aqf "name=spatial_")
docker rm $(docker ps -aqf "name=spatial_")

for element in "${to_docker[@]}"; do
   if [ "$element" == "b" ] || [ "$element" == "all"  ]; then
      echo -e ${YELLOW}Docker build: spatial_benchmark-controller${NC}
      docker build -t spatial_benchmark-controller -f spatialbenchmarkcontroller.docker .
   fi
   if [ "$element" == "d"  ] || [ "$element" == "all"  ]; then
      echo -e ${YELLOW}Docker build: spatial_data-generator${NC}
      docker build -t spatial_data-generator -f spatialdatagenerator.docker .
   fi
   if [ "$element" == "t"  ] || [ "$element" == "all"  ]; then
      echo -e ${YELLOW}Docker build: spatial_task-generator${NC}
      docker build -t spatial_task-generator -f spatialtaskgenerator.docker .
   fi
   if [ "$element" == "e"  ] || [ "$element" == "all"  ]; then
      echo -e ${YELLOW}Docker build: spatial_evaluation-module${NC}
      docker build -t spatial_evaluation-module -f spatialevaluationmodule.docker .
   fi
   if [ "$element" == "s1"  ] || [ "$element" == "all"  ]; then
      echo -e ${YELLOW}Docker build: spatial_limes-system${NC}
      docker build -t spatial_limes-system -f limessystemadapter.docker .
   fi
   if [ "$element" == "s2"  ] || [ "$element" == "all"  ]; then
      echo -e ${YELLOW}Docker build: spatial_silk-system${NC}
      docker build -t spatial_silk-system -f silksystemadapter.docker .
   fi
   
done
