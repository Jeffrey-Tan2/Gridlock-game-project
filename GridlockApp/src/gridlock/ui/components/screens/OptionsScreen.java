package gridlock.ui.components.screens;

import gridlock.ui.components.AppRoot;
import gridlock.ui.components.Component;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class OptionsScreen implements Component {

	private StackPane node = new StackPane();
	// Layers
	private FlowPane bgLayer = new FlowPane();
	private FlowPane mainLayer = new FlowPane(Orientation.VERTICAL);

	private Slider difficultySlider;

	public OptionsScreen(boolean multiplayer) {
		AppRoot appRoot = AppRoot.getInstance();

		// Configure the layers
		mainLayer.setVgap(20);
		mainLayer.setAlignment(Pos.CENTER);
		bgLayer.setAlignment(Pos.CENTER);

		// Add the layers into the StackPane
		node.getChildren().add(bgLayer);
		node.getChildren().add(mainLayer);

		Image bg = new Image("/gridlock/ui/sprites/enteringmine.jpg", 600, 700, false, false);
		ImageView bgView = new ImageView();
		bgView.setImage(bg);
		bgLayer.getChildren().add(bgView);

		int lastSoloDifficulty = appRoot.getLastSoloDifficulty();
		difficultySlider = new Slider(4, 12, lastSoloDifficulty > 0 ? lastSoloDifficulty : 6);
		difficultySlider.setMinorTickCount(0);
		difficultySlider.setMajorTickUnit(1);
		difficultySlider.setSnapToTicks(true);
		difficultySlider.setShowTickMarks(true);
		difficultySlider.setShowTickLabels(true);

		// Text screenHeading = new Text((multiplayer ? "MULTIPLAYER" : "SOLO") + " LEVEL OPTIONS");
		// screenHeading.getStyleClass().add("main-heading");

		Text difficultyHeading = new Text("DIFFICULTY");
		difficultyHeading.getStyleClass().add("main-heading");

		Button backButton = new Button("< BACK");
		backButton.getStyleClass().add("styled-button");
		backButton.setOnAction(event -> appRoot.goBack());

		Button proceedButton = new Button(multiplayer ? "CONTINUE" : "START GAME");
		proceedButton.getStyleClass().add("styled-button");
		proceedButton.setOnAction(event -> {
			int difficulty = (int)difficultySlider.getValue();
			if (multiplayer) appRoot.goToHostScreen(difficulty);
			else appRoot.goToSoloScreen(difficulty);
		});

		mainLayer.getChildren().add(backButton);
		// mainLayer.getChildren().add(screenHeading);

		mainLayer.getChildren().add(difficultyHeading);
		mainLayer.getChildren().add(difficultySlider);

		mainLayer.getChildren().add(proceedButton);
	}

	@Override
	public Node getNode() {
		return node;
	}
}
