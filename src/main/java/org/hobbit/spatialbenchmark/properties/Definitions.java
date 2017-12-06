package org.hobbit.spatialbenchmark.properties;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Random;
import org.hobbit.spatialbenchmark.util.AllocationsUtil;

/**
 * A holder of the benchmark definitions of allocation values. Client is
 * expected to initialize from file definitions.properties first all allocation
 * values.
 *
 */
public class Definitions {

    public static final String SPATIAL_RELATIONS_ALLOCATION = "spatialRelationsAllocation";
    public static final String KEEP_POINTS_ALLOCATION = "keepPointsAllocation";
    public static final String TARGET_GEOMETRY_ALLOCATION = "targetGeometryAllocation";

    //Determines spatial allocation amount of relations 
    public static AllocationsUtil spatialRelationsAllocation;

    //Determines percentage of points we want to keep for each trace
    public static AllocationsUtil keepPointsAllocation;

    //Determines percentage of LineString to be transformed to LineString or Polygons
    public static AllocationsUtil targetGeometryAllocation;
    
    private static Properties definitionsProperties = new Properties();

    private boolean verbose = false;

    /**
     * Load the configuration from the given file (java properties format).
     *
     * @param filename A readable file on the file system.
     * @throws IOException
     */
    public void loadFromFile(String filename) throws IOException {

        InputStream input = new FileInputStream(filename);
        try {
            definitionsProperties.load(input);
        } finally {
            input.close();
        }
    }

    /**
     * Read a definition parameter's value as a string
     *
     * @param key
     * @return
     */
    private String getString(String key) {
        String value = definitionsProperties.getProperty(key);

        if (value == null) {
            throw new IllegalStateException("Missing definitions parameter: " + key);
        }
        return value;
    }

    /**
     * Read a configuration parameter's value as an int
     *
     * @param key
     * @return
     */
    public int getInt(String key) {
        String value = getString(key);

        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalStateException("Illegal value for integer configuration parameter: " + key);
        }
    }

    /**
     * Read a configuration parameter's value as a long
     *
     * @param key
     * @return
     */
    public long getLong(String key) {
        String value = getString(key);

        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw new IllegalStateException("Illegal value for long integer configuration parameter: " + key);
        }
    }

    /**
     * Read a configuration parameter's value as a Double
     *
     * @param key
     * @return
     */
    public double getDouble(String key) {
        String value = getString(key);

        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new IllegalStateException("Illegal value for long integer configuration parameter: " + key);
        }
    }

    public void setLong(String key, long value) {
        definitionsProperties.setProperty(key, Long.toString(value));
    }

    public void initializeAllocations(Random random) {
        if (verbose) {
            System.out.println("Initializing allocations...");
        }

        initializeAllocation(SPATIAL_RELATIONS_ALLOCATION, random);
        initializeAllocation(KEEP_POINTS_ALLOCATION, random);
        initializeAllocation(TARGET_GEOMETRY_ALLOCATION, random);
    }

    public static void reconfigureAllocations(Random random) {
        spatialRelationsAllocation.setRandom(random);
        keepPointsAllocation.setRandom(random);
        targetGeometryAllocation.setRandom(random);
    }

    /**
     * Initialize allocations depending on allocationProperty name
     */
    private void initializeAllocation(String allocationPropertyName, Random random) {
        String allocations = getString(allocationPropertyName);
        String[] allocationsAsStrings = allocations.split(",");
        double[] allocationsAsDoubles = new double[allocationsAsStrings.length];

        for (int i = 0; i < allocationsAsDoubles.length; i++) {
            allocationsAsDoubles[i] = Double.parseDouble(allocationsAsStrings[i]);
        }

        if (allocationPropertyName.equals(SPATIAL_RELATIONS_ALLOCATION)) {
            spatialRelationsAllocation = new AllocationsUtil(allocationsAsDoubles, random);
        } else if (allocationPropertyName.equals(KEEP_POINTS_ALLOCATION)) {
            keepPointsAllocation = new AllocationsUtil(allocationsAsDoubles, random);
        }else if (allocationPropertyName.equals(TARGET_GEOMETRY_ALLOCATION)) {
            targetGeometryAllocation = new AllocationsUtil(allocationsAsDoubles, random);
        }
        if (verbose) {
            System.out.println(String.format("\t%-33s : {%s}", allocationPropertyName, allocations));
        }
    }

    public void setIntProperty(String key, int value) {
        definitionsProperties.setProperty(key, Integer.toString(value));
    }

    public void setStringProperty(String key, String value) {
        definitionsProperties.setProperty(key, value);
    }
}
