package org.hobbit.spatialbenchmark.platformConnection;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.hobbit.core.components.AbstractDataGenerator;
import org.hobbit.core.rabbit.RabbitMQUtils;
import org.hobbit.spatialbenchmark.data.Generator;
import org.hobbit.spatialbenchmark.data.Worker;
import org.hobbit.spatialbenchmark.platformConnection.util.PlatformConstants;
import org.hobbit.spatialbenchmark.properties.Configurations;
import org.hobbit.spatialbenchmark.properties.Definitions;
import org.hobbit.spatialbenchmark.util.AllocationsUtil;
import static org.hobbit.spatialbenchmark.properties.Configurations.GENERATED_DATA_FORMAT;
import static org.hobbit.spatialbenchmark.properties.Configurations.INSTANCES;
import static org.hobbit.spatialbenchmark.data.Generator.call;
import static org.hobbit.spatialbenchmark.data.Generator.configurations;
import static org.hobbit.spatialbenchmark.data.Generator.definitions;
import static org.hobbit.spatialbenchmark.data.Generator.randomGenerator;

/**
 *
 * @author jsaveta
 */
public class DataGenerator extends AbstractDataGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataGenerator.class);

    private int numberOfDataGenerators;
    private int population;
    public static String serializationFormat;
    private String spatialRelation;
    private double keepPoints;
    private int taskId = 0;

    public static Generator dataGeneration = new Generator();

    public static String testPropertiesFile = System.getProperty("user.dir") + File.separator + "test.properties";
    public static String definitionsPropertiesFile = System.getProperty("user.dir") + File.separator + "definitions.properties";
    public static String datasetsPath = System.getProperty("user.dir") + File.separator + "datasets";
    public static String givenDatasetsPath = System.getProperty("user.dir") + File.separator + "datasets" + File.separator + "givenDatasets";

    private Task task;

    @Override
    public void init() throws Exception {
        LOGGER.info("Initializing Data Generator '" + getGeneratorId() + "'");
        // Always init the super class first!
        LOGGER.info("BEFORE super.init() of DataGenerator");
        super.init();

        LOGGER.info("AFTER super.init() of DataGenerator");
        initFromEnv();

        LOGGER.info("reInitializeProperties...");
        reInitializeProperties();

        task = new Task(Integer.toString(taskId++), null, null);

    }

    @Override
    protected void generateData() throws Exception {
        // Create your data inside this method. You might want to use the
        // id of this data generator and the number of all data generators
        // running in parallel.

        LOGGER.info("Generate data.. ");
        try {
            //to filescount de xreiazetai logika
            //episis to id pou allaza vasi tou generator?
            //to task id? tsekare ayta!!!!
            //to generator id??

//            LOGGER.info("filesCount " + filesCount);
            LOGGER.info("datasetsPath " + datasetsPath);
            LOGGER.info("serializationFormat " + serializationFormat);

            Worker worker = new Worker(datasetsPath, serializationFormat);
            LOGGER.info("before worker execute ");
            worker.execute();
            LOGGER.info("Worker is done! ");

            LOGGER.info("Now we have produced source, target and gs ");

//Error response from daemon: 
// open /hdd/docker/containers/9112d192614bb76730f33863a6d1f47746155d22f48c7f156f265b07e446732f/9112d192614bb76730f33863a6d1f47746155d22f48c7f156f265b07e446732f-json.log: 
// no such file or directory
//document doesn't start with a valid json element : null
            LOGGER.info("before sourcePath");
            File sourcePath = new File(Generator.configurations.getString(Configurations.DATASETS_PATH) + File.separator + "SourceDatasets");
            LOGGER.info("sourcePath  " + Generator.configurations.getString(Configurations.DATASETS_PATH) + File.separator + "SourceDatasets");
            ArrayList<File> sourceFiles = new ArrayList<File>(Arrays.asList(sourcePath.listFiles()));
            LOGGER.info("sourcePath " + datasetsPath + File.separator + "SourceDatasets");

            LOGGER.info("before targetPath");
            File targetPath = new File(Generator.configurations.getString(Configurations.DATASETS_PATH) + File.separator + "TargetDatasets");
            ArrayList<File> targetFiles = new ArrayList<File>(Arrays.asList(targetPath.listFiles()));
            LOGGER.info("targetPath " + datasetsPath + File.separator + "TargetDatasets");
//mipos na pairno mono auta pou teleionoun me to relation?

            ByteArrayOutputStream dataBos = null;
            ByteArrayOutputStream tasksBos = null;

            //exoun paraxthei source, target kai gs alla ta parakato loggers deixnoun oti to size einai 0
            //vasika tupose to periexomeno! 
            //mipos thelei / sto telos?
            LOGGER.info("sourceFiles size " + sourceFiles.size());
            LOGGER.info("targetFiles size " + targetFiles.size());

            // send generated data to system adapter
            // all data have to be sent before sending the first query to system adapter
            for (File file : sourceFiles) {
                byte[] fileNameBytes = RabbitMQUtils.writeString(file.getAbsolutePath());
                byte[] fileContentBytes = FileUtils.readFileToByteArray(file);

                dataBos = new ByteArrayOutputStream();
                dataBos.write(fileNameBytes.length);
                dataBos.write(fileNameBytes);
                dataBos.write(fileContentBytes);
                String sentFilePath = new String(fileNameBytes);

//                 LOGGER.info("SOS I COMMENTED OUT sendDataToSystemAdapter");
                sendDataToSystemAdapter(dataBos.toByteArray());
                LOGGER.info(sentFilePath + " sent to System Adapter.");

            }
            LOGGER.info("Source data successfully sent to System Adapter.");

            File gsPath = new File(Generator.configurations.getString(Configurations.DATASETS_PATH) + File.separator + "GoldStandards");
//File gsPath = new File("./datasets/GoldStandards");
            LOGGER.info("DATAGENERATOR gsPath " + gsPath);
            ArrayList<File> gsFiles = new ArrayList<File>(Arrays.asList(gsPath.listFiles()));
            LOGGER.info("DATAGENERATOR gsFiles.size() " + gsFiles.size());

            //mipos to signal kanei kati? paremvainei?
            LOGGER.info("THA DIAVASO TA GS");
            ByteArrayOutputStream gsBos = null;
            // send generated data to system adapter
            // all data have to be sent before sending the first query to system adapter

            //mipos an einai pano apo ena file exo thema? sto send kai to set?
            for (File file : gsFiles) {
                byte[] fileNameBytes = RabbitMQUtils.writeString(file.getAbsolutePath());
                byte[] fileContentBytes = FileUtils.readFileToByteArray(file);

                gsBos = new ByteArrayOutputStream();
                gsBos.write(fileNameBytes.length);
                gsBos.write(fileNameBytes);
                gsBos.write(fileContentBytes);
//                String sentFilePath = new String(fileNameBytes);

                task.setExpectedAnswers(fileNameBytes);
            }
            LOGGER.info("Gold Standard successfully added to Task.");

            // send generated tasks along with their expected answers to task generator
            for (File file : targetFiles) {
                byte[] fileNameBytes = RabbitMQUtils.writeString(file.getAbsolutePath());
                byte[] fileContentBytes = FileUtils.readFileToByteArray(file);

                tasksBos = new ByteArrayOutputStream();
                tasksBos.write(fileNameBytes.length);
                tasksBos.write(fileNameBytes);
                tasksBos.write(fileContentBytes);
//                String sentFilePath = new String(fileNameBytes);

                task.setTarget(fileNameBytes);

                byte[] data = SerializationUtils.serialize(task);
                sendDataToTaskGenerator(data);
//                sendDataToTaskGenerator(tasksBos.toByteArray());

//                LOGGER.info("TARGET DATA BYTE ARRAY " + Arrays.toString(tasksBos.toByteArray()));
//                String s = new String(tasksBos.toByteArray());
//                LOGGER.info("Source Text Decrypted : " + s);
            }

//            for (Task task : tasks) {
//                byte[] data = SerializationUtils.serialize(task);
//                sendDataToTaskGenerator(data);
//                LOGGER.info("Task " + task.getTaskId() + " sent to Task Generator.");
//            }
            LOGGER.info("Target data successfully sent to Task Generator. - TASK ");

            LOGGER.info("****** dataGeneration.configurations == null " + (dataGeneration.configurations == null));
            LOGGER.info("******* Generator.configurations == null " + (Generator.configurations == null));

//            LOGGER.info("EGRAPSE TO GS STO BYTE[]");
//            //todo tasks!!!!?!!!?
//            //send gold standard ?
//            //energopoise ta alla tasks
        } catch (Exception e) {
            LOGGER.error("Exception while sending file to System Adapter or Task Generator(s).", e);
        }

//        int dataGeneratorId = getGeneratorId();
//        int numberOfGenerators = getNumberOfGenerators();
//
//        byte[] data;
//        while(notEnoughDataGenerated) {
//            // Create your data here
//            data = ...
//
//            // the data can be sent to the task generator(s) ...
//            sendDataToTaskGenerator(data);
//            // ... and/or to the system
//            sendDataToSystemAdapter(data);
//        }
    }

