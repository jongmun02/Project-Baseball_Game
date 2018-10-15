import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.MissingFormatArgumentException;

public class Game {
	Socket socket;
	private BufferedWriter bw;
	private BufferedReader br;
	String choice;
	private int choice_int;
	String firstNum;
	String secondNum;

	public Game(Socket socket) {
		this.socket = socket;
		try {
			this.bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			this.br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public int checkRockScissorsPapers() {
		while (true) {
			try {
				String msg = null;
				bw.write("가위/바위/보 게임으로 이기는 사람이 '선'이 됩니다.\n");
				bw.write("========================" + "\n");
				bw.write("1.가위 \t2.바위\t3.보\n");
				bw.write("========================" + "\n");
				bw.write("하나를 선택하세요 :" + "\n");
				bw.flush();
				choice = br.readLine();

				if (choice.equals("1") || choice.equals("2") || choice.equals("3")) {
					choice_int = Integer.parseInt(this.choice); // 사용자가 입력한 숫자를 int형으로 변환
					break;
				} else {
					bw.write("===========================================\n");
					bw.write("1(가위),2(바위),3(보) 세 가지 숫자 중 하나를 입력해주세요. \n");
					bw.write("===========================================\n");
					bw.write("\n");
					bw.flush();
				}
			} catch (IOException e) {
			}
		}
		return choice_int;
	}

	public String inputFirstNum() {
		while (true) {
			try {
				bw.write("\n");
				bw.write("=================================\n");
				bw.write("▷ 상대가 맞추게 할 4자리 숫자를 입력하세요 :\n");
				bw.write("=================================\n");
				bw.flush();
				firstNum = br.readLine(); // 숫자 exception 체크
				if (checkPNum(firstNum) == false) {
					break;
				} else {
					bw.write("0 ~ 9까지의 중복되지 않는 네 자리 수를 입력하세요.\n");
					bw.flush();
				}
			} catch (IOException e) {
				try {
					bw.write("0 ~ 9까지의 중복되지 않는 네 자리 수를 입력하세요.\n");
					bw.flush();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		return firstNum;
	}

	public String inputSecondNum(String name) {
		while (true) {
			try {
				bw.write("\n");
				bw.write("=================================\n");
				bw.write("▶ 상대가 입력한 4자리 숫자를 맞춰 보세요 :\n");
				bw.write("=================================\n");
				bw.flush();
				secondNum = br.readLine();
				System.out.println(name + "이 입력한 숫자 : " + secondNum);
				if (checkPNum(secondNum) == false) {
					break;
				} else {
					bw.write("\n");
					bw.write("0 ~ 9까지의 중복되지 않는 네 자리 수를 입력하세요. \n");
					bw.flush();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return secondNum;
	}

	public boolean checkPNum(String pNo) {// 플레이어들이 각자 입력한 4자리 숫자를 저장해 두는 메소드
		char[] temp;
		boolean y = true;

		if (pNo.length() == 4) { // 4자리 입력여부 확인
			temp = new char[pNo.length()]; // 4자리 숫자를 담을 char형 배열 생성
			for (int i = 0; i < temp.length; i++)
				temp[i] = (pNo.charAt(i)); // 한 글자씩 끊어 배열에 저장
			for (int i = 0; i < temp.length; i++) {
				if (47 < (int) temp[i] && (int) temp[i] < 58) { // 한 글자씩 0~9까지의 숫자인지 여부 확인
					if (temp[0] != temp[1] && temp[0] != temp[2] && temp[0] != temp[3] && temp[1] != temp[2]
							&& temp[1] != temp[3] && temp[2] != temp[3]) { // 값 중복 여부 확인
						y = false;
						continue;
					} else {
						y = true;
						break;
					}
				} else {
					y = true;
					break;
				}
			}
		} else {
			y = true;
		}
		return y;
	}
}
