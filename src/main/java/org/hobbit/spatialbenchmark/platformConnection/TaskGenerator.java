/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hobbit.spatialbenchmark.platformConnection;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.hobbit.core.Commands;
import org.hobbit.core.components.AbstractTaskGenerator;
import org.hobbit.core.rabbit.RabbitMQUtils;
import org.hobbit.core.rabbit.SimpleFileReceiver;
import org.hobbit.core.rabbit.SimpleFileSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jsaveta
 */
public class TaskGenerator extends AbstractTaskGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskGenerator.class);
    private SimpleFileReceiver targetReceiver;

    public TaskGenerator() {
        super(1);
    }

    @Override
    public void init() throws Exception {

        LOGGER.info("Initializing Task Generators...");
        super.init();
        targetReceiver = SimpleFileReceiver.create(this.incomingDataQueueFactory, "target_file");
        LOGGER.info("Task Generators initialized successfully.");
    }

    @Override
    protected void generateTask(byte[] data) throws Exception {
        try {
            // Create tasks based on the incoming data inside this method.
            // You might want to use the id of this task generator and the
            // number of all task generators running in parallel.

//          int dataGeneratorId = getGeneratorId();
//          int numberOfGenerators = getNumberOfGenerators();
//          targetReceiver = SimpleFileReceiver.create(this.incomingDataQueueFactory, "target_file");
            
            String[] receivedFiles_target = targetReceiver.receiveData("./datasets/TargetDatasets/");

            for (String f : receivedFiles_target) {
                // define a queue name, e.g., read it from the environment
                String queueName = "task_target_file";
                File file = new File("./datasets/TargetDatasets/" + f);
                // create the sender
                SimpleFileSender sender = SimpleFileSender.create(this.outgoingDataQueuefactory, queueName);

                InputStream is = null;
                try {
                    // create input stream, e.g., by opening a file
                    is = new FileInputStream(file);
                    // send data
                    sender.streamData(is, file.getName());
                } catch (Exception e) {
                    // handle exception
                } finally {
                    IOUtils.closeQuietly(is);
                }
                // close the sender
                IOUtils.closeQuietly(sender);

            }
            // Create an ID for the task
            Task task = (Task) SerializationUtils.deserialize(data);
            String taskId = task.getTaskId();
            LOGGER.info("taskId: " + taskId);
            String taskRelation = task.getRelation();
            LOGGER.info("taskRelation: " + taskRelation);
            String targetGeom = task.getTargetGeom();
            LOGGER.info("targetGeom: " + targetGeom);
            String datagen = task.getDataGen();
            LOGGER.info("datagen: " + datagen);
            byte[] target = task.getTarget();
            ByteBuffer taskBuffer = ByteBuffer.wrap(target);
            String format = RabbitMQUtils.readString(taskBuffer);
            String path = RabbitMQUtils.readString(taskBuffer);

            byte[][] taskDataArray = new byte[5][];
            taskDataArray[0] = RabbitMQUtils.writeString(taskRelation);
            taskDataArray[1] = RabbitMQUtils.writeString(targetGeom);
            taskDataArray[2] = RabbitMQUtils.writeString(datagen);
            taskDataArray[3] = RabbitMQUtils.writeString(format);
            taskDataArray[4] = RabbitMQUtils.writeString(path);

            byte[] taskData = RabbitMQUtils.writeByteArrays(taskDataArray);

            byte[] expectedAnswerData = task.getExpectedAnswers();

            // Send the task to the system (and store the timestamp)
            sendTaskToSystemAdapter(taskId, taskData);
            LOGGER.info("Task " + taskId + " sent to System Adapter.");

            // Send the expected answer to the evaluation store
            long timestamp = System.currentTimeMillis();
            sendTaskToEvalStorage(taskId, timestamp, expectedAnswerData);
            LOGGER.info("Expected answers of task " + taskId + " sent to Evaluation Storage.");

        } catch (Exception e) {
            LOGGER.error("Exception caught while reading the tasks and their expected answers", e);
        }
    }

    @Override
    public void receiveCommand(byte command, byte[] data) {
        if (Commands.DATA_GENERATION_FINISHED == command) {
            targetReceiver.terminate();

        }
        super.receiveCommand(command, data);
    }

    @Override
    public void close() throws IOException {
        LOGGER.info("Closign Task Generator...");

        super.close();
        LOGGER.info("Task Genererator closed successfully.");
    }
}
