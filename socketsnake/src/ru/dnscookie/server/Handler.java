package ru.dnscookie.server;

import java.util.ArrayList;

import ru.dnscookie.logger.Logger;
import ru.dnscookie.snake.DirectionEnum;

public class Handler implements Runnable {
	private ArrayList<String> stack;
	private Session session;
	
	public Handler(Session session) {
		this.session = session;
		this.stack = new ArrayList<String>();
	}
	
	public void run() {
		while (true) {
			try {
				Thread.sleep(50);
			} catch (Exception e) {
				new Logger("HANDLER").err(e.getLocalizedMessage());
			}
			if (stack.size() == 0) {
				if (!session.isActive()) break;
				continue;
			}
			// processing
			switch(next()) {
			case "move":
				switch (next()) {
				case "u":
					session.getPlayer().setDirection(DirectionEnum.UP);
					break;
				case "r":
					session.getPlayer().setDirection(DirectionEnum.RIGHT);
					break;
				case "l":
					session.getPlayer().setDirection(DirectionEnum.LEFT);
					break;
				case "d":
					session.getPlayer().setDirection(DirectionEnum.DOWN);
					break;
				}
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
		String j = stack.remove(0);
		return j;
	}
	
	public void setSession(Session session) {
		this.session = session;
	}
}
