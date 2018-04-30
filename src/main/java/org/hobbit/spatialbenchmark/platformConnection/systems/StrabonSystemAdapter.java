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
import org.hobbit.core.Commands;
import org.hobbit.core.components.AbstractSystemAdapter;
import org.hobbit.core.rabbit.RabbitMQUtils;
import org.hobbit.core.rabbit.SimpleFileReceiver;
import org.hobbit.spatialbenchmark.rabbit.SingleFileReceiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import eu.earthobservatory.runtime.postgis.Strabon;
//import eu.earthobservatory.utils.Format;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Properties;
import org.hobbit.spatialbenchmark.util.SesameUtils;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResultHandlerException;
import org.openrdf.rio.RDFFormat;
//import org.openrdf.sail.postgis.PostGISSqlStore;

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
//    private Strabon strabon = null; // An instance of Strabon
    private String db = null; // Spatially enabled PostGIS database where data is stored 
    private String user = null; // Username to connect to database
    private String passwd = null; // Password to connect to database
    private Integer port = null; // Database host to connect to
    private String host = null; // Port to connect to on the database host
    private String url = null;
    private static String PREFIXES
            = "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n"
            + "PREFIX tomtom: <http://www.tomtom.com/ontologies/traces#>\n"
            + "PREFIX tomtomregions: <http://www.tomtom.com/ontologies/regions#>\n"
            + "PREFIX spaten: <http://www.spaten.com/ontologies/traces#>\n"
            + "PREFIX spatenregions: <http://www.spaten.com/ontologies/regions#>\n"
            + "PREFIX strdf: <http://strdf.di.uoa.gr/ontology#>\n"; //fill this and append the query 

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
//            java.util.logging.Logger.getLogger(SilkSystemAdapter.class.getName()).log(Level.SEVERE, null, ex);
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
            String postgresqlContName = this.createContainer("git.project-hobbit.eu:4567/jsaveta1/strabonsystemadapter/strabon:latest", envVariablesPostgresql);
            LOGGER.info("postgresqlContName " + postgresqlContName);

            //connect to strabon
            LOGGER.info("[Strabon] store source and target files to database");
//            Class.forName("org.postgresql.Driver");
//            url = "jdbc:postgresql://" + postgresqlContName + ":" + port + "/" + db + "?user=" + user + "&password=" + passwd + "&application_name=strabon";
//            Connection con = DriverManager.getConnection(url);


//          http://www.strabon.di.uoa.gr/files/stSPARQL_tutorial.pdf
            
            String[] command = {"/bin/sh", "-c", "cd Strabon/endpoint/jars/target && java -cp $(for file in ‘ls -1 *.jar‘; do myVar=$myVar./$file\":\";\n"
                + "done;echo $myVar;) eu.earthobservatory.runtime.postgis.StoreOp " + postgresqlContName + " "+port+" "+db+" \n"
                + " "+user+ " " +passwd  + " " + source + " " + taskFormat +" "+ G1 + " && java -cp $(for file in ‘ls -1 *.jar‘; do myVar=$myVar./$file\":\";\n"
                + "done;echo $myVar;) eu.earthobservatory.runtime.postgis.StoreOp " + postgresqlContName + " "+port+" "+db+" \n"
                + " "+user+ " " +passwd  + " " + target + " " + taskFormat + G2};
            Process p = Runtime.getRuntime().exec(command);
            LOGGER.info("[Strabon] store completed in source (G1) and target (G2) graphs");
                        
            String line;
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((line = input.readLine()) != null) {
                LOGGER.info("* "+line);
            }
            input.close();

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

//    public void start() {
//
//        //sudo /etc/init.d/postgresql restart
//        //      /etc/postgresql
//        String[] start_postgres = {"/bin/bash", "-c", "service postgresql start"};
//
//        Process pr;
//
//        try {
//            LOGGER.info("Starting Strabon (Postgres) ...");
//
//            pr = new ProcessBuilder(start_postgres).redirectErrorStream(true).start();
//            BufferedReader in = new BufferedReader(new InputStreamReader(pr.getInputStream()));
//            String line;
//            while ((line = in.readLine()) != null) {
//                LOGGER.info(line);
//            }
//            pr.waitFor();
//            if (pr.exitValue() != 0) {
//                LOGGER.error("Something went wrong while starting postgres");
//
//            }
//            strabon = new Strabon(db, user, passwd, port, host, true);
//            LOGGER.info("Strabon (Postgres) started");
//        } catch (Exception e) {
//            LOGGER.info("Cannot start Strabon");
//            StringWriter sw = new StringWriter();
//            e.printStackTrace(new PrintWriter(sw));
//            String stacktrace = sw.toString();
//            LOGGER.info(stacktrace);
//        }
//    }
//
//    // Clear caches (to be able to measure times for cold and warm caches)
//    public void clearCaches() {
//
//        String[] stop_postgres = {"/bin/bash", "-c", "service postgresql stop"};
//        String[] clear_caches = {"/bin/bash", "-c", "sync && echo 3 > /proc/sys/vm/drop_caches"};
//        String[] start_postgres = {"/bin/bash", "-c", "service postgresql start"};
//
//        Process pr;
//
//        try {
//            LOGGER.info("Clearing caches...");
//
//            pr = Runtime.getRuntime().exec(stop_postgres);
//            pr.waitFor();
//            if (pr.exitValue() != 0) {
//                LOGGER.error("Something went wrong while stoping postgres");
//            }
//
//            pr = Runtime.getRuntime().exec(clear_caches);
//            pr.waitFor();
//            if (pr.exitValue() != 0) {
//                LOGGER.error("Something went wrong while clearing caches");
//            }
//
//            pr = Runtime.getRuntime().exec(start_postgres);
//            pr.waitFor();
//            if (pr.exitValue() != 0) {
//                LOGGER.error("Something went wrong while clearing caches");
//            }
//
//            Thread.sleep(5000);
//            LOGGER.info("Caches cleared");
//        } catch (Exception e) {
//            LOGGER.info("Cannot clear caches");
//            StringWriter sw = new StringWriter();
//            e.printStackTrace(new PrintWriter(sw));
//            String stacktrace = sw.toString();
//            LOGGER.info(stacktrace);
//        }
//    }
//
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

    }
}
