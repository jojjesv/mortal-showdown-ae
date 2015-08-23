package com.partlight.ms.scene.mainmenu;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.exception.OutOfCharactersException;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.FontUtils;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.modifier.IModifier;
import org.andengine.util.modifier.ease.EaseCubicInOut;
import org.andengine.util.modifier.ease.EaseSineIn;
import org.andengine.util.modifier.ease.EaseSineInOut;
import org.andengine.util.modifier.ease.EaseSineOut;

import com.partlight.ms.entity.ShadowedText;
import com.partlight.ms.resource.EnvironmentVars;
import com.partlight.ms.resource.ResourceManager;
import com.partlight.ms.util.EntityModifierAdapter;
import com.partlight.ms.util.EntityUtils;
import com.partlight.ms.util.Fade;
import com.partlight.ms.util.updatehandler.FloatValueModifier;

public class MainMenuNavDemo {

	private final MainMenuScene		mmsContext;
	private ShadowedText			stNavDemoText;
	private boolean					canUserInteractWithNavDemo;
	private int						navDemoTextIndex;
	private float[]					iconX;
	private float[]					iconY;
	private FloatValueModifier[]	fvmSwipeIconsBrightness;

	private ScaleModifier[] smSwipeIconsModifiers;

	public MainMenuNavDemo(MainMenuScene context) {
		this.mmsContext = context;
	}

	private void animateIcon(boolean scaleUp, int iconIndex) {

		Sprite indexedSwipeIcon = null;
		float swipeIconScaleX = 0f, swipeIconScaleY = 0f;

		if (this.iconX == null) {
			this.iconX = new float[] {
					this.mmsContext.sSwipeAchievements.getX(),
					this.mmsContext.sSwipeOptions.getX(),
					this.mmsContext.sSwipeLeaderboard.getX(),
					this.mmsContext.sSwipeStore.getX()
			};
			this.iconY = new float[] {
					this.mmsContext.sSwipeAchievements.getY(),
					this.mmsContext.sSwipeOptions.getY(),
					this.mmsContext.sSwipeLeaderboard.getY(),
					this.mmsContext.sSwipeStore.getY()
			};
		}

		switch (iconIndex) {
		case 0:
			indexedSwipeIcon = this.mmsContext.sSwipeAchievements;
			swipeIconScaleX = this.mmsContext.sSwipeAchievements.getWidth();
			swipeIconScaleY = this.mmsContext.sSwipeAchievements.getHeight() / 2f;
			break;
		case 1:
			indexedSwipeIcon = this.mmsContext.sSwipeOptions;
			swipeIconScaleX = this.mmsContext.sSwipeOptions.getWidth() / 2f;
			swipeIconScaleY = 0f;
			break;
		case 2:
			indexedSwipeIcon = this.mmsContext.sSwipeLeaderboard;
			swipeIconScaleX = 0f;
			swipeIconScaleY = this.mmsContext.sSwipeLeaderboard.getHeight() / 2f;
			break;
		case 3:
			indexedSwipeIcon = this.mmsContext.sSwipeStore;
			swipeIconScaleX = this.mmsContext.sSwipeStore.getWidth() / 2f;
			swipeIconScaleY = this.mmsContext.sSwipeStore.getHeight();
			break;
		}

		// Neccessary final modifier
		final Sprite SWIPE_ICON = indexedSwipeIcon;
		final float ICON_SCALE_X = swipeIconScaleX;
		final float ICON_SCALE_Y = swipeIconScaleY;
		final float SWIPE_ICON_X = this.iconX[iconIndex];
		final float SWIPE_ICON_Y = this.iconY[iconIndex];
		final boolean SCALE_UP = scaleUp;

		this.smSwipeIconsModifiers[iconIndex] = new ScaleModifier(0.2f, 0f, 0f, EaseCubicInOut.getInstance()) {
			@Override
			protected void onSetValues(IEntity pEntity, float pPercentageDone, float pScaleA, float pScaleB) {
				if (!SCALE_UP)
					pPercentageDone = 1f - pPercentageDone;

				pScaleA = 2f + 0.4f * pPercentageDone;
				pScaleB = pScaleA;

				super.onSetValues(pEntity, pPercentageDone, pScaleA, pScaleB);
				pEntity.setX(SWIPE_ICON_X - (ICON_SCALE_X - pEntity.getScaleCenterX()) * pPercentageDone);
				pEntity.setY(SWIPE_ICON_Y - (ICON_SCALE_Y - pEntity.getScaleCenterY()) * pPercentageDone);
			}
		};
		this.smSwipeIconsModifiers[iconIndex].setAutoUnregisterWhenFinished(true);

		SWIPE_ICON.registerEntityModifier(this.smSwipeIconsModifiers[iconIndex]);

	}

