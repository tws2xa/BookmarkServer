package edu.virginia.bookmark;

import java.awt.Point;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/******
 * 
 * DATABASE SCHEMA
 * 
 * Table: People
 * 		[int PersonID unique][char(120) UserName][char(120) Password][char(120) PersonName][TimeStamp TIMESTAMP]
 * 
 * Table: Classes
 * 		[int ClassID unique][char(120) ClassName][text ClassInfo][int TeacherID][int CurrentAssignmentID][TimeStamp TIMESTAMP]
 * 
 * Table: Teams
 * 		[int TeamID unique][char(120) TeamName][int ClassID][int AssignmentID][TimeStamp TIMESTAMP]
 * 
 * Table: ClassStudents
 * 		[int ClassID][int StudentID][TimeStamp TIMESTAMP]
 * 
 * Table: TeamStudents
 * 		[int TeamID][int StudentID][TimeStamp TIMESTAMP]
 * 
 * Table: Cards
 * 		[int CardID unique][int PersonID][int ClassID][int AssignmentID][char(120) CardType][text CardBody][int PageStart][int PageEnd][TimeStamp TIMESTAMP]
 * 
 * Table: Chains
 * 		[int ChainID][int ArgumentCardID][char(120) ChainQuality][TimeStamp TIMESTAMP]
 * 
 * Table: ChainCards
 *		[int ChainID][int CardID][int CardX][int CardY][TimeStamp TIMESTAMP]
 * 
 * Table: ChainLinks
 * 		[int ChainID][int Card1ID][int Card2ID][TimeStamp TIMESTAMP]
 * 
 * Table: Assignments
 * 		[int AssignmentID][int ClassID][char(120) AssignmentName][Text AssignmentInfo][char(120) DeckType][int PrevAssignmentID]
 *
 * Table: DeckCardTypes
 * 		[char(120) DeckTypeName][char(120) CardType] <-- This is not ideal but I'm in a rush.
 *
 ******/




public class DatabaseManager {

	/**
	 * Clears all database content and re-initializes everything.
	 */
	public static void InitializeDB() {
		MysqlDataSource datasource = null;
		Connection connection = null;
		Statement statement = null;
		
		String url="jdbc:mysql://localhost:3306/bookmarkdb";
		String user="Bookmark";
		String password="jetbookmark";
		
		try {
			datasource = new MysqlDataSource();
			datasource.setUrl(url);
			datasource.setUser(user);
			datasource.setPassword(password);
			connection = datasource.getConnection();
			statement = connection.createStatement();
			
			// Drop any current tables
			statement.executeUpdate("DROP TABLE IF EXISTS People");
			statement.executeUpdate("DROP TABLE IF EXISTS Classes");
			statement.executeUpdate("DROP TABLE IF EXISTS Teams");
			statement.executeUpdate("DROP TABLE IF EXISTS ClassStudents");
			statement.executeUpdate("DROP TABLE IF EXISTS TeamStudents");
			statement.executeUpdate("DROP TABLE IF EXISTS Cards");
			statement.executeUpdate("DROP TABLE IF EXISTS Chains");
			statement.executeUpdate("DROP TABLE IF EXISTS ChainCards");
			statement.executeUpdate("DROP TABLE IF EXISTS ChainLinks");
			statement.executeUpdate("DROP TABLE IF EXISTS CardLinks");
			statement.executeUpdate("DROP TABLE IF EXISTS Assignments");
			statement.executeUpdate("DROP TABLE IF EXISTS DeckCardTypes");
			
			// People
			// [int PersonID unique][char(120) UserName][char(120) Password][char(120) PersonName][TimeStamp TIMESTAMP]
			//
			statement.executeUpdate("CREATE TABLE People "
					+ "(PersonID INT UNSIGNED AUTO_INCREMENT PRIMARY KEY, "
					+ "Username CHAR(120) NOT NULL, "
					+ "Password CHAR(120) NOT NULL, "
					+ "PersonName CHAR(120) NOT NULL, "
					+ "TimeStamp TIMESTAMP);");
				
			// Classes
			// [int ClassID unique][char(120) ClassName][text ClassInfo][int TeacherID][int CurrentAssignmentID][TimeStamp TIMESTAMP]
			statement.executeUpdate("CREATE TABLE Classes "
					+ "(ClassID INT UNSIGNED AUTO_INCREMENT PRIMARY KEY, "
					+ "ClassName CHAR(120) NOT NULL, "
					+ "ClassInfo TEXT, "
					+ "TeacherID INT NOT NULL, "
					+ "CurrentAssignmentID INT NOT NULL, "
					+ "TimeStamp TIMESTAMP);");
			
			// Teams
			// [int TeamID unique][char(120) TeamName][int ClassID][int AssignmentID][TimeStamp TIMESTAMP]
			statement.executeUpdate("CREATE TABLE Teams "
					+ "(TeamID INT UNSIGNED AUTO_INCREMENT PRIMARY KEY, "
					+ "TeamName CHAR(120) NOT NULL, "
					+ "ClassID INT NOT NULL, "
					+ "AssignmentID INT NOT NULL, "
					+ "TimeStamp TIMESTAMP);");
			  
			// ClassStudents
			// [int ClassID][int StudentID][TimeStamp TIMESTAMP]
			statement.executeUpdate("CREATE TABLE ClassStudents (ClassID INT NOT NULL, StudentID INT NOT NULL, TimeStamp TIMESTAMP);");
			 
			// TeamStudents
			// [int TeamID][int StudentID][TimeStamp TIMESTAMP]
			statement.executeUpdate("CREATE TABLE TeamStudents (TeamID INT NOT NULL, StudentID INT NOT NULL, TimeStamp TIMESTAMP);");
			
			// Cards
			// [int CardID unique][int PersonID][int ClassID][char(120) CardType][text CardBody][int PageStart][int PageEnd][TimeStamp TIMESTAMP]
			statement.executeUpdate("CREATE TABLE Cards "
					+ "(CardID INT UNSIGNED AUTO_INCREMENT PRIMARY KEY, "
					+ "PersonID INT NOT NULL, "
					+ "ClassID INT NOT NULL, "
					+ "AssignmentID INT NOT NULL, "
					+ "CardType CHAR(120) NOT NULL, "
					+ "CardBody TEXT, "
					+ "PageStart INT, "
					+ "PageEnd INT, "
					+ "TimeStamp TIMESTAMP);");
			 
			// Table: Chains
			// [int ChainID][int ArgumentCardID][TimeStamp TIMESTAMP]
			statement.executeUpdate("CREATE TABLE Chains "
					+ "(ChainID INT UNSIGNED AUTO_INCREMENT PRIMARY KEY, "
					+ "ArgumentCardID INT NOT NULL, "
					+ "ChainQuality CHAR(120) NOT NULL, "
					+ "TimeStamp TIMESTAMP);");
			 
			// ChainCards
			// [int ChainID][int CardID][int CardX][int CardY][TimeStamp TIMESTAMP]
			statement.executeUpdate("CREATE TABLE ChainCards (ChainID INT NOT NULL, CardID INT NOT NULL, CardX INT NOT NULL, CardY INT NOT NULL, TimeStamp TIMESTAMP);");
			 
			// ChainLinks
			// [int ChainID][int Card1ID][int Card2ID][TimeStamp TIMESTAMP]
			statement.executeUpdate("CREATE TABLE ChainLinks (ChainID INT NOT NULL, Card1ID INT NOT NULL, Card2ID INT NOT NULL, TimeStamp TIMESTAMP);");
			

			 // Assignments
			 // [int AssignmentID][char(120) AssignmentName][Text AssignmentInfo][char(120) DeckType][int PrevAssignmentID]
			statement.executeUpdate("CREATE TABLE Assignments ("
					+ "AssignmentID INT UNSIGNED AUTO_INCREMENT PRIMARY KEY, "
					+ "ClassID INT UNSIGNED NOT NULL, "
					+ "AssignmentName CHAR(120) NOT NULL, "
					+ "AssignmentInfo TEXT, "
					+ "DeckType CHAR(120) NOT NULL, "
					+ "PrevAssignmentID INT NOT NULL,"
					+ "TimeStamp TIMESTAMP);");

			 // DeckCardTypes
			 // [char(120) DeckTypeName][char(120) CardType]
			statement.executeUpdate("CREATE TABLE DeckCardTypes (DeckTypeName CHAR(120) NOT NULL, CardType CHAR(120) NOT NULL);");
			
			// Create the test class and data
			DatabaseManager.createTestContent();
			DatabaseManager.loadDeckTypes();
		} catch(SQLException ex) {
			System.out.println("SQL Exception in Initialize: " + ex.getMessage());
		} finally {
			try {
				if(statement != null) {
					statement.close();
				}
				if(connection != null) {
					connection.close();
				}
			} catch (SQLException ex) {
				System.out.println("SQL Exception in Initialzie Finally: " + ex.getMessage());
			}
		}
	}
		
