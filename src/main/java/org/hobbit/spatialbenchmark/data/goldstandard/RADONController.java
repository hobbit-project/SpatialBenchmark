/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hobbit.spatialbenchmark.data.goldstandard;

import java.io.File;
import org.aksw.limes.core.controller.Controller;
import org.aksw.limes.core.controller.ResultMappings;
import org.aksw.limes.core.io.config.Configuration;
import org.aksw.limes.core.io.serializer.ISerializer;
import org.aksw.limes.core.io.serializer.SerializerFactory;
import org.apache.commons.cli.CommandLine;
import static org.hobbit.spatialbenchmark.data.AbstractWorker.RELATION;
import org.hobbit.spatialbenchmark.data.Generator;
import org.hobbit.spatialbenchmark.properties.Configurations;
import org.hobbit.spatialbenchmark.util.FileUtil;
import org.openrdf.rio.RDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jsaveta
 */
public class RADONController extends Controller {

    private static final Logger LOGGER = LoggerFactory.getLogger(RADONController.class);

    private ResultMappings mappings;

    public RADONController(RDFFormat rdfFormat) {
        String[] args = new String[1];
        args[0] = Generator.configurations.getString(Configurations.DATASETS_PATH) + File.separator + "topologicalConfigs/config" + RELATION + ".xml";

        //den vlepei to path tou resources 
//        args[0] = "topologicalConfigs/config" + RELATION + ".xml";
        CommandLine cmd = parseCommandLine(args);
        Configuration config = getConfig(cmd);
        config.getSourceInfo().setEndpoint(config.getSourceInfo().getEndpoint() + "." + rdfFormat.getDefaultFileExtension());
        config.getSourceInfo().setType(rdfFormat.getDefaultFileExtension().toUpperCase());
        config.getTargetInfo().setEndpoint(config.getTargetInfo().getEndpoint() + "." + rdfFormat.getDefaultFileExtension());
        config.getTargetInfo().setType(rdfFormat.getDefaultFileExtension().toUpperCase());

        config.setAcceptanceFile(Generator.configurations.getString(Configurations.DATASETS_PATH) + File.separator + "GoldStandards" + File.separator + RELATION + "mappings.nt");

//        System.out.println("config " + config.toString());
        //keep mappings for the oaei format
        mappings = getMapping(config);
        writeResults(mappings, config);

        //delete cache folder 
        File folder = new File("./cache/");
        FileUtil.removeDirectory(folder);
        

    }

    //prepei na ginei public sto jar?
    private static void writeResults(ResultMappings mappings, Configuration config) {
        String outputFormat = config.getOutputFormat();
        ISerializer output = SerializerFactory.createSerializer(outputFormat);
        output.setPrefixes(config.getPrefixes());
//        output.writeToFile(mappings.getVerificationMapping(), config.getVerificationRelation(),
//                config.getVerificationFile());
        output.writeToFile(mappings.getAcceptanceMapping(), config.getAcceptanceRelation(), config.getAcceptanceFile());
    }

    public ResultMappings getMappings() {
        return this.mappings;
    }

}