	private void animateIconBrightness(boolean fadeOut, int iconIndex) {
		if (this.fvmSwipeIconsBrightness[iconIndex] != null)
			this.fvmSwipeIconsBrightness[iconIndex].reset();
		else {

			final int ICON_INDEX = iconIndex;
			this.fvmSwipeIconsBrightness[iconIndex] = new FloatValueModifier(0f, 0f, EaseSineInOut.getInstance(), 0.2f) {
				@Override
				protected void onFinished() {
					super.onFinished();
					EntityUtils.safetlyUnregisterUpdateHandler(MainMenuNavDemo.this.mmsContext,
							MainMenuNavDemo.this.fvmSwipeIconsBrightness[ICON_INDEX]);
				}

				@Override
				protected void onValueChanged(float value) {
					super.onValueChanged(value);

					switch (ICON_INDEX) {
					case 0:
						MainMenuNavDemo.this.mmsContext.swipeAchievementsBrightness = value;
						break;
					case 1:
						MainMenuNavDemo.this.mmsContext.swipeOptionsBrightness = value;
						break;
					case 2:
						MainMenuNavDemo.this.mmsContext.swipeLeaderboardBrightness = value;
						break;
					case 3:
						MainMenuNavDemo.this.mmsContext.swipeStoreBrightness = value;
						break;
					}
				}
			};
		}

		if (fadeOut) {
			this.fvmSwipeIconsBrightness[iconIndex].setFrom(1f);
			this.fvmSwipeIconsBrightness[iconIndex].setTo(0.5f);
		} else {
			this.fvmSwipeIconsBrightness[iconIndex].setFrom(0.5f);
			this.fvmSwipeIconsBrightness[iconIndex].setTo(1f);
		}

		this.mmsContext.registerUpdateHandler(this.fvmSwipeIconsBrightness[iconIndex]);
	}

	public boolean canUserInteract() {
		return this.canUserInteractWithNavDemo;
	}

	private void finalizeText() {
		this.animateIcon(false, 3);
		this.animateIconBrightness(false, 0);
		this.animateIconBrightness(false, 1);
		this.animateIconBrightness(false, 2);

		EntityUtils.animateEntity(this.stNavDemoText, 0.25f, EntityUtils.ANIMATION_SCALE_OUT_FADE_OUT, EaseCubicInOut.getInstance(),
				EntityUtils.getDetachDisposeListener());

		this.mmsContext.fFade.runOnFadeOut(new Runnable() {
			@Override
			public void run() {
				MainMenuNavDemo.this.mmsContext.fFade.detachSelf();
				MainMenuNavDemo.this.mmsContext.reattachSwipeIcons();
				MainMenuNavDemo.this.mmsContext.isNavDemoShowing = false;
				MainMenuNavDemo.this.mmsContext.nullifyNavDemo();
			}
		});
		this.mmsContext.fFade.setEase(EaseSineIn.getInstance());
		this.mmsContext.fFade.hide();
	}

	public boolean onTouchEvent(TouchEvent touchEvent) {

		if (touchEvent.isActionUp() && this.canUserInteract()) {
			if (this.navDemoTextIndex == MainMenuNavDemoStrings.NAV_DEMO_STRINGS.length - 1)
				this.finalizeText();
			else
				this.showNextText();

			this.canUserInteractWithNavDemo = false;

			return true;
		}

		return false;
	}