	// --------------------------------------------------------------------------
	// ------------------------- GET SCHOOL CLASS INFO --------------------------
	// --------------------------------------------------------------------------

	
	/**
	 * Returns ALL the information about a class in XML format.
	 */
	public static String getFullClassInfo(int classId) {
		// Table: People [int PersonID unique][char(120) UserName][char(120) Password][char(120) PersonName][TimeStamp TIMESTAMP]
		// Classes: [int ClassID unique][char(120) ClassName][text ClassInfo][int TeacherID][int CurrentAssignmentID][TimeStamp TIMESTAMP]
		// Teams: [int TeamID unique][char(120) TeamName][int ClassID][int AssignmentID][TimeStamp TIMESTAMP]
		// ClassStudents: [int ClassID][int StudentID][TimeStamp TIMESTAMP]
		// TeamStudents: [int TeamID][int StudentID][TimeStamp TIMESTAMP]
		// Cards [int CardID unique][int PersonID][int ClassID][int AssignmentID][char(120) CardType][text CardBody][int PageStart][int PageEnd][TimeStamp TIMESTAMP]
		// Assignments: [int AssignmentID][int ClassID][char(120) AssignmentName][Text AssignmentInfo][char(120) DeckType][int PrevAssignmentID]
		
		/**
		 * <class_info>
		 * 		<class_name>Name</class_name>
		 * 		<current_assignment>current assignment name</current_assignment>
		 * 		<assignments>
		 * 			<assignment>
		 * 				<assignment_name>Assignment Name</assignment_name>
		 *				<assignment_id>assignment_id</assignment_id>
		 * 				<assignment_teams>
		 * 					<team>
		 * 						<team_name>Team Name</team_name>
		 * 						<students>
		 * 							<student>
		 * 								<student_name>Student Name</student_name>
		 * 								<student_num_cards>37</student_num_cards>
		 * 								<student_id>id</student_id>
		 * 							</student>
		 * 							<student>
		 * 								...
		 * 							</student>
		 * 							...
		 * 						</students>
		 * 					</team>
		 * 					<team>
		 * 						...
		 * 					</team>
		 *	 				...
		 *				</assignment_teams>
		 * 			</assignment>
		 * 			<assignment>
		 * 				...
		 * 			</assignment>
		 * 		</assignments>
		 * </class_info>
		 */
		
		String className = DatabaseManager.getClassName(classId);
		int currentAssignmentId = DatabaseManager.getCurrentAssignmentIDForClass(classId);
		String currentAssignment = DatabaseManager.getStringFromDB(
				"Get Current Assignment Name in Get Class Info",
				"SELECT AssignmentName FROM Assignments WHERE AssignmentID=" + currentAssignmentId + ";",
				"AssignmentName",
				"No Current Assignment"
				);
		
		String classXML = "<class_info>";
		classXML += "<class_name>" + className + "</class_name>";
		classXML += "<current_assignment>" + currentAssignment + "</current_assignment>";

		classXML += "<assignments>";

		ArrayList<Integer> assignmentIds = DatabaseManager.getClassAssignmentIds(classId);
		Collections.reverse(assignmentIds); // Reverse so most recent is first
		for(int assignmentId : assignmentIds) {
			classXML += DatabaseManager.getFullAssignmentInfo(assignmentId, classId);
		}
		
		classXML += "</assignments>";
		
		classXML += "</class_info>";
		return classXML;
	}
	
	/**
	 * Return the name of the class with the given id.
	 */
	public static String getClassName(int classId) {
		// Classes: [int ClassID unique][char(120) ClassName][text ClassInfo][int TeacherID][int CurrentAssignmentID][TimeStamp TIMESTAMP]
		String query = ("SELECT ClassName FROM Classes WHERE ClassID=" + classId + ";");
		return DatabaseManager.getStringFromDB("Get Class Name", query, "ClassName", "Class #" + classId);
	}
	
	/**
	 * Get all assignment ids linked to the given class.
	 */
	public static ArrayList<Integer> getClassAssignmentIds(int classId) {
		// Assignments: [int AssignmentID][char(120) AssignmentName][Text AssignmentInfo][char(120) DeckType][int PrevAssignmentID]
		ArrayList<Integer> assignmentIds = new ArrayList<Integer>();	
		
		MysqlDataSource datasource = null;
		Connection connection = null;
		Statement statement = null;
		
		String url="jdbc:mysql://localhost:3306/bookmarkdb";
		String user="Bookmark";
		String password="jetbookmark";
			
		try {
			datasource = new MysqlDataSource();
			datasource.setUrl(url);
			datasource.setUser(user);
			datasource.setPassword(password);
			connection = datasource.getConnection();
			statement = connection.createStatement();
				
			String query = "SELECT Assignments.AssignmentID FROM Assignments "
					+ "WHERE (Assignments.ClassID=" + classId + ");";
			ResultSet results = statement.executeQuery(query);
			while(results.next()) {
				int id = results.getInt("AssignmentID");
				assignmentIds.add(id);
			}
			
		} catch(SQLException ex) {
			System.out.println("SQL Exception in Load Class Assignment IDs: " + ex.getMessage());
		} finally {
			try {
				if(statement != null) {
					statement.close();
				}
				if(connection != null) {
					connection.close();
				}
			} catch (SQLException ex) {
				System.out.println("SQL Exception in Load Class Assignment IDs Finally: " + ex.getMessage());
			}
		}
		
		return assignmentIds;
	}
	
	/**
	 * Get the teams for a specific class
	 * @param classId The id of the class
	 * @return A list of teams
	 */
	public static ArrayList<Integer> getClassTeamIds(int classId) {
		ArrayList<Integer> teamIds = new ArrayList<Integer>();	
		
		MysqlDataSource datasource = null;
		Connection connection = null;
		Statement statement = null;
		
		String url="jdbc:mysql://localhost:3306/bookmarkdb";
		String user="Bookmark";
		String password="jetbookmark";
			
		try {
			datasource = new MysqlDataSource();
			datasource.setUrl(url);
			datasource.setUser(user);
			datasource.setPassword(password);
			connection = datasource.getConnection();
			statement = connection.createStatement();
				
			String query = "SELECT Teams.TeamID FROM Teams, Classes "
					+ "WHERE (Teams.ClassID=" + classId + " "
					+ "AND Classes.ClassID=" + classId + " "
					+ "AND Teams.AssignmentID=Classes.CurrentAssignmentID);";
			ResultSet results = statement.executeQuery(query);
			while(results.next()) {
				int id = results.getInt("TeamID");
				System.out.println("Team ID: " + id);
				teamIds.add(id);
			}
			
		} catch(SQLException ex) {
			System.out.println("SQL Exception in Load Class Team IDs: " + ex.getMessage());
		} finally {
			try {
				if(statement != null) {
					statement.close();
				}
				if(connection != null) {
					connection.close();
				}
			} catch (SQLException ex) {
				System.out.println("SQL Exception in Load Class Team IDs Finally: " + ex.getMessage());
			}
		}
		
		return teamIds;
	}
	
	/**
	 * Returns the id of the current assignment for the class with the given id.
	 * Returns -1 if no class is found with the given id.
	 */
	public static int getCurrentAssignmentIDForClass(int classId) {
		String query = "SELECT CurrentAssignmentID FROM Classes WHERE ClassID=" + classId + ";";
		int assignmentID = DatabaseManager.getIntFromDB(
				"Get Current Assignment For Class " + classId,
				query,
				"CurrentAssignmentID",
				-1);
		return assignmentID;
	}
	

	/**
	 * Returns a list containing the string name of every student in the class.
	 * @param teacherId Id of the desired class' teacher
	 */
	public static ArrayList<String> getClassStudentNames(int teacherId) {
		ArrayList<String> studentNames = new ArrayList<String>();	
		
		MysqlDataSource datasource = null;
		Connection connection = null;
		Statement statement = null;
		
		String url="jdbc:mysql://localhost:3306/bookmarkdb";
		String user="Bookmark";
		String password="jetbookmark";
			
		try {
			datasource = new MysqlDataSource();
			datasource.setUrl(url);
			datasource.setUser(user);
			datasource.setPassword(password);
			connection = datasource.getConnection();
			statement = connection.createStatement();
			
			/*
			 * Table: People
			 * 		[int PersonID unique][char(120) UserName][char(120) Password][char(120) PersonName][TimeStamp TIMESTAMP]
			 *
			 * Table: Classes
			 * 		[int ClassID unique][char(120) ClassName][text ClassInfo][int TeacherID][int CurrentAssignmentID][TimeStamp TIMESTAMP]
			 * 
			 * Table: ClassStudents
			 * 		[int ClassID][int StudentID][TimeStamp TIMESTAMP]
			*/
			
			String query = "SELECT People.PersonName "
					+ "FROM People, Classes, ClassStudents "
					+ "WHERE People.PersonID=ClassStudents.StudentID AND ClassStudents.ClassID=Classes.ClassID AND Classes.TeacherID=" + teacherId + ";";
			ResultSet results = statement.executeQuery(query);
			while(results.next()) {
				studentNames.add(results.getString("PersonName"));
			}
			
		} catch(SQLException ex) {
			System.out.println("SQL Exception in Load Class Student Names: " + ex.getMessage());
		} finally {
			try {
				if(statement != null) {
					statement.close();
				}
				if(connection != null) {
					connection.close();
				}
			} catch (SQLException ex) {
				System.out.println("SQL Exception in Load Class Student Names: " + ex.getMessage());
			}
		}
		
		return studentNames;
	}
		


	// --------------------------------------------------------------------------
	// -------------------------- GET ASSIGNMENT INFO ---------------------------
	// --------------------------------------------------------------------------
	
