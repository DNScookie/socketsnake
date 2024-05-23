package ru.dnscookie.snake;

import java.util.Random;

import ru.dnscookie.logger.Logger;

public class Logic implements Runnable {
	private Logger l;
	private DeskObject[][] desk;
	public int sizeX = 15;
	public int sizeY = 15;
	Player bluePlayer;
	Player redPlayer;
	boolean gameStarted;
	int tick = 500;
	int appleCount;
	
	public Logic(int appleAmount, int sizeX, int sizeY) {
		this.appleCount = appleAmount;
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		l = new Logger("LOGIC");
	}
	
	public void run() {
		gameStarted = true;
		bluePlayer = new Player();
		redPlayer = new Player();
		Main.serv.getSession(0).setPlayer(bluePlayer);
		Main.serv.getSession(1).setPlayer(redPlayer);
		bluePlayer.session = Main.serv.getSession(0);
		redPlayer.session = Main.serv.getSession(1);
		//bluePlayer.session.setPlayer(bluePlayer);
		//redPlayer.session.setPlayer(redPlayer);
		desk = new DeskObject[sizeX + 1][sizeY + 1];
		for (int i = 0; i < sizeX + 1; i++) {
			desk[i][0] = DeskObject.WALL;
			desk[i][sizeY] = DeskObject.WALL;
		}
		for (int i = 1; i < sizeY; i++) {
			desk[0][i] = DeskObject.WALL;
			desk[sizeX][i] = DeskObject.WALL;
		}
		redPlayer.initialize(new Coordinate(1, 2), new Coordinate(1, 1));
		bluePlayer.initialize(new Coordinate(sizeX - 1, sizeY - 2), new Coordinate(sizeX - 1, sizeY - 1));
		for (int i = 0; i < appleCount; i++) newApple();
		
		bluePlayer.session.send("start;" + sizeX + ";" + sizeY);
		redPlayer.session.send("start;" + sizeX + ";" + sizeY);
		while (gameStarted) {
			try {
				Thread.sleep(tick);
			} catch (Exception e) {
				
			}
			Coordinate lastR = redPlayer.move();
			Coordinate lastB = bluePlayer.move();
			Coordinate rh = redPlayer.getHead();
			Coordinate bh = bluePlayer.getHead();
			if (desk[rh.getX()][rh.getY()] == DeskObject.WALL) redPlayer.dead = true;
			if (desk[bh.getX()][bh.getY()] == DeskObject.WALL) bluePlayer.dead = true;
			if (desk[rh.getX()][rh.getY()] == DeskObject.APPLE) {
				redPlayer.parts.add(lastR);
				desk[rh.getX()][rh.getY()] = DeskObject.EMPTY;
				newApple();
			}
			if (desk[bh.getX()][bh.getY()] == DeskObject.APPLE) {
				bluePlayer.parts.add(lastB);
				desk[bh.getX()][bh.getY()] = DeskObject.EMPTY;
				newApple();
			}
			if (bluePlayer.has(rh)) redPlayer.dead = true;
			if (redPlayer.has(bh)) bluePlayer.dead = true;
			
			if (redPlayer.dead && bluePlayer.dead) {
				if (redPlayer.getScore() > bluePlayer.getScore()) finish(redPlayer);
				if (bluePlayer.getScore() > redPlayer.getScore()) finish(bluePlayer);
				if (bluePlayer.getScore() == redPlayer.getScore()) finish(null);
				break;
			}
			if (redPlayer.dead) finish(bluePlayer);
			if (bluePlayer.dead) finish(redPlayer);
			if (!gameStarted) {
				break;
			}
			String deskData = "upd;";
			for (int i = 1; i < sizeX + 1; i++) {
				for (int j = 1; j < sizeY + 1; j++) {
					Coordinate t = new Coordinate(i, j);
					if (redPlayer.getHead().equals(t)) deskData += "rh";
					else if (bluePlayer.getHead().equals(t)) deskData += "bh";
					else if (redPlayer.has(new Coordinate(i, j))) deskData += "r";
					else if (bluePlayer.has(new Coordinate(i, j))) deskData += "b";
					else if (desk[i][j] == DeskObject.APPLE) deskData += "a";
					else if (desk[i][j] == DeskObject.WALL) deskData += "w";
					else deskData += "e";
					deskData += ";";
				}
			}
			redPlayer.session.send(deskData);
			bluePlayer.session.send(deskData);
		}
		l.log("Game finished, thread is closing.");
	}
	
	void newApple() {
		Random r = new Random();
		Coordinate apple;
		do {
			apple = new Coordinate(r.nextInt(sizeX - 2) + 1, r.nextInt(sizeY - 2) + 1);
		} while (!redPlayer.has(apple) && !bluePlayer.has(apple) && desk[apple.getX()][apple.getY()] == DeskObject.EMPTY);
		desk[apple.getX()][apple.getY()] = DeskObject.APPLE;
	}

	void finish(Player winner) {
		gameStarted = false;
		String request = "end;";
		if (winner == redPlayer) request += "r";
		if (winner == bluePlayer) request += "b";
		if (winner == null) request += "d"; // draw
		redPlayer.session.send(request);
		bluePlayer.session.send(request);
	}
}
