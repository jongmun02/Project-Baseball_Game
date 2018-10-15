import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ClientSenderThread implements Runnable {
	private Socket socket;
	String name;

	public ClientSenderThread(Socket socket, String nickName) {
		this.socket = socket;
		this.name = nickName;
	}

	@Override
	public void run() {

		BufferedWriter bw;
		try {
			bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			Scanner input = new Scanner(System.in);

			bw.write(name + "\n");
			bw.flush();

			while (true) {
				String msg = input.nextLine();
				if (msg.equals("n") || msg.equals("N") || msg.equals("ㅜ")) {
					break;
				}
				bw.write(msg + "\n");
				bw.flush();
			} // while 끝.
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("연결이 끊어졌습니다.");
		} finally {
			try {
				if (socket != null && !socket.isClosed()) {
					socket.close();
				}
			} catch (IOException e2) {
				e2.printStackTrace();
			}
		}
	}// run

}
