package dk.kasper.lightsim;

import java.awt.Point;

public class Vec2 {
	public float x, y;

	public Vec2() {
		x = 0F;
		y = 0F;
	}
	
	public Vec2(Point p) {
		x = p.x;
		y = p.y;
	}

	public Vec2(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public Vec2(float bearing) {
		this.x = (float) Math.cos(bearing);
		this.y = (float) Math.sin(bearing);
	}
	
	public Vec2(Vec2 vector) {
		this.x(vector.x());
		this.y(vector.y());
	}

	public Vec2 add(Vec2 v) {
		this.x += v.x;
		this.y += v.y;
		return this;
	}

	public Vec2 addFraction(Vec2 v, float f) {
		this.x += v.x * f;
		this.y += v.y * f;
		return this;
	}

	public Vec2 sub(Vec2 v) {
		this.x -= v.x;
		this.y -= v.y;
		return this;
	}

	public Vec2 mult(Vec2 v) {
		this.x *= v.x;
		this.y *= v.y;
		return this;
	}

	public Vec2 div(Vec2 v) {
		this.x /= v.x;
		this.y /= v.y;
		return this;
	}

	public Vec2 add(float f) {
		this.x += f;
		this.y += f;
		return this;
	}

	public Vec2 addFraction(float n, float f) {
		this.x += n * f;
		this.y += n * f;
		return this;
	}

	public Vec2 sub(float f) {
		this.x -= f;
		this.y -= f;
		return this;
	}

	public Vec2 mult(float f) {
		this.x *= f;
		this.y *= f;
		return this;
	}

	public Vec2 div(float f) {
		this.x /= f;
		this.y /= f;
		return this;
	}

	public float x() {
		return this.x;
	}

	public float y() {
		return this.y;
	}
	
	public Vec2 x(float x) {
		this.x = x;
		return this;
	}
	
	public Vec2 y(float y) {
		this.y = y;
		return this;
	}

	public Vec2 copy() {
		return new Vec2(this.x, this.y);
	}
	
	public Vec2 set(float x, float y) {
		this.x = x;
		this.y = y;
		return this;
	}
	
	public Vec2 addX(float x) {
		this.x += x;
		return this;
	}
	
	public Vec2 addY(float y) {
		this.y += y;
		return this;
	}
	
	public float amplitude() {
		return (float)Math.pow((Math.pow(x, 2)+Math.pow(y, 2)), 0.5);
	}
	
	public Vec2 scale(float scalar) {
		this.y *= scalar;
		return this;
	}
	
	public float getDotProduct(Vec2 v) {
		return (this.x*v.x)+(this.y*v.y);
	}
	
	public Vec2 normalize() {
		float amp = this.amplitude();
		this.x /= amp;
		this.y /= amp;
		return this;
	}
	
	public Vec2 toTrueVector() {
		float newX = this.getBearingTo(this.copy().add(this));
		float newY = this.amplitude();
		this.x = newX;
		this.y = newY;
		return this;
	}
	
	public Vec2 toPointVector() {
		Vec2 newVec = new Vec2(this.x).mult(this.y);
		this.x = newVec.x;
		this.y = newVec.y;
		return this;
	}

	public Vec2 getNormal() {
		return new Vec2(-this.y, this.x);
	}
	
	public Vec2 getNormal2() {
		return new Vec2(this.y, -this.x);
	}
	
	public float dist(Vec2 v) {
		double x = Math.max(this.x, v.x)-Math.min(this.x, v.x);
		double y = Math.max(this.y, v.y)-Math.min(this.y, v.y);
		return (float)Math.pow((Math.pow(x, 2)+Math.pow(y, 2)), 0.5);
	}

	public float getBearingTo(Vec2 endingPoint) {
		Vec2 originPoint = new Vec2(endingPoint.x - this.x, endingPoint.y - this.y);
		float bearingRadians = (float) Math.atan2(originPoint.y(), originPoint.x());
		return bearingRadians;
	}
	
	public boolean hasNegativeComponent() {
		if(this.x < 0) return true;
		if(this.y < 0) return true;
		return false;
	}
	
	public String print() {
		//System.out.print("["+this.x+","+this.y+"]");
		return "["+this.x+","+this.y+"]";
	}
}