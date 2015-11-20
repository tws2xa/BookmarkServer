package edu.virginia.bookmark;

public abstract class Person {
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

	public String getPersonXMLInfoString() {
		String info = "<person>";
		info += "<name>" + this.getName() + "</name>";
		info += "<id>" + this.id + "</id>";
		info += "</person>";
		return info;
	};
}