	/**
	 * Gets all information associated with the given assignment.
	 */
	public static String getFullAssignmentInfo(int assignmentId, int classId) {
		// Table: People [int PersonID unique][char(120) UserName][char(120) Password][char(120) PersonName][TimeStamp TIMESTAMP]
		// Classes: [int ClassID unique][char(120) ClassName][text ClassInfo][int TeacherID][int CurrentAssignmentID][TimeStamp TIMESTAMP]
		// Teams: [int TeamID unique][char(120) TeamName][int ClassID][int AssignmentID][TimeStamp TIMESTAMP]
		// ClassStudents: [int ClassID][int StudentID][TimeStamp TIMESTAMP]
		// TeamStudents: [int TeamID][int StudentID][TimeStamp TIMESTAMP]
		// Cards [int CardID unique][int PersonID][int ClassID][int AssignmentID][char(120) CardType][text CardBody][int PageStart][int PageEnd][TimeStamp TIMESTAMP]
		// Assignments: [int AssignmentID][int ClassID][char(120) AssignmentName][Text AssignmentInfo][char(120) DeckType][int PrevAssignmentID]
				
		/**
		 * <assignment>
		 *		<assignment_name>Assignment Name</assignment_name>
         *		<assignment_id>assignment_id</assignment_id>
		 *		<assignment_teams>
		 *			<team>
		 *				<team_name>Team Name</team_name>
		 *				<students>
		 *					<student>
		 *						<student_name>Student Name</student_name>
		 *						<student_num_cards>37</student_num_cards>
		 * 						<student_id>id</student_id>
		 *					</student>
		 *					<student>
		 *						...
		 *					</student>
		 *					...
		 *				</students>
		 *			</team>
		 *			<team>
		 *				...
		 *			</team>
		 *			...
		 *		</assignment_teams>
		 * </assignment>
		 */	
		
		String assignmentXML = "<assignment>";
		
		String assignmentName = DatabaseManager.getStringFromDB(
				"Getting Assignment Name In Full Assignment Info",
				"SELECT AssignmentName FROM Assignments WHERE AssignmentID=" + assignmentId + ";",
				"AssignmentName",
				"Unknown Assignment: #" + assignmentId
				);
		assignmentXML += "<assignment_name>" + assignmentName + "</assignment_name>";
		assignmentXML += "<assignment_id>" + assignmentId + "</assignment_id>";
		
		assignmentXML += "<assignment_teams>";
		for(int teamId : DatabaseManager.getAssignmentTeamIds(assignmentId)) {
			assignmentXML += DatabaseManager.getFullTeamXML(teamId, assignmentId, classId);
		}
		assignmentXML += "</assignment_teams>";
		
		assignmentXML += "</assignment>";
		return assignmentXML;
	}
	
	/**
	 * Get all teams for the given assignment
	 */
	public static ArrayList<Integer> getAssignmentTeamIds(int assignmentId) {
		ArrayList<Integer> teamIds = new ArrayList<Integer>();	
		
		MysqlDataSource datasource = null;
		Connection connection = null;
		Statement statement = null;
		
		String url="jdbc:mysql://localhost:3306/bookmarkdb";
		String user="Bookmark";
		String password="jetbookmark";
			
		try {
			datasource = new MysqlDataSource();
			datasource.setUrl(url);
			datasource.setUser(user);
			datasource.setPassword(password);
			connection = datasource.getConnection();
			statement = connection.createStatement();
				
			String query = "SELECT TeamID FROM Teams WHERE (AssignmentID=" + assignmentId + ");";
			ResultSet results = statement.executeQuery(query);
			while(results.next()) {
				int id = results.getInt("TeamID");
				teamIds.add(id);
			}
			
		} catch(SQLException ex) {
			System.out.println("SQL Exception in Load Assignment Team IDs: " + ex.getMessage());
		} finally {
			try {
				if(statement != null) {
					statement.close();
				}
				if(connection != null) {
					connection.close();
				}
			} catch (SQLException ex) {
				System.out.println("SQL Exception in Load Assignment Team IDs Finally: " + ex.getMessage());
			}
		}
		
		return teamIds;
	}
	
	// --------------------------------------------------------------------------
	// ----------------------------- GET CARD INFO ------------------------------
	// --------------------------------------------------------------------------
	
	
	public static Chain getChainForArgumentCard(int argCardId) {
		boolean found = false;
		HashMap<Card, Point> cards = new HashMap<Card, Point>();
		ArrayList<int[]> links = new ArrayList<int[]>();
		
		MysqlDataSource datasource = null;
		Connection connection = null;
		Statement statement = null;
		
		String url="jdbc:mysql://localhost:3306/bookmarkdb";
		String user="Bookmark";
		String password="jetbookmark";
			
		try {
			datasource = new MysqlDataSource();
			datasource.setUrl(url);
			datasource.setUser(user);
			datasource.setPassword(password);
			connection = datasource.getConnection();
			statement = connection.createStatement();
			
			int chainId = getChainIdForArgumentCard(argCardId);
			
			if(chainId == -1) {
				// No chain found for given argument card.
				found = false;
			} else {
				// Chain found
				found = true;
				
				// Load in cards and positions from ChainCards.
				// Table: ChainCards [int ChainID][int CardID][int CardX][int CardY][TimeStamp TIMESTAMP]
				String getCardsQuery = ("SELECT * FROM ChainCards WHERE ChainID=" + chainId + ";");
				ResultSet results = statement.executeQuery(getCardsQuery);
				while(results.next()) {
					int cardID = results.getInt("CardID");
					int cardX = results.getInt("CardX");
					int cardY = results.getInt("CardY");
					Point pos = new Point(cardX, cardY);
					Card card = new Card(cardID);
					cards.put(card, pos);
				}
				
				// Load in links from ChainLinks.
				// Table: ChainLinks [int ChainID][int Card1ID][int Card2ID][TimeStamp TIMESTAMP]
				String getLinksQuery = ("SELECT * FROM ChainLinks WHERE ChainID=" + chainId + ";");
				results = statement.executeQuery(getLinksQuery);
				while(results.next()) {
					int[] link = new int[2];
					link[0] = results.getInt("Card1ID");
					link[1] = results.getInt("Card2ID");
					links.add(link);
				}
			}
		} catch(SQLException ex) {
			System.out.println("SQL Exception in Get Chain For Argument Card: " + ex.getMessage());
		} finally {
			try {
				if(statement != null) {
					statement.close();
				}
				if(connection != null) {
					connection.close();
				}
			} catch (SQLException ex) {
				System.out.println("SQL Exception in Get Chain For Argument Card Finally: " + ex.getMessage());
			}
		}
		
		if(!found) {
			return null;
		}
		Chain chain = new Chain(cards, links);
		return chain;
	}
	
	/**
	 * Gets the id of a chain based around the argument card with the given id
	 * Returns -1 if no chain exists for the given argument card id.
	 */
	public static int getChainIdForArgumentCard(int argCardId) {
		String getChainIDQuery = ("SELECT ChainID FROM Chains WHERE ArgumentCardID=" + argCardId + ";");
		int chainId = DatabaseManager.getIntFromDB(("Searching For Chain With Argument ID: " + argCardId), getChainIDQuery, "ChainID", -1);
		return chainId;
	}
	
	public static HashMap<String, Object> getCardProperties(int cardId) {
		HashMap<String, Object> cardProperties = new HashMap<String, Object>();

		MysqlDataSource datasource = null;
		Connection connection = null;
		Statement statement = null;
		
		String url="jdbc:mysql://localhost:3306/bookmarkdb";
		String user="Bookmark";
		String password="jetbookmark";
			
		try {
			datasource = new MysqlDataSource();
			datasource.setUrl(url);
			datasource.setUser(user);
			datasource.setPassword(password);
			connection = datasource.getConnection();
			statement = connection.createStatement();
			
			// Table: Cards
			// [int CardID unique][int PersonID][int ClassID][int AssignmentID][char(120) CardType][text CardBody][int PageStart][int PageEnd][TimeStamp TIMESTAMP]
					 
			String query = "SELECT * FROM Cards WHERE CardID=" + cardId + ";";
			ResultSet results = statement.executeQuery(query);
			
			if(results.next()) {
				// Found Match
				String type = results.getString("CardType").trim();
				String bodyText = results.getString("CardBody").trim();
				int pageStart = results.getInt("PageStart");
				int pageEnd = results.getInt("PageEnd");
				
				cardProperties.put("type", type);
				cardProperties.put("bodyText", bodyText);
				cardProperties.put("pageStart", pageStart);
				cardProperties.put("pageEnd", pageEnd);
			}
		} catch(SQLException ex) {
			System.out.println("SQL Exception in Get Card Properties: " + ex.getMessage());
		} finally {
			try {
				if(statement != null) {
					statement.close();
				}
				if(connection != null) {
					connection.close();
				}
			} catch (SQLException ex) {
				System.out.println("SQL Exception in Get Card Properties Finally: " + ex.getMessage());
			}
		}
		
		return cardProperties;
	}
	
	// --------------------------------------------------------------------------
	// ---------------------------- GET PERSON INFO -----------------------------
	// --------------------------------------------------------------------------

	/**
	 * Get the name of the person with the given id.
	 */
	public static String getPersonName(int personId) {
		String query = "SELECT PersonName FROM People WHERE (PersonId=" + personId + ");";
		return getStringFromDB("Get_Person_Name", query, "PersonName", "Unnamed Person #" + personId);
	}
	
	/**
	 * Get the id of the person with the given name.
	 */
	public static int getPersonIDFromName(String personName) {
		System.out.println("CAUTOION: ASSUMING ALL STUDENTS HAVE UNIQUE NAMES");
		String query = "SELECT PersonId FROM People WHERE (PersonName='" + personName + "');";
		return getIntFromDB("Get_Person_ID_From_Name", query, "PersonID", -1);
	}
	
