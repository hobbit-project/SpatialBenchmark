package org.hobbit.spatialbenchmark.data;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hobbit.spatialbenchmark.properties.Configurations;
import org.hobbit.spatialbenchmark.properties.Definitions;
import org.hobbit.spatialbenchmark.transformations.RelationsCall;
import org.hobbit.spatialbenchmark.transformations.SpatialTransformation;

/**
 * The class responsible for managing data generation for the benchmark. It is
 * the entry point for any data generation related process.
 *
 */
public class Generator {

    public static AtomicLong filesCount = new AtomicLong(0);
    public static SpatialTransformation transform;
    public static Configurations configurations;
    public static Definitions definitions ;
    public static Random randomGenerator;//is important not to remove seed from random (deterministic)
    public static RelationsCall call;

    public Generator() {
        configurations = new Configurations();
        definitions = new Definitions();
        randomGenerator = new Random(0);//is important not to remove seed from random (deterministic)
        call = new RelationsCall();

    }

//    public Configurations getConfigurations() {
//        return configurations;
//    }
//
//    public Definitions getDefinitions() {
//        return definitions;
//    }
    public void setConfigurations(Configurations conf) {
        configurations = conf;
    }

    public void setDefinitions(Definitions def) {
        definitions = def;
    }

    public void loadPropertiesFile() throws IOException {
        configurations.loadFromFile("test.properties");
    }

    public void exec() {
        try {
            call.spatialRelationsCases();
            transform = call.getSpatialRelationsConfiguration();

            String destinationPath = configurations.getString(Configurations.DATASETS_PATH);
            String serializationFormat = configurations.getString(Configurations.GENERATED_DATA_FORMAT);
            Worker worker = new Worker(destinationPath, serializationFormat);

            worker.execute();

        } catch (Exception ex) {
            Logger.getLogger(Generator.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }
}
