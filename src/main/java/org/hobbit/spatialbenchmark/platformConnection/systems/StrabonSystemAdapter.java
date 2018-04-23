/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hobbit.spatialbenchmark.platformConnection.systems;

import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.ShutdownSignalException;
import eu.earthobservatory.runtime.postgis.Strabon;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import org.apache.commons.io.FileUtils;
import org.hobbit.core.Commands;
import org.hobbit.core.components.AbstractSystemAdapter;
import org.hobbit.core.rabbit.RabbitMQUtils;
import org.hobbit.core.rabbit.SimpleFileReceiver;
import org.hobbit.spatialbenchmark.rabbit.SingleFileReceiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import eu.earthobservatory.runtime.postgis.Strabon;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 *
 * @author jsaveta
 */
public class StrabonSystemAdapter extends AbstractSystemAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(StrabonSystemAdapter.class);
    protected File folder = new File("");
    private SimpleFileReceiver sourceReceiver;
    private SimpleFileReceiver targetReceiver;
    private String receivedGeneratedDataFilePath;
    private String dataFormat;
    private String taskFormat;
    private String resultsFile;
    private Strabon strabon = null; // An instance of Strabon
    String db = null; // Spatially enabled PostGIS database where data is stored 
    String user = null; // Username to connect to database
    String passwd = null; // Password to connect to database
    Integer port = null; // Database host to connect to
    String host = null; // Port to connect to on the database host
    String url = null;

    @Override
    public void init() throws Exception {
        LOGGER.info("Initializing Strabon test system...");
        long time = System.currentTimeMillis();
        super.init();
        LOGGER.info("Super class initialized. It took {}ms.", System.currentTimeMillis() - time);
        time = System.currentTimeMillis();
        sourceReceiver = SimpleFileReceiver.create(this.incomingDataQueueFactory, "source_file");
        LOGGER.info("Receivers initialized. It took {}ms.", System.currentTimeMillis() - time);
        LOGGER.info("Strabon initialized successfully.");

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
            receivedGeneratedDataFilePath = "./datasets/SourceDatasets/" + receivedFiles[0];
            LOGGER.info("Received data from receiveGeneratedData..");

        } catch (IOException | ShutdownSignalException | ConsumerCancelledException | InterruptedException ex) {
            java.util.logging.Logger.getLogger(SilkSystemAdapter.class.getName()).log(Level.SEVERE, null, ex);
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
//LOGGER.info("receivedFiles TASK " + Arrays.toString(receivedFiles));
                receivedGeneratedTaskFilePath = "./datasets/TargetDatasets/" + receivedFiles[0];

            } catch (Exception e) {
                LOGGER.error("Exception while trying to receive data. Aborting.", e);
            }
            LOGGER.info("Received task data. It took {}ms.", System.currentTimeMillis() - time);
            time = System.currentTimeMillis();

            LOGGER.info("Task " + taskId + " received from task generator");

            strabonController(receivedGeneratedDataFilePath, receivedGeneratedTaskFilePath, taskRelation);
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

    public void strabonController(String source, String target, String relation) {

        try {

            LOGGER.info("Started strabonController.. ");
            // docker file creates
            // user: postgres
            // with password: postgres
            // db: template_postgis
            // wiht port: 9999
            // url: jdbc:postgresql://localhost:9999/template_postgis  ?????

            //http://hg.strabon.di.uoa.gr/Strabon/file/9ee6935c722a/runtime/src/main/java/eu/earthobservatory/runtime/postgis
            
            String db = "template_postgis";
            String user = "postgres";
            String passwd = "postgres";
            Integer port = 9999;
            String host = "localhost";
            //String url = "jdbc:postgresql://localhost:9999/template_postgis";

            //initialize strabon - kanei kai connect auto?
            strabon = new Strabon(db, user, passwd, port, host, true);
            
            LOGGER.info("Strabon is initialized");
            //connect to strabon
           
            //http://hg.strabon.di.uoa.gr/Strabon/file/9ee6935c722a/runtime/src/main/java/eu/earthobservatory/runtime/postgis/StoreOp.java
            
            //store source dataset            
            Boolean inference = false;
            String graph = "sourceGraph";            
            strabon.storeInRepo(source, null, graph, dataFormat, inference);
            
            LOGGER.info("Source dataset is stored in graph named sourceGraph");
            
            //store target dataset
            graph = "targetGraph";            
            strabon.storeInRepo(target, null, graph, dataFormat, inference);
            LOGGER.info("Target dataset is stored in graph named targetGraph");
            

            //run query 

        } catch (Exception e) {
            LOGGER.info("Cannot initialize Strabon or cannot store datasets into graphs");
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String stacktrace = sw.toString();
            LOGGER.info("" + stacktrace);
        }

        LOGGER.info("strabonController finished..");
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
        LOGGER.info("Closing Strabon System Adapter...");
        // Always close the super class after yours!
        super.close();
        LOGGER.info("Strabon System Adapter closed successfully.");
    }
}
