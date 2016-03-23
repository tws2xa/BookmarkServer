package edu.virginia.bookmark;


import java.awt.Point;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.util.RequestUtil;

public class BookmarkServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private final String DATABASE_TEST = "database-test";
	private final String BEGIN_SESSION = "begin-session";
	private final String JOIN_SESSION = "join-session";
	private final String CHECK_SESSION = "check-session";
	private final String LOGIN = "login";
	private final String SHOW_ERROR = "show-error";
	private final String GET_STUDENT_INFO = "get-student-info";
	private final String GET_PERSON_INFO = "get-person-info";
	private final String GET_PERSON_NAME = "get-person-name";
	private final String IS_TEACHER = "is-teacher";
	private final String CHECK_BOARD_UPDATE = "check-board-update";
	private final String GET_BOARD_STATE = "get-board-state";
	private final String SUBMIT_CHAIN = "submit-chain"; // Parameters: "id" (Chain id), "chain_xml" (xml of the chain to submit).
	private final String GET_STUDENT_DECK = "get-student-deck";
	private final String GET_TEAM_DECK = "get-team-deck";
	private final String STUDENT_ADD_CARD = "student-add-card";
	private final String GET_CLASS_ARGUMENT_CARD_DECK = "get-class-argument-card-deck";
	private final String GET_CHAIN_FOR_ARGUMENT = "get-chain-for-argument";
	private final String PASS_ON_CHALLENGE = "pass-on-challenge"; // Parameter: "id" (the student id).
	private final String GET_BOARD_CARD = "get-board-card";
	private final String SUBMIT_WINNING_CHAIN = "submit-winning-chain"; // Parameters: "id" (student id), "chain_accessor" (id to access the chain), "chain_xml" (xml of chain to submit).
	private final String GET_TEAM_POSITION = "get-team-position";
	private final String UPDATE_TEAM_POSITION = "update-team-position";
	private final String GET_CLASS_STUDENTS = "get-class-students"; // Parameters: "id" (teacher id)
	private final String LAUNCH_NEW_ASSIGNMENT = "launch-new-assignment"; // Parameters: "id" (teacher id), "assignment_info" (assignment xml)
	private final String GET_CARD_TYPES = "get-card-types"; // Parameters: "id" (person id).    
	private final String GET_TEAM_NAME = "get-team-name";
	private final String GET_FULL_CLASS_INFO = "get-full-class-info"; // Parameters: "id" (person id).

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		try {
			// Get the URL Info
			//
			String action = request.getPathInfo();

			// Allow localhost communication
			//
			response.addHeader("Access-Control-Allow-Origin", "http://localhost");

			// Allow gdrg.cs.viginia.edu communication
			//
			// response.addHeader("Access-Control-Allow-Origin", "http://gdrg.cs.virginia.edu");

			// Must designate an action
			//
			if(action.isEmpty()) {
				response.sendError(500, "Badly Formed URL. Must designate an action.");
				return;
			} else {
				// Trim trailing or leading '/' characters.
				if(action.charAt(0) == '/') {
					action = action.substring(1);
				}
				if(action.charAt(action.length() - 1) == '/') {
					action = action.substring(0, action.length() - 1);
				}
			}
			if(action.isEmpty()) {
				response.sendError(500, "Badly Formed URL. Must designate an action.");
				return;
			}

			System.out.println("Received Request to : \"" + action + "\"");

			// Handle the action
			//
			ResponseInfo actionResponse = handleRequest(action, request.getParameterMap());

			// Write the response back to the client
			//
			if(actionResponse.hasError()) {
				response.sendError(actionResponse.status, actionResponse.message);
				return;
			} else {
				PrintWriter writer = response.getWriter();
				writer.println(actionResponse.message);
				writer.close();
			}
		} catch (Exception e) {
			System.out.println("Hit Unexpected Error:");
			System.out.println(e);
			System.out.println(e.getMessage());
			e.printStackTrace(System.out);
			response.sendError(500, "Unknown Error: " + e + " " + e.getMessage());
		}
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
	{
		PrintWriter writer = response.getWriter();
		writer.println("Welcome to Bookmark!");
		writer.close();
	}

	/**
	 * Handles the given request
	 * @param action The action to perform
	 * @param params Parameters for the action
	 * @return The response to send to the client
	 */
	public ResponseInfo handleRequest(String action, Map<String, String[]> params) {
		System.out.print("\t Parameters: ");
		for(String param : params.keySet()) {
			System.out.print("\"" + param + ": " + params.get(param)[0] + "\" ");
		}
		
		System.out.println("");

		switch(action) {
		case (SHOW_ERROR):
			return new ResponseInfo(501, "Intentional Error!");

		case(BEGIN_SESSION):
			System.out.println("Starting Session");
			for(String str : params.keySet()) {
				System.out.println(str);
			}
			int teacherId = Integer.parseInt(params.get("teacher_id")[0]);
			int classId = Integer.parseInt(params.get("class_id")[0]);
			if(classId == -1) {
				ArrayList<Integer> classes = DatabaseManager.loadTeacherClassIds(teacherId);
				if(classes.size() > 0) {
					classId = classes.get(0);
				}
	
				System.out.println("CAUTION: ASSUMING ONE CLASS PER TEACHER. DOES NOT ALLOW TEACHER TO CHOOSE CLASS!");
			}
			return GameManager.beginSession(teacherId, classId);

		case(JOIN_SESSION):
			System.out.println("Joining Session");
			int joinSessionId = Integer.parseInt(params.get("id")[0]);
			return GameManager.joinSession(joinSessionId);
			case(CHECK_SESSION):
				int checkSession_Id = Integer.parseInt(params.get("id")[0]);
			boolean hasSession = GameManager.checkSession(checkSession_Id);
			return new ResponseInfo(200, hasSession + "");
			case(LOGIN):
				String username = params.get("username")[0];
			String password = params.get("password")[0];
	
			int loginId = DatabaseManager.doLogin(username, password);
			if(loginId == -1) {
				return new ResponseInfo(400, "Invalid Login");
			} else {
				return new ResponseInfo(200, loginId + "");
			}

		case(GET_STUDENT_INFO):
			return new ResponseInfo(400, "GET STUDENT INFO NOT IMPLEMENTED");

		case(GET_PERSON_INFO):
			return new ResponseInfo(400, "GET PERSON INFO NOT IMPLEMENTED");

		case(GET_PERSON_NAME):
			int personNameId = Integer.parseInt(params.get("id")[0]);
			String name = DatabaseManager.getPersonName(personNameId);
			return new ResponseInfo(200, name);

		case(IS_TEACHER) :
			int givenIsTeacherId = Integer.parseInt(params.get("id")[0]);
			boolean isTeacher = DatabaseManager.verifyTeacher(givenIsTeacherId);
			return new ResponseInfo(200, isTeacher + "");

		case (CHECK_BOARD_UPDATE):
			int givenCheckBoardId = Integer.parseInt(params.get("id")[0]);
			boolean needUpdate = GameManager.checkNeedUpdate(givenCheckBoardId);
			return new ResponseInfo(200, needUpdate + "");

		case(GET_BOARD_STATE):
			int givenGetBoardStateId = Integer.parseInt(params.get("id")[0]);
			String boardStateXML = GameManager.getBoardStateXML(givenGetBoardStateId);
			if(boardStateXML == null) {
				return new ResponseInfo(400, "No session containing id: " + givenGetBoardStateId);
			} else {
				return new ResponseInfo(200, boardStateXML);
			}

		case(DATABASE_TEST) :
			DatabaseManager.InitializeDB();
			return new ResponseInfo(200, "Tried to do a thing. Check TomCat Output.");

		case(SUBMIT_CHAIN):
			int submitChainId = Integer.parseInt(params.get("id")[0]);
			String chainXML = params.get("chain_xml")[0];

			Chain chain = Chain.generateChainFromXML(chainXML);
			return GameManager.submitChain(submitChainId, chain);

		case(GET_STUDENT_DECK):
			int getDeckStudentId = Integer.parseInt(params.get("id")[0]);
			int getDeckClassId = Integer.parseInt(params.get("classId")[0]);
			if(getDeckClassId == -1) {
				System.out.println("USING DEFAULT CLASS FOR STUDENT IN GET STUDENT DECK.");
				getDeckClassId = DatabaseManager.getClassContainingStudent(getDeckStudentId);
				if(getDeckClassId == -1) {
					return new ResponseInfo(400, "Could not find class containing student with id #" + getDeckStudentId);
				}
			}
			String studentDeckXML = getStudentDeckXML(getDeckStudentId, getDeckClassId);
			return new ResponseInfo(200, studentDeckXML);

		case(GET_TEAM_DECK):
			int getTeamDeckStudentId = Integer.parseInt(params.get("id")[0]);
			int getTDeckClassId = GameManager.getActiveClassId(getTeamDeckStudentId);
			if(getTDeckClassId == -1) {
				return new ResponseInfo(400, "Could not find class containing student with id #" + getTeamDeckStudentId);
			}
			int getTeamDeckTeamId = GameManager.getActiveTeamWithStudentId(getTeamDeckStudentId);
			if(getTeamDeckTeamId == -1) {
				return new ResponseInfo(400, "Could not find team containing student with id #" + getTeamDeckStudentId);
			}
			String teamDeckXML = getTeamDeckXML(getTeamDeckTeamId, getTDeckClassId);
			return new ResponseInfo(200, teamDeckXML);

		case(STUDENT_ADD_CARD):
			int addCard_StudentId = Integer.parseInt(params.get("id")[0]);
			int addCard_ClassId = Integer.parseInt(params.get("classId")[0]);
			String addCard_Type = params.get("cardType")[0];
			String addCard__Body = params.get("bodyText")[0];
			int addCard_PageStart = Integer.parseInt(params.get("pageStart")[0]);
			int addCard_PageEnd = Integer.parseInt(params.get("pageEnd")[0]);
			int addCard_EditId = Integer.parseInt(params.get("editId")[0]);
	
			if(addCard_ClassId == -1) {
				System.out.println("USING DEFAULT CLASS FOR ADD STUDENT CARD.");
				addCard_ClassId = DatabaseManager.getClassContainingStudent(addCard_StudentId);
				if(addCard_ClassId == -1) {
					return new ResponseInfo(400, "Could not find class containing student with id #" + addCard_StudentId);
				}
			}
	
			int addCard_AssignmentId = DatabaseManager.getCurrentAssignmentIDForClass(addCard_ClassId);
	
			if(addCard_EditId == -1) {
				DatabaseManager.addCardForStudent(
						addCard_StudentId,
						addCard_ClassId,
						addCard_AssignmentId,
						addCard_Type,
						addCard__Body,
						addCard_PageStart,
						addCard_PageEnd
						);
				return new ResponseInfo(200, "Card Added");
			}
			else {
				DatabaseManager.updateCardForStudent(
						addCard_EditId,
						addCard_StudentId,
						addCard_ClassId,
						addCard_Type,
						addCard__Body,
						addCard_PageStart,
						addCard_PageEnd
						);
				return new ResponseInfo(200, "Card Updated");
			}

		case(GET_TEAM_POSITION):
			int getTeamPosition_studentId = Integer.parseInt(params.get("id")[0]);
	
			String ret = getTeamPos(getTeamPosition_studentId);
	
			return new ResponseInfo(200, ret);


		case(GET_CLASS_ARGUMENT_CARD_DECK):
			int getClassArgDeck_studentId = Integer.parseInt(params.get("id")[0]);
			return GameManager.getClassArgumentCardXML(getClassArgDeck_studentId);

		case(GET_CHAIN_FOR_ARGUMENT):
			int getChainForArg_ArgCardID = Integer.parseInt(params.get("argument_card_id")[0]);
			Chain getChainForArg_Chain = DatabaseManager.getChainForArgumentCard(getChainForArg_ArgCardID);
	
			String retStr = "";
			if(getChainForArg_Chain == null) {
				retStr = "null";
			} else {
				retStr = getChainForArg_Chain.generateChainXML();
			}
	
			System.out.println("\nReturning: " + retStr + "\n");
			return new ResponseInfo(200, retStr);

		case(GET_BOARD_CARD):
			int getBoardCard_studentId = Integer.parseInt(params.get("id")[0]);
			int getBoardCard_teamId = GameManager.getActiveTeamWithStudentId(getBoardCard_studentId);
	
			String getBoardCard_ret = getBoardCardXML(getBoardCard_teamId, getBoardCard_studentId);        	
			return new ResponseInfo(200, getBoardCard_ret);

		case(PASS_ON_CHALLENGE):
			int passOnChallenge_StudentId = Integer.parseInt(params.get("id")[0]);
			return GameManager.passOnChallenge(passOnChallenge_StudentId);

		case(SUBMIT_WINNING_CHAIN):
			int submitWinningChain_StudentId = Integer.parseInt(params.get("id")[0]);
			int submitWinningChain_ChainAccessor = Integer.parseInt(params.get("chain_accessor")[0]);
			String submitWinningChain_ChainXML = params.get("chain_xml")[0];
			Chain submit_WinningChain_Chain = Chain.generateChainFromXML(submitWinningChain_ChainXML);
	
			return GameManager.submitWinningChain(submitWinningChain_StudentId, submitWinningChain_ChainAccessor, submit_WinningChain_Chain);

		case(UPDATE_TEAM_POSITION):
			int updateTeamPos_studentId = Integer.parseInt(params.get("id")[0]);
			int posX = Integer.parseInt(params.get("posX")[0]);
			int posY = Integer.parseInt(params.get("posY")[0]);
	
	
			if(setTeamPos(updateTeamPos_studentId, posX, posY)) {
				return new ResponseInfo(200, "Success");
			}
			return new ResponseInfo(500, "Failure Updating Team Pos: " + Integer.toString(updateTeamPos_studentId));
		
		case(GET_CLASS_STUDENTS):
			int getClassStudents_id = Integer.parseInt(params.get("id")[0]);
			if (!DatabaseManager.verifyTeacher(getClassStudents_id)) {
				return new ResponseInfo(400, "No Class Found With Teacher Id: " + getClassStudents_id);
			}
			return GameManager.getStudentList(getClassStudents_id);
		
		case(LAUNCH_NEW_ASSIGNMENT):
			int launchNewAssignment_TeacherId = Integer.parseInt(params.get("id")[0]);
			String launchNewAssignment_AssignmentXML = params.get("assignment_info")[0].trim();
			return GameManager.launchNewAssignment(launchNewAssignment_TeacherId, launchNewAssignment_AssignmentXML);

		case(GET_CARD_TYPES):
			int getCardTypes_Id = Integer.parseInt(params.get("id")[0]);
			String typesXML = getCardTypeListXML(getCardTypes_Id);
			if(typesXML == null) {
				return new ResponseInfo(400, "Error Obtaining Card Types");
			}
			return new ResponseInfo(200, typesXML);

		case(GET_TEAM_NAME):
			int getStudent_Id = Integer.parseInt(params.get("id")[0]);
			int team_id = DatabaseManager.getTeamContainingStudent(getStudent_Id);
			String team_name = DatabaseManager.getTeamName(team_id);
			if(team_name == null) {
				return new ResponseInfo(400, "Error Obtaining Team Name");
			}

			return new ResponseInfo(200, team_name);

		case(GET_FULL_CLASS_INFO):
			int getFullClassInfo_Id = Integer.parseInt(params.get("id")[0]);
			int getFullClassInfo_ClassId = DatabaseManager.getClassIdForPerson(getFullClassInfo_Id, -1);
			if(getFullClassInfo_ClassId < 0) {
				return new ResponseInfo(200, "No Class Found for Person with ID: " + getFullClassInfo_Id);
			}
			
			String getFullClassInfo_XML = DatabaseManager.getFullClassInfo(getFullClassInfo_ClassId);
			return new ResponseInfo(200, getFullClassInfo_XML);
			
		default:
			return new ResponseInfo(500, "Unrecognized Action: " + action);

		}
	}

	/**
	 * Gets an xml representation of all card types a person may use during their
	 * current assignment.
	 */
	private String getCardTypeListXML(int personId) {
		ArrayList<String> types = DatabaseManager.getCurrentCardTypesForPerson(personId);

		if(types == null) {
			return null;
		}

		String xml = "<card_types>";
		for(String type : types) {
			xml+="<type>"+ type + "</type>";
		}
		xml += "</card_types>";
		return xml;
	}

	/**
	 * Generates xml representing a student's deck.
	 * @param studentId The id of the student whose deck we are looking up
	 */
	private String getStudentDeckXML(int studentId, int classId) {
		int currentAssignmentId = DatabaseManager.getCurrentAssignmentIDForClass(classId);
		ArrayList<Integer> cardIds = DatabaseManager.loadStudentDeckIds(studentId, classId, currentAssignmentId);
		String xml = "<deck>";
		for(int cardId : cardIds) {
			Card card = new Card(cardId);
			xml += card.generateCardXML();
		}
		xml += "</deck>";
		return xml;
	}

	/* Generates xml for a team's Deck.
	 */
	private String getTeamDeckXML(int teamId, int classId) {
		//get students, take each student's Deck XML, cocatenate on string
		ArrayList<Integer> teamIds = DatabaseManager.loadTeamStudentIds(teamId/*, classId*/);

		String xml = "<team_deck>";
		for(int studentId : teamIds) {
			String deck = getStudentDeckXML(studentId, classId);
			xml += deck;
		}

		xml += "</team_deck>";
		return xml;
	}

	private String getTeamPos(int studentId) {

		Session session  = GameManager.getSessionWithId(studentId);
		SchoolClass sClass = session.schoolClass;

		int team = session.schoolClass.findTeamIdWithStudentId(studentId);
		Point pos = new Point();
		for(Team t : sClass.getTeams()) {
			if(t.id == team) {
				pos = t.getPosition();
				break;
			}
		}

		String xml = "<team_pos>";
		xml += "<x>";
		xml += Double.toString(pos.getX());
		xml += "</x>";
		xml += "<y>";
		xml += Double.toString(pos.getY());
		xml += "</y>";
		xml += "/<team_pos>";

		return xml;
	}

	private boolean setTeamPos(int studentId, int x, int y) {
		Session session  = GameManager.getSessionWithId(studentId);

		int team = session.schoolClass.findTeamIdWithStudentId(studentId);
		if(!session.isActiveTeam(team)) {
			// Not this team's turn. Do nothing. Return.
			System.out.println("Team " + team + " is Trying to Change Positions On Team " + session.getActiveTeam().id + "'s Turn.");
			return true;
		} else {
			Point pos = new Point(x, y);
			session.getActiveTeam().setPosition(pos);
			session.advanceTurn();
			session.clearUpToDateStatus();
			return true;
		}
	}

	private String getBoardCardXML(int teamId, int studentId) {
		Session session  = GameManager.getSessionWithId(studentId);
		SchoolClass sClass = session.schoolClass;
		Board board = session.board;

		Team t = null;

		for(Team team : sClass.getTeams()) {
			if(team.id == teamId) {
				t = team;
				break;
			}
		}

		Card card = board.returnCardAtPos(t.getPosition());

		String xml = "";

		xml += card.generateCardXML();

		return xml;

	}

}
