/*
 * Copyright (c) 2013, 2026 Vassilios Karakoidas.
 * Licensed under the BSD 3-Clause License; see LICENSE.
 */
package org.sare;

import org.sare.errors.DefaultError;
import org.sare.errors.SAREError;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The SARE parser (Simple API for Regular Expressions), over {@code java.util.regex}.
 *
 * <p>Assign an {@link IGroupAction} to each group of interest, then call one of the
 * {@code process} methods. Every action sees {@link IGroupAction#start} once, then
 * {@link IGroupAction#match} for each match its group took part in, then
 * {@link IGroupAction#end} once.
 *
 * <p>A parser can be reused for any number of inputs. It is not safe to assign actions while
 * another thread is processing.
 *
 * @author Vassilios Karakoidas (vassilios.karakoidas@gmail.com)
 * @see java.util.regex
 */
public class RegexParser {
    private final Pattern pattern;
    /** Group number to its actions, in group order. */
    private final Map<Integer, ActionList> actions = new TreeMap<>();
    private SAREError error = new DefaultError();

    /**
     * Compiles the expression with {@link Pattern#compile(String)}.
     *
     * @param regex the regular expression
     * @throws java.util.regex.PatternSyntaxException if the expression is invalid
     */
    public RegexParser(String regex) {
        this(Pattern.compile(regex));
    }

    /**
     * @param pattern the compiled regular expression
     */
    public RegexParser(Pattern pattern) {
        this.pattern = Objects.requireNonNull(pattern, "pattern");
    }

    /**
     * Assigns an action to a group. An action can be assigned to several groups, and a group can
     * have several actions; they are called in the order they were assigned.
     *
     * @param group the group number; 0 is the whole match
     * @param action the action
     * @throws IndexOutOfBoundsException if the expression has no such group
     */
    public void assignAction(int group, IGroupAction action) {
        Objects.requireNonNull(action, "action");
        int groups = pattern.matcher("").groupCount();
        if (group < 0 || group > groups) {
            throw new IndexOutOfBoundsException("No group " + group + " in " + pattern.pattern());
        }
        actions.computeIfAbsent(group, ActionList::new).register(action);
    }

    /**
     * Assigns an action to a named group, {@code (?<name>...)}. The action still receives the
     * group's number.
     *
     * @param name the group's name
     * @param action the action
     * @throws IllegalArgumentException if the expression has no group of that name
     */
    public void assignAction(String name, IGroupAction action) {
        Integer group = pattern.namedGroups().get(name);
        if (group == null) {
            throw new IllegalArgumentException("No group with name <" + name + "> in " + pattern.pattern());
        }
        assignAction(group, action);
    }

    /**
     * Sets the handler for errors met while reading the input. The default prints them on
     * standard error.
     *
     * @param error the handler
     */
    public void setError(SAREError error) {
        this.error = Objects.requireNonNull(error, "error");
    }

    /**
     * Processes a character sequence.
     *
     * @param cs the input
     * @return the number of matches
     */
    public int process(CharSequence cs) {
        List<IGroupAction> distinct = start();
        try {
            return find(cs);
        } finally {
            end(distinct);
        }
    }

    /**
     * Processes a byte stream decoded as UTF-8. The whole stream is read before matching.
     *
     * @param is the input; it is read to the end but not closed
     * @return the number of matches, or 0 if the stream could not be read
     */
    public int process(InputStream is) {
        return process(is, StandardCharsets.UTF_8);
    }

    /**
     * Processes a byte stream in the given charset. The whole stream is read before matching.
     *
     * @param is the input; it is read to the end but not closed
     * @param charset the stream's encoding
     * @return the number of matches, or 0 if the stream could not be read
     */
    public int process(InputStream is, Charset charset) {
        List<IGroupAction> distinct = start();
        try {
            return find(new String(is.readAllBytes(), charset));
        } catch (IOException e) {
            error.error(pattern.pattern(), e);
            return 0;
        } finally {
            end(distinct);
        }
    }

    /**
     * Processes text one line at a time, so memory holds a line rather than the input, and a
     * match never spans a line break.
     *
     * @param br the input; it is read to the end but not closed
     * @return the number of matches, counting those found before a read error
     */
    public int process(BufferedReader br) {
        List<IGroupAction> distinct = start();
        int result = 0;
        try {
            for (String line = br.readLine(); line != null; line = br.readLine()) {
                result += find(line);
            }
        } catch (IOException e) {
            error.error(pattern.pattern(), e);
        } finally {
            end(distinct);
        }
        return result;
    }

    /**
     * Calls {@link IGroupAction#start} once on each distinct action, in group order.
     *
     * @return the actions it was called on, for {@link #end}
     */
    private List<IGroupAction> start() {
        Set<IGroupAction> seen = Collections.newSetFromMap(new IdentityHashMap<>());
        List<IGroupAction> distinct = new ArrayList<>();
        for (ActionList list : actions.values()) {
            for (IGroupAction action : list) {
                if (seen.add(action)) {
                    distinct.add(action);
                }
            }
        }
        for (IGroupAction action : distinct) {
            action.start();
        }
        return distinct;
    }

    private static void end(List<IGroupAction> distinct) {
        for (IGroupAction action : distinct) {
            action.end();
        }
    }

    /**
     * Reports every match in the input to the actions of the groups that took part in it.
     *
     * @return the number of matches
     */
    private int find(CharSequence cs) {
        Matcher matcher = pattern.matcher(cs);
        int matchCount = 0;

        while (matcher.find()) {
            matchCount++;
            String full = matcher.group();
            for (ActionList list : actions.values()) {
                String text = matcher.group(list.group());
                if (text != null) {
                    list.match(full, text);
                }
            }
        }

        return matchCount;
    }
}
