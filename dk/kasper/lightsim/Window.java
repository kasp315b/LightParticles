package dk.kasper.lightsim;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferStrategy;
import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Window extends JFrame implements Runnable, ConsoleListener {

	public static final int WIDTH = 800;
	public static final int HEIGHT = 800;

	Canvas canvas;
	BufferStrategy bufferStrategy;
	
	private int frameCount;
	
	private ArrayList<Long> framerate = new ArrayList<Long>();

	public Window() {
		super("Window");
		
		JPanel panel = (JPanel) getContentPane();
		panel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		panel.setLayout(null);

		canvas = new Canvas();
		canvas.setBounds(0, 0, WIDTH, HEIGHT);
		canvas.setIgnoreRepaint(true);

		panel.add(canvas);

		canvas.addMouseListener(new MouseControl());
		canvas.addMouseMotionListener(new MouseMotionControl());
		canvas.addKeyListener(new KeyboardControl());

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		
		//Fullscreen
		//setUndecorated(true);
		//setExtendedState(JFrame.MAXIMIZED_BOTH);
		
		pack();
		setSize(new Dimension(WIDTH+7, HEIGHT+30));
		setVisible(true);
		
		canvas.createBufferStrategy(2);
		bufferStrategy = canvas.getBufferStrategy();

		canvas.requestFocus();

	}

	private class MouseControl extends MouseAdapter {
		public void mousePressed(MouseEvent e) {
			if(lsim != null) lsim.xMousePressed(e);
		}

		public void mouseReleased(MouseEvent e) {
			if(lsim != null) lsim.xMouseReleased(e);
		}
	}

	private class MouseMotionControl extends MouseMotionAdapter {
		public void mouseMoved(MouseEvent e) {
			if(lsim != null) lsim.xMouseMoved(e);
		}

		public void mouseDragged(MouseEvent e) {
			if(lsim != null) lsim.xMouseDragged(e);
		}
	}

	private class KeyboardControl extends KeyAdapter {
		public void keyPressed(KeyEvent e) {
			if(lsim != null) lsim.xKeyPressed(e);
		}

		public void keyReleased(KeyEvent e) {
			if(lsim != null) lsim.xKeyReleased(e);
		}
	}
	
	

	long desiredFPS = 60;
	long desiredDeltaLoop = (1000 * 1000 * 1000) / desiredFPS;

	boolean running = true;

	public void run() {
		long beginLoopTime;
		long endLoopTime;
		long currentUpdateTime = System.nanoTime();
		long lastUpdateTime;
		long deltaLoop;

		setup();

		while (running) {
			beginLoopTime = System.nanoTime();

			render();

			lastUpdateTime = currentUpdateTime;
			currentUpdateTime = System.nanoTime();
			update((int) ((currentUpdateTime - lastUpdateTime) / (1000 * 1000)));

			endLoopTime = System.nanoTime();
			deltaLoop = endLoopTime - beginLoopTime;

			if (deltaLoop > desiredDeltaLoop) {
				// Hurry on, we are late.
			} else {
				try {
					Thread.sleep((desiredDeltaLoop - deltaLoop) / (1000 * 1000));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void main(String[] args) {
		Window ex = new Window();
		ConsoleHandler ch = new ConsoleHandler();
		new Thread(ch).start();
		ch.addListener(ex);
		new Thread(ex).start();
	}

	private void render() {
		frameCount++;
		Graphics2D g = (Graphics2D) bufferStrategy.getDrawGraphics();
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, WIDTH, HEIGHT);
		render(g);
		g.setColor(Color.WHITE);
		g.drawString("Framerate: "+framerate.size(), 10, 20);
		g.dispose();
		bufferStrategy.show();
	}
	
	public int getFramerate() {
		return framerate.size();
	}
	
	public int getFrameCount() {
		return frameCount;
	}
	// Actual code goes here below:
	
	private LightSim lsim;
	
	protected void setup() {
		lsim = new LightSim(this);
	}

	protected void update(int deltaTime) {
		lsim.update(deltaTime);
	}
	
	protected void render(Graphics2D g) {
		framerate.add(new Long(System.currentTimeMillis()));
		for(int i = 0; i < framerate.size(); i++) {
			long ctm = framerate.get(i).longValue();
			if(ctm < System.currentTimeMillis()-1000) framerate.remove(i);
		}
		lsim.render(g);
	}
	
	public void consoleLineEntered(String line) {
		if(line == null) return;
		String[] params = line.split(" ");
		if(params.length == 0) return;
		if(params.length > 0) {
			if(params[0].equals("create")) {
				if(params[1].equals("wall")) {
					if(params.length != 6) return;
					int sx = Integer.parseInt(params[2]);
					int sy = Integer.parseInt(params[3]);
					int ex = Integer.parseInt(params[4]);
					int ey = Integer.parseInt(params[5]);
					lsim.walls.add(new Wall(sx, sy, ex, ey));
					System.out.println("Wall created at ["+sx+","+sy+"] to ["+ex+","+ey+"]");
				}
				if(params[1].equals("particle")) {
					if(params.length != 5) return;
					int amount = Integer.parseInt(params[2]);
					int spawnX = Integer.parseInt(params[3]);
					int spawnY = Integer.parseInt(params[4]);
					for(int i = 0; i < amount; i++) {
						lsim.lightlist.add(new LightParticle(spawnX, spawnY, (float)(Math.random()*Math.PI*2), 2));
					}
					System.out.println("Created "+amount+" particles at ["+spawnX+","+spawnY+"]");
				}
			}
			if(params[0].equals("remove")) {
				if(params[1].equals("particle")) {
					if(params.length == 3) {
						int id = Integer.parseInt(params[2]);
						lsim.lightlist.remove(id);
						System.out.println("Remove particle "+id);
					} else {
						lsim.lightlist.clear();
						System.out.println("Removed all particles");
					}
				}
				if(params[1].equals("wall")) {
					if(params.length == 3) {
						int id = Integer.parseInt(params[2]);
						lsim.walls.remove(id);
						System.out.println("Remove wall "+id);
					} else {
						lsim.lightlist.clear();
						System.out.println("Removed all walls");
					}
				}
			}
			if(params[0].equals("info")) {
				if(params[1].equals("particle")) {
					if(params.length == 3) {
						int id = Integer.parseInt(params[2]);
						LightParticle ls = lsim.lightlist.get(id);
						int posx = (int)ls.getPosition().x;
						int posy = (int)ls.getPosition().y;
						System.out.println("Particle "+id+" is at ["+posx+","+posy+"]");
					} else {
						System.out.println("There are "+lsim.lightlist.size()+" particles");
					}
				}
				if(params[1].equals("wall")) {

					if(params.length == 3) {
						int id = Integer.parseInt(params[2]);
						Wall w = lsim.walls.get(id);
						int sposx = (int)w.getPosition().x;
						int sposy = (int)w.getPosition().y;
						int eposx = (int)w.getEndingPoint().x;
						int eposy = (int)w.getEndingPoint().y;
						System.out.println("Wall "+id+" is at ["+sposx+","+sposy+"] to ["+eposx+","+eposy+"]");
						int[] I_IDs = w.getIntersectIDs(lsim.walls);
						String intersects = "";
						for(int i = 0; i < I_IDs.length; i++) {
							if(intersects.equals("")) {
								intersects = ""+I_IDs[i];
							} else {
								intersects = intersects+", "+I_IDs[i];
							}
						}
						System.out.println("Wall "+id+" intersects with "+intersects);
					} else {
						System.out.println("There are "+lsim.walls.size()+" walls");
					}
				}
			}
		}
	}
}
