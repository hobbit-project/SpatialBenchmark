/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hobbit.spatialbenchmark.data.mimicking;

import java.io.File;
import java.io.IOException;

public class MimickingFactory {

    private String executeCommand = "podigg/podigg-lc-hobbit";
    private String mimickingName = "TRANSPORT_DATA";
    private String outputType = "rdf";

    

    /**
     * Function responsible for creating a byte array that includes the
     * appropriate input parameters for each mimicking algorithm.
     *
     * @param type, Type of the mimicking algorithm
     * @param population, size of output events
     * @param outputType, type of output data
     * @param outputFolder, name of the output folder
     * @param seed, seed for the mimicking algorithm
     *
     * @exception IOException if the measure type is invalid
     *
     */
    public static String[] getMimickingArguments(String population, String outputType,
            String outputFolder, String seed) throws IOException {
        // check if folder exists and create it if otherwise
        boolean success = false;
        File directory = new File(outputFolder);
        if (!directory.exists()) {
            success = directory.mkdir();
            if (!success) {
                throw new IOException("Failed to create new directory: " + outputFolder);
            }
        }
        /////////////////////////////////////////////////////////////////////
        String[] mimickingTask = null;

        String[] transportDataArguments = new String[2];
        transportDataArguments[0] = "GTFS_GEN_SEED=" + seed;
        transportDataArguments[1] = "GTFS_GEN_CONNECTIONS__CONNECTIONS=" + population;
        mimickingTask = transportDataArguments;

        return mimickingTask;
    }
}
