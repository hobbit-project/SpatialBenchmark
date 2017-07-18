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
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
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
public class SilkSystemAdapter extends AbstractSystemAdapter {

    private static final Logger LOGGER_SILK = LoggerFactory.getLogger(org.silkframework.execution.GenerateLinks.class);
    private static final Logger LOGGER = LoggerFactory.getLogger(SilkSystemAdapter.class);
    protected File folder = new File("");
    private SimpleFileReceiver sourceReceiver;
    private SimpleFileReceiver targetReceiver;
    private String receivedGeneratedDataFilePath;
    private String dataFormat;
    private String taskFormat;
    private String resultsFile;

    @Override
    public void init() throws Exception {
        LOGGER.info("Initializing Silk test system...");
        long time = System.currentTimeMillis();
        super.init();
        LOGGER.info("Super class initialized. It took {}ms.", System.currentTimeMillis() - time);
        time = System.currentTimeMillis();
        sourceReceiver = SimpleFileReceiver.create(this.incomingDataQueueFactory, "source_file");
        LOGGER.info("Receivers initialized. It took {}ms.", System.currentTimeMillis() - time);
        LOGGER.info("Silk initialized successfully.");

    }

    @Override
    public void receiveGeneratedData(byte[] data) {
        try {
            LOGGER.info("Starting receiveGeneratedData..");

            ByteBuffer dataBuffer = ByteBuffer.wrap(data);
            // read the file path
            dataFormat = RabbitMQUtils.readString(dataBuffer);
            receivedGeneratedDataFilePath = RabbitMQUtils.readString(dataBuffer);

            
            String[] receivedFiles = sourceReceiver.receiveData("./datasets/SourceDatasets/");
//LOGGER.info("receivedFiles 1 " + Arrays.toString(receivedFiles));
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

            // read the file path
            taskFormat = RabbitMQUtils.readString(taskBuffer);
            LOGGER.info("Parsed task " + taskId + ". It took {}ms.", System.currentTimeMillis() - time);
            time = System.currentTimeMillis();
            
            String receivedGeneratedTaskFilePath = null;
            try {

                targetReceiver = SingleFileReceiver.create(this.incomingDataQueueFactory,
                    "task_target_file");
                String[] receivedFiles = targetReceiver.receiveData("./datasets/TargetDatasets/");
//LOGGER.info("receivedFiles 2 " + Arrays.toString(receivedFiles));
                receivedGeneratedTaskFilePath = "./datasets/TargetDatasets/" + receivedFiles[0];

            } catch (Exception e) {
                 LOGGER.error("Exception while trying to receive data. Aborting.", e);
            }
            LOGGER.info("Received task data. It took {}ms.", System.currentTimeMillis() - time);
            time = System.currentTimeMillis();


            LOGGER.info("Task " + taskId + " received from task generator");

            silkController(receivedGeneratedDataFilePath, receivedGeneratedTaskFilePath, taskRelation);
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
            java.util.logging.Logger.getLogger(SilkSystemAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void silkController(String source, String target, String relation) throws IOException {

        try {

            LOGGER.info("Started silkController.. ");
            String config = "./configs/topologicalConfigs/silkConfig" + relation + ".xml";
            String newConfig = "./configs/topologicalConfigs/silkNewConfig" + relation + ".xml";

            try {
                resultsFile = "./datasets/SilkSystemAdapterResults/" + relation + "mappings." + SesameUtils.parseRdfFormat(dataFormat).getDefaultFileExtension();

                String content = FileUtils.readFileToString(new File(config), "UTF-8");
                content = content.replaceAll("source-clear-for-silk.nt", "../../" + source);
                content = content.replaceAll("target-clear-for-silk.nt", "../../" + target);
//                content = content.replaceAll("N-TRIPLE", SesameUtils.parseRdfFormat(dataFormat).toString());
//                content = content.replaceAll("N-TRIPLES", "N-TRIPLE");

//  TODO             CORRECT FORMAT.. Now only gets nt, find how the have defined it
//or maybe they only return .nt resutls! I should fix that if so
                content = content.replaceAll("mappings.nt", "../../" + resultsFile);

                File tempFile = new File(newConfig);
                FileUtils.writeStringToFile(tempFile, content, "UTF-8");

//                LOGGER.info("------------CONFIG");
//                try (BufferedReader br = new BufferedReader(new FileReader(newConfig))) {
//                    String line = null;
//                    while ((line = br.readLine()) != null) {
//                        LOGGER.info("" + line);
//                    }
//                }
            } catch (IOException e) {
                throw new RuntimeException("Generating file failed", e);
            }

            Process p = Runtime.getRuntime().exec("java -DconfigFile=" + newConfig + "  -jar ./lib/silk.jar ");
            p.waitFor();

            LOGGER_SILK.info(IOUtils.toString(p.getInputStream()));
            LOGGER_SILK.info(IOUtils.toString(p.getErrorStream()));

            //delete cache folder 
            File folder = new File("./cache/");
            FileUtil.removeDirectory(folder);

            LOGGER.info("silkController finished..");

        } catch (InterruptedException ex) {
            java.util.logging.Logger.getLogger(SilkSystemAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }

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