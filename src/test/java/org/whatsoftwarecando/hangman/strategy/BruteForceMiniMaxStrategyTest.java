package org.whatsoftwarecando.hangman.strategy;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.whatsoftwarecando.hangman.GreedyCounterexample;
import org.whatsoftwarecando.hangman.HangmanGame;

/**
 * Pins down the numbers from the blog article "Warum die Greedy-Strategie bei
 * Hangman nicht immer gewinnt": on the six-word counterexample the greedy
 * strategy picks b and the word-giver can force two misses, while the
 * brute-force minimax strategy picks a and wins with at most one miss.
 */
public class BruteForceMiniMaxStrategyTest {

	private static final List<String> WORDS = GreedyCounterexample.WORDS;
	private static final Set<Character> LETTERS = GreedyCounterexample.lettersIn(WORDS);

	@Test
	public void greedyChoosesB() {
		HangmanGame game = new HangmanGame(GreedyCounterexample.createWordlist(WORDS), "abcdef",
				new MiniMaxOneStepStrategy());
		assertEquals(Character.valueOf('b'), game.bestGuess());
	}

	@Test
	public void bruteForceMiniMaxChoosesA() {
		HangmanGame game = new HangmanGame(GreedyCounterexample.createWordlist(WORDS), "abcdef",
				new BruteForceMiniMaxStrategy());
		assertEquals(Character.valueOf('a'), game.bestGuess());
	}

	@Test
	public void biggestBlocksMatchArticleTable() {
		MiniMaxOneStepStrategy greedy = new MiniMaxOneStepStrategy();
		assertEquals(3, greedy.biggestBlockAfterGuess('b', GreedyCounterexample.createWordlist(WORDS)));
		assertEquals(4, greedy.biggestBlockAfterGuess('a', GreedyCounterexample.createWordlist(WORDS)));
		assertEquals(4, greedy.biggestBlockAfterGuess('e', GreedyCounterexample.createWordlist(WORDS)));
		assertEquals(4, greedy.biggestBlockAfterGuess('c', GreedyCounterexample.createWordlist(WORDS)));
		assertEquals(4, greedy.biggestBlockAfterGuess('d', GreedyCounterexample.createWordlist(WORDS)));
		assertEquals(4, greedy.biggestBlockAfterGuess('f', GreedyCounterexample.createWordlist(WORDS)));
	}

	@Test
	public void forcedMissesMatchArticleTable() {
		BruteForceMiniMaxStrategy bruteForce = new BruteForceMiniMaxStrategy();
		assertEquals(2, bruteForce.forcedMissesAfterGuess(WORDS, LETTERS, 'b'));
		assertEquals(1, bruteForce.forcedMissesAfterGuess(WORDS, LETTERS, 'a'));
		assertEquals(1, bruteForce.forcedMissesAfterGuess(WORDS, LETTERS, 'e'));
		assertEquals(2, bruteForce.forcedMissesAfterGuess(WORDS, LETTERS, 'c'));
		assertEquals(2, bruteForce.forcedMissesAfterGuess(WORDS, LETTERS, 'd'));
		assertEquals(2, bruteForce.forcedMissesAfterGuess(WORDS, LETTERS, 'f'));
	}

	@Test
	public void perfectPlayLosesOnlyOneMiss() {
		BruteForceMiniMaxStrategy bruteForce = new BruteForceMiniMaxStrategy();
		assertEquals(1, bruteForce.forcedMisses(WORDS, LETTERS));
	}
}
