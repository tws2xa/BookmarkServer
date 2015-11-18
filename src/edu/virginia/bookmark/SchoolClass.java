package edu.virginia.bookmark;

import java.util.ArrayList;

public class SchoolClass {
	public int id;
	private ArrayList<Team> teams;

	public SchoolClass(int id, ArrayList<Team> teams) {
		this.id = id;
		this.teams = teams;
	}
	
	/**
	 * @return the teams
	 */
	public ArrayList<Team> getTeams() {
		return teams;
	}
	
	/**
	 * @return The combined decks of all students in the class
	 */
	public ArrayList<Card> getClassDeck() {
		ArrayList<Card> classDeck = new ArrayList<Card>();
		
		for(Team team : teams) {
			classDeck.addAll(team.getTeamDeck());
		}
		
		return classDeck;
	}
}
