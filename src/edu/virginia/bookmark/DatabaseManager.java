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
 *		[int ChainID][int CardID][int CardX][int CardY][TimeStamp TIMESTAMP]
 * 
 * Table: ChainLinks
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
			statement.executeUpdate("DROP TABLE IF EXISTS ChainLinks");
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
			// [int ChainID][int CardID][int CardX][int CardY][TimeStamp TIMESTAMP]
			statement.executeUpdate("CREATE TABLE ChainCards (ChainID INT NOT NULL, CardID INT NOT NULL, CardX INT NOT NULL, CardY INT NOT NULL, TimeStamp TIMESTAMP);");
			 
			// ChainLinks
			// [int ChainID][int Card1ID][int Card2ID][TimeStamp TIMESTAMP]
			statement.executeUpdate("CREATE TABLE ChainLinks (ChainID INT NOT NULL, Card1ID INT NOT NULL, Card2ID INT NOT NULL, TimeStamp TIMESTAMP);");
			
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
				
			String query = ("SELECT CardID FROM Cards WHERE PersonID=" + studentId + " AND ClassID=" + classId + ";");
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
			
			if(chainId == -1) {
				// No Match Found: Create New
				String intoChainsQuery = "INSERT INTO Chains (ArgumentCardID) VALUES (" + argumentCardID + ");";
				statement.executeUpdate(intoChainsQuery);
				
				// Get the added chain's id.
				chainId = getChainIdForArgumentCard(argumentCardID);
			} else {
				// Match Found: Update
				// (This shouldn't actually be necessary, but it updates the time stamp as well, which could be helpful.
				String updateChainsQuery = ("UPDATE Chains SET ArgumentCardID=" + argumentCardID + " WHERE ChainId=" + chainId + ";");
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
			DatabaseManager.addCard(connection, 2, 1, CardType.Plot_Point.name(),  "People did things! Hooray!", -1, -1);
			DatabaseManager.addCard(connection, 2, 1, CardType.Theme.name(), "Here is a theme! Hooray!", -1, -1);
			DatabaseManager.addCard(connection, 3, 1, CardType.Plot_Point.name() , "A thing occurred! Huzzah!", 2, -1);
			DatabaseManager.addCard(connection, 3, 1, CardType.Tone.name() , "A tone! Huzzah!", 7, 8);
			DatabaseManager.addCard(connection, 4, 1, CardType.Plot_Point.name() , "Cool stuff happened! Yippie!", 6, 7);
			DatabaseManager.addCard(connection, 4, 1, CardType.Argument.name() , "The argument is this! Yippie!", -1, -1);
			DatabaseManager.addCard(connection, 5, 1, CardType.Plot_Point.name() , "Happenings! Woohoo!", -1, -1);
			DatabaseManager.addCard(connection, 5, 1, CardType.Imagery.name() , "I found some descriptive words! Woohoo!", 9, -1);
			DatabaseManager.addCard(connection, 6, 1, CardType.Plot_Point.name() , "This is a thing that happened! Yowzah!", -1, -1);
			DatabaseManager.addCard(connection, 6, 1, CardType.Diction.name() , "Word choice! Yowzah!", -1, -1);
			DatabaseManager.addCard(connection, 7, 1, CardType.Plot_Point.name() , "Here is a brand new thought! I love this game!!", -1, -1);
			DatabaseManager.addCard(connection, 7, 1, CardType.Imagery.name() , "Pictures! I love this game!", 8, 10);


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
