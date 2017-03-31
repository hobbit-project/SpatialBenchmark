#!/bin/bash

while true; do
echo "$(date) =====================================================================================" >> spatial_benchmark-controller.log
echo "$(date) =====================================================================================" >> spatial_data-generators.log
echo "$(date) =====================================================================================" >> spatial_task-generators.log
echo "$(date) =====================================================================================" >> spatial_evaluation-module.log
echo "$(date) =====================================================================================" >> spatial_limes-system.log



data_gens=( $(docker ps -aqf "name=spatial_data-generator") )
task_gens=( $(docker ps -aqf "name=spatial_task-generator") )
bench=( $(docker ps -aqf "name=spatial_benchmark-controller") )
eval_mod=( $(docker ps -aqf "name=spatial_evaluation-module") )
system=( $(docker ps -aqf "name=spatial_limes-system") )
virt_gs=( $(docker ps -aqf "name=tenforce") )

for data_gen in "${data_gens[@]}"; do
   docker logs $data_gen  >> spatial_data-generators.log
   echo "-------------------------------------------------------------------------------------" >> spatial_data-generators.log
   echo Data generators logs saved.
done

for task_gen in "${task_gens[@]}"; do
   docker logs $task_gen  >> spatial_task-generators.log
   echo "-------------------------------------------------------------------------------------" >> spatial_task-generators.log
   echo Task generators logs saved.
done

for ben in "${bench[@]}"; do
   docker logs $ben  >> spatial_benchmark-controller.log
   echo "-------------------------------------------------------------------------------------" >> spatial_benchmark-controller.log
   echo Benchmark controller logs saved.
done


for ev_mod in "${eval_mod[@]}"; do
   docker logs $ev_mod  >> spatial_evaluation-module.log
   echo "-------------------------------------------------------------------------------------" >> spatial_evaluation-module.log
   echo Evaluation module logs saved.
done

for sys in "${system[@]}"; do
   docker logs $sys  >> spatial_limes-system.log
   echo "-------------------------------------------------------------------------------------" >> spatial_limes-system.log
   echo Test system logs saved.
done

sleep 1
done