	/**
	 * Returns the class id for the person (teacher or student).
	 * Assumes one class per teacher, and no teacher is also a student.
	 * Returns failNum when no class found.
	 */
	public static int getClassIdForPerson(int personId, int failNum) {
		int classId = failNum;
		ArrayList<Integer> classIds = loadTeacherClassIds(personId);
		if(classIds.size() > 0) {
			System.out.println("CAUTION: ASSUMING ONE CLASS PER TEACHER IN getCurrentCardTypesForPerson.");
			classId = classIds.get(0);
		} else {
			classId = getClassContainingStudent(personId);
		}
		return classId;
	}
	
	/**
	 * Get the current allowed list of card typed for the person.
	 * Based on assignment deck.
	 */
	public static ArrayList<String> getCurrentCardTypesForPerson(int personId) {
		// Get the class id from the person ID
		int classId = getClassIdForPerson(personId, -1);
		if(classId < 0) {
			System.out.println("No Class ID Found for Person With ID: " + personId);
			return null;
		}
		
		// Get the current assignment ID from the class
		int currentAssignmentId = DatabaseManager.getCurrentAssignmentIDForClass(classId);
		if(currentAssignmentId < 0) {
			System.out.println("No Current Assignment Found for Person " + personId + " With Class ID: " + classId);
			return null;
		}
		
		// Get the deck type from the assignment
		String deckQuery = "SELECT DeckType FROM Assignments WHERE AssignmentId=" + currentAssignmentId + ";";
		String deckType = DatabaseManager.getStringFromDB("Get DeckType in Get_Current_Card_Types_For_Person", deckQuery, "DeckType", "");
		if(deckType.isEmpty()) {
			System.out.println("No DeckType found for Assignment ID: " + currentAssignmentId);
			return null;
		}
		
		// Now load in card types.
		ArrayList<String> cardTypes = new ArrayList<String>();	
		
		MysqlDataSource datasource = null;
		Connection connection = null;
		Statement statement = null;
		String url="jdbc:mysql://localhost:3306/bookmarkdb";
		String user="Bookmark";
		String password="jetbookmark";
			
		try {
			datasource = new MysqlDataSource();
			datasource.setUrl(url);
			datasource.setUser(user);
			datasource.setPassword(password);
			connection = datasource.getConnection();
			statement = connection.createStatement();

			// Table: DeckCardTypes
			// [char(120) DeckTypeName][char(120) CardType]
			String query = ("SELECT CardType FROM DeckCardTypes WHERE DeckTypeName='" + deckType + "';");
			ResultSet results = statement.executeQuery(query);
			while(results.next()) {
				cardTypes.add(results.getString("CardType"));
			}
			
		} catch(SQLException ex) {
			System.out.println("SQL Exception in Get Current Card Types For Person: " + ex.getMessage());
		} finally {
			try {
				if(statement != null) {
					statement.close();
				}
				if(connection != null) {
					connection.close();
				}
			} catch (SQLException ex) {
				System.out.println("SQL Exception in Get Current Card Types For Person's Finally: " + ex.getMessage());
			}
		}
		
		System.out.println("Card Types: ");
		for(String type : cardTypes) {
			System.out.println("\t" + type);
		}
		
		return cardTypes;
	}
		
	// --------------------------------------------------------------------------
	// ---------------------------- GET STUDENT INFO ----------------------------
	// --------------------------------------------------------------------------
	
	/**
	 * Uses the id to lookup the id's of every card in the student's deck. 
	 */
	public static ArrayList<Integer> loadStudentDeckIds(int studentId, int classId, int assignmentId) {
		ArrayList<Integer> cardIds = new ArrayList<Integer>();	
		
		MysqlDataSource datasource = null;
		Connection connection = null;
		Statement statement = null;
		
		String url="jdbc:mysql://localhost:3306/bookmarkdb";
		String user="Bookmark";
		String password="jetbookmark";
			
		try {
			datasource = new MysqlDataSource();
			datasource.setUrl(url);
			datasource.setUser(user);
			datasource.setPassword(password);
			connection = datasource.getConnection();
			statement = connection.createStatement();
				
			String query = ("SELECT CardID FROM Cards WHERE PersonID=" + studentId + " AND ClassID=" + classId + " AND AssignmentID=" + assignmentId + ";");
			ResultSet results = statement.executeQuery(query);
			while(results.next()) {
				cardIds.add(results.getInt("CardID"));
			}
			
		} catch(SQLException ex) {
			System.out.println("SQL Exception in Load Student Deck IDs: " + ex.getMessage());
		} finally {
			try {
				if(statement != null) {
					statement.close();
				}
				if(connection != null) {
					connection.close();
				}
			} catch (SQLException ex) {
				System.out.println("SQL Exception in Load Student Deck IDs Finally: " + ex.getMessage());
			}
		}
		
		return cardIds;
	}
	
	public static int getClassContainingStudent(int id) {
		String query = "SELECT Classes.ClassID AS LookupID "
				+ "FROM Classes, ClassStudents "
				+ "WHERE (Classes.ClassID=ClassStudents.ClassID AND ClassStudents.StudentID=" + id + ");";
		int classId = DatabaseManager.getIntFromDB("GET CLASS CONTAINING STUDENT", query, "LookupID", -1);
		return classId;
	}

	public static int getTeamContainingStudent(int id) {

	String query = "SELECT Teams.TeamID AS LookupID "
			+ "FROM Teams, TeamStudents, Classes "
			+ "WHERE (Teams.TeamID=TeamStudents.TeamID AND Teams.AssignmentID=Classes.CurrentAssignmentID AND TeamStudents.StudentID=" + id + ");";
	int teamId = DatabaseManager.getIntFromDB("GET TEAM CONTAINING STUDENT", query, "LookupID", -1);

	return teamId;

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
		return (loadTeacherClassIds(teacherId).size() > 0);
	}
	
	/**
	 * Load in the id's of every class the teacher owns.
	 */
	public static ArrayList<Integer> loadTeacherClassIds(int teacherId) {
		ArrayList<Integer> classIds = new ArrayList<Integer>();	
		
		MysqlDataSource datasource = null;
		Connection connection = null;
		Statement statement = null;
		
		String url="jdbc:mysql://localhost:3306/bookmarkdb";
		String user="Bookmark";
		String password="jetbookmark";
			
		try {
			datasource = new MysqlDataSource();
			datasource.setUrl(url);
			datasource.setUser(user);
			datasource.setPassword(password);
			connection = datasource.getConnection();
			statement = connection.createStatement();
				
			
			ResultSet results = statement.executeQuery("SELECT ClassID FROM Classes WHERE TeacherID = " + teacherId + ";");
			while(results.next()) {
				int classId = results.getInt("ClassID");
				classIds.add(classId);
			}
			
		} catch(SQLException ex) {
			System.out.println("SQL Exception in Load Teacher Class IDs: " + ex.getMessage());
		} finally {
			try {
				if(statement != null) {
					statement.close();
				}
				if(connection != null) {
					connection.close();
				}
			} catch (SQLException ex) {
				System.out.println("SQL Exception in Load Teacher Class IDs Finally: " + ex.getMessage());
			}
		}
		
		return classIds;
	}
	
	/**
	 * Verifies that the given teacher is in charge of the given class
	 * @param teacherId ID of the teacher
	 * @param classId ID of the class
	 * @return True if the teacher is in charge of the class. False otherwise.
	 */
	public static boolean verifyTeacherClass(int teacherId, int classId) {
		return (loadTeacherClassIds(teacherId).contains(classId));
	}

	// --------------------------------------------------------------------------
	// ----------------------------- GET TEAM INFO ------------------------------
	// --------------------------------------------------------------------------
	
	public static int getTeamClassID(int teamId) {
		String query = ("SELECT ClassID FROM Teams WHERE TeamID=" + teamId + ";");
		return DatabaseManager.getIntFromDB("Get Team ClassID", query, "ClassID", -1);
	}
	
	/**
	 * Load in the team's name from the database
	 */
	public static String getTeamName(int teamId) {
		String query = "SELECT TeamName FROM Teams WHERE TeamID = " + teamId + ";";
		return DatabaseManager.getStringFromDB("Get Team Name", query, "TeamName", "No Name Team #" + teamId);
	}
	
	/**
	 * Load in a the team's students using the team's id.
	 */
	public static ArrayList<Integer> loadTeamStudentIds(int teamId) {
		ArrayList<Integer> studentIDs = new ArrayList<Integer>();	
		
		MysqlDataSource datasource = null;
		Connection connection = null;
		Statement statement = null;
		
		String url="jdbc:mysql://localhost:3306/bookmarkdb";
		String user="Bookmark";
		String password="jetbookmark";
			
		try {
			datasource = new MysqlDataSource();
			datasource.setUrl(url);
			datasource.setUser(user);
			datasource.setPassword(password);
			connection = datasource.getConnection();
			statement = connection.createStatement();
				
			String query = ("SELECT StudentID FROM TeamStudents WHERE TeamID = " + teamId + ";");
			ResultSet results = statement.executeQuery(query);
			while(results.next()) {
				studentIDs.add(results.getInt("StudentID"));
			}
			
		} catch(SQLException ex) {
			System.out.println("SQL Exception in Load Team Students IDs: " + ex.getMessage());
		} finally {
			try {
				if(statement != null) {
					statement.close();
				}
				if(connection != null) {
					connection.close();
				}
			} catch (SQLException ex) {
				System.out.println("SQL Exception in Load Team Students IDs Finally: " + ex.getMessage());
			}
		}
		
		return studentIDs;
	}
	
