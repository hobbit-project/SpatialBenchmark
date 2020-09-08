docker run -it --rm --network="hobbit-core" -v `pwd`:/usr/src/app -e "HOBBIT_RABBIT_HOST=rabbit" -e "BENCHMARK=http://w3id.org/bench#Spatial" -e "SYSTEM=http://w3id.org/system#limesV1" -e "BENCHMARK_PARAM_FILE=src/test/resources/experiment.ttl" -e "USERNAME=jsaveta1" maven bash