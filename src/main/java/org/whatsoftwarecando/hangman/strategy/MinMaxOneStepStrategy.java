package org.whatsoftwarecando.hangman.strategy;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.whatsoftwarecando.hangman.HangmanGame;
import org.whatsoftwarecando.hangman.IGuessingStrategy;
import org.whatsoftwarecando.hangman.Wordlist;

public class MinMaxOneStepStrategy implements IGuessingStrategy{

	public Character bestGuess(HangmanGame hangManGame) {
		Integer bestMinMaxValueForGuess = Integer.MAX_VALUE;
		Character bestGuess = null;
		
		for (char currentChar : hangManGame.getCharactersAllowedForGuesses()) {
			Wordlist noWordlist = hangManGame.getWordlist().copy();
			noWordlist.addRestriction(currentChar);
			int noEval = noWordlist.getRemainingWords().size();
			if(noEval == hangManGame.getWordlist().getRemainingWords().size()){
				continue;
			}
			Wordlist yesWordlist = hangManGame.getWordlist();
			Map<Set<Integer>, Integer> placesToWordCount = new HashMap<Set<Integer>, Integer>();
			int yesWorstCase = 0;
			for(String currentWord : yesWordlist.getRemainingWords()){
				int currentPlace = 1;
				Set<Integer> currentPlacesSet = new HashSet<Integer>();
				for(char currentWordChar : currentWord.toCharArray()){
					if(currentWordChar == currentChar){
						currentPlacesSet.add(currentPlace);
					}
					currentPlace++;
				}
				Integer valueOfCurrentPlaces = placesToWordCount.get(currentPlacesSet);
				if(valueOfCurrentPlaces == null){
					valueOfCurrentPlaces = 0;
				}
				valueOfCurrentPlaces++;
				placesToWordCount.put(currentPlacesSet, valueOfCurrentPlaces);
				if(valueOfCurrentPlaces > yesWorstCase){
					yesWorstCase = valueOfCurrentPlaces;
				}
			}
			int eval = Math.max(noEval, yesWorstCase);
			if(eval < bestMinMaxValueForGuess){
				bestMinMaxValueForGuess = eval;
				bestGuess = currentChar;
			}
		}
		
		return bestGuess;
	}
	
}
