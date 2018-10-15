import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class UserGuide {
	Socket socket;
	int choice;
	String choice2;
	String yOrN;
	char[] pNo;
	char[] guessNo;
	int ball;
	int strike;
	String nickName;
	BufferedWriter log;
	BufferedReader br;
	BufferedWriter bw;


	public UserGuide(Socket socket) {
		this.socket = socket;
	}

	public void callTxt() {// 게임 설명 text파일을 불러오는 코드

		BufferedReader in = null;
		BufferedWriter out = null;
		try {
			in = new BufferedReader(new InputStreamReader(new FileInputStream("src/게임룰.txt"), "euc-kr"));
			String c;
			while ((c = in.readLine()) != null) {
				System.out.println(c);
			}
		} catch (IOException e) {
			e.printStackTrace();
			// TODO: handle exception
		} finally {
			try {
				if (in != null)
					in.close();
				if (out != null)
					out.close();
			} catch (Exception e) {
				e.printStackTrace();
				// TODO: handle exception
			}
		}
	}

	public void callTxt2() {// 두번째 이후 반복 시 callTxt 실행

		BufferedReader in = null;
		BufferedWriter out = null;
		try {
			in = new BufferedReader(new InputStreamReader(new FileInputStream("src/게임룰.txt"), "euc-kr"));
			out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			String c;
			while ((c = in.readLine()) != null) {
				out.write(c + "\n");
				out.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
			// TODO: handle exception
		}
	}// callTxt2끝

	public void selectMenu() {
		int count = 0;
		callTxt();
		while (true) {
			try {
				System.out.println("메뉴를 선택하세요.");
				System.out.println("=======================");
				System.out.println("1.경기장 입장\t2.종료");
				System.out.println("=======================");
				System.out.print(">>");
				Scanner input = new Scanner(System.in);
				choice = input.nextInt();
				if (choice == 1) {
					System.out.print("사용하실 닉네임을 입력 해 주세요.\n>>");
					Scanner scan = new Scanner(System.in);
					nickName = scan.nextLine();
					connectToGame();
					break;
				} else if (choice == 2) {
					System.out.println("프로그램을 종료합니다. GoodBye~!");
					break;
				} else {
					if (count == 2) {
						System.out.println();
						System.out.println("적당히 하시죠?^^");
						System.out.println();
						count++;
					} else if (count > 2) {
						System.out.println();
						System.out.println("그만 하시고 게임이나 하세요~^^");
						System.out.println();
						count = 0;
					}
					else {
						System.out.println();
					System.out.println("메뉴선택은 1번 아니면 2번 입니다. 좀 보고 입력 하세요.^^");
					System.out.println();
					count++;
					}
				}
			} catch (InputMismatchException e) {
				System.out.println();
				System.out.println("메뉴는 숫자 1 또는 2 한자리로만 입력 가능합니다.");
				System.out.println();
				count++;
			}
		} // while끝
	}// selectMenu메소드 끝.

	public void selectMenu2() {
		int count = 0;
		try {
			bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			while (true) {
				Scanner input = new Scanner(System.in);
				bw.write("메뉴를 선택하세요.\n");
				bw.write("==================\n");
				bw.write("1.경기장 입장\t2.종료\n");
				bw.write("==================\n");
				bw.write(">>\n");
				bw.flush();
				choice2 = br.readLine();
				if (choice2.equals("1")) {
					bw.write("사용하실 닉네임을 입력 해 주세요.\n>>\n");
					bw.flush();
					Scanner scan = new Scanner(System.in);
					break;
				} else if (choice2.equals("2")) {
					bw.write("프로그램을 종료합니다. \n");
					bw.flush();
					break;
				} else {
					if (count == 3) {
						bw.write("적당히 하시죠?^^ \n");
						bw.write("\n");
						bw.flush();
						count++;
						System.out.println(count);
					} else if (count > 3) {
						bw.write("그만 하시고 게임이나 하세요~^^ \n");
						bw.write("\n");
						bw.flush();
						count = 0;
					}
					bw.write("메뉴선택은 1번 아니면 2번 입니다. 좀 보고 입력 하세요.^^\n");
					bw.write("\n");
					bw.flush();
					count++;
				}
			}
		} catch (InputMismatchException | IOException e) {
			try {
				bw.write("메뉴는 숫자 1 이나 2로만 입력 가능합니다. \n");
				bw.flush();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	} // while끝

	// selectMenu메소드 끝.
	public void connectToGame() {
		while (true) {
			Scanner input = new Scanner(System.in);
			System.out.print("접속하실 ip주소를 입력 해 주세요.\n>>");
			try {
				String ip = input.nextLine();
				socket = new Socket(ip, 5001);
				// log = new BufferedWriter(new FileWriter(l.getAdate() + " Log", true));// 로그
				// 파일생성
				// l = new Log(socket,nickName);
				ClientSenderThread clientSenderThread = new ClientSenderThread(socket, nickName);
				ClientReceiverThread clientReceiverThread = new ClientReceiverThread(socket);
				Thread t1 = new Thread(clientSenderThread);
				Thread t2 = new Thread(clientReceiverThread);
				t1.start();
				t2.start();
				break;
			} catch (IOException | NoSuchElementException e) {
				System.out.println("잘못 입력하셨습니다. 똑바로 입력 하세요 제발...");

			}

		} // while 끝
	}// goToTheGame메소드 끝.

	// public String getNickName() {
	// return nickName;
	// }

}// class끝
