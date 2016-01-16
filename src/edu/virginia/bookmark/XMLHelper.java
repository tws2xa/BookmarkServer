package edu.virginia.bookmark;

import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class XMLHelper {
	/**
	 * Get a text value from an xml element
	 */
	protected static String getTextValue(Element element, String tagName) {
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
	protected static int getIntValue(Element ele, String tagName) {
		//in production application you would catch the exception
		return Integer.parseInt(getTextValue(ele,tagName));
	}
	
	/**
	 * Gets an xml element from the given parent and node
	 */
	protected static Element getSingleNodeElement(Element parent, String tagName) {
		NodeList allItems = parent.getElementsByTagName(tagName);
		if(allItems.getLength() <= 0) {
			System.out.println("Error: Parsing Data to find <" + tagName + "> tag, but none found.");
			return null;
		}
		Element element = (Element) allItems.item(0);
		return element;
	}
}
