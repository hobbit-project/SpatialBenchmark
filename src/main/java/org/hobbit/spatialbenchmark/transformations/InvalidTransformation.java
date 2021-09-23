package org.hobbit.spatialbenchmark.transformations;

public class InvalidTransformation extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = -5441815029013851904L;

    /* (non-Javadoc)
	 * @see java.lang.Throwable#getMessage()
     */
    @Override
    public String getMessage() {
        return "Wrong type of object transformation";
    }

}
