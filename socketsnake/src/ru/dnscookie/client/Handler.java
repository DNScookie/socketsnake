package ru.dnscookie.client;

import java.util.ArrayList;

import ru.dnscookie.logger.Logger;
import ru.dnscookie.snake.Coordinate;
import ru.dnscookie.snake.DeskObject;
import ru.dnscookie.snake.GameWindow;

public class Handler implements Runnable {
	private ArrayList<String> stack;
	private Connection connection;
	private GameWindow game;
	
	public Handler(Connection c) {
		this.connection = c;
		this.stack = new ArrayList<String>();
		this.game = ru.dnscookie.snake.Main.gameWindow;
	}
	
	public void run() {
		while (true) {
			try {
				Thread.sleep(50);
			} catch (Exception e) {
				new Logger("HANDLER").err(e.getLocalizedMessage());
			}
			if (stack.size() == 0) {
				if (!connection.isActive()) break;
				continue;
			}
			switch (next()) {
			case "start":
				game.sizeX = Integer.parseInt(next());
				game.sizeY = Integer.parseInt(next());
				game.drawWalls();
				break;
			case "upd":
				for (int i = 1; i < game.sizeX + 1; i++) {
					for (int j = 1; j < game.sizeY + 1; j++) {
						DeskObject obj;
						switch (next()) {
						case "rh":
							obj = DeskObject.RED_HEAD;
							break;
						case "bh":
							obj = DeskObject.BLUE_HEAD;
							break;
						case "r":
							obj = DeskObject.RED_SNAKE;
							break;
						case "b":
							obj = DeskObject.BLUE_SNAKE;
							break;
						case "a":
							obj = DeskObject.APPLE;
							break;
						case "w":
							obj = DeskObject.WALL;
							break;
						default:
							obj = DeskObject.EMPTY;
							break;
						}
						game.draw(new Coordinate(i, j), obj);
					}
				}
				break;
			case "end":
				game.drawWinner(next());
			}
		}
	}
	public void handle(String inputRaw) {
		String[] input = inputRaw.split(";");
		for (int i = 0; i < input.length; i++) {
			String inputEntry = input[i];
			if (inputEntry != "") {
				stack.add(inputEntry);
			}
		}
	}
	private String next() {
		return stack.remove(0);
	}
}
