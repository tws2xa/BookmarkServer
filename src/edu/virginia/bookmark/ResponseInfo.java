package edu.virginia.bookmark;

public class ResponseInfo {
	public int status;
	public String message;
	
	public ResponseInfo(int status, String message) {
		this.status = status;
		this.message = message;
	}
	
	public boolean hasError() {
		return status != 200;
	}
}
