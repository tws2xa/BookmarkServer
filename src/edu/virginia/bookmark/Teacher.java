package edu.virginia.bookmark;

import java.util.ArrayList;

public class Teacher extends Person {
	private ArrayList<SchoolClass> classes;
	
	public Teacher(int id, String name, ArrayList<SchoolClass> classes) {
		super(id, name);
		this.classes = classes;
	}

	/**
	 * @return the classes
	 */
	public ArrayList<SchoolClass> getClasses() {
		return classes;
	}
}
