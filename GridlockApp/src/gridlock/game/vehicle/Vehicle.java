package gridlock.game.vehicle;

import gridlock.game.level.Level;
import gridlock.util.IntPoint2D;
import javafx.geometry.Orientation;
import javafx.scene.paint.Color;

import java.io.Serializable;

public class Vehicle implements Serializable {

	private final Level level;
	private final int id;
	private final int length;
	private final Orientation orientation;

	private IntPoint2D boardPosition;

	private transient final Color color;

	public Vehicle(Level level, int id, int length, Orientation orientation, IntPoint2D boardPosition) {
		this.level = level;
		this.id = id;
		this.length = length;
		this.orientation = orientation;
		this.boardPosition = boardPosition;
		// color = Color.color(Math.random(), Math.random(), Math.random());
		if (id == 1) {
			color = Color.DARKRED;
		} else {
			double brightness = (id - 2) / (float)(level.getVehicles().length - 2);
			color = Color.color(brightness / 2, brightness, brightness / 2);
		}
	}

	// Copy-constructor
	public Vehicle(Vehicle other, Level level) {
		this.level = level;
		id = other.id;
		length = other.length;
		orientation = other.orientation;
		boardPosition = other.boardPosition;
		color = other.color;
	}

	public Movement moveTo(IntPoint2D newPosition) {
		Movement movement = new Movement(this, boardPosition, newPosition);
		boardPosition = newPosition;
		level.updateBoard(movement);
		return movement;
	}

	public IntPoint2D getMovementMin() {
		return level.getMovementMin(this);
	}

	public IntPoint2D getMovementMax() {
		return level.getMovementMax(this);
	}

	public int getId() {
		return id;
	}

	public IntPoint2D getBoardPosition() {
		return boardPosition;
	}

	public int getLength() {
		return length;
	}

	public Orientation getOrientation() {
		return orientation;
	}

	public Color getColor() {
		return color;
	}
}
