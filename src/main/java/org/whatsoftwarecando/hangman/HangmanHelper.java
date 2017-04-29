package org.whatsoftwarecando.hangman;

import java.util.Scanner;

import org.whatsoftwarecando.hangman.strategy.ClosestToMiddleStrategy;
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
				System.out.println("Neues Spiel");
				System.out.println("===========");
				einSpiel(input, argv[0], argv[1]);
				System.out.println("Nochmal spielen? [j] für ja");

				String nochmal = input.nextLine();
				if (!nochmal.equals("j")) {
					break;
				}
				System.out.println();
			}
		}
	}

	private static void einSpiel(Scanner input, String wordlistFilename, String allowedCharactersForGuesses) {

		System.out.println("Wie viele Buchstaben?");
		Integer anzahlBuchstaben = null;
		while (anzahlBuchstaben == null) {
			try {
				anzahlBuchstaben = Integer.parseInt(input.nextLine());
			} catch (NumberFormatException n) {
				System.err.println("Nur eine Zahl erlaubt!");
			}
		}
		System.out.println("Lade Wortliste für " + anzahlBuchstaben + " Buchstaben...");
		Wordlist wordlist = new Wordlist(HangmanHelper.class.getResourceAsStream(wordlistFilename),
				anzahlBuchstaben);
		
		HangmanGame hangmanGame = new HangmanGame(wordlist, allowedCharactersForGuesses, new MinMaxOneStepStrategy());
		System.out.println(
				wordlist.getRemainingWords().size() + " Wörter mit " + anzahlBuchstaben + " Buchstaben geladen.");
		System.out.println("Bester Buchstabe: " + hangmanGame.bestGuess());
		String nextInput = null;
		int versuche = 1;
		System.out.println("Versuch " + versuche);
		while (!(nextInput = input.nextLine()).equals(".")) {
			Character currentChar = null;
			int[] places = null;
			try {
				String[] parts = nextInput.split(" ");
				if (parts[0].length() != 1) {
					throw new RuntimeException("Einen Buchstaben eingeben!");
				}
				currentChar = new Character(parts[0].charAt(0));
				try {
					places = new int[parts.length - 1];
					for (int i = 0; i < places.length; i++) {
						places[i] = Integer.parseInt(parts[i + 1]);
					}
				} catch (NumberFormatException n) {
					throw new RuntimeException("Nur Zahlen erlaubt!");
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
							"Lösung gefunden nach " + versuche + " Versuchen: " + wordlist.getRemainingWords().get(0));
					System.out.println("======================================");
				} else {
					System.out.println("======================================");
					System.out.println("Die Lösung ist nicht in der Wortliste.");
					System.out.println("======================================");
				}
				break;
			}
			Character bestGuess = hangmanGame.bestGuess();
			if (bestGuess == null) {
				System.out.println("Keine unterscheidenden erlaubten Buchstaben mehr übrig. Du musst raten:\n"
						+ wordlist.getRemainingWords());
			} else {
				System.out.println(wordlist.getRemainingWords().size() + " verbleibende Wörter: "
						+ ((wordlist.getRemainingWords().size() > 20)
								? wordlist.getRemainingWords().subList(0, 20) + " ... "
								: wordlist.getRemainingWords()));
				System.out.println("Bester Buchstabe: " + bestGuess);
			}
			versuche++;
			System.out.println("Versuch " + versuche);
		}
		if (nextInput.equals(".")) {
			System.out.println("Folgende Wörter sind übrig: " + wordlist.getRemainingWords());
		}
	}
}
