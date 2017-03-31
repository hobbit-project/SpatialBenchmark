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
import org.hobbit.spatialbenchmark.util.FileUtil;
import org.openrdf.rio.RDFFormat;

/**
 *
 * @author jsaveta
 */
public class RADONController extends Controller {

    private ResultMappings mappings;

    public RADONController(RDFFormat rdfFormat) {

        String[] args = new String[1];
        args[0] = "./src/main/resources/topologicalConfigs/config" + RELATION + ".xml";
        CommandLine cmd = parseCommandLine(args);
        Configuration config = getConfig(cmd);
        config.getSourceInfo().setEndpoint(config.getSourceInfo().getEndpoint() + "." + rdfFormat.getDefaultFileExtension());
        config.getSourceInfo().setType(rdfFormat.getDefaultFileExtension().toUpperCase());
        config.getTargetInfo().setEndpoint(config.getTargetInfo().getEndpoint() + "." + rdfFormat.getDefaultFileExtension());
        config.getTargetInfo().setType(rdfFormat.getDefaultFileExtension().toUpperCase());

        //keep mappings for the oaei format
        mappings = getMapping(config);
        writeResults(mappings, config);

        //delete cache folder 
        File folder = new File("./cache/");
        FileUtil.removeDirectory(folder);
    }

    //fix this: make public on jar
    private static void writeResults(ResultMappings mappings, Configuration config) {
        String outputFormat = config.getOutputFormat();
        ISerializer output = SerializerFactory.createSerializer(outputFormat);
        output.setPrefixes(config.getPrefixes());
        output.writeToFile(mappings.getVerificationMapping(), config.getVerificationRelation(),
                config.getVerificationFile());
        output.writeToFile(mappings.getAcceptanceMapping(), config.getAcceptanceRelation(), config.getAcceptanceFile());
    }

    public ResultMappings getMappings() {
        return this.mappings;
    }

}
