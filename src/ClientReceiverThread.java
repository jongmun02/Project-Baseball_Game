import java.io.*;
import java.net.Socket;

public class ClientReceiverThread implements Runnable {
	private Socket socket;

	public ClientReceiverThread(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			while (true) {
				String msg = br.readLine();
				if (msg.equals("n") || msg.equals("N") || msg.equals("ㅜ")) {
					break;
				}
				System.out.println(msg);
			}
		} catch (IOException e) {
			// TODO: handle exception
			System.out.println("연결이 종료되었습니다.");
		} finally {
			try {
				if (socket != null && !socket.isClosed()) {
					socket.close();
				}
			} catch (IOException e2) {
				e2.printStackTrace();
			}
		}
	}

}
