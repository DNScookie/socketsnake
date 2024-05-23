package ru.dnscookie.snake;

import java.util.ArrayList;

import ru.dnscookie.server.Session;

public class Player {
	public Session session;
	public DirectionEnum direction;
	public ArrayList<Coordinate> parts;
	public boolean dead;
	
	public Player() {
		dead = false;
		parts = new ArrayList<Coordinate>();
	}

	public void setDirection(DirectionEnum dir) {
		if (getDirection() == DirectionEnum.UP && dir == DirectionEnum.DOWN) return;
		if (getDirection() == DirectionEnum.DOWN && dir == DirectionEnum.UP) return;
		if (getDirection() == DirectionEnum.RIGHT && dir == DirectionEnum.LEFT) return;
		if (getDirection() == DirectionEnum.LEFT && dir == DirectionEnum.RIGHT) return;
		direction = dir;
	}
	
	public DirectionEnum getDirection() {
		Coordinate tail = parts.get(1);
		Coordinate head = getHead();
		if (tail.getDirection(DirectionEnum.UP).equals(head)) return DirectionEnum.UP;
		if (tail.getDirection(DirectionEnum.DOWN).equals(head)) return DirectionEnum.DOWN;
		if (tail.getDirection(DirectionEnum.LEFT).equals(head)) return DirectionEnum.LEFT;
		return DirectionEnum.RIGHT;
	}
	
	public Coordinate getHead() {
		return parts.get(0);
	}
	
	public Coordinate move() {
		parts.add(0, getHead().getDirection(direction));
		return parts.remove(parts.size() - 1);
	}
	public boolean has(Coordinate c) {
		for (Coordinate cord : parts) {
			if (cord.equals(c)) return true;
		}
		return false;
	}
	
	public boolean ateHimself() {
		Coordinate c = getHead();
		for (int i = 1; i < parts.size(); i++) {
			Coordinate cord = parts.get(i);
			if (cord.equals(c)) return true;
		}
		return false;
	}
	public int getScore() {
		return parts.size() - 2;
	}
	
	public void initialize(Coordinate head, Coordinate tail) {
		parts.add(head);
		parts.add(tail);
		direction = getDirection();
	}
}
