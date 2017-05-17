/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hobbit.spatialbenchmark.platformConnection;

import java.io.IOException;
import java.nio.ByteBuffer;
import org.apache.commons.lang3.SerializationUtils;
import org.hobbit.core.components.AbstractTaskGenerator;
import org.hobbit.core.rabbit.RabbitMQUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jsaveta
 */
public class TaskGenerator extends AbstractTaskGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskGenerator.class);

    public TaskGenerator(){
        super(1);
    }
    @Override
    public void init() throws Exception {
        
        LOGGER.info("Initializing Task Generators...");
        super.init();
        LOGGER.info("Task Generators initialized successfully.");
    }

    @Override
    protected void generateTask(byte[] data) throws Exception {
        try {
            // Create tasks based on the incoming data inside this method.
            // You might want to use the id of this task generator and the
            // number of all task generators running in parallel.
//        int dataGeneratorId = getGeneratorId();
//        int numberOfGenerators = getNumberOfGenerators();

            // Create an ID for the task
            Task task = (Task) SerializationUtils.deserialize(data);
            String taskId = task.getTaskId(); 
            String taskRelation = task.getRelation();

            byte[] target = task.getTarget();
            ByteBuffer taskBuffer = ByteBuffer.wrap(target);
            String format = RabbitMQUtils.readString(taskBuffer);
            String path = RabbitMQUtils.readString(taskBuffer);
            byte[] targetData = RabbitMQUtils.readByteArray(taskBuffer);

            byte[][] taskDataArray = new byte[4][];
            taskDataArray[0] = RabbitMQUtils.writeString(taskRelation);
            taskDataArray[1] = RabbitMQUtils.writeString(format);
            taskDataArray[2] = RabbitMQUtils.writeString(path);
            taskDataArray[3] = targetData;
            byte[] taskData = RabbitMQUtils.writeByteArrays(taskDataArray);

            byte[] expectedAnswerData = task.getExpectedAnswers();

            // Send the task to the system (and store the timestamp)
            long timestamp = System.currentTimeMillis();
            sendTaskToSystemAdapter(taskId, taskData);
            LOGGER.info("Task " + taskId + " sent to System Adapter.");

            // Send the expected answer to the evaluation store
            sendTaskToEvalStorage(taskId, timestamp, expectedAnswerData);
            LOGGER.info("Expected answers of task " + taskId + " sent to Evaluation Storage.");

        } catch (Exception e) {
            LOGGER.error("Exception caught while reading the tasks and their expected answers", e);
        }
    }

    @Override
    public void close() throws IOException {
        LOGGER.info("Closign Task Generator...");
        
        super.close();
        LOGGER.info("Task Genererator closed successfully.");
    }
}
