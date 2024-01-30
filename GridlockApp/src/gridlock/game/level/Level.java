package gridlock.game.level;

import gridlock.game.vehicle.Movement;
import gridlock.game.vehicle.Vehicle;
import gridlock.util.IntPoint2D;
import javafx.geometry.Orientation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Level implements Serializable {

	// package-private (for access by LevelGenerator)
	final int[][] board;
	final Vehicle[] vehicles;

	// Note: package-private constructor (should only ever be constructed by LevelGenerator)
	Level(int[][] board) {
		this.board = board;

		// find number of vehicles
		int biggest = 1;
		for (int x = 0; x < board.length; x++) {
			for (int y = 0; y < board.length; y++) {
				if (board[x][y] > biggest) biggest = board[x][y];
			}
		}
		// make list of vehicles
		this.vehicles = new Vehicle[biggest];
		int nextVehicle = 0;
		int length = board.length;
		List<Integer> found = new ArrayList<Integer>();
		found.add(0);
		for (int x = 0; x < length; x++) {
			for (int y = 0; y < length; y++) {
				if (found.contains(board[x][y])) continue;
				// found new vehicle
				int num = board[x][y];
				found.add(num);
				IntPoint2D point = new IntPoint2D(x, y);

				int vehicleLength;
				Orientation orientation;
				if (y < length - 1 && board[x][y + 1] == num) {
					// vertical vehicle
					orientation = Orientation.VERTICAL;
					int vehicleEnd = y;
					for (int i = y; i < length && board[x][i] == num; i++) {
						vehicleEnd = i;
					}
					vehicleLength = vehicleEnd - y + 1;
				}
				else if (x < length - 1 && board[x + 1][y] == num) {
					// horizontal vehicle
					orientation = Orientation.HORIZONTAL;
					int vehicleEnd = x;
					for (int i = x; i < length && board[i][y] == num; i++) {
						vehicleEnd = i;
					}
					vehicleLength = vehicleEnd - x + 1;
				}
				else {
					// error
					throw new RuntimeException("Invalid board");
				}
				this.vehicles[nextVehicle++] = new Vehicle(this, num, vehicleLength, orientation, point);
			}
		}
	}

	// Copy-constructor
	public Level(Level other) {
		board = new int[other.board.length][];
		for (int i = 0; i < board.length; i++) {
			board[i] = other.board[i].clone();
		}

		vehicles = new Vehicle[other.vehicles.length];
		for (int i = 0; i < vehicles.length; i++) {
			vehicles[i] = new Vehicle(other.vehicles[i], this);
		}
	}

	public int getBoardSize() {
		return board.length;
	}

	public Vehicle[] getVehicles() {
		return vehicles;
	}

	public boolean updateBoard(Movement movement) {
		Vehicle vehicle = movement.getVehicle();

		int prevX = movement.getStartPosition().getX();
		int prevY = movement.getStartPosition().getY();

		for (int i = 0; i < vehicle.getLength(); i++) {
			if (vehicle.getOrientation() == Orientation.HORIZONTAL) {
				board[prevX + i][prevY] = 0;
			} else {
				board[prevX][prevY + i] = 0;
			}
		}

		int newX = movement.getEndPosition().getX();
		int newY = movement.getEndPosition().getY();

		for (int i = 0; i < vehicle.getLength(); i++) {
			if (vehicle.getOrientation() == Orientation.HORIZONTAL) {
				board[newX + i][prevY] = vehicle.getId();
			} else {
				board[prevX][newY + i] = vehicle.getId();
			}
		}

		printBoard();

		Vehicle red = vehicles[0];
		IntPoint2D redMax = vehicles[0].getMovementMax();
		if (red.getMovementMax().getX() >= board.length - red.getLength()) return true;
		return false;
	}

	public IntPoint2D getMovementMin(Vehicle vehicle) {
		int x = vehicle.getBoardPosition().getX();
		int y = vehicle.getBoardPosition().getY();

		int i;
		if (vehicle.getOrientation() == Orientation.HORIZONTAL) {
			for (i = x;; i--) {
				if (i == 0) break;
				if (board[i - 1][y] > 0) break;
			}
		} else {
			for (i = y;; i--) {
				if (i == 0) break;
				if (board[x][i - 1] > 0) break;
			}
		}
		System.out.println(i);
		return new IntPoint2D(i, i);
	}

	public IntPoint2D getMovementMax(Vehicle vehicle) {
		int x = vehicle.getBoardPosition().getX();
		int y = vehicle.getBoardPosition().getY();

		int i;
		if (vehicle.getOrientation() == Orientation.HORIZONTAL) {
			for (i = x + vehicle.getLength() - 1;; i++) {
				if (i == board.length - 1) {
					if (vehicle.getId() == 1) i = 99;
					break;
				}
				if (board[i + 1][y] > 0) break;
			}
		} else {
			for (i = y + vehicle.getLength() - 1;; i++) {
				if (i == board.length - 1) break;
				if (board[x][i + 1] > 0) break;
			}
		}
		System.out.println(i);
		i -= vehicle.getLength() - 1;
		return new IntPoint2D(i, i);
	}

	public void printBoard() {
		System.out.println("--------------------");
		for (int y = 0; y < board.length; y++) {
			for (int x = 0; x < board.length; x++) {
				int id = board[x][y];
				if (id > 0) {
					System.out.print(id + " ");
				} else {
					System.out.print("  ");
				}
			}
			System.out.println();
		}
		System.out.println("--------------------");
	}

	public void printVehicles() {
		System.out.println("--------------------");
		for (Vehicle v : vehicles) {
			System.out.printf("%d: (%d, %d)\n", v.getId(), v.getBoardPosition().getY(), v.getBoardPosition().getY());
		}
		System.out.println("--------------------");
	}

	private int hashBoardState() {
		int result = 0;
		for (int[] row : board) {
			result += Arrays.hashCode(row);
		}
		return result;
	}

	@Override
	public String toString() {
		return String.format("Level#%s", hashBoardState());
	}
}
