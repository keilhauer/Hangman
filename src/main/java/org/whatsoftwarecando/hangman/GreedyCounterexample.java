package org.whatsoftwarecando.hangman;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeSet;

import org.whatsoftwarecando.hangman.strategy.BruteForceMiniMaxStrategy;
import org.whatsoftwarecando.hangman.strategy.MiniMaxOneStepStrategy;

/**
 * Verifies the counterexample from the blog article "Warum die
 * Greedy-Strategie bei Hangman nicht immer gewinnt" by brute-force minimax.
 *
 * For every possible first guess it prints the greedy criterion (the size of
 * the biggest block of words that can remain) and the number of misses the
 * word-giver can force if the game is played perfectly afterwards. Greedy
 * picks b (biggest block 3) and loses with two forced misses, while a and e
 * (biggest block 4) win with only one forced miss.
 */
public class GreedyCounterexample {

	public static final List<String> WORDS = Arrays.asList("abe", "abf", "ade", "aef", "bce", "cde");

	/**
	 * Prints the counterexample table. Output is English by default; pass a
	 * language tag (e.g. {@code de}) as the first argument to print the German
	 * wording used in the German article. The locale is taken from the argument
	 * rather than the machine so either article's output is reproducible
	 * anywhere.
	 */
	public static void main(String[] argv) {
		Locale locale = argv.length > 0 ? Locale.forLanguageTag(argv[0]) : Locale.getDefault();
		ResourceBundle messages = ResourceBundle.getBundle("org.whatsoftwarecando.hangman.messages", locale,
				ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_PROPERTIES));

		Set<Character> letters = lettersIn(WORDS);
		Wordlist wordlist = createWordlist(WORDS);
		MiniMaxOneStepStrategy greedy = new MiniMaxOneStepStrategy();
		BruteForceMiniMaxStrategy bruteForce = new BruteForceMiniMaxStrategy();

		for (Character currentChar : letters) {
			int biggestBlock = greedy.biggestBlockAfterGuess(currentChar, wordlist);
			int forcedMisses = bruteForce.forcedMissesAfterGuess(WORDS, letters, currentChar);
			System.out.println(MessageFormat.format(messages.getString("letterLine"), currentChar, biggestBlock,
					forcedMisses));
		}

		String allowedCharacters = asString(letters);
		Character greedyGuess = new HangmanGame(createWordlist(WORDS), allowedCharacters, greedy).bestGuess();
		Character bruteForceGuess = new HangmanGame(createWordlist(WORDS), allowedCharacters, bruteForce).bestGuess();
		System.out.println(MessageFormat.format(messages.getString("greedyChoice"), greedyGuess));
		System.out.println(MessageFormat.format(messages.getString("bruteForceChoice"), bruteForceGuess));
	}

	public static Wordlist createWordlist(List<String> words) {
		StringBuilder sb = new StringBuilder();
		for (String currentWord : words) {
			sb.append(currentWord).append("\n");
		}
		return new Wordlist(new ByteArrayInputStream(sb.toString().getBytes(StandardCharsets.UTF_8)),
				words.get(0).length());
	}

	public static Set<Character> lettersIn(List<String> words) {
		Set<Character> letters = new TreeSet<Character>();
		for (String currentWord : words) {
			for (char currentChar : currentWord.toCharArray()) {
				letters.add(currentChar);
			}
		}
		return letters;
	}

	private static String asString(Set<Character> letters) {
		StringBuilder sb = new StringBuilder();
		for (Character currentChar : letters) {
			sb.append(currentChar);
		}
		return sb.toString();
	}
}
