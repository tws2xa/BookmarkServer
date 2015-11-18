package edu.virginia.bookmark;

public class Card {
	
	public enum CardType {
		Imagery,
		Diction,
		Tone,
		Theme,
		Argument,
		Other
	}
	
	public int id;
	private CardType type;
	private String bodyText;
	private int pageStart;
	private int pageEnd;
	
	public Card(int id, CardType type, String bodyText, int pageStart, int pageEnd) {
		this.id = id;
		this.type = type;
		this.bodyText = bodyText;
		this.pageStart = pageStart;
		this.pageEnd = pageEnd;
	}

	/**
	 * @return the type
	 */
	public CardType getType() {
		return type;
	}

	/**
	 * @return the bodyText
	 */
	public String getBodyText() {
		return bodyText;
	}

	/**
	 * @return the pageStart
	 */
	public int getPageStart() {
		return pageStart;
	}

	/**
	 * @return the pageEnd
	 */
	public int getPageEnd() {
		return pageEnd;
	}
}
