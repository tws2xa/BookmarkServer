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
			Session session = new Session(teacherId, classId);
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
	 * Handle Chain Submission
	 */
	public static ResponseInfo submitChain(int id, Chain chain) {
		System.out.println("Chain Submission: ");
		System.out.println(chain);
		
		// This should actually go to another mode.
		System.out.println("CHAIN SUBMISSION SHORT CIRCUITING TO SAVE. SKIPPING OPPORTUNITY FOR CHALLENGE. SHOULD CHANG IN GameManager.submitChain()!!");
		DatabaseManager.addChainToDatabase(chain);
		
		return new ResponseInfo(200, "All Good");
	}
	
	/**
	 * Finds the class containing the given student.
	 * Collects all argument cards from that class.
	 * Provides an xml representation of the deck.
	 */
	public static ResponseInfo getClassArgumentCardXML(int studentId) {
		Session sessionWithId = getSessionWithId(studentId);
		if(sessionWithId == null) {
			return new ResponseInfo(400, "Cannot find session containing student with id " + studentId);
		}
		
		ArrayList<Card> argumentCards = sessionWithId.schoolClass.getClassArgumentCards();
		
		String xml = "<deck>";
    	for(Card card : argumentCards) {
			xml += card.generateCardXML();
		}
    	xml += "</deck>";
    	
    	return new ResponseInfo(200, xml);
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
	public static Session getSessionWithId(int id) {
		for(Session session : activeSessions) {
			if(session.containsPersonWithId(id)) {
				return session;
			}
		}
		return null;
	}
	
	/**
	 * Gets the id of the class running the active session
	 * containing the given person id.
	 */
	public static int getActiveClassId(int id) {
		Session sessionWithId = getSessionWithId(id);
		if(sessionWithId == null) {
			return -1;
		}
		return sessionWithId.schoolClass.id;
	}
	
	/**
	 * Provides the id of the team containing the student with the given id.
	 */
	public static int getActiveTeamWithStudentId(int id) {
		Session sessionWithId = getSessionWithId(id);
		if(sessionWithId == null) {
			return -1;
		}
		return sessionWithId.schoolClass.findTeamIdWithStudentId(id);
	}
	
	/**
	 * Finds the card with the given id
	 */
	public static Card getCardWithId(int id) {
		return new Card(id);
	}
}
