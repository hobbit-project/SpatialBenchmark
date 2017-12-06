package org.hobbit.spatialbenchmark.data;

import static org.hobbit.spatialbenchmark.data.Generator.getSpatialTransformation;
import org.openrdf.model.Model;

public abstract class AbstractWorker {

    public static final String RELATION = getSpatialTransformation().getClass().getSimpleName();
    public static final String SOURCE_FILENAME = "%s%ssource" + getSpatialTransformation().getClass().getSimpleName() + "-%04d.";
    public static final String TARGET_FILENAME = "%s%starget" + getSpatialTransformation().getClass().getSimpleName() + "-%04d.";
    public static final String GOLDSTANDARD_FILENAME = "%s%sgoldStandard" + getSpatialTransformation().getClass().getSimpleName() + "-%04d.";
    public static final String OAEI_GOLDSTANDARD_FILENAME = "%s%soaeiGoldStandard" + getSpatialTransformation().getClass().getSimpleName() + "-%04d.";

//    public void run() {
//        try {
//            execute();
//        } catch (Exception e) {
//            System.out.println("Exception caught by : " + Thread.currentThread().getName() + " : " + e.getMessage());
//        }
//    }

    public abstract void execute() throws Exception;

    public abstract Model createSourceModel(Model model) throws Exception;

    public abstract Model createTargetModel(Model model) throws Exception;
}
