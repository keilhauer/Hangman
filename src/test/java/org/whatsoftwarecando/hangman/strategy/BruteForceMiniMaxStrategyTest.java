package org.whatsoftwarecando.hangman.strategy;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.whatsoftwarecando.hangman.GreedyCounterexample;
import org.whatsoftwarecando.hangman.HangmanGame;

/**
 * Pins down the numbers from the blog article "Warum die Greedy-Strategie bei
 * Hangman nicht immer gewinnt": on the counterexample cheap/cheat/chefs/chess/
 * chest the risk-averse one-step greedy strategy picks the "safest" letter s
 * and the word-giver can force two misses, while the brute-force minimax
 * strategy picks the riskier a and wins with at most one miss.
 */
public class BruteForceMiniMaxStrategyTest {

	private static final List<String> WORDS = GreedyCounterexample.WORDS;
	private static final Set<Character> LETTERS = GreedyCounterexample.lettersIn(WORDS);
	private static final String ALLOWED = "acefhpst";

	@Test
	public void greedyChoosesS() {
		HangmanGame game = new HangmanGame(GreedyCounterexample.createWordlist(WORDS), ALLOWED,
				new MiniMaxOneStepStrategy());
		assertEquals(Character.valueOf('s'), game.bestGuess());
	}

	@Test
	public void bruteForceMiniMaxChoosesA() {
		HangmanGame game = new HangmanGame(GreedyCounterexample.createWordlist(WORDS), ALLOWED,
				new BruteForceMiniMaxStrategy());
		assertEquals(Character.valueOf('a'), game.bestGuess());
	}

	@Test
	public void biggestBlocksMatchArticleTable() {
		MiniMaxOneStepStrategy greedy = new MiniMaxOneStepStrategy();
		assertEquals(2, greedy.biggestBlockAfterGuess('s', GreedyCounterexample.createWordlist(WORDS)));
		assertEquals(3, greedy.biggestBlockAfterGuess('a', GreedyCounterexample.createWordlist(WORDS)));
		assertEquals(3, greedy.biggestBlockAfterGuess('t', GreedyCounterexample.createWordlist(WORDS)));
		assertEquals(4, greedy.biggestBlockAfterGuess('f', GreedyCounterexample.createWordlist(WORDS)));
		assertEquals(4, greedy.biggestBlockAfterGuess('p', GreedyCounterexample.createWordlist(WORDS)));
	}

	@Test
	public void forcedMissesMatchArticleTable() {
		BruteForceMiniMaxStrategy bruteForce = new BruteForceMiniMaxStrategy();
		assertEquals(2, bruteForce.forcedMissesAfterGuess(WORDS, LETTERS, 's'));
		assertEquals(1, bruteForce.forcedMissesAfterGuess(WORDS, LETTERS, 'a'));
		assertEquals(2, bruteForce.forcedMissesAfterGuess(WORDS, LETTERS, 't'));
		assertEquals(2, bruteForce.forcedMissesAfterGuess(WORDS, LETTERS, 'f'));
		assertEquals(2, bruteForce.forcedMissesAfterGuess(WORDS, LETTERS, 'p'));
	}

	@Test
	public void perfectPlayLosesOnlyOneMiss() {
		BruteForceMiniMaxStrategy bruteForce = new BruteForceMiniMaxStrategy();
		assertEquals(1, bruteForce.forcedMisses(WORDS, LETTERS));
	}
}
