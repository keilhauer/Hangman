package org.whatsoftwarecando.hangman.strategy;

import java.util.Arrays;
import java.util.List;

import org.whatsoftwarecando.hangman.GreedyCounterexample;
import org.whatsoftwarecando.hangman.HangmanGame;
import org.whatsoftwarecando.hangman.IGuessingStrategy;

/**
 * Small helper for the greedy-strategy unit tests: runs a strategy against a
 * word list (all 26 letters allowed as guesses) and returns its first guess.
 */
final class StrategyTestSupport {

	private static final String ALL_LETTERS = "abcdefghijklmnopqrstuvwxyz";

	private StrategyTestSupport() {
	}

	static Character bestGuess(IGuessingStrategy strategy, String... words) {
		List<String> wordList = Arrays.asList(words);
		return new HangmanGame(GreedyCounterexample.createWordlist(wordList), ALL_LETTERS, strategy).bestGuess();
	}
}
