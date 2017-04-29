package org.whatsoftwarecando.hangman;

import java.util.HashSet;
import java.util.Set;

public class HangmanGame {

	private Set<Character> charactersAllowedForGuesses;

	private Wordlist wordlist;

	private IGuessingStrategy buestGuessStrategy;
	
	public HangmanGame(Wordlist wordlist, String allowedCharactersForGuesses, IGuessingStrategy bestGuessStrategy) {
		this.wordlist = wordlist;
		this.charactersAllowedForGuesses = new HashSet<Character>();
		for (Character currentKonsonant : allowedCharactersForGuesses.toCharArray()) {
			this.charactersAllowedForGuesses.add(currentKonsonant);
		}
		this.buestGuessStrategy = bestGuessStrategy;
	}

	public Character bestGuess() {
		return buestGuessStrategy.bestGuess(this);
	}
	
	public Wordlist getWordlist() {
		return wordlist;
	}

	public Set<Character> getCharactersAllowedForGuesses() {
		return charactersAllowedForGuesses;
	}
	
	public void addRestriction(char ch, int... places){
		charactersAllowedForGuesses.remove(ch);
		wordlist.addRestriction(ch, places);
	}
}
