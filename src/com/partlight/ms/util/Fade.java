package com.partlight.ms.util;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.color.Color;
import org.andengine.util.modifier.IModifier;
import org.andengine.util.modifier.IModifier.IModifierListener;
import org.andengine.util.modifier.ease.EaseSineInOut;
import org.andengine.util.modifier.ease.IEaseFunction;

import com.partlight.ms.resource.EnvironmentVars;

public class Fade extends Rectangle {

	private AlphaModifier	amAlphaModifier;
	private float			alphaFactor;
	private Runnable		rOnFadeIn;
	private Runnable		rOnFadeOut;
	private float			fadeDuration;
	private boolean			isShowing;
	private IEaseFunction	ease;

	public Fade(VertexBufferObjectManager pVertexBufferObjectManager) {
		super(0f, 0f, EnvironmentVars.MAIN_CONTEXT.width(), EnvironmentVars.MAIN_CONTEXT.height(), pVertexBufferObjectManager);
		this.ease = EaseSineInOut.getInstance();
		this.alphaFactor = 1f;
		this.setColor(Color.BLACK);
		this.setAlpha(0);
		this.setDuration(0.5f);
	}

	private void changeVisibility(boolean fadeIn) {
		if (this.isAnimating())
			return;

		final boolean FADE_IN = fadeIn;
		final float FADE_DURATION = this.fadeDuration;

		EnvironmentVars.MAIN_CONTEXT.runOnUpdateThread(new Runnable() {
			@Override
			public void run() {
				Fade.this.clearEntityModifiers();

				if (FADE_IN)
					Fade.this.amAlphaModifier = new AlphaModifier(FADE_DURATION, 0f, 1f, Fade.this.ease);
				else
					Fade.this.amAlphaModifier = new AlphaModifier(FADE_DURATION, 1f, 0f, Fade.this.ease);

				//@formatter:off
				Fade.this.amAlphaModifier
						.addModifierListener(new IModifierListener<IEntity>() {
							@Override
							public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
								if (FADE_IN)
									Fade.this.showInstantly();
								else
									Fade.this.hideInstantly();
							}
							
							@Override
							public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) { }
						});
				//@formatter:on

				Fade.this.amAlphaModifier.setAutoUnregisterWhenFinished(true);
				Fade.this.registerEntityModifier(Fade.this.amAlphaModifier);
			}
		});
	}

	public IEaseFunction getEase() {
		return this.ease;
	}

	public float getFadeDuration() {
		return this.fadeDuration;
	}

	public void hide() {
		this.isShowing = false;
		this.changeVisibility(false);
	}

	public void hideInstantly() {
		this.setAlpha(0f);
		this.isShowing = false;
		this.onFadeOut();
	}

	public boolean isAnimating() {
		if (this.amAlphaModifier == null)
			return false;
		return !this.amAlphaModifier.isFinished();
	}

	public boolean isShowing() {
		return this.isShowing;
	}

	protected void onFadeIn() {
		if (Fade.this.rOnFadeIn != null) {
			Fade.this.rOnFadeIn.run();
			Fade.this.rOnFadeIn = null;
		}
	}

	protected void onFadeOut() {
		if (Fade.this.rOnFadeOut != null) {
			Fade.this.rOnFadeOut.run();
			Fade.this.rOnFadeOut = null;
		}
	}

	public void runOnFadeIn(Runnable onFadeIn) {
		this.rOnFadeIn = onFadeIn;
	}

	public void runOnFadeOut(Runnable onFadeOut) {
		this.rOnFadeOut = onFadeOut;
	}

	@Override
	public void setAlpha(float pAlpha) {
		super.setAlpha(pAlpha * this.alphaFactor);

		final int childSize = this.getChildCount();

		for (int i = 0; i < childSize; i++)
			this.getChildByIndex(i).setAlpha(pAlpha);
	}

	public void setDuration(float duration) {
		this.fadeDuration = duration;
	}

	public void setEase(IEaseFunction ease) {
		this.ease = ease;
	}

	public void show() {
		this.show(1f);
	}

	public void show(float factor) {
		this.alphaFactor = factor;
		this.isShowing = true;
		this.changeVisibility(true);
	}

	public void showInstantly() {
		this.setAlpha(1f);
		this.isShowing = true;
		this.onFadeIn();
	}
}
