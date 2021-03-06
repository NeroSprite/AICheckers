package fr.istic.ia.tp1;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Implementation of the English Draughts game.
 * @author vdrevell
 *
 */
public class EnglishDraughts extends Game {
	/** 
	 * The checker board
	 */
	CheckerBoard board;
	
	/** 
	 * The {@link PlayerId} of the current player
	 * {@link PlayerId#ONE} corresponds to the whites
	 * {@link PlayerId#TWO} corresponds to the blacks
	 */
	PlayerId playerId;
	
	/**
	 * The current game turn (incremented each time the whites play)
	 */
	int nbTurn;
	
	/**
	 * The number of consecutive moves played only with kings and without capture
	 * (used to decide equality)
	 */
	int nbKingMovesWithoutCapture;
	
	/**
	 * Class representing a move in the English draughts game
	 * A move is an ArrayList of Integers, corresponding to the successive tile numbers (Manouri notation)
	 * toString is overrided to provide Manouri notation output.
	 * @author vdrevell
	 *
	 */
	class DraughtsMove extends ArrayList<Integer> implements Game.Move {

		private static final long serialVersionUID = -8215846964873293714L;

		@Override
		public String toString() {
			Iterator<Integer> it = this.iterator();
			Integer from = it.next();
			StringBuffer sb = new StringBuffer();
			sb.append(from);
			while (it.hasNext()) {
				Integer to = it.next();
				if (board.neighborDownLeft(from)==to || board.neighborUpLeft(from)==to
						|| board.neighborDownRight(from)==to || board.neighborUpRight(from)==to) {
					sb.append('-');
				}
				else {
					sb.append('x');
				}
				sb.append(to);
				from = to;
			}
			return sb.toString();
		}
	}
	
	/**
	 * The default constructor: initializes a game on the standard 8x8 board.
	 */
	public EnglishDraughts() {
		this(8);
	}
	
	/**
	 * Constructor with custom boardSize (to play on a boardSize x boardSize checkerBoard).
	 * @param boardSize See {@link CheckerBoard#CheckerBoard(int)} for valid board sizes. 
	 */
	public EnglishDraughts(int boardSize) {
		this.board = new CheckerBoard(boardSize);
		this.playerId = PlayerId.ONE;
		this.nbTurn = 1;
		this.nbKingMovesWithoutCapture = 0;
	}
	
	/**
	 * Copy constructor
	 * @param d The game to copy
	 */
	EnglishDraughts(EnglishDraughts d) {
		this.board = d.board.clone();
		this.playerId = d.playerId;
		this.nbTurn = d.nbTurn;
		this.nbKingMovesWithoutCapture = d.nbKingMovesWithoutCapture;
	}
	
	@Override
	public EnglishDraughts clone() {
		return new EnglishDraughts(this);
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(nbTurn);
		sb.append(". ");
		sb.append(this.playerId==PlayerId.ONE?"W":"B");
		sb.append(":");
		sb.append(board.toString());
		return sb.toString();
	}
	
	@Override
	public String playerName(PlayerId playerId) {
		switch (playerId) {
		case ONE:
			return "Player with the whites";
		case TWO:
			return "Player with the blacks";
		case NONE:
		default:
			return "Nobody";
		}
	}
	
	@Override
	public String view() {
		return board.boardView() + "Turn #" + nbTurn + ". " + playerName(playerId) + " plays.\n";
	}
	
	/**
	 * Check if a tile is empty
	 * @param square Tile number
	 * @return
	 */
	boolean isEmpty(int square) {
		return board.isEmpty(square);
	}
	//FIXME
	/** 
	 * Check if a tile is owned by adversary
	 * @param square Tile number
	 * @return
	 */
	boolean isAdversary(int square) {
		if (playerId.equals(PlayerId.ONE)) {
			return board.isBlack(square);
		} else {
			return board.isWhite(square);
		}
	}
	//FIXME
	/** 
	 * Check if a tile is owned by the current player
	 * @param square Tile number
	 * @return
	 */
	boolean isMine(int square) {
		if (playerId.equals(PlayerId.ONE)) {
			return board.isWhite(square);
		} else {
			return board.isBlack(square);
		}
	}
	
