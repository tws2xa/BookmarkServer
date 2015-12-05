package edu.virginia.bookmark;

import javax.xml.parsers.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.w3c.dom.*;
import java.io.*;

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

	public String generateCardXML() {
		String xmlStr = "<card>";
		xmlStr += "<id>" + this.id + "</id>";
		xmlStr += "<type>" + this.type + "</type>";
		xmlStr += "<body_text>" + this.bodyText + "</body_text>";
		xmlStr += "<page_start>" + this.pageStart + "</page_start>";
		xmlStr += "<page_end>" + this.pageEnd + "</page_end>";
		xmlStr += "</card>";
		return xmlStr;
	}


	public static Card createCardFromXML(String xmlData) {
		try {
			DocumentBuilderFactory docBuildFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = docBuildFactory.newDocumentBuilder();
			InputSource inputSource = new InputSource();
			inputSource.setCharacterStream(new StringReader(xmlData));
			Document doc;
			doc = builder.parse(inputSource);
			
			NodeList allCards = doc.getElementsByTagName("card");
			if(allCards.getLength() <= 0) {
				System.out.println("Error: Parsing Card Data with no <card> tag.");
				return null;
			}
			
			// Get Card ID
			//
			Element cardData = (Element) allCards.item(0);
			int newId = getIntValue(cardData, "id");
			String newTypeStr = getTextValue(cardData, "type");
			String newBodyText = getTextValue(cardData, "body_text");
			int newPageStart = getIntValue(cardData, "page_start");
			int newPageEnd = getIntValue(cardData, "page_end");
			
			CardType newType = getCardTypeFromString(newTypeStr);
			
			return new Card(newId, newType, newBodyText, newPageStart, newPageEnd);
		} catch (SAXException e) {
			System.out.println("SAXException Parsing Card XML: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("IOException Parsing Card XML: " + e.getMessage());
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			System.out.println("ParserConfigurationException Parsing Card XML: " + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Get a text value from an xml element
	 */
	private static String getTextValue(Element element, String tagName) {
		String textVal = null;
		
		NodeList nodeList = element.getElementsByTagName(tagName);
		if(nodeList != null && nodeList.getLength() > 0) {
			Element el = (Element)nodeList.item(0);
			textVal = el.getFirstChild().getNodeValue();
		}

		return textVal;
	}

	/**
	 * Get an int value from an xml element
	 */
	private static int getIntValue(Element ele, String tagName) {
		//in production application you would catch the exception
		return Integer.parseInt(getTextValue(ele,tagName));
	}
	
	public static CardType getCardTypeFromString(String str) {
		str = str.toLowerCase();
		if(str.equals("argument")) {
			return CardType.Argument;
		} else if(str.equals("diction")) {
			return CardType.Diction;
		} else if(str.equals("imagery")) {
			return CardType.Imagery;
		} else if(str.equals("other")) {
			return CardType.Other;
		} else if(str.equals("theme")) {
			return CardType.Theme;
		} else if(str.equals("tone")) {
			return CardType.Tone;
		} else {
			System.out.println("Searching for invalid card type: " + str);
			return null;
		}
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
