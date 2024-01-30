package gridlock;

import gridlock.game.level.Level;
import gridlock.game.level.LevelGenerator;
import gridlock.net.OnlineGame;
import gridlock.ui.components.AppRoot;
import gridlock.util.Constants;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.Socket;
import java.util.List;

public class GridlockApp extends Application {

	@Override
	public void start(Stage stage) {
		AppRoot appRoot = AppRoot.getInstance();
		Scene scene = new Scene(appRoot.getNode(), Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
		scene.getStylesheets().add("/gridlock/ui/styles/styles.css");
		appRoot.init();

		stage.setTitle("Gridlock!");
		stage.setScene(scene);
		stage.show();

		// Skip to specific screens based on command-line args (for dev purposes)
		List<String> args = getParameters().getRaw();
		if (args.size() >= 1) {
			String s = args.get(0);
			switch (s) {
				case "host":
					appRoot.goToHostScreen(2);
					break;
				case "join":
					appRoot.goToJoinScreen();
					break;
				case "solo":
					appRoot.goToSoloScreen(2);
					break;
				case "multiplayer":
					LevelGenerator levelGen = new LevelGenerator(Constants.BOARD_SIZE, 2);
					Level level = levelGen.generate();
					appRoot.goToMultiplayerScreen(new OnlineGame(new Socket(), level));
					break;
			}
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
