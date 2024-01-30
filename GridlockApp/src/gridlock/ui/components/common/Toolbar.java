package gridlock.ui.components.common;

import gridlock.ui.components.AppRoot;
import gridlock.ui.components.Component;
import gridlock.ui.components.level.LevelComponent;
import gridlock.util.Constants;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;

public class Toolbar implements Component {

	private final FlowPane node = new FlowPane(Orientation.HORIZONTAL);

	public Toolbar(boolean multiplayer, LevelComponent levelComponent) {
		BooleanBinding historyIsEmpty = Bindings.size(levelComponent.getHistory()).isEqualTo(0);

		Button returnToMenuButton = new Button(multiplayer ? "LEAVE GAME" : "RETURN TO MENU");
		returnToMenuButton.getStyleClass().add("styled-button");
		returnToMenuButton.setOnAction(event -> AppRoot.getInstance().returnToMainMenu());

		Button undoButton = new Button("UNDO LAST MOVE");
		undoButton.getStyleClass().add("styled-button");
		undoButton.setOnAction(event -> levelComponent.undoLastMove());
		undoButton.disableProperty().bind(historyIsEmpty);

		Button resetButton = new Button(multiplayer ? "RESET" : "RESTART LEVEL");
		resetButton.getStyleClass().add("styled-button");
		resetButton.setOnAction(event -> levelComponent.init());
		resetButton.disableProperty().bind(historyIsEmpty);

		node.setAlignment(Pos.CENTER);
		node.setHgap(20);
		node.setMaxHeight(Constants.TOOLBAR_HEIGHT);
		node.getChildren().add(returnToMenuButton);
		node.getChildren().add(undoButton);
		node.getChildren().add(resetButton);
		node.setPickOnBounds(false);
	}

	@Override
	public Node getNode() {
		return node;
	}
}
