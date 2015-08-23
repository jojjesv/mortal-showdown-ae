package com.partlight.ms.session.character;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.Entity;
import org.andengine.entity.modifier.ColorModifier;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.util.color.Color;

import com.partlight.ms.resource.EnvironmentVars;
import com.partlight.ms.shader.TintShaderProgram;
import com.partlight.ms.util.EaseJump;
import com.partlight.ms.util.Ruler;

import android.util.Log;

public class CharacterOutOfViewArrow extends Entity {

	private final Sprite				sIndicator;
	private final Sprite				sIndicatorShadow;
	private Character					cTarget;
	private boolean						isCharOutOfView;
	private final LoopEntityModifier	INDICATOR_ANIMATION;

	public CharacterOutOfViewArrow(TextureRegion indicatorTextureRegion) {

		this.sIndicator = new Sprite(0f, 0f, indicatorTextureRegion, EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager());
		this.sIndicatorShadow = new Sprite(0f, 0f, indicatorTextureRegion, EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager());

		this.sIndicator.setScaleCenter(0, 0);
		this.sIndicator.setScale(2f);
		this.sIndicator.setRotationCenter(this.sIndicator.getWidthScaled() / 2f, this.sIndicator.getHeightScaled() / 2f);

		this.sIndicatorShadow.setScaleCenter(this.sIndicator.getScaleCenterX(), this.sIndicator.getScaleCenterY());
		this.sIndicatorShadow.setScale(this.sIndicator.getScaleX(), this.sIndicator.getScaleY());
		this.sIndicatorShadow.setRotationCenter(this.sIndicator.getRotationCenterX(), this.sIndicator.getRotationCenterY());
		this.INDICATOR_ANIMATION = new LoopEntityModifier(
				new ColorModifier(0.5f, Color.WHITE, new Color(1f, 0.75f, 0.75f), EaseJump.getInstance()));

		this.sIndicator.setShaderProgram(TintShaderProgram.getNonMultipliedInstance());
	}

	@Override
	public void dispose() {
		super.dispose();
	}

	public Character getCharacter() {
		return this.cTarget;
	}

	public boolean isCharOutOfView() {
		return this.isCharOutOfView;
	}

	@Override
	protected void onManagedUpdate(float pSecondsElapsed) {
		super.onManagedUpdate(pSecondsElapsed);
		this.INDICATOR_ANIMATION.onUpdate(pSecondsElapsed, this);
		this.onUpdateIndicator();
	}

	protected void onUpdateIndicator() {

		final float WIDTH = this.cTarget.getSkin().getTorso().getWidthScaled();
		final float HEIGHT = this.cTarget.getSkin().getTorso().getHeightScaled();

		// @formatter:on
		try {
			final Camera CAMERA = EnvironmentVars.MAIN_CONTEXT.getCamera();

			//@formatter:off
			this.isCharOutOfView = this.cTarget.getX() + WIDTH / 2f < CAMERA.getXMin();
			this.isCharOutOfView = this.isCharOutOfView || this.cTarget.getX() + WIDTH / 2f > CAMERA.getXMax();
			this.isCharOutOfView = this.isCharOutOfView || this.cTarget.getY() + HEIGHT / 2f < CAMERA.getYMin();
			this.isCharOutOfView = this.isCharOutOfView || this.cTarget.getY() + HEIGHT / 2f > CAMERA.getYMax();

			// @formatter:on
			if (this.isCharOutOfView()) {
				try {
					this.attachChild(this.sIndicatorShadow);
					this.attachChild(this.sIndicator);
				} catch (final IllegalStateException ex) {

				}

				if (!this.sIndicatorShadow.getTextureRegion().getTexture().isLoadedToHardware())
					this.sIndicatorShadow.getTextureRegion().getTexture().load();

				float indicatorX = this.cTarget.getX() - CAMERA.getXMin();

				final float X_MIN = 8f;
				final float X_MAX = CAMERA.getWidth() - X_MIN - this.sIndicator.getWidthScaled();

				float indicatorY = this.cTarget.getY() - CAMERA.getYMin();

				final float Y_MIN = 8f;
				final float Y_MAX = CAMERA.getHeight() - Y_MIN - this.sIndicator.getHeightScaled();

				if (indicatorX < X_MIN)
					indicatorX = X_MIN;
				if (indicatorX > X_MAX)
					indicatorX = X_MAX;

				if (indicatorY < Y_MIN)
					indicatorY = Y_MIN;
				if (indicatorY > Y_MAX)
					indicatorY = Y_MAX;

				this.sIndicatorShadow.setPosition(indicatorX + this.sIndicatorShadow.getScaleX(),
						indicatorY + this.sIndicatorShadow.getScaleY());
				this.sIndicator.setPosition(indicatorX, indicatorY);

				//@formatter:off
				this.sIndicatorShadow.setRotation((float) Math.toDegrees(Ruler.getAngle(
								this.sIndicatorShadow.getX() + this.sIndicatorShadow.getWidthScaled() / 2f,
								this.sIndicatorShadow.getY() + this.sIndicatorShadow.getHeightScaled() / 2f,
								this.cTarget.getX() - CAMERA.getXMin() + WIDTH / 2f,
								this.cTarget.getY() - CAMERA.getYMin() + HEIGHT / 2f)));
				this.sIndicator.setRotation(this.sIndicatorShadow.getRotation());
			} else {
				if (this.sIndicatorShadow.getTextureRegion().getTexture().isLoadedToHardware())
					this.sIndicatorShadow.getTextureRegion().getTexture().unload();
				this.sIndicator.detachSelf();
				this.sIndicatorShadow.detachSelf();
			}
		} catch (final NullPointerException ex) {
			// @formatter:on
			Log.e("Mortal Showdown", "(" + CharacterOutOfViewArrow.class.getSimpleName() + ") target character is null!", ex);
		}
	}

	public void setCharacter(Character c) {
		this.cTarget = c;
	}
}
