package io.m3.sql.apt.util;

public final class Tuple<X, Y> {
	
	public final X x;
	public final Y y;

	public Tuple(X x, Y y) {
		this.x = x;
		this.y = y;
	}

	public X getX() {
		return x;
	}

	public Y getY() {
		return y;
	}
	
}
