package org.whatsoftwarecando.hangman.strategy;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.whatsoftwarecando.hangman.HangmanGame;

/**
 * One-step (half-a-move) minimax that minimizes the size of the biggest block
 * of words that can remain (like {@link MiniMaxOneStepSizeReductionStrategy}),
 * but among letters that reduce the biggest block equally well prefers a
 * riskless one - a letter with the smallest miss block, ideally one that cannot
 * cause a miss at all. For example, with only "microsoft" and "minnesota" left
 * both 'a' and 'o' single out the word, but 'a' is a miss for "microsoft" while
 * 'o' occurs in both words, so 'o' is chosen.
 *
 * @see MiniMaxOneStepSizeReductionStrategy
 * @see MiniMaxOneStepSafetyStrategy
 */
public class MiniMaxOneStepSizeReductionWithRiskAvoidanceStrategy extends MiniMaxOneStepStrategy {

	@Override
	protected int scoreGuess(HangmanGame hangManGame, char guess, Map<Set<Integer>, List<String>> blocks,
			Map<String, Integer> cache) {
		return biggestBlock(blocks);
	}

	@Override
	protected int tieBreak(Map<Set<Integer>, List<String>> blocks) {
		return missBlock(blocks);
	}
}
