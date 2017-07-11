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
import org.apache.commons.cli.CommandLine;
import org.apache.commons.io.FileUtils;
import org.hobbit.core.Commands;
import org.hobbit.core.components.AbstractSystemAdapter;
import org.hobbit.core.rabbit.RabbitMQUtils;
import org.hobbit.core.rabbit.SimpleFileReceiver;
import org.hobbit.spatialbenchmark.util.FileUtil;
import org.hobbit.spatialbenchmark.util.SesameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jsaveta
 */
public class SilkSystemAdapter extends AbstractSystemAdapter {
    //na anevei k system.ttl gia to silk
    //na exei to diko tou docker image
    //vale sto build tou docker kai ta 2 sustimata
    // sto experiment.ttl na valo to sosto sustima gia local kai sto test_cmd
    //na ftiakso ta configs gia ola ta relations.. 
    // na valo ta sosta paths edo

    private static final Logger LOGGER = LoggerFactory.getLogger(SilkSystemAdapter.class);
    private SimpleFileReceiver sourceReceiver;
    private SimpleFileReceiver targetReceiver;
    private String receivedGeneratedDataFilePath;
    private String dataFormat;
    private String taskFormat;
    private String resultsFile;

    @Override
    public void init() throws Exception {
        LOGGER.info("Initializing SILK test system...");
        super.init();
        LOGGER.info("SILK initialized successfully .");

    }

    @Override
    public void receiveGeneratedData(byte[] data) {
        try {
            LOGGER.info("Starting receiveGeneratedData..");

            ByteBuffer dataBuffer = ByteBuffer.wrap(data);
            // read the file path
            dataFormat = RabbitMQUtils.readString(dataBuffer);
            receivedGeneratedDataFilePath = RabbitMQUtils.readString(dataBuffer);

            sourceReceiver = SimpleFileReceiver.create(this.incomingDataQueueFactory, "source_file");
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
        try {

            ByteBuffer taskBuffer = ByteBuffer.wrap(data);
            // read the relation
            String taskRelation = RabbitMQUtils.readString(taskBuffer);
            LOGGER.info("taskRelation " + taskRelation);

            // read the file path
            taskFormat = RabbitMQUtils.readString(taskBuffer);
            String receivedGeneratedTaskFilePath = null;
            try {

                targetReceiver = SimpleFileReceiver.create(this.incomingDataQueueFactory, "task_target_file");
                String[] receivedFiles = targetReceiver.receiveData("./datasets/TargetDatasets/");
//LOGGER.info("receivedFiles 2 " + Arrays.toString(receivedFiles));
                receivedGeneratedTaskFilePath = "./datasets/TargetDatasets/" + receivedFiles[0];

            } catch (ShutdownSignalException | ConsumerCancelledException | InterruptedException ex) {
                java.util.logging.Logger.getLogger(LimesSystemAdapter.class.getName()).log(Level.SEVERE, null, ex);
            }

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
//na diavazei to file kai na kanei replace ola ta format kai tin kataliksi tou arxeiou 
        //na svinei apo to arxeio ta polu megala WKT 

        LOGGER.info("Started silkController.. ");

        String config = "./configs/topologicalConfigs/silkConfig" + relation + ".xml";
        String newConfig = "./configs/topologicalConfigs/silkNewConfig" + relation + ".xml";
        
        try {
            //replace format with the correct
            String content = FileUtils.readFileToString(new File(config), "UTF-8");
            content = content.replaceAll("N-TRIPLE", SesameUtils.parseRdfFormat(dataFormat).toString());
            content = content.replaceAll("mappings.nt", "./datasets/SilkSystemAdapterResults/"+relation+"mappings."+SesameUtils.parseRdfFormat(dataFormat).getDefaultFileExtension());
            content = content.replaceAll(".nt", "."+SesameUtils.parseRdfFormat(dataFormat).getDefaultFileExtension());
            
            File tempFile = new File(newConfig);
            FileUtils.writeStringToFile(tempFile, content, "UTF-8");
        } catch (IOException e) {
            //Simple exception handling, replace with what's necessary for your use case!
            throw new RuntimeException("Generating file failed", e);
        }
        
        //vale auto to path sto config
        resultsFile = "./datasets/SilkSystemAdapterResults/mappings." + SesameUtils.parseRdfFormat(dataFormat).getDefaultFileExtension();

        
        //Java streams can't store strings longer than 64 kbyte = 65536 byte so we trim lines
        //larger than 64KB 
        //awk 'length($0) < 65535' sourceCONTAINS-0001.nt > source-clear-for-silk.nt
        //awk 'length($0) < 65535' targetCONTAINS-0001.nt > target-clear-for-silk.nt
        
        Runtime.getRuntime().exec("awk 'length($0) < 65535' sourceCONTAINS-0001."+SesameUtils.parseRdfFormat(dataFormat).getDefaultFileExtension()+" > "
                + "source-clear-for-silk."+SesameUtils.parseRdfFormat(dataFormat).getDefaultFileExtension());

        Runtime.getRuntime().exec("awk 'length($0) < 65535' targetCONTAINS-0001."+SesameUtils.parseRdfFormat(dataFormat).getDefaultFileExtension()+" > "
                + "target-clear-for-silk."+SesameUtils.parseRdfFormat(dataFormat).getDefaultFileExtension());

        //run silk 
        // java -DconfigFile=silkConfigCONTAINS.xml -jar ./lib/silk.jar 
        
        //make sure you copy the lib folder in the docker image
        Runtime.getRuntime().exec("java -DconfigFile=silkConfig"+relation+".xml -jar ./lib/silk.jar ");
        LOGGER.info("silkController finished..");
    }

    @Override
    public void receiveCommand(byte command, byte[] data) {
        if (Commands.DATA_GENERATION_FINISHED == command) {
            LOGGER.info("my receiveCommand for source");
            sourceReceiver.terminate();

        } else if (Commands.TASK_GENERATION_FINISHED == command) {
            LOGGER.info("my receiveCommand for target");
            targetReceiver.terminate();
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
