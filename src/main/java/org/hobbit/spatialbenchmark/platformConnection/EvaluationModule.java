package org.hobbit.spatialbenchmark.platformConnection;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.apache.jena.datatypes.xsd.XSDDatatype;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.hobbit.core.Constants;
import org.hobbit.core.components.AbstractEvaluationModule;
import org.hobbit.core.rabbit.RabbitMQUtils;
import org.hobbit.spatialbenchmark.platformConnection.util.PlatformConstants;
import org.hobbit.vocab.HOBBIT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EvaluationModule extends AbstractEvaluationModule {

    private static final Logger LOGGER = LoggerFactory.getLogger(EvaluationModule.class);

    private Property EVALUATION_RECALL = null;
    private Property EVALUATION_PRECISION = null;
    private Property EVALUATION_FMEASURE = null;
    private Property EVALUATION_TIME_PERFORMANCE = null;

    private Model finalModel = ModelFactory.createDefaultModel();

    private int totalTruePositives = 0;
    private int totalFalsePositives = 0;
    private int totalFalseNegatives = 0;

    private long time_performance = 0;

    private boolean flag = true;

    @Override
    public void init() throws Exception {
        LOGGER.info("Initializing Evaluation Module started...");
        super.init();

        Map<String, String> env = System.getenv();

        /* time performance */
        if (!env.containsKey(PlatformConstants.EVALUATION_TIME_PERFORMANCE)) {
            throw new IllegalArgumentException("Couldn't get \"" + PlatformConstants.EVALUATION_TIME_PERFORMANCE
                    + "\" from the environment. Aborting.");
        }
        EVALUATION_TIME_PERFORMANCE = this.finalModel
                .createProperty(env.get(PlatformConstants.EVALUATION_TIME_PERFORMANCE));

        LOGGER.info("EVALUATION_TIME_PERFORMANCE setted");
        /* recall */
        if (!env.containsKey(PlatformConstants.EVALUATION_RECALL)) {
            throw new IllegalArgumentException("Couldn't get \"" + PlatformConstants.EVALUATION_RECALL
                    + "\" from the environment. Aborting.");
        }
        EVALUATION_RECALL = this.finalModel
                .createProperty(env.get(PlatformConstants.EVALUATION_RECALL));

        LOGGER.info("EVALUATION_RECALL setted");

        /* precision */
        if (!env.containsKey(PlatformConstants.EVALUATION_PRECISION)) {
            throw new IllegalArgumentException("Couldn't get \""
                    + PlatformConstants.EVALUATION_PRECISION + "\" from the environment. Aborting.");
        }
        EVALUATION_PRECISION = this.finalModel
                .createProperty(env.get(PlatformConstants.EVALUATION_PRECISION));

        LOGGER.info("EVALUATION_PRECISION setted");

        /* fmeasure */
        if (!env.containsKey(PlatformConstants.EVALUATION_FMEASURE)) {
            throw new IllegalArgumentException("Couldn't get \"" + PlatformConstants.EVALUATION_FMEASURE
                    + "\" from the environment. Aborting.");
        }
        EVALUATION_FMEASURE = this.finalModel
                .createProperty(env.get(PlatformConstants.EVALUATION_FMEASURE));

        LOGGER.info("EVALUATION_FMEASURE setted");

        LOGGER.info("Initializing Evaluation Module ended...");

    }

    @Override
    protected void evaluateResponse(byte[] expectedData, byte[] receivedData, long taskSentTimestamp,
            long responseReceivedTimestamp) throws Exception {

        LOGGER.info("EvaluationModule evaluateResponse");
        LOGGER.info("taskSentTimestamp " + taskSentTimestamp);
        LOGGER.info("responseReceivedTimestamp " + responseReceivedTimestamp);

        // read expected data
        LOGGER.info("read expected data");
        ByteBuffer buffer = ByteBuffer.wrap(expectedData);
        String format = RabbitMQUtils.readString(buffer);
        String path = RabbitMQUtils.readString(buffer);

        byte[] expected = RabbitMQUtils.readByteArray(buffer);
        String[] dataAnswers = RabbitMQUtils.readString(expected).split(System.getProperty("line.separator"));

        HashMap<String, String> expectedMap = new HashMap<String, String>();
        for (String answer : dataAnswers) {
            String source = answer.split("\t")[0];
            String target = answer.split("\t")[1];
            expectedMap.put(source, target);
        }
        LOGGER.info("expected data into the map");

        // read received data
        String[] receivedDataAnswers = RabbitMQUtils.readString(expected).split(System.getProperty("line.separator"));
        HashMap<String, String> receivedMap = new HashMap<String, String>();
        for (String answer : receivedDataAnswers) {
            String source = answer.split("\t")[0];
            String target = answer.split("\t")[1];
            receivedMap.put(source, target);
        }
        LOGGER.info("received data into the map");
        
        int truePositives = 0;
        int falsePositives = 0;
        int falseNegatives = 0;

        boolean found = false;
        boolean isPositive = true;
        for (Map.Entry<String, String> entry : receivedMap.entrySet()) {
            String variable = entry.getKey();
            String receivedAnswer = entry.getValue();
            if (!receivedAnswer.equals(expectedMap.get(variable))) {
                isPositive = false;
                break;
            }
        }
        if (isPositive == true) {
            truePositives++;
            found = true;
        } else if (isPositive == false) {
            falsePositives++;
        }
        if (found == false) {
            falseNegatives++;
        }

        this.totalFalseNegatives += falseNegatives;
        this.totalFalsePositives += falsePositives;
        this.totalTruePositives += truePositives;

        System.out.println("this.totalFalseNegatives " + this.totalFalseNegatives);
        System.out.println("this.totalFalsePositives " + this.totalFalsePositives);
        System.out.println("this.totalTruePositives " + this.totalTruePositives);
    }


    @Override
    protected Model summarizeEvaluation() throws Exception {
        LOGGER.info("summarizeEvaluation Not supported yet.");
        return null;//To change body of generated methods, choose Tools | Templates.
    }


}