	/** 
	 * Retrieve the list of positions of the pawns owned by the current player
	 * @return The list of current player pawn positions
	 */
	ArrayList<Integer> myPawns() {
		List<Integer> pawns = new ArrayList<Integer>();
		if (playerId.equals(PlayerId.ONE)) {
			return board.getWhitePawns();
		} else {
			return board.getBlackPawns();
		}
	}
	
	
	/**
	 * Generate the list of possible moves
	 * - first check moves with captures
	 * - if no capture possible, return displacement moves
	 */
	@Override
	public List<Move> possibleMoves() {
		//
		// TODO generate the list of possible moves
		//
		// Advice: 
		// create two auxiliary functions :
		// - one for jump moves from a given position, with capture (and multi-capture).
		//    Use recursive calls to explore all multiple capture possibilities
		// - one function that returns the displacement moves from a given position (without capture)
		//
		ArrayList<Move> moves = new ArrayList<>();
		moves.addAll(possibleMovesWithoutCapture());
		return moves;
	}

	public ArrayList<Move> possibleMovesWithoutCapture(){
		ArrayList<Move> moves = new ArrayList<>();
		ArrayList<Integer> myPawns = myPawns();
		//Les pions sont blanc
		if(playerId.equals(PlayerId.ONE)){ //WHITE
			for(int i = 0; i< myPawns.size(); i++) {
				int current = myPawns.get(i);
					int upLeft = board.neighborUpLeft(current);
					int upRight = board.neighborUpRight(current);
					if (upLeft != 0 && board.isEmpty(upLeft)) {
						DraughtsMove move1 = new DraughtsMove();
						move1.add(current);
						move1.add(upLeft);
						moves.add(move1);
					}
					if (upRight != 0 && board.isEmpty(upRight)) {
						DraughtsMove move1 = new DraughtsMove();
						move1.add(current);
						move1.add(upRight);
						moves.add(move1);
					}
					if (board.isKing(current)) {//WhiteKing
						int downLeft = board.neighborDownLeft(current);
						int downRight = board.neighborDownRight(current);
						if (upRight != 0 && board.isEmpty(downLeft)) {
							DraughtsMove move1 = new DraughtsMove();
							move1.add(current);
							move1.add(downLeft);
							moves.add( move1);
						}
						if (upRight != 0 && board.isEmpty(downRight)) {
							DraughtsMove move1 = new DraughtsMove();
							move1.add(current);
							move1.add(downRight);
							moves.add( move1);
						}
				}
			}
		}
		else { //BLACK
			for(int i = 0; i< myPawns.size(); i++) {
				int current = myPawns.get(i);
				int downLeft = board.neighborDownLeft(current);
				int downRight = board.neighborDownRight(current);
				if (downLeft != 0 && board.isEmpty(downLeft)) {
					DraughtsMove move1 = new DraughtsMove();
					move1.add(current);
					move1.add(downLeft);
					moves.add( move1);
				}
				if (downRight != 0 && board.isEmpty(downRight)) {
					DraughtsMove move1 = new DraughtsMove();
					move1.add(current);
					move1.add(downRight);
					moves.add(move1);
				}
				if (board.isKing(current)) {//WhiteKing
					int upLeft = board.neighborUpLeft(current);
					int upRight = board.neighborUpRight(current);
					if (upLeft != 0 && board.isEmpty(upLeft)) {
						DraughtsMove move1 = new DraughtsMove();
						move1.add(current);
						move1.add(upLeft);
						moves.add(move1);
					}
					if (upRight != 0 && board.isEmpty(upRight)) {
						DraughtsMove move1 = new DraughtsMove();
						move1.add(current);
						move1.add(upRight);
						moves.add(move1);
					}
				}
			}

		}


		return moves;
	}

	@Override
	public void play(Move aMove) {
		// Player should be valid
		if (playerId == PlayerId.NONE)
			return;
		// We will cast Move to DraughtsMove (kind of ArrayList<Integer>
		if (!(aMove instanceof DraughtsMove))
			return;
		// Cast and apply the move
		DraughtsMove move = (DraughtsMove) aMove;
		
		
		//
		// TODO implement play
		//
		
		// Move pawn and capture opponents
		
		// Promote to king if the pawn ends on the opposite of the board
		
		// Next player
		
		// Update nbTurn
		
		// Keep track of successive moves with kings wthout capture

	}

	@Override
	public PlayerId player() {
		return playerId;
	}

	/**
	 * Get the winner (or null if the game is still going)
	 * Victory conditions are :
	 * - adversary with no more pawns or no move possibilities
	 * Null game condition (return PlayerId.NONE) is
	 * - more than 25 successive moves of only kings and without any capture
	 */
	@Override
	public PlayerId winner() {
		//
		// TODO implement winner
		//
		
		// return the winner ID if possible
		
		// return PlayerId.NONE if the game is null
		
		// Return null is the game has not ended yet
		return null;
	}
}
