package ru.dnscookie.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import ru.dnscookie.logger.Logger;

public class Connection implements Runnable {
	private Logger l;
	private Socket s;
	private DataInputStream is;
	private DataOutputStream os;
	private boolean isActive;
	private Handler handler;
	
	public Connection(String ip) {
		l = new Logger("CLIENT");
		try {
			s = new Socket(ip, 907);
			this.is = new DataInputStream(s.getInputStream());
			this.os = new DataOutputStream(s.getOutputStream());
			isActive = true;
			handler = new Handler(this);
			new Thread(handler).start();
			l.log("Connection established");
		} catch (Exception e) {
			l.err("Server is unavaible.");
		}
	}

	public void run() {
		while (isActive) {
			try {
				String input = is.readUTF();
				handler.handle(input);
			} catch (Exception e) {
				if (isActive) l.err("Connection is lost.");
				close();
			}
		}
	}
	
	public void send(String request) {
		try {
			os.writeUTF(request);
			os.flush();
		} catch (IOException e) {
			l.err("Error while sending request:");
			l.err(e.getLocalizedMessage());
		}
	}
	
	public void close() {
		if (isActive) {
			try {
				s.close();
			} catch (Exception e) {
				l.err("Error while closing connection:");
				l.err(e.getLocalizedMessage());
			}
			isActive = false;
		}
	}

	public boolean isActive() {
		return isActive;
	}
}
