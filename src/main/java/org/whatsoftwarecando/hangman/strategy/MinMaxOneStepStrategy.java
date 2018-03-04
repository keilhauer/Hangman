package org.whatsoftwarecando.hangman.strategy;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.whatsoftwarecando.hangman.HangmanGame;
import org.whatsoftwarecando.hangman.IGuessingStrategy;
import org.whatsoftwarecando.hangman.Wordlist;

public class MinMaxOneStepStrategy implements IGuessingStrategy {

	@Override
	public Character bestGuess(HangmanGame hangManGame) {
		Integer bestMinMaxValueForGuess = Integer.MAX_VALUE;
		Character bestGuess = null;

		for (char currentChar : hangManGame.getCharactersAllowedForGuesses()) {
			Wordlist wordlist = hangManGame.getWordlist();
			Wordlist noWordlist = wordlist.copy();
			noWordlist.addRestriction(currentChar);
			int noCaseLeftOver = noWordlist.getRemainingWords().size();
			if (noCaseLeftOver == wordlist.getRemainingWords().size()) {
				continue;
			}
			int yesWorstCaseLeftOver = 0;
			if (noCaseLeftOver < wordlist.getRemainingWords().size() / 2.0) {
				Wordlist yesWordlist = wordlist;
				Map<Set<Integer>, Integer> placesToWordCount = new HashMap<Set<Integer>, Integer>();
				for (String currentWord : yesWordlist.getRemainingWords()) {
					int currentPlace = 1;
					Set<Integer> currentPlacesSet = new HashSet<Integer>();
					for (char currentWordChar : currentWord.toCharArray()) {
						if (currentWordChar == currentChar) {
							currentPlacesSet.add(currentPlace);
						}
						currentPlace++;
					}
					Integer valueOfCurrentPlaces = placesToWordCount.get(currentPlacesSet);
					if (valueOfCurrentPlaces == null) {
						valueOfCurrentPlaces = 0;
					}
					valueOfCurrentPlaces++;
					placesToWordCount.put(currentPlacesSet, valueOfCurrentPlaces);
					if (valueOfCurrentPlaces > yesWorstCaseLeftOver) {
						yesWorstCaseLeftOver = valueOfCurrentPlaces;
					}
				}
			}
			int maxLeftOver = Math.max(noCaseLeftOver, yesWorstCaseLeftOver);
			if (maxLeftOver < bestMinMaxValueForGuess) {
				bestMinMaxValueForGuess = maxLeftOver;
				bestGuess = currentChar;
			}
		}

		return bestGuess;
	}

}
