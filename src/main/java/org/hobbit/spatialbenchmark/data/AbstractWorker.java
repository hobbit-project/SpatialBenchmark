package org.hobbit.spatialbenchmark.data;

import org.openrdf.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractWorker extends Generator {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractWorker.class);
    
    public static final String RELATION = transform.getClass().getSimpleName();
    public static final String SOURCE_FILENAME = "%s%ssource" + transform.getClass().getSimpleName() + "-%04d.";
    public static final String TARGET_FILENAME = "%s%starget" + transform.getClass().getSimpleName() + "-%04d.";
    public static final String GOLDSTANDARD_FILENAME = "%s%sgoldStandard" + transform.getClass().getSimpleName() + "-%04d.";
    public static final String OAEI_GOLDSTANDARD_FILENAME = "%s%soaeiGoldStandard" + transform.getClass().getSimpleName() + "-%04d.";

    public void run() {
        try {
            execute();
        } catch (Exception e) {
            System.out.println("Exception caught by : " + Thread.currentThread().getName() + " : " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * This method will be called for execution of a concrete task
     */
    public abstract void execute() throws Exception;

    public abstract Model createSourceModel(Model model) throws Exception;

    public abstract Model createTargetModel(Model model) throws Exception;
}
