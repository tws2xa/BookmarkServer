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
			Session session = new Session(teacherId, -1); // -1 indicates use test content
			session.startSession();
			activeSessions.add(session);
			status = 200;
			message = session.getGameBoardState(teacherId);
		} else {
			status = 500;
			message = "You are not registered as the teacher for the requested class.";
		}
		
		return new ResponseInfo(status, message);
	}
	
	
}
