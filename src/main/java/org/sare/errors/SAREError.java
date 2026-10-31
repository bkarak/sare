/*
 * Copyright (c) 2013, 2026 Vassilios Karakoidas.
 * Licensed under the BSD 3-Clause License; see LICENSE.
 */
package org.sare.errors;

/**
 * Receives the errors a {@link org.sare.RegexParser} meets while reading its input.
 *
 * @author Vassilios Karakoidas (vassilios.karakoidas@gmail.com)
 */
@FunctionalInterface
public interface SAREError {

    /**
     * Called when reading the input fails.
     *
     * @param regex the parser's regular expression
     * @param e the exception
     */
    void error(String regex, Exception e);
}
