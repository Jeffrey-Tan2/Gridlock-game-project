package gridlock.util;

import javafx.geometry.Point2D;

import java.io.Serializable;

public class IntPoint2D implements Serializable {

	private int x;
	private int y;

	public IntPoint2D(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public IntPoint2D(Point2D point2D) {
		x = (int)Math.round(point2D.getX());
		y = (int)Math.round(point2D.getY());
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
}
