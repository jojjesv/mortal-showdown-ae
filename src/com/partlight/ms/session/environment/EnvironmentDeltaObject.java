package com.partlight.ms.session.environment;

import org.andengine.opengl.texture.region.ITextureRegion;

import android.graphics.PointF;

public class EnvironmentDeltaObject extends EnvironmentObject {

	public PointF delta;

	public EnvironmentDeltaObject(float x, float y, float alpha, ITextureRegion textureRegion) {
		super(x, y, alpha, textureRegion);
	}

	@Override
	public void onUpdate(EnvironmentSpriteGroup env, float secondsElapsed) {
		super.location.x += this.delta.x;
		super.location.y += this.delta.y;

		env.updateDrawing();

		super.onUpdate(env, secondsElapsed);
	}
}
