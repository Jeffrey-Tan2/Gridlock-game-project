package gridlock.ui.components.screens;

import gridlock.net.Client;
import gridlock.net.OnlineGame;
import gridlock.ui.components.AppRoot;
import gridlock.ui.components.Component;
import gridlock.util.Constants;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class JoinScreen implements Component {

	private final SimpleBooleanProperty joining = new SimpleBooleanProperty(false);
	private final StringProperty status = new SimpleStringProperty();
	private Client client;

	// UI nodes
	private final StackPane node = new StackPane();
	private final Pane bgLayer = new Pane();
	private final FlowPane uiLayer = new FlowPane(Orientation.VERTICAL);
	private final TextField addressField = new TextField();
	private final TextField portField = new TextField();

	public JoinScreen() {
		Image bgImage = new Image("/gridlock/ui/sprites/cave.jpg", 600, 700, false, false);
		ImageView iv1 = new ImageView();
		iv1.setImage(bgImage);
		bgLayer.getChildren().add(iv1);
		
		Button backButton = new Button("< BACK");
		backButton.getStyleClass().add("styled-button");
		backButton.setOnAction(event -> AppRoot.getInstance().goBack());

		Text headingText = new Text("JOIN AN ONLINE GAME");
		headingText.setTextAlignment(TextAlignment.CENTER);
		headingText.getStyleClass().add("main-heading");

		Text statusText = new Text();
		statusText.textProperty().bind(status);
		statusText.getStyleClass().add("text");

		Button actionButton = new Button();
		actionButton.getStyleClass().add("styled-button");
		actionButton.textProperty().bind(
			Bindings.when(joining).then("CANCEL").otherwise("JOIN")
		);
		actionButton.setOnAction(event -> {
			if (joining.getValue()) cancelJoining();
			else startJoining();
		});

		uiLayer.getChildren().add(backButton);
		uiLayer.getChildren().add(headingText);
		uiLayer.getChildren().add(addressField);
		uiLayer.getChildren().add(portField);
		uiLayer.getChildren().add(statusText);
		uiLayer.getChildren().add(actionButton);

		node.getChildren().add(bgLayer);
		node.getChildren().add(uiLayer);

		if (Constants.DEBUG) {
			client = new Client("localhost", 1337, this::handleSuccess, this::handleFailure);
		}
	}

	private void startJoining() {
		joining.set(true);
		status.setValue("Connecting...");
		try {
			int port = Integer.parseInt(String.valueOf(portField.getCharacters()));
			String hostname = String.valueOf(addressField.getCharacters());
			client = new Client(hostname, port, this::handleSuccess, this::handleFailure);
		} catch (NumberFormatException e) {
			status.setValue("Invalid port number");
			joining.set(false);
		}
	}

	private void cancelJoining() {
		if (client != null) client.cancel();
		status.setValue("");
		joining.set(false);
	}

	private void handleSuccess(OnlineGame onlineGame) {
		AppRoot.getInstance().goToMultiplayerScreen(onlineGame);
	}

	private void handleFailure(String message) {
		status.setValue("Failed to join game: " + message);
		joining.set(false);

		if (Constants.DEBUG) {
			try {
				Thread.sleep(100);
				client = new Client("localhost", 1337, this::handleSuccess, this::handleFailure);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public Node getNode() {
		return node;
	}
}
