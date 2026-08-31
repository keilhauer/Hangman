package org.whatsoftwarecando.hangman.strategy;

import static org.junit.Assert.assertEquals;
import static org.whatsoftwarecando.hangman.strategy.StrategyTestSupport.bestGuess;

import org.junit.Test;

/**
 * Unit tests for {@link MiniMaxOneStepSizeReductionStrategy}: the article's pure
 * greedy that minimizes the biggest remaining block and ignores the risk of a
 * miss.
 */
public class MiniMaxOneStepSizeReductionStrategyTest {

	private final MiniMaxOneStepSizeReductionStrategy strategy = new MiniMaxOneStepSizeReductionStrategy();

	@Test
	public void picksTheLetterWithTheSmallestBiggestBlock() {
		// s leaves a biggest block of 2, every other letter 3 or 4
		assertEquals(Character.valueOf('s'), bestGuess(strategy, "cheap", "cheat", "chefs", "chess", "chest"));
	}

	@Test
	public void ignoresMissRiskWhenBlocksAreEqual() {
		// a and o both single out the word (biggest block 1), but a is a miss for
		// microsoft while o occurs in both words; ignoring risk, pure size
		// reduction just takes the alphabetically first letter, a
		assertEquals(Character.valueOf('a'), bestGuess(strategy, "microsoft", "minnesota"));
	}

	@Test
	public void prefersASmallerBlockOverARisklessLetter() {
		// b leaves a biggest block of 2 (but has miss block 2); a is riskless (it
		// occurs in every word) yet leaves a biggest block of 3 - size reduction
		// picks the smaller block b
		assertEquals(Character.valueOf('b'), bestGuess(strategy, "abc", "abd", "aef", "gah"));
	}
}
