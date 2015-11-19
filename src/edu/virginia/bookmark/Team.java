package edu.virginia.bookmark;

import java.awt.Point;
import java.util.ArrayList;

public class Team {
	public int id;
	int score;
	String name;
	ArrayList<Student> students;
	Point position;
	
	public Team(int id, String name, ArrayList<Student> students) {
		this.id = id;
		this.name = name;
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
	
	/**
	 * Generates and returns the XML string representation of this team.
	 */
	public String getInfoString() {
		String info = "<team>";
		info += "<team_name>" + name + "</team_name>";
		info += "<team_id>" + id + "</team_id>";
		info += "<team_score>" + getScore() + "</team_score>";
		info += "<team_position><x>" + getPosition().getX() + "</x><y>" + getPosition().getY() + "</y></team_position>";
		info += "</team>";
		return info;
	}
	
	/**
	 * Set the team's position on the board
	 * @param x The x position
	 * @param y The y position
	 */
	public void setPosition(int x, int y) {
		setPosition(new Point(x, y));
	}
	
	/**
	 * Set the team's position on the board
	 * @param pos the new position
	 */
	public void setPosition(Point pos) {
		this.position = pos;
	}
	
	/**
	 * Get the team's score.
	 */
	public int getScore() {
		return score;
	}
	
	/**
	 * Get the team's position on the board.
	 */
	public Point getPosition() {
		return position;
	}
	
	/**
	 * Get the team's name.
	 */
	public String getName() {
		return name;
	}
}