//    @Override
//    public void close() throws IOException {
//        super.close();
//    }
    public void initFromEnv() {
        LOGGER.info("Getting Data Generator's properites from the environment...");

        Map<String, String> env = System.getenv();
        serializationFormat = (String) getFromEnv(env, PlatformConstants.GENERATED_DATA_FORMAT, "");
        population = (Integer) getFromEnv(env, PlatformConstants.GENERATED_POPULATION, 0);
        numberOfDataGenerators = (Integer) getFromEnv(env, PlatformConstants.NUMBER_OF_DATA_GENERATORS, 0);
        spatialRelation = (String) getFromEnv(env, PlatformConstants.SPATIAL_RELATION, "");
        keepPoints = (double) getFromEnv(env, PlatformConstants.KEEP_POINTS, 0.0);
    }

    /**
     * A generic method for initialize benchmark parameters from environment
     * variables
     *
     * @param env a map of all available environment variables
     * @param parameter the property that we want to get
     * @param paramType a dummy parameter to recognize property's type
     */
    @SuppressWarnings("unchecked")
    private <T> T getFromEnv(Map<String, String> env, String parameter, T paramType) {
        if (!env.containsKey(parameter)) {
            LOGGER.error(
                    "Environment variable \"" + parameter + "\" is not set. Aborting.");
            throw new IllegalArgumentException(
                    "Environment variable \"" + parameter + "\" is not set. Aborting.");
        }
        try {
            if (paramType instanceof String) {
                return (T) env.get(parameter);
            } else if (paramType instanceof Integer) {
                return (T) (Integer) Integer.parseInt(env.get(parameter));
            } else if (paramType instanceof Long) {
                return (T) (Long) Long.parseLong(env.get(parameter));
            } else if (paramType instanceof Double) {
                return (T) (Double) Double.parseDouble(env.get(parameter));
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Couldn't get \"" + parameter + "\" from the environment. Aborting.", e);
        }
        return paramType;
    }

    /*
	 * Update SPB configuration files that are necessary for data generation
     */
    public void reInitializeProperties() throws IOException {
        LOGGER.info("start method reInitializeProperties()");
//        int numberOfGenerators = getNumberOfGenerators();
        int generatorId = getGeneratorId();
//        dataGeneration = new Generator();
        LOGGER.info("reInitializeProperties 1 ");
        loadPropertiesConfigurationFiles();

        definitions.initializeAllocations(randomGenerator);

        // re-initialize test.properties file that is required for data generation
        configurations.setStringProperty(INSTANCES, String.valueOf(population));
        configurations.setStringProperty(GENERATED_DATA_FORMAT, serializationFormat);
        configurations.setStringProperty(Configurations.DATASETS_PATH, datasetsPath);
        configurations.setStringProperty(Configurations.GIVEN_DATASETS_PATH, givenDatasetsPath);
        LOGGER.info("reInitializeProperties 2 ");
        //todo : check if keep points < 1.0

        LOGGER.info("keepPoints " + keepPoints);

        ArrayList<Double> points = new ArrayList<Double>();
        points.add(keepPoints);
        points.add(1.0 - keepPoints);
        Random random = new Random();
        Definitions.keepPointsAllocation = new AllocationsUtil(points, random);

        LOGGER.info("reInitializeProperties 3 ");
        ArrayList<Double> relation = new ArrayList<Double>();
        for (int i = 0; i < 10; i++) {
            relation.add(0.0);
        }
        relation.add(spatialRelation.indexOf(spatialRelation), 1.0);
        Definitions.spatialRelationsAllocation = new AllocationsUtil(relation, random);

        call.spatialRelationsCases();
        Generator.transform = call.getSpatialRelationsConfiguration();

        LOGGER.info("-points " + points.toString());
        LOGGER.info("-Definitions.keepPointsAllocation.getAllocation() " + Definitions.keepPointsAllocation.getAllocation());

        LOGGER.info("-logger from data generator: " + Generator.transform);
        LOGGER.info("-relation " + relation.toString());
        LOGGER.info("-Definitions.spatialRelationsAllocation.getAllocation() " + Definitions.spatialRelationsAllocation.getAllocation());

        LOGGER.info("-getConfigurations() GENERATED_DATA_FORMAT " + configurations.getProperties().getProperty(GENERATED_DATA_FORMAT));
        LOGGER.info("-spatialRelation.indexOf(spatialRelation) " + spatialRelation.indexOf(spatialRelation));
    }

    public static void loadPropertiesConfigurationFiles() throws IOException {
        configurations.loadFromFile(testPropertiesFile);
        definitions.loadFromFile(configurations.getString(Configurations.DEFINITIONS_PATH));
    }

}
