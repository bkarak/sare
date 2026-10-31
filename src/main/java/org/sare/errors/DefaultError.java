/*
 * Copyright (c) 2013, 2026 Vassilios Karakoidas.
 * Licensed under the BSD 3-Clause License; see LICENSE.
 */
package org.sare.errors;

/**
 * The default error handler: prints the expression and the exception on standard error.
 *
 * @author Vassilios Karakoidas (vassilios.karakoidas@gmail.com)
 */
public class DefaultError implements SAREError {
    @Override
    public void error(String regex, Exception e) {
        System.err.println(regex + " - " + e);
    }
}
