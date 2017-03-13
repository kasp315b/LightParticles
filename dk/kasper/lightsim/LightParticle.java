package dk.kasper.lightsim;


import java.awt.Color;
import java.awt.Graphics2D;

public class LightParticle {

	private Vec2 position, velocity;

	public LightParticle(float x, float y, float bearing, float speed) {
		position = new Vec2(x, y);
		velocity = new Vec2(bearing).mult(speed);
	}

	public void update(int deltaTime, LightSim ls) {
		for(int i = 0; i < ls.walls.size(); i++) {
			Wall w = ls.walls.get(i);
			Vec2 intersect = w.getIntersect(this);
			if(intersect != null) {
				if(w.intersects(this)) {
					this.reflect(w.getNormal());
				}
			}
		}
		position.add(velocity.copy().mult(deltaTime/20F));
	}

	public void render(Graphics2D g, LightSim ls) {
		g.setColor(Color.WHITE);
		g.fillRect((int)position.x, (int)position.y, 1, 1);
	}

	public Vec2 getPosition() {
		return position;
	}

	public Vec2 getVelocity() {
		return velocity;
	}
	
	public synchronized void reflect(Vec2 normal) {
		velocity = new Vec2(velocity.x-(2*normal.x*velocity.getDotProduct(normal)), velocity.y-(2*normal.y*velocity.getDotProduct(normal)));
	}
}
