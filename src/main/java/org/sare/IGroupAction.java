/*
 * Copyright (c) 2013, 2026 Vassilios Karakoidas.
 * Licensed under the BSD 3-Clause License; see LICENSE.
 */
package org.sare;

/**
 * The action interface. Assign an implementation to one or more groups of a {@link RegexParser}
 * and it is called as the parser finds matches.
 *
 * <p>Only {@link #match} has to be written, so an action can be a lambda:
 * {@code parser.assignAction(1, (group, full, text) -> ...)}.
 *
 * @author Vassilios Karakoidas (vassilios.karakoidas@gmail.com)
 */
@FunctionalInterface
public interface IGroupAction {
    /**
     * Called once per {@code process} call, before the first match, however many groups the
     * action is assigned to.
     */
    default void start() {
    }

    /**
     * Called once per {@code process} call, after the last match. It is called also when reading
     * the input failed, so it is the place to release what {@link #start} acquired.
     */
    default void end() {
    }

    /**
     * Called for each match in which the group took part, in group order within a match. A group
     * that took no part in a match (the {@code (b)} of {@code (a)|(b)} on {@code "a"}) is not
     * reported for that match.
     *
     * @param group the group number; 0 is the whole match
     * @param full the whole match, {@code group(0)}
     * @param text the text of the group, never {@code null}
     */
    void match(int group, String full, String text);
}
