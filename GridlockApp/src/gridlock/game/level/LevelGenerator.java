package gridlock.game.level;

import gridlock.game.helpers.Solver;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * A class to generate Levels.
 * Construct it with a given board size and difficulty (minimum number of moves to completion),
 * then call {@link #generate()} to get a Level object
 * @author z5164800
 * @see gridlock.game.level.Level
 */
public class LevelGenerator {
	private final int boardSize;
	private final int difficulty;
	private Random randGen;
	private Level previousBoard;

	/**
	 * Creates a new LevelGenerator, with a specified seed and difficulty
	 * @param boardSize - the square size of the boards to be created
	 * @param seed - any number for the random number generator
	 * @param difficulty - the minimum number of moves in the solution of generated boards
	 */
	public LevelGenerator(int boardSize, int difficulty) {
		this.boardSize = boardSize;
		this.randGen = new Random();
		this.randGen.setSeed(0);
		this.difficulty = difficulty;
		this.previousBoard = null;
	}

	/**
	 * Recreate the previous level
	 * @return the previous Level object, or null if {@link #generate()} was not called before
	 */
	public Level regenerate() {
		return this.previousBoard;
	}
	
	/**
	 * Generates a level. Can be used in a thread (implements callable).
	 * @return a Level object
	 */
	private Level parallelGenerate() {
		Solver solver = new Solver();
		
		// create the most basic board (red car only)
		int[][] bestBoard = new int[this.boardSize][this.boardSize];
		bestBoard[0][bestBoard.length / 2] = 1;
		bestBoard[1][bestBoard.length / 2] = 1;

		int i = 0, moves = 0;

		int[][] newBoard;
		int consecutive = 0;
		while (true) {
			i++;
			consecutive++;
			// randomise the board
			newBoard = this.randomBoard(bestBoard, 2);
			if (newBoard == null || consecutive > 10) {
				// start a new board if no more insertions can be made, or no progress has been made for 10 iterations
				moves = 0;
				consecutive = 0;
				bestBoard = new int[this.boardSize][this.boardSize];
				bestBoard[0][bestBoard.length / 2] = 1;
				bestBoard[1][bestBoard.length / 2] = 1;
				continue;
			}
			// board is unsolvable
			if (solver.solveBoard(newBoard, moves + 3) == 1) continue;
			// new best board found
			if (solver.solMoveCount() > moves) {
				bestBoard = newBoard;
				moves = solver.solMoveCount();
				consecutive = 0;
			}
			// valid board found, return it
			if (moves >= this.difficulty) {
				System.out.println("iterations: " + i);
				System.out.println("moves: " + moves);
				Level level = new Level(bestBoard);
				//level.printBoard();
				return level;
			}
			// shutdown this thread when the shutdownNow() is called
			if (Thread.currentThread().isInterrupted()) return null;
		}
	}
	
	/**
	 * Generates a Level for this level generator (using the given board size and difficulty)
	 * @return a Level object
	 */
	public Level generate() {
		int cores = Runtime.getRuntime().availableProcessors();
		cores = cores/3 + 1;
		ExecutorService pool = Executors.newFixedThreadPool(cores);
		List<Callable<Level>> tasks = new ArrayList<Callable<Level>>();
		for (int i = 0; i < cores; i++) {
			Callable<Level> task = () -> this.parallelGenerate();
			tasks.add(task);
		}
		Level level = null;
		synchronized (pool) {
			try {
				level = pool.invokeAny(tasks, 10, TimeUnit.SECONDS);
				this.previousBoard = level;
			}
			catch (Throwable e) {
				System.out.println(e);
			}
			finally {
				pool.shutdownNow();
			}
		}
		return level;
	}
	
	/**
	 * Randomises a given board by randomly inserting a given number of vehicles (if possible)
	 * @param board - the square board to be randomised
	 * @param numVehicles - the number of vehicles to try to add
	 * @return a copy of the board with the random insertions (the original board is preserved), 
	 * or null if no insertions were made
	 */
	private int[][] randomBoard(int[][] board, int numVehicles) {
		// copy the board
		int[][] newBoard = new int[board.length][board.length];
		for (int i = 0; i < board.length; i++) {
			System.arraycopy(board[i], 0, newBoard[i], 0, board.length);
		}
		// find highest vehicle id
		int highest = 0;
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board.length; j++) {
				if (board[i][j] > highest) highest = board[i][j];
			}
		}
		// Insert vehicles
		int inserted = 0;
		for (int i = 0; i < numVehicles; i++) {
			int id = i + highest + 1;
			int length = this.randGen.nextInt(2) + 2;
			boolean horizontal = this.randGen.nextBoolean();
			if (this.insertVehicle(newBoard, length, id, horizontal) == 1) {
				continue;
			}
			inserted++;
		}
		if (inserted == 0) return null;
		return newBoard;
	}
	
	/**
	 * Inserts a given vehicle into a random space in a given board
	 * @param board - the square board where the vehicle is to be inserted
	 * @param length - the length of the vehicle to be inserted
	 * @param identifier - a unique integer to identify the vehicle
	 * @param horizontal - a boolean stating whether the vehicle is to be horizontally inserted
	 * @return 0 pm success, 1 on failure
	 */
	private int insertVehicle(int[][] board, int length, int identifier, boolean horizontal) {
		// Ensure that there is at least one available location for the vehicle
		int x, y;
		boolean found = false;
		for (x = 0; x < board.length; x++) {
			for (y = board.length - 1; y >= 0; y--) {
				if (checkSpace(board, x, y, length, horizontal)) {
					found = true;
					break;
				}
			}
			if (found == true) {
				break;
			}
		}
		if (!found) return 1;
		
		// Check random squares until a valid one is found, then insert
		int randNum;
		while (true) {
			// Find a random square in the board
			randNum = this.randGen.nextInt(board.length * board.length);
			x = randNum / board.length;
			y = randNum % board.length;
			// Check that the vehicle can be inserted
			if (checkSpace(board, x, y, length, horizontal)) {
				// Space found, insert vehicle
				for (int i = 0; i < length; i++) {
					if (horizontal) {
						board[x + i][y] = identifier;
					} else {
						board[x][y - i] = identifier;
					}
				}
				return 0;
			}
		}		
	}
	
	/**
	 * Helper function for {@link #insertVehicle}.
	 * Checks whether there is enough space to the right or under the starting square.
	 * @param board - the square board where the vehicle is to be inserted
	 * @param x - the x coordinate of the starting square for the vehicle, must be in the board
	 * @param y - the y coordinate of the starting square for the vehicle, must be in the board
	 * @param length - the length of the vehicle to be inserted
	 * @param horizontal - a boolean specifying whether the vehicle is to be horizontal, or vertical
	 * @return true if there is space for the vehicle, false otherwise
	 */
	private static boolean checkSpace(int[][] board, int x, int y, int length, boolean horizontal) {
		for (int i = 0; i < length; i++) {
			if (horizontal) {
				if ((x + i) == board.length) return false;
				if (board[x + i][y] != 0) return false;
			} else {
				if ((y - i) < 0) return false;
				if (board[x][y - i] != 0) return false;
			}
		}
		return true;
	}
}

