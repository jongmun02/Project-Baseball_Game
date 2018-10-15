import java.net.Socket;

public class BaseballClient {

	public static void main(String[] args) {
		
		Socket socket = null;
		UserGuide userGuide = new UserGuide(socket);
		userGuide.selectMenu();

	}

}
