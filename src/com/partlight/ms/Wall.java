package com.partlight.ms;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.partlight.ms.session.hazard.Bullet;
import com.partlight.ms.session.hazard.BulletTarget;
import com.partlight.ms.util.boundary.Boundary;

public class Wall extends Rectangle implements Boundary, BulletTarget {

	public Wall(float pX, float pY, float pWidth, float pHeight, VertexBufferObjectManager pVertexBufferObjectManager) {
		super(pX, pY, pWidth, pHeight, pVertexBufferObjectManager);

	}

	@Override
	public float getBoundaryHeight() {
		return this.getHeightScaled();
	}

	@Override
	public float getBoundaryWidth() {
		return this.getWidthScaled();
	}

	@Override
	public float getBoundaryX() {
		return this.getX();
	}

	@Override
	public float getBoundaryY() {
		return this.getY();
	}

	@Override
	public void onHit(Bullet b) {
	}

}
