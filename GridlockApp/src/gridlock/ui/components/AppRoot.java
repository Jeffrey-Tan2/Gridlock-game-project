package gridlock.ui.components;

import gridlock.net.OnlineGame;
import gridlock.ui.components.screens.*;
import gridlock.util.Constants;
import javafx.animation.TranslateTransition;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;

import java.util.Stack;

public class AppRoot implements Component {

	private static final AppRoot instance = new AppRoot();

	public static AppRoot getInstance() {
		return instance;
	}

	private StackPane node = new StackPane();
	private Stack<Component> screenStack;
	private int lastSoloDifficulty = -1;

	private AppRoot() {}

	public void init() {
		screenStack = new Stack<>();
		node.getChildren().clear();

		Stop[] stops = new Stop[] {new Stop(0, Color.BLANCHEDALMOND), new Stop(1, Color.SANDYBROWN)};
		LinearGradient gradient = new LinearGradient(0, 0, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT, false, CycleMethod.NO_CYCLE, stops);
		Rectangle sampleBackground = new Rectangle(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT, gradient);
		node.getChildren().add(sampleBackground);

		MainMenuScreen mainMenuScreen = new MainMenuScreen();
		screenStack.push(mainMenuScreen);
		node.getChildren().add(mainMenuScreen.getNode());
	}

	public void goToOptionsScreen(boolean multiplayer) {
		OptionsScreen optionsScreen = new OptionsScreen(multiplayer);
		transitionForward(optionsScreen);
	}
	
	public void goToOnlineResultsScreen(OnlineGame onlineGame, boolean won) {
		OnlineResultsScreen onlineResultsScreen = new OnlineResultsScreen(onlineGame, won);
		transitionForward(onlineResultsScreen);
	}

	public void goToSoloScreen(int difficulty) {
		lastSoloDifficulty = difficulty;
		SoloScreen soloScreen = new SoloScreen(difficulty);
		transitionForward(soloScreen);
	}

	public void goToMultiplayerScreen(OnlineGame onlineGame) {
		MultiplayerScreen multiplayerScreen = new MultiplayerScreen(onlineGame);
		transitionForward(multiplayerScreen);
	}

	public void goToPlayback(OnlineGame onlineGame) {
		PlaybackScreen playbackScreen = new PlaybackScreen(onlineGame);
		transitionForward(playbackScreen);
	}

	public void goToHostScreen(int difficulty) {
		HostScreen hostScreen = new HostScreen(difficulty);
		transitionForward(hostScreen);
	}

	public void goToJoinScreen() {
		JoinScreen joinScreen = new JoinScreen();
		transitionForward(joinScreen);
	}
	
	public void goToResultsScreen(int numMoves) {
		ResultsScreen resultsScreen = new ResultsScreen(numMoves);
		transitionForward(resultsScreen);
	}

	// Go back one screen in the stack
	public void goBack() {
		Component from = screenStack.pop();
		Component to = screenStack.peek();
		transitionBackward(from, to);
	}

	// Skip all the way back to the first screen in the stack (the main menu)
	public void returnToMainMenu() {
		Component from = screenStack.peek();
		Component to;
		while (!((to = screenStack.peek()) instanceof MainMenuScreen)) {
			screenStack.pop();
		}
		transitionBackward(from, to);
	}

	// Push to the stack and swipe forward
	private void transitionForward(Component to) {
		Component from = screenStack.peek();
		screenStack.push(to);

		to.getNode().setTranslateX(Constants.WINDOW_WIDTH);
		node.getChildren().add(to.getNode());

		TranslateTransition transitionOut = new TranslateTransition(Constants.TRANSITION_DURATION, from.getNode());
		transitionOut.setByX(-1 * Constants.WINDOW_WIDTH);
		transitionOut.setOnFinished(event -> node.getChildren().remove(from.getNode()));
		transitionOut.play();

		TranslateTransition transitionIn = new TranslateTransition(Constants.TRANSITION_DURATION, to.getNode());
		transitionIn.setByX(-1 * Constants.WINDOW_WIDTH);
		transitionIn.play();
	}

	// Pop from the stack and swipe backward
	private void transitionBackward(Component from, Component to) {

		node.getChildren().add(to.getNode());

		TranslateTransition transitionOut = new TranslateTransition(Constants.TRANSITION_DURATION, from.getNode());
		transitionOut.setByX(Constants.WINDOW_WIDTH);
		transitionOut.setOnFinished(event -> node.getChildren().remove(from.getNode()));
		transitionOut.play();

		TranslateTransition transitionIn = new TranslateTransition(Constants.TRANSITION_DURATION, to.getNode());
		transitionIn.setByX(Constants.WINDOW_WIDTH);
		transitionIn.play();
	}

	@Override
	public StackPane getNode() {
		return node;
	}

	public int getLastSoloDifficulty() {
		return lastSoloDifficulty;
	}
}
