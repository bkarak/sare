# SARE

Simple API for Regular Expressions. You assign an action to a capture group, and the parser
calls it for every match that group takes part in, the way a SAX parser calls you for every
element of an XML document. It runs on `java.util.regex`.

SARE began in 2006 as a byproduct of [FIRE/J](https://github.com/bkarak/firej-oss), the DFA
regular expression engine; this repository holds it from 2012.

## Using it

An action only has to implement `match`, so it can be a lambda:

```java
RegexParser sare = new RegexParser("(?<key>\\w+)=(?<value>\\w+)");
sare.assignAction("key", (group, full, text) -> System.out.println("key " + text));
sare.assignAction(2, (group, full, text) -> System.out.println("value " + text));

int matches = sare.process("host=example, port=8080");   // 2
```

An action that needs to set something up or tear it down implements `start` and `end` too.
This is the example from 2013, which reads the four numbers of an IPv4 address:

```java
public class ExampleSARE {
    public static void main(String[] args) {
        RegexParser sare = new RegexParser("([0-9]+)\\.([0-9]+)\\.([0-9]+)\\.([0-9]+)");
        ConsoleOut co = new ConsoleOut();

        for (int group = 0; group <= 4; group++) {
            sare.assignAction(group, co);
        }

        sare.process("192.255.255.150");
    }
}

class ConsoleOut implements IGroupAction {
    public void start() {
        System.out.println("Starting");
    }

    public void end() {
        System.out.println("End");
    }

    public void match(int group, String full, String text) {
        System.out.println("[" + group + "] - " + text);
    }
}
```

```
Starting
[0] - 192.255.255.150
[1] - 192
[2] - 255
[3] - 255
[4] - 150
End
```

What gets called, and when:

- **`start()`** once per `process` call on each action, however many groups it is assigned to,
  and **`end()`** once after the last match. `end()` runs even when reading the input failed.
- **`match(group, full, text)`** for each match, for each group of it that has actions: in group
  order, and within a group in the order the actions were assigned. `full` is the whole match.
  A group that took no part in a match, like `(b)` in `(a)|(b)` against `a`, is not reported.
- An invalid expression is refused when you build the parser (`PatternSyntaxException`), and a
  group the expression does not have when you assign to it.

## Input

| Method | Reads |
| --- | --- |
| `process(CharSequence)` | the text as it is |
| `process(BufferedReader)` | one line at a time: memory holds a line, not the input, and a match never spans a line break |
| `process(InputStream)` | the whole stream, decoded as UTF-8, then matched as one text |
| `process(InputStream, Charset)` | the same, in another encoding |

Each returns the number of matches. A read error goes to the parser's error handler, which you
set with `setError` and which prints on standard error by default. An exception thrown by an
action is not a read error: it leaves `process` as it is.

## Where it stops

- **For one action over a short text, the JDK is enough.** Since Java 9,
  `pattern.matcher(text).results()` is a stream of matches. SARE earns its keep when several
  actions share a parse, or when they have something to set up and tear down around it.
- **Only `BufferedReader` input is streamed.** The other inputs are held in memory whole.
- **Matching is `java.util.regex`'s**: leftmost-first, with backtracking.

## Building

JDK 21 or later, Maven:

```bash
mvn            # compiles, runs the 16 tests, writes target/sare-3.0.0.jar
```

There are no runtime dependencies.

## What changed from 2.0

Maven and JDK 21 instead of Ant and Java 1.6, actions as lambdas, named groups, and fixes for the
defects 2.0 carried: reading from a `BufferedReader` never worked, `start` and `end` ran once per
group instead of once per parse, and an invalid expression left a parser that failed later with a
`NullPointerException`. [CHANGELOG.md](CHANGELOG.md) lists every change.

## License

BSD 3-Clause. See [LICENSE](LICENSE).
