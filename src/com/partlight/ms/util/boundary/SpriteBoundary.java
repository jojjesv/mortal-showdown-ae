package com.partlight.ms.util.boundary;

import org.andengine.entity.sprite.Sprite;

public class SpriteBoundary implements Boundary {

	private final Sprite sSprite;

	public SpriteBoundary(Sprite sprite) {
		if (sprite == null)
			throw new NullPointerException("Sprite cannot be equal to null!");

		this.sSprite = sprite;
	}

	@Override
	public float getBoundaryHeight() {
		return this.sSprite.getHeightScaled();
	}

	@Override
	public float getBoundaryWidth() {
		return this.sSprite.getWidthScaled();
	}

	@Override
	public float getBoundaryX() {
		return this.sSprite.getX();
	}

	@Override
	public float getBoundaryY() {
		return this.sSprite.getY();
	}

}
