package org.hobbit.spatialbenchmark.data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import org.aksw.limes.core.controller.ResultMappings;
import org.apache.commons.io.FileUtils;
import org.hobbit.spatialbenchmark.data.goldstandard.RADONController;
import org.hobbit.spatialbenchmark.data.goldstandard.oaei.OAEIRDFAlignmentFormat;
import org.hobbit.spatialbenchmark.properties.Configurations;
import org.hobbit.spatialbenchmark.util.FileUtil;
import org.hobbit.spatialbenchmark.util.SesameUtils;
import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.query.BindingSet;
import org.openrdf.query.GraphQueryResult;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.QueryResults;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.Rio;
import org.openrdf.sail.memory.MemoryStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Worker extends AbstractWorker {

    protected long targetTriples;
    protected long totalTriplesForWorker;
    protected String destinationPath;
    protected String serializationFormat;


    private static final Logger LOGGER = LoggerFactory.getLogger(Worker.class);

    public Worker(String destinationPath, String serializationFormat) {
        LOGGER.info("Worker constructor");
//        this.filesCount = filesCount;
        this.destinationPath = destinationPath;
        this.serializationFormat = serializationFormat;
    }

    @Override
    public void execute() throws Exception {
        FileOutputStream sourceFos = null;
        FileOutputStream targetFos = null;
        FileOutputStream gsFos = null;
        FileOutputStream oaeiGSFos = null;
        RDFFormat rdfFormat = SesameUtils.parseRdfFormat(serializationFormat);

        String sourceDestination = destinationPath + "/SourceDatasets";
        String targetDestination = destinationPath + "/TargetDatasets";
        String goldStandardDestination = destinationPath + "/GoldStandards";
        String OAEIGoldStandardDestination = destinationPath + "/OAEIGoldStandards";
        
        File theFileS = new File(sourceDestination);
        theFileS.mkdirs();
        FileUtils.cleanDirectory(theFileS); // will create a folder for the transformed data if not exists                     

        File theFileT = new File(targetDestination);
        theFileT.mkdirs();
        FileUtils.cleanDirectory(theFileT);

        File theFilegs = new File(goldStandardDestination);
        theFilegs.mkdirs();
        FileUtils.cleanDirectory(theFilegs);

        File theFileOAEIGS = new File(OAEIGoldStandardDestination);
        theFileOAEIGS.mkdirs();
        FileUtils.cleanDirectory(theFileOAEIGS);

        long currentFilesCount = filesCount.incrementAndGet();
        String sourceFileName = String.format(SOURCE_FILENAME + rdfFormat.getDefaultFileExtension(), sourceDestination, File.separator, currentFilesCount);
        String targetFileName = String.format(TARGET_FILENAME + rdfFormat.getDefaultFileExtension(), targetDestination, File.separator, currentFilesCount);
        String oaeiGSFileName = String.format(OAEI_GOLDSTANDARD_FILENAME + rdfFormat.getDefaultFileExtension(), OAEIGoldStandardDestination, File.separator, currentFilesCount);
//        LoadGivenData loadData = new LoadGivenData();
//        Repository repository = loadData.LoadDatasetsFromFile();
//        RepositoryConnection con = repository.getConnection();
//        if (con.size() <= Main.getConfigurations().getInt(Configurations.TOTAL_TRIPLES)) {
//            targetTriples = con.size();
//        } else {
//            targetTriples = Main.getConfigurations().getInt(Configurations.TOTAL_TRIPLES); //or target size that user gave
//        }
//        System.out.println("targetTriples " + targetTriples);


        RDFFormat format = RDFFormat.TURTLE; //to format tha diaforetiko, analoga to retrieve apo to mimicking?
        String path = configurations.getString(Configurations.GIVEN_DATASETS_PATH);
        //String path = destinationPath + "/givenDatasets/";
        List<File> collectedFiles = new ArrayList<File>();
//        Repository repository = new SailRepository(new MemoryStore());
        RepositoryConnection con = null;
//
//        repository.initialize();
//        con = repository.getConnection();

        //each file is an instance
        FileUtil.collectFilesList(path, collectedFiles, "*", true);
        //List<File> files = collectedFiles;
        //episis auto mallon de tha einai etsi, diladi ana file! 
        // na kano generate me to mimicking osa xreiazomai kai retrieve ola?
        //episis na ta kratao stin cache?! pos to exoun kanei sto limes?
        //i kleanthi pos to exei kanei?
        int numOfInstances = configurations.getInt(Configurations.INSTANCES);

        if ((configurations.getInt(Configurations.INSTANCES) > collectedFiles.size()) || (configurations.getInt(Configurations.INSTANCES) == 0)) {
            numOfInstances = collectedFiles.size();
        }
        try {
            sourceFos = new FileOutputStream(sourceFileName);
            targetFos = new FileOutputStream(targetFileName);
            oaeiGSFos = new FileOutputStream(oaeiGSFileName);

            for (int i = 0; i < numOfInstances; i++) {
                Repository repository = new SailRepository(new MemoryStore());
                con = null;
                repository.initialize();
                con = repository.getConnection();
                con.add(collectedFiles.get(i), "", format);
//                System.out.println("con size " + con.size());
                System.out.println("i " + i + " " + collectedFiles.get(i).getName());
//            System.out.println("numOfInstances " + numOfInstances);

                //ids of traces for defined number of instances 
                String queryNumInstances = "SELECT ?s WHERE {"
                        + "?s  a  <http://www.tomtom.com/ontologies/traces#Trace> . }";
//                        + "LIMIT " + numOfInstances;

                TupleQuery query = con.prepareTupleQuery(QueryLanguage.SPARQL, queryNumInstances);
                TupleQueryResult result = query.evaluate();

//            while (result.hasNext() && targetTriples > 0) {
                BindingSet nextResult = result.next();
                String traceID = nextResult.getBinding("s").getValue().stringValue();
//                System.out.println("traceID: " + traceID);

                //retrieve long, lat from given datasets
                String queryString
                        = "CONSTRUCT{"
                        + "<" + traceID + ">  a  <http://www.tomtom.com/ontologies/traces#Trace> ."
                        + "<" + traceID + "> <http://www.tomtom.com/ontologies/traces#hasPoint> ?o1 . " //Point
                        + "?o1 <http://www.tomtom.com/ontologies/traces#lat> ?o2 . "
                        + "?o1 <http://www.tomtom.com/ontologies/traces#long> ?o3 . }"
                        + "WHERE {"
                        + "<" + traceID + ">  a  <http://www.tomtom.com/ontologies/traces#Trace> ."
                        + "<" + traceID + "> <http://www.tomtom.com/ontologies/traces#hasPoint> ?o1 . " //Point
                        + "?o1 <http://www.tomtom.com/ontologies/traces#lat> ?o2 . "
                        + "?o1 <http://www.tomtom.com/ontologies/traces#long> ?o3 . }";

                GraphQueryResult graphResult = con.prepareGraphQuery(QueryLanguage.SPARQL, queryString).evaluate();
                Model resultsModel = QueryResults.asModel(graphResult);
                targetTriples -= resultsModel.size();
                CreateInstances create = new CreateInstances();

                Model givenInstanceModel = new LinkedHashModel();
                Iterator<Statement> it = resultsModel.iterator();

                //retrieve each Trace instance 
                while (it.hasNext()) {
                    Statement statement = it.next();

                    if (statement.getObject().stringValue().endsWith("Trace") || !it.hasNext()) {
                        if (!givenInstanceModel.isEmpty()) {
                            //source instance - Trace

                            if (!it.hasNext()) {
                                givenInstanceModel.add(statement);
                            }
                            create.sourceInstance(givenInstanceModel);
                            Model sourceTrace = create.getSourceTrace();
                            Model targetTrace = create.targetInstance(sourceTrace);
                            //if the relation is within or covered_by, write sources on target file and targets on source file
                            if (transform.getClass().getSimpleName().equals("WITHIN") || transform.getClass().getSimpleName().equals("COVERED_BY")) {
                                Rio.write(targetTrace, sourceFos, rdfFormat);
                                Rio.write(sourceTrace, targetFos, rdfFormat);
                            } else {
                                Rio.write(sourceTrace, sourceFos, rdfFormat);
                                Rio.write(targetTrace, targetFos, rdfFormat);
                            }

                            givenInstanceModel = new LinkedHashModel();
                        }
                    }
                    givenInstanceModel.add(statement);
                }
            }

            //oaei gold standard
            OAEIRDFAlignmentFormat oaeiRDF = new OAEIRDFAlignmentFormat(oaeiGSFileName, sourceFileName, targetFileName);

            //mappings from RADON
            ResultMappings results = new RADONController(rdfFormat).getMappings(); //generate gold standard
            HashMap<String, HashMap<String, Double>> mappings = results.getAcceptanceMapping().getMap();

            for (HashMap.Entry<String, HashMap<String, Double>> entry : mappings.entrySet()) {
                String source = entry.getKey();
//                System.out.println("source " + source);
                for (HashMap.Entry<String, Double> innerEntry : entry.getValue().entrySet()) {
                    String target = innerEntry.getKey();
//                    System.out.println("target " + target);
                    oaeiRDF.addMapping2Output(source, target, RELATION, 1.0);
                }
            }
            try {
                oaeiRDF.saveOutputFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
            con.close();
            flushClose(sourceFos);
            flushClose(targetFos);
            flushClose(gsFos);
            flushClose(oaeiGSFos);

        } catch (RDFHandlerException e) {
            con.close();
            flushClose(sourceFos);
            flushClose(targetFos);
            flushClose(gsFos);
            flushClose(oaeiGSFos);

            throw new IOException("A problem occurred while generating RDF data: " + e.getMessage());
        }
    }

    protected synchronized void flushClose(OutputStream fos) throws IOException {
        if (fos != null) {
            fos.flush();
            fos.close();
        }
    }

    @Override
    public Model createSourceModel(Model model) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Model createTargetModel(Model model) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
