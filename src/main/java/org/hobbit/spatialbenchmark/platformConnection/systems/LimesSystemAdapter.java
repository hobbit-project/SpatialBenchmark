/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hobbit.spatialbenchmark.platformConnection.systems;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.hobbit.core.components.AbstractSystemAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jsaveta
 */
public class LimesSystemAdapter extends AbstractSystemAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(LimesSystemAdapter.class);


    /*  http://aksw.github.io/LIMES-dev/user_manual/running_limes.html
    * java -jar  limes-core-1.0.0-SNAPSHOT.jar limesConfig.xml 
     */
    @Override
    public void init() throws Exception {
        super.init();
//        runRadon();

//pare ta configs opos sto radon controller, kanta copy meso tou docker sto workdir
    }

    @Override
    public void receiveGeneratedData(byte[] data) {

        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(data);

            // get the received file name
            int fileNameLength = bis.read();
            byte[] fileNameBytes = new byte[fileNameLength];
            bis.read(fileNameBytes, 0, fileNameLength);
            String receivedFilePath = new String(fileNameBytes);

            // get the received file content
            int contentLength = bis.available();
            byte[] fileContentBytes = new byte[contentLength];
            bis.read(fileContentBytes, 0, contentLength);

            FileOutputStream fos = null;

            File outputFile = new File(receivedFilePath);
            fos = FileUtils.openOutputStream(outputFile, false);
            IOUtils.write(fileContentBytes, fos);
            LOGGER.info(receivedFilePath + " received from Data Generator");

            LOGGER.info(dataGen2SystemQueue.messageCount() + " available messages.");
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(LimesSystemAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void receiveGeneratedTask(String string, byte[] data) {
        LOGGER.info("MPIKE STO RECEIVE TWN GENERATED TASKS APO TO SYSTEM ADAPTER");

        // read the sparql query
        String task = SerializationUtils.deserialize(data);

        //des kai tis kleanthis to sys adapter
        // TODO: execute the query and send the results to evaluation storage
    }

    @Override
    public void close() throws IOException {
        LOGGER.info("MPIKE STO CLOSE TOU SYSTEM ADAPTER");
        // Always close the super class after yours!
        super.close();
    }
}