	/**
	 * Creates XML with all info for the given team (and assignment id).
	 */
	public static String getFullTeamXML(int teamId, int assignmentId, int classId) {
		/**
		 * <team>
		 *		<team_name>Team Name</team_name>
		 *		<students>
		 *			<student>
		 *				<student_name>Student Name</student_name>
		 *				<student_num_cards>37</student_num_cards>
		 * 				<student_id>id</student_id>
		 *			</student>
		 *			<student>
		 *				...
		 *			</student>
		 *			...
		 *		</students>
		 * </team>
		 */
		String teamXML = "<team>";
		String teamName = DatabaseManager.getTeamName(teamId);
		teamXML += "<team_name>" + teamName + "</team_name>";
		
		teamXML += "<students>";
		for(int studentId : DatabaseManager.loadTeamStudentIds(teamId)) {
			teamXML += "<student>";
				String studentName = DatabaseManager.getPersonName(studentId);
				teamXML += "<student_name>" + studentName + "</student_name>";
				int numCards = DatabaseManager.loadStudentDeckIds(studentId, classId, assignmentId).size();
				teamXML += "<student_num_cards>" + numCards + "</student_num_cards>";
				teamXML += "<student_id>" + studentId + "</student_id>";
			teamXML += "</student>";
		}
		teamXML += "</students>";
		
		teamXML += "</team>";
		return teamXML;
	}

	// --------------------------------------------------------------------------
	// ---------------------------- ACCOUNT METHODS -----------------------------
	// --------------------------------------------------------------------------
	
	/**
	 * Log in the user with the given username and password
	 * @return The user's person id. -1 on Failure.
	 */
	public static int doLogin(String username, String pass) {
		String query = "SELECT PersonID FROM People WHERE (Username='" + username + "' AND Password='" + pass + "');";
		return getIntFromDB("Login", query, "PersonID", -1);
	}


	// --------------------------------------------------------------------------
	// ----------------------- DATA MODIFICATION METHODS ------------------------
	// --------------------------------------------------------------------------
	
	/**
	 * Stores the given chain in the database.
	 * If a chain already exists with argument card id, will replace.
	 */
	public static void addChainToDatabase(Chain chain) {
		MysqlDataSource datasource = null;
		Connection connection = null;
		Statement statement = null;
		
		String url="jdbc:mysql://localhost:3306/bookmarkdb";
		String user="Bookmark";
		String password="jetbookmark";
			
		try {
			
			datasource = new MysqlDataSource();
			datasource.setUrl(url);
			datasource.setUser(user);
			datasource.setPassword(password);
			connection = datasource.getConnection();
			statement = connection.createStatement();

			// Table: Chains [int ChainID][int ArgumentCardID][TimeStamp TIMESTAMP]
			// Table: ChainCards [int ChainID][int CardID][int CardX][int CardY][TimeStamp TIMESTAMP]
			// Table: ChainLinks [int ChainID][int Card1ID][int Card2ID][TimeStamp TIMESTAMP]
			

			// ------------------------------------------ //
			// ------- Insert into Chains Table --------- //
			// ------------------------------------------ //
			// Table: Chains [int ChainID][int ArgumentCardID][TimeStamp TIMESTAMP]		 	
			int argumentCardID = chain.getArgumentCard().id;
			int chainId = getChainIdForArgumentCard(argumentCardID);
			String chainQuality = chain.quality.name();
			
			if(chainId == -1) {
				// No Match Found: Create New
				String intoChainsQuery = "INSERT INTO Chains (ArgumentCardID, ChainQuality) VALUES (" + argumentCardID + ", '" + chainQuality + "');";
				System.out.println("Query: " + intoChainsQuery);
				statement.executeUpdate(intoChainsQuery);
				
				// Get the added chain's id.
				chainId = getChainIdForArgumentCard(argumentCardID);
			} else {
				// Match Found: Update
				// (This shouldn't actually be necessary, but it updates the time stamp as well, which could be helpful.
				String updateChainsQuery = ("UPDATE Chains SET ArgumentCardID=" + argumentCardID + ", ChainQuality='" + chainQuality + "' WHERE ChainId=" + chainId + ";");
				statement.executeUpdate(updateChainsQuery);	
			}

			// ---------------------------------------------- //
			// ----- Insert all cards into ChainCards ------- //
			// ---------------------------------------------- //
			// Table: ChainCards [int ChainID][int CardID][int CardX][int CardY][TimeStamp TIMESTAMP]
			// Clear Any Old Data
			String chainCardsRemove = "DELETE FROM ChainCards WHERE ChainID=" + chainId + ";";
			statement.executeUpdate(chainCardsRemove);
			
			// Building Query: INSERT INTO ChainCards (ChainID, CardID, CardX, CardY) VALUES (w, x, y, z), (d, c, b, a), ...
			String chainCardInsertQuery = "INSERT INTO ChainCards (ChainID, CardID, CardX, CardY) VALUES";
			for(Card card : chain.cards.keySet()) {
				Point pos = chain.cards.get(card);
				chainCardInsertQuery += " (" + chainId + ", " + card.id + ", " + pos.getX() + ", " + pos.getY() + "),";
			}
			chainCardInsertQuery = chainCardInsertQuery.substring(0, chainCardInsertQuery.length() - 1) + ";";
			statement.executeUpdate(chainCardInsertQuery);

			// ---------------------------------------------- //
			// ------- Insert Links into ChainLinks --------- //
			// ---------------------------------------------- //
			// Table: ChainLinks [int ChainID][int Card1ID][int Card2ID][TimeStamp TIMESTAMP]
			// Clear Any Old Data
			String chainLinksRemove = "DELETE FROM ChainLinks WHERE ChainID=" + chainId + ";";
			statement.executeUpdate(chainLinksRemove);
			
			// Building Query: INSERT INTO ChainLinks (ChainID, Card1ID, Card2ID) VALUES (x, y, z), (c, b, a), ...
			String chainLinksInsertQuery = "INSERT INTO ChainLinks (ChainID, Card1ID, Card2ID) VALUES";
			for(int[] link : chain.links) {
				chainLinksInsertQuery += " (" + chainId + ", " + link[0] + ", " + link[1] + "),";
			}
			chainLinksInsertQuery = chainLinksInsertQuery.substring(0, chainLinksInsertQuery.length() - 1) + ";";
			statement.executeUpdate(chainLinksInsertQuery);
		} catch(SQLException ex) {
			System.out.println("SQL Exception in Add Chain: " + ex.getMessage());
		} finally {
			try {
				if(statement != null) {
					statement.close();
				}
				if(connection != null) {
					connection.close();
				}
			} catch (SQLException ex) {
				System.out.println("SQL Exception in Add Chain Finally: " + ex.getMessage());
			}
		}
	}
	
	public static void updateCardForStudent(int cardID, int studentID, int classID, String cardType, String cardBody, int pageStart, int pageEnd) {
		MysqlDataSource datasource = null;
		Connection connection = null;
		PreparedStatement prepStmt = null;
		
		String url="jdbc:mysql://localhost:3306/bookmarkdb";
		String user="Bookmark";
		String password="jetbookmark";
			
		try {
			datasource = new MysqlDataSource();
			datasource.setUrl(url);
			datasource.setUser(user);
			datasource.setPassword(password);
			connection = datasource.getConnection();
			
			String prepQuery = "UPDATE Cards SET "
					+ "CardType=?, "
					+ "CardBody=?, "
					+ "PageStart=?, "
					+ "PageEnd=? "
					+ "WHERE (CardID=? AND PersonID=?);";
			
			prepStmt = connection.prepareStatement(prepQuery);
			
			prepStmt.setString(1, cardType);
			prepStmt.setString(2, cardBody);
			prepStmt.setInt(3, pageStart);
			prepStmt.setInt(4, pageEnd);
			prepStmt.setInt(5,  cardID);
			prepStmt.setInt(6, studentID);
			
			prepStmt.executeUpdate();
			
		} catch(SQLException ex) {
			System.out.println("SQL Exception in Add Card for Student: " + ex.getMessage());
		} finally {
			try {
				if(prepStmt != null) {
					prepStmt.close();
				}
				if(connection != null) {
					connection.close();
				}
			} catch (SQLException ex) {
				System.out.println("SQL Exception in Add Card for Student Finally: " + ex.getMessage());
			}
		}
	}
	
	public static void addCardForStudent(int studentID, int classID, int assignmentID, String cardType, String cardBody, int pageStart, int pageEnd) {
		MysqlDataSource datasource = null;
		Connection connection = null;
		Statement statement = null;
		
		String url="jdbc:mysql://localhost:3306/bookmarkdb";
		String user="Bookmark";
		String password="jetbookmark";
			
		try {
			datasource = new MysqlDataSource();
			datasource.setUrl(url);
			datasource.setUser(user);
			datasource.setPassword(password);
			connection = datasource.getConnection();
			statement = connection.createStatement();
			
			DatabaseManager.addCard(connection, studentID, classID, assignmentID, cardType, cardBody, pageStart, pageEnd);
			
		} catch(SQLException ex) {
			System.out.println("SQL Exception in Add Card for Student: " + ex.getMessage());
		} finally {
			try {
				if(statement != null) {
					statement.close();
				}
				if(connection != null) {
					connection.close();
				}
			} catch (SQLException ex) {
				System.out.println("SQL Exception in Add Card for Student Finally: " + ex.getMessage());
			}
		}
	}
	
