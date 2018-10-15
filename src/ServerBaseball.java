import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

class ServerManager {
	public List<Socket> socketList;
	public ArrayList<Integer> arr = new ArrayList<Integer>();
	public ArrayList<String> nameArr = new ArrayList<String>();
	public HashMap<Integer, String> map = new HashMap<Integer, String>();
	public HashMap<String, Socket> nameSocket = new HashMap<String, Socket>();
	public HashMap<Socket, String> compare1 = new HashMap<Socket, String>();
	public HashMap<String, String> compare2 = new HashMap<String, String>();
	public HashMap<String, String> forWait = new HashMap<String, String>();
	public HashMap<String, String> isBoolean = new HashMap<String, String>();
	public int readyCount = 0;
	public char[] temp = new char[4];
	boolean getNum = false;
	int count = 1;
	public String winnerName, loserName;
	public String nameTemp;
	public boolean check = true;

	public ServerManager(Socket socket) {
		socketList = new ArrayList<Socket>();
	}

	public void addSocket(Socket socket) {
		this.socketList.add(socket);
		new Thread(new ServerThread(socket)).start();
	}

	///////////////////////////////////////////////////////////////////////////////////

	public class ServerThread implements Runnable {
		private Socket socket;
		private String name;
		private BufferedReader br;
		private BufferedWriter bw;
		private Game game;
		private UserGuide userguide = new UserGuide(socket);
		private boolean isSecTime = true;

		BufferedWriter log;// 로그 파일 생성 변수.

