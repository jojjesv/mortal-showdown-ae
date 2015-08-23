package com.partlight.ms.session.hud;

import java.util.ArrayList;

import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.Entity;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.shape.RectangularShape;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.color.Color;
import org.andengine.util.modifier.IModifier;
import org.andengine.util.modifier.ease.EaseBounceOut;
import org.andengine.util.modifier.ease.EaseLinear;

import com.partlight.ms.activity.GameActivity.GooglePlayConstants;
import com.partlight.ms.entity.ShadowedText;
import com.partlight.ms.entity.session.notification.Notification;
import com.partlight.ms.entity.session.notification.Notification.NotificationEffects;
import com.partlight.ms.resource.EnvironmentVars;
import com.partlight.ms.resource.ResourceManager;
import com.partlight.ms.resource.ResourceManager.HudRegions;
import com.partlight.ms.scene.session.SessionScene;
import com.partlight.ms.session.achievement.AchievementsManager;
import com.partlight.ms.shader.ScissorShaderProgram;
import com.partlight.ms.util.EntityModifierAdapter;
import com.partlight.ms.util.EntityUtils;
import com.partlight.ms.util.listener.OnResumeListener;

public class ComboTracker extends BaseScreenComponent implements OnResumeListener {

	private class ComboTrackerUpdateHandler implements IUpdateHandler {

		private static final float	INTERVAL	= 0.25f;
		private float				elapsedIntervalSeconds;

		@Override
		public void onUpdate(float pSecondsElapsed) {
			int virtualIntervalMultiplier = ComboTracker.this.getMultiplier();

			final int INTERVAL_MULTIPLIER_PEAK = 52;

			if (virtualIntervalMultiplier > INTERVAL_MULTIPLIER_PEAK)
				virtualIntervalMultiplier = INTERVAL_MULTIPLIER_PEAK;

			final float interval = ComboTrackerUpdateHandler.INTERVAL * (1f - virtualIntervalMultiplier / 62f);

			if (this.elapsedIntervalSeconds >= interval) {
				ComboTracker.this
						.increment((int) (-3 - 0.005f * ((ComboTracker.this.multiplier < 42) ? ComboTracker.this.multiplier : 42)));
				this.elapsedIntervalSeconds = 0;
			}

			this.elapsedIntervalSeconds += pSecondsElapsed;
		}

		@Override
		public void reset() {
		}
	}

	public static class NotificationConstants {
		public static final float	NOTIFICATION_SCALE			= 1.5f;
		public static final float	NOTIFICATION_DURATION		= 4f;
		public static final Color	NOTIFICATION_COLOR_UPGRADE	= new Color(0.35f, 0.35f, 1f);
		public static final Color	NOTIFICATION_COLOR_NEW		= new Color(0.35f, 1f, 0.35f);
		public static final Color	NOTIFICATION_COLOR_MESSAGE	= new Color(1f, 1f, 0.6f);
		public static final Color	NOTIFICATION_COLOR_WAVE		= new Color(1f, 0.2f, 0.2f);
	}

	private final ShadowedText				stMultiplier;
	private final ComboTrackerUpdateHandler	updateHandler;
	private final Sprite					sProgressBarCanvas;
	private final Sprite					sProgressBar;
	private final Sprite					sProgressBarBackground;
	private int								percentOnCurrentCombo;
	private int								multiplier;
	private int								highestMultiplier;
	private int								multiplierSinceIncrement;
	private ArrayList<Notification>			gtNotifications;
	private Entity							eNotificationContainer;
	private boolean							animateText;

	private final ScissorShaderProgram PROGRESS_BAR_SHADER;

	public ComboTracker(float x, float y, VertexBufferObjectManager pVertexBufferObjectManager, SessionScene context) {
		super(x, y);

		this.setContext(context);

		this.stMultiplier = new ShadowedText(0, 0, ResourceManager.fFontMain, "XXX", pVertexBufferObjectManager);

		this.sProgressBarCanvas = new Sprite(0, 0, HudRegions.region_combo_canvas, pVertexBufferObjectManager);
		this.sProgressBarBackground = new Sprite(0, 0, HudRegions.region_combo_back, pVertexBufferObjectManager);
		this.sProgressBar = new Sprite(0, 0, HudRegions.region_combo_fore, pVertexBufferObjectManager);

		this.PROGRESS_BAR_SHADER = new ScissorShaderProgram();
		this.sProgressBar.setShaderProgram(this.PROGRESS_BAR_SHADER);

		this.centerEntityInBoundary(this.sProgressBarCanvas);
		this.centerEntityInBoundary(this.sProgressBar);
		this.centerEntityInBoundary(this.sProgressBarBackground);

		final float progressBarXPadding = 16f;

		this.sProgressBar.setX(this.sProgressBar.getX() - progressBarXPadding);
		this.sProgressBarBackground.setX(this.sProgressBarBackground.getX() - progressBarXPadding);

		this.sProgressBarCanvas.setRotation(-1.5f);
		this.sProgressBarBackground.setRotation(-1.5f);
		this.sProgressBar.setRotation(-1.5f);
		this.setAlpha(1);

		this.animateText = true;

		this.alignComboText();
		this.setMultiplier(1);
		this.stMultiplier.setColor(Color.WHITE);

		this.registerUpdateHandler(this.updateHandler = new ComboTrackerUpdateHandler());
	}

