package gridlock.ui.components.level;

import gridlock.game.level.Level;
import gridlock.game.vehicle.Movement;
import gridlock.game.vehicle.Vehicle;
import gridlock.ui.components.Component;
import gridlock.util.Constants;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.LinkedList;

public class LevelComponent implements Component {

	private final Level levelSnapshot;
	private final Runnable victoryHandler;
	private final StackPane node = new StackPane();

	private Level level;

	private ObservableList<Movement> history;
	private Point2D gridOffset;
	private VehicleComponent[] vehicleComponents;

	// Layers
	private Pane bgLayer;
	private Pane gridLayer;
	private Pane vehiclesLayer;
	private Pane modalLayer;

	public LevelComponent(Level levelSnapshot, Runnable victoryHandler) {
		this.levelSnapshot = levelSnapshot;
		this.victoryHandler = victoryHandler;
		history = FXCollections.observableList(new LinkedList<>());
		init();
	}

	// Called by the constructor, but can also be manually called again to "restart" the level
	public void init() {
		// Make a deep copy of the level (so that we can "reset" back to its initial state later)
		level = new Level(levelSnapshot); // copy-constructor
		history.removeAll(history); // clear history (can't just reinitialize or else binding will be lost)

		// Clear the stackpane
		node.getChildren().clear();

		// Add the layers into the StackPane
		bgLayer = new Pane();
		Image dirtbg = new Image("file:///tmp_amd/adams/export/adams/3/z5162796/COMP2511-Project-master/GridlockApp/src/gridlock/ui/sprites/dirtbg.png");
		ImageView dirtbgView = new ImageView(dirtbg);
		bgLayer.getChildren().add(dirtbgView);

		gridLayer = new Pane();
		vehiclesLayer = new Pane();
		modalLayer = new Pane();
		node.getChildren().add(bgLayer);
		node.getChildren().add(gridLayer);
		node.getChildren().add(vehiclesLayer);
		// note: modalLayer is not added until the modal should actually appear

		// temporary
		node.setBackground(new Background(new BackgroundFill(Color.GRAY, null, null)));

		int boardSize = level.getBoardSize();
		int padding = 100;
		int gridWidth = Constants.WINDOW_WIDTH - padding * 2;
		gridOffset = new Point2D(padding, padding);
		double SCALED_CELL_SIZE = gridWidth / (double)boardSize;


		// ----- GRID LAYER -----

		for (int x = 0; x < boardSize; x++) {
			for (int y = 0; y < boardSize; y++) {
				if (y == 3) {
				Image track = new Image("/gridlock/ui/sprites/tracks.png", SCALED_CELL_SIZE, SCALED_CELL_SIZE, false, false);
				ImageView trackView = new ImageView(track);
				trackView.setLayoutX(gridOffset.getX() + x * SCALED_CELL_SIZE);
				trackView.setLayoutY(gridOffset.getY() + y * SCALED_CELL_SIZE);
				gridLayer.getChildren().add(trackView);
				} else {
				Image dirt = new Image("/gridlock/ui/sprites/dirt.jpg", SCALED_CELL_SIZE, SCALED_CELL_SIZE, false, false);
				ImageView dirtView = new ImageView(dirt);
				dirtView.setLayoutX(gridOffset.getX() + x * SCALED_CELL_SIZE);
				dirtView.setLayoutY(gridOffset.getY() + y * SCALED_CELL_SIZE);
				gridLayer.getChildren().add(dirtView);
				}
			}
		}


		// ----- VEHICLES LAYER -----


		Vehicle[] vehicles = level.getVehicles();
		vehicleComponents = new VehicleComponent[level.getVehicles().length];
		for (Vehicle v : vehicles) {
			Color color;
			if (v.getId() == 1) {
				color = Color.DARKRED;
			} else {
				double brightness = (v.getId() - 2) / (float)(level.getVehicles().length - 2);
				color = Color.color(brightness / 2, brightness, brightness / 2);
			}
			VehicleComponent vc = new VehicleComponent(this, v, SCALED_CELL_SIZE, gridOffset, color);
			vehicleComponents[v.getId() - 1] = vc;
			vehiclesLayer.getChildren().add(vc.getNode());
		}
	}

	public void triggerVictory(double layoutX) {
		node.getChildren().add(modalLayer);
		// TODO: display more feedback

		double remainingDistance = Math.max(Constants.WINDOW_WIDTH - layoutX, 0);

		TranslateTransition transition = new TranslateTransition(Duration.millis(remainingDistance), vehicleComponents[0].getNode());
		transition.setByX(remainingDistance);
		transition.play();
		transition.setOnFinished(event -> {
			vehicleComponents[0].getNode().setVisible(false);
			victoryHandler.run();
		});
	}

	public void handleMovement(Movement movement) {
		history.add(movement);
		for (Movement m : history) {
			System.out.printf("%d: (%d, %d) -> (%d, %d)\n", m.getVehicle().getId(), m.getStartPosition().getX(), m.getStartPosition().getY(), m.getEndPosition().getX(), m.getEndPosition().getY());
		}
	}

	public void undoLastMove() {
		Movement lastMovement = history.remove(history.size() - 1);
		VehicleComponent vc = vehicleComponents[lastMovement.getVehicle().getId() - 1];
		vc.undo(lastMovement);
	}

	public ObservableList<Movement> getHistory() {
		return history;
	}

	@Override
	public Node getNode() {
		return node;
	}
}
