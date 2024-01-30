package gridlock.net;

import gridlock.game.level.Level;
import javafx.application.Platform;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class Server {

	private final int port;
	private final Level level;
	private final SuccessHandler successHandler;
	private final FailureHandler failureHandler;
	private ServerSocket serverSocket = null;
	private Thread listenThread = null;

	public Server(int port, Level level, SuccessHandler successHandler, FailureHandler failureHandler) {
		this.port = port;
		this.level = level;
		this.successHandler = successHandler;
		this.failureHandler = failureHandler;

		try {
			serverSocket = new ServerSocket(port);
			listenThread = new Thread(this::listenLoop);
			listenThread.setDaemon(true);
			listenThread.start();
		} catch (IOException e) {
			failureHandler.handle(e.getMessage());
		}
	}

	public void cancel() {
		if (listenThread != null) listenThread.interrupt();
		if (serverSocket != null) {
			try {
				serverSocket.close();
			} catch (IOException e) {
				// Don't need to do anything
			}
		}
	}

	private void listenLoop() {
		while (!Thread.interrupted()) {
			try {
				Socket socket = serverSocket.accept();

				ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
				out.writeObject(level);
				System.out.printf("Server: sent %s\n", level);

				System.out.println("Server: waiting for ack...");
				ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
				String ack = (String)in.readObject();
				System.out.printf("Server: received %s\n", ack);

				OnlineGame onlineGame = new OnlineGame(socket, level);
				Platform.runLater(() -> successHandler.handle(onlineGame));
				break;
			} catch (Exception e) {
				if (!Thread.interrupted()) Platform.runLater(() -> failureHandler.handle(e.getMessage()));
				break;
			}
		}
	}
}
