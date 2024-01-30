package gridlock.net;

@FunctionalInterface
public interface FailureHandler {
	void handle(String message);
}
