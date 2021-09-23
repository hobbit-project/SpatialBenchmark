package org.hobbit.spatialbenchmark.data;

import static org.hobbit.spatialbenchmark.data.Generator.getSpatialTransformation;

public abstract class AbstractWorker {

    public static final String RELATION = getSpatialTransformation().getClass().getSimpleName();
    public static final String SOURCE_FILENAME = "%s%ssource" + getSpatialTransformation().getClass().getSimpleName() + "-%04d.";
    public static final String TARGET_FILENAME = "%s%starget" + getSpatialTransformation().getClass().getSimpleName() + "-%04d.";
    public static final String GOLDSTANDARD_FILENAME = "%s%sgoldStandard" + getSpatialTransformation().getClass().getSimpleName() + "-%04d.";
    public static final String OAEI_GOLDSTANDARD_FILENAME = "%s%soaeiGoldStandard" + getSpatialTransformation().getClass().getSimpleName() + "-%04d.";

    public abstract void execute() throws Exception;
}
