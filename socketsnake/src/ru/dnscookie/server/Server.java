package ru.dnscookie.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

import ru.dnscookie.logger.Logger;

public class Server extends ServerSocket implements Runnable {
	public boolean isActive;
	private Vector<Session> sessions;
	private Logger l;
	private int nextId = 0;
	
	public Server(int port) throws IOException {
		super(port);
		this.l = new Logger("SERVER");
	}

	@Override
	public void run() {
		isActive = true;
		sessions = new Vector<Session>();
		l.log("Server is started");
		while (isActive) {
			try {
				Socket socket = this.accept();
				Session session = new Session(socket, nextId++, this);
				sessions.add(session);
				new Thread(session).start();
			}
			catch (Exception e) {}
		}
	}

	public void send(int id, String request) {
		Session s = getSession(id);
		if (s != null) {
			s.send(request);
		}
	}
	public void closeServer() {
		for (int i = 0; i < sessions.size(); i++) {
			sessions.get(i).close();
		}
		try {
			this.close();
		}
		catch (Exception e) {
			l.err("Error while closing server:");
			l.err(e.getLocalizedMessage());
		}
		l.log("Server is closed");
		isActive = false;
	}
	public void closeSession(int id) {
		Session s = getSession(id);
		sessions.remove(s);
		if (s != null) {
			s.close();
		}
	}
	
	public Session getSession(int id) {
		for (int i = 0; i < sessions.size(); i++) {
			Session s = sessions.get(i);
			if (s.getId() == id) {
				return s;
			}
		}
		l.err("Session " + id + " wasn't found.");
		return null;
	}
	public int sessionsCount() {
		if (sessions == null) return 0;
		return sessions.size();
	}
}
