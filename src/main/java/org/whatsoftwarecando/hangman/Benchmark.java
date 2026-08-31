package org.whatsoftwarecando.hangman;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;

import org.whatsoftwarecando.hangman.strategy.MiniMaxOneStepSafetyStrategy;
import org.whatsoftwarecando.hangman.strategy.MiniMaxOneStepSizeReductionStrategy;
import org.whatsoftwarecando.hangman.strategy.MiniMaxOneStepSizeReductionWithRiskAvoidanceStrategy;

/**
 * Reproduces the average-misses table in the blog article. For each bundled
 * word list and word length (5-7) it draws a fixed sample of up to
 * {@value #SAMPLE_SIZE} words and lets each one-step greedy strategy guess every
 * sampled word once, as if it were the word-giver's secret. Per strategy it
 * prints the average and maximum number of misses and how many words are lost
 * with {@value #LIVES} lives (i.e. need more than {@value #LIVES} misses).
 *
 * <p>The sample is drawn by shuffling the matching words with a <em>fixed</em>
 * {@link Random} seed ({@value #SEED}) and truncating, so the published numbers
 * are exactly reproducible. Change the seed only if you intend to publish new
 * numbers.
 */
public class Benchmark {

	private static final int SEED = 1;
	private static final int SAMPLE_SIZE = 1000;
	private static final int LIVES = 6;

	private static final String[] WORDLIST_RESOURCES = { "word_list_german_uppercase_spell_checked.txt",
			"word_list_english_uppercase_spell_checked.txt" };
	private static final String[] WORDLIST_LABELS = { "Deutsch", "Englisch" };

	public static void main(String[] args) throws Exception {
		for (int list = 0; list < WORDLIST_RESOURCES.length; list++) {
			for (int length = 5; length <= 7; length++) {
				List<String> sample = sampleWords(WORDLIST_RESOURCES[list], length);
				System.out.printf("%s, %d Buchstaben (%d Woerter):%n", WORDLIST_LABELS[list], length, sample.size());
				report("nur Groesse", new MiniMaxOneStepSizeReductionStrategy(), sample);
				report("Groesse+Risiko", new MiniMaxOneStepSizeReductionWithRiskAvoidanceStrategy(), sample);
				report("Sicherheit", new MiniMaxOneStepSafetyStrategy(), sample);
			}
		}
	}

	private static void report(String name, IGuessingStrategy strategy, List<String> sample) {
		int totalMisses = 0;
		int maxMisses = 0;
		int lost = 0;
		for (String secret : sample) {
			int misses = play(secret, sample, strategy);
			totalMisses += misses;
			maxMisses = Math.max(maxMisses, misses);
			if (misses > LIVES) {
				lost++;
			}
		}
		System.out.printf("  %-15s avg %.2f  max %d  lost %d%n", name, (double) totalMisses / sample.size(), maxMisses,
				lost);
	}

	/**
	 * Plays one game: {@code strategy} guesses until a single word remains,
	 * answering truthfully for {@code secret}, and returns the number of misses.
	 */
	private static int play(String secret, List<String> words, IGuessingStrategy strategy) {
		HangmanGame game = new HangmanGame(GreedyCounterexample.createWordlist(words), allowedLetters(words), strategy);
		int misses = 0;
		while (game.getWordlist().getRemainingWords().size() > 1) {
			Character guess = game.bestGuess();
			if (guess == null) {
				// no letter distinguishes the rest; they can only be tried one by one
				return misses + game.getWordlist().getRemainingWords().size() - 1;
			}
			int[] places = positionsOf(guess, secret);
			if (places.length == 0) {
				misses++;
			}
			game.addRestriction(guess, places);
		}
		return misses;
	}

	/** 1-based positions of {@code letter} in {@code word}, as HangmanGame expects. */
	private static int[] positionsOf(char letter, String word) {
		List<Integer> places = new ArrayList<Integer>();
		for (int i = 0; i < word.length(); i++) {
			if (word.charAt(i) == letter) {
				places.add(i + 1);
			}
		}
		int[] result = new int[places.size()];
		for (int i = 0; i < result.length; i++) {
			result[i] = places.get(i);
		}
		return result;
	}

	private static String allowedLetters(List<String> words) {
		TreeSet<Character> letters = new TreeSet<Character>();
		for (String word : words) {
			for (char c : word.toCharArray()) {
				letters.add(c);
			}
		}
		StringBuilder sb = new StringBuilder();
		for (char c : letters) {
			sb.append(c);
		}
		return sb.toString();
	}

	/**
	 * Reproducible sample: all distinct lowercase words of the given length from
	 * the resource, shuffled with the fixed seed, truncated to
	 * {@value #SAMPLE_SIZE} and sorted.
	 */
	private static List<String> sampleWords(String resource, int length) throws Exception {
		List<String> all = new ArrayList<String>();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(
				Benchmark.class.getResourceAsStream("/org/whatsoftwarecando/hangman/" + resource),
				StandardCharsets.UTF_8))) {
			String line;
			while ((line = reader.readLine()) != null) {
				line = line.trim().toLowerCase();
				if (line.length() == length && line.chars().allMatch(Character::isLetter)) {
					all.add(line);
				}
			}
		}
		all = new ArrayList<String>(new TreeSet<String>(all));
		Collections.shuffle(all, new Random(SEED));
		List<String> sample = new ArrayList<String>(all.subList(0, Math.min(SAMPLE_SIZE, all.size())));
		Collections.sort(sample);
		return sample;
	}
}
