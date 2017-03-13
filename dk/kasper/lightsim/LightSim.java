package dk.kasper.lightsim;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class LightSim {

	private Window window;

	boolean mousePressed = false;
	boolean keyPressed = false;
	char keyChar = 0;
	int keyCode = 0;
	int mouseX = 0;
	int mouseY = 0;
	int mouseClickX = 0;
	int mouseClickY = 0;
	
	ArrayList<LightParticle> lightlist;
	ArrayList<Wall> walls;

	public LightSim(Window window) {
		this.window = window;
		
		lightlist = new ArrayList<LightParticle>();
		walls = new ArrayList<Wall>();
	}

	// Update the game x milliseconds into the future
	public void update(int deltaTime) {
		for(int i = 0; i < lightlist.size(); i++) {
			LightParticle lp = lightlist.get(i);
			if(lp == null) continue;
			lp.update(deltaTime, this);
		}
		for(int i = 0; i < walls.size(); i++) {
			Wall w = walls.get(i);
			if(w == null) continue;
			w.update(deltaTime, this);
		}
	}

	// Render all renderable objects
	public void render(Graphics2D g) {
		for(int i = 0; i < lightlist.size(); i++) {
			LightParticle lp = lightlist.get(i);
			if(lp == null) continue;
			lp.render(g, this);
		}
		for(int i = 0; i < walls.size(); i++) {
			Wall w = walls.get(i);
			if(w == null) continue;
			w.render(g, this);
		}
	}

	// Mouse and keyboard passthrough functions

	public void xMousePressed(MouseEvent e) {
		mouseClickX = e.getX();
		mouseClickY = e.getY();
		mousePressed = true;
		if(e.getButton() == MouseEvent.BUTTON1) {
			lightlist.add(new LightParticle(e.getX(), e.getY(), (float)(Math.random()*Math.PI*2), 2));
		}
	}

	public void xMouseReleased(MouseEvent e) {
		mousePressed = false;
		if(e.getButton() == MouseEvent.BUTTON3) {
			walls.add(new Wall(mouseClickX, mouseClickY, e.getX(), e.getY()));
		}
	}

	public void xMouseMoved(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
	}

	public void xMouseDragged(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
	}

	public void xKeyPressed(KeyEvent e) {
		keyPressed = true;
		keyCode = e.getKeyCode();
		keyChar = java.lang.Character.toLowerCase(e.getKeyChar());
		
		System.out.println("\nKeypressed: #"+keyCode+"-\""+keyChar+'"');
		
		if(keyCode == 32) {
			walls.removeAll(walls);
			lightlist.removeAll(lightlist);
		}
	}

	public void xKeyReleased(KeyEvent e) {
		keyPressed = false;
	}
	
	public Point getMouse() {
		return new Point(mouseX, mouseY);
	}
	
	public Window getWindow() {
		return window;
	}
}
