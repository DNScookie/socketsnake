package ru.dnscookie.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import ru.dnscookie.logger.Logger;
import ru.dnscookie.snake.Player;

public class Session implements Runnable {
	private Socket s;
	private Handler handler;
	private boolean isActive;
	private int id;
	private DataInputStream is;
	private DataOutputStream os;
	private Logger l;
	private Server serv;
	private Player player;
	
	public Session(Socket socket, int id, Server serv) {
		isActive = true;
		this.serv = serv;
		this.s = socket;
		handler = new Handler(this);
		new Thread(handler).start();
		this.id = id;
		this.l = new Logger("SESSION " + id);
		try {
			this.is = new DataInputStream(s.getInputStream());
			this.os = new DataOutputStream(s.getOutputStream());
			l.log("Session is started");
		} catch (IOException e) {
			l.err("Error while initializing session:");
			l.err(e.getLocalizedMessage());
			close();
		}
	}
	
	public void send(String request) {
		try {
			os.writeUTF(request);
			os.flush();
		} catch (IOException e) {
			l.err("Error while sending request:");
			l.err(e.getLocalizedMessage());
			close();
		}
	}

	@Override
	public void run() {
		while (isActive) {
			try {
				String input = is.readUTF();
				handler.handle(input);
			} catch (IOException e) {
				if (isActive) l.err("Connection is lost.");
				serv.closeSession(id);
			}
		}
	}

	public boolean isActive() {
		return isActive;
	}

	public int getId() {
		return id;
	}

	public void close() {
		if (isActive) {
			try {
				s.close();
			} catch (Exception e) {
				l.err("Error while closing session:");
				l.err(e.getLocalizedMessage());
			}
			isActive = false;
		}
	}
	public Server getServer() {
		return serv;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}
}
