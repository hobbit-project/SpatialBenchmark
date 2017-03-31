/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hobbit.spatialbenchmark.platformConnection.systems;

import org.hobbit.core.components.AbstractSystemAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jsaveta
 */
public class LimesSystemAdapter extends AbstractSystemAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(LimesSystemAdapter.class);

    @Override
    public void init() throws Exception {
        super.init();
//        runLimes();
    }

    private void runLimes() {

    }

    @Override
    public void receiveGeneratedData(byte[] arg0) {

        //grapse sto config to sosto onoma file
        //exei noima etsi? 
        // mipos to byte [] tha exei ena endpoint etsi ki allios stin platforma oste na dino auto? 
        //tha trexo to docker tou limes, auto tha kalei tin klasi, i klasi kalei to jar,
        //ta apotelesmata tou limes einai pali se byte array to opoio stelenetai sto evaluation module
        // apo to receiveGeneratedTask ? h' prepei na to stelno ego?
//        BytesToFile(arg0, "source");

    }

    @Override
    public void receiveGeneratedTask(String arg0, byte[] arg1) {
//        try {
//            String taskId = arg0;
//            BytesToFile(arg1, "target");
//
//            byte[] a = FileUtil.loadByteData("./src/main/resources/absolute_mapping_value.nt");
//            byte[] answers = RabbitMQUtils.writeByteArrays(new byte[][]{a});
//
//            this.sendResultToEvalStorage(taskId, answers);
//        } catch (IOException ex) {
//            java.util.logging.Logger.getLogger(LimesSystemAdapter.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }

    public void BytesToFile(byte[] array, String filename) {
//        RDFFormat rdfFormat = SesameUtils.parseRdfFormat(Main.getConfigurations().getString(Configurations.GENERATED_DATA_FORMAT));
//
//        //sto config einai .nt prepei auto na antikathistate, opos kai to id logw tis cache
//        File outputFile = new File("./src/main/resources/" + filename + "." + rdfFormat.getDefaultFileExtension());
//        try (FileOutputStream outputStream = new FileOutputStream(outputFile);) {
//            outputStream.write(array);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

    }

}
