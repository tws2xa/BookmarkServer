package edu.virginia.bookmark;

public class GameActions {
	
	/**
	 * Begin a new session for the given class
	 * @param teacherId The id of the teacher beginning the session
	 * @param classId The id of the class who will be playing this session
	 */
	public static void beginSession(int teacherId, int classId) {
		// Verify teacherId.
		boolean validTeacher = DatabaseManager.verifyTeacher(teacherId);
		if(!validTeacher) {
			// Fail.
		} else {
			// Generate SchoolClass from classId.
			SchoolClass schoolClass = DatabaseManager.loadClass(classId);
			
			if(schoolClass != null) {
				// Create Session using schoolClass	
				Session session = new Session(0);
				session.startSession();
			}
		}
	}
	
	
}
