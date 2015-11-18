package edu.virginia.bookmark;

import java.util.ArrayList;

public class Team {
	public int id;
	ArrayList<Student> students;
	
	public Team(int id, ArrayList<Student> students) {
		this.id = id;
		this.students = students;
	}
	
	/**
	 * @return The combined deck of all students in the team
	 */
	public ArrayList<Card> getTeamDeck() {
		ArrayList<Card> teamDeck = new ArrayList<Card>();
		
		for(Student student : students) {
			teamDeck.addAll(student.getDeck());
		}
		
		return teamDeck;
	}
}
