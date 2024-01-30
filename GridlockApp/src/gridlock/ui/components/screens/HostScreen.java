package gridlock.ui.components.screens;

import gridlock.game.level.Level;
import gridlock.game.level.LevelGenerator;
import gridlock.net.OnlineGame;
import gridlock.net.Server;
import gridlock.ui.components.AppRoot;
import gridlock.ui.components.Component;
import gridlock.util.Constants;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class HostScreen implements Component {

	private final Level level;
	private final SimpleBooleanProperty hosting = new SimpleBooleanProperty(false);
	private final StringProperty status = new SimpleStringProperty();
	private Server server;

	// UI nodes
	private final FlowPane node = new FlowPane(Orientation.VERTICAL);
	private final TextField portField = new TextField();

	public HostScreen(int difficulty) {
		Button backButton = new Button("< BACK");
		backButton.getStyleClass().add("styled-button");
		backButton.setOnAction(event -> AppRoot.getInstance().goBack());

		Text headingText = new Text("HOST AN ONLINE GAME");
		headingText.setTextAlignment(TextAlignment.CENTER);
		headingText.getStyleClass().add("main-heading");

		Text statusText = new Text();
		statusText.textProperty().bind(status);

		Button actionButton = new Button();
		actionButton.getStyleClass().add("styled-button");
		actionButton.textProperty().bind(
			Bindings.when(hosting).then("CANCEL").otherwise("START HOSTING")
		);
		actionButton.setOnAction(event -> {
			if (hosting.getValue()) cancelHosting();
			else startHosting();
		});

		String hostAddress;
		try {
			hostAddress = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			hostAddress = "Could not determine host address";
			e.printStackTrace();
		}
		System.out.println(hostAddress);
		Text addressText = new Text(hostAddress);

		node.getChildren().add(backButton);
		node.getChildren().add(headingText);
		node.getChildren().add(addressText);
		node.getChildren().add(portField);
		node.getChildren().add(statusText);
		node.getChildren().add(actionButton);

		LevelGenerator levelGen = new LevelGenerator(Constants.BOARD_SIZE, difficulty);
		level = levelGen.generate();

		if (Constants.DEBUG) {
			server = new Server(1337, level, this::handleSuccess, this::handleFailure);
		}
	}

	private void startHosting() {
		hosting.set(true);
		status.setValue("Waiting for a partner...");
		CharSequence portString = portField.getCharacters();
		try {
			int port = Integer.parseInt(String.valueOf(portString));
			server = new Server(port, level, this::handleSuccess, this::handleFailure);
		} catch (NumberFormatException e) {
			status.setValue("Invalid port number");
			hosting.set(false);
		}
	}

	private void cancelHosting() {
		if (server != null) server.cancel();
		status.setValue("");
		hosting.set(false);
	}

	private void handleSuccess(OnlineGame onlineGame) {
		AppRoot.getInstance().goToMultiplayerScreen(onlineGame);
	}

	private void handleFailure(String message) {
		status.setValue("Failed to host game: " + message);
		hosting.set(false);
	}

	@Override
	public Node getNode() {
		return node;
	}
}
