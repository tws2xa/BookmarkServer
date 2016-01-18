package edu.virginia.bookmark;

import java.util.ArrayList;
import java.util.HashMap;

public class Session {
	int teacherId;
	Board board;
	SchoolClass schoolClass;
	ArrayList<Team> teams;
	
	enum SessionState {
		Paused, // Nothing happening yet
		PlayerTurn, // A team is taking their turn
		Challenge // A team has submitted a chain and it's being challenged
	}
	
	private SessionState sessionState;
	ArrayList<Integer> upToDateIds;
	
	int activeTurnTeamId = -1;
	HashMap<Integer, Chain> challengeChains; //<Team ID, Chain>
	
	/**
	 * @param teacherId The id of the teacher who owns this session
	 * @param classId The id of the class playing this session
	 */
	public Session(int teacherId, int classId) {
		this.teacherId = teacherId;
		this.schoolClass = new SchoolClass(classId);
		this.teams = this.schoolClass.getTeams();
		this.setSessionState(SessionState.Paused);
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
		challengeChains = new HashMap<Integer, Chain>();
		this.setSessionState(SessionState.PlayerTurn);
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
	
	public void setSessionState(SessionState state) {
		this.sessionState = state;
		this.clearUpToDateStatus();
	}
	
	/**
	 * Gets an XML string representing the board state
	 * @param requestId The id of the user requesting info.
	 */
	public String getGameBoardState(int requestId) {
		markAsUpToDate(requestId);
		
		String info = "<board_state>";
		info += ("<mode>" + sessionState.name() + "</mode>");
		
		info += getModeInfoXML(requestId);
		
		for(Team team : teams) {
			info += team.getInfoString();
		}
		
		info += "</board_state>";
		return info;
	}
	
	private Team getActiveTeam() {
		for(Team team : schoolClass.getTeams()) {
			if(team.id == activeTurnTeamId) {
				return team;
			}
		}
		return null;
	}
	
	/**
	 * Returns the information needed for the game's current mode
	 */
	public String getModeInfoXML(int requestID) {
		String info = "";
		
		if(sessionState.equals(SessionState.PlayerTurn)) {
			info += ("<turn_id>" + activeTurnTeamId + "</turn_id>");
			if(getActiveTeam() != null) {
				info += ("<turn_team_name>" +  getActiveTeam().name + "</turn_team_name>");
			}
			info += ("<your_turn>" + (schoolClass.findTeamIdWithStudentId(requestID) == activeTurnTeamId) + "</your_turn>");
		}
		else if(sessionState.equals(SessionState.Challenge)) {
			/*
			 * <turn_id> 2 </turn_id> // Still need to know whose turn it is.
			 * <turn_team_name> Gandalf </turn_team_name>
			 * <your_turn> true </your_turn> // Since client doesn't know team #, this is helpful
			 * <challenge_chains>
			 * 		<chain_info>  // Each chain_info represents a team who has challenged and their chain.
			 * 			<team_id> 2 </team_id>
			 * 			<chain> ... </chain>
			 * 		</chain_info>
			 * 		<chain_info>
			 * 			...
			 * 		</chain_info>
			 * 		...
			 * </challenge_chains>
			 */
			info += ("<turn_id>" + activeTurnTeamId + "</turn_id>");
			info += ("<turn_team_name>" +  getActiveTeam().name + "</turn_team_name>");
			info += ("<your_turn>" + (schoolClass.findTeamIdWithStudentId(requestID) == activeTurnTeamId) + "</your_turn>");
			info += "<challenge_chains>";
			for(int teamID : challengeChains.keySet()) {
				info += "<chain_info>";
				info += "<team_id>" + teamID + "</team_id>";
				info += challengeChains.get(teamID).generateChainXML();
				info += "</chain_info>";
			}
			info += "</challenge_chains>";
		}
		
		return info;
	}
		
	/**
	 * Check if someone is up to date
	 * @return True if up to date. False otherwise.
	 */
	public boolean isUpToDate(int id) {
		System.out.print("Checking if ID " + id + " is up to date");
		boolean upToDate = upToDateIds.contains(id);
		System.out.println("   \tUp To Date: " + upToDate);
		return upToDate;
	}
	
	/**
	 * Mark a user as up to date.
	 */
	public void markAsUpToDate(int id) {
		if(!upToDateIds.contains(id)) {
			System.out.println("Marking ID " + id + " as up to date.");
			upToDateIds.add(id);
		}	
	}
	
	/**
	 * Marks all users as not up to date.
	 */
	public void clearUpToDateStatus() {
		if(upToDateIds != null) {
			System.out.println("Clearing Up To Date Status.");
			upToDateIds.clear();
		}
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
	
	/**
	 * Adds a chain to the list of challenges
	 */
	public void addChallenge(int studentId, Chain chain) {
		int teamId = schoolClass.findTeamIdWithStudentId(studentId);
		this.challengeChains.put(teamId, chain);
	}
}
