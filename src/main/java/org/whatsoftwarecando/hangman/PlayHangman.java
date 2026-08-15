package org.whatsoftwarecando.hangman;

import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

/**
 * An ordinary game of Hangman: the computer secretly picks a random word from
 * the word list, the player guesses letters and loses a life for every miss.
 * Counterpart of {@link HangmanHelper}, which supports the guessing side.
 */
public class PlayHangman {

	private static final String[] GALLOWS = {
			"  +---+\n  |   |\n      |\n      |\n      |\n      |\n=========",
			"  +---+\n  |   |\n  O   |\n      |\n      |\n      |\n=========",
			"  +---+\n  |   |\n  O   |\n  |   |\n      |\n      |\n=========",
			"  +---+\n  |   |\n  O   |\n /|   |\n      |\n      |\n=========",
			"  +---+\n  |   |\n  O   |\n /|\\  |\n      |\n      |\n=========",
			"  +---+\n  |   |\n  O   |\n /|\\  |\n /    |\n      |\n=========",
			"  +---+\n  |   |\n  O   |\n /|\\  |\n / \\  |\n      |\n=========" };

	private static final int MAX_MISSES = GALLOWS.length - 1;

	public static void main(String[] argv) {
		if (argv.length != 1) {
			System.err.println("Usage: PlayHangman <wordlist-file-name>");
			System.exit(1);
		}
		try (Scanner input = new Scanner(System.in)) {
			while (true) {
				String wordlistFilename = argv[0];
				System.out.println(HangmanHelper.emphasized("New Game\nFile name of wordlist: " + wordlistFilename));
				oneGame(input, wordlistFilename);
				System.out.println("Play again? [y] for yes");

				String again = input.nextLine();
				if (!again.equals("y")) {
					break;
				}
				System.out.println();
			}
		}
	}

	private static void oneGame(Scanner input, String wordlistFilename) {
		System.out.print("How many characters should the word have? ");
		Integer numberOfCharacters = null;
		while (numberOfCharacters == null) {
			try {
				numberOfCharacters = Integer.parseInt(input.nextLine());
			} catch (NumberFormatException n) {
				System.err.println("Only numbers allowed!");
			}
		}
		System.out.print("Loading word list with " + numberOfCharacters + " letters...");
		Wordlist wordlist = new Wordlist(PlayHangman.class.getResourceAsStream(wordlistFilename), numberOfCharacters);
		List<String> words = wordlist.getRemainingWords();
		System.out.println("\n" + words.size() + " words with " + numberOfCharacters + " letters loaded.");
		if (words.isEmpty()) {
			System.out.println("Nothing to play.");
			return;
		}

		String secretWord = words.get(new Random().nextInt(words.size()));
		Set<Character> guessedCharacters = new TreeSet<Character>();
		int misses = 0;
		while (true) {
			System.out.println(GALLOWS[misses]);
			System.out.println("Word: " + masked(secretWord, guessedCharacters));
			if (isSolved(secretWord, guessedCharacters)) {
				System.out.println(HangmanHelper.emphasized("You win! The word was: " + secretWord));
				return;
			}
			if (misses >= MAX_MISSES) {
				System.out.println(HangmanHelper.emphasized("You lose! The word was: " + secretWord));
				return;
			}
			System.out.println("Guessed so far: " + guessedCharacters + ", misses left: " + (MAX_MISSES - misses));
			System.out.print("Guess a letter: ");
			String nextInput = input.nextLine().trim().toLowerCase();
			if (nextInput.length() != 1 || !Character.isLetter(nextInput.charAt(0))) {
				System.err.println("Please enter a single letter!");
				continue;
			}
			char guess = nextInput.charAt(0);
			if (!guessedCharacters.add(guess)) {
				System.err.println("You already guessed '" + guess + "'!");
				continue;
			}
			if (secretWord.indexOf(guess) >= 0) {
				System.out.println("Hit!");
			} else {
				System.out.println("Miss!");
				misses++;
			}
		}
	}

	private static String masked(String word, Set<Character> guessedCharacters) {
		StringBuilder sb = new StringBuilder();
		for (char currentChar : word.toCharArray()) {
			if (sb.length() > 0) {
				sb.append(' ');
			}
			sb.append(guessedCharacters.contains(currentChar) ? currentChar : '_');
		}
		return sb.toString();
	}

	private static boolean isSolved(String word, Set<Character> guessedCharacters) {
		for (char currentChar : word.toCharArray()) {
			if (!guessedCharacters.contains(currentChar)) {
				return false;
			}
		}
		return true;
	}
}
