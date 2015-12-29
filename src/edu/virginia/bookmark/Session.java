package edu.virginia.bookmark;

import java.util.ArrayList;

public class Session {
	int teacherId;
	Board board;
	SchoolClass schoolClass;
	ArrayList<Team> teams;

	ArrayList<Integer> upToDateIds;
	int activeTurnTeamId = -1;
	
	/**
	 * @param teacherId The id of the teacher who owns this session
	 * @param classId The id of the class playing this session
	 */
	public Session(int teacherId, int classId) {
		this.teacherId = teacherId;
		this.schoolClass = new SchoolClass(classId);
		this.teams = this.schoolClass.getTeams();
	}
	
	/**
	 * Launch the new game session
	 */
	public void startSession() {
		System.out.println("Creating Board");
		this.board = new Board(teams);
		System.out.println("Setting Team Positions.");
		initializeTeamPositions();
		System.out.println("Clearing Up To Date Status.");
		upToDateIds = new ArrayList<Integer>();
	}
	
	/**
	 * Setup the positions for each team on the board.
	 */
	public void initializeTeamPositions() {
		int x = 0;
		int y = 0;
		for(Team team : teams) {
			team.setPosition(x, y);
			x++;
			if(x >= board.BOARD_WIDTH) {
				x = 0;
				y++;
				if(y >= board.BOARD_HEIGHT) {
					y = 0;
				}
			}
		}
	}
	
	/**
	 * Gets an XML string representing the board state
	 * @param requestId The id of the user requesting info.
	 */
	public String getGameBoardState(int requestId) {
		markAsUpToDate(requestId);
		
		String info = "<board_state>";
		info += ("<turn_id>" + activeTurnTeamId + "</turn_id>");
		
		for(Team team : teams) {
			info += team.getInfoString();
		}
		
		info += "</board_state>";
		return info;
	}
	
	/**
	 * Check if someone is up to date
	 * @return True if up to date. False otherwise.
	 */
	public boolean isUpToDate(int id) {
		System.out.println("Checking if ID (" + id + ") is up to date");
		boolean upToDate = upToDateIds.contains(id);
		System.out.println("Up To Date: " + upToDate);
		return upToDate;
	}
	
	/**
	 * Mark a user as up to date.
	 */
	public void markAsUpToDate(int id) {
		if(!upToDateIds.contains(id)) {
			System.out.println("Marking ID (" + id + ") as up to date.");
			upToDateIds.add(id);
		}	
	}
	
	/**
	 * Marks all users as not up to date.
	 */
	public void clearUpToDateStatus() {
		System.out.println("Clearing Up To Date Status.");
		upToDateIds.clear();
	}

	/**
	 * Checks if the given person is a user within this session.
	 */
	public boolean containsPersonWithId(int id) {
		if(id == teacherId) {
			return true;
		}
		for(Team team : teams) {
			if(team.containsStudentWithId(id)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Adds the student with the given ID to the session.
	 */
	public void addStudentWithId(int id) {
		// if(this.activeTurnTeamId == -1) {
			for(Team team : teams) {
				if(team.containsStudentWithId(id)) {
					System.out.println("Adding a Student From Team " + team.getName());
					activeTurnTeamId = team.id;
					clearUpToDateStatus();
					return;
				}
			}
		// }
	}
}
