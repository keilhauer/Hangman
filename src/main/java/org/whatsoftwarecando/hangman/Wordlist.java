package org.whatsoftwarecando.hangman;

import java.io.InputStream;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Scanner;
import java.util.Set;

public class Wordlist {

	private List<String> remainingWords;

	public Wordlist(InputStream is, int lengthOfWords) {
		LinkedHashSet<String> setOfWords = new LinkedHashSet<String>();
		try (Scanner scanner = new Scanner(is, "UTF-8")) {
			while (scanner.hasNextLine()) {
				String word = scanner.nextLine().trim().toLowerCase();
				int lengthOfWord = word.length();
				if (lengthOfWord > 0 && lengthOfWord == lengthOfWords) {
					setOfWords.add(word);
					if (setOfWords.size() % 10000 == 0) {
						System.out.print(".");
					}
				}

			}
		}
		this.remainingWords = new LinkedList<String>(setOfWords);
	}

	private Wordlist(List<String> words){
		this.remainingWords = words;
	}
	
	public void addRestriction(char ch, int... places) {
		Set<Integer> placesSet = new HashSet<Integer>();
		for (int place : places) {
			placesSet.add(place - 1);
		}
		char c = Character.toLowerCase(ch);
		ListIterator<String> listItr = remainingWords.listIterator();
		while (listItr.hasNext()) {
			String currentWord = listItr.next();
			for (int i = 0; i < currentWord.length(); i++) {
				if (placesSet.contains(i)) {
					if (!(currentWord.charAt(i) == c)) {
						listItr.remove();
						break;
					}
				} else {
					if ((currentWord.charAt(i) == c)) {
						listItr.remove();
						break;
					}
				}
			}
		}
	}

	public List<String> getRemainingWords() {
		return remainingWords;
	}

	public Wordlist copy(){
		return new Wordlist(new LinkedList<String>(remainingWords));
	}
}
