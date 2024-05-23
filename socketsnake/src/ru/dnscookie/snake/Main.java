package ru.dnscookie.snake;

import ru.dnscookie.client.Connection;
import ru.dnscookie.server.Server;

public class Main {
	public static GameWindow gameWindow;
	public static Logic logic;
	public static Connection connection;
	public static Server serv;
	
	public static void main(String[] args) {
		gameWindow = new GameWindow();
	}
	
	static void joinRoom(String ip) {
		connection = new Connection(ip);
		new Thread(connection).start();
	}

	static void createRoom(int appleAmount, int sizeX, int sizeY) {
		logic = new Logic(appleAmount, sizeX, sizeY);
		try {
			serv = new Server(907);
			new Thread(serv).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		joinRoom("127.0.0.1");
		new Thread(new Runnable() {
			public void run() {
				do {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} while (serv.sessionsCount() != 2);
				startGame();
			}
		}).start();
	}
	
	public static void startGame() {
		new Thread(logic).start();
	}
}
