package edu.virginia.bookmark;


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
    private final String LOGIN = "login";
    private final String SHOW_ERROR = "show-error";
    private final String GET_STUDENT_INFO = "get-student-info";
    private final String GET_PERSON_INFO = "get-person-info";
    private final String GET_PERSON_NAME = "get-person-name";
    private final String IS_TEACHER = "is-teacher";
    private final String CHECK_BOARD_UPDATE = "check-board-update";
    private final String GET_BOARD_STATE = "get-board-state";
    private final String SUBMIT_CHAIN = "submit-chain";
    private final String GET_STUDENT_DECK = "get-student-deck";
    private final String GET_TEAM_DECK = "get-team-deck";
    private final String STUDENT_ADD_CARD = "student-add-card";
    private final String GET_CLASS_ARGUMENT_CARD_DECK = "get-class-argument-card-deck";
    private final String GET_CHAIN_FOR_ARGUMENT = "get-chain-for-argument";
    private final String PASS_ON_CHALLENGE = "pass-on-challenge"; // Parameter: "id" (the student id).
            
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
        	
        	if(addCard_EditId == -1) {
	        	DatabaseManager.addCardForStudent(
	        			addCard_StudentId,
	        			addCard_ClassId,
	        			addCard_Type,
	        			addCard__Body,
	        			addCard_PageStart,
	        			addCard_PageEnd
	        			);
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
        	}
        	
            return new ResponseInfo(200, "Card Successfully Added");
        
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
        	
        case(PASS_ON_CHALLENGE):
        	int passOnChallenge_StudentId = Integer.parseInt(params.get("id")[0]);
        	return GameManager.passOnChallenge(passOnChallenge_StudentId);
        	
    	default:
    		return new ResponseInfo(500, "Unrecognized Action: " + action);
    		
    	}
    }
    
    /**
     * Generates xml representing a student's deck.
     * @param studentId The id of the student whose deck we are looking up
     */
    private String getStudentDeckXML(int studentId, int classId) {
    	ArrayList<Integer> cardIds = DatabaseManager.loadStudentDeckIds(studentId, classId);
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
        
	
}
