package gridlock.util;

import javafx.util.Duration;

public class Constants {

	public static final boolean DEBUG = false;
	public static final int BOARD_SIZE = 6;
	public static final Duration TRANSITION_DURATION = Duration.millis(200);

	// Adjust these as necessary:
	public static final int TOOLBAR_HEIGHT = 80;
	public static final int WINDOW_WIDTH = 600;

	// Derived from the above -- don't directly change this!
	public static final int WINDOW_HEIGHT = WINDOW_WIDTH;
}
