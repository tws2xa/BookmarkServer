package edu.virginia.bookmark;

import java.util.ArrayList;

public class Session {
	int teacherId;
	Board board;
	SchoolClass schoolClass;
	ArrayList<Team> teams;
	
	public Session(int teacherId) {
		this.teacherId = teacherId;
		this.schoolClass = DatabaseManager.loadTeacherClasses(teacherId).get(0); // Assumes one class per teacher
		this.teams = schoolClass.getTeams();
	}
	
	public void startSession() {
		this.board = new Board(teams);
	}
}