	public static void setClassAssignment(int classID, int assignmentID) {
		MysqlDataSource datasource = null;
		Connection connection = null;
		Statement statement = null;
		
		String url="jdbc:mysql://localhost:3306/bookmarkdb";
		String user="Bookmark";
		String password="jetbookmark";
			
		try {
			datasource = new MysqlDataSource();
			datasource.setUrl(url);
			datasource.setUser(user);
			datasource.setPassword(password);
			connection = datasource.getConnection();
			statement = connection.createStatement();
			
			 // Table: Classes
			 // [int ClassID unique][char(120) ClassName][text ClassInfo][int TeacherID][int CurrentAssignmentID][TimeStamp TIMESTAMP]
			String query = "UPDATE Classes SET CurrentAssignmentID=" + assignmentID + " WHERE ClassID=" + classID;
			statement.executeUpdate(query);
			
			
		} catch(SQLException ex) {
			System.out.println("SQL Exception in Set Class Assignment: " + ex.getMessage());
		} finally {
			try {
				if(statement != null) {
					statement.close();
				}
				if(connection != null) {
					connection.close();
				}
			} catch (SQLException ex) {
				System.out.println("SQL Exception in Set Class Assignment Finally: " + ex.getMessage());
			}
		}
	}

	/**
	 * Puts a new assignment into the assignment table
	 * Returns the assignment's id
	 */
	public static int addNewAssignment(int classId, String assignmentName, String assignmentInfo, String deckType, int prevAssignment) {
		MysqlDataSource datasource = null;
		Connection connection = null;
		PreparedStatement prepStmt = null;
		
		String url="jdbc:mysql://localhost:3306/bookmarkdb";
		String user="Bookmark";
		String password="jetbookmark";
		
		int assignmentId = -1;
		
		try {
			datasource = new MysqlDataSource();
			datasource.setUrl(url);
			datasource.setUser(user);
			datasource.setPassword(password);
			connection = datasource.getConnection();

			// Table: Assignments
			// [int AssignmentID][char(120) AssignmentName][Text AssignmentInfo][char(120) DeckType][int PrevAssignmentID]
			
			/*
			System.out.println("Creating Assignment With: ");
			System.out.println("\tAssignment Name: " + assignmentName);
			System.out.println("\tAssignment Text: " + assignmentInfo);
			System.out.println("\tDeck Type: " + deckType);
			System.out.println("\tPrevious Assignment: " + prevAssignment);
			*/
			
			// Insert into the assignment table
			String prepQuery = "INSERT INTO Assignments (ClassID, AssignmentName, AssignmentInfo, DeckType, PrevAssignmentID) VALUES (?, ?, ?, ?, ?);";

			prepStmt = connection.prepareStatement(prepQuery);
			
			prepStmt.setInt(1, classId);
			prepStmt.setString(2, assignmentName);
			prepStmt.setString(3, assignmentInfo);
			prepStmt.setString(4, deckType);
			prepStmt.setInt(5, prevAssignment);
			
			prepStmt.executeUpdate();
			
			String assignmentIdQuery = "SELECT AssignmentID FROM Assignments WHERE (ClassID=? AND AssignmentName=? AND AssignmentInfo=? AND DeckType=? AND PrevAssignmentID=?);";
			
			prepStmt.close();
			prepStmt = connection.prepareStatement(assignmentIdQuery);
			prepStmt.setInt(1, classId);
			prepStmt.setString(2, assignmentName);
			prepStmt.setString(3, assignmentInfo);
			prepStmt.setString(4, deckType);
			prepStmt.setInt(5, prevAssignment);
			
			ResultSet results = prepStmt.executeQuery();
			while(results.next()) { // while so we grab the most recent, in case of duplicates.
				// Found Match
				assignmentId = results.getInt("AssignmentID");
			}
			
		} catch(SQLException ex) {
			System.out.println("SQL Exception in Launch Assignment: " + ex.getMessage());
		} finally {
			try {
				if(prepStmt != null) {
					prepStmt.close();
				}
				if(connection != null) {
					connection.close();
				}
			} catch (SQLException ex) {
				System.out.println("SQL Exception in Launch Assignment: " + ex.getMessage());
			}
		}
		
		return assignmentId;
	}
	
	public static boolean addTeam(String teamName, ArrayList<String> studentNames, int classID, int assignmentID) {
		MysqlDataSource datasource = null;
		Connection connection = null;
		PreparedStatement prepStmt = null;
		
		String url="jdbc:mysql://localhost:3306/bookmarkdb";
		String user="Bookmark";
		String password="jetbookmark";
		
		boolean success = false;
		
		try {
			datasource = new MysqlDataSource();
			datasource.setUrl(url);
			datasource.setUser(user);
			datasource.setPassword(password);
			connection = datasource.getConnection();
			
			// Test Teams
			// Teams: [int TeamID unique][char(120) TeamName][int ClassID][int AssignmentID][TimeStamp TIMESTAMP]
			prepStmt = connection.prepareStatement("INSERT INTO Teams (TeamName, ClassID, AssignmentID) VALUES (?, ?, ?)");
			
			prepStmt.setString(1, teamName);
			prepStmt.setInt(2, classID);
			prepStmt.setInt(3, assignmentID);
			
			prepStmt.executeUpdate();
			prepStmt.close();
			
			int teamID = -1;
			String teamIDQuery = "SELECT TeamID FROM Teams WHERE (TeamName='" + teamName + "' AND ClassID=" + classID + " AND AssignmentID=" + assignmentID + ");";
			teamID = DatabaseManager.getIntFromDB("Get Team ID From Name in Add Team", teamIDQuery, "TeamID", -1);
			
			if(teamID != -1) {
				// Test Team Links
				// TeamStudents: [int TeamID][int StudentID][TimeStamp TIMESTAMP]
				for(String studentName : studentNames) {
					int studentID = DatabaseManager.getPersonIDFromName(studentName);
					
					prepStmt = connection.prepareStatement("INSERT INTO TeamStudents (TeamID, StudentID) VALUES (?, ?)");
					prepStmt.setInt(1,  teamID);
					prepStmt.setInt(2,  studentID);
					
					prepStmt.executeUpdate();
					prepStmt.close();
				}
				
				success = true;
			}
		} catch(SQLException ex) {
			System.out.println("SQL Exception in Launch Assignment: " + ex.getMessage());
		} finally {
			try {
				if(prepStmt != null) {
					prepStmt.close();
				}
				if(connection != null) {
					connection.close();
				}
			} catch (SQLException ex) {
				System.out.println("SQL Exception in Launch Assignment: " + ex.getMessage());
			}
		}
		
		return success;
	}
	
	// --------------------------------------------------------------------------
	// ---------------------------- TESTING METHODS -----------------------------
	// --------------------------------------------------------------------------
	
