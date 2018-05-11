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
import org.hobbit.core.Commands;
import org.hobbit.core.components.AbstractSystemAdapter;
import org.hobbit.core.rabbit.RabbitMQUtils;
import org.hobbit.core.rabbit.SimpleFileReceiver;
import org.hobbit.spatialbenchmark.rabbit.SingleFileReceiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

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
    private String db = null; // Spatially enabled PostGIS database where data is stored 
    private String user = null; // Username to connect to database
    private String passwd = null; // Password to connect to database
    private Integer port = null; // Database host to connect to
    private String host = null; // Port to connect to on the database host
    private String url = null;

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
            java.util.logging.Logger.getLogger(StrabonSystemAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void receiveGeneratedTask(String taskId, byte[] data) {
        LOGGER.info("Starting receiveGeneratedTask..");
        LOGGER.info("Task " + taskId + " received from task generator");
        long time = System.currentTimeMillis();
//        try {

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
//            byte[][] resultsArray = new byte[1][];
//
//            resultsArray[0] = FileUtils.readFileToByteArray(new File(resultsFile));
//            byte[] results = RabbitMQUtils.writeByteArrays(resultsArray);
//            try {
//
//                sendResultToEvalStorage(taskId, results);
//                LOGGER.info("Results sent to evaluation storage.");
//            } catch (IOException e) {
//                LOGGER.error("Exception while sending storage space cost to evaluation storage.", e);
//            }
//        } catch (IOException ex) {
//            java.util.logging.Logger.getLogger(StrabonSystemAdapter.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }

    public void strabonController(String source, String target, String relation) {
        LOGGER.info("Started strabonController.. ");
        String db = "endpoint";
        String user = "postgres";
        String passwd = "postgres";
        Integer port = 5432;
        String G1 = "http://G1";
        String G2 = "http://G2";

        try {
            String[] envVariablesPostgresql = new String[]{};
            String postgresqlContName = "localhost"; //this.createContainer("git.project-hobbit.eu:4567/jsaveta1/strabonsystemadapter/strabon:latest", envVariablesPostgresql);
            LOGGER.info("postgresqlContName " + postgresqlContName);

            //////////////////////////////////
            LOGGER.info("Storing source dataset into G1 graph..");
            source = System.getProperty("user.dir") + File.separator + source.replace("./", "");
            String storeScriptFilePath = System.getProperty("user.dir") + File.separator + "store.sh";
            String[] storeG1 = {"/bin/bash", storeScriptFilePath, postgresqlContName, port.toString(), db, user, passwd, source, taskFormat, G1};

            Process pG1 = new ProcessBuilder(storeG1).redirectErrorStream(true).start(); //= Runtime.getRuntime().exec(command);
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(pG1.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(pG1.getErrorStream()));

            LOGGER.info("Here is the standard output of the command for storing source dataset into G1 graph:\n");
            String out = null;
            while ((out = stdInput.readLine()) != null) {
                LOGGER.info(out);
            }

            // read any errors from the attempted command
            LOGGER.info("Here is the standard error of the command for storing source dataset into G1 graph (if any):\n");
            while ((out = stdError.readLine()) != null) {
                LOGGER.info(out);
            }

            pG1.waitFor();
            stdInput.close();
            stdInput.close();
            LOGGER.info("Store source dataset into G1 graph completed...");

            //////////////////////////////////
            LOGGER.info("Storing target dataset into G2 graph..");

            target = System.getProperty("user.dir") + File.separator + target.replace("./", "");
            String[] storeG2 = {"/bin/bash", storeScriptFilePath, postgresqlContName, port.toString(), db, user, passwd, target, taskFormat, G2};

            Process pG2 = new ProcessBuilder(storeG2).redirectErrorStream(true).start(); //= Runtime.getRuntime().exec(command);
            stdInput = new BufferedReader(new InputStreamReader(pG2.getInputStream()));
            stdError = new BufferedReader(new InputStreamReader(pG2.getErrorStream()));

            LOGGER.info("Here is the standard output of the command for storing target dataset into G2 graph:\n");
            out = null;
            while ((out = stdInput.readLine()) != null) {
                LOGGER.info(out);
            }

            // read any errors from the attempted command
            LOGGER.info("Here is the standard error of the command for storing target dataset into G2 graph (if any):\n");
            while ((out = stdError.readLine()) != null) {
                LOGGER.info(out);
            }

            pG2.waitFor();
            stdInput.close();
            stdInput.close();
            LOGGER.info("Store target dataset into G2 graph completed...");

            //////////////////////////////////
            LOGGER.info("Query for " + relation + " relation between source and target graphs (G1, G2)..");
            String queryScriptFilePath = System.getProperty("user.dir") + File.separator + "query.sh";
            String[] queryCMD = {"/bin/bash", queryScriptFilePath, postgresqlContName, port.toString(), db, user,passwd, G1, G2, relation};
            
            Process pQ = new ProcessBuilder(queryCMD).redirectErrorStream(true).start(); //= Runtime.getRuntime().exec(command);
            stdInput = new BufferedReader(new InputStreamReader(pQ.getInputStream()));
            stdError = new BufferedReader(new InputStreamReader(pQ.getErrorStream()));

            LOGGER.info("Here is the standard output of the command for the query:\n");
            out = null;
            while ((out = stdInput.readLine()) != null) {
                LOGGER.info(out);
            }

            // read any errors from the attempted command
            LOGGER.info("Here is the standard error of the command for the query (if any):\n");
            while ((out = stdError.readLine()) != null) {
                LOGGER.info(out);
            }

            pQ.waitFor();
            stdInput.close();
            stdInput.close();
            LOGGER.info("Query for " + relation + " relation between source and target graphs (G1, G2) completed...");

            //TODO: save results in file.. 
            
            LOGGER.info("[Strabon] Store and Query to Strabon completed.");

        } catch (Exception e) {
            LOGGER.error("[Strabon] Error during connection or store.", e);
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String stacktrace = sw.toString();
            LOGGER.info("-> " + stacktrace);
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

    public static void copy(String sourcePath, String destinationPath) throws IOException {
        Files.copy(Paths.get(sourcePath), new FileOutputStream(destinationPath));
    }

    private static void help() {
        LOGGER.info("Usage: eu.earthobservatory.runtime.postgis.StoreOp <HOST> <PORT> <DATABASE> <USERNAME> <PASSWORD> <FILE> [-f <FORMAT>] [-g <NAMED_GRAPH>] [-i <INFERENCE>]");
        LOGGER.info("       where <HOST>       		 is the postgis database host to connect to");
        LOGGER.info("             <PORT>       		 is the port to connect to on the database host");
        LOGGER.info("             <DATABASE>   		 is the spatially enabled postgis database that Strabon will use as a backend");
        LOGGER.info("             <USERNAME>   		 is the username to use when connecting to the database");
        LOGGER.info("             <PASSWORD>   		 is the password to use when connecting to the database");
        LOGGER.info("             <FILE>       		 is the file to be stored");
        LOGGER.info("             [-f <FORMAT>] 		 is the format of the file (default: NTRIPLES)");
        LOGGER.info("             [-g <NAMED_GRAPH>]  is the URI of the named graph to store the input file (default: default graph)");
        LOGGER.info("             [-i <INFERENCE>] 	 is true when inference is enabled (default: false)");

        LOGGER.info("Usage: eu.earthobservatory.runtime.postgis.QueryOp <HOST> <PORT> <DATABASE> <USERNAME> <PASSWORD> <QUERY> ");
        LOGGER.info("       where <HOST>       is the postgis database host to connect to");
        LOGGER.info("             <PORT>       is the port to connect to on the database host");
        LOGGER.info("             <DATABASE>   is the spatially enabled postgis database that Strabon will use as a backend, ");
        LOGGER.info("             <USERNAME>   is the username to use when connecting to the database ");
        LOGGER.info("             <PASSWORD>   is the password to use when connecting to the database");
        LOGGER.info("             <QUERY>      is the stSPARQL query to evaluate.");
        LOGGER.info("             <DELET_LOCK> is true when deletion of \"locked\" table should be enforced (e.g., when Strabon has been ungracefully shutdown).");
        LOGGER.info("             [<FORMAT>]   is the format of your results (default: XML)");
    }

}
