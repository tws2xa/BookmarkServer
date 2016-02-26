package edu.virginia.bookmark;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Board {
	final int BOARD_WIDTH = 4;
	final int BOARD_HEIGHT = 4;
	ArrayList<Integer> usedCardIds;
	ArrayList<Card> classCards;
	
	public Card[][] board;
	
	public Board(ArrayList<Team> teams, ArrayList<Card> boardDeck) {
		board = new Card[BOARD_WIDTH][BOARD_HEIGHT];
		usedCardIds = new ArrayList<Integer>();
		classCards = boardDeck;
		setupBoard();
	}
	
	public void setupBoard() {
		usedCardIds.clear();

			
		for(int i = 0; i < BOARD_WIDTH; i++) {
			for(int j = 0; j < BOARD_HEIGHT; j++) {
				Card newCard = getUnusedCard();			
				board[i][j] = newCard;
				usedCardIds.add(newCard.id);
			}
		}


	}
	
	/**
	 * Replace the card at the position with a new, unused card.
	 */
	public void replaceCardOnBoard(Point pos) {
		Card newCard = getUnusedCard();			
		board[pos.x][pos.y] = newCard;
		usedCardIds.add(newCard.id);
	}

	public Card returnCardAtPos(Point pos) {
		return board[pos.x][pos.y];
	}
	
	private Card getUnusedCard() {
		if(classCards.size() <= 0) {
			System.out.println("ERROR: Trying to get an unused card, but class has no cards.");
			return null;
		}
		
		if(usedCardIds.size() >= classCards.size()) {
			System.out.println("All Cards Used. Clearing.");
			usedCardIds.clear(); // All cards are used, reset used cards.
		}

		// Select a random number.
		Random rand = new Random();
		int index = rand.nextInt(classCards.size()-1);
		
		// Keep trying for random number, but don't go forever.
		int numRands = 0;
		while(usedCardIds.contains(classCards.get(index).id) && numRands < 100) {
			index = rand.nextInt(classCards.size()-1);
			numRands++;
		}
		
		// If it still hasn't found a card, but there are more class cards than used cards, linearly search.
		if(numRands >= 100 && classCards.size() > usedCardIds.size()) {
			index = 0;
			while(usedCardIds.contains(classCards.get(index).id)) {
				index++;
			}
		} else if(numRands >= 100 && classCards.size() <= usedCardIds.size()) {
			// This code should never hit, but just in case, all cards are used; select a random number.
			// And clear used cards.
			System.out.println("All Cards Used. Clearing.");
			usedCardIds.clear();
			index = rand.nextInt(classCards.size()-1);
		}
		
		return classCards.get(index);
	}
	
	/* public static HashMap<String, Object> getBoardPropertiesFromXMLElement(Element boardData) {

		String xmlBodyText = XMLHelper.getTextValue(cardData, "team");
		int xmlTurnId = XMLHelper.getIntValue(cardData, "turn_id");
	
		
		HashMap<String, Object> properties = new HashMap<String, Object>();

		properties.put("type", xmlTypeStr);
		properties.put("bodyText", xmlBodyText);
		properties.put("pageStart", xmlPageStart);
		properties.put("pageEnd", xmlPageEnd);
		
		return properties;
	} */
}
