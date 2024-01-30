package gridlock.ui.components.screens;

import gridlock.game.level.Level;
import gridlock.game.level.LevelGenerator;
import gridlock.game.vehicle.Movement;
import gridlock.ui.components.AppRoot;
import gridlock.ui.components.Component;
import gridlock.ui.components.common.Toolbar;
import gridlock.ui.components.level.LevelComponent;
import gridlock.util.Constants;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

public class SoloScreen implements Component {

	private final StackPane node = new StackPane();
	private final LevelComponent levelComponent;
	private final ObservableList<Movement> history;

	public SoloScreen(int difficulty) {
		LevelGenerator levelGen = new LevelGenerator(Constants.BOARD_SIZE, difficulty);
		Level level = levelGen.generate();
		levelComponent = new LevelComponent(level, null, this::handleVictory);
		history = levelComponent.getHistory();

		Toolbar toolbar = new Toolbar(false, levelComponent);

		node.setAlignment(Pos.TOP_CENTER);
		node.getChildren().add(levelComponent.getNode());
		node.getChildren().add(toolbar.getNode());
	}

	private void handleVictory() {
		AppRoot.getInstance().goToResultsScreen(4);
		//System.out.println("VICTORY! :D");
	}

	@Override
	public Node getNode() {
		return node;
	}
}
