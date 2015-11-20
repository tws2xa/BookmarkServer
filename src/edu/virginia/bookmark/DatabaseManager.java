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
	 * Finds the person with the given id.
	 * @return The person if the id is found, null otherwise.
	 */
	public static Person findPersonWithId(int id) {
		createTestContent();
		if(id == 100) {
			ArrayList<SchoolClass> teachClasses = new ArrayList<SchoolClass>();
			teachClasses.add(testClass);
			return new Teacher(100, "Teacher", teachClasses);
		}
		for(Student student : testStudents) {
			if(student.id == id) {
				return student;
			}
		}
		return null;
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
	
	/**
	 * Finds the student with the given id.
	 * @return The student if the id is found, null otherwise.
	 */
	public static Student findStudentWithId(int id) {
		createTestContent();
		for(Student student : testStudents) {
			if(student.id == id) {
				return student;
			}
		}
		return null;
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
		return teacherId == 100;
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
	

	// --------------------------------------------------------------------------
	// ---------------------------- ACCOUNT METHODS -----------------------------
	// --------------------------------------------------------------------------
	
	/**
	 * Log in the user with the given username and password
	 * @return The user's person id. -1 on Failure.
	 */
	public static int doLogin(String username, String pass) {
		for(int i=12; i>=7; i--) {
			String docWord = ("Doc" + i);
			if(username.equals(docWord) && pass.equals(docWord)) {
				return (100 + i);
			}
		}
		if(username.equals("a") && pass.equals("a")) {
			return 100; // Teacher
		}
		return -1; // No match
	}

	// --------------------------------------------------------------------------
	// ---------------------------- TESTING METHODS -----------------------------
	// --------------------------------------------------------------------------
	
	public static SchoolClass testClass;
	public static ArrayList<Student> testStudents;
	
	public static void createTestContent() {
		System.out.println("Creating Cards.");
		// Create 12 cards
		//
		ArrayList<Card> cards = new ArrayList<Card>();
		cards.add(new Card(0, Card.CardType.Argument, "Here is the point I wish to argue! Hooray!", -1, -1));
		cards.add(new Card(1, Card.CardType.Tone, "I have uncovered the mysteries of Tone! Hooray!", -1, -1));
		cards.add(new Card(2, Card.CardType.Imagery, "Here is some imagery! Huzzah!", 2, -1));
		cards.add(new Card(3, Card.CardType.Diction, "Words are my speciality! Huzzah!", 7, 8));
		cards.add(new Card(4, Card.CardType.Diction, "Here are some fancy words! Yippie!", 6, 7));
		cards.add(new Card(5, Card.CardType.Theme, "I theme, you theme, we all theme for Ice Cream! Yippie!", -1, -1));
		cards.add(new Card(6, Card.CardType.Tone, "This is how the author felt when writing! Woohoo!", -1, -1));
		cards.add(new Card(7, Card.CardType.Other, "I found some alliteration! Woohoo!", 9, -1));
		cards.add(new Card(8, Card.CardType.Theme, "Here is a main, underlying idea of the text! Yowzah!", -1, -1));
		cards.add(new Card(9, Card.CardType.Argument, "This is a thing I believe! Yowzah!", -1, -1));
		cards.add(new Card(10, Card.CardType.Other, "Here is an incredibly original thought! I love this game!!", -1, -1));
		cards.add(new Card(11, Card.CardType.Imagery, "Beautiful words paint a beautiful picture! I love this game!", 8, 10));
		
		System.out.println("Creating Students");
		// Create 6 Students to hold cards
		//
		testStudents = new ArrayList<Student>();
		testStudents.add(new Student(112, "Peter Capaldi", new ArrayList<Card>(cards.subList(0, 2))));
		testStudents.add(new Student(111, "Matt Smith", new ArrayList<Card>(cards.subList(2, 4))));
		testStudents.add(new Student(110, "David Tennant", new ArrayList<Card>(cards.subList(4, 6))));
		testStudents.add(new Student(109, "Christopher Eccelston", new ArrayList<Card>(cards.subList(6, 8))));
		testStudents.add(new Student(108, "Paul McGann", new ArrayList<Card>(cards.subList(8, 10))));
		testStudents.add(new Student(107, "Sylvester McCoy", new ArrayList<Card>(cards.subList(10, 12))));
		
		System.out.println("Creating Teams");
		// Create 3 Teams for Students
		//
		ArrayList<Team> teams = new ArrayList<Team>();
		teams.add(new Team(1000, "Gandalf", new ArrayList<Student>(testStudents.subList(0, 2))));
		teams.add(new Team(1001, "Aragorn", new ArrayList<Student>(testStudents.subList(2, 4))));
		teams.add(new Team(1002, "Samwise", new ArrayList<Student>(testStudents.subList(4, 6))));
		
		System.out.println("Creating Class");
		// Create 1 class with the teams
		//
		testClass = new SchoolClass(10000, teams);
	}
	
}
