echo "I am at run.sh"

echo "Running postgres.. "
enable_remote.sh && su - postgres -c "/usr/lib/postgresql/9.4/bin/postgres -D /var/lib/postgresql/9.4/main -c config_file=/etc/postgresql/9.4/main/postgresql.conf" &

 
echo "Running java -cp for system adapter..  "
java -cp SpatialBenchmark.jar org.hobbit.core.run.ComponentStarter org.hobbit.spatialbenchmark.platformConnection.systems.StrabonSystemAdapter

