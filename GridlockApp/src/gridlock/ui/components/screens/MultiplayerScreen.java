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

public class MultiplayerScreen implements Component {

	private final StackPane node = new StackPane();
	private final LevelComponent levelComponent;
	private final OnlineGame onlineGame;

	public MultiplayerScreen(OnlineGame onlineGame) {
		this.onlineGame = onlineGame;
		onlineGame.setLossHandler(this::handleLoss);

		Level level = onlineGame.getLevel();
		levelComponent = new LevelComponent(level, null, this::handleVictory);

		Toolbar toolbar = new Toolbar(true, levelComponent);

		node.setAlignment(Pos.TOP_CENTER);
		node.getChildren().add(levelComponent.getNode());
		node.getChildren().add(toolbar.getNode());
	}

	private void handleVictory() {
		System.out.println("A WINNER IS YOU! :D");
		ArrayList<Movement> serializableHistory = new ArrayList<>(levelComponent.getHistory());
		onlineGame.declareVictory(serializableHistory);
		AppRoot.getInstance().goToOnlineResultsScreen(onlineGame, true);
	}

	private void handleLoss() {
		AppRoot.getInstance().goToOnlineResultsScreen(onlineGame, false);
	}

	@Override
	public Node getNode() {
		return node;
	}
}
