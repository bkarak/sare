package org.sare.errors;

/**
 * Default Implementation of SAREError interface
 *
 * @author Vassilios Karakoidas (vassilios.karakoidas@gmail.com)
 */
public class DefaultSAREError implements SAREError {
    /**
     * Standard constructor
     */
    public DefaultSAREError() {
        super();
    }

    /**
     * This method is executed if an error occurs.
     *
     * @param regex the input regular expressions
     * @param e     the generic exception instance
     */
    public void error(String regex, Exception e) {
        System.err.println(regex + " - " + e.toString());
    }
}
