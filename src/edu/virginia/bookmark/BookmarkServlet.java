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
    private final String IS_TEACHER = "is-teacher";
    private final String CHECK_BOARD_UPDATE = "check-board-update";
    private final String GET_BOARD_STATE = "get-board-state";
            
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
    		System.out.print("\"" + param + ": " + params.get(param) + "\"");
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
    		int studentId = Integer.parseInt(params.get("student_id")[0]);
    		Student student = DatabaseManager.findStudentWithId(studentId);
    		if(student == null) {
    			return new ResponseInfo(400, "Invalid Student ID: " + studentId);
    		} else {
    			return new ResponseInfo(200, student.getStudentXMLInfoString());
    		}
    	
    	case(GET_PERSON_INFO):
    		int personId = Integer.parseInt(params.get("id")[0]);
    		Person person = DatabaseManager.findPersonWithId(personId);
    		if(person == null) {
    			return new ResponseInfo(400, "Invalid Person ID: " + personId);
    		} else {
    			return new ResponseInfo(200, person.getPersonXMLInfoString());
    		}
    			
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
    		
    	default:
    		return new ResponseInfo(500, "Unrecognized Action: " + action);
    		
    	}
    }
    	
	
}