	private void postFadeIn() {
		EntityUtils.animateEntity(this.stNavDemoText, 0.25f, EntityUtils.ANIMATION_SCALE_OUT_FADE_IN, EaseCubicInOut.getInstance(),
				new EntityModifierAdapter() {
					@Override
					public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
						MainMenuNavDemo.this.canUserInteractWithNavDemo = true;
					}
				});
		this.mmsContext.fFade.attachChild(this.stNavDemoText);
	}

	public void showDemo() {

		this.stNavDemoText = new ShadowedText(0f, 0f, ResourceManager.fFontMain, MainMenuNavDemoStrings.NAV_DEMO_STRING_LONGEST,
				EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager()) {
			@Override
			public void setText(CharSequence pText) throws OutOfCharactersException {
				super.setText(pText);

				String longestTextLine = "";

				final String[] LINES = pText.toString().split("\n");
				for (final String s : LINES)
					if (s.length() > longestTextLine.length())
						longestTextLine = s;

				final float WIDTH = FontUtils.measureText(super.getFont(), longestTextLine);
				final float HEIGHT = (super.getFont().getLineHeight() * LINES.length);

				this.setX((EnvironmentVars.MAIN_CONTEXT.width() - WIDTH) / 2f);
				this.setY((EnvironmentVars.MAIN_CONTEXT.height() - HEIGHT) / 2f);

				try {
					this.setScaleCenter(WIDTH / 2f, HEIGHT / 2f);
				} catch (final NullPointerException ex) {
				}
			}
		};

		this.stNavDemoText.setScale(1.85f);
		this.stNavDemoText.setHorizontalAlign(HorizontalAlign.CENTER);
		this.stNavDemoText.setText(MainMenuNavDemoStrings.NAV_DEMO_STRINGS[0]);

		final Fade FADE = this.mmsContext.fFade;

		this.mmsContext.sSwipeAchievements.detachSelf();
		this.mmsContext.sSwipeLeaderboard.detachSelf();
		this.mmsContext.sSwipeOptions.detachSelf();
		this.mmsContext.sSwipeStore.detachSelf();

		FADE.attachChild(this.mmsContext.sSwipeAchievements);
		FADE.attachChild(this.mmsContext.sSwipeLeaderboard);
		FADE.attachChild(this.mmsContext.sSwipeOptions);
		FADE.attachChild(this.mmsContext.sSwipeStore);

		FADE.setColor(4f / 255f, 34f / 255f, 36f / 255f);
		FADE.setEase(EaseSineOut.getInstance());
		FADE.setDuration(0.33f);
		FADE.runOnFadeIn(new Runnable() {
			@Override
			public void run() {
				MainMenuNavDemo.this.postFadeIn();
			}
		});
		FADE.show(0.66f);
	}

	private void showNextText() {
		this.navDemoTextIndex++;

		switch (this.navDemoTextIndex) {
		case 1:
			this.smSwipeIconsModifiers = new ScaleModifier[4];
			this.animateIcon(true, 0);

			this.fvmSwipeIconsBrightness = new FloatValueModifier[4];
			this.animateIconBrightness(true, 1);
			this.animateIconBrightness(true, 2);
			this.animateIconBrightness(true, 3);
			break;
		case 2:
			this.animateIcon(true, 1);
			this.animateIcon(false, 0);
			this.animateIconBrightness(true, 0);
			this.animateIconBrightness(false, 1);
			break;
		case 3:
			this.animateIcon(true, 2);
			this.animateIcon(false, 1);
			this.animateIconBrightness(true, 1);
			this.animateIconBrightness(false, 2);
			break;
		case 4:
			this.animateIcon(true, 3);
			this.animateIcon(false, 2);
			this.animateIconBrightness(true, 2);
			this.animateIconBrightness(false, 3);
			break;
		}

		EntityUtils.animateEntity(this.stNavDemoText, 0.25f, EntityUtils.ANIMATION_FADE_OUT, EaseSineInOut.getInstance(),
				new EntityModifierAdapter() {
					@Override
					public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
						MainMenuNavDemo.this.stNavDemoText
								.setText(MainMenuNavDemoStrings.NAV_DEMO_STRINGS[MainMenuNavDemo.this.navDemoTextIndex]);
						MainMenuNavDemo.this.stNavDemoText.setAlpha(1f);

						EntityUtils.animateEntity(MainMenuNavDemo.this.stNavDemoText, 0.25f, EntityUtils.ANIMATION_FADE_IN,
								EaseSineInOut.getInstance(), new EntityModifierAdapter() {
							@Override
							public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
								MainMenuNavDemo.this.canUserInteractWithNavDemo = true;
							}
						});
					}
				});
	}
}
