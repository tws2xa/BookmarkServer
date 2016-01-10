package edu.virginia.bookmark;

import javax.xml.parsers.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.w3c.dom.*;
import java.io.*;
import java.util.HashMap;

public class Card {
	
	public enum CardType {
		Imagery,
		Diction,
		Tone,
		Theme,
		Argument,
		Plot_Point,
		Other
	}
	
	public int id;
	private CardType type;
	private String bodyText;
	private int pageStart;
	private int pageEnd;
	
	public Card(int id) {
		this.id = id;
		
		HashMap<String, Object> cardProperties = DatabaseManager.getCardProperties(id);
		this.type = getCardTypeFromString((String)cardProperties.get("type"));
		this.bodyText = (String)cardProperties.get("bodyText");
		this.pageStart = (int)cardProperties.get("pageStart");
		this.pageEnd = (int)cardProperties.get("pageEnd");
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
		} else if(str.equals("plot point") || str.equals("plot_point")) {
			return CardType.Plot_Point;
		} else {
			try {
				return CardType.valueOf(str);
			} catch(Exception e) {}
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
	
	
	/**
	 * Create a list of card properties given a card's xml.
	 * Does not need an ID.
	 * 
	 * Cannot simply create a Card Object because we don't have an ID
	 * And Card objects are only meant to be used during active sessions.
	 */
	public static HashMap<String, Object> getCardPropertiesFromXMLElement(Element cardData) {
		String xmlTypeStr = XMLHelper.getTextValue(cardData, "type");
		String xmlBodyText = XMLHelper.getTextValue(cardData, "body_text");
		int xmlPageStart = XMLHelper.getIntValue(cardData, "page_start");
		int xmlPageEnd = XMLHelper.getIntValue(cardData, "page_end");
		
		HashMap<String, Object> properties = new HashMap<String, Object>();

		properties.put("type", xmlTypeStr);
		properties.put("bodyText", xmlBodyText);
		properties.put("pageStart", xmlPageStart);
		properties.put("pageEnd", xmlPageEnd);
		
		return properties;
	}
	
	public static HashMap<String, Object> getCardPropertiesFromXML(String xmlData) {
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
			return getCardPropertiesFromXMLElement(cardData);
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
}
