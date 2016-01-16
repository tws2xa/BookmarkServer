package edu.virginia.bookmark;

import java.util.ArrayList;

public class Student extends Person {
	private ArrayList<Card> deck;
	
	public Student(int id, int classId) {
		super(id, DatabaseManager.getPersonName(id));
		this.deck = loadStudentDeckFromDB(classId);
	}
	
	private ArrayList<Card> loadStudentDeckFromDB(int classId) {
		ArrayList<Integer> cardIds = DatabaseManager.loadStudentDeckIds(id, classId);
		ArrayList<Card> retDeck = new ArrayList<Card>();
		for(int cardId : cardIds) {
			retDeck.add(new Card(cardId));
		}
		return retDeck;
	}
	
	/**
	 * Get the student's deck
	 */
	public ArrayList<Card> getDeck() {
		return deck;
	}
	
	/**
	 * Get the XML info string representing the student.
	 */
	public String getStudentXMLInfoString() {
		String info = "<student>";
		info += "<id>" + this.id + "</id>";
		info += "<name>" + this.getName() + "</name>";
		info += "</student>";
		return info;
	}
	
	
}
