package edu.virginia.bookmark;

import java.awt.Point;
import java.util.HashMap;

public class Chain {
	HashMap<Card, Point> cards;
	HashMap<Integer, Integer> links;
	
	public Chain(HashMap<Card, Point> cards, HashMap<Integer, Integer> links) {
		this.cards = cards;
		this.links = links;
	}
	
}
