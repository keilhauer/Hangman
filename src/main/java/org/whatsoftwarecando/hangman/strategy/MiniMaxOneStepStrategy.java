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
 * One-step (half-a-move) minimax with risk avoidance. It first minimizes the
 * risk of a miss - the number of remaining words that do not contain the
 * guessed letter ({@link #scoreGuess}) - and, among equally safe letters,
 * minimizes the size of the biggest remaining block ({@link #tieBreak}).
 *
 * <p>NOTE: this differs from the pure greedy of the article, which minimizes
 * only the biggest block regardless of miss risk.
 *
 * <p>This class also serves as the base for {@link BruteForceMiniMaxStrategy},
 * which looks all the way to the end of the game instead of a single step. Both
 * share the same driver ({@link #bestGuess}): enumerate the allowed letters,
 * split the remaining words into the blocks the word-giver's answer would
 * create, and pick the letter whose blocks score best. They differ only in how
 * a guess is scored ({@link #scoreGuess}) - one step versus n steps of
 * look-ahead - and how ties are broken ({@link #tieBreak}).
 */
public class MiniMaxOneStepStrategy implements IGuessingStrategy {

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
	 * Scores a guess for {@link #bestGuess}; lower is better. This risk-averse
	 * one-step strategy scores by the size of the miss block (the remaining
	 * words that do not contain the letter): a letter that cannot cause a miss
	 * scores 0 and is preferred. {@link BruteForceMiniMaxStrategy} overrides it
	 * with a full game-tree look-ahead.
	 *
	 * @param cache
	 *            per-call memoization table offered to deeper look-ahead;
	 *            unused by the one-step scoring.
	 */
	protected int scoreGuess(HangmanGame hangManGame, char guess, Map<Set<Integer>, List<String>> blocks,
			Map<String, Integer> cache) {
		return missBlock(blocks);
	}

	/**
	 * Tie-breaker among guesses that share the same {@link #scoreGuess}; the
	 * lowest value wins. Among equally safe letters this one-step strategy
	 * prefers the one that shrinks the biggest block the most (see
	 * {@link #biggestBlockAfterGuess}). {@link BruteForceMiniMaxStrategy}
	 * overrides this: its full look-ahead already accounts for misses.
	 */
	protected int tieBreak(Map<Set<Integer>, List<String>> blocks) {
		return biggestBlock(blocks);
	}

	/**
	 * Size of the biggest block of words that can remain after guessing the
	 * given character - the criterion this strategy minimizes.
	 */
	public int biggestBlockAfterGuess(char guess, Wordlist wordlist) {
		return biggestBlock(splitByHitPattern(wordlist.getRemainingWords(), guess));
	}

	private int biggestBlock(Map<Set<Integer>, List<String>> blocks) {
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
	private int missBlock(Map<Set<Integer>, List<String>> blocks) {
		List<String> missBlock = blocks.get(Collections.<Integer>emptySet());
		return missBlock == null ? 0 : missBlock.size();
	}

	/**
	 * Splits the words by the answer the word-giver would have to give for the
	 * guessed letter: the set of positions where the letter occurs. The empty
	 * set means the guess is a miss. This is the partition into "blocks" the
	 * article describes, and the shared primitive of both strategies.
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
