package org.hobbit.spatialbenchmark.main;

import java.io.IOException;
import org.hobbit.spatialbenchmark.data.Generator;
import static org.hobbit.spatialbenchmark.data.Generator.configurations;
import static org.hobbit.spatialbenchmark.data.Generator.definitions;
import org.hobbit.spatialbenchmark.properties.Configurations;

public class Main {

    private static Generator generateData;

    public static void main(String[] args) throws IOException {
        generateData = new Generator();
        generateData.loadPropertiesFile();
        //        if( args.length < 1) {
//                throw new IllegalArgumentException("Missing parameter - the configuration file must be specified");
//        }
//        configurations.loadFromFile(args[0]);

        definitions.loadFromFile(configurations.getString(Configurations.DEFINITIONS_PATH));
        definitions.initializeAllocations(Generator.randomGenerator);
        generateData.exec();
    }

}
