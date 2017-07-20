/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hobbit.spatialbenchmark.data.goldstandard;

import java.io.File;
import java.io.IOException;
import org.aksw.limes.core.controller.Controller;
import org.aksw.limes.core.controller.ResultMappings;
import org.aksw.limes.core.io.config.Configuration;
import org.aksw.limes.core.io.serializer.ISerializer;
import org.aksw.limes.core.io.serializer.SerializerFactory;
import org.apache.commons.cli.CommandLine;
import static org.hobbit.spatialbenchmark.data.AbstractWorker.RELATION;
import static org.hobbit.spatialbenchmark.data.Generator.getConfigurations;
import org.hobbit.spatialbenchmark.platformConnection.BenchmarkController;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(BenchmarkController.class);

    private ResultMappings mappings;

    public RADONController(RDFFormat rdfFormat) throws IOException {
        String[] args = new String[1];
        args[0] = getConfigurations().getString(Configurations.CONFIGS_PATH) + File.separator + "topologicalConfigs/config" + RELATION + ".xml";

        CommandLine cmd = parseCommandLine(args);
        Configuration config = getConfig(cmd);
        String sourceFile = config.getSourceInfo().getEndpoint() + "." + rdfFormat.getDefaultFileExtension();
        String targetFile = config.getTargetInfo().getEndpoint() + "." + rdfFormat.getDefaultFileExtension();
      
        config.getSourceInfo().setEndpoint(sourceFile);
        config.getSourceInfo().setType(rdfFormat.getDefaultFileExtension().toUpperCase());
        config.getTargetInfo().setEndpoint(targetFile);
        config.getTargetInfo().setType(rdfFormat.getDefaultFileExtension().toUpperCase());
        
        config.setAcceptanceFile(getConfigurations().getString(Configurations.DATASETS_PATH) + File.separator + "GoldStandards" + File.separator + RELATION + "mappings." + rdfFormat.getDefaultFileExtension());
        config.setVerificationFile(getConfigurations().getString(Configurations.DATASETS_PATH) + File.separator + "GoldStandards" + File.separator + RELATION + "absolute_mapping_almost." + rdfFormat.getDefaultFileExtension());

//        BufferedReader br = new BufferedReader(new FileReader(config.getSourceInfo().getEndpoint()));
//        for (String line; (line = br.readLine()) != null;) {
//            System.out.print("line---------------------------->" + line);
//        }

        LOGGER.info("RADONController SesameUtils.parseRdfFormat(dataFormat).getDefaultFileExtension() " + rdfFormat.getDefaultFileExtension());

        //keep mappings for the oaei format
        mappings = getMapping(config);
        writeResults(mappings, config);

        //delete cache folder 
        File folder = new File("./cache/");
        FileUtil.removeDirectory(folder);

    }

    private static void writeResults(ResultMappings mappings, Configuration config) {
        String outputFormat = config.getOutputFormat();
        ISerializer output = SerializerFactory.createSerializer(outputFormat);
        output.setPrefixes(config.getPrefixes());
        output.writeToFile(mappings.getAcceptanceMapping(), config.getAcceptanceRelation(), config.getAcceptanceFile());
    }

    public ResultMappings getMappings() {
        return this.mappings;
    }

}