	private void alignComboText() {

		final float textWidth = this.stMultiplier.getWidth();

		this.stMultiplier.setRotationCenter(textWidth / 2f, this.stMultiplier.getFont().getLineHeight() / 2f);
		this.stMultiplier.setRotation(12.5f);

		this.stMultiplier.setScale(4f);

		float multiplierX = this.sProgressBarBackground.getX() + this.sProgressBarBackground.getWidthScaled();
		final float multiplierY = this.sProgressBarCanvas.getY()
				+ (this.sProgressBarCanvas.getHeightScaled() - this.stMultiplier.getHeightScaled()) / 2f
				+ EntityUtils.getYDelta(this.stMultiplier);

		// Padding
		multiplierX += 16;

		this.stMultiplier.setPosition(multiplierX, multiplierY);
	}

	private void animateText(final boolean asIncrementing) {

		if (!this.animateText)
			return;

		EnvironmentVars.MAIN_CONTEXT.runOnUpdateThread(new Runnable() {
			@Override
			public void run() {
				ComboTracker.this.stMultiplier.setScale(4f);
				ComboTracker.this.stMultiplier.clearEntityModifiers();
				ComboTracker.this.alignComboText();

				if (asIncrementing) {
					ComboTracker.this.stMultiplier.hideShadow();

					EntityUtils.animateEntity(ComboTracker.this.stMultiplier, 0.25f, EntityUtils.ANIMATION_JUMP_IN_LOW_INTESTIVITY,
							EaseLinear.getInstance(), new EntityModifierAdapter() {
						@Override
						public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
							ComboTracker.this.stMultiplier.showShadow();
						}
					});
				} else {
					ComboTracker.this.stMultiplier.showShadow();
					ComboTracker.this.stMultiplier.setAlpha(1);

					final float textScale = ComboTracker.this.stMultiplier.getScaleX();

					final ScaleModifier mod = new ScaleModifier(0.25f, textScale + 0.5f, textScale, EaseBounceOut.getInstance());
					mod.setAutoUnregisterWhenFinished(true);
					ComboTracker.this.stMultiplier.registerEntityModifier(mod);
				}
			}
		});

	}

	private void centerEntityInBoundary(RectangularShape entity) {
		entity.setScaleCenter(0, 0);
		entity.setScale(2f);
		entity.setX(this.getBoundaryX() + (this.getBoundaryWidth() - entity.getWidthScaled()) / 2f);
		entity.setY(this.getBoundaryY() + (this.getBoundaryHeight() - entity.getHeightScaled()) / 2f);
	}

	@Override
	public float getBoundaryHeight() {
		return 76f;
	}

	@Override
	public float getBoundaryWidth() {
		return 320f;
	}

	@Override
	public float getBoundaryX() {
		return 16f;
	}

	@Override
	public float getBoundaryY() {
		return 8f;
	}

	public int getMultiplier() {
		return this.multiplier;
	}

	public void increment(int percent) {

		this.percentOnCurrentCombo += percent;

		if (this.percentOnCurrentCombo >= 100) {
			this.percentOnCurrentCombo = this.percentOnCurrentCombo - 100;
			this.setMultiplier(this.getMultiplier() + 1);
			this.animateText(this.getMultiplier() < 20 && true);
		} else if (this.percentOnCurrentCombo < 0)
			if (this.multiplier <= 1)
				this.percentOnCurrentCombo = 0;
			else {
				this.percentOnCurrentCombo = 100 + this.percentOnCurrentCombo;
				if (this.percentOnCurrentCombo > 100)
					this.percentOnCurrentCombo = 100;
				this.setMultiplier(this.getMultiplier() - 1);
				this.animateText(false);
			}

		if (percent > 0)
			this.multiplierSinceIncrement = this.getMultiplier();

		this.PROGRESS_BAR_SHADER.setPercent(this.percentOnCurrentCombo / 100f);
	}

	public void notify(Notification notification) {

		if (this.gtNotifications == null)
			this.gtNotifications = new ArrayList<Notification>();

		if (this.eNotificationContainer == null) {
			this.eNotificationContainer = new Entity(0f,
					this.sProgressBarCanvas.getHeight() + ((SessionScene) this.getContext()).getScoreTracker().getHeight() + 8f);
			this.eNotificationContainer.setScale(1f / this.sProgressBarCanvas.getScaleX(), 1f / this.sProgressBarCanvas.getScaleY());

			this.sProgressBarCanvas.attachChild(this.eNotificationContainer);
		}

		final ShadowedText NOTIFICATION_TEXT = notification.getShadowedText();

		NOTIFICATION_TEXT.setZIndex(((SessionScene) this.getContext()).getLevel().getMapHeight());

		for (final Notification n : this.gtNotifications)
			n.pushShadowedText(NOTIFICATION_TEXT.getHeightScaled());

		notification.promptRemovalFromListOnDispose(this.gtNotifications);
		notification.onUpdate(0f);
		this.gtNotifications.add(notification);
		this.eNotificationContainer.attachChild(NOTIFICATION_TEXT);
	}

	public void notify(String message, final Color color) {
		this.notify(message, color, NotificationConstants.NOTIFICATION_SCALE);
	}

	public void notify(String message, final Color color, float scale) {
		this.notify(message, color, scale, NotificationEffects.NONE);
	}

	public void notify(String message, final Color color, float scale, final NotificationEffects effect) {
		this.notify(message, color, scale, effect, false);
	}

	public void notify(String message, final Color color, float scale, final NotificationEffects effect, boolean endless) {

		final ShadowedText NOTIF_TEXT = new ShadowedText(0f, 0f, ResourceManager.fFontMain, message,
				EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager());

		NOTIF_TEXT.setColor(color);
		NOTIF_TEXT.setScaleCenter(0, 0);
		NOTIF_TEXT.setScale(scale);

		this.notify(new Notification(NOTIF_TEXT, endless, effect));
	}

	@Override
	public void onAttached() {
		super.onAttached();
		try {
			this.attachChild(this.sProgressBarCanvas);
			this.attachChild(this.sProgressBarBackground);
			this.attachChild(this.sProgressBar);
			this.attachChild(this.stMultiplier);
		} catch (final IllegalStateException ex) {

		}

		EnvironmentVars.MAIN_CONTEXT.addOnResumeListener(this);
	}

	@Override
	public void onDetached() {
		this.detachChildren();
		EnvironmentVars.MAIN_CONTEXT.removeOnResumeListener(this);

		super.onDetached();
	}

	@Override
	protected void onManagedUpdate(float pSecondsElapsed) {
		super.onManagedUpdate(pSecondsElapsed);

		if (this.gtNotifications != null)
			for (final Notification notification : this.gtNotifications)
				notification.onUpdate(pSecondsElapsed);
	}

	@Override
	public void onResume() {
		this.PROGRESS_BAR_SHADER.setCompiled(false);
	}

	@Override
	public void setAlpha(float pAlpha) {
		super.setAlpha(pAlpha);
		this.stMultiplier.setAlpha(pAlpha);
		this.sProgressBar.setAlpha(pAlpha);
		this.sProgressBarBackground.setAlpha(pAlpha * 0.6f);
		this.sProgressBarCanvas.setAlpha(pAlpha * 0.85f);

		if (this.gtNotifications != null)
			for (final Notification notification : this.gtNotifications)
				notification.getShadowedText().setAlpha(pAlpha);
	}

	public void setAnimateText(boolean animateText) {
		this.animateText = animateText;
	}

	@Override
	public void setColor(float pRed, float pGreen, float pBlue, float pAlpha) {
		super.setColor(pRed, pGreen, pBlue, pAlpha);
		this.sProgressBar.setAlpha(pAlpha);
		this.sProgressBarBackground.setAlpha(pAlpha);
		this.sProgressBarCanvas.setAlpha(pAlpha);
	}

	public void setMultiplier(int multiplier) {
		this.multiplier = multiplier;

		if (this.multiplier > 99)
			this.multiplier = 99;

		// Unlock achievement "An Awkward Slap On The Back"
		if (multiplier == 1 && this.multiplierSinceIncrement >= 25)
			AchievementsManager.unlockAchievement(GooglePlayConstants.ACHIEVEMENT_1_ID);

		if (this.multiplier > this.highestMultiplier) {
			this.highestMultiplier = this.multiplier;
			((SessionScene) this.getContext()).onNewMultiplier(this.multiplier);
		}

		final int PEAK_MULTIPLIER = 25;
		float multiplierPeakPercent = (float) this.multiplier / (float) PEAK_MULTIPLIER;

		if (multiplierPeakPercent > 1f)
			multiplierPeakPercent = 1f;

		final Color multiplierColor = new Color(1f - 0.3f * multiplierPeakPercent, 1f - 0.64f * multiplierPeakPercent,
				1f - 0.82f * multiplierPeakPercent);

		this.stMultiplier.setColor(multiplierColor);
		this.setMultiplierText(this.multiplier);
	}

	private void setMultiplierText(int combo) {
		this.stMultiplier.setText(String.format("X%d", combo));
	}

}
