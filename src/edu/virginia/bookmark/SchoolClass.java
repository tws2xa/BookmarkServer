package edu.virginia.bookmark;

import java.util.ArrayList;

import edu.virginia.bookmark.Card.CardType;

public class SchoolClass {
	public int id;
	private ArrayList<Team> teams;

	public SchoolClass(int id) {
		this.id = id;
		this.teams = loadTeamsFromDB();
	}
	
	private ArrayList<Team> loadTeamsFromDB() {
		ArrayList<Integer> teamIds = DatabaseManager.getClassTeamIds(id);
		ArrayList<Team> retTeams = new ArrayList<Team>();
		for(int teamId : teamIds) {
			retTeams.add(new Team(teamId));
		}
		return retTeams;
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
	
	/**
	 * Collects all of the argument cards for a class.
	 */
	public ArrayList<Card> getClassArgumentCards() {
		ArrayList<Card> allCards = getClassDeck();
		ArrayList<Card> argumentCards = new ArrayList<Card>();
		
		for(Card card : allCards) {
			if(card.getType().equals(CardType.Argument)) {
				argumentCards.add(card);
			}
		}

		return argumentCards;
	}
	
	/**
	 * Finds the id of the team that contains the student with the given id.
	 * Returns -1 if none.
	 */
	public int findTeamIdWithStudentId(int id) {
		for(Team team : teams) {
			if(team.containsStudentWithId(id)) {
				return team.id;
			}
		}
		return -1;
	}
}
