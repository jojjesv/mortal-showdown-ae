package com.partlight.ms.util.boundary;

public class BoundaryAdapter implements Boundary {

	private float	x;
	private float	y;
	private float	width;
	private float	height;

	public BoundaryAdapter(float x, float y, float width, float height) {
		this.set(x, y, width, height);
	}

	@Override
	public float getBoundaryHeight() {
		return this.height;
	}

	@Override
	public float getBoundaryWidth() {
		return this.width;
	}

	@Override
	public float getBoundaryX() {
		return this.x;
	}

	@Override
	public float getBoundaryY() {
		return this.y;
	}

	public void set(float x, float y, float width, float height) {
		this.x = x;
		this.y = y;
		this.height = height;
		this.width = width;
	}

}
