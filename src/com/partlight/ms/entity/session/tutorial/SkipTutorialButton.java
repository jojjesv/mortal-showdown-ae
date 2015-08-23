package com.partlight.ms.entity.session.tutorial;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.shape.RectangularShape;
import org.andengine.entity.text.Text;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.VerticalAlign;
import org.andengine.util.modifier.ease.EaseCubicInOut;
import org.andengine.util.modifier.ease.EaseSineInOut;
import org.andengine.util.modifier.ease.IEaseFunction;

import com.partlight.ms.entity.DissolveAnimatedSprite;
import com.partlight.ms.resource.EnvironmentVars;
import com.partlight.ms.resource.ResourceManager;
import com.partlight.ms.resource.ResourceManager.StrokeTextureRegions;
import com.partlight.ms.session.hud.BaseScreenComponent;
import com.partlight.ms.util.EntityUtils;
import com.partlight.ms.util.updatehandler.FloatValueModifier;

public class SkipTutorialButton extends BaseScreenComponent {

	private final DelayModifier			dmShowDelay;
	private final FloatValueModifier	fvmShowFactor;
	private boolean						isShowing	= true;
	private Text						tText;

	public SkipTutorialButton() {
		super(0, 0);
		this.setZIndex(5);

		this.dmShowDelay = new DelayModifier(3.5f) {

			@Override
			protected void onModifierFinished(IEntity pItem) {
				super.onModifierFinished(pItem);
				if (SkipTutorialButton.this.isShowing)
					SkipTutorialButton.this.hide();
				else
					SkipTutorialButton.this.show();
			}
		};
		this.dmShowDelay.setAutoUnregisterWhenFinished(true);
		this.fvmShowFactor = new FloatValueModifier(1, 0, EaseCubicInOut.getInstance(), 0.25f) {

			@Override
			protected void onFinished() {
				super.onFinished();
				EntityUtils.safetlyUnregisterUpdateHandler(SkipTutorialButton.this, this);

				if (SkipTutorialButton.this.isShowing)
					SkipTutorialButton.this.postShow(SkipTutorialButton.this.dmShowDelay.isFinished());
			}

			@Override
			protected void onValueChanged(float value) {
				SkipTutorialButton.this.setY(SkipTutorialButton.this.getAnimationY(value));
			}
		};

		final DissolveAnimatedSprite BACKGROUND = this.createBackground();
		BACKGROUND.setScale(2);
		BACKGROUND.setInverted(true);
		this.setBackground(BACKGROUND);
		EntityUtils.alignEntity(BACKGROUND, HorizontalAlign.CENTER, VerticalAlign.TOP, 0, 0);

		this.fvmShowFactor.onUpdate(0);

		this.postShow(true);
	}

	protected DissolveAnimatedSprite createBackground() {
		return new DissolveAnimatedSprite(0, 0, StrokeTextureRegions.region_stroke_3, ResourceManager.btStrokeMap,
				EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager()) {
			@Override
			public void animate(float duration, IEaseFunction ease) {
				ResourceManager.btStrokeMap.load();
				super.animate(duration, ease);
			}

			@Override
			protected void onAnimationFinish() {
				super.onAnimationFinish();
				SkipTutorialButton.this.fadeInText();

				ResourceManager.btStrokeMap.unload();
			}

			@Override
			public void onAttached() {
				this.animate(0.25f, EaseCubicInOut.getInstance());
				ResourceManager.btStroke3.load();
			}

			@Override
			public void onDetached() {
				ResourceManager.btStroke3.unload();
			}
		};
	}

	protected void fadeInText() {
		this.tText = new Text(0, 0, ResourceManager.fFontMain, "SKIP TUTORIAL?",
				EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager());

		final RectangularShape BACKGROUND = this.getBackground();
		this.tText.setPosition((BACKGROUND.getWidth() - this.tText.getWidthScaled()) / 2,
				(BACKGROUND.getHeight() - this.tText.getHeightScaled()) / 2);

		BACKGROUND.attachChild(this.tText);

		EntityUtils.animateEntity(this.tText, 0.5f, EntityUtils.ANIMATION_FADE_IN, EaseSineInOut.getInstance());
	}

	protected float getAnimationY(float percent) {
		final float min = -this.getHeightScaled() * 0.8f;
		final float max = 16;
		return min + (max - min) * percent;
	}

	protected void hide() {
		this.isShowing = false;
		this.fvmShowFactor.setFrom(1);
		this.fvmShowFactor.setTo(0);
		this.fvmShowFactor.reset();
		this.unregisterUpdateHandler(this.fvmShowFactor);
		this.registerUpdateHandler(this.fvmShowFactor);
	}

	@Override
	public void onAttached() {
		this.getParent().sortChildren();
	}

	@Override
	protected void onManagedUpdate(float pSecondsElapsed) {
		super.onManagedUpdate(pSecondsElapsed);
	}

	protected void postShow(boolean registerDelayModifier) {
		this.dmShowDelay.reset();
		if (registerDelayModifier)
			this.registerEntityModifier(this.dmShowDelay);
	}

	public void show() {
		if (this.isShowing)
			return;
		this.isShowing = true;
		this.fvmShowFactor.setFrom(0);
		this.fvmShowFactor.setTo(1);
		this.fvmShowFactor.reset();
		this.unregisterUpdateHandler(this.fvmShowFactor);
		this.registerUpdateHandler(this.fvmShowFactor);
	}
}
