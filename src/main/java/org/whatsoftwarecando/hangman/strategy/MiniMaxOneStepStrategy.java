package org.whatsoftwarecando.hangman.strategy;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.whatsoftwarecando.hangman.HangmanGame;
import org.whatsoftwarecando.hangman.IGuessingStrategy;
import org.whatsoftwarecando.hangman.Wordlist;

/**
 * Abstract base of the one-step (half-a-move) minimax strategies. It provides
 * the shared driver ({@link #bestGuess}): enumerate the allowed letters, split
 * the remaining words into the blocks the word-giver's answer would create, and
 * pick the letter whose blocks score best. Concrete strategies differ only in
 * how a guess is scored ({@link #scoreGuess}, primary) and how ties are broken
 * ({@link #tieBreak}, secondary); both criteria are computed from the two block
 * measures {@link #biggestBlock} and {@link #missBlock}:
 *
 * <ul>
 * <li>{@link MiniMaxOneStepSizeReductionStrategy} - minimize the biggest block
 * (the article's greedy), no risk consideration.</li>
 * <li>{@link MiniMaxOneStepSizeReductionWithRiskAvoidanceStrategy} - minimize
 * the biggest block, and among equally good letters prefer a riskless one.</li>
 * <li>{@link MiniMaxOneStepSafetyStrategy} - minimize the risk of a miss first,
 * and among equally safe letters minimize the biggest block.</li>
 * </ul>
 *
 * {@link BruteForceMiniMaxStrategy} is built on the same driver but replaces
 * the one-step {@link #scoreGuess} with a full n-step look-ahead.
 */
public abstract class MiniMaxOneStepStrategy implements IGuessingStrategy {

	@Override
	public Character bestGuess(HangmanGame hangManGame) {
		List<String> remainingWords = hangManGame.getWordlist().getRemainingWords();
		Map<String, Integer> cache = new HashMap<String, Integer>();
		Character bestGuess = null;
		int bestScore = Integer.MAX_VALUE;
		int bestTieBreak = Integer.MAX_VALUE;
		for (char currentChar : new TreeSet<Character>(hangManGame.getCharactersAllowedForGuesses())) {
			Map<Set<Integer>, List<String>> blocks = splitByHitPattern(remainingWords, currentChar);
			if (blocks.size() < 2) {
				// a letter that leaves only one block yields no information
				continue;
			}
			int score = scoreGuess(hangManGame, currentChar, blocks, cache);
			int tie = tieBreak(blocks);
			if (score < bestScore || (score == bestScore && tie < bestTieBreak)) {
				bestScore = score;
				bestTieBreak = tie;
				bestGuess = currentChar;
			}
		}
		return bestGuess;
	}

	/**
	 * Scores a guess for {@link #bestGuess}; lower is better. The one-step
	 * strategies return either {@link #biggestBlock} or {@link #missBlock};
	 * {@link BruteForceMiniMaxStrategy} returns a full game-tree look-ahead.
	 *
	 * @param cache
	 *            per-call memoization table offered to deeper look-ahead;
	 *            unused by the one-step scoring.
	 */
	protected abstract int scoreGuess(HangmanGame hangManGame, char guess, Map<Set<Integer>, List<String>> blocks,
			Map<String, Integer> cache);

	/**
	 * Tie-breaker among guesses that share the same {@link #scoreGuess}; the
	 * lowest value wins. The default breaks no ties (so the first letter in
	 * alphabetical order wins); subclasses override it with {@link #biggestBlock}
	 * or {@link #missBlock}.
	 */
	protected int tieBreak(Map<Set<Integer>, List<String>> blocks) {
		return 0;
	}

	/**
	 * Size of the biggest block of words that can remain after guessing the
	 * given character - the criterion the size-reduction strategies minimize.
	 */
	public int biggestBlockAfterGuess(char guess, Wordlist wordlist) {
		return biggestBlock(splitByHitPattern(wordlist.getRemainingWords(), guess));
	}

	/**
	 * Size of the biggest block: the worst-case number of words still lumped
	 * together after the guess.
	 */
	protected int biggestBlock(Map<Set<Integer>, List<String>> blocks) {
		int biggest = 0;
		for (List<String> block : blocks.values()) {
			if (block.size() > biggest) {
				biggest = block.size();
			}
		}
		return biggest;
	}

	/**
	 * Size of the block of words for which the guess is a miss (the words not
	 * containing the letter); 0 when the letter occurs in every remaining word
	 * and hence cannot cause a miss. The miss block is the one keyed by the
	 * empty hit pattern.
	 */
	protected int missBlock(Map<Set<Integer>, List<String>> blocks) {
		List<String> missBlock = blocks.get(Collections.<Integer>emptySet());
		return missBlock == null ? 0 : missBlock.size();
	}

	/**
	 * Splits the words by the answer the word-giver would have to give for the
	 * guessed letter: the set of positions where the letter occurs. The empty
	 * set means the guess is a miss. This is the partition into "blocks" the
	 * article describes, and the shared primitive of all strategies.
	 */
	protected Map<Set<Integer>, List<String>> splitByHitPattern(List<String> words, char guess) {
		Map<Set<Integer>, List<String>> blocks = new LinkedHashMap<Set<Integer>, List<String>>();
		for (String currentWord : words) {
			Set<Integer> hitPattern = new HashSet<Integer>();
			for (int i = 0; i < currentWord.length(); i++) {
				if (currentWord.charAt(i) == guess) {
					hitPattern.add(i);
				}
			}
			List<String> block = blocks.get(hitPattern);
			if (block == null) {
				block = new LinkedList<String>();
				blocks.put(hitPattern, block);
			}
			block.add(currentWord);
		}
		return blocks;
	}
}
