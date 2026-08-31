package org.whatsoftwarecando.hangman.strategy;

import static org.junit.Assert.assertEquals;
import static org.whatsoftwarecando.hangman.strategy.StrategyTestSupport.bestGuess;

import org.junit.Test;

/**
 * Unit tests for {@link MiniMaxOneStepSizeReductionWithRiskAvoidanceStrategy}:
 * minimizes the biggest block first, and among equally good letters prefers a
 * riskless one.
 */
public class MiniMaxOneStepSizeReductionWithRiskAvoidanceStrategyTest {

	private final MiniMaxOneStepSizeReductionWithRiskAvoidanceStrategy strategy =
			new MiniMaxOneStepSizeReductionWithRiskAvoidanceStrategy();

	@Test
	public void prefersARisklessLetterAmongEquallySmallBlocks() {
		// a and o both leave a biggest block of 1; o cannot miss, a can - so the
		// risk-avoidance tie-break chooses o (unlike pure size reduction, which
		// takes the alphabetically first letter, a)
		assertEquals(Character.valueOf('o'), bestGuess(strategy, "microsoft", "minnesota"));
	}

	@Test
	public void sizeReductionKeepsPriorityOverSafety() {
		// b leaves a biggest block of 2, the riskless a leaves 3; size wins, so b
		// is chosen (unlike the safety strategy, which takes a)
		assertEquals(Character.valueOf('b'), bestGuess(strategy, "abc", "abd", "aef", "gah"));
	}

	@Test
	public void picksTheLetterWithTheSmallestBiggestBlock() {
		// s leaves the uniquely smallest biggest block of 2
		assertEquals(Character.valueOf('s'), bestGuess(strategy, "cheap", "cheat", "chefs", "chess", "chest"));
	}
}
