package gridlock.game.helpers;

import java.util.*;

/**
 * A solution generator for a Gridlock game.
 * To use, call {@link #solveBoard(int[][], int)} and give a valid input board (see {@link LevelGenerator}).
 * Then, other functions may be called to either getInstance the solution, or to getInstance more information about the solution.
 * @author z5164800
 *
 */
public class Solver {
	private int[][] board;
	private ArrayList<int[][]> solution;
	private boolean solved; // bool to prevent other functions from being called before solveBoard()
	
	/**
	 * Constructor for the solver class.
	 */
	public Solver() {
		this.solved = false;
	}
	
	/**
	 * Inserts the given board into the solver.
	 * The board will automatically be solved, and the other functions may be called after this.
	 * If the board cannot be solved, or an invalid board is given, this function does nothing, and returns 1.
	 * @param board - a 2 dimensional array of integers (see {@link LevelGenerator}).
	 * @param limit - a limit on the number of moves the solution must have
	 * @return 0 on success, 1 on failure
	 */
	public int solveBoard(int[][] board, int limit) {
		if (board == null) return 0;
		if (limit < 1) return 0;
		this.board = board;
		if (this.solve(limit) == 0) {
			this.solved = true;
			return 0;
		}
		else {
			this.solved = false;
			return 1;
		}
	}

	/**
	 * Returns the number of moves required to solve the last valid inputted board.
	 * A move is considered to be the movement of a vehicle any number of squares in one direction.
	 * @return the number of moves on success, -1 if there is an error (no board has been given)
	 */
	public int solMoveCount() {
		if (this.solved == false) return -1;
		return this.solution.size() - 1;
	}
	
	/**
	 * Returns an array of boards, where the first board is the starting board, and the final board is the ending board.
	 * @return a 3 dimensional array of integers
	 */
	public ArrayList<int[][]> getSol() {
		return this.solution;
	}
	
	/**
	 * Solves the board stored in the object's board field.
	 * The board must be valid (non null).
	 * The solution is put into the solution field of the object on success
	 * @return 0 on success, 1 on failure
	 */
	private int solve(int limit) {
		State origin = new State(this.board);
		origin.setDistance(0);
		
		Queue<State> openSet = new LinkedList<State>();
		openSet.add(origin);
		
		Map<State, State> predecessors = new HashMap<State, State>();
		predecessors.put(origin, origin);
		
		while (!openSet.isEmpty()) {
			State curr = openSet.remove();
			
			if (curr.isSolved()) {
				reconstructPath(predecessors, origin, curr);
				return 0;
			}
			
			if (curr.getDistance() == limit) {
				continue;
			}
			
			for (State neighbour : curr.neighbours()) {
				if (!predecessors.containsKey(neighbour)) {
					openSet.add(neighbour);
					neighbour.setDistance(curr.getDistance() + 1);
					predecessors.put(neighbour, curr);
				}
			}
		}
		
		return 1;
	}
	
	/**
	 * Sets this object's solution attribute to the list of board states
	 * starting from the origin state to the current state
	 * @param predecessors - a predecessor map of states to their predecessors
	 * @param origin - the starting state
	 * @param curr - the current state
	 */
	private void reconstructPath(Map<State, State> predecessors, State origin, State curr) {
		ArrayList<int[][]> path = new ArrayList<int[][]>();
		State next;
		for (path.add(curr.getBoard()); !origin.equals(curr); curr = next) {
			next = predecessors.get(curr);
			path.add(0, next.getBoard());
		}
		this.solution = path;
	}
	
}

