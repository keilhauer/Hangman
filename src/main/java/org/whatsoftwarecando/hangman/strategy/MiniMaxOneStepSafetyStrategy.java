package org.whatsoftwarecando.hangman.strategy;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.whatsoftwarecando.hangman.HangmanGame;

/**
 * One-step (half-a-move) minimax that minimizes the risk of a miss: it picks
 * the letter with the smallest miss block (the fewest remaining words that do
 * not contain the letter), preferring a letter that cannot cause a miss at all.
 * Among equally safe letters it minimizes the size of the biggest remaining
 * block.
 *
 * <p>Playing safe one guess ahead is not the same as optimal play: see
 * {@link org.whatsoftwarecando.hangman.GreedyCounterexample}, where this
 * strategy picks the "safest" letter and still loses to the brute-force
 * minimax.
 *
 * @see MiniMaxOneStepSizeReductionStrategy
 * @see MiniMaxOneStepSizeReductionWithRiskAvoidanceStrategy
 */
public class MiniMaxOneStepSafetyStrategy extends MiniMaxOneStepStrategy {

	@Override
	protected int scoreGuess(HangmanGame hangManGame, char guess, Map<Set<Integer>, List<String>> blocks,
			Map<String, Integer> cache) {
		return missBlock(blocks);
	}

	@Override
	protected int tieBreak(Map<Set<Integer>, List<String>> blocks) {
		return biggestBlock(blocks);
	}
}
