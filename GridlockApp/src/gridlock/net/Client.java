package gridlock.net;

import gridlock.game.level.Level;
import javafx.application.Platform;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class Client {

	private final String hostname;
	private final int port;
	private final SuccessHandler successHandler;
	private final FailureHandler failureHandler;
	private Thread connectThread;
	private Socket socket;

	public Client(String hostname, int port, SuccessHandler successHandler, FailureHandler failureHandler) {
		this.hostname = hostname;
		this.port = port;
		this.successHandler = successHandler;
		this.failureHandler = failureHandler;

		connectThread = new Thread(this::connectLoop);
		connectThread.setDaemon(true);
		connectThread.start();
	}

	public void cancel() {
		if (connectThread != null) connectThread.interrupt();
		if (socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
				// Don't need to do anything
			}
		}
	}

	private void connectLoop() {
		socket = new Socket();
		while (!Thread.interrupted()) {
			try {
				socket.connect(new InetSocketAddress(hostname, port), 5000);

				System.out.println("Client: waiting for level...");
				ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
				Level level = (Level)in.readObject();
				System.out.printf("Client: received %s\n", level);

				String ack = "ACK";
				ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
				out.writeObject(ack);
				System.out.printf("Client: sent %s\n", ack);

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
