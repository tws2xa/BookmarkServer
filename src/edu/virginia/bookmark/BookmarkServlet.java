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

    private final String BEGIN_SESSION = "begin-session";
    private final String SHOW_ERROR = "show-error";
        
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    	try {
	    	// Get the URL Info
	    	//
	        String action = request.getPathInfo();
	        System.out.println("Recieved Request to : \"" + action + "\"");
	        
	        // Allow localhost communication
	        //
	        response.addHeader("Access-Control-Allow-Origin", "http://localhost");
	        
	        // Must designate an action
	        //
	        if(action.isEmpty()) {
	        	response.sendError(500, "Badly Formed URL. Must designate an action.");
	        	return;
	        }
	    	
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
     		response.sendError(500, "Unknown Error: " + e.getMessage());
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
    	switch(action) {
    	case (SHOW_ERROR):
    		return new ResponseInfo(501, "Intentional Error!");
    	
    	case(BEGIN_SESSION):
    		int teacherId = Integer.parseInt(params.get("teacher_id")[0]);
			int classId = Integer.parseInt(params.get("class_id")[0]);
        	return GameManager.beginSession(teacherId, classId);
    	
    	default:
    		return new ResponseInfo(500, "Unrecognized Action: " + action);
    		
    	}
    }
    	
	
}
