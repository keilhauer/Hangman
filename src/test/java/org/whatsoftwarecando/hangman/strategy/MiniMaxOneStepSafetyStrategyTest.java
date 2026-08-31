package org.whatsoftwarecando.hangman.strategy;

import static org.junit.Assert.assertEquals;
import static org.whatsoftwarecando.hangman.strategy.StrategyTestSupport.bestGuess;

import org.junit.Test;

/**
 * Unit tests for {@link MiniMaxOneStepSafetyStrategy}: minimizes the risk of a
 * miss first, and among equally safe letters minimizes the biggest block.
 */
public class MiniMaxOneStepSafetyStrategyTest {

	private final MiniMaxOneStepSafetyStrategy strategy = new MiniMaxOneStepSafetyStrategy();

	@Test
	public void prefersARisklessLetterEvenOverASmallerBlock() {
		// a occurs in every word (miss block 0) but leaves a biggest block of 3;
		// b leaves a smaller biggest block of 2 but has miss block 2. Minimizing
		// risk first, safety chooses a (unlike the size-reduction strategies,
		// which choose b)
		assertEquals(Character.valueOf('a'), bestGuess(strategy, "abc", "abd", "aef", "gah"));
	}

	@Test
	public void choosesTheLetterThatCannotMiss() {
		// o occurs in both words and cannot miss; a would miss on microsoft
		assertEquals(Character.valueOf('o'), bestGuess(strategy, "microsoft", "minnesota"));
	}

	@Test
	public void safestLetterIsNotAlwaysOptimal() {
		// the article counterexample: the "safest" letter s (smallest miss block)
		// is still the losing move against brute-force minimax - see
		// GreedyCounterexample
		assertEquals(Character.valueOf('s'), bestGuess(strategy, "cheap", "cheat", "chefs", "chess", "chest"));
	}
}
