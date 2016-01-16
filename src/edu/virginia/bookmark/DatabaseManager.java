package edu.virginia.bookmark;

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

import edu.virginia.bookmark.Card.CardType;

import java.util.ArrayList;
import java.util.HashMap;

/******
 * 
 * DATABASE SCHEMA
 * 
 * Table: People
 * 		[int PersonID unique][char(120) UserName][char(120) Password][char(120) PersonName][TimeStamp TIMESTAMP]
 * 
 * Table: Classes
 * 		[int ClassID unique][char(120) ClassName][text ClassInfo][int TeacherID][TimeStamp TIMESTAMP]
 * 
 * Table: Teams
 * 		[int TeamID unique][char(120) TeamName][int ClassID][TimeStamp TIMESTAMP]
 * 
 * Table: ClassStudents
 * 		[int ClassID][int StudentID][TimeStamp TIMESTAMP]
 * 
 * Table: TeamStudents
 * 		[int TeamID][int StudentID][TimeStamp TIMESTAMP]
 * 
 * Table: Cards
 * 		[int CardID unique][int PersonID][int ClassID][char(120) CardType][text CardBody][int PageStart][int PageEnd][TimeStamp TIMESTAMP]
 * 
 * Table: Chains
 * 		[int ChainID][int ArgumentCardID][TimeStamp TIMESTAMP]
 * 
 * Table: ChainCards
 *		[int ChainID][int CardID][TimeStamp TIMESTAMP]
 * 
 * Table: CardLinks
 * 		[int ChainID][int Card1ID][int Card2ID][TimeStamp TIMESTAMP]
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
			statement.executeUpdate("DROP TABLE IF EXISTS CardLinks");
			
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
			// [int ClassID unique][char(120) ClassName][text ClassInfo][int TeacherID][TimeStamp TIMESTAMP]
			statement.executeUpdate("CREATE TABLE Classes "
					+ "(ClassID INT UNSIGNED AUTO_INCREMENT PRIMARY KEY, "
					+ "ClassName CHAR(120) NOT NULL, "
					+ "ClassInfo TEXT, "
					+ "TeacherID INT NOT NULL, "
					+ "TimeStamp TIMESTAMP);");
			
			// Teams
			// [int TeamID unique][char(120) TeamName][int ClassID][TimeStamp TIMESTAMP]
			statement.executeUpdate("CREATE TABLE Teams "
					+ "(TeamID INT UNSIGNED AUTO_INCREMENT PRIMARY KEY, "
					+ "TeamName CHAR(120) NOT NULL, "
					+ "ClassID INT NOT NULL, "
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
					+ "CardType CHAR(120) NOT NULL, "
					+ "CardBody TEXT, "
					+ "PageStart INT, "
					+ "PageEnd INT, "
					+ "TimeStamp TIMESTAMP);");
			 
			// Table: Chains
			// [int ChainID][int ArgumentCardID][TimeStamp TIMESTAMP]
			statement.executeUpdate("CREATE TABLE Chains (ChainID INT UNSIGNED AUTO_INCREMENT PRIMARY KEY, ArgumentCardID INT NOT NULL, TimeStamp TIMESTAMP);");
			 
			// ChainCards
			// [int ChainID][int CardID][TimeStamp TIMESTAMP]
			statement.executeUpdate("CREATE TABLE ChainCards (ChainID INT NOT NULL, CardID INT NOT NULL, TimeStamp TIMESTAMP);");
			 
			// CardLinks
			// [int ChainID][int Card1ID][int Card2ID][TimeStamp TIMESTAMP]
			statement.executeUpdate("CREATE TABLE CardLinks (ChainID INT NOT NULL, Card1ID INT NOT NULL, Card2ID INT NOT NULL, TimeStamp TIMESTAMP);");
			
			// Create the test class and data
			DatabaseManager.createTestContent();
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
				
			
			ResultSet results = statement.executeQuery("SELECT TeamID FROM Teams WHERE ClassID = " + classId + ";");
			while(results.next()) {
				teamIds.add(results.getInt("TeamID"));
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
	
	// --------------------------------------------------------------------------
	// ----------------------------- GET CARD INFO ------------------------------
	// --------------------------------------------------------------------------
	
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
			// [int CardID unique][int PersonID][int ClassID][char(120) CardType][text CardBody][int PageStart][int PageEnd][TimeStamp TIMESTAMP]
					 
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
		
	// --------------------------------------------------------------------------
	// ---------------------------- GET STUDENT INFO ----------------------------
	// --------------------------------------------------------------------------
	
	/**
	 * Uses the id to lookup the id's of every card in the student's deck. 
	 */
	public static ArrayList<Integer> loadStudentDeckIds(int studentId, int classId) {
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
				
			
			ResultSet results = statement.executeQuery("SELECT CardID FROM Cards WHERE PersonID=" + studentId + " AND ClassID=" + classId + ";");
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
			+ "FROM Teams, TeamStudents "
			+ "WHERE (Teams.TeamID=TeamStudents.TeamID AND TeamStudents.StudentID=" + id + ");";
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
				classIds.add(results.getInt("ClassID"));
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
	
	public static void UpdateCardForStudent(int cardID, int studentID, int classID, String cardType, String cardBody, int pageStart, int pageEnd) {
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
	
	public static void addCardForStudent(int studentID, int classID, String cardType, String cardBody, int pageStart, int pageEnd) {
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
			
			DatabaseManager.addCard(connection, studentID, classID, cardType, cardBody, pageStart, pageEnd);
			
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
				

			// Test Class
			// Classes: [int ClassID unique][char(120) ClassName][text ClassInfo][int TeacherID][TimeStamp TIMESTAMP]
			statement.executeUpdate("INSERT INTO Classes (ClassName, ClassInfo, TeacherID) VALUES ('Test Class', 'This test class has Doctors in it!', 1)");
			
			// Test Teacher
			// People: [int PersonID unique][char(120) UserName][char(120) Password][char(120) PersonName][TimeStamp TIMESTAMP]
			statement.executeUpdate("INSERT INTO People (UserName, Password, PersonName) VALUES ('a', 'a', 'Teacher')");
			
			// Test Students
			DatabaseManager.addStudent(statement, "Doc12", "Doc12", "Peter Capaldi", 1);
			DatabaseManager.addStudent(statement, "Doc11", "Doc11", "Matt Smith", 1);
			DatabaseManager.addStudent(statement, "Doc10", "Doc10", "David Tennant", 1);
			DatabaseManager.addStudent(statement, "Doc9", "Doc9", "Christopher Eccelston", 1);
			DatabaseManager.addStudent(statement, "Doc8", "Doc8", "Paul McGann", 1);
			DatabaseManager.addStudent(statement, "Doc7", "Doc7", "Sylvester McCoy", 1);
			
			// Cards
			DatabaseManager.addCard(connection, 2, 1, CardType.Argument.name(),  "Here is the point I wish to argue! Hooray!", -1, -1);
			DatabaseManager.addCard(connection, 2, 1, CardType.Tone.name(), "I have uncovered the mysteries of Tone! Hooray!", -1, -1);
			DatabaseManager.addCard(connection, 3, 1, CardType.Imagery.name() , "Here is some imagery! Huzzah!", 2, -1);
			DatabaseManager.addCard(connection, 3, 1, CardType.Diction.name() , "Words are my speciality! Huzzah!", 7, 8);
			DatabaseManager.addCard(connection, 4, 1, CardType.Diction.name() , "Here are some fancy words! Yippie!", 6, 7);
			DatabaseManager.addCard(connection, 4, 1, CardType.Theme.name() , "I theme, you theme, we all theme for Ice Cream! Yippie!", -1, -1);
			DatabaseManager.addCard(connection, 5, 1, CardType.Tone.name() , "This is how the author felt when writing! Woohoo!", -1, -1);
			DatabaseManager.addCard(connection, 5, 1, CardType.Other.name() , "I found some alliteration! Woohoo!", 9, -1);
			DatabaseManager.addCard(connection, 6, 1, CardType.Theme.name() , "Here is a main, underlying idea of the text! Yowzah!", -1, -1);
			DatabaseManager.addCard(connection, 6, 1, CardType.Argument.name() , "This is a thing I believe! Yowzah!", -1, -1);
			DatabaseManager.addCard(connection, 7, 1, CardType.Other.name() , "Here is an incredibly original thought! I love this game!!", -1, -1);
			DatabaseManager.addCard(connection, 7, 1, CardType.Imagery.name() , "Beautiful words paint a beautiful picture! I love this game!", 8, 10);
			
			// Test Teams
			// Teams: [int TeamID unique][char(120) TeamName][int ClassID][TimeStamp TIMESTAMP]
			statement.executeUpdate("INSERT INTO Teams (TeamName, ClassID) VALUES ('Daleks', 1)");
			statement.executeUpdate("INSERT INTO Teams (TeamName, ClassID) VALUES ('Cybermen', 1)");
			statement.executeUpdate("INSERT INTO Teams (TeamName, ClassID) VALUES ('Ood', 1)");
			
			// Test Team Links
			// TeamStudents: [int TeamID][int StudentID][TimeStamp TIMESTAMP]
			statement.executeUpdate("INSERT INTO TeamStudents (TeamID, StudentID) VALUES (1, 2)");
			statement.executeUpdate("INSERT INTO TeamStudents (TeamID, StudentID) VALUES (1, 3)");
			statement.executeUpdate("INSERT INTO TeamStudents (TeamID, StudentID) VALUES (2, 4)");
			statement.executeUpdate("INSERT INTO TeamStudents (TeamID, StudentID) VALUES (2, 5)");
			statement.executeUpdate("INSERT INTO TeamStudents (TeamID, StudentID) VALUES (3, 6)");
			statement.executeUpdate("INSERT INTO TeamStudents (TeamID, StudentID) VALUES (3, 7)");
			
			
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
			String cardType,
			String cardBody,
			int pageStart,
			int pageEnd) throws SQLException {
		
		PreparedStatement prepStmt = connection.prepareStatement("INSERT INTO Cards (PersonID, ClassID, CardType, CardBody, PageStart, PageEnd) VALUES (?, ?, ?, ?, ?, ?);");
		
		prepStmt.setInt(1,  studentID);
		prepStmt.setInt(2, classID);
		prepStmt.setString(3, cardType);
		prepStmt.setString(4, cardBody);
		prepStmt.setInt(5, pageStart);
		prepStmt.setInt(6, pageEnd);

		prepStmt.executeUpdate();
		
	}
	
	private static String sanitizeString(String str) {
		 String data = null;
		 if (str != null && str.length() > 0) {
			  str = str.replace("\\", "\\\\");
			  str = str.replace("'", "\\'");
			  str = str.replace("\0", "\\0");
			  str = str.replace("\n", "\\n");
			  str = str.replace("\r", "\\r");
			  str = str.replace("\"", "\\\"");
			  str = str.replace("\\x1a", "\\Z");
			  data = str;
		 }
		 return data;
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
