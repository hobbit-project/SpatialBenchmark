package org.hobbit.spatialbenchmark.platformConnection;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.apache.commons.io.FileUtils;
import org.hobbit.core.components.AbstractDataGenerator;
import org.hobbit.core.rabbit.RabbitMQUtils;
import org.hobbit.spatialbenchmark.data.Generator;
import org.hobbit.spatialbenchmark.main.Main;
import org.hobbit.spatialbenchmark.platformConnection.util.PlatformConstants;
import org.hobbit.spatialbenchmark.properties.Configurations;
import static org.hobbit.spatialbenchmark.properties.Configurations.NEW_URI_NAMESPACE;
import static org.hobbit.spatialbenchmark.properties.Configurations.INSTANCES;
import org.hobbit.spatialbenchmark.properties.Definitions;
import org.hobbit.spatialbenchmark.util.SesameUtils;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jsaveta
 */
public class DataGenerator extends AbstractDataGenerator {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(DataGenerator.class);

    public static Configurations configurations = new Configurations();
    public static Definitions definitions = new Definitions();
    protected static Random randomGenerator = new Random(0);
    public static Generator generateData = new Generator();
    private int population;
    private String serializationFormat;

    /* Number of expected triples generated*/
    private String DATA_GENERATOR_POPULATION;
    private String DATA_GENERATOR_FORMAT;

    public void setDATA_GENERATOR_POPULATION(String dATA_GENERATOR_POPULATION) {
        DATA_GENERATOR_POPULATION = dATA_GENERATOR_POPULATION;
    }

    public void setDATA_GENERATOR_FORMAT(String dATA_GENERATOR_FORMAT) {
        DATA_GENERATOR_FORMAT = dATA_GENERATOR_FORMAT;
    }

    public String getDATA_GENERATOR_POPULATION() {
        return DATA_GENERATOR_POPULATION;
    }

    public String getDATA_GENERATOR_FORMAT() {
        return DATA_GENERATOR_FORMAT;
    }

