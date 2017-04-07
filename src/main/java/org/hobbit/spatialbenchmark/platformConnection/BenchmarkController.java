/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hobbit.spatialbenchmark.platformConnection;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.jena.rdf.model.NodeIterator;
import org.hobbit.core.Commands;
import org.hobbit.core.components.AbstractBenchmarkController;
import org.hobbit.spatialbenchmark.platformConnection.util.PlatformConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jsaveta
 */
public class BenchmarkController extends AbstractBenchmarkController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BenchmarkController.class);
//    private static final String DATA_GENERATOR_CONTAINER_IMAGE = "git.project-hobbit.eu:4567/jsaveta1/spatialdatagenerator";
//    private static final String TASK_GENERATOR_CONTAINER_IMAGE = "git.project-hobbit.eu:4567/jsaveta1/spatialtaskgenerator";
//    private static final String EVALUATION_MODULE_CONTAINER_IMAGE = "git.project-hobbit.eu:4567/jsaveta1/spatialevaluationmodule";

    private static final String DATA_GENERATOR_CONTAINER_IMAGE = "spatial_data-generator";
    private static final String TASK_GENERATOR_CONTAINER_IMAGE = "spatial_task-generator";
    private static final String EVALUATION_MODULE_CONTAINER_IMAGE = "spatial_evaluation-module";

    @Override
    public void init() throws Exception {
        LOGGER.info("Initilalizing Benchmark Controller...");
        super.init();

        int numberOfDataGenerators = (Integer) getProperty("http://w3id.org/bench#hasNumberOfGenerators", 1);
        int population = (Integer) getProperty("http://w3id.org/bench#hasPopulation", 100);
        String serializationFormat = (String) getProperty("http://w3id.org/bench#spatialDataFormat", "n-triples");
        String spatialRelation = (String) getProperty("http://w3id.org/bench#spatialRelation", "COVERS");
        double keepPoints = (double) getProperty("http://w3id.org/bench#keepPoints", 0.3);

        // data generators environmental values
        String[] envVariablesDataGenerator = new String[]{
            PlatformConstants.NUMBER_OF_DATA_GENERATORS + "=" + numberOfDataGenerators,
            PlatformConstants.GENERATED_POPULATION + "=" + population,
            PlatformConstants.GENERATED_DATA_FORMAT + "=" + serializationFormat,
            PlatformConstants.SPATIAL_RELATION + "=" + spatialRelation,
            PlatformConstants.KEEP_POINTS + "=" + keepPoints
        };

        // Create data generators
        createDataGenerators(DATA_GENERATOR_CONTAINER_IMAGE, numberOfDataGenerators, envVariablesDataGenerator);
        LOGGER.info("Initilalizing Benchmark Controller...");

        // Create task generators
        createTaskGenerators(TASK_GENERATOR_CONTAINER_IMAGE, 1, new String[]{});
        LOGGER.info("Task Generators created successfully.");

        // Create evaluation storage
        createEvaluationStorage();
        LOGGER.info("Evaluation Storage created successfully.");

        waitForComponentsToInitialize();
        LOGGER.info("All components initilized.");
    }

    /**
     * A generic method for loading parameters from the benchmark parameter
     * model
     *
     * @param property the property that we want to load
     * @param defaultValue the default value that will be used in case of an
     * error while loading the property
     */
    @SuppressWarnings("unchecked")
    private <T> T getProperty(String property, T defaultValue) {
        T propertyValue = null;
        NodeIterator iterator = benchmarkParamModel
                .listObjectsOfProperty(benchmarkParamModel
                        .getProperty(property));

        if (iterator.hasNext()) {
            try {
                if (defaultValue instanceof String) {
                    return (T) iterator.next().asLiteral().getString();
                } else if (defaultValue instanceof Integer) {
                    return (T) ((Integer) iterator.next().asLiteral().getInt());
                } else if (defaultValue instanceof Long) {
                    return (T) ((Long) iterator.next().asLiteral().getLong());
                } else if (defaultValue instanceof Double) {
                    return (T) ((Double) iterator.next().asLiteral().getDouble());
                }
            } catch (Exception e) {
                LOGGER.error("Exception while parsing parameter.");
            }
        } else {
            LOGGER.info("Couldn't get property '" + property + "' from the parameter model. Using '" + defaultValue + "' as a default value.");
            propertyValue = defaultValue;
        }
        return propertyValue;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.hobbit.core.components.AbstractBenchmarkController#executeBenchmark()
     */
    @Override
    protected void executeBenchmark() throws Exception {

        // give the start signals
        sendToCmdQueue(Commands.TASK_GENERATOR_START_SIGNAL);
        sendToCmdQueue(Commands.DATA_GENERATOR_START_SIGNAL);
        LOGGER.info("Start signals sent to Data and Task Generators");

        // wait for the data generators to finish their work
        LOGGER.info("Waiting for the data generators to finish their work.");
        waitForDataGenToFinish();
        LOGGER.info("Data generators finished.");

        // wait for the task generators to finish their work
        LOGGER.info("Waiting for the task generators to finish their work.");
        waitForTaskGenToFinish();
        LOGGER.info("Task generators finished.");

        // wait for the system to terminate
        LOGGER.info("Waiting for the system to terminate.");
        waitForSystemToFinish();
        LOGGER.info("System terminated.");

        // create the evaluation module
        String[] envVariablesEvaluationModule = new String[] { };
        createEvaluationModule(EVALUATION_MODULE_CONTAINER_IMAGE, envVariablesEvaluationModule);
      
        // wait for the evaluation to finish
        LOGGER.info("Waiting for the evaluation to finish.");
        waitForEvalComponentsToFinish();
        LOGGER.info("Evaluation finished.");
//        
//        // Send the resultModul to the platform controller and terminate
//        sendResultModel(this.resultModel);
//        LOGGER.info("Evaluated results sent to the platform controller.");
    }
    
}
