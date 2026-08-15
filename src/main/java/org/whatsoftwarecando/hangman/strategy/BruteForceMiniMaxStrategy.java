package org.whatsoftwarecando.hangman.strategy;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.whatsoftwarecando.hangman.HangmanGame;
import org.whatsoftwarecando.hangman.IGuessingStrategy;

/**
 * Full-depth brute-force minimax. Chooses the letter that minimizes the number
 * of misses the word-giver can force in the worst case, assuming perfect play
 * on both sides until the word is uniquely identified.
 *
 * In contrast to {@link MiniMaxOneStepStrategy} (the greedy heuristic, which
 * only looks half a move ahead), this strategy evaluates the complete game
 * tree. Its runtime is exponential in the size of the wordlist, so it is only
 * practical for small lists.
 */
public class BruteForceMiniMaxStrategy implements IGuessingStrategy {

	@Override
	public Character bestGuess(HangmanGame hangManGame) {
		List<String> remainingWords = hangManGame.getWordlist().getRemainingWords();
		Set<Character> availableCharacters = new TreeSet<Character>(hangManGame.getCharactersAllowedForGuesses());
		Map<String, Integer> cache = new HashMap<String, Integer>();
		Character bestGuess = null;
		int bestForcedMisses = Integer.MAX_VALUE;
		for (Character currentChar : availableCharacters) {
			Map<Set<Integer>, List<String>> blocks = splitByHitPattern(remainingWords, currentChar);
			if (blocks.size() < 2) {
				// a letter that leaves only one block yields no information
				continue;
			}
			int forcedMisses = worstCaseMisses(blocks, remove(availableCharacters, currentChar), cache);
			if (forcedMisses < bestForcedMisses) {
				bestForcedMisses = forcedMisses;
				bestGuess = currentChar;
			}
		}
		return bestGuess;
	}

	/**
	 * Number of misses the word-giver can force in the worst case if both
	 * sides play perfectly from this position on.
	 */
	public int forcedMisses(List<String> words, Set<Character> availableCharacters) {
		return minimaxValue(words, new TreeSet<Character>(availableCharacters), new HashMap<String, Integer>());
	}

	/**
	 * Number of misses the word-giver can force in the worst case if the
	 * guesser starts with the given guess and plays perfectly afterwards.
	 */
	public int forcedMissesAfterGuess(List<String> words, Set<Character> availableCharacters, char guess) {
		Map<Set<Integer>, List<String>> blocks = splitByHitPattern(words, guess);
		return worstCaseMisses(blocks, remove(new TreeSet<Character>(availableCharacters), guess),
				new HashMap<String, Integer>());
	}

	private int minimaxValue(List<String> words, Set<Character> availableCharacters, Map<String, Integer> cache) {
		if (words.size() <= 1) {
			return 0;
		}
		String cacheKey = words + "|" + availableCharacters;
		Integer cachedValue = cache.get(cacheKey);
		if (cachedValue != null) {
			return cachedValue;
		}
		int bestForcedMisses = Integer.MAX_VALUE;
		for (Character currentChar : availableCharacters) {
			Map<Set<Integer>, List<String>> blocks = splitByHitPattern(words, currentChar);
			if (blocks.size() < 2) {
				continue;
			}
			int forcedMisses = worstCaseMisses(blocks, remove(availableCharacters, currentChar), cache);
			if (forcedMisses < bestForcedMisses) {
				bestForcedMisses = forcedMisses;
			}
		}
		if (bestForcedMisses == Integer.MAX_VALUE) {
			// no available letter distinguishes the remaining words, so the
			// guesser can only try the words one by one
			bestForcedMisses = words.size() - 1;
		}
		cache.put(cacheKey, bestForcedMisses);
		return bestForcedMisses;
	}

	private int worstCaseMisses(Map<Set<Integer>, List<String>> blocks, Set<Character> remainingCharacters,
			Map<String, Integer> cache) {
		int worstCase = 0;
		for (Entry<Set<Integer>, List<String>> currentBlock : blocks.entrySet()) {
			boolean isMiss = currentBlock.getKey().isEmpty();
			int forcedMisses = (isMiss ? 1 : 0)
					+ minimaxValue(currentBlock.getValue(), remainingCharacters, cache);
			if (forcedMisses > worstCase) {
				worstCase = forcedMisses;
			}
		}
		return worstCase;
	}

	/**
	 * Splits the words by the answer the word-giver would have to give for the
	 * guessed letter: the set of positions where the letter occurs. The empty
	 * set means the guess is a miss.
	 */
	private Map<Set<Integer>, List<String>> splitByHitPattern(List<String> words, char guess) {
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

	private Set<Character> remove(Set<Character> characters, Character toRemove) {
		Set<Character> result = new TreeSet<Character>(characters);
		result.remove(toRemove);
		return result;
	}
}
