package ru.dnscookie.snake;

public class Coordinate {
	private int x;
	private int y;
	public Coordinate(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public Coordinate getDirection(DirectionEnum dir) {
		switch (dir) {
		case UP:
			return new Coordinate(x, y + 1);
		case RIGHT:
			return new Coordinate(x + 1, y);
		case LEFT:
			return new Coordinate(x - 1, y);
		case DOWN:
			return new Coordinate(x, y - 1);
		}
		return this;
	}
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public boolean equals(Object o) {
		if (o.getClass() == this.getClass()) {
			Coordinate c = (Coordinate) o;
			return c.x == this.x && c.y == this.y;
		}
		return false;
	}
}
