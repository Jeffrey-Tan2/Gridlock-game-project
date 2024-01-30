package gridlock.game.helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * A class to assist in solving a Gridlock board.
 * Can be used to find possible moves, or to check if a board is solved.
 * @see LevelGenerator
 * @see Solver
 * @author z5164800
 *
 */
public class State {
	private int[][] board;
	private int distance = -1;
	
	/**
	 * Constructor for the State class.
	 * Creates a copy of and stores the given board.
	 * The given board must be valid, see {@link LevelGenerator}
	 * @param board - a 2 dimensional array of ints
	 */
	public State(int[][] board) {
		this.board = deepCopy(board);
	}
	
	/**
	 * Performs a deep copy of the given board.
	 * @param board - a 2 dimensional array of ints
	 * @return a deep copy of board
	 */
	private static int[][] deepCopy(int[][] board) {
		int size = board.length;
		int[][] copy = new int[size][size];
		for (int i = 0; i < size; i++) {
			System.arraycopy(board[i], 0, copy[i], 0, size);
		}
		return copy;
	}

	/**
	 * Checks whether the board in this state is solved.
	 * To see what a solved board means, see {@link LevelGenerator}
	 * @return whether the board is solved
	 */
	public boolean isSolved() {
		for (int i = 0; i < this.board.length; i++) {
			if (this.board[this.board.length - 1][i] == 1) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Finds the list of all reachable states from this state (in one move)
	 * @return an {@link ArrayList} of States, null on error
	 */
	public ArrayList<State> neighbours() {
		ArrayList<State> neighbours = new ArrayList<State>();
		List<Integer> found = new ArrayList<Integer>(); // list of found vehicles
		// loop through board and find vehicles, then generate neighbours accordingly
		int length = this.board.length;
		for (int x = 0; x < length; x++) {
			for (int y = 0; y < length; y++) {
				if (this.board[x][y] == 0) continue;
				if (found.contains(this.board[x][y])) continue;
				
				// found new vehicle
				int num = this.board[x][y];
				found.add(num);
				
				if (y < length - 1 && this.board[x][y + 1] == num) {
					// vertical vehicle
					int[][] tempBoard = deepCopy(this.board);
					// find the end of the vehicle
					int vehicleEnd = y;
					for (int i = y; i < length && tempBoard[x][i] == num; i++) {
						vehicleEnd = i;
					}
					// shift vehicle down 1 step at a time, and create a new state
					for (int i = 1; y - i >= 0 && tempBoard[x][y - i] == 0; i++) {
						tempBoard[x][y - i] = num;
						tempBoard[x][vehicleEnd - i + 1] = 0;
						State newNeighbour = new State(tempBoard);
						neighbours.add(newNeighbour);
					}
					// restore tempBoard
					tempBoard = deepCopy(this.board);
					// shift vehicle up 1 step at a time, and create a new state
					for (int i = 1; vehicleEnd + i < length && tempBoard[x][vehicleEnd + i] == 0; i++) {
						tempBoard[x][vehicleEnd + i] = num;
						tempBoard[x][y + i - 1] = 0;
						State newNeighbour = new State(tempBoard);
						neighbours.add(newNeighbour);
					}
				}
				else if (x < length - 1 && this.board[x + 1][y] == num) {
					// horizontal vehicle
					int[][] tempBoard = deepCopy(this.board);
					// find the end of the vehicle
					int vehicleEnd = x;
					for (int i = x; i < length && tempBoard[i][y] == num; i++) {
						vehicleEnd = i;
					}
					// shift vehicle left 1 step at a time, and create a new state
					for (int i = 1; x - i >= 0 && tempBoard[x - i][y] == 0; i++) {
						tempBoard[x - i][y] = num;
						tempBoard[vehicleEnd - i + 1][y] = 0;
						State newNeighbour = new State(tempBoard);
						neighbours.add(newNeighbour);
					}
					// restore tempBoard
					tempBoard = deepCopy(this.board);
					// shift vehicle right 1 step at a time, and create a new state
					for (int i = 1; vehicleEnd + i < length && tempBoard[vehicleEnd + i][y] == 0; i++) {
						tempBoard[vehicleEnd + i][y] = num;
						tempBoard[x + i - 1][y] = 0;
						State newNeighbour = new State(tempBoard);
						neighbours.add(newNeighbour);
					}
				}
				else {
					// error
					throw new RuntimeException("Invalid board");
				}
			}
		}
		return neighbours;
	}
	
	/**
	 * Getter for the distance attribute of this state.
	 * Distance can be used for anything, but should probably be used to keep track of the
	 * number of moves needed to reach the current state
	 * @return this object's distance attribute, or -1 if it is uninitialised
	 */
	public int getDistance() {
		return this.distance;
	}
	
	/**
	 * Setter for the distance attribute of this state
	 * @see State#getDistance()
	 * @param distance - an integer
	 */
	public void setDistance(int distance) {
		this.distance = distance;
	}
	
	/**
	 * Getter for the board in this state.
	 * @return a 2d array of ints, see {@link LevelGenerator}
	 */
	public int[][] getBoard() {
		return this.board;
	}
	
	/**
	 * Returns a hashcode for this object
	 */
	@Override
	public int hashCode() {
		return Arrays.deepHashCode(this.board);
	}
	
	/**
	 * Checks whether this object is equal to the given object
	 * @param other - the object to compare to for equality
	 */
	@Override
	public boolean equals(Object other) {
		if (other == null) return false;
		if (!(other instanceof State)) return false;
		State state = (State) other;
		return Arrays.deepEquals(this.board, state.board);
	}
}