	public static void createTestContent() {	
		MysqlDataSource datasource = null;
		Connection connection = null;
		Statement statement = null;
		
		String url="jdbc:mysql://localhost:3306/bookmarkdb";
		String user="Bookmark";
		String password="jetbookmark";
			
		try {
			datasource = new MysqlDataSource();
			datasource.setUrl(url);
			datasource.setUser(user);
			datasource.setPassword(password);
			connection = datasource.getConnection();
			statement = connection.createStatement();
					
				
			// ------------------------------------------------------------------------
			// ----------------------------- Test Class 1 -----------------------------
			// ------------------------------------------------------------------------
			
			// Assignments: [int AssignmentID][char(120) AssignmentName][Text AssignmentInfo][char(120) DeckType][int PrevAssignmentID]
			statement.executeUpdate("INSERT INTO Assignments (ClassID, AssignmentName, AssignmentInfo, DeckType, PrevAssignmentID) VALUES ("
					+ "1, "
					+ "'Doctor Who Assignment', "
					+ "'This is an assignment description for Doctor Who.', "
					+ "'Fiction', "
					+ "0);");
			statement.executeUpdate("INSERT INTO Assignments (ClassID, AssignmentName, AssignmentInfo, DeckType, PrevAssignmentID) VALUES ("
					+ "2, "
					+ "'Avatar Assignment', "
					+ "'This is an assignment description for Avatar.', "
					+ "'Non-Fiction', "
					+ "0);");
						
			
			// Classes: [int ClassID unique][char(120) ClassName][text ClassInfo][int TeacherID][int CurrentAssignmentID][TimeStamp TIMESTAMP]
			statement.executeUpdate("INSERT INTO Classes (ClassName, ClassInfo, TeacherID, CurrentAssignmentID) VALUES ('Whovians', 'This test class has Doctors in it!', 1, 1)");
			
			// Test Teacher
			// People: [int PersonID unique][char(120) UserName][char(120) Password][char(120) PersonName][TimeStamp TIMESTAMP]
			statement.executeUpdate("INSERT INTO People (UserName, Password, PersonName) VALUES ('a', 'a', 'Doctor Who Teacher')");
			
			// Test Students
			DatabaseManager.addStudent(statement, "Doc12", "Doc12", "Peter Capaldi", 1);
			DatabaseManager.addStudent(statement, "Doc11", "Doc11", "Matt Smith", 1);
			DatabaseManager.addStudent(statement, "Doc10", "Doc10", "David Tennant", 1);
			DatabaseManager.addStudent(statement, "Doc9", "Doc9", "Christopher Eccelston", 1);
			DatabaseManager.addStudent(statement, "Doc8", "Doc8", "Paul McGann", 1);
			DatabaseManager.addStudent(statement, "Doc7", "Doc7", "Sylvester McCoy", 1);
			
			// Cards
			DatabaseManager.addCard(connection, 2, 1, 1, "Argument",  "Here is the point I wish to argue! Hooray!", -1, -1);
			DatabaseManager.addCard(connection, 2, 1, 1, "Tone", "I have uncovered the mysteries of Tone! Hooray!", -1, -1);
			DatabaseManager.addCard(connection, 3, 1, 1, "Imagery" , "Here is some imagery! Huzzah!", 2, -1);
			DatabaseManager.addCard(connection, 3, 1, 1, "Diction" , "Words are my speciality! Huzzah!", 7, 8);
			DatabaseManager.addCard(connection, 4, 1, 1, "Diction" , "Here are some fancy words! Yippie!", 6, 7);
			DatabaseManager.addCard(connection, 4, 1, 1, "Theme" , "I theme, you theme, we all theme for Ice Cream! Yippie!", -1, -1);
			DatabaseManager.addCard(connection, 5, 1, 1, "Tone" , "This is how the author felt when writing! Woohoo!", -1, -1);
			DatabaseManager.addCard(connection, 5, 1, 1, "Other" , "I found some alliteration! Woohoo!", 9, -1);
			DatabaseManager.addCard(connection, 6, 1, 1, "Theme" , "Here is a main, underlying idea of the text! Yowzah!", -1, -1);
			DatabaseManager.addCard(connection, 6, 1, 1, "Argument" , "This is a thing I believe! Yowzah!", -1, -1);
			DatabaseManager.addCard(connection, 7, 1, 1, "Other" , "Here is an incredibly original thought! I love this game!!", -1, -1);
			DatabaseManager.addCard(connection, 7, 1, 1, "Imagery" , "Beautiful words paint a beautiful picture! I love this game!", 8, 10);
			DatabaseManager.addCard(connection, 2, 1, 1, "Plot Point",  "People did things! Hooray!", -1, -1);
			DatabaseManager.addCard(connection, 2, 1, 1, "Theme", "Here is a theme! Hooray!", -1, -1);
			DatabaseManager.addCard(connection, 3, 1, 1, "Plot Point" , "A thing occurred! Huzzah!", 2, -1);
			DatabaseManager.addCard(connection, 3, 1, 1, "Tone" , "A tone! Huzzah!", 7, 8);
			DatabaseManager.addCard(connection, 4, 1, 1, "Plot Point" , "Cool stuff happened! Yippie!", 6, 7);
			DatabaseManager.addCard(connection, 4, 1, 1, "Argument" , "The argument is this! Yippie!", -1, -1);
			DatabaseManager.addCard(connection, 5, 1, 1, "Plot Point" , "Happenings! Woohoo!", -1, -1);
			DatabaseManager.addCard(connection, 5, 1, 1, "Imagery" , "I found some descriptive words! Woohoo!", 9, -1);
			DatabaseManager.addCard(connection, 6, 1, 1, "Plot Point" , "This is a thing that happened! Yowzah!", -1, -1);
			DatabaseManager.addCard(connection, 6, 1, 1, "Diction" , "Word choice! Yowzah!", -1, -1);
			DatabaseManager.addCard(connection, 7, 1, 1, "Plot Point" , "Here is a brand new thought! I love this game!!", -1, -1);
			DatabaseManager.addCard(connection, 7, 1, 1, "Imagery" , "Pictures! I love this game!", 8, 10);


			// Test Teams
			// Teams: [int TeamID unique][char(120) TeamName][int ClassID][int AssignmentID][TimeStamp TIMESTAMP]
			statement.executeUpdate("INSERT INTO Teams (TeamName, ClassID, AssignmentID) VALUES ('Daleks', 1, 1)");
			statement.executeUpdate("INSERT INTO Teams (TeamName, ClassID, AssignmentID) VALUES ('Cybermen', 1, 1)");
			statement.executeUpdate("INSERT INTO Teams (TeamName, ClassID, AssignmentID) VALUES ('Ood', 1, 1)");
			
			// Test Team Links
			// TeamStudents: [int TeamID][int StudentID][TimeStamp TIMESTAMP]
			statement.executeUpdate("INSERT INTO TeamStudents (TeamID, StudentID) VALUES (1, 2)");
			statement.executeUpdate("INSERT INTO TeamStudents (TeamID, StudentID) VALUES (1, 3)");
			statement.executeUpdate("INSERT INTO TeamStudents (TeamID, StudentID) VALUES (2, 4)");
			statement.executeUpdate("INSERT INTO TeamStudents (TeamID, StudentID) VALUES (2, 5)");
			statement.executeUpdate("INSERT INTO TeamStudents (TeamID, StudentID) VALUES (3, 6)");
			statement.executeUpdate("INSERT INTO TeamStudents (TeamID, StudentID) VALUES (3, 7)");
			

			// ------------------------------------------------------------------------
			// ----------------------------- Test Class 2 -----------------------------
			// ------------------------------------------------------------------------
			
			// Classes: [int ClassID unique][char(120) ClassName][text ClassInfo][int TeacherID][int CurrentAssignmentID][TimeStamp TIMESTAMP]
			statement.executeUpdate("INSERT INTO Classes (ClassName, ClassInfo, TeacherID, CurrentAssignmentID) VALUES ("
					+ "'Whatever the Avatar World is Called', "
					+ "'This test class has avatar characters in it!', "
					+ "8, 2)");
			
			// Test Teacher
			// People: [int PersonID unique][char(120) UserName][char(120) Password][char(120) PersonName][TimeStamp TIMESTAMP]
			statement.executeUpdate("INSERT INTO People (UserName, Password, PersonName) VALUES ('b', 'b', 'Avatar Teacher')");
			
			// Test Students
			DatabaseManager.addStudent(statement, "Aang", "a", "Aang", 2); // 9
			DatabaseManager.addStudent(statement, "Appa", "a", "Appa", 2); // 10
			DatabaseManager.addStudent(statement, "Katara", "k", "Katara", 2); // 11
			DatabaseManager.addStudent(statement, "Sokka", "s", "Sokka", 2); // 12
			DatabaseManager.addStudent(statement, "Toph", "t", "Toph", 2); // 13
			DatabaseManager.addStudent(statement, "Bumi", "b", "Bumi", 2); // 14
			DatabaseManager.addStudent(statement, "Zuko", "z", "Zuko", 2); // 15
			DatabaseManager.addStudent(statement, "Uncle Iroh", "ui", "Uncle Iroh", 2); // 16
			
			
			
			// Cards
			DatabaseManager.addCard(connection, 9, 2, 2, "Argument",  "I am Aang. Balance and peace are the good stuff.", -1, -1);
			DatabaseManager.addCard(connection, 10, 2, 2, "Tone", "I am Appa. This passage portrays the effervescent essence of extraordinary exuberance.", 100, -1);
			DatabaseManager.addCard(connection, 11, 2, 2, "Imagery" , "I am Katara. There is a beautiful stream, flowing like the hope in my heart.", 2, -1);
			DatabaseManager.addCard(connection, 12, 2, 2, "Diction" , "I am Sokka! I found words!", 7, 8);
			DatabaseManager.addCard(connection, 13, 2, 2, "Diction" , "I am Toph. I found better words than Sokka.", 6, 7);
			DatabaseManager.addCard(connection, 14, 2, 2, "Theme" , "I'm Bumi! There's a theme of changing perspective.", -1, -1);
			DatabaseManager.addCard(connection, 15, 2, 2, "Tone" , "I am Zuko. The tone is angst.", -1, -1);
			DatabaseManager.addCard(connection, 16, 2, 2, "Other" , "I am Uncle Iroh. Humility is key.", 9, -1);
			DatabaseManager.addCard(connection, 9, 2, 2, "Theme" , "I am Aang. The theme is forgiveness.", -1, -1);
			DatabaseManager.addCard(connection, 10, 2, 2, "Argument" , "I am Appa. The true meaning of this work lies not in its wearisome words, but in its deeper sense of abstractional injustice.", -1, -1);
			DatabaseManager.addCard(connection, 11, 2, 2, "Other" , "I am Katara. I have found Hope.", -1, -1);
			DatabaseManager.addCard(connection, 12, 2, 2, "Imagery" , "I am Sokka! This book has pictures!", 8, 10);
			DatabaseManager.addCard(connection, 13, 2, 2, "Plot Point",  "I am Toph. Some things happened. I didn't care.", -1, -1);
			DatabaseManager.addCard(connection, 14, 2, 2, "Argument", "I am Bumi! It was all a dream!", -1, -1);
			DatabaseManager.addCard(connection, 15, 2, 2, "Plot Point" , "I am Zuko. I lost my honor. I will regain my honor. My father will be proud.", 2, -1);
			DatabaseManager.addCard(connection, 16, 2, 2, "Plot Point" , "I am Uncle Iroh. They had a nice cup of jasmine tea.", 7, 8);


			// Test Teams
			// Teams: [int TeamID unique][char(120) TeamName][int ClassID][int AssignmentID][TimeStamp TIMESTAMP]
			statement.executeUpdate("INSERT INTO Teams (TeamName, ClassID, AssignmentID) VALUES ('Air Nation', 2, 2)"); // 4
			statement.executeUpdate("INSERT INTO Teams (TeamName, ClassID, AssignmentID) VALUES ('Water Nation', 2, 2)"); // 5
			statement.executeUpdate("INSERT INTO Teams (TeamName, ClassID, AssignmentID) VALUES ('Earth Nation', 2, 2)"); // 6
			statement.executeUpdate("INSERT INTO Teams (TeamName, ClassID, AssignmentID) VALUES ('Fire Nation', 2, 2)"); // 7
			
			// Test Team Links
			// TeamStudents: [int TeamID][int StudentID][TimeStamp TIMESTAMP]
			statement.executeUpdate("INSERT INTO TeamStudents (TeamID, StudentID) VALUES (4, 9)");
			statement.executeUpdate("INSERT INTO TeamStudents (TeamID, StudentID) VALUES (4, 10)");
			statement.executeUpdate("INSERT INTO TeamStudents (TeamID, StudentID) VALUES (5, 11)");
			statement.executeUpdate("INSERT INTO TeamStudents (TeamID, StudentID) VALUES (5, 12)");
			statement.executeUpdate("INSERT INTO TeamStudents (TeamID, StudentID) VALUES (6, 13)");
			statement.executeUpdate("INSERT INTO TeamStudents (TeamID, StudentID) VALUES (6, 14)");
			statement.executeUpdate("INSERT INTO TeamStudents (TeamID, StudentID) VALUES (7, 15)");
			statement.executeUpdate("INSERT INTO TeamStudents (TeamID, StudentID) VALUES (7, 16)");
			
		} catch(SQLException ex) {
			System.out.println("SQL Exception in Create Test Content: " + ex.getMessage());
		} finally {
			try {
				if(statement != null) {
					statement.close();
				}
				if(connection != null) {
					connection.close();
				}
			} catch (SQLException ex) {
				System.out.println("SQL Exception in Create Test Content Finally: " + ex.getMessage());
			}
		}
	}
	
