package org.hobbit.spatialbenchmark.data;

import com.vividsolutions.jts.geom.create.GeometryType.GeometryTypes;
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

    private static AtomicLong filesCount = new AtomicLong(0);
    private static SpatialTransformation transform;
    private static Configurations configurations;
    private static Definitions definitions;
    private static Random randomGenerator;//is important not to remove seed from random (deterministic)
    private static RelationsCall call;
    private static GeometryTypes geometryType;

    public Generator() {

        configurations = new Configurations();
        definitions = new Definitions();
        randomGenerator = new Random(0);//is important not to remove seed from random (deterministic)
        call = new RelationsCall();

    }

    public static Random getRandom() {
        return randomGenerator;
    }

    public static AtomicLong getAtomicLong() {
        return filesCount;
    }

    public static RelationsCall getRelationsCall() {
        return call;
    }

    public static Configurations getConfigurations() {
        return configurations;
    }

    public static Definitions getDefinitions() {
        return definitions;
    }

    public static SpatialTransformation getSpatialTransformation() {
        return transform;
    }

    public static void setRelationsCall(RelationsCall c) {
        call = c;
    }

    public static void setConfigurations(Configurations conf) {
        configurations = conf;
    }

    public static void setDefinitions(Definitions def) {
        definitions = def;
    }

    public static void setSpatialTransformation(SpatialTransformation tr) {
        transform = tr;
    }
    public static void setTargetGeometryType(GeometryTypes gt) {
        geometryType = gt;
    }
    

//    public static void loadPropertiesFile(String file) throws IOException {
//        configurations.loadFromFile(file);
//    }

    public void exec() {
        try {

            call.spatialRelationsCases();
            transform = call.getSpatialRelationsConfiguration();
            geometryType = call.getTargetGeometryType();
            System.out.println("exec configurations instances " + getConfigurations().getString(Configurations.INSTANCES));

            Worker worker = new Worker();
            worker.execute();

        } catch (Exception ex) {
            Logger.getLogger(Generator.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }
}
