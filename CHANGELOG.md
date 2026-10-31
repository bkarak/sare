# Changelog

All notable changes to SARE. The format follows
[Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and versions follow
[Semantic Versioning](https://semver.org/).

## [3.0.0] - 2026-10-31

The 2013 library, rebuilt. The package, the class names and `assignAction` / `process` are
unchanged.

### Fixed

- **`process(BufferedReader)` never worked.** The loop read past the last line and matched the
  `null` that marks the end of the input, which threw a `NullPointerException`. The handler
  printed it, the call returned 0 whatever had matched, and `end()` was never called. It now
  reads to the end and returns the number of matches.
- **`start()` and `end()` ran once per group, not once per parse.** An action assigned to five
  groups was started five times; the README's own example printed "Starting" and "End" five
  times each. Each action now sees each once per `process` call.
- **An invalid expression left a broken parser.** The constructor printed the
  `PatternSyntaxException` and carried on without a pattern, so the first `assignAction` threw a
  `NullPointerException`. The constructor now throws the `PatternSyntaxException`.
- **`process(InputStream)` could read part of the stream.** It read `available()` bytes in a
  single `read`, which on a pipe or a socket is whatever has arrived so far, and decoded them in
  the platform charset. It now reads the stream to the end and decodes it as UTF-8.
- **A group that took no part in a match was reported with `null` text.** It is now not
  reported for that match.
- **`end()` was skipped when reading the input failed.** It now always runs.
- **An action's own exception was reported as a read error.** `process(InputStream)` and
  `process(BufferedReader)` caught every exception, an action's included, printed it and
  returned 0. Only read errors go to the handler now; an action's exception propagates.
- **Assigning to a group the expression does not have was accepted**, and the action never
  ran. It is now refused with `IndexOutOfBoundsException`.
- The javadoc described `match`'s first argument as the match count. It is the group number.

### Added

- `IGroupAction.start()` and `end()` have empty defaults, so an action can be a lambda.
- `assignAction(String, IGroupAction)` for named groups.
- `process(InputStream, Charset)`.
- JUnit tests, and CI on JDK 21 and 25.

### Changed

- Maven and JDK 21 instead of Ant and Java 1.6, which no JDK since 12 compiles.
- `ActionList` is package-private. Nothing in the API handed one out.
- The licence is the BSD 3-Clause text, in `LICENSE`. The copy in the 2013 README had lost the
  second half of its first clause.
- The example moved out of `org.sare.tests` into the README and the tests.
