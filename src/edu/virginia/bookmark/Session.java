package edu.virginia.bookmark;

import java.util.ArrayList;

public class Session {
	int teacherId;
	Board board;
	SchoolClass schoolClass;
	ArrayList<Team> teams;

	ArrayList<Integer> upToDateTeamIds;
	boolean teacherUpToDate;
	int activeTurnTeamId = -1;
	
	/**
	 * @param teacherId The id of the teacher who owns this session
	 * @param classId The id of the class playing this session
	 */
	public Session(int teacherId, int classId) {
		this.teacherId = teacherId;
		if(classId != -1) {
			this.schoolClass = DatabaseManager.loadClass(classId);
		} else {
			DatabaseManager.createTestContent();
			this.schoolClass = DatabaseManager.testClass;
		}
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
		upToDateTeamIds = new ArrayList<Integer>();
		teacherUpToDate = false;
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
	 * @param requestId The id of the team requesting info. -1 if teacher.
	 */
	public String getGameBoardState(int requestId) {
		markAsUpToDate(requestId);
		
		String info = "<board_state>";
		info += ("<turn_id>" + activeTurnTeamId + "</turn_id>");
		
		for(Team team : teams) {
			info += team.getInfoString();
		}
		
		info += "</board_state>";
		System.out.println("Returning:");
		System.out.println(info);
		return info;
	}
	
	/**
	 * Check if someone is up to date
	 * @param id The id of the team to check. -1 if teacher.
	 * @return True if up to date. False otherwise.
	 */
	public boolean isUpToDate(int id) {
		if(id == -1) return teacherUpToDate;
		return upToDateTeamIds.contains(id);
	}
	
	/**
	 * Mark a user as up to date.
	 * @param id The team id. -1 if teacher.
	 */
	public void markAsUpToDate(int id) {
		if(id != -1 ) {
			teacherUpToDate = true;
		} else if(!upToDateTeamIds.contains(id)) {
			upToDateTeamIds.add(id);
		}	
	}
	
	/**
	 * Marks all users as not up to date.
	 */
	public void clearUpToDateStatus() {
		upToDateTeamIds.clear();
		teacherUpToDate = false;
	}
}
