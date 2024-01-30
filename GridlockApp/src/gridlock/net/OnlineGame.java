package gridlock.net;

import gridlock.game.level.Level;
import gridlock.game.vehicle.Movement;
import javafx.application.Platform;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class OnlineGame {


	private enum Message {
		PARTNER_WON,
		PARTNER_ABANDONED,
	}

	private final Socket socket;
	private final Level level;
	private final Thread listenThread;
	private Runnable lossHandler;
	private ArrayList<Movement> winnerHistory;

	public OnlineGame(Socket socket, Level level) {
		this.socket = socket;
		this.level = level;
		listenThread = new Thread(this::listenLoop);
		listenThread.start();
	}

	public void declareVictory(ArrayList<Movement> history) {
		new Thread(() -> {
			try {
				ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
				out.writeObject(history);
				out.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			close();
		}).start();
	}

	private void listenLoop() {
		while (!Thread.interrupted()) {
			try {
				ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
				Object o = in.readObject();
				if (o instanceof ArrayList) {
					ArrayList<Movement> history = (ArrayList<Movement>)o;
					this.winnerHistory = history;
					if (lossHandler != null) Platform.runLater(lossHandler);
				} else if (o instanceof Message) {
					switch((Message)o) {
						case PARTNER_WON:
							break;
						case PARTNER_ABANDONED:
							System.out.println("Partner abandoned you TT_TT");
							break;
					}
				}
			} catch (IOException e) {
				break;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	public Level getLevel() {
		return level;
	}

	public ArrayList<Movement> getWinnerHistory() {
		return winnerHistory;
	}

	public void setLossHandler(Runnable lossHandler) {
		this.lossHandler = lossHandler;
	}

	public void close() {
		try {
			listenThread.interrupt();
			socket.close();
		} catch (IOException e) {
			// Don't need to do anything
		}
	}
}
