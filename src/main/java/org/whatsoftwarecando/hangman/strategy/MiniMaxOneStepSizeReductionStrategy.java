package org.whatsoftwarecando.hangman.strategy;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.whatsoftwarecando.hangman.HangmanGame;

/**
 * The article's greedy strategy: one-step (half-a-move) minimax that minimizes
 * the size of the biggest block of words that can remain after a single guess.
 * It does not consider the risk of a miss, so among letters that reduce the
 * biggest block equally well it simply picks the first in alphabetical order.
 *
 * @see MiniMaxOneStepSizeReductionWithRiskAvoidanceStrategy
 * @see MiniMaxOneStepSafetyStrategy
 */
public class MiniMaxOneStepSizeReductionStrategy extends MiniMaxOneStepStrategy {

	@Override
	protected int scoreGuess(HangmanGame hangManGame, char guess, Map<Set<Integer>, List<String>> blocks,
			Map<String, Integer> cache) {
		return biggestBlock(blocks);
	}
}
