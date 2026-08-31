package org.whatsoftwarecando.hangman.strategy;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.whatsoftwarecando.hangman.GreedyCounterexample;
import org.whatsoftwarecando.hangman.HangmanGame;

/**
 * Pins down the numbers from the blog article "Warum die Greedy-Strategie bei
 * Hangman nicht immer gewinnt": on the counterexample radials/radians/radiant/
 * radiate the risk-averse one-step greedy strategy picks n and the word-giver
 * can force two misses, while the brute-force minimax strategy picks s and wins
 * with at most one miss.
 */
public class BruteForceMiniMaxStrategyTest {

	private static final List<String> WORDS = GreedyCounterexample.WORDS;
	private static final Set<Character> LETTERS = GreedyCounterexample.lettersIn(WORDS);
	private static final String ALLOWED = "adeilnrst";

	@Test
	public void greedyChoosesN() {
		HangmanGame game = new HangmanGame(GreedyCounterexample.createWordlist(WORDS), ALLOWED,
				new MiniMaxOneStepStrategy());
		assertEquals(Character.valueOf('n'), game.bestGuess());
	}

	@Test
	public void bruteForceMiniMaxChoosesS() {
		HangmanGame game = new HangmanGame(GreedyCounterexample.createWordlist(WORDS), ALLOWED,
				new BruteForceMiniMaxStrategy());
		assertEquals(Character.valueOf('s'), game.bestGuess());
	}

	@Test
	public void biggestBlocksMatchArticleTable() {
		MiniMaxOneStepStrategy greedy = new MiniMaxOneStepStrategy();
		assertEquals(2, greedy.biggestBlockAfterGuess('n', GreedyCounterexample.createWordlist(WORDS)));
		assertEquals(2, greedy.biggestBlockAfterGuess('s', GreedyCounterexample.createWordlist(WORDS)));
		assertEquals(2, greedy.biggestBlockAfterGuess('t', GreedyCounterexample.createWordlist(WORDS)));
		assertEquals(3, greedy.biggestBlockAfterGuess('l', GreedyCounterexample.createWordlist(WORDS)));
		assertEquals(3, greedy.biggestBlockAfterGuess('e', GreedyCounterexample.createWordlist(WORDS)));
	}

	@Test
	public void forcedMissesMatchArticleTable() {
		BruteForceMiniMaxStrategy bruteForce = new BruteForceMiniMaxStrategy();
		assertEquals(2, bruteForce.forcedMissesAfterGuess(WORDS, LETTERS, 'n'));
		assertEquals(1, bruteForce.forcedMissesAfterGuess(WORDS, LETTERS, 's'));
		assertEquals(2, bruteForce.forcedMissesAfterGuess(WORDS, LETTERS, 't'));
		assertEquals(2, bruteForce.forcedMissesAfterGuess(WORDS, LETTERS, 'l'));
		assertEquals(2, bruteForce.forcedMissesAfterGuess(WORDS, LETTERS, 'e'));
	}

	@Test
	public void perfectPlayLosesOnlyOneMiss() {
		BruteForceMiniMaxStrategy bruteForce = new BruteForceMiniMaxStrategy();
		assertEquals(1, bruteForce.forcedMisses(WORDS, LETTERS));
	}
}
