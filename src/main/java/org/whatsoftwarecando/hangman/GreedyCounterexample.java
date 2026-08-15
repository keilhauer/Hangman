package org.whatsoftwarecando.hangman;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
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

	public static void main(String[] argv) {
		Set<Character> letters = lettersIn(WORDS);
		Wordlist wordlist = createWordlist(WORDS);
		MiniMaxOneStepStrategy greedy = new MiniMaxOneStepStrategy();
		BruteForceMiniMaxStrategy bruteForce = new BruteForceMiniMaxStrategy();

		for (Character currentChar : letters) {
			int biggestBlock = greedy.biggestBlockAfterGuess(currentChar, wordlist);
			int forcedMisses = bruteForce.forcedMissesAfterGuess(WORDS, letters, currentChar);
			System.out.println(
					currentChar + ": größter Block " + biggestBlock + ", erzwingbare Fehlversuche " + forcedMisses);
		}

		String allowedCharacters = asString(letters);
		Character greedyGuess = new HangmanGame(createWordlist(WORDS), allowedCharacters, greedy).bestGuess();
		Character bruteForceGuess = new HangmanGame(createWordlist(WORDS), allowedCharacters, bruteForce).bestGuess();
		System.out.println("Greedy (Ein-Zug-Minimax) wählt: " + greedyGuess);
		System.out.println("Brute-Force-Minimax wählt: " + bruteForceGuess);
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
