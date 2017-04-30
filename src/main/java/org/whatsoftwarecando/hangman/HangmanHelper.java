package org.whatsoftwarecando.hangman;

import java.util.Scanner;

import org.whatsoftwarecando.hangman.strategy.MinMaxOneStepStrategy;

public class HangmanHelper {
	public static void main(String[] argv) {
		if(argv.length != 2){
			System.err.println("Usage: HangmanHelper <wordlist-file-name> <allowed characters>");
			System.exit(1);
		}
		try (Scanner input = new Scanner(System.in)) {
			while (true) {
				System.out.println("===========");
				String wordlistFilename = argv[0];
				String allowedCharacters = argv[1];
				System.out.println("New Game");
				System.out.println("File name of wordlist: "+wordlistFilename);
				System.out.println("Allowed characters for guesses: "+allowedCharacters);
				System.out.println("===========");
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

		System.out.println("How many characters?");
		Integer anzahlBuchstaben = null;
		while (anzahlBuchstaben == null) {
			try {
				anzahlBuchstaben = Integer.parseInt(input.nextLine());
			} catch (NumberFormatException n) {
				System.err.println("Only numbers allowed!");
			}
		}
		System.out.println("Loading word list with " + anzahlBuchstaben + " letters...");
		Wordlist wordlist = new Wordlist(HangmanHelper.class.getResourceAsStream(wordlistFilename),
				anzahlBuchstaben);
		
		HangmanGame hangmanGame = new HangmanGame(wordlist, allowedCharactersForGuesses, new MinMaxOneStepStrategy());
		System.out.println(
				wordlist.getRemainingWords().size() + " Words with " + anzahlBuchstaben + " letters loaded.");
		System.out.println("Best guess: " + hangmanGame.bestGuess());
		String nextInput = null;
		int versuche = 1;
		System.out.println("Guess " + versuche);
		while (!(nextInput = input.nextLine()).equals(".")) {
			Character currentChar = null;
			int[] places = null;
			try {
				String[] parts = nextInput.split(" ");
				if (parts[0].length() != 1) {
					throw new RuntimeException("Please enter a character followed by the positions where they occur. '.' to end the current game).");
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
				System.err.println(r.getMessage());
				continue;
			}

			hangmanGame.addRestriction(currentChar, places);
			if (wordlist.getRemainingWords().size() < 2) {
				if (wordlist.getRemainingWords().size() == 1) {
					System.out.println("======================================");
					System.out.println(
							"Solution found after " + versuche + " guesses: " + wordlist.getRemainingWords().get(0));
					System.out.println("======================================");
				} else {
					System.out.println("=============================================");
					System.out.println("The solution is not contained in the wordlist");
					System.out.println("=============================================");
				}
				break;
			}
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
			versuche++;
			System.out.println("Guess " + versuche);
		}
		if (nextInput.equals(".")) {
			System.out.println("The following words are left: " + wordlist.getRemainingWords());
		}
	}
}
