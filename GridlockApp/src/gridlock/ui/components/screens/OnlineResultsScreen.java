package gridlock.ui.components.screens;

import gridlock.net.OnlineGame;
import gridlock.ui.components.AppRoot;
import gridlock.ui.components.Component;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

public class OnlineResultsScreen implements Component {

	private StackPane node = new StackPane();
	// Layers
	private FlowPane bgLayer = new FlowPane();
	private FlowPane mainLayer = new FlowPane(Orientation.VERTICAL);

	public OnlineResultsScreen(OnlineGame onlineGame, boolean won) {
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

		Text resultsHeading = new Text("A  " + (won ? "WINNER" : "LOSER") + " IS YOU!");
		resultsHeading.getStyleClass().add("main-heading");

		Button returnToMenuButton = new Button("RETURN TO MENU");
		returnToMenuButton.getStyleClass().add("styled-button");
		returnToMenuButton.setOnAction(event -> appRoot.returnToMainMenu());

		Button playbackButton = new Button("VIEW OPPONENT'S SOLUTION");
		playbackButton.getStyleClass().add("styled-button");
		playbackButton.setOnAction(event -> appRoot.goToPlayback(onlineGame));

		mainLayer.getChildren().add(resultsHeading);
		if (won) mainLayer.getChildren().add(returnToMenuButton);
		if (!won) mainLayer.getChildren().add(playbackButton);
	}

	@Override
	public Node getNode() {
		return node;
	}
}
