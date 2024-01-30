package gridlock.ui.components.screens;

import gridlock.ui.components.AppRoot;
import gridlock.ui.components.Component;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class MainMenuScreen implements Component {

	private StackPane node = new StackPane();

	// Layers
	private FlowPane bgLayer = new FlowPane();
	private FlowPane mainLayer = new FlowPane(Orientation.VERTICAL);

	public MainMenuScreen() {
		AppRoot appRoot = AppRoot.getInstance();

		// Configure the layers
		mainLayer.setVgap(20);
		mainLayer.setAlignment(Pos.CENTER);
		mainLayer.setColumnHalignment(HPos.CENTER);
		bgLayer.setAlignment(Pos.CENTER);

		// Add the layers into the StackPane
		node.getChildren().add(bgLayer);
		node.getChildren().add(mainLayer);

		// Set up the background
		Image bgImage = new Image("/gridlock/ui/sprites/cave.jpg", 600, 700, false, false);
		ImageView iv1 = new ImageView();
		iv1.setImage(bgImage);
		bgLayer.getChildren().add(iv1);

		// Set up the menu elements
		Text heading = new Text("MAIN MENU");
		heading.getStyleClass().add("main-heading");
		heading.getStyleClass().add("xl-heading");
		heading.setTextAlignment(TextAlignment.CENTER);

		Button soloButton = new Button("START SOLO");
		soloButton.getStyleClass().add("styled-button");
		soloButton.setOnAction(event -> appRoot.goToOptionsScreen(false));

		Button hostButton = new Button("HOST ONLINE GAME");
		hostButton.getStyleClass().add("styled-button");
		hostButton.setOnAction(event -> appRoot.goToOptionsScreen(true));

		Button joinButton = new Button("JOIN ONLINE GAME");
		joinButton.getStyleClass().add("styled-button");
		joinButton.setOnAction(event -> appRoot.goToJoinScreen());

		Button exitButton = new Button("QUIT");
		exitButton.getStyleClass().add("styled-button");
		exitButton.setOnAction(event -> {
			System.out.println("Calling Platform.exit()");
			Platform.exit();
		});

		// Add the menu elements into the main layer
		mainLayer.getChildren().add(heading);
		mainLayer.getChildren().add(soloButton);
		mainLayer.getChildren().add(hostButton);
		mainLayer.getChildren().add(joinButton);
		mainLayer.getChildren().add(exitButton);
	}

	@Override
	public Node getNode() {
		return node;
	}
}
