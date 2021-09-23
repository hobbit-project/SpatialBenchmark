/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hobbit.spatialbenchmark.platformConnection.systems;

import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.ShutdownSignalException;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.logging.Level;
import static org.aksw.limes.core.controller.Controller.getMapping;
import org.aksw.limes.core.controller.ResultMappings;
import org.aksw.limes.core.io.config.Configuration;
import org.aksw.limes.core.io.config.reader.AConfigurationReader;
import org.aksw.limes.core.io.config.reader.xml.XMLConfigurationReader;
import org.aksw.limes.core.io.serializer.ISerializer;
import org.aksw.limes.core.io.serializer.SerializerFactory;
import org.apache.commons.io.FileUtils;
import org.hobbit.core.Commands;
import org.hobbit.core.components.AbstractSystemAdapter;
import org.hobbit.core.rabbit.RabbitMQUtils;
import org.hobbit.core.rabbit.SimpleFileReceiver;
import org.hobbit.spatialbenchmark.rabbit.SingleFileReceiver;
import org.hobbit.spatialbenchmark.util.FileUtil;
import org.hobbit.spatialbenchmark.util.SesameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jsaveta
 */
public class LimesSystemAdapter extends AbstractSystemAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(LimesSystemAdapter.class);
    private SimpleFileReceiver sourceReceiver;
    private SimpleFileReceiver targetReceiver;
    private String receivedGeneratedDataFilePath;
    private String dataFormat;
    private String taskFormat;
    private String resultsFile;
    private ResultMappings mappings;

    @Override
    public void init() throws Exception {
        LOGGER.info("Initializing Limes test system...");
        long time = System.currentTimeMillis();
        super.init();
        LOGGER.info("Super class initialized. It took {}ms.", System.currentTimeMillis() - time);
        time = System.currentTimeMillis();
        sourceReceiver = SimpleFileReceiver.create(this.incomingDataQueueFactory, "source_file");
        LOGGER.info("Receivers initialized. It took {}ms.", System.currentTimeMillis() - time);
        LOGGER.info("Limes initialized successfully.");

    }

    @Override
    public void receiveGeneratedData(byte[] data) {
        try {
            LOGGER.info("Starting receiveGeneratedData..");

            ByteBuffer dataBuffer = ByteBuffer.wrap(data);
            dataFormat = RabbitMQUtils.readString(dataBuffer);
            // read the file path
            receivedGeneratedDataFilePath = RabbitMQUtils.readString(dataBuffer);

            String[] receivedFiles = sourceReceiver.receiveData("./datasets/SourceDatasets/");
//            LOGGER.info("receivedFiles DATA " + Arrays.toString(receivedFiles));
            receivedGeneratedDataFilePath = "./datasets/SourceDatasets/" + receivedFiles[0];
            LOGGER.info("Received data from receiveGeneratedData..");

        } catch (IOException | ShutdownSignalException | ConsumerCancelledException | InterruptedException ex) {
            java.util.logging.Logger.getLogger(LimesSystemAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void receiveGeneratedTask(String taskId, byte[] data) {
        LOGGER.info("Starting receiveGeneratedTask..");
        LOGGER.info("Task " + taskId + " received from task generator");
        long time = System.currentTimeMillis();
        try {

            ByteBuffer taskBuffer = ByteBuffer.wrap(data);
            // read the relation
            String taskRelation = RabbitMQUtils.readString(taskBuffer);
            LOGGER.info("taskRelation " + taskRelation);
            // read the target geometry
            String targetGeom = RabbitMQUtils.readString(taskBuffer);
            LOGGER.info("targetGeom " + targetGeom);
            // read namespace
            String namespace = RabbitMQUtils.readString(taskBuffer);
            LOGGER.info("namespace " + namespace);
            // read the file path
            taskFormat = RabbitMQUtils.readString(taskBuffer);
            LOGGER.info("Parsed task " + taskId + ". It took {}ms.", System.currentTimeMillis() - time);
            time = System.currentTimeMillis();

            String receivedGeneratedTaskFilePath = null;
            try {
                targetReceiver = SingleFileReceiver.create(this.incomingDataQueueFactory,
                        "task_target_file");
                String[] receivedFiles = targetReceiver.receiveData("./datasets/TargetDatasets/");
//                LOGGER.info("receivedFiles TASK " + Arrays.toString(receivedFiles));
                receivedGeneratedTaskFilePath = "./datasets/TargetDatasets/" + receivedFiles[0];

            } catch (Exception e) {
                LOGGER.error("Exception while trying to receive data. Aborting.", e);
            }
            LOGGER.info("Received task data. It took {}ms.", System.currentTimeMillis() - time);
            time = System.currentTimeMillis();

            LOGGER.info("Task " + taskId + " received from task generator");

            limesController(receivedGeneratedDataFilePath, receivedGeneratedTaskFilePath, taskRelation, targetGeom, namespace.toLowerCase());
            byte[][] resultsArray = new byte[1][];
            resultsArray[0] = FileUtils.readFileToByteArray(new File(resultsFile));
            byte[] results = RabbitMQUtils.writeByteArrays(resultsArray);
            try {

                sendResultToEvalStorage(taskId, results);
                LOGGER.info("Results sent to evaluation storage.");
            } catch (IOException e) {
                LOGGER.error("Exception while sending storage space cost to evaluation storage.", e);
            }
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(LimesSystemAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void limesController(String source, String target, String relation, String targetGeom, String namespace) throws IOException {

        LOGGER.info("Started limesController.. ");
        AConfigurationReader reader = new XMLConfigurationReader("./configs/topologicalConfigs/config" + relation + ".xml");
        Configuration config = reader.read();
        
        
//        String[] args = new String[1];
//        args[0] = "./configs/topologicalConfigs/config" + relation + ".xml";
//
//        CommandLine cmd = parseCommandLine(args);
//        Configuration config = getConfig(cmd);

        config.getSourceInfo().setEndpoint(source);
        config.getTargetInfo().setEndpoint(target);
        config.getSourceInfo().setType(dataFormat);
        config.getTargetInfo().setType(taskFormat);
//        LOGGER.info("targetGeom " + targetGeom);
        ArrayList<String> sourceRestrictions = new ArrayList<String>();
        ArrayList<String> targetRestrictions = new ArrayList<String>();

        if (targetGeom.equals("POLYGON") && (relation.equals("CONTAINS") || relation.equals("COVERS"))) {
            sourceRestrictions.add("?y a " + namespace + "regions:Region");
            targetRestrictions.add("?y a " + namespace + ":Trace");

        } else if (targetGeom.equals("POLYGON")) {
            sourceRestrictions.add("?y a " + namespace + ":Trace");
            targetRestrictions.add("?y a " + namespace + "regions:Region");

        } else { //LineString
            sourceRestrictions.add("?y a " + namespace + ":Trace");
            targetRestrictions.add("?y a " + namespace + ":Trace");
        }

        config.getSourceInfo().setRestrictions(sourceRestrictions);
        config.getTargetInfo().setRestrictions(targetRestrictions);

        resultsFile = "./datasets/LimesSystemAdapterResults/" + relation + "mappings." + SesameUtils.parseRdfFormat(dataFormat).getDefaultFileExtension();

        File dir = new File("./datasets/LimesSystemAdapterResults");
        dir.mkdirs();
        File file = new File(dir, relation + "mappings." + SesameUtils.parseRdfFormat(dataFormat).getDefaultFileExtension());

        config.setAcceptanceFile(resultsFile);
        config.setVerificationFile("./datasets/LimesSystemAdapterResults/" + relation + "absolute_mapping_almost." + SesameUtils.parseRdfFormat(dataFormat).getDefaultFileExtension());

        mappings = getMapping(config);
        writeResults(mappings, config);

        //delete cache folder 
        File folder = new File("./cache/");
        FileUtil.removeDirectory(folder);

        LOGGER.info("limesController finished..");
    }

    private static void writeResults(ResultMappings mappings, Configuration config) {
        String outputFormat = config.getOutputFormat();
        ISerializer output = SerializerFactory.createSerializer(outputFormat);
        output.setPrefixes(config.getPrefixes());
        output.writeToFile(mappings.getAcceptanceMapping(), config.getAcceptanceRelation(), config.getAcceptanceFile());
    }

    @Override
    public void receiveCommand(byte command, byte[] data) {
        if (Commands.DATA_GENERATION_FINISHED == command) {
            LOGGER.info("my receiveCommand for source");
            sourceReceiver.terminate();

//        } else if (Commands.TASK_GENERATION_FINISHED == command) {
//            LOGGER.info("my receiveCommand for target");
//            targetReceiver.terminate();
        }
        super.receiveCommand(command, data);
    }

    @Override
    public void close() throws IOException {
        LOGGER.info("Closing System Adapter...");
        // Always close the super class after yours!
        super.close();
        LOGGER.info("System Adapter closed successfully.");
    }
}
