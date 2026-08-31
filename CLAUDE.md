# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Purpose

This project complements the blog article in the project root
(`warum-die-greedy-strategie-bei-hangman-nicht-immer-gewinnt.md.pdf`, "Why the
greedy strategy doesn't always win at Hangman"). **Goal: every piece of code
shown in the article must exist as Java code in this project** (the article's
Python appendix has been ported as `GreedyCounterexample` +
`BruteForceMiniMaxStrategy`).

## Build & test

Plain Eclipse project, no Maven/Gradle. Java 8 source level, UTF-8, JUnit 4
(via the Eclipse JUnit container; no jars in `lib/`). From the command line:

```
javac -encoding UTF-8 --release 8 -d bin $(find src/main/java -name '*.java')
java -cp bin org.whatsoftwarecando.hangman.GreedyCounterexample
```

Tests need a JUnit 4 + Hamcrest jar on the classpath (e.g. from
`~/.m2/repository`), then run via `org.junit.runner.JUnitCore`.

## Architecture

- `HangmanHelper` — interactive CLI (main); the `.launch` files in the project
  root run it against the word lists in `src/main/resources`. It narrows the
  candidate list from the word-giver's answers, suggests the next guess (using
  `MiniMaxOneStepSizeReductionWithRiskAvoidanceStrategy`), and counts misses
- `Wordlist` / `HangmanGame` — candidate words + guessed-character state;
  `addRestriction(char, places...)` uses **1-based** positions and encodes the
  word-giver's answer (empty places = miss)
- `IGuessingStrategy` implementations in `strategy/`:
  - `MiniMaxOneStepStrategy` — **abstract** base of the one-step (half-a-move)
    minimax family. Its driver enumerates the allowed letters, splits the
    remaining words into blocks by the word-giver's answer, and picks the
    letter whose blocks score best. Subclasses supply two hooks: `scoreGuess`
    (primary) and `tieBreak` (secondary), each built from `biggestBlock` and
    `missBlock`. The three concrete greedy strategies are:
    - `MiniMaxOneStepSizeReductionStrategy` — the article's original greedy:
      minimize the biggest block, ignore miss risk (no tie-break)
    - `MiniMaxOneStepSizeReductionWithRiskAvoidanceStrategy` — minimize the
      biggest block, tie-break toward the smaller miss block (riskless if any)
    - `MiniMaxOneStepSafetyStrategy` — minimize the miss block first, tie-break
      toward the smaller biggest block
  - `BruteForceMiniMaxStrategy` — same base/driver, but `scoreGuess` does a
    full game-tree look-ahead: minimizes the worst-case number of forced misses
    (exponential; small word lists only)
  - `MaximumSuccessProbabilityStrategy` — picks the letter occurring in the
    most remaining words
- `GreedyCounterexample` — main that reproduces the article's counterexample
  table for the five words `cheap/cheat/chefs/chess/chest`: the risk-averse
  greedy (`MiniMaxOneStepSafetyStrategy`) plays the "safest" letter `s` and
  loses (two forced misses) while brute-force minimax picks `a` and wins (one
  miss). Output text comes from `messages[_de].properties` (English default;
  pass `de` as the first arg for the German article). `BruteForceMiniMaxStrategyTest`
  pins its numbers; each greedy strategy also has its own `*Test`
- Each greedy strategy has a unit test in `strategy/` (shared helper
  `StrategyTestSupport`); the tests use discriminating word sets so a test
  fails if a strategy's criteria or their order are swapped

Word patterns are represented as the set of positions where the guessed letter
occurs; the empty set means a miss. This is the same partition ("blocks") the
article describes.
