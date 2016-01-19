package edu.virginia.bookmark;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Board {
	final int BOARD_WIDTH = 4;
	final int BOARD_HEIGHT = 4;
	public Card[][] board;
	
	public Board(ArrayList<Team> teams, ArrayList<Card> boardDeck) {
		board = new Card[BOARD_WIDTH][BOARD_HEIGHT];
		fillBoard(boardDeck);
	}
	
	public void fillBoard(ArrayList<Card> classCards) {
		
		ArrayList<Integer> usedNums = new ArrayList<Integer>();

		Random rand = new Random();
			int index = 0;
				
			
		for(int i = 0; i < BOARD_WIDTH; i++) {
			for(int j = 0; j < BOARD_HEIGHT; j++) {
			
			index = rand.nextInt(BOARD_WIDTH*BOARD_HEIGHT);
			
			if(!usedNums.contains(index)) {
				board[i][j] = classCards.get(index);
				usedNums.add(index);
				}
			}
			
		}


	}

	public Card returnCardAtPos(Point pos) {
		return board[pos.x][pos.y];
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
