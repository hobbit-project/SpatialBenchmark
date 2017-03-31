package org.hobbit.spatialbenchmark.platformConnection;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.hobbit.core.components.AbstractEvaluationModule;
import org.hobbit.core.rabbit.RabbitMQUtils;
import org.hobbit.spatialbenchmark.platformConnection.util.PlatformConstants;
import org.hobbit.vocab.HOBBIT;

public class EvaluationModule extends AbstractEvaluationModule {

    /* Experiment key */
    private String EVALUATION_PARAMETER_KEY; // NUMBER OF BATCHES

    private Property EVALUATION_RECALL = null;
    private Property EVALUATION_PRECISION = null;
    private Property EVALUATION_FMEASURE = null;

    private Model finalModel = ModelFactory.createDefaultModel();

    private int totalTruePositives = 0;
    private int totalFalsePositives = 0;
    private int totalFalseNegatives = 0;

    private double sumTaskDelay = 0;

    private int taskCounter = 0;

    private boolean flag = true;

    /* Setters and Getters */
    public String getEVALUATION_PARAMETER_KEY() {
        return EVALUATION_PARAMETER_KEY;
    }

    public void setEVALUATION_PARAMETER_KEY(String eVALUATION_PARAMETER_KEY) {
        EVALUATION_PARAMETER_KEY = eVALUATION_PARAMETER_KEY;
    }

    private Property EVALUATION_AVERAGE_TASK_DELAY = null;

    public Property getEVALUATION_AVERAGE_TASK_DELAY() {
        return EVALUATION_AVERAGE_TASK_DELAY;
    }

    public void setEVALUATION_AVERAGE_TASK_DELAY(Property eVALUATION_AVERAGE_TASK_DELAY) {
        EVALUATION_AVERAGE_TASK_DELAY = eVALUATION_AVERAGE_TASK_DELAY;
    }

    public Property getEVALUATION_RECALL() {
        return EVALUATION_RECALL;
    }

    public void setEVALUATION_RECALL(Property eVALUATION_RECALL) {
        EVALUATION_RECALL = eVALUATION_RECALL;
    }

    public Property getEVALUATION_PRECISION() {
        return EVALUATION_PRECISION;
    }

    public void setEVALUATION_PRECISION(Property eVALUATION_PRECISION) {
        EVALUATION_PRECISION = eVALUATION_PRECISION;
    }

    public Property getEVALUATION_FMEASURE() {
        return EVALUATION_FMEASURE;
    }

