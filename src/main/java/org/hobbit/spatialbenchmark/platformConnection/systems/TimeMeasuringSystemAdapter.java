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
import java.util.logging.Level;
import static org.aksw.limes.core.controller.Controller.getConfig;
import static org.aksw.limes.core.controller.Controller.getMapping;
import static org.aksw.limes.core.controller.Controller.parseCommandLine;
import org.aksw.limes.core.controller.ResultMappings;
import org.aksw.limes.core.io.config.Configuration;
import org.aksw.limes.core.io.serializer.ISerializer;
import org.aksw.limes.core.io.serializer.SerializerFactory;
import org.apache.commons.cli.CommandLine;
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
public class TimeMeasuringSystemAdapter extends AbstractSystemAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(TimeMeasuringSystemAdapter.class);
    private SimpleFileReceiver sourceReceiver;
    private SimpleFileReceiver targetReceiver;
    private String receivedGeneratedDataFilePath;
    private String dataFormat;
    private String taskFormat;
    private String resultsFile;
    private ResultMappings mappings;

    @Override
    public void init() throws Exception {
        LOGGER.info("Initializing test system...");
        long time = System.currentTimeMillis();
        super.init();
        LOGGER.info("Super class initialized. It took {}ms.", System.currentTimeMillis() - time);
        time = System.currentTimeMillis();
        sourceReceiver = SimpleFileReceiver.create(this.incomingDataQueueFactory, "source_file");
        LOGGER.info("Receivers initialized. It took {}ms.", System.currentTimeMillis() - time);
    }

    @Override
    public void receiveGeneratedData(byte[] data) {
        try {
            LOGGER.info("Starting receiveGeneratedData..");
            long time = System.currentTimeMillis();

            ByteBuffer dataBuffer = ByteBuffer.wrap(data);
            // read the file path
            dataFormat = RabbitMQUtils.readString(dataBuffer);
            receivedGeneratedDataFilePath = RabbitMQUtils.readString(dataBuffer);

            String[] receivedFiles = sourceReceiver.receiveData("./datasets/SourceDatasets/");
            // LOGGER.info("receivedFiles 1 " + Arrays.toString(receivedFiles));
            receivedGeneratedDataFilePath = "./datasets/SourceDatasets/" + receivedFiles[0];
            LOGGER.info("Received data from receiveGeneratedData. It took {}ms.", System.currentTimeMillis() - time);

        } catch (IOException | ShutdownSignalException | ConsumerCancelledException | InterruptedException ex) {
            LOGGER.error("Got an exception while receiving generated data.", ex);
        }
    }

    @Override
    public void receiveGeneratedTask(String taskId, byte[] data) {
        LOGGER.info("Starting receiveGeneratedTask..");
        LOGGER.info("Task " + taskId + " received from task generator");
        long time = System.currentTimeMillis();
        ByteBuffer taskBuffer = ByteBuffer.wrap(data);
        // read the relation
        String taskRelation = RabbitMQUtils.readString(taskBuffer);
        LOGGER.info("taskRelation " + taskRelation);

        // read the file path
        taskFormat = RabbitMQUtils.readString(taskBuffer);
        LOGGER.info("Parsed task " + taskId + ". It took {}ms.", System.currentTimeMillis() - time);
        time = System.currentTimeMillis();

        SingleFileReceiver targetReceiver;
        try {
            targetReceiver = SingleFileReceiver.create(this.incomingDataQueueFactory,
                    "task_target_file");
            String[] receivedFiles = targetReceiver.receiveData("./datasets/TargetDatasets/");
            String receivedGeneratedTaskFilePath = "./datasets/TargetDatasets/" + receivedFiles[0];
        } catch (Exception e) {
            LOGGER.error("Exception while trying to receive data. Aborting.", e);
        }
        LOGGER.info("Received task data. It took {}ms.", System.currentTimeMillis() - time);
        time = System.currentTimeMillis();

        byte[][] resultsArray = new byte[1][];
        resultsArray[0] = new byte[0];
        byte[] results = RabbitMQUtils.writeByteArrays(resultsArray);
        try {
            sendResultToEvalStorage(taskId, results);
            LOGGER.info("Results sent to evaluation storage. It took {}ms.", System.currentTimeMillis() - time);
        } catch (IOException e) {
            LOGGER.error("Exception while sending storage space cost to evaluation storage.", e);
        }
    }

    @Override
    public void receiveCommand(byte command, byte[] data) {
        if (Commands.DATA_GENERATION_FINISHED == command) {
            LOGGER.info("my receiveCommand for source");
            sourceReceiver.terminate();
            // } else if (Commands.TASK_GENERATION_FINISHED == command) {
            // LOGGER.info("my receiveCommand for target");
            // targetReceiver.terminate();
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
