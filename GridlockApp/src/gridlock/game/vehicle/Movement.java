package gridlock.game.vehicle;

import gridlock.util.IntPoint2D;

import java.io.Serializable;

public class Movement implements Serializable {
	private final Vehicle vehicle;
	private final IntPoint2D startPosition;
	private final IntPoint2D endPosition;

	public Movement(Vehicle vehicle, IntPoint2D startPosition, IntPoint2D endPosition) {
		this.vehicle = vehicle;
		this.startPosition = startPosition;
		this.endPosition = endPosition;
	}

	public Vehicle getVehicle() {
		return vehicle;
	}

	public IntPoint2D getStartPosition() {
		return startPosition;
	}

	public IntPoint2D getEndPosition() {
		return endPosition;
	}
}
