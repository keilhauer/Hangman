package org.whatsoftwarecando.hangman;

import static org.junit.Assert.assertEquals;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

public class WordlistTest {

	@Test
	public void testRestrictions4(){
		Wordlist wordlist = new Wordlist(Wordlist.class.getResourceAsStream("test-wordlist.txt"), 4);
		assertEquals(4, wordlist.getRemainingWords().size());
	}
	
	@Test
	public void testRestrictions4t0(){
		Wordlist germanWords = createWordlist(4);
		germanWords.addRestriction('t', 0);
		assertEquals(0, germanWords.getRemainingWords().size());
	}
	
	@Test
	public void testRestrictions4t4v1b1(){
		Wordlist germanWords = createWordlist(4);
		germanWords.addRestriction('t', 4);
		assertEquals(words("bolt", "volt"), germanWords.getRemainingWords());
		germanWords.addRestriction('v', 1);
		assertEquals(words("volt"), germanWords.getRemainingWords());
		germanWords.addRestriction('b', 1);
		assertEquals(words(), germanWords.getRemainingWords());
	}
	
	private List<String> words(String... words){
		List<String> result = new LinkedList<String>();
		for(String word : words){
			result.add(word);
		}
		return result;
	}

	private Wordlist createWordlist(int numberOfCharacters) {
		return new Wordlist(Wordlist.class.getResourceAsStream("test-wordlist.txt"), numberOfCharacters);
	}
}
