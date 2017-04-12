package org.hobbit.spatialbenchmark.main;

import java.io.IOException;
import org.hobbit.spatialbenchmark.data.Generator;
import static org.hobbit.spatialbenchmark.data.Generator.getConfigurations;
import static org.hobbit.spatialbenchmark.data.Generator.getDefinitions;
import static org.hobbit.spatialbenchmark.data.Generator.getRandom;
import org.hobbit.spatialbenchmark.properties.Configurations;

public class Main {

    private static Generator generateData;

    public static void main(String[] args) throws IOException {
        generateData = new Generator();
        getConfigurations().loadFromFile("test.properties");
        getDefinitions().loadFromFile(getConfigurations().getString(Configurations.DEFINITIONS_PATH));
        getDefinitions().initializeAllocations(getRandom());

        generateData.exec();
    }

}
