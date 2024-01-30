package gridlock.ui.components.screens;

import gridlock.game.level.Level;
import gridlock.game.vehicle.Movement;
import gridlock.net.OnlineGame;
import gridlock.ui.components.AppRoot;
import gridlock.ui.components.Component;
import gridlock.ui.components.common.Toolbar;
import gridlock.ui.components.level.LevelComponent;
import gridlock.util.Constants;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.ArrayList;

public class PlaybackScreen implements Component {

	private final StackPane node = new StackPane();
	private final LevelComponent levelComponent;
	private final OnlineGame onlineGame;

	public PlaybackScreen(OnlineGame onlineGame) {
		this.onlineGame = onlineGame;

		Level level = onlineGame.getLevel();
		levelComponent = new LevelComponent(level, onlineGame.getWinnerHistory(), this::handleVictory);

		// Toolbar toolbar = new Toolbar(true, levelComponent);

		node.setAlignment(Pos.TOP_CENTER);
		node.getChildren().add(levelComponent.getNode());
		// node.getChildren().add(toolbar.getNode());
	}

	private void handleVictory() {
		onlineGame.close();
		AppRoot.getInstance().returnToMainMenu();
	}

	@Override
	public Node getNode() {
		return node;
	}
}
