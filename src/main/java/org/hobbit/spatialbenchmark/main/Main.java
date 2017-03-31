package org.hobbit.spatialbenchmark.main;

import java.io.IOException;
import java.util.Random;
import org.hobbit.spatialbenchmark.data.Generator;
import org.hobbit.spatialbenchmark.properties.Configurations;
import org.hobbit.spatialbenchmark.properties.Definitions;

public class Main {

    public static Configurations configurations = new Configurations();
    public static Definitions definitions = new Definitions();
    protected static Random randomGenerator = new Random(0); //is important not to remove seed from random (deterministic)
    public static Generator generateData = new Generator();

    public static void main(String[] args) throws IOException {
        loadPropertiesFile();
        start();
    }

    public static void start() throws IOException {
//        if( args.length < 1) {
//                throw new IllegalArgumentException("Missing parameter - the configuration file must be specified");
//        }
//        configurations.loadFromFile(args[0]);

        definitions.loadFromFile(configurations.getString(Configurations.DEFINITIONS_PATH));
        definitions.initializeAllocations(randomGenerator);
        generateData.exec();

    }

    public static void loadPropertiesFile() throws IOException {
        configurations.loadFromFile("test.properties");
    }

    public static Configurations getConfigurations() {
        return configurations;
    }

    public static Definitions getDefinitions() {
        return definitions;
    }

}
