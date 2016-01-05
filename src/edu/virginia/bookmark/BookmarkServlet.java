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
    			return new ResponseInfo(400, "No session with id: " + givenGetBoardStateId);
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
    		int getDeckStudentId = Integer.parseInt("id");
    		int getDeckClassId = Integer.parseInt("classId");
    		String studentDeckXML = getStudentDeckXML(getDeckStudentId, getDeckClassId);
    		return new ResponseInfo(200, studentDeckXML);
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
    	
	
}
