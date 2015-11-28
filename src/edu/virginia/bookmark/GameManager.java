package edu.virginia.bookmark;

import java.util.ArrayList;

public class GameManager {

    private static ArrayList<Session> activeSessions = new ArrayList<Session>();
    
	/**
	 * Begin a new session for the given class
	 * @param teacherId The id of the teacher beginning the session
	 * @param classId The id of the class who will be playing this session
	 */
	public static ResponseInfo beginSession(int teacherId, int classId) {
		int status = -1;
		String message = "Failed to run.";
		
		if(DatabaseManager.verifyTeacherClass(teacherId, classId)) {
			// Create the session
			//
			System.out.println("Creating Session Object");
			Session session = new Session(teacherId, -1); // -1 indicates use test content
			System.out.println("Starting Session!");
			session.startSession();
			System.out.println("Session Started!");
			activeSessions.add(session);
			status = 200;
			message = session.getGameBoardState(teacherId);
		} else {
			status = 401;
			message = "You are not registered as the teacher for the requested class.";
		}
		
		return new ResponseInfo(status, message);
	}
	
	/**
	 * Allows the student with the given id to join their session.
	 */
	public static ResponseInfo joinSession(int id) {
		int status = -1;
		String message = "Failed to run.";
		
		// Find the session to join.
		//
		Session toJoin = getSessionWithId(id);
		
		if(toJoin == null) {
			// No valid session found
			//
			status = 400;
			message = "No Session Found for the Given User";
		}
		else {
			toJoin.addStudentWithId(id);
			status = 200;
			message = toJoin.getGameBoardState(id);
		}
		
		return new ResponseInfo(status, message);
	}

	/**
	 * Checks if the user with the given id needs to update the board view.
	 */
	public static boolean checkNeedUpdate(int id) {
		Session session = getSessionWithId(id);
		if(session == null) {
			return false;
		}
		return !session.isUpToDate(id);
	}

	/**
	 * Returns the XML string representing the current board state.
	 * @param id The id of the requesting user.
	 */
	public static String getBoardStateXML(int id) {
		Session session = getSessionWithId(id);
		if(session == null) {
			return null;
		}
		return session.getGameBoardState(id);
	}
	
	/**
	 * Finds the session that contains a user
	 * with the given id
	 * @param id The id of the user to check
	 * @return The session if a valid session is found, else null.
	 */
	private static Session getSessionWithId(int id) {
		for(Session session : activeSessions) {
			if(session.containsPersonWithId(id)) {
				return session;
			}
		}
		return null;
	}
}
