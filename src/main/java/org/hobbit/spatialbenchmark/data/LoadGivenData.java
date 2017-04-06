///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package org.hobbit.spatialbenchmark.data;
//
//import com.rabbitmq.client.ConsumerCancelledException;
//import com.rabbitmq.client.ShutdownSignalException;
//import java.io.File;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//import org.hobbit.spatialbenchmark.data.cache.FileCache;
//import org.hobbit.spatialbenchmark.main.Main;
//import org.hobbit.spatialbenchmark.properties.Configurations;
//import org.hobbit.spatialbenchmark.util.FileUtil;
//import org.openrdf.repository.Repository;
//import org.openrdf.repository.RepositoryConnection;
//import org.openrdf.repository.RepositoryException;
//import org.openrdf.repository.sail.SailRepository;
//import org.openrdf.rio.RDFFormat;
//import org.openrdf.rio.RDFParseException;
//import org.openrdf.sail.memory.MemoryStore;
//
///**
// *
// * @author jsaveta
// */
//public class LoadGivenData {
//
//    //load data in memory 
//    public Repository LoadDatasetsFromFile() throws IOException, ShutdownSignalException, ConsumerCancelledException, InterruptedException, RepositoryException, RDFParseException {
////        new MimickingAlg();
//        //data from mimicking algorithm
//        //Receiving data
////            https://github.com/hobbit-project/platform/wiki/Transferring-mimicked-data
////            String queueName = System.getenv().get(Constants.DATA_QUEUE_NAME_KEY);
////            System.out.println("queueName " +queueName);
////            SimpleFileReceiver receiver = SimpleFileReceiver.create(this, queueName);
////            String[] receivedFiles = receiver.receiveData(Configurations.GIVEN_DATASETS_PATH);            
////            System.out.println("receivedFiles " +Arrays.toString(receivedFiles));
//
//        //Integration into the data generator
////        DockerBasedMimickingAlg alg = new DockerBasedMimickingAlg(this, "podigg/podigg-lc-hobbit");
////        try {
////            alg.generateData(getDATA_GENERATOR_OUTPUT_DATASET(), arguments);
////        } catch (Exception e) {
////            e.printStackTrace();
////        }
//        //file format to load
//        RDFFormat format = RDFFormat.TURTLE; //to format tha diaforetiko, analoga to retrieve apo to mimicking?
//        String path = Main.getConfigurations().getString(Configurations.GIVEN_DATASETS_PATH);
//        List<File> collectedFiles = new ArrayList<File>();
//
//        Repository repository = new SailRepository(new MemoryStore());
//        RepositoryConnection connection = null;
//
//        repository.initialize();
//        connection = repository.getConnection();
//
//        //each file is an instance
//        FileUtil.collectFilesList(path, collectedFiles, "*", true);
//        //List<File> files = collectedFiles;
//
//        //episis auto mallon de tha einai etsi, diladi ana file! 
//        // na kano generate me to mimicking osa xreiazomai kai retrieve ola?
//        //episis na ta kratao stin cache?! pos to exoun kanei sto limes?
//        //i kleanthi pos to exei kanei?
//        int size = Main.getConfigurations().getInt(Configurations.INSTANCES);
//        if ((Main.getConfigurations().getInt(Configurations.INSTANCES) > collectedFiles.size()) || (Main.getConfigurations().getInt(Configurations.INSTANCES) == 0)) {
//            size = collectedFiles.size();
//        }
//
//        for (int i = 0; i < size; i++) {
//            
//            connection.add(collectedFiles.get(i), "", format);
//            System.out.println("i "+i+" "+collectedFiles.get(i).getName());
//        }
//
//        return repository;
//
//    }
//
//}
