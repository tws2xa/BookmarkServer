package edu.virginia.bookmark;

import java.util.ArrayList;

public class Student extends Person {
	private ArrayList<Card> deck;
	
	public Student(int id, String name, ArrayList<Card> deck) {
		super(id, name);
		this.deck = deck;
	}
	
	/**
	 * Get the student's deck
	 */
	public ArrayList<Card> getDeck() {
		return deck;
	}
	
	
}
