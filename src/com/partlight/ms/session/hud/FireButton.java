package com.partlight.ms.session.hud;

import org.andengine.opengl.texture.region.ITiledTextureRegion;

public class FireButton extends BaseScreenComponent {

	public FireButton(float x, float y, ITiledTextureRegion pTiledTextureRegion) {
		super(x, y, pTiledTextureRegion);
	}

	@Override
	public float getScaleCenterX() {
		return 0;
	}

	@Override
	public float getScaleCenterY() {
		return 0;
	}
}
