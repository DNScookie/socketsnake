package ru.dnscookie.logger;

public class Logger {
	private String name;
	
	public Logger(String name) {
		this.name = name;
	}
	
	public void log(String s) {
		System.out.println("[" + name + "] " + s);
	}
	
	public void err(String s) {
		log("[ERROR] " + s);
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
