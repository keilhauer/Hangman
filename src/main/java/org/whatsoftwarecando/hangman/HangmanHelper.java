package org.whatsoftwarecando.hangman;

import java.util.Scanner;
import java.util.StringTokenizer;

import org.whatsoftwarecando.hangman.strategy.MiniMaxOneStepStrategy;

public class HangmanHelper {

	static final String CHARACTER_INPUT_HELP_TEXT = "Please enter a character followed by the positions where it occurs\n"
			+ "(e.g. \"u 1 3\" if the letter u occurs exactly in position 1 and 3 or \n"
			+ "just \"u\" if the letter does not occur at all).\n" + "Input '.' to end the current game";

	public static void main(String[] argv) {
		if (argv.length != 2) {
			System.err.println("Usage: HangmanHelper <wordlist-file-name> <allowed characters>");
			System.exit(1);
		}
		try (Scanner input = new Scanner(System.in)) {
			while (true) {
				String wordlistFilename = argv[0];
				String allowedCharacters = argv[1];
				System.out.println(emphasized("New Game\nFile name of wordlist: " + wordlistFilename
						+ "\nAllowed characters for guesses: " + allowedCharacters));
				einSpiel(input, wordlistFilename, allowedCharacters);
				System.out.println("Play again? [y] for yes");

				String nochmal = input.nextLine();
				if (!nochmal.equals("y")) {
					break;
				}
				System.out.println();
			}
		}
	}

	private static void einSpiel(Scanner input, String wordlistFilename, String allowedCharactersForGuesses) {

		System.out.print("How many characters does the word have? ");
		Integer anzahlBuchstaben = null;
		while (anzahlBuchstaben == null) {
			try {
				anzahlBuchstaben = Integer.parseInt(input.nextLine());
			} catch (NumberFormatException n) {
				System.err.println("Only numbers allowed!");
			}
		}
		System.out.print("Loading word list with " + anzahlBuchstaben + " letters...");
		Wordlist wordlist = new Wordlist(HangmanHelper.class.getResourceAsStream(wordlistFilename), anzahlBuchstaben);

		HangmanGame hangmanGame = new HangmanGame(wordlist, allowedCharactersForGuesses, new MiniMaxOneStepStrategy());
		System.out
				.println("\n" + wordlist.getRemainingWords().size() + " words with " + anzahlBuchstaben
						+ " letters loaded.");

		System.out.println(emphasized(CHARACTER_INPUT_HELP_TEXT));
		int nrOfGuess = 1;
		while (true) {
			doBestGuess(wordlist, hangmanGame);
			System.out.print("Guess " + nrOfGuess + ": ");
			String nextInput = input.nextLine();

			if (nextInput.equals(".")) {
				System.out.println("The following words are left: " + wordlist.getRemainingWords());
				break;
			} else {
				boolean successfulMove = oneMove(hangmanGame, nextInput);
				if (!successfulMove) {
					continue;
				}
				if (wordlist.getRemainingWords().size() < 2) {
					if (wordlist.getRemainingWords().size() == 1) {
						System.out.println(emphasized("Solution found after " + nrOfGuess + " guesses: "
								+ wordlist.getRemainingWords().get(0)));
					} else {
						System.out.println(emphasized("The solution is not contained in the wordlist"));
					}
					break;
				}
			}
			nrOfGuess++;
		}

	}

	private static String emphasized(String output) {
		StringTokenizer st = new StringTokenizer(output, "\n");
		int longestLine = 0;
		while (st.hasMoreTokens()) {
			String currentLine = st.nextToken();
			if (currentLine.length() > longestLine) {
				longestLine = currentLine.length();
			}
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < longestLine; i++) {
			sb.append("-");
		}
		return sb.toString() + "\n" + output + "\n" + sb.toString();
	}

	private static Character doBestGuess(Wordlist wordlist, HangmanGame hangmanGame) {
		Character bestGuess = hangmanGame.bestGuess();
		if (bestGuess == null) {
			System.out.println("No more differentiating allowed characters left. You have to guess:\n"
					+ wordlist.getRemainingWords());
		} else {
			System.out.println(wordlist.getRemainingWords().size() + " words remaining: "
					+ ((wordlist.getRemainingWords().size() > 20)
							? wordlist.getRemainingWords().subList(0, 20) + " ... "
							: wordlist.getRemainingWords()));
			System.out.println("Best guess: " + bestGuess);
		}
		return bestGuess;
	}

	private static boolean oneMove(HangmanGame hangmanGame, String nextInput) {
		Character currentChar = null;
		int[] places = null;
		try {
			String[] parts = nextInput.split(" ");
			if (parts[0].length() != 1) {
				throw new RuntimeException(CHARACTER_INPUT_HELP_TEXT);
			}
			currentChar = new Character(parts[0].charAt(0));
			try {
				places = new int[parts.length - 1];
				for (int i = 0; i < places.length; i++) {
					places[i] = Integer.parseInt(parts[i + 1]);
				}
			} catch (NumberFormatException n) {
				throw new RuntimeException("Only numbers allowed!");
			}
		} catch (RuntimeException r) {
			System.err.println(emphasized(r.getMessage()));
			return false;
		}

		hangmanGame.addRestriction(currentChar, places);
		return true;
	}
}
