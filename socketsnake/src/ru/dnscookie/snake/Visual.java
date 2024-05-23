package ru.dnscookie.snake;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class Visual {
	public Graphics2D gr;
	Graphics2D sceneGr;
	BufferedImage scene;
	int squareSide;
	
	public void draw(Coordinate cord, DeskObject obj) {
		int size = (int) (squareSide * 0.45);
		int cX = (int) (squareSide * (cord.getX() + 0.5));
		int cY = (int) (squareSide * (cord.getY() + 0.5));
		Color col = Color.LIGHT_GRAY;
		if (obj == DeskObject.WALL) col = Color.DARK_GRAY;
		sceneGr.setColor(col);
		sceneGr.fillRoundRect(cX - size, cY - size, size * 2, size * 2, 2, 2);
		switch (obj) {
		case APPLE:
			size = (int) (squareSide * 0.15);
			col = Color.RED;
			break;
		case BLUE_HEAD:
			size = (int) (squareSide * 0.30);
			col = Color.BLUE;
			break;
		case BLUE_SNAKE:
			size = (int) (squareSide * 0.25);
			col = Color.BLUE;
			break;
		case RED_HEAD:
			size = (int) (squareSide * 0.30);
			col = Color.RED;
			break;
		case RED_SNAKE:
			size = (int) (squareSide * 0.25);
			col = Color.RED;
			break;
		default:
			break;
		}
		sceneGr.setColor(col);
		sceneGr.fillRoundRect(cX - size, cY - size, size * 2, size * 2, 2, 2);
	}
}
