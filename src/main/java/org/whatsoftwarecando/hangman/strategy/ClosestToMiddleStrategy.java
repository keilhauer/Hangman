package org.whatsoftwarecando.hangman.strategy;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import org.whatsoftwarecando.hangman.HangmanGame;
import org.whatsoftwarecando.hangman.IGuessingStrategy;

import java.util.Set;

public class ClosestToMiddleStrategy implements IGuessingStrategy{

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
		return closestToMiddle(hangManGame.getWordlist().getRemainingWords().size(), statistic);
	}

	private Character closestToMiddle(int numberOfWords, Map<Character, Integer> statistic) {
		int middleTimesTwo = numberOfWords;
		int closestToMiddle = Integer.MAX_VALUE;
		Character bestChar = null;
		for (Entry<Character, Integer> currentStat : statistic.entrySet()) {
			int eval = Math.abs(currentStat.getValue() * 2 - middleTimesTwo);
			if (bestChar == null || eval < closestToMiddle) {
				bestChar = currentStat.getKey();
				closestToMiddle = eval;
			}
		}
		if(closestToMiddle >= middleTimesTwo){
			return null;
		}
		return bestChar;
	}
}
