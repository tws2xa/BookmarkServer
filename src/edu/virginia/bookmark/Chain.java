package edu.virginia.bookmark;

import java.awt.Point;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import edu.virginia.bookmark.Card.CardType;

public class Chain {
	HashMap<Card, Point> cards;
	ArrayList<int[]> links;
	
	public Chain(HashMap<Card, Point> cards, ArrayList<int[]> links) {
		this.cards = cards;
		this.links = links;
	}

	/**
	 * Gets the chain's argument card
	 */
	public Card getArgumentCard() {
		for(Card card : cards.keySet()) {
			if(card.getType().equals(CardType.Argument)) {
				return card;
			}
		}
		return null;
	}
	
	@Override
	public String toString() {
		String ret = "Chain:\n";
		ret += "\tCards:\n";
		for(Card card : cards.keySet()) {
			Point pos = cards.get(card);
			ret += "\t\tCard ID:" + card.id + " Position: " + pos;
		}
		ret += "\tLinks:\n";
		for(int[] link : links) {
			ret += "\t\t" + link[0] + " <----> " + link[1];
		}
		return ret;
	}
	
	/**
	 * Create the xml string to represent this chain.
	 */
	public String generateChainXML() {
		String xmlStr = "<chain>";

		// Card Info (Card and position)
		xmlStr += "<cards>";
		for(Card card : cards.keySet()) {
			xmlStr += "<card_info>";
			Point pos = this.cards.get(card);
			
			xmlStr += card.generateCardXML(); // <card> ... </card>

			xmlStr += "<position>";
			xmlStr += "<x>" + pos.getX() + "</x>";
			xmlStr += "<y>" + pos.getY() + "</y>";
			xmlStr += "</position>";

			xmlStr += "</card_info>";
		}
		xmlStr += "</cards>";

		xmlStr += "<links>";
		for(int i=0; i<this.links.size(); i++) {
			xmlStr += "<link>";
			xmlStr += "<card1_id>" + this.links.get(i)[0] + "</card1_id>";
			xmlStr += "<card2_id>" + this.links.get(i)[1] + "</card2_id>";
	        xmlStr += "</link>";
	    }
	    xmlStr += "</links>";


		xmlStr += "</chain>";

		return xmlStr;
	}
	
	/**
	 * Create a new chain from an XML string
	 */
	public static Chain generateChainFromXML(String xmlStr) {
		try {
			DocumentBuilderFactory docBuildFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = docBuildFactory.newDocumentBuilder();
			InputSource inputSource = new InputSource();
			inputSource.setCharacterStream(new StringReader(xmlStr));
			Document doc;
			doc = builder.parse(inputSource);
			
			NodeList allChains = doc.getElementsByTagName("chain");
			if(allChains.getLength() <= 0) {
				System.out.println("Error: Parsing Chain Data with no <chain> tag.");
				return null;
			}
			Element chainData = (Element) allChains.item(0);
			
			HashMap<Card, Point> newCardsAndPos = new HashMap<Card, Point>();
			ArrayList<int[]> newLinks = new ArrayList<int[]>();
			
			// ------------- Getting Card and Position Info -------------
			
			Element cardSection = XMLHelper.getSingleNodeElement(chainData, "cards");
			// Loop through card_info tags
			NodeList cardInfoList = cardSection.getElementsByTagName("card_info");
			for(int i=0; i<cardInfoList.getLength(); i++) {
				Element cardInfo = (Element) cardInfoList.item(i);
				Element cardElement = XMLHelper.getSingleNodeElement(cardInfo, "card");
				int cardId = XMLHelper.getIntValue(cardElement, "id");
				Card card = GameManager.getCardWithId(cardId);
				Element positionElement = XMLHelper.getSingleNodeElement(cardInfo, "position");
				int xPos = XMLHelper.getIntValue(positionElement, "x");
				int yPos = XMLHelper.getIntValue(positionElement, "y");
				Point position = new Point(xPos, yPos);
				newCardsAndPos.put(card, position);
			}
			
			// ------------- Getting Link Info -------------
			
			Element linksSection = XMLHelper.getSingleNodeElement(chainData, "links");
			NodeList allLinks = linksSection.getElementsByTagName("link");
			for(int i=0; i<allLinks.getLength(); i++) {
				Element linkElement = (Element) allLinks.item(i);
				int[] link = new int[2];
				link[0] = XMLHelper.getIntValue(linkElement, "card1_id");
				link[1] = XMLHelper.getIntValue(linkElement, "card2_id");
				newLinks.add(link);
			}
						
			return new Chain(newCardsAndPos, newLinks);
		} catch (SAXException e) {
			System.out.println("SAXException Parsing Chain XML: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("IOException Parsing Chain XML: " + e.getMessage());
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			System.out.println("ParserConfigurationException Parsing Chain XML: " + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
	
}
