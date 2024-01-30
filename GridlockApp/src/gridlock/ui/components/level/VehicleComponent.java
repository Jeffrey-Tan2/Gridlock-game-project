package gridlock.ui.components.level;

import gridlock.game.vehicle.Movement;
import gridlock.game.vehicle.Vehicle;
import gridlock.ui.components.Component;
import gridlock.util.Constants;
import gridlock.util.IntPoint2D;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class VehicleComponent implements Component {

	private final ImageView node;
	private final LevelComponent levelComponent;
	private final Vehicle vehicle;
	private final Color color;

	private double cellSize;
	private Point2D offset;
	private Point2D nodeDragStart;
	private Point2D mouseDragStart;
	Point2D movementMin;
	Point2D movementMax;

	public VehicleComponent(LevelComponent levelComponent, Vehicle vehicle, double cellSize, Point2D offset, Color color) {
		this.levelComponent = levelComponent;
		this.vehicle = vehicle;
		this.cellSize = cellSize;
		this.offset = offset;
		this.color = color;

		int length = vehicle.getLength();

		if (vehicle.getOrientation() == Orientation.HORIZONTAL) {
			if (vehicle.getId() == 1) {
				Image gold = new Image("/gridlock/ui/sprites/gold.png", cellSize * length, cellSize, false, false);
				ImageView goldView = new ImageView(gold);
				node = goldView;
			}
			else if (vehicle.getLength() == 2) {
				Image rock = new Image("/gridlock/ui/sprites/hstone2.jpg",cellSize * length, cellSize, false, false);
				ImageView rockView = new ImageView(rock);
				node = rockView;
			}
			else {
				Image rock = new Image("/gridlock/ui/sprites/hstone3.jpg",cellSize * length, cellSize, false, false);
				ImageView rockView = new ImageView(rock);
				node = rockView;
			}
		} else {
			if (vehicle.getLength() == 2) {
				Image rock = new Image("/gridlock/ui/sprites/vstone2.jpg",cellSize, cellSize * length, false, false);
				ImageView rockView = new ImageView(rock);
				node = rockView;
			}
			else {
				Image rock = new Image("/gridlock/ui/sprites/vstone3.jpg",cellSize, cellSize * length, false, false);
				ImageView rockView = new ImageView(rock);
				node = rockView;
			}
		}
		Point2D initialPosition = gridToLayout(vehicle.getBoardPosition());
		node.setLayoutX(initialPosition.getX());
		node.setLayoutY(initialPosition.getY());

		//node.setFill(color);
		node.setCursor(Cursor.OPEN_HAND);

		node.setOnMousePressed(this::handleMousePressed);
		node.setOnMouseDragged(this::handleMouseDragged);
		node.setOnMouseReleased(this::handleMouseReleased);
	}

	private void handleMousePressed(MouseEvent event) {
		node.setCursor(Cursor.CLOSED_HAND);
		nodeDragStart = new Point2D(node.getLayoutX(), node.getLayoutY());
		mouseDragStart = new Point2D(event.getSceneX(), event.getSceneY());
		movementMin = gridToLayout(vehicle.getMovementMin());
		movementMax = gridToLayout(vehicle.getMovementMax());

	}

	private void handleMouseDragged(MouseEvent event) {
		double x = nodeDragStart.getX();
		double y = nodeDragStart.getY();

		if (vehicle.getOrientation() == Orientation.HORIZONTAL) {
			double minX = movementMin.getX();
			double maxX = movementMax.getX();

			x += event.getSceneX() - mouseDragStart.getX();

			if (x < minX) {
				x = minX;
			} else if (x > maxX) {
				x = maxX;
			}
		} else {
			double minY = movementMin.getY();
			double maxY = movementMax.getY();

			y += event.getSceneY() - mouseDragStart.getY();

			if (y < minY) {
				y = minY;
			} else if (y > maxY) {
				y = maxY;
			}
		}

		node.setLayoutX(x);
		node.setLayoutY(y);
	}

	private void handleMouseReleased(MouseEvent event) {
		node.setCursor(Cursor.OPEN_HAND);

		// Calculate the closest grid position
		IntPoint2D closestGridPosition = layoutToGrid(new Point2D(node.getLayoutX(), node.getLayoutY()));

		if (vehicle.getId() == 1 && closestGridPosition.getX() > Constants.BOARD_SIZE - vehicle.getLength()) {
			levelComponent.triggerVictory(node.getLayoutX());
		} else {
			// Update the vehicle's value in the back-end
			Movement movement = vehicle.moveTo(closestGridPosition);

			// Snap to the closest grid position
			Point2D finalLayoutPosition = gridToLayout(closestGridPosition);
			node.setLayoutX(finalLayoutPosition.getX());
			node.setLayoutY(finalLayoutPosition.getY());

			levelComponent.handleMovement(movement);
		}
	}

	public void undo(Movement movement) {
		IntPoint2D gridDestination = movement.getStartPosition();
		vehicle.moveTo(gridDestination);

		Point2D layoutDestination = gridToLayout(gridDestination);
		node.setLayoutX(layoutDestination.getX());
		node.setLayoutY(layoutDestination.getY());
	}

	public void redo(Movement movement) {
		IntPoint2D gridDestination = movement.getEndPosition();
		vehicle.moveTo(gridDestination);

		Point2D layoutDestination = gridToLayout(gridDestination);
		node.setLayoutX(layoutDestination.getX());
		node.setLayoutY(layoutDestination.getY());
	}

	private IntPoint2D layoutToGrid(Point2D layoutPosition) {
		Point2D transformedPosition = layoutPosition
			.subtract(offset)
			.multiply(1 / cellSize);

		return new IntPoint2D(transformedPosition);
	}

	private Point2D gridToLayout(IntPoint2D gridPosition) {
		return new Point2D(gridPosition.getX(), gridPosition.getY())
			.multiply(cellSize)
			.add(offset);
	}

	@Override
	public Node getNode() {
		return node;
	}
}
