package edu.virginia.bookmark;

public class Person {
	public int id;
	private String name;
	
	public Person(int id, String name) { 
		this.id = id;
		this.name = name;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
}
