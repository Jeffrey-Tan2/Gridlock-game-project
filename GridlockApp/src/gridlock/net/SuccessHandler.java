package gridlock.net;

@FunctionalInterface
public interface SuccessHandler {
	void handle(OnlineGame onlineGame);
}
