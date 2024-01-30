package gridlock.ui.components.screens;

import gridlock.ui.components.AppRoot;
import gridlock.ui.components.Component;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class ResultsScreen implements Component {

	private StackPane node = new StackPane();

	// Layers
	private Pane bgLayer = new Pane();
	private FlowPane mainLayer = new FlowPane(Orientation.VERTICAL);

	public ResultsScreen(int numMoves) {
		AppRoot appRoot = AppRoot.getInstance();

		// Configure the layers
		mainLayer.setVgap(20);
		mainLayer.setAlignment(Pos.CENTER);

		// Add the layers into the StackPane
		node.getChildren().add(bgLayer);
		node.getChildren().add(mainLayer);

		// Set up the background
		Image bg = new Image("/gridlock/ui/sprites/enteringmine.jpg", 600, 700, false, false);
		ImageView bgView = new ImageView();
		bgView.setImage(bg);
		bgLayer.getChildren().add(bgView);

		// Set up the menu elements
		Text heading = new Text("A WINNER IS YOU! :D");
		heading.setTextAlignment(TextAlignment.CENTER);
		heading.getStyleClass().add("main-heading");

		Text movesText = new Text("FINISHED IN " + numMoves + " MOVES!");
		movesText.setTextAlignment(TextAlignment.CENTER);
		movesText.getStyleClass().add("main-heading");

		Button soloButton = new Button("NEW LEVEL");
		soloButton.getStyleClass().add("styled-button");
		soloButton.setOnAction(event -> appRoot.goToSoloScreen(appRoot.getLastSoloDifficulty()));

		// StyledButton retryButton = new StyledButton("Retry Level");
		// retryButton.setOnAction(event -> {});

		Button changeDifficultyButton = new Button("CHANGE DIFFICULTY");
		changeDifficultyButton.getStyleClass().add("styled-button");
		changeDifficultyButton.setOnAction(event -> appRoot.goToOptionsScreen(false));

		Button returnToMenuButton = new Button("RETURN TO MENU");
		returnToMenuButton.getStyleClass().add("styled-button");
		returnToMenuButton.setOnAction(event -> appRoot.returnToMainMenu());

		// Add the menu elements into the main layer
		mainLayer.getChildren().add(heading);
		mainLayer.getChildren().add(movesText);
		mainLayer.getChildren().add(soloButton);
		// mainLayer.getChildren().add(retryButton);
		mainLayer.getChildren().add(changeDifficultyButton);
		mainLayer.getChildren().add(returnToMenuButton);
	}

	@Override
	public Node getNode() {
		return node;
	}
}