		public ServerThread(Socket socket) {
			this.socket = socket;
			try {
				bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
				br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				game = new Game(socket);
				userguide = new UserGuide(socket);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		///////////////////////////////////////////////////////////////////////////////////
		@Override
		public void run() {
			while (true) {
				try {
					log = new BufferedWriter(new FileWriter(getAdate() + " Log", true));// 로그 파일생성

					if (isSecTime != true)
						secGameCheck();
					chat(); // 채팅 메소드
					game1(); // 가위바위보 메소드
					game2(); // 야구게임 메소드(처음 숫자 입력 부분)
					game3(); // 야구게임 메소드(숫자 비교 부분)
					check = isCheck();
					if (check != true)
						break;

				} catch (IOException | NullPointerException e) {
					isSecTime = true;
				}
			}
		}
		///////////////////////////////////////////////////////////////////////////////////

		public void secGameCheck() {
			userguide.callTxt2();
			userguide.selectMenu2();
		}

		public void chat() {
			try {
				name = br.readLine();
				setName(getName());
				bw.write("\n");
				sendNotMe(getName() + "님이 입장하셨습니다.\n");
				bw.write("\n");
				bw.flush();
				System.out.println(getName() + "님 입장");
				log.write(getTime() + "\n");// 로그에씀.
				log.write(getName() + "님 입장\n");
				log.write("\n");
				log.flush();
				nameArr.add(getName());
				for (int i = 0; i >= 0; i++) {
					if (socketList.size() < 2) {
						if (i == 0) {
							bw.write("상대를 찾는 중입니다....\n");
							bw.flush();
						}
						threadSleep(1000);
					} else if (socketList.size() == 2) {// 두명 입장하면 채팅시작.
						bw.write("\n");
						bw.write("+---현재 참여 플레이어---+\n");
						bw.write("   " + nameArr.get(0) + "님\n");
						bw.write("   " + nameArr.get(1) + "님\n");
						bw.write("+-----------------+\n");
						bw.write("\n");
						bw.write("=========================================\n");
						bw.write("상대와 메세지를 자유롭게 주고 받으세요~!\n");
						bw.write("★ 게임을 시작하고 싶으시면, 'ready'를 입력 해 주세요!\n상대도 'ready'를 입력하면 게임이 시작됩니다.★ \n");
						bw.write("=========================================\n");
						bw.flush();
						while (true) {
							bw.write("메세지 입력 :\n");
							bw.flush();
							String msg = br.readLine();
							System.out.println(getName() + " : " + msg);
							if (!msg.equals("ready"))
								sendNotMe(msg);
							if (msg.equals("ready")) { // ready 입력 여부 체크하는 조건문
								sendNotMe(getName() + "님 ready완료.");
								readyCount++;
								break;
							}
							// if

						} // inner while끝
						break;
					}
				} // while끝

				while (true) {
					if (readyCount < 2) {
						bw.write("상대방의 ready를 기다립니다.\n");
						bw.flush();
						threadSleep(5000);
					} else if (readyCount >= 2) {
						bw.write("\n");
						bw.write("플레이어 모두 'ready'완료!  ٩(･ิᴗ･ิ๑)۶ \n");
						bw.write("'선'정하기 게임으로 Go Go!!\n");
						bw.write("\n");
						bw.flush();
						threadSleep(1000);
						break;
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}// chat메소드 끝

		///////////////////////////////////////////////////////////////////////////////////////////////////////////////

		/// 가위바위보 부분
		public void game1() {
			try {
				while (true) {
					int choice_int = game.checkRockScissorsPapers();
					arr.add(choice_int); // 소켓 리스트에 저장하듯이 사용자가 입력한 가위바위보 중 하나를 list에 저장
					for (int i = 0; i >= 0; i++) {
						System.out.println(arr.size());
						if (arr.size() >= 2) {
							break;
						} else if (arr.size() < 2) {
							if (i == 0) {
								bw.write("상대가 입력 중입니다... 잠시 기다려주세요...☆\n");
								bw.flush();
								threadSleep(3000);
							}
						}
					}
					String msg = RockScissorsPapers(choice_int, getName()); // 가위바위보 연산하는 메소드 호출
					System.out.println(msg);
					// 가위바위보 결과값 출력
					if (msg.equals("비김\n")) {
						bw.write("\n");
						bw.write("ಠಒ್ದಠ! 비겼네요...ㅠㅠ 다시한번 Go Go!! \n");
						bw.write("\n");
						bw.flush();
						threadSleep(3000);
					} else {
						bw.write("\n");
						bw.write(msg);
						bw.write("\n");
						bw.write("°º¤ø,¸¸,ø¤º°`°º¤ø,¸,ø¤°º¤ø,¸¸,ø¤º°`°º¤ø,¸\n");
						bw.write("본격적인 게임이 시작됩니다!! 후비고오오우~~!!!\n");
						bw.write("°º¤ø,¸¸,ø¤º°`°º¤ø,¸,ø¤°º¤ø,¸¸,ø¤º°`°º¤ø,¸\n");
						bw.flush();
						if (getName().equals(winnerName)) {
							isBoolean.put(winnerName, "true");
							break;
						} else if (getName().equals(loserName)) {
							isBoolean.put(loserName, "true");
							break;
						}
					}
					arr.clear();
				} // while끝
			} catch (IOException e) {
				e.fillInStackTrace();
			}

		}// game1메소드끝

		/////////////////////////////////////////////////////////////////////

		// 각 유저의 첫번째 숫자를 받아 저장하는 메소드
		public void game2() {
			try {
				for (int i = 0; i >= 0; i++) {
					if (getName().equals(loserName)) {
						if (isBoolean.get(loserName).equals("true")) {
							if (i == 0) {
								bw.write("상대가 입력 중입니다... 잠시 기다려주세요...☆\n");
								bw.flush();
							}
							threadSleep(3000);
						} else
							break;
					} else
						break;
				}
				// bw.write("----٩(◕‿◕)۶ 본격적으로 게임을 시작 해 볼까요!\n경기장으로 입장 합니다!!!----\n");
				// bw.write("\n");
				// bw.flush();

				String firstNum = game.inputFirstNum(); // 숫자 4자리가 형식에 맞는지 체크하는 Game 클래스 내 메소드
				compare1.put(socket, firstNum);
				isBoolean.put(loserName, "false");
				for (int i = 0; i >= 0; i++) {
					if (compare1.size() >= 2) {
						if (getName().equals(winnerName))
							break;
						else if (getName().equals(loserName)) {
							isBoolean.put(loserName, "true");
							break;
						}
					} else if (compare1.size() < 2) {
						if (i == 0) {
							bw.write("상대가 입력 중입니다... 잠시 기다려주세요...☆\n");
							bw.flush();
						}
						threadSleep(3000);
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public void game3() {
			Game game = new Game(socket);
			String finalWinner = null;
			BufferedReader in_2 = null;
			BufferedReader in_3 = null;
			try {
				while (true) {
					if (getName().equals(loserName)) {
						for (int i = 0; i >= 0; i++) {
							if (isBoolean.get(loserName).equals("true")) {
								if (i == 0) {
									bw.write("상대가 입력 중입니다... 잠시 기다려주세요...☆\n");
									bw.flush();
								}
								threadSleep(2000);
							} else if (isBoolean.get(loserName).equals("false")) {
								break;
							} else if (isBoolean.get(loserName).equals("reSet")) {
								in_2 = new BufferedReader(
										new InputStreamReader(new FileInputStream("src/패배.txt"), "euc-kr"));
								String st;
								while ((st = in_2.readLine()) != null) {
									bw.write(st + "\n");
									bw.flush();
								}
								bw.write("아이고.. 상대방이 먼저 맞춰버렸네요..\n");
								bw.write("패배 하셨습니다... ｡･ﾟﾟ*(>д<)*ﾟﾟ･｡\n");
								bw.flush();
								threadSleep(1000);
								break;
							}
						} // while
					}

					else if (getName().equals(winnerName)) {
						for (int i = 0; i >= 0; i++) {
							if (isBoolean.get(winnerName).equals("false")) {
								if (i == 0) {
									bw.write("상대가 입력 중입니다... 잠시 기다려주세요...☆\n");
									bw.flush();
								}
								threadSleep(2000);
							} else if (isBoolean.get(winnerName).equals("true")) {
								break;
							} else if (isBoolean.get(winnerName).equals("reSet")) {
								in_3 = new BufferedReader(
										new InputStreamReader(new FileInputStream("src/패배.txt"), "euc-kr"));
								String st;
								while ((st = in_3.readLine()) != null) {
									bw.write(st + "\n");
									bw.flush();
								}
								bw.write("아이고.. 상대방이 먼저 맞춰버렸네요..\n");
								bw.write("패배 하셨습니다... ｡･ﾟﾟ*(>д<)*ﾟﾟ･｡\n");
								bw.flush();
								threadSleep(2000);
								break;
							}
						}
					}

					if (isBoolean.get(loserName).equals("reSet") || isBoolean.get(winnerName).equals("reSet"))
						break;

					String secondNum = game.inputSecondNum(getName());
					compare2.put(getName(), secondNum);
					String game2Msg = checkGuessNum(compare1, compare2);
					String name1 = null, name2 = null;
					BufferedReader in = null;

					if (game2Msg.equals("\nSTRIKE : 4\tBALL : 0\n")) {
						finalWinner = getName();
						bw.write(game2Msg);
						bw.write("\n");
						bw.flush();
						in = new BufferedReader(new InputStreamReader(new FileInputStream("src/승리.txt"), "euc-kr"));
						String st;
						while ((st = in.readLine()) != null) {
							bw.write(st + "\n");
							bw.flush();
						}
						bw.write("-----(함성소리) ☆★☆★☆★  4 ST~~~RIKE!!!☆★☆★☆★ 띠로리 띳또리~~~\n");
						bw.write(" ヾ(o✪‿✪o)ｼ  " + getName() + "님의 승리!!\n");
						bw.write("\n");
						bw.flush();
						if (getName().equals(winnerName)) {
							isBoolean.put(loserName, "reSet");

						} else if (getName().equals(loserName)) {
							isBoolean.put(winnerName, "reSet");
						}

						if (nameArr.get(0).equals(finalWinner)) {
							name1 = finalWinner;
							name2 = nameArr.get(1);
						} else if (nameArr.get(1).equals(finalWinner)) {
							name1 = finalWinner;
							name2 = nameArr.get(0);
						}

						log.write(getTime() + "\n");// 로그씀.
						log.write(name1 + "님 승리\n");
						log.write(name2 + "님 패배\n");
						log.write("\n");
						log.flush();
						break;
					} else {
						bw.write(game2Msg);
						bw.write("\n");
						bw.flush();
						if (isBoolean.get(winnerName).equals("true")) {
							isBoolean.put(loserName, "false");
							isBoolean.put(winnerName, "false");
						} else if (isBoolean.get(winnerName).equals("false")) {
							isBoolean.put(loserName, "true");
							isBoolean.put(winnerName, "true");
						}
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public boolean isCheck() {
			boolean check = true;
			try {
				while (true) {
					bw.write("\n");
					bw.write(" ฅ^._.^ฅ 한판 더?!(y/n) \n");
					bw.flush();
					String yOrn = br.readLine();

					if (yOrn.equals("y") || yOrn.equals("Y") || yOrn.equals("ㅛ")) {
						check = true;
						break;
					} else if (yOrn.equals("N") || yOrn.equals("n") || yOrn.equals("ㅜ")) {
						check = false;
						bw.write(yOrn + "\n");
						bw.flush();
						break;
					} else {
						bw.write("ヽ(　￣д￣)ノ 'y'or'n'으로만 입력 해 주세요!\n");
						bw.write("\n");
						bw.flush();
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			initialization();
			return check;
		}

		// 상대방의 처음 숫자와 내가 추측한 숫자를 비교하는 메소드
		public String checkGuessNum(HashMap<Socket, String> compare1, HashMap<String, String> compare2) {
			// 플레이어들이 추측한
			// 4자릴 숫자를
			// 처음 입력한
			// 숫자와 비교하는
			// 메소드
			char[] firstNum = new char[4];
			char[] secondNum = new char[4];
			String a;
			String b;
			int s = 0;
			int ball = 0, strike = 0;

			for (int k = 0; k < socketList.size(); k++) {
				if (socketList.get(k) != socket) {
					s = k;
				}
			}
			a = compare1.get(socketList.get(s));
			b = compare2.get(getName());

			for (int q = 0; q < 4; q++) {
				firstNum[q] = a.charAt(q);
				secondNum[q] = b.charAt(q);
			}

			for (int j = 0; j < 4; j++) {
				for (int i = 0; i < 4; i++) {
					if (firstNum[j] == secondNum[i]) {
						if (j == i) {
							strike++;
						} else
							ball++;
					}
				}
			} // for문 끝
			count++; // 턴 수 계산
			return "\nSTRIKE : " + strike + "\t" + "BALL : " + ball + "\n";
		}// checkGuessNum 메소드 끝

		// 채팅 시 내가 입력한 채팅을 남들에게 전송하는 메소드
		public void sendNotMe(String msg) {
			Socket tmpSocket = null;
			for (int i = 0; i < socketList.size(); i++) {
				try {
					tmpSocket = socketList.get(i);
					BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(tmpSocket.getOutputStream()));
					if (tmpSocket != socket) {
						bw.write(getName() + " : " + msg + "\n");
						bw.flush();
					}
				} catch (IOException e) {
					if (tmpSocket != null) {
						socketList.remove(tmpSocket);
						System.out.println("========현재참여자========");
						for (Socket s : socketList)
							System.out.println(s.getRemoteSocketAddress());
						System.out.println("===========================");
					}
				}
			}
		}

		// 서버가 모든 플레이어에게 전송
		public void sendAll(String msg) {
			Socket tmpSocket = null;
			for (int i = 0; i < socketList.size(); i++) {
				try {
					tmpSocket = socketList.get(i);
					BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(tmpSocket.getOutputStream()));
					bw.write("SERVER : " + msg + "\n");
					bw.flush();
				} catch (IOException e) {
					if (tmpSocket != null) {
						socketList.remove(tmpSocket);
						System.out.println("========현재참여자========");
						for (Socket s : socketList)
							System.out.println(s.getRemoteSocketAddress());
						System.out.println("===========================");
					}
				}
			}
		}

		// 가위바위보 승패 판단 메소드
		public String RockScissorsPapers(int num, String name) {
			String s = null;
			if (arr.get(0) == arr.get(1)) {
				s = "비김\n";
			} else if (arr.get(0) != arr.get(1)) {
				map.put(num, name);
				System.out.println(map.size());
				while (true) {
					if (map.size() >= 2) {
						if (arr.get(0) == 1 && arr.get(1) == 2) {
							s = (map.get(2) + "님이 이겼습니다. '선' 입니다!!! \n" + map.get(1) + "님이 패배하였습니다. 후공입니다!!! \n");
							winnerName = map.get(2);
							loserName = map.get(1);
							break;
						} else if (arr.get(0) == 2 && arr.get(1) == 1) {
							s = (map.get(2) + "님이 이겼습니다. 선공입니다!!! \n" + map.get(1) + "님이 패배하였습니다. 후공입니다!!! \n");
							winnerName = map.get(2);
							loserName = map.get(1);
							break;
						} else if (arr.get(0) == 2 && arr.get(1) == 3) {
							s = (map.get(3) + "님이 이겼습니다. 선공입니다!!! \n" + map.get(2) + "님이 패배하였습니다. 후공입니다!!! \n");
							winnerName = map.get(3);
							loserName = map.get(2);
							break;
						} else if (arr.get(0) == 3 && arr.get(1) == 2) {
							s = (map.get(3) + "님이 이겼습니다. 선공입니다!!! \n" + map.get(2) + "님이 패배하였습니다. 후공입니다!!! \n");
							winnerName = map.get(3);
							loserName = map.get(2);
							break;
						} else if (arr.get(0) == 3 && arr.get(1) == 1) {
							s = (map.get(1) + "님이 이겼습니다. 선공입니다!!! \n" + map.get(3) + "님이 패배하였습니다. 후공입니다!!! \n");
							winnerName = map.get(1);
							loserName = map.get(3);
							break;
						} else if (arr.get(0) == 1 && arr.get(1) == 3) {
							s = (map.get(1) + "님이 이겼습니다. 선공입니다!!! \n" + map.get(3) + "님이 패배하였습니다. 후공입니다!!! \n");
							winnerName = map.get(1);
							loserName = map.get(3);
							break;
						}
					} else if (map.size() < 2) {
						System.out.println("대기중");
						threadSleep(3000);
					}

				}
			}
			return s;
		}

		public void initialization() {
			arr.clear();
			nameArr.clear();
			map.clear();
			nameSocket.clear();
			compare1.clear();
			compare2.clear();
			forWait.clear();
			readyCount = 0;
			getNum = false;
			count = 1;
			isSecTime = false;

		}

		public void threadSleep(int i) {
			try {
				Thread.sleep(i);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getAdate() {// 로그 파일생성 (파일이름을 날짜로...)
			Date d = new Date();
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
			String date = sf.format(d);
			return date;
		}

		public String getTime() {// 로그 시간생성 (유저 입장시간, 게임결과 및 시간 생성....)
			Date d = new Date();
			SimpleDateFormat sf = new SimpleDateFormat("HH:mm:ss");
			String time = sf.format(d);
			return time;
		}

	}// serverThread.class

	public class SeverCheck implements Runnable {
		private Socket socket;
		private BufferedReader br;
		private BufferedWriter bw;

		public SeverCheck(Socket socket) {
			this.socket = socket;
			try {
				bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
				br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public void run() {
			if (socketList.size() < 2) {
				try {
					bw.write("n \n");
					bw.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

	}
}

public class ServerBaseball {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ServerSocket serverSocket = null;
		Socket socket = null;
		ServerManager servermanager = new ServerManager(socket);

		System.out.println("클라이언트의 접속을 기다립니다!!!!!!");

		try {
			serverSocket = new ServerSocket(5001);
			while (true) {
				socket = serverSocket.accept();
				System.out.println(socket + "연결");
				servermanager.addSocket(socket);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
