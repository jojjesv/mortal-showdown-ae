package com.partlight.ms.entity;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.shader.PositionColorTextureCoordinatesShaderProgram;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.util.GLState;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.modifier.ease.EaseLinear;
import org.andengine.util.modifier.ease.IEaseFunction;

import com.partlight.ms.resource.EnvironmentVars;
import com.partlight.ms.shader.DissolveShaderProgram;
import com.partlight.ms.util.EntityUtils;
import com.partlight.ms.util.listener.OnResumeListener;
import com.partlight.ms.util.updatehandler.FloatValueModifier;

import android.opengl.GLES20;

public class DissolveAnimatedSprite extends Sprite implements OnResumeListener {

	private ITexture					tDissolveMap;
	private final DissolveShaderProgram	dspShader;
	private FloatValueModifier			fvmRatioAnimation;
	private boolean						isAnimating;

	public DissolveAnimatedSprite(float x, float y, ITextureRegion textureRegion, ITexture dissolveMap,
			VertexBufferObjectManager vertexBufferObjectManager) {
		this(x, y, textureRegion, dissolveMap, vertexBufferObjectManager, false);
	}

	public DissolveAnimatedSprite(float x, float y, ITextureRegion textureRegion, ITexture dissolveMap,
			VertexBufferObjectManager vertexBufferObjectManager, boolean invertShader) {
		super(x, y, textureRegion, vertexBufferObjectManager);

		this.dspShader = new DissolveShaderProgram(this.tDissolveMap = dissolveMap, 2, true);
		this.setInverted(invertShader);
		super.setShaderProgram(this.dspShader);
	}

	public void animate(float duration) {
		this.animate(duration, EaseLinear.getInstance());
	}

	public void animate(float duration, IEaseFunction ease) {
		super.setShaderProgram(this.dspShader);

		if (this.fvmRatioAnimation != null)
			this.unregisterUpdateHandler(this.fvmRatioAnimation);

		this.fvmRatioAnimation = new FloatValueModifier(0, 1, ease, duration) {
			@Override
			protected void onFinished() {
				DissolveAnimatedSprite.this.isAnimating = false;
				DissolveAnimatedSprite.this.onAnimationFinish();
				super.onFinished();
				EntityUtils.safetlyUnregisterUpdateHandler(DissolveAnimatedSprite.this, this);
			}

			@Override
			protected void onValueChanged(float value) {
				super.onValueChanged(value);
				DissolveAnimatedSprite.this.dspShader.setRatio(value);
			}
		};
		this.registerUpdateHandler(this.fvmRatioAnimation);
		this.isAnimating = true;
	}

	public boolean isAnimating() {
		return this.isAnimating;
	}

	public boolean isInverted() {
		return this.dspShader.isInverted();
	}

	protected void onAnimationFinish() {
		super.setShaderProgram(PositionColorTextureCoordinatesShaderProgram.getInstance());
	}

	@Override
	public void onAttached() {
		super.onAttached();
		EnvironmentVars.MAIN_CONTEXT.addOnResumeListener(this);
	}

	@Override
	public void onDetached() {
		super.onDetached();
		EnvironmentVars.MAIN_CONTEXT.removeOnResumeListener(this);
	}

	@Override
	public void onResume() {
		this.dspShader.setCompiled(false);
	}

	@Override
	protected void preDraw(GLState pGLState, Camera pCamera) {
		super.preDraw(pGLState, pCamera);

		pGLState.activeTexture(GLES20.GL_TEXTURE0 + this.dspShader.getActiveTextureId());
		this.tDissolveMap.bind(pGLState);
		pGLState.activeTexture(GLES20.GL_TEXTURE0);
	}

	public void setInverted(boolean inverted) {
		this.dspShader.setInverted(inverted);
	}
}
