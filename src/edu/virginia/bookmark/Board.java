package edu.virginia.bookmark;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;

public class Board {
	final int BOARD_WIDTH = 4;
	final int BOARD_HEIGHT = 4;
	public Card[][] board;
	HashMap<Team, Point> teamPositions;
	
	public Board(ArrayList<Team> teams) {
		board = new Card[BOARD_WIDTH][BOARD_HEIGHT];
		fillBoard();
	}
	
	public void fillBoard() {
		// Fills all the slots in the board with cards
		// that are randomly sampled from the class decks
	}
}