    @Override
    public void init() throws Exception {
        // Always init the super class first!
        LOGGER.info("start DataGenerator init");
        super.init();
        LOGGER.info("finish DataGenerator init");

        LOGGER.info("start DataGenerator init from env");
        initFromEnv();
        LOGGER.info("finish DataGenerator init from env");
        // call data generation
        generateData();
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

    public void initFromEnv() {
        LOGGER.info("Getting Data Generator's properites from the environment...");

        Map<String, String> env = System.getenv();

        /*   Assigns the corresponding values to fields whose values are defined by the platform's GUI.
        Population */
        if (!env.containsKey(PlatformConstants.GENERATED_POPULATION)) {
            LOGGER.error(
                    "Couldn't get \"" + PlatformConstants.GENERATED_POPULATION + "\" from the properties. Aborting.");
            System.exit(1);
        }
        setDATA_GENERATOR_POPULATION(env.get(PlatformConstants.GENERATED_POPULATION));

        /* output data format */
        if (!env.containsKey(PlatformConstants.GENERATED_DATA_FORMAT)) {
            LOGGER.error(
                    "Couldn't get \"" + PlatformConstants.GENERATED_DATA_FORMAT + "\" from the properties. Aborting.");
            System.exit(1);
        }
        setDATA_GENERATOR_FORMAT(env.get(PlatformConstants.GENERATED_DATA_FORMAT));

        population = getFromEnv(env, PlatformConstants.GENERATED_POPULATION, 0);
        serializationFormat = (String) getFromEnv(env, PlatformConstants.GENERATED_DATA_FORMAT, "");
        
      
        LOGGER.info("population " +population);
        LOGGER.info("serializationFormat " +serializationFormat);
    }
    
    
    @Override
    // This method is used for sending the already generated data, tasks and gold standard
    // to the appropriate components.
    protected void generateData() throws Exception {
        // Create your data inside this method. You might want to use the
        // id of this data generator and the number of all data generators
        // running in parallel.
        int dataGeneratorId = getGeneratorId();
        int numberOfGenerators = getNumberOfGenerators();

        //read properties file
        Main.loadPropertiesFile();
        //change desired properties 
        DATA_GENERATOR_POPULATION = "10"; //edo tha kalestei to mimicking?! sigoura pantos to population de tha einai auto

        Main.getConfigurations().getProperties().setProperty(INSTANCES, DATA_GENERATOR_POPULATION); //population

        //to add the id on the folders of the generated data, add the line below
//        Main.getConfigurations().getProperties().setProperty(DATASETS_PATH, DATASETS_PATH+"/" + dataGeneratorId + "/"); 
        Main.getConfigurations().getProperties().setProperty(NEW_URI_NAMESPACE, Main.getConfigurations().getString(Configurations.NEW_URI_NAMESPACE) + dataGeneratorId + "/");

        //run data,task and gs generation
        runDataGeneration();
        
        String[] extensions = new String[]{SesameUtils.parseRdfFormat(Main.getConfigurations().getString(Configurations.GENERATED_DATA_FORMAT)).toString()};

        File dataPath = new File(Main.getConfigurations().getString(Configurations.DATASETS_PATH) + "/SourceDatasets");
        List<File> dataFiles = (List<File>) FileUtils.listFiles(dataPath, extensions, true);

        ByteArrayOutputStream dataBos = null;

        File taskPath = new File(Main.getConfigurations().getString(Configurations.DATASETS_PATH) + "/TargetDatasets");
        List<File> taskFiles = (List<File>) FileUtils.listFiles(taskPath, extensions, true);
        
        ByteArrayOutputStream tasksBos = null;
        
        File gsPath = new File(Main.getConfigurations().getString(Configurations.DATASETS_PATH) + "/GoldStandards");
        List<File> gsFiles = (List<File>) FileUtils.listFiles(gsPath, extensions, true);
        
        ByteArrayOutputStream gsBos = null;

        try {
            // send generated data to system adapter
            // all data have to be sent before sending the first query to system adapter
            for (File file : dataFiles) {
                byte[] fileNameBytes = RabbitMQUtils.writeString(file.getAbsolutePath());
                byte[] fileContentBytes = FileUtils.readFileToByteArray(file);

                dataBos = new ByteArrayOutputStream();
                dataBos.write(fileNameBytes.length);
                dataBos.write(fileNameBytes);
//                dataBos.write(fileContentBytes.length);
                dataBos.write(fileContentBytes);
                String sentFilePath = new String(fileNameBytes);

//                sendDataToSystemAdapter(dataBos.toByteArray());
                LOGGER.info(sentFilePath + " sent to System Adapter.");
            }
            LOGGER.info("All generated data successfully sent to System Adapter.");

            // send generated tasks along with their expected answers to task generator
            for (File file : taskFiles) {
                byte[] fileNameBytes = RabbitMQUtils.writeString(file.getAbsolutePath());
                byte[] fileContentBytes = FileUtils.readFileToByteArray(file);

                tasksBos = new ByteArrayOutputStream();
                tasksBos.write(fileNameBytes.length);
                tasksBos.write(fileNameBytes);
//                tasksBos.write(fileContentBytes.length);
                tasksBos.write(fileContentBytes);
//                String sentFilePath = new String(fileNameBytes);

                //fix this tasks
                sendDataToTaskGenerator(tasksBos.toByteArray());
               LOGGER.info("Task ??? sent to Task Generator.");
            }
            LOGGER.info("All generated tasks successfully sent to Task Generator.");
            
            
            
//            for (File file : gsFiles) {
//                byte[] fileNameBytes = RabbitMQUtils.writeString(file.getAbsolutePath());
//                byte[] fileContentBytes = FileUtils.readFileToByteArray(file);
//
//                gsBos = new ByteArrayOutputStream();
//                gsBos.write(fileNameBytes.length);
//                gsBos.write(fileNameBytes);
//                gsBos.write(fileContentBytes.length);
//                gsBos.write(fileContentBytes);
//                String sentFilePath = new String(fileNameBytes);
//
//                //where to send to gs?
//            }
            
        } catch (Exception e) {
            LOGGER.error("Exception while sending file to System Adapter or Task Generator(s).", e);
        }
    }

    public void runDataGeneration() throws IOException {
        Main.start();
    }

    @Override
    public void close() throws IOException {
        // Free the resources you requested here
        //...

        // Always close the super class after yours!
        super.close();
    }

}
