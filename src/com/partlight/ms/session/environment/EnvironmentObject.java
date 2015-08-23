package com.partlight.ms.session.environment;

import org.andengine.opengl.texture.region.ITextureRegion;

import android.graphics.PointF;

public class EnvironmentObject {
	public ITextureRegion	textureRegion;
	public PointF			location;
	public boolean			fadeOut;
	public double			alpha;
	public double			fadeOutFactor;
	int						elapsedTicksUntilFadeOut;
	public int				ticksUntilFadeOut;

	public EnvironmentObject(float x, float y, float alpha, ITextureRegion textureRegion) {
		this(new PointF(x, y), alpha, textureRegion);
	}

	public EnvironmentObject(PointF location, float alpha, ITextureRegion textureRegion) {
		this.textureRegion = textureRegion;
		this.location = location;
		this.alpha = alpha;
		this.ticksUntilFadeOut = 60 * 2;
	}

	public void onUpdate(EnvironmentSpriteGroup env, float secondsElapsed) {
		if (this.fadeOut)
			if (this.elapsedTicksUntilFadeOut < this.ticksUntilFadeOut)
				this.elapsedTicksUntilFadeOut++;
			else {
				this.alpha -= this.fadeOutFactor;
				env.updateDrawing();

				if (this.alpha <= 0) {
					this.alpha = 0;
					env.removeEnvironmentObject(this);
				}
			}
	}
}
