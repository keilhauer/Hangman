package org.whatsoftwarecando.hangman;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.whatsoftwarecando.hangman.strategy.MiniMaxOneStepSafetyStrategy;
import org.whatsoftwarecando.hangman.strategy.MiniMaxOneStepSizeReductionStrategy;
import org.whatsoftwarecando.hangman.strategy.MiniMaxOneStepSizeReductionWithRiskAvoidanceStrategy;

/**
 * Reproduces the average-misses and lost-games tables in the blog article.
 *
 * <p>Everything is measured against the <em>complete</em> bundled dictionary for
 * each word length: the guesser knows every word of that length, and every word
 * is played once as the word-giver's secret. There is no sampling and no random
 * seed, so the numbers are exact and reproducible by construction.
 *
 * <p>Rather than simulating one game per word - which would repeat the same
 * work over and over, since all games start from the same list - it exploits
 * that a deterministic strategy induces a single decision tree: at each node the
 * strategy picks one letter, and the word-giver's answer splits the remaining
 * words into blocks. Walking that tree once yields the exact number of misses
 * for every word simultaneously, which is orders of magnitude faster than
 * playing the games individually.
 *
 * <p>Per strategy it prints the average and maximum number of misses and how
 * many words are lost with {@value #LIVES} lives (i.e. need more than
 * {@value #LIVES} misses).
 */
public class Benchmark {

	private static final int LIVES = 6;
	private static final int MIN_WORD_LENGTH = 5;

	/**
	 * Longest word length measured. Nine is where the story ends: the guesser
	 * loses practically no games any more, so longer words add nothing.
	 */
	private static final int MAX_WORD_LENGTH = 9;

	private static final String RESOURCE_PATH = "/org/whatsoftwarecando/hangman/";
	private static final String[] WORDLIST_RESOURCES = { "word_list_german_uppercase_spell_checked.txt",
			"word_list_english_uppercase_spell_checked.txt" };
	private static final String[] WORDLIST_LABELS = { "German", "English" };

	public static void main(String[] args) {
		for (int list = 0; list < WORDLIST_RESOURCES.length; list++) {
			for (int length = MIN_WORD_LENGTH; length <= MAX_WORD_LENGTH; length++) {
				Wordlist dictionary = new Wordlist(
						Benchmark.class.getResourceAsStream(RESOURCE_PATH + WORDLIST_RESOURCES[list]), length);
				List<String> words = dictionary.getRemainingWords();
				Set<Character> letters = lettersIn(words);

				System.out.printf("%n%s, %d letters | %d words%n", WORDLIST_LABELS[list], length, words.size());
				report("size only", new MiniMaxOneStepSizeReductionStrategy(), words, letters);
				report("size+risk", new MiniMaxOneStepSizeReductionWithRiskAvoidanceStrategy(), words, letters);
				report("safety", new MiniMaxOneStepSafetyStrategy(), words, letters);
			}
		}
	}

	private static void report(String name, IGuessingStrategy strategy, List<String> words, Set<Character> letters) {
		Map<String, Integer> missesPerWord = new HashMap<String, Integer>();
		walk(words, letters, 0, strategy, missesPerWord);

		long totalMisses = 0;
		int maxMisses = 0;
		int lost = 0;
		for (String word : words) {
			int misses = missesPerWord.get(word);
			totalMisses += misses;
			maxMisses = Math.max(maxMisses, misses);
			if (misses > LIVES) {
				lost++;
			}
		}
		System.out.printf("  %-10s avg %.2f  max %d  lost %d of %d  (won %.1f%%)%n", name,
				(double) totalMisses / words.size(), maxMisses, lost, words.size(),
				100.0 * (words.size() - lost) / words.size());
	}

	/**
	 * Walks the strategy's decision tree, recording for every word how many
	 * misses the guesser accumulates before only that word remains. Mirrors the
	 * interactive game exactly: the guesser stops once a single candidate is
	 * left, and a guess counts as a miss when the secret does not contain it.
	 */
	private static void walk(List<String> words, Set<Character> allowedCharacters, int missesSoFar,
			IGuessingStrategy strategy, Map<String, Integer> missesPerWord) {
		if (words.size() <= 1) {
			for (String word : words) {
				missesPerWord.put(word, missesSoFar);
			}
			return;
		}
		HangmanGame game = new HangmanGame(GreedyCounterexample.createWordlist(words), asString(allowedCharacters),
				strategy);
		Character guess = game.bestGuess();
		if (guess == null) {
			// no letter distinguishes the rest; they can only be tried one by one
			for (String word : words) {
				missesPerWord.put(word, missesSoFar + words.size() - 1);
			}
			return;
		}
		Set<Character> remaining = new TreeSet<Character>(allowedCharacters);
		remaining.remove(guess);
		for (Map.Entry<Set<Integer>, List<String>> block : splitByHitPattern(words, guess).entrySet()) {
			int missCost = block.getKey().isEmpty() ? 1 : 0;
			walk(block.getValue(), remaining, missesSoFar + missCost, strategy, missesPerWord);
		}
	}

	/**
	 * Splits the words by the word-giver's answer for the guessed letter: the set
	 * of positions where it occurs, the empty set meaning a miss.
	 */
	private static Map<Set<Integer>, List<String>> splitByHitPattern(List<String> words, char guess) {
		Map<Set<Integer>, List<String>> blocks = new LinkedHashMap<Set<Integer>, List<String>>();
		for (String word : words) {
			Set<Integer> hitPattern = new HashSet<Integer>();
			for (int i = 0; i < word.length(); i++) {
				if (word.charAt(i) == guess) {
					hitPattern.add(i);
				}
			}
			List<String> block = blocks.get(hitPattern);
			if (block == null) {
				block = new LinkedList<String>();
				blocks.put(hitPattern, block);
			}
			block.add(word);
		}
		return blocks;
	}

	private static Set<Character> lettersIn(List<String> words) {
		Set<Character> letters = new TreeSet<Character>();
		for (String word : words) {
			for (char c : word.toCharArray()) {
				letters.add(c);
			}
		}
		return letters;
	}

	private static String asString(Set<Character> letters) {
		StringBuilder sb = new StringBuilder();
		for (char c : letters) {
			sb.append(c);
		}
		return sb.toString();
	}

	private Benchmark() {
	}
}
