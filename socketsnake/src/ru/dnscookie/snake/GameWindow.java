package ru.dnscookie.snake;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import ru.dnscookie.logger.Logger;

public class GameWindow extends JFrame {
	private static final long serialVersionUID = -5337032096246001274L;
	
	public static JPanel menu;
	public static JButton buttonCreate;
	public static JButton buttonJoin;
	public static JTextField ip;
	public static JLabel ipLabel;
	public static JTextField apples;
	public static JLabel applesLabel;
	public static JTextField sizeXInput;
	public static JTextField sizeYInput;
	public static JLabel sizeXLabel;
	public static JLabel sizeYLabel;
	
	public int sizeX;
	public int sizeY;
	public int screenSizeX;
	public int screenSizeY;
	public Dimension screenDimension;
	public static int squareSide = 40;
	public Graphics2D gr;
	Graphics2D sceneGr;
	BufferedImage scene;
	private Logger l;
	
	public GameWindow() {
		super("Snake 2P v0.8");
		l = new Logger("GRAPHICS");
		this.setSize(750, 200);
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		buttonCreate = new JButton("Create room");
		buttonJoin = new JButton("Join room");
		buttonCreate.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				try {
					ip.setText(InetAddress.getLocalHost().getHostAddress());
				} catch (UnknownHostException exc) {
					ip.setText("127.0.0.1");
				}
				Main.createRoom(Integer.parseInt(apples.getText()), Integer.parseInt(sizeXInput.getText()), Integer.parseInt(sizeYInput.getText()));
				ip.setEditable(false);
				buttonCreate.setEnabled(false);
				buttonJoin.setEnabled(false);
				apples.setEditable(false);
				sizeXInput.setEditable(false);
				sizeYInput.setEditable(false);
			}
		});
		buttonJoin.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				Main.joinRoom(ip.getText());
			}
		});
		ip = new JTextField("127.0.0.1", 9);
		ipLabel = new JLabel("IP Address of host (for join):");
		apples = new JTextField("3", 9);
		applesLabel = new JLabel("Count of apples (for create):");
		sizeXInput = new JTextField("15", 2);
		sizeYInput = new JTextField("15", 2);
		sizeXLabel = new JLabel("Width:");
		sizeYLabel = new JLabel("Height:");
		menu = new JPanel(new FlowLayout(FlowLayout.LEFT));
		menu.add(buttonCreate);
		menu.add(buttonJoin);
		menu.add(ipLabel);
		menu.add(ip);
		menu.add(applesLabel);
		menu.add(apples);
		menu.add(sizeXLabel);
		menu.add(sizeXInput);
		menu.add(sizeYLabel);
		menu.add(sizeYInput);
		this.add(menu);
		ip.setSize(200, 40);
		this.setVisible(true);
	}
	
	public void draw(Coordinate cord, DeskObject obj) {
		int size = (int) (squareSide * 0.9);
		int cX = (int) (squareSide * (cord.getX() + 0.5));
		int cY = (int) (squareSide * (cord.getY() + 0.5));
		Color col = Color.LIGHT_GRAY;
		if (obj == DeskObject.WALL) col = Color.DARK_GRAY;
		sceneGr.setColor(col);
		sceneGr.fillRect(cX - size / 2, cY - size / 2, size, size);
		switch (obj) {
		case APPLE:
			size = (int) (squareSide * 0.3);
			col = Color.GREEN;
			break;
		case BLUE_HEAD:
			size = (int) (squareSide * 0.6);
			col = Color.BLUE;
			break;
		case BLUE_SNAKE:
			size = (int) (squareSide * 0.5);
			col = Color.BLUE;
			break;
		case RED_HEAD:
			size = (int) (squareSide * 0.6);
			col = Color.RED;
			break;
		case RED_SNAKE:
			size = (int) (squareSide * 0.5);
			col = Color.RED;
			break;
		default:
			break;
		}
		sceneGr.setColor(col);
		sceneGr.fillRect(cX - size / 2, cY - size / 2, size, size);
		// l.log("Drew " + obj.name());
	}
	
	public void drawWalls() {
		screenSizeX = squareSide * (sizeX + 1);
		screenSizeY = squareSide * (sizeY + 1);
		screenDimension = new Dimension(screenSizeX, screenSizeY);
		this.getContentPane().setPreferredSize(screenDimension);
		this.remove(menu);
		this.setLayout(new JFrame().getLayout());
		this.pack();
		// frame.setVisible(true);
		KeyAdapter adapter = new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				super.keyPressed(e);
				switch (e.getKeyCode()) {
				case KeyEvent.VK_UP:
				case KeyEvent.VK_W:
					Main.connection.send("move;d;");
					break;
				case KeyEvent.VK_DOWN:
				case KeyEvent.VK_S:
					Main.connection.send("move;u;");
					break;
				case KeyEvent.VK_LEFT:
				case KeyEvent.VK_A:
					Main.connection.send("move;l;");
					break;
				case KeyEvent.VK_RIGHT:
				case KeyEvent.VK_D:
					Main.connection.send("move;r;");
					break;
				case KeyEvent.VK_R:
					if (!Main.logic.gameStarted) Main.startGame();
				}
			}
		};
		new Thread() {
			@Override
			public void run() {
				super.run();
				while (true) {
					try {
						sleep(30);
					} catch (InterruptedException e) {}
					Main.gameWindow.flush();
				}
			}
		}.start();
		this.addKeyListener(adapter);
		this.requestFocus();
		gr = (Graphics2D) this.getContentPane().getGraphics();
		scene = new BufferedImage(screenSizeX, screenSizeY, BufferedImage.TYPE_USHORT_555_RGB);
		sceneGr = scene.createGraphics();
		for (int i = 0; i < sizeX + 1; i++) {
			draw(new Coordinate(i, 0), DeskObject.WALL);
			draw(new Coordinate(i, sizeX), DeskObject.WALL);
		}
		for (int i = 1; i < sizeY; i++) {
			draw(new Coordinate(0, i), DeskObject.WALL);
			draw(new Coordinate(sizeY, i), DeskObject.WALL);
		}
		l.log("Drew the walls");
	}
	public void drawWinner(String winner) {
		String whoWins = "DRAW";
		sceneGr.setColor(Color.white);
		sceneGr.fillRect(screenSizeX / 2 - squareSide * 2, screenSizeY / 2 - squareSide, squareSide * 4, squareSide * 2);
		switch (winner) {
		case "r":
			sceneGr.setColor(Color.red);
			whoWins = "RED WINS";
			break;
		case "b":
			sceneGr.setColor(Color.blue);
			whoWins = "BLUE WINS";
			break;
		case "d":
			sceneGr.setColor(Color.black);
		}
		sceneGr.setFont(sceneGr.getFont().deriveFont((float) 20));
		Rectangle2D bounds = sceneGr.getFont().getStringBounds("GAME OVER", sceneGr.getFontRenderContext());
		sceneGr.drawString("GAME OVER", (int) (screenSizeX / 2 - bounds.getCenterX()), (int) (screenSizeY / 2 - bounds.getCenterY()));
		int zapas = (int) (bounds.getHeight());
		sceneGr.setFont(sceneGr.getFont().deriveFont((float) 14));
		bounds = sceneGr.getFont().getStringBounds(whoWins, sceneGr.getFontRenderContext());
		sceneGr.drawString(whoWins, (int) (screenSizeX / 2 - bounds.getCenterX()), (int) (screenSizeY / 2 + bounds.getCenterY() + zapas));
	}
	
	public void flush() {
		gr.drawImage(scene, 0, 0, null);
	}
}
