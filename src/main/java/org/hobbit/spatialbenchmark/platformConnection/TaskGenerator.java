/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hobbit.spatialbenchmark.platformConnection;

import java.util.Arrays;
import org.apache.commons.lang3.SerializationUtils;
import org.hobbit.core.components.AbstractTaskGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jsaveta
 */
public class TaskGenerator extends AbstractTaskGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskGenerator.class);

    @Override
    public void init() throws Exception {
        LOGGER.info("MPIKE STO INIT TOU TASK GENERATOR");

        super.init();

        LOGGER.info("TELEIWSE TO INIT TOU TASK GENERATOR");
    }

    @Override
    protected void generateTask(byte[] data) throws Exception {
        try {
            LOGGER.info("MPIKE STO generateTask TOU TASK GENERATOR");
            // Create tasks based on the incoming data inside this method.
            // You might want to use the id of this task generator and the
            // number of all task generators running in parallel.
//        int dataGeneratorId = getGeneratorId();
//        int numberOfGenerators = getNumberOfGenerators();
            // Create an ID for the task
            String taskId = getNextTaskId();
            LOGGER.info("taskId " + taskId);

            Task task = (Task) SerializationUtils.deserialize(data);
            LOGGER.info("Task " + task.getTaskId() + " received from Data Generator");

            byte[] taskData = task.getTarget();
            
            LOGGER.info("target sto taskData");
            LOGGER.info("eimai ston task generator TASK DATA " + Arrays.toString(taskData));
            
            byte[] expectedAnswerData = task.getExpectedAnswers();
            
            LOGGER.info("eimai ston task generator GS DATA " + Arrays.toString(expectedAnswerData));
            LOGGER.info("GS sto expectedAnswerData");
            // Send the task to the system (and store the timestamp)
            long timestamp = System.currentTimeMillis();
            sendTaskToSystemAdapter(taskId, taskData);
            LOGGER.info("EKANA TO sendTaskToSystemAdapter(taskId, taskData)");

            // Send the expected answer to the evaluation store
            sendTaskToEvalStorage(taskId, timestamp, expectedAnswerData);
            LOGGER.info("sendTaskToEvalStorage(taskId, timestamp, expectedAnswerData)");
        } catch (Exception e) {
            LOGGER.error("Exception caught while reading the tasks and their expected answers", e);
        }
        LOGGER.info("TELEIWSE TO generateTask TOU TASK GENERATOR");
    }

//    @Override
//    public void close() throws IOException {
//        // Free the resources you requested here
//        // Always close the super class after yours!
//        super.close();
//    }
}
