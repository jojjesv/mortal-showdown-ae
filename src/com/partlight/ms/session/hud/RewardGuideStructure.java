package com.partlight.ms.session.hud;

import javax.microedition.khronos.opengles.GL10;

import org.andengine.entity.Entity;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.FontUtils;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.color.Color;

import com.partlight.ms.entity.touch.scroll.ScrollContainer;
import com.partlight.ms.resource.EnvironmentVars;
import com.partlight.ms.resource.ResourceManager;
import com.partlight.ms.scene.session.SessionScene;
import com.partlight.ms.session.hud.ComboTracker.NotificationConstants;
import com.partlight.ms.session.hud.notification.NotificationRewards;
import com.partlight.ms.util.Fade;

public class RewardGuideStructure extends Entity implements IOnSceneTouchListener, Runnable {

	private static final String	FOOTER_STRING	= "TOUCH ANYWHERE TO DISMISS";
	private static final float	TEXT_X			= 32f;
	private static final float	TEXT_Y			= 32f;

	private static final String		TEXT_SEPARATOR	= "  -  ";
	private final Text				tGroupNew;
	private final Text				tGroupUpgrade;
	private final Text				tFooter;
	private final SessionScene		ssContext;
	private boolean					areComponentsAttached;
	private ScrollContainer			scScrollContainer;
	private boolean					isStateFadedIn;
	private IOnSceneTouchListener	ostlPreviousHudListener;
	private Runnable				rOnClosed;

	public RewardGuideStructure(SessionScene context, VertexBufferObjectManager vertexBuffer) {
		super(0f, 0f);
		this.ssContext = context;

		this.tGroupNew = new Text(RewardGuideStructure.TEXT_X, RewardGuideStructure.TEXT_Y, ResourceManager.fFontMain,
				this.generateText(NotificationConstants.NOTIFICATION_COLOR_NEW), vertexBuffer);
		this.tGroupNew.setColor(NotificationConstants.NOTIFICATION_COLOR_NEW);
		this.tGroupNew.setAlpha(0f);
		this.tGroupNew.setScaleCenter(0, 0);
		this.tGroupNew.setScale(1.75f);

		this.tGroupUpgrade = new Text(RewardGuideStructure.TEXT_X, RewardGuideStructure.TEXT_Y, ResourceManager.fFontMain,
				this.generateText(NotificationConstants.NOTIFICATION_COLOR_UPGRADE), vertexBuffer);
		this.tGroupUpgrade.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		this.tGroupUpgrade.setColor(NotificationConstants.NOTIFICATION_COLOR_UPGRADE);
		this.tGroupUpgrade.setAlpha(0f);
		this.tGroupUpgrade.setScaleCenter(0, 0);
		this.tGroupUpgrade.setScale(1.75f);

		final float FOOTER_SCALE = 1.5f;
		final float FOOTER_X = EnvironmentVars.MAIN_CONTEXT.width()
				- FontUtils.measureText(ResourceManager.fFontMain, RewardGuideStructure.FOOTER_STRING) * FOOTER_SCALE - 16f;
		final float FOOTER_Y = EnvironmentVars.MAIN_CONTEXT.height() - 32f;

		this.tFooter = new Text(FOOTER_X, FOOTER_Y, ResourceManager.fFontMain, RewardGuideStructure.FOOTER_STRING, vertexBuffer);
		this.tFooter.setScaleCenter(0, 0);
		this.tFooter.setScale(FOOTER_SCALE);

	}

	private String generateText(Color matchingColor) {

		String out = "";
		final int rlength = NotificationRewards.NOTIFICATION_MULTIPLIERS.length;

		for (int x = 0; x < rlength; x++) {

			if (NotificationRewards.NOTIFICATION_COLORS[x] == matchingColor)
				out += NotificationRewards.NOTIFICATION_MULTIPLIERS[x] + RewardGuideStructure.TEXT_SEPARATOR
						+ NotificationRewards.NOTIFICATION_DESCRIPTIONS[x];

			out += "\n";
		}

		return out;
	}

	public void hide() {
		this.ssContext.getFade().hide();
		this.isStateFadedIn = false;
		this.ssContext.getFade().runOnFadeOut(this);
	}

	private void initScrollContainer() {
		if (this.scScrollContainer != null)
			return;

		this.scScrollContainer = new ScrollContainer(16f);

		try {

			// Calculate minimum scroll
			{
				final float CONTENT_HEIGHT = Math.max(this.tGroupNew.getHeightScaled(), this.tGroupUpgrade.getHeightScaled());

				final float CONTENT_Y = Math.min(this.tGroupNew.getY(), this.tGroupUpgrade.getY());

				final float CONTENT_HEIGHT_DIFFERENCE = this.ssContext.getFade().getHeightScaled() - CONTENT_HEIGHT;

				this.scScrollContainer.setMinY(CONTENT_HEIGHT_DIFFERENCE - CONTENT_Y);
			}

		} catch (final Exception ex) {

		}
	}

	public void onAttachComponents() {
		this.initScrollContainer();
		this.ssContext.getFade().detachChildren();
		this.scScrollContainer.detachChildren();
		if (this.scScrollContainer != null) {
			this.scScrollContainer.attachChild(this.tGroupNew);
			this.scScrollContainer.attachChild(this.tGroupUpgrade);
			this.ssContext.getFade().attachChild(this.scScrollContainer);
		}
		this.ssContext.getFade().attachChild(this.tFooter);
	}

	protected void onClosed() {

	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		if (((this.scScrollContainer.getMinY() < 0f) ? !this.scScrollContainer.onTouchEvent(pSceneTouchEvent) : true)
				&& pSceneTouchEvent.isActionUp()) {
			this.hide();
			return true;
		}
		return false;
	}

	@Override
	public void run() {
		if (this.isStateFadedIn)
			EnvironmentVars.MAIN_CONTEXT.getHud().setOnSceneTouchListener(this);
		else {
			this.ssContext.setIgnoreUpdate(false);
			this.ssContext.getComboTracker().setIgnoreUpdate(false);
			EnvironmentVars.MAIN_CONTEXT.getHud().setOnSceneTouchListener(this.ostlPreviousHudListener);
			if (this.rOnClosed != null) {
				this.rOnClosed.run();
				this.rOnClosed = null;
			}
			this.onClosed();
		}
	}

	public void runOnClosed(Runnable action) {
		this.rOnClosed = action;
	}

	@Override
	public void setColor(Color pColor) {
	}

	@Override
	public void setX(float pX) {
	}

	@Override
	public void setY(float pY) {
	}

	public void show() {
		if (!this.areComponentsAttached) {
			this.onAttachComponents();
			this.areComponentsAttached = true;
		}

		this.ostlPreviousHudListener = EnvironmentVars.MAIN_CONTEXT.getHud().getOnSceneTouchListener();
		EnvironmentVars.MAIN_CONTEXT.getHud().setOnSceneTouchListener(null);

		this.initScrollContainer();

		final Fade FADE = this.ssContext.getFade();
		FADE.show(0.75f);
		FADE.runOnFadeIn(this);

		FADE.detachSelf();
		EnvironmentVars.MAIN_CONTEXT.getHud().attachChild(FADE);

		this.isStateFadedIn = true;

		this.ssContext.setIgnoreUpdate(true);
		this.ssContext.getComboTracker().setIgnoreUpdate(true);
	}
}
