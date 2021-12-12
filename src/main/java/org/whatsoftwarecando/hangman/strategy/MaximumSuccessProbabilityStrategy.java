package org.whatsoftwarecando.hangman.strategy;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.whatsoftwarecando.hangman.HangmanGame;
import org.whatsoftwarecando.hangman.IGuessingStrategy;

public class MaximumSuccessProbabilityStrategy implements IGuessingStrategy {

	@Override
	public Character bestGuess(HangmanGame hangManGame) {
		Map<Character, Integer> statistic = new HashMap<Character, Integer>();
		for (char currentChar : hangManGame.getCharactersAllowedForGuesses()) {
			statistic.put(currentChar, 0);
		}
		for (String currentWord : hangManGame.getWordlist().getRemainingWords()) {
			Set<Character> charSet = new HashSet<Character>();
			for (char currentChar : currentWord.toCharArray()) {
				charSet.add(currentChar);
			}
			for (char currentChar : charSet) {
				Integer wordsWithCharacter = statistic.get(currentChar);
				if (wordsWithCharacter != null) {
					statistic.put(currentChar, wordsWithCharacter + 1);
				}
			}
		}
		return maximumOccurence(statistic);
	}

	private Character maximumOccurence(Map<Character, Integer> statistic) {
		int bestCharOccurence = Integer.MIN_VALUE;
		Character bestChar = null;
		for (Entry<Character, Integer> currentStat : statistic.entrySet()) {
			if (currentStat.getValue() > bestCharOccurence) {
				bestCharOccurence = currentStat.getValue();
				bestChar = currentStat.getKey();
			}
		}
		return bestChar;
	}
}
