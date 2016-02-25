package edu.virginia.bookmark;

import java.awt.List;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import edu.virginia.bookmark.Session.SessionState;

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
			toJoin.clearUpToDateStatus();
			message = toJoin.getGameBoardState(id);
		}
		
		return new ResponseInfo(status, message);
	}
	
	public static boolean checkSession(int id) {
		Session sessionWithId = getSessionWithId(id);
		if(sessionWithId != null) {
			return true;
		}
		return false;
	}
	
	/**
	 * Handle Chain Submission
	 */
	public static ResponseInfo submitChain(int id, Chain chain) {
		System.out.println("Chain Submitted");
		Session activeSession = GameManager.getSessionWithId(id);
		if(activeSession == null) {
			return new ResponseInfo(400, "Cannot find session containing id " + id);
		}
		
		boolean firstChain = (activeSession.getSessionState() != SessionState.Challenge);
		if(firstChain) {
			activeSession.clearChallengeChains();
			activeSession.setSessionState(SessionState.Challenge);
		}
		
		
		int chainAccessId = activeSession.addChallenge(id, chain, firstChain);
		activeSession.clearUpToDateStatus();
		
		// Return the id used to access the chain again (which is actually just the team id).
		return new ResponseInfo(200, "" + chainAccessId);
	}
	
	public static ResponseInfo submitWinningChain(int id, int chainAccessor, Chain chain) {
		System.out.println("Winning Chain Submitted. Quality: " + chain.quality);
		
		Session activeSession = GameManager.getSessionWithId(id);
		if(activeSession == null) {
			return new ResponseInfo(400, "Cannot find session containing id " + id);
		}
		
		if(!activeSession.getSessionState().equals(SessionState.Challenge)) {
			return new ResponseInfo(200, "State Mismatch. Expected Challenge, Found " +
					activeSession.getSessionState().name() +
					". No modification occured.");
		}
		
		DatabaseManager.addChainToDatabase(chain);
		activeSession.selectChallengeWinner(chainAccessor, chain);
		activeSession.advanceTurn();
		activeSession.setSessionState(SessionState.PlayerTurn);
		activeSession.clearUpToDateStatus();
		
		return new ResponseInfo(200, "Entering Challenge State");
	}
	
	/**
	 * Handle a Team's Pass on Challenge
	 */
	public static ResponseInfo passOnChallenge(int id) {
		Session activeSession = GameManager.getSessionWithId(id);
		if(activeSession == null) {
			return new ResponseInfo(400, "Cannot find session containing id " + id);
		}
		
		int teamId = activeSession.schoolClass.findTeamIdWithStudentId(id);
		activeSession.registerTeamChallengeResponse(teamId);
		System.out.println("Team " + teamId + " has passed on the challenge.");
		return new ResponseInfo(200, "Pass Registered.");
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
	
	public static ResponseInfo launchNewAssignment(int teacherId, String assignmentXML) {
		String assignmentName = "";
		String assignmentInfo = "";
		String deckType = "";
		int prevAssignment = -1;
		Element teamsElement;
		
		ArrayList<Integer> classIdList = DatabaseManager.loadTeacherClassIds(teacherId);
		int classId = -1;
		if(classIdList == null || classIdList.size() <= 0) {
			return new ResponseInfo(400, "No Class Found for the Given Teacher ID: " + teacherId);
		} else {
			classId = classIdList.get(0);
		}
		
		int statusNum = 200;
		String statusMsg = "Success";
		
		try {
			DocumentBuilderFactory docBuildFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = docBuildFactory.newDocumentBuilder();
			InputSource inputSource = new InputSource();
			inputSource.setCharacterStream(new StringReader(assignmentXML));
			Document doc;
			doc = builder.parse(inputSource);
			
			NodeList allAssignments = doc.getElementsByTagName("assignment");
			if(allAssignments.getLength() <= 0) {
				System.out.println("Error: Parsing Assignment Data with no <assignment> tag.");
				return null;
			}
			
			Element assignmentData = (Element) allAssignments.item(0);
			assignmentName = XMLHelper.getTextValue(assignmentData, "assignment_name");
			assignmentInfo = XMLHelper.getTextValue(assignmentData, "assignment_info");
			deckType = XMLHelper.getTextValue(assignmentData, "assignment_deck_type");
			prevAssignment = DatabaseManager.getCurrentAssignmentIDForClass(classId);
			teamsElement = (Element) assignmentData.getElementsByTagName("teams").item(0);

			int assignmentId = DatabaseManager.addNewAssignment(assignmentName, assignmentInfo, deckType, prevAssignment);
			
			if(assignmentId == -1) {
				statusNum = 400;
				statusMsg = "Error Adding Assignment to Database";
			} else {
				DatabaseManager.setClassAssignment(classId, assignmentId);
				GameManager.createNewTeamsFromXML(classId, assignmentId, teamsElement);
			}
			
		} catch (SAXException e) {
			System.out.println("SAXException Parsing Card XML: " + e.getMessage());
			e.printStackTrace();
			return new ResponseInfo(400, "SAXException Parsing Card XML: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("IOException Parsing Card XML: " + e.getMessage());
			e.printStackTrace();
			return new ResponseInfo(400, "IOException Parsing Card XML: " + e.getMessage());
		} catch (ParserConfigurationException e) {
			System.out.println("ParserConfigurationException Parsing Card XML: " + e.getMessage());
			e.printStackTrace();
			return new ResponseInfo(400, "ParserConfigurationException Parsing Card XML: " + e.getMessage());
		}

		return new ResponseInfo(statusNum, statusMsg);
	}
	
	/**
	 * Creates the new teams from XML
	 */
	private static void createNewTeamsFromXML(int classId, int assignmentId, Element allTeamsData) {
		/*
		 * <team>
		 * 		<team_name></team_name>
		 * 		<team_students>
		 * 			<student_name></student_name>
		 * 			...
		 * 		</team_students>
		 * </team>
		 * <team>
		 * 		...
		 * </team>
		 * ...
		 */
		
		NodeList teamDataList = allTeamsData.getElementsByTagName("team");
		for(int teamDataCounter=0; teamDataCounter<teamDataList.getLength(); teamDataCounter++) {
			// Team Info
			Element teamData = (Element) teamDataList.item(teamDataCounter);
			String teamName = XMLHelper.getTextValue(teamData, "team_name");
			
			// Students
			ArrayList<String> students = new ArrayList<String>();
			Element allStudentsData = (Element) teamData.getElementsByTagName("team_students").item(0);
			NodeList studentNameList = allStudentsData.getElementsByTagName("student_name");
			for(int studentNameCounter = 0; studentNameCounter < studentNameList.getLength(); studentNameCounter++) {
				String studentName = studentNameList.item(studentNameCounter).getFirstChild().getNodeValue();
				students.add(studentName);
			}
			
			DatabaseManager.addTeam(teamName, students, classId, assignmentId);
		}
		
	}

	/**
	 * Returns a ResponseInfo with student list xml for the class with the given teacher id.
	 */
	public static ResponseInfo getStudentList(int teacherId) {
		ArrayList<String> studentNames = DatabaseManager.getClassStudentNames(teacherId);
		String xml = "<student_list>";
		for(String name : studentNames) {
			xml += "<student_name>" + name + "</student_name>";
		}
		xml += "</student_list>";
		System.out.println("Returning Student List: " + xml);
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
