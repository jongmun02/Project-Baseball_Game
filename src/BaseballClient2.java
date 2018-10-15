import java.net.Socket;

public class BaseballClient2 {

	public static void main(String[] args) {

		Socket socket = null;
		UserGuide userGuide = new UserGuide(socket);
		userGuide.selectMenu();

	}// 메인끝.
}// 클래스 끝
