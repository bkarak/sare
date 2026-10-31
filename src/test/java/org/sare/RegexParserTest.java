/*
 * Copyright (c) 2013, 2026 Vassilios Karakoidas.
 * Licensed under the BSD 3-Clause License; see LICENSE.
 */
package org.sare;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RegexParserTest {
    private static final String IPV4 = "([0-9]+)\\.([0-9]+)\\.([0-9]+)\\.([0-9]+)";

    /** Writes every call it receives to a shared log, prefixed with its name. */
    private record Recorder(String name, List<String> log) implements IGroupAction {
        @Override
        public void start() {
            log.add(name + " start");
        }

        @Override
        public void end() {
            log.add(name + " end");
        }

        @Override
        public void match(int group, String full, String text) {
            log.add(name + " [" + group + "] " + full + " " + text);
        }
    }

    /** The README's example: one action on all five groups is started and ended once. */
    @Test
    void oneActionOnEveryGroupStartsAndEndsOnce() {
        List<String> log = new ArrayList<>();
        RegexParser sare = new RegexParser(IPV4);
        Recorder co = new Recorder("co", log);
        for (int g = 0; g <= 4; g++) {
            sare.assignAction(g, co);
        }

        assertEquals(1, sare.process("192.255.255.150"));
        assertEquals(List.of(
                "co start",
                "co [0] 192.255.255.150 192.255.255.150",
                "co [1] 192.255.255.150 192",
                "co [2] 192.255.255.150 255",
                "co [3] 192.255.255.150 255",
                "co [4] 192.255.255.150 150",
                "co end"), log);
    }

    @Test
    void matchesArriveInGroupOrderThenAssignmentOrder() {
        List<String> log = new ArrayList<>();
        RegexParser sare = new RegexParser("(\\w)(\\d)");
        sare.assignAction(2, new Recorder("b", log));
        sare.assignAction(1, new Recorder("a", log));
        sare.assignAction(2, new Recorder("c", log));

        assertEquals(2, sare.process("x1 y2"));
        assertEquals(List.of(
                "a start", "b start", "c start",
                "a [1] x1 x", "b [2] x1 1", "c [2] x1 1",
                "a [1] y2 y", "b [2] y2 2", "c [2] y2 2",
                "a end", "b end", "c end"), log);
    }

    @Test
    void lambdaAction() {
        List<String> words = new ArrayList<>();
        RegexParser sare = new RegexParser("[a-z]+");
        sare.assignAction(0, (group, full, text) -> words.add(text));

        assertEquals(3, sare.process("one two  three"));
        assertEquals(List.of("one", "two", "three"), words);
    }

    @Test
    void namedGroup() {
        List<String> seen = new ArrayList<>();
        RegexParser sare = new RegexParser("(?<key>\\w+)=(?<value>\\w+)");
        sare.assignAction("value", (group, full, text) -> seen.add(group + ":" + text));

        assertEquals(2, sare.process("a=1, b=2"));
        assertEquals(List.of("2:1", "2:2"), seen);
    }

    @Test
    void groupThatTookNoPartIsNotReported() {
        List<String> log = new ArrayList<>();
        RegexParser sare = new RegexParser("(a)|(b)");
        Recorder r = new Recorder("r", log);
        sare.assignAction(1, r);
        sare.assignAction(2, r);

        assertEquals(2, sare.process("ba"));
        assertEquals(List.of("r start", "r [2] b b", "r [1] a a", "r end"), log);
    }

    @Test
    void noMatchStillStartsAndEnds() {
        List<String> log = new ArrayList<>();
        RegexParser sare = new RegexParser("x");
        sare.assignAction(0, new Recorder("r", log));

        assertEquals(0, sare.process("abc"));
        assertEquals(List.of("r start", "r end"), log);
    }

    @Test
    void parserIsReusable() {
        List<String> log = new ArrayList<>();
        RegexParser sare = new RegexParser("\\d");
        sare.assignAction(0, new Recorder("r", log));

        assertEquals(1, sare.process("a1"));
        assertEquals(2, sare.process("2b3"));
        assertEquals(List.of("r start", "r [0] 1 1", "r end",
                "r start", "r [0] 2 2", "r [0] 3 3", "r end"), log);
    }

    /** In 2013 this always failed: the loop matched the null that ends the input. */
    @Test
    void bufferedReaderIsReadLineByLine() {
        List<String> log = new ArrayList<>();
        RegexParser sare = new RegexParser("[0-9]+");
        sare.assignAction(0, new Recorder("r", log));
        List<String> errors = new ArrayList<>();
        sare.setError((regex, e) -> errors.add(e.toString()));

        assertEquals(3, sare.process(new BufferedReader(new StringReader("a 1\nb 22\n333"))));
        assertEquals(List.of("r start", "r [0] 1 1", "r [0] 22 22", "r [0] 333 333", "r end"), log);
        assertEquals(List.of(), errors);
    }

    @Test
    void bufferedReaderMatchDoesNotSpanLines() {
        RegexParser sare = new RegexParser("a\\s+b");
        assertEquals(0, sare.process(new BufferedReader(new StringReader("a\nb"))));
        assertEquals(1, sare.process("a\nb"));
    }

    @Test
    void readErrorGoesToTheHandlerAndEndStillRuns() {
        List<String> log = new ArrayList<>();
        RegexParser sare = new RegexParser("[0-9]+");
        sare.assignAction(0, new Recorder("r", log));
        List<String> errors = new ArrayList<>();
        sare.setError((regex, e) -> errors.add(regex + " " + e.getMessage()));

        Reader failing = new Reader() {
            private final Reader first = new StringReader("1 2\n");

            @Override
            public int read(char[] buf, int off, int len) throws IOException {
                int n = first.read(buf, off, len);
                if (n < 0) {
                    throw new IOException("disk on fire");
                }
                return n;
            }

            @Override
            public void close() {
            }
        };

        assertEquals(2, sare.process(new BufferedReader(failing)));
        assertEquals(List.of("r start", "r [0] 1 1", "r [0] 2 2", "r end"), log);
        assertEquals(List.of("[0-9]+ disk on fire"), errors);
    }

    @Test
    void inputStreamIsUtf8ByDefault() {
        List<String> words = new ArrayList<>();
        RegexParser sare = new RegexParser("\\p{L}+");
        sare.assignAction(0, (group, full, text) -> words.add(text));

        InputStream in = new ByteArrayInputStream("Καλημέρα κόσμε".getBytes(StandardCharsets.UTF_8));
        assertEquals(2, sare.process(in));
        assertEquals(List.of("Καλημέρα", "κόσμε"), words);
    }

    @Test
    void inputStreamInAnotherCharset() {
        List<String> words = new ArrayList<>();
        RegexParser sare = new RegexParser("\\p{L}+");
        sare.assignAction(0, (group, full, text) -> words.add(text));

        InputStream in = new ByteArrayInputStream("Καλημέρα".getBytes(StandardCharsets.UTF_16LE));
        assertEquals(1, sare.process(in, StandardCharsets.UTF_16LE));
        assertEquals(List.of("Καλημέρα"), words);
    }

    /** In 2013 the error was printed and the parser was left without a pattern. */
    @Test
    void invalidExpressionIsRefusedAtConstruction() {
        assertThrows(PatternSyntaxException.class, () -> new RegexParser("("));
    }

    @Test
    void unknownGroupIsRefused() {
        RegexParser sare = new RegexParser("(a)(b)");
        assertThrows(IndexOutOfBoundsException.class, () -> sare.assignAction(3, (g, f, t) -> { }));
        assertThrows(IndexOutOfBoundsException.class, () -> sare.assignAction(-1, (g, f, t) -> { }));
        assertThrows(IllegalArgumentException.class, () -> sare.assignAction("nope", (g, f, t) -> { }));
    }

    @Test
    void compiledPatternKeepsItsFlags() {
        List<String> seen = new ArrayList<>();
        RegexParser sare = new RegexParser(Pattern.compile("abc", Pattern.CASE_INSENSITIVE));
        sare.assignAction(0, (group, full, text) -> seen.add(text));

        assertEquals(2, sare.process("ABC abc"));
        assertEquals(List.of("ABC", "abc"), seen);
    }

    @Test
    void exceptionInAnActionPropagatesAndEndStillRuns() {
        List<String> log = new ArrayList<>();
        RegexParser sare = new RegexParser("x");
        sare.assignAction(0, new Recorder("r", log));
        sare.assignAction(0, (group, full, text) -> {
            throw new IllegalStateException("boom");
        });

        assertThrows(IllegalStateException.class, () -> sare.process("x"));
        assertEquals(List.of("r start", "r [0] x x", "r end"), log);
    }
}
