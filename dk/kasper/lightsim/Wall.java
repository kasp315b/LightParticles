package dk.kasper.lightsim;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

public class Wall {
	private Vec2 position, direction;
	
	private Color color;
	
	public Wall(float x, float y, float x2, float y2) {
		position = new Vec2(x, y);
		direction = new Vec2(position.getBearingTo(new Vec2(x2, y2)), new Vec2(x2, y2).sub(position).amplitude());
		color = Color.WHITE;
	}
	
	public Wall(Vec2 p, float b, float a) {
		position = p.copy();
		direction = new Vec2(b, a);
	}
	
	public void update(int deltaTime, LightSim ls) {
	}
	
	public void render(Graphics2D g, LightSim ls) {
		g.setColor(color);
		g.drawLine((int)this.getPosition().x, (int)this.getPosition().y, (int)this.getEndingPoint().x, (int)this.getEndingPoint().y);
		for(int i = 0; i < ls.walls.size(); i++) {
			Wall w = ls.walls.get(i);
			Vec2 p = this.getIntersect(w);
			if(p != null) {
				if(this.intersects(w)) {
					g.setColor(Color.GREEN);
					g.drawOval((int)p.x-5, (int)p.y-5, 10, 10);
				}
			}
		}
	}
	
	public int[] getIntersectIDs(ArrayList<Wall> walls) {
		ArrayList<Integer> intersectIDs = new ArrayList<Integer>();
		for(int i = 0; i < walls.size(); i++) {
			Wall w = walls.get(i);
			Vec2 p = this.getIntersect(w);
			if(p != null) {
				if(this.intersects(w)) {
					intersectIDs.add(new Integer(i));
				}
			}
		}
		int[] intIDs = new int[intersectIDs.size()];
		for(int i = 0; i < intersectIDs.size(); i++) {
			intIDs[i] = intersectIDs.get(i).intValue();
		}
		return intIDs;
	}

	public boolean intersects(Wall wall) {
		Vec2 intersect = this.getIntersect(wall);
		float pair1SX = this.getPosition().x, pair1SY = this.getPosition().y, pair1EX = this.getEndingPoint().x, pair1EY = this.getEndingPoint().y;
		float pair2SX = wall.getPosition().x, pair2SY = wall.getPosition().y, pair2EX = wall.getEndingPoint().x, pair2EY = wall.getEndingPoint().y;
		
		if(this.getPosition().x > this.getEndingPoint().x) {
			pair1SX = this.getEndingPoint().x;
			pair1EX = this.getPosition().x;
		}
		if(this.getPosition().y > this.getEndingPoint().y) {
			pair1SY = this.getEndingPoint().y;
			pair1EY = this.getPosition().y;
		}
		if(wall.getPosition().x > wall.getEndingPoint().x) {
			pair2SX = wall.getEndingPoint().x;
			pair2EX = wall.getPosition().x;
		}
		if(wall.getPosition().y > wall.getEndingPoint().y) {
			pair2SY = wall.getEndingPoint().y;
			pair2EY = wall.getPosition().y;
		}
		if(intersect.x < pair1SX || intersect.x < pair2SX) return false;
		if(intersect.y < pair1SY || intersect.y < pair2SY) return false;
		if(intersect.x > pair1EX || intersect.x > pair2EX) return false;
		if(intersect.y > pair1EY || intersect.y > pair2EY) return false;
		return true;
	}

	public boolean intersects(LightParticle lp) {
		Vec2 intersect = this.getIntersect(lp);
		float pair1SX = this.getPosition().x, pair1SY = this.getPosition().y, pair1EX = this.getEndingPoint().x, pair1EY = this.getEndingPoint().y;
		float pair2SX = lp.getPosition().x, pair2SY = lp.getPosition().y, pair2EX = lp.getPosition().x+lp.getVelocity().x, pair2EY = lp.getPosition().y+lp.getVelocity().y;
		
		if(this.getPosition().x > this.getEndingPoint().x) {
			pair1SX = this.getEndingPoint().x;
			pair1EX = this.getPosition().x;
		}
		if(this.getPosition().y > this.getEndingPoint().y) {
			pair1SY = this.getEndingPoint().y;
			pair1EY = this.getPosition().y;
		}
		if(lp.getPosition().x > lp.getPosition().x+lp.getVelocity().x) {
			pair2SX = lp.getPosition().x+lp.getVelocity().x;
			pair2EX = lp.getPosition().x;
		}
		if(lp.getPosition().y > lp.getPosition().y+lp.getVelocity().y) {
			pair2SY = lp.getPosition().y+lp.getVelocity().y;
			pair2EY = lp.getPosition().y;
		}
		if(intersect.x < pair1SX || intersect.x < pair2SX) return false;
		if(intersect.y < pair1SY || intersect.y < pair2SY) return false;
		if(intersect.x > pair1EX || intersect.x > pair2EX) return false;
		if(intersect.y > pair1EY || intersect.y > pair2EY) return false;
		return true;
	}

	public Vec2 getIntersect(LightParticle lp) {
		// Get slope-intercepts
		float slope1 = (this.getEndingPoint().y-this.getPosition().y)/(this.getEndingPoint().x-this.getPosition().x);
		float intercept1 = (slope1*this.getPosition().x)-this.getPosition().y;
		float slope2 = (lp.getPosition().y+lp.getVelocity().y-lp.getPosition().y)/(lp.getPosition().x+lp.getVelocity().x-lp.getPosition().x);
		float intercept2 = (slope2*lp.getPosition().x)-lp.getPosition().y;
		
		if(slope1 == slope2) return null;
		
		// Find point of intersect
		float x = (-intercept1+intercept2)/(slope1-slope2);
		float y = (slope1*x)+intercept1;
		
		return new Vec2(x*-1, y*-1);
	}
	
	public Vec2 getIntersect(Wall wall) {
		if(wall.getPosition().x == this.getPosition().x) return null;
		
		// Get slope-intercepts
		float slope1 = (this.getEndingPoint().y-this.getPosition().y)/(this.getEndingPoint().x-this.getPosition().x);
		float intercept1 = (slope1*this.getPosition().x)-this.getPosition().y;
		float slope2 = (wall.getEndingPoint().y-wall.getPosition().y)/(wall.getEndingPoint().x-wall.getPosition().x);
		float intercept2 = (slope2*wall.getPosition().x)-wall.getPosition().y;
		
		if(slope1 == slope2) return null;
		
		// Find point of intersect
		float x = (-intercept1+intercept2)/(slope1-slope2);
		float y = (slope1*x)+intercept1;
		
		return new Vec2(x*-1, y*-1);
	}
	
	public Vec2 getNormal() {
		float dx = this.getEndingPoint().x - this.getPosition().x;
		float dy = this.getEndingPoint().y - this.getPosition().y;
		return new Vec2(-dy, dx).normalize();
	}
	
	public Vec2 getNormal2() {
		float dx = this.getEndingPoint().x - this.getPosition().x;
		float dy = this.getEndingPoint().y - this.getPosition().y;
		return new Vec2(dy, -dx).normalize();
	}
	
	public Vec2 getPosition() {
		return position;
	}
	
	public Vec2 getDirection() {
		return direction;
	}
	
	public Vec2 getEndingPoint() {
		return new Vec2(direction.x).mult(direction.y).add(position);
	}
}
