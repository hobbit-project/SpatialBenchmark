/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hobbit.spatialbenchmark.platformConnection;

import java.io.IOException;
import org.apache.commons.lang.SerializationUtils;
import org.hobbit.core.components.AbstractTaskGenerator;
import org.hobbit.core.rabbit.RabbitMQUtils;
import org.hobbit.spatialbenchmark.main.Main;
import org.hobbit.spatialbenchmark.properties.Configurations;
import org.hobbit.spatialbenchmark.util.FileUtil;
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
        LOGGER.info("TaskGenerator init before super");
        super.init();
        LOGGER.info("TaskGenerator init after super");
    }

    @Override
    protected void generateTask(byte[] data) throws Exception {

        LOGGER.info("TaskGenerator generateTask");

        int dataGeneratorId = getGeneratorId();
        int numberOfGenerators = getNumberOfGenerators();
        // Create an ID for the task
        String taskId = getNextTaskId();

        // Create the task and the expected answer
        byte[] t = FileUtil.loadByteData(Main.getConfigurations().getString(Configurations.DATASETS_PATH) + "/TargetDatasets");
        byte[] e = FileUtil.loadByteData(Main.getConfigurations().getString(Configurations.DATASETS_PATH) + "/GoldStandards");

        byte[] taskData = RabbitMQUtils.writeByteArrays(new byte[][]{t});
        byte[] expectedAnswerData = RabbitMQUtils.writeByteArrays(new byte[][]{e});

        // Send the task to the system (and store the timestamp)
        long timestamp = System.currentTimeMillis();
        sendTaskToSystemAdapter(taskId, taskData);

        // Send the expected answer to the evaluation store
        sendTaskToEvalStorage(taskId, timestamp, expectedAnswerData);
    }
    @Override
    public void close() throws IOException {
        // Free the resources you requested here
        //...

        // Always close the super class after yours!
        super.close();
    }

}
