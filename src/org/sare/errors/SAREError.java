package org.sare.errors;

/**
 *
 * @author Vassilios Karakoidas (vassilios.karakoidas@gmail.com)
 */
public interface SAREError {

    /**
     * This method is executed if an error occurs.
     *
     * @param regex the input regular expressions
     * @param e the generic exception instance
     */
    public void error(String regex, Exception e);
}
