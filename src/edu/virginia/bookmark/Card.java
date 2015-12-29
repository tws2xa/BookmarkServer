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
		this.type = (CardType)cardProperties.get("type");
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

	/*
	public static Card createCardFromXMLElement(Element cardData) {
		int newId = XMLHelper.getIntValue(cardData, "id");
		String newTypeStr = XMLHelper.getTextValue(cardData, "type");
		String newBodyText = XMLHelper.getTextValue(cardData, "body_text");
		int newPageStart = XMLHelper.getIntValue(cardData, "page_start");
		int newPageEnd = XMLHelper.getIntValue(cardData, "page_end");
		
		CardType newType = getCardTypeFromString(newTypeStr);
		
		return new Card(newId, newType, newBodyText, newPageStart, newPageEnd);
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
			return createCardFromXMLElement(cardData);
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
	*/
	
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
