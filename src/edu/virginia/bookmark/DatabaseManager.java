package edu.virginia.bookmark;

import java.util.ArrayList;

public class DatabaseManager {

	// --------------------------------------------------------------------------
	// ------------------------- GET SCHOOL CLASS INFO --------------------------
	// --------------------------------------------------------------------------

	/**
	 * Loads a class in from the database.
	 * @param The id of the class.
	 * @return The SchoolClass with the given id. Null if none exists.
	 */
	public static SchoolClass loadClass(int classId) {
		ArrayList<Team> teams = DatabaseManager.getClassTeams(classId);
		return new SchoolClass(classId, teams);
	}
	
	/**
	 * Get the teams for a specific class
	 * @param classId The id of the class
	 * @return A list of teams
	 */
	public static ArrayList<Team> getClassTeams(int classId) {
		return new ArrayList<Team>();
	}
	
	// --------------------------------------------------------------------------
	// ----------------------------- GET CARD INFO ------------------------------
	// --------------------------------------------------------------------------
	
	public static Card loadCard(int cardId) {
		return new Card(cardId, Card.CardType.Other, "This is the Body Text!", 1, 2);
	}
	
	// --------------------------------------------------------------------------
	// ---------------------------- GET STUDENT INFO ----------------------------
	// --------------------------------------------------------------------------
	
	/**
	 * Uses the id to load the student's deck in from the database
	 */
	public static ArrayList<Card> loadStudentDeck(int studentId) {
		return new ArrayList<Card>();
	}
	

	// --------------------------------------------------------------------------
	// ---------------------------- GET TEACHER INFO ----------------------------
	// --------------------------------------------------------------------------
	
	/**
	 * Checks that the given id is the id of a teacher
	 * @param teacherId The id to check
	 * @return True if the id is a teacher, false otherwise
	 */
	public static boolean verifyTeacher(int teacherId) {
		return true;
	}
	
	/**
	 * Load in the teacher's classes from the database using their id.
	 */
	public static ArrayList<SchoolClass> loadTeacherClasses(int teacherId) {
		return new ArrayList<SchoolClass>();
	}
	
	/**
	 * Verifies that the given teacher is in chargeo of
	 * the given class
	 * @param teacherId ID of the teacher
	 * @param classId ID of the class
	 * @return True if the teacher is in charge of the class. False otherwise.
	 */
	public static boolean verifyTeacherClass(int teacherId, int classId) {
		return true;
	}

	// --------------------------------------------------------------------------
	// ----------------------------- GET TEAM INFO ------------------------------
	// --------------------------------------------------------------------------
	
	/**
	 * Load in a the team's students using the team's id.
	 */
	public static ArrayList<Student> loadTeamStudents(int teamId) {
		return new ArrayList<Student>();
	}
}
