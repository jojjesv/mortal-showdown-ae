package com.partlight.ms.entity.session.notification;

import java.util.ArrayList;

import org.andengine.util.color.Color;
import org.andengine.util.modifier.ease.EaseCubicInOut;
import org.andengine.util.modifier.ease.EaseCubicOut;

import com.partlight.ms.entity.ShadowedText;
import com.partlight.ms.resource.EnvironmentVars;
import com.partlight.ms.util.EntityUtils;
import com.partlight.ms.util.updatehandler.FlashUpdateHandler;

public class Notification {

	public enum NotificationEffects {
		NONE, PULSATE,
	}

	private static final float	TEXT_Y_ANIM_DURATION	= 0.15f;
	private static final float	TEXT_DURATION			= 4f;

	private static final Color TEXT_COLOR;

	static {
		TEXT_COLOR = new Color(0.925f, 0.925f, 0.925f);
	}

	private ShadowedText				stText;
	private final boolean				isEndless;
	private final NotificationEffects	neEffect;
	private final float					textOldR;
	private final float					textOldG;
	private final float					textOldB;
	private float						textOldY;
	private float						textTargetY;
	private float						textYTotalSecondsElapsed;
	private float						totalSecondsElapsed;

	private FlashUpdateHandler fuhTextFlash;

	private ArrayList<Notification> alContextList;

	public Notification(ShadowedText shadowedText) {
		this(shadowedText, false);
	}

	public Notification(ShadowedText shadowedText, boolean endless) {
		this(shadowedText, endless, NotificationEffects.NONE);
	}

	public Notification(ShadowedText shadowedText, boolean endless, NotificationEffects effect) {
		this.stText = shadowedText;
		this.isEndless = endless;
		this.neEffect = effect;
		this.textOldR = shadowedText.getRed();
		this.textOldG = shadowedText.getGreen();
		this.textOldB = shadowedText.getBlue();
		this.textTargetY = this.textOldY = this.stText.getY();
	}

	public ShadowedText getShadowedText() {
		return this.stText;
	}

	public boolean isEndless() {
		return this.isEndless;
	}

	public void onUpdate(float secondsElapsed) {

		this.totalSecondsElapsed += secondsElapsed;
		this.textYTotalSecondsElapsed += secondsElapsed;

		if (this.stText == null || this.stText.isDisposed())
			return;

		if (!this.isEndless && this.totalSecondsElapsed >= Notification.TEXT_DURATION) {
			if (this.fuhTextFlash == null)
				this.fuhTextFlash = new FlashUpdateHandler(0.05f, 3) {
					@Override
					protected void onFinished() {
						super.onFinished();

						Notification.this.stText.setVisible(false);
						EnvironmentVars.MAIN_CONTEXT.runOnUpdateThread(new Runnable() {

							@Override
							public void run() {
								Notification.this.stText.detachSelf();
								Notification.this.stText.dispose();
								Notification.this.stText = null;

								Notification.this.fuhTextFlash = null;

								if (Notification.this.alContextList != null)
									Notification.this.alContextList.remove(Notification.this);
							}
						});
					}

					@Override
					protected void onSwitch() {
						super.onSwitch();
						Notification.this.stText.setVisible(!Notification.this.stText.isVisible());
					}
				};

			this.fuhTextFlash.onUpdate(secondsElapsed);
		}

		float animPercentage = EaseCubicOut.getInstance().getPercentage(this.totalSecondsElapsed, 0.33f);
		if (animPercentage > 1f)
			animPercentage = 1f;

		this.transformShadowedText(animPercentage);

		if (this.textYTotalSecondsElapsed > Notification.TEXT_Y_ANIM_DURATION)
			this.textYTotalSecondsElapsed = Notification.TEXT_Y_ANIM_DURATION;

		this.stText.setY(this.textOldY + (this.textTargetY - this.textOldY)
				* EaseCubicInOut.getInstance().getPercentage(this.textYTotalSecondsElapsed, Notification.TEXT_Y_ANIM_DURATION));
	}

	public void promptRemovalFromListOnDispose(ArrayList<Notification> list) {
		this.alContextList = list;
	}

	public void pushShadowedText(float pixels) {
		this.textOldY = this.stText.getY();
		this.textTargetY += pixels;
		this.textYTotalSecondsElapsed = 0f;
	}

	protected void transformShadowedText(float percentage) {
		float textX = -(this.stText.getWidth() + 1f) * this.stText.getScaleX();
		textX -= textX * percentage;
		this.stText.setX(textX);

		float textR = 0f, textG = 0f, textB = 0f;

		final float TEXT_R = Notification.TEXT_COLOR.getRed();
		final float TEXT_G = Notification.TEXT_COLOR.getGreen();
		final float TEXT_B = Notification.TEXT_COLOR.getBlue();

		switch (this.neEffect) {
		case PULSATE:

			final float FACTOR = EntityUtils.getSineValue(0.75f);

			textR = this.textOldR + (TEXT_R - this.textOldR) * FACTOR;
			textG = this.textOldG + (TEXT_G - this.textOldG) * FACTOR;
			textB = this.textOldB + (TEXT_B - this.textOldB) * FACTOR;
			break;
		case NONE:
			textR = this.textOldR + (TEXT_R - this.textOldR) * percentage;
			textG = this.textOldG + (TEXT_G - this.textOldG) * percentage;
			textB = this.textOldB + (TEXT_B - this.textOldB) * percentage;
			break;
		}

		this.stText.setColor(textR, textG, textB);
	}
}