	// --------------------------------------------------------------------------
	// ----------------------------- HELPER METHODS -----------------------------
	// --------------------------------------------------------------------------

	/**
	 * Loads standard deck types into database.
	 */
	public static void loadDeckTypes() {
		MysqlDataSource datasource = null;
		Connection connection = null;
		PreparedStatement prepStmt = null;
		
		String url="jdbc:mysql://localhost:3306/bookmarkdb";
		String user="Bookmark";
		String password="jetbookmark";
			
		try {
			datasource = new MysqlDataSource();
			datasource.setUrl(url);
			datasource.setUser(user);
			datasource.setPassword(password);
			connection = datasource.getConnection();

			// Table: DeckCardTypes
			// [char(120) DeckTypeName][char(120) CardType]
			prepStmt = connection.prepareStatement("INSERT INTO DeckCardTypes (DeckTypeName, CardType) VALUES (?, ?);");
			
			prepStmt.setString(1, "Fiction");
			prepStmt.setString(2, "Argument");
			prepStmt.executeUpdate();
			prepStmt.setString(2, "Imagery");
			prepStmt.executeUpdate();
			prepStmt.setString(2, "Tone");
			prepStmt.executeUpdate();
			prepStmt.setString(2, "Theme");
			prepStmt.executeUpdate();
			prepStmt.setString(2, "Plot Point");
			prepStmt.executeUpdate();
			prepStmt.setString(2, "Figurative Language");
			prepStmt.executeUpdate();
			prepStmt.setString(2, "Subgenre");
			prepStmt.executeUpdate();
			prepStmt.setString(2, "Other");
			prepStmt.executeUpdate();
			
			
			prepStmt.clearParameters();
			prepStmt.setString(1, "Non-Fiction");
			prepStmt.setString(2, "Argument");
			prepStmt.executeUpdate();
			prepStmt.setString(2, "Organizational Patterns");
			prepStmt.executeUpdate();
			prepStmt.setString(2, "Main Idea");
			prepStmt.executeUpdate();
			prepStmt.setString(2, "Supporting Details");
			prepStmt.executeUpdate();
			prepStmt.setString(2, "Author's Specific Purpose");
			prepStmt.executeUpdate();
			prepStmt.setString(2, "Audience");
			prepStmt.executeUpdate();
			prepStmt.setString(2, "Other");
			prepStmt.executeUpdate();
			
		} catch(SQLException ex) {
			System.out.println("SQL Exception in Create Deck Types: " + ex.getMessage());
		} finally {
			try {
				if(prepStmt != null) {
					prepStmt.close();
				}
				if(connection != null) {
					connection.close();
				}
			} catch (SQLException ex) {
				System.out.println("SQL Exception in Create Deck Types Finally: " + ex.getMessage());
			}
		}
	}
	
	/**
	 * Returns a single String result from a query to the database
	 * @param task The task you are trying to perform (used for error messages).
	 * @param query The query you wish to make.
	 * @param resultsCol The string name of the column that will contain the String result
	 * @param errStr The String to return on error
	 * @return If no result or error, errStr. Else, the String stored in resultsCol.
	 */
	private static String getStringFromDB(String task, String query, String resultsCol, String errStr) {
		String retStr = errStr;
		
		MysqlDataSource datasource = null;
		Connection connection = null;
		Statement statement = null;
		
		String url="jdbc:mysql://localhost:3306/bookmarkdb";
		String user="Bookmark";
		String password="jetbookmark";
			
		try {
			datasource = new MysqlDataSource();
			datasource.setUrl(url);
			datasource.setUser(user);
			datasource.setPassword(password);
			connection = datasource.getConnection();
			statement = connection.createStatement();
				
			ResultSet results = statement.executeQuery(query);
			if(results.next()) {
				// Found Match
				retStr = results.getString(resultsCol);
			} else {
				// Nothing Found
				retStr = errStr;
			}
			
		} catch(SQLException ex) {
			System.out.println("SQL Exception in " + task + ": " + ex.getMessage());
			retStr = errStr;
		} finally {
			try {
				if(statement != null) {
					statement.close();
				}
				if(connection != null) {
					connection.close();
				}
			} catch (SQLException ex) {
				System.out.println("SQL Exception in " + task + "'s Finally: " + ex.getMessage());
			}
		}
		
		return retStr;
	}
	
	/**
	 * Returns a single integer result from a query to the database
	 * @param task The task you are trying to perform (used for error messages).
	 * @param query The query you wish to make.
	 * @param resultsCol The string name of the column that will contain the integer result
	 * @param errNum The number to return on error
	 * @return If no result or error, errNum. Else, the integer stored in resultsCol.
	 */
	private static int getIntFromDB(String task, String query, String resultsCol, int errNum) {
		int retNum = errNum;
		
		MysqlDataSource datasource = null;
		Connection connection = null;
		Statement statement = null;
		
		String url="jdbc:mysql://localhost:3306/bookmarkdb";
		String user="Bookmark";
		String password="jetbookmark";
			
		try {
			datasource = new MysqlDataSource();
			datasource.setUrl(url);
			datasource.setUser(user);
			datasource.setPassword(password);
			connection = datasource.getConnection();
			statement = connection.createStatement();
				
			ResultSet results = statement.executeQuery(query);
			if(results.next()) {
				// Found Match
				retNum = results.getInt(resultsCol);
			} else {
				// Nothing Found
				retNum = errNum;
			}
		} catch(SQLException ex) {
			System.out.println("SQL Exception in " + task + ": " + ex.getMessage());
			retNum = errNum;
		} finally {
			try {
				if(statement != null) {
					statement.close();
				}
				if(connection != null) {
					connection.close();
				}
			} catch (SQLException ex) {
				System.out.println("SQL Exception in " + task + "'s Finally: " + ex.getMessage());
			}
		}
		
		return retNum;
	}
	
	private static void addStudent(Statement statement, String username, String password, String name, int classID) throws SQLException {
		
		// Add to student table
		// People: [int PersonID unique][char(120) UserName][char(120) Password][char(120) PersonName][TimeStamp TIMESTAMP]
		statement.executeUpdate("INSERT INTO People (UserName, Password, PersonName) VALUES ("
				+ "'" + username + "', "
				+ "'" + password + "', "
				+ "'" + name  + "')");
		
		// Add to student class list
		// ClassStudents: [int ClassID][int StudentID][TimeStamp TIMESTAMP]
		statement.executeUpdate("INSERT INTO ClassStudents (ClassID, StudentID) SELECT "
				+ classID + ", "
				+ "PersonID FROM People ORDER BY PersonID DESC LIMIT 1;");
		
	}
	
	private static void addCard(
			Connection connection,
			int studentID,
			int classID,
			int assignmentID,
			String cardType,
			String cardBody,
			int pageStart,
			int pageEnd) throws SQLException {
		
		PreparedStatement prepStmt = connection.prepareStatement("INSERT INTO Cards (PersonID, ClassID, AssignmentID, CardType, CardBody, PageStart, PageEnd) VALUES (?, ?, ?, ?, ?, ?, ?);");
		
		prepStmt.setInt(1,  studentID);
		prepStmt.setInt(2, classID);
		prepStmt.setInt(3, assignmentID);
		prepStmt.setString(4, cardType);
		prepStmt.setString(5, cardBody);
		prepStmt.setInt(6, pageStart);
		prepStmt.setInt(7, pageEnd);

		prepStmt.executeUpdate();
		
	}
	
	/*******
	 * METHOD WRAPPER
	 	MysqlDataSource datasource = null;
		Connection connection = null;
		Statement statement = null;
		
		String url="jdbc:mysql://localhost:3306/bookmarkdb";
		String user="Bookmark";
		String password="jetbookmark";
			
		try {
			datasource = new MysqlDataSource();
			datasource.setUrl(url);
			datasource.setUser(user);
			datasource.setPassword(password);
			connection = datasource.getConnection();
			statement = connection.createStatement();
				
			
			///// SQL Content Goes Here ///// 
			
			
		} catch(SQLException ex) {
			System.out.println("SQL Exception in [Method Name]: " + ex.getMessage());
		} finally {
			try {
				if(statement != null) {
					statement.close();
				}
				if(connection != null) {
					connection.close();
				}
			} catch (SQLException ex) {
				System.out.println("SQL Exception in [Method Name] Finally: " + ex.getMessage());
			}
		}
	 * 
	 */
	
	
}