    public void setEVALUATION_FMEASURE(Property eVALUATION_FMEASURE) {
        EVALUATION_FMEASURE = eVALUATION_FMEASURE;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    @Override
    public void init() throws Exception {
        super.init();

        Map<String, String> env = System.getenv();
        if (!env.containsKey(PlatformConstants.EVALUATION_PARAMETER_KEY)) {
            throw new IllegalArgumentException("Couldn't get \"" + PlatformConstants.EVALUATION_PARAMETER_KEY
                    + "\" from the environment. Aborting.");
        }
        EVALUATION_PARAMETER_KEY = env.get(PlatformConstants.EVALUATION_PARAMETER_KEY);

        /* average task delay */
        if (!env.containsKey(PlatformConstants.EVALUATION_AVERAGE_TASK_DELAY)) {
            throw new IllegalArgumentException("Couldn't get \"" + PlatformConstants.EVALUATION_AVERAGE_TASK_DELAY
                    + "\" from the environment. Aborting.");
        }
        EVALUATION_AVERAGE_TASK_DELAY = this.finalModel
                .createProperty(env.get(PlatformConstants.EVALUATION_AVERAGE_TASK_DELAY));

        /* recall */
        if (!env.containsKey(PlatformConstants.EVALUATION_RECALL)) {
            throw new IllegalArgumentException("Couldn't get \"" + PlatformConstants.EVALUATION_RECALL
                    + "\" from the environment. Aborting.");
        }
        EVALUATION_RECALL = this.finalModel
                .createProperty(env.get(PlatformConstants.EVALUATION_RECALL));

        /* precision */
        if (!env.containsKey(PlatformConstants.EVALUATION_PRECISION)) {
            throw new IllegalArgumentException("Couldn't get \""
                    + PlatformConstants.EVALUATION_PRECISION + "\" from the environment. Aborting.");
        }
        EVALUATION_PRECISION = this.finalModel
                .createProperty(env.get(PlatformConstants.EVALUATION_PRECISION));

        /* fmeasure */
        if (!env.containsKey(PlatformConstants.EVALUATION_FMEASURE)) {
            throw new IllegalArgumentException("Couldn't get \"" + PlatformConstants.EVALUATION_FMEASURE
                    + "\" from the environment. Aborting.");
        }
        EVALUATION_FMEASURE = this.finalModel
                .createProperty(env.get(PlatformConstants.EVALUATION_FMEASURE));
    }

    @Override
    public void receiveCommand(byte command, byte[] data) {
        System.out.println("EvaluationModule receiveCommand");
        this.receiveCommand(command, data);
    }

    @Override
    protected void evaluateResponse(byte[] expectedData, byte[] receivedData, long taskSentTimestamp,
            long responseReceivedTimestamp) throws Exception {
        System.out.println("EvaluationModule evaluateResponse");

        taskCounter++;

        long delay = responseReceivedTimestamp - taskSentTimestamp;
        this.sumTaskDelay += delay;
        // read expected data
        ByteBuffer expectedBuffer = ByteBuffer.wrap(expectedData);
        HashMap<String, String> expected = new HashMap<String, String>();
        while (expectedBuffer.remaining() > 0) {
            String answer = RabbitMQUtils.readString(expectedBuffer);
            String source = answer.split(" ")[0];
            String relation = answer.split(" ")[1];
            String target = answer.split(" ")[2];
            expected.put(source, target);
        }

        // read received data
        ByteBuffer receivedBuffer = ByteBuffer.wrap(expectedData);
        HashMap<String, String> received = new HashMap<String, String>();
        while (receivedBuffer.remaining() > 0) {
            String answer = RabbitMQUtils.readString(receivedBuffer);
            String source = answer.split(" ")[0];
            String relation = answer.split(" ")[1];
            String target = answer.split(" ")[2];
            received.put(source, target); 
        }

        int truePositives = 0;
        int falsePositives = 0;
        int falseNegatives = 0;

        boolean found = false;
        boolean isPositive = true;
        for (Map.Entry<String, String> entry : received.entrySet()) {
            String variable = entry.getKey();
            String receivedAnswer = entry.getValue();
            if (!receivedAnswer.equals(expected.get(variable))) {
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
        System.out.println("this.totalFalseNegatives " + this.totalFalseNegatives);
        System.out.println("this.totalFalsePositives " + this.totalFalsePositives);
        System.out.println("this.totalTruePositives " + this.totalTruePositives);
        this.totalFalseNegatives += falseNegatives;
        this.totalFalsePositives += falsePositives;
        this.totalTruePositives += truePositives;

    }

    @Override
    protected Model summarizeEvaluation() throws Exception {
        System.out.println("EvaluationModule summarizeEvaluation");

        float averageTaskDelay = (float) this.sumTaskDelay / this.taskCounter;

        // compute macro and micro averages KPIs
        float microAverageRecall = (float) this.totalTruePositives
                / (float) (this.totalTruePositives + this.totalFalseNegatives);
        float microAveragePrecision = (float) this.totalTruePositives
                / (float) (this.totalTruePositives + this.totalFalsePositives);
        float microAverageFmeasure = (float) (2.0 * microAverageRecall * microAveragePrecision)
                / (float) (microAverageRecall + microAveragePrecision);

        //////////////////////////////////////////////////////////////////////////////////////////////
        Resource experiment = this.finalModel
                .createResource("http://w3id.org/hobbit/experiments#" + EVALUATION_PARAMETER_KEY);
        this.finalModel.add(experiment, RDF.type, HOBBIT.Experiment);

        Literal averageTaskDelayLiteral = this.finalModel.createTypedLiteral(new Float(averageTaskDelay));
        this.finalModel.add(experiment, this.EVALUATION_AVERAGE_TASK_DELAY, averageTaskDelayLiteral);

        Literal microAverageRecallLiteral = this.finalModel.createTypedLiteral(new Float(microAverageRecall));
        this.finalModel.add(experiment, this.EVALUATION_RECALL, microAverageRecallLiteral);

        Literal microAveragePrecisionLiteral = this.finalModel.createTypedLiteral(new Float(microAveragePrecision));
        this.finalModel.add(experiment, this.EVALUATION_PRECISION, microAveragePrecisionLiteral);

        Literal microAverageFmeasureLiteral = this.finalModel.createTypedLiteral(new Float(microAverageFmeasure));
        this.finalModel.add(experiment, this.EVALUATION_FMEASURE, microAverageFmeasureLiteral);

        return this.finalModel;
    }

}
