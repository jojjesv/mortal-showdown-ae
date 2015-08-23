package com.partlight.ms.entity.dialog;

import org.andengine.audio.sound.Sound;
import org.andengine.entity.Entity;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.VerticalAlign;
import org.andengine.util.color.Color;
import org.andengine.util.modifier.IModifier;
import org.andengine.util.modifier.IModifier.IModifierListener;

import com.partlight.ms.activity.ad.AdUtils;
import com.partlight.ms.entity.ShadowedText;
import com.partlight.ms.resource.EnvironmentVars;
import com.partlight.ms.resource.ResourceManager;
import com.partlight.ms.scene.session.SessionScene;
import com.partlight.ms.util.EntityUtils;

public class DialogBox {

	private static final float	MSG_X_PADDING		= 32;
	private static final float	MSG_Y_PADDING		= DialogBox.MSG_X_PADDING;
	private static final String	MSG_FOOTER			= "TOUCH ANYWHERE TO CONTINUE";
	private static final Color	MSG_FOOTER_COLOR	= new Color(0.6f, 0.6f, 0.6f);
	private static final float	MSG_CONTENT_SCALE	= 1f;
	private static final float	MSG_WAIT_DURATION	= 0.3f;
	private static final Sound	SND_SHOW			= ResourceManager.sDialog;

	public static final float BG_01_X_CENTER() {
		return (EnvironmentVars.MAIN_CONTEXT.width() - 280 * 2f) / 2f;
	}

	public static final float BG_01_Y_CENTER() {
		return (EnvironmentVars.MAIN_CONTEXT.height() - 60 * 2f) / 2f;
	}

	public static final float BG_02_X_CENTER() {
		return (EnvironmentVars.MAIN_CONTEXT.width() - 280 * 2f) / 2f;
	}

	public static final float BG_02_Y_CENTER() {
		return (EnvironmentVars.MAIN_CONTEXT.height() - 128 * 2f) / 2f;
	}

	private DelayModifier	dmMessageWait;
	private Rectangle		rBackgroundTint;
	private Runnable		rOnHide;
	private ShadowedText	stContent;
	private ShadowedText	stFooter;
	private Sprite			sBackgroundIcon;
	private Sprite			sIcon;
	private Sprite			sContainer;
	private boolean			canCloseMessage;
	private boolean			hasPressedWhileShowingMessage;
	private boolean			isHiding;
	private boolean			isShowingMessage;
	private float			msgAlpha	= 1;

	public void attachText(Text text) {
		text.setY((this.sContainer.getHeight() - text.getHeightScaled()) / 2f);
		text.setX((this.sContainer.getWidth() - text.getWidthScaled()) / 2f);
		this.sContainer.attachChild(text);
	}

	public boolean canClose() {
		return this.canCloseMessage;
	}

	protected ShadowedText createFooter() {
		final ShadowedText FOOTER = new ShadowedText(0, 0, ResourceManager.fFontMain, DialogBox.MSG_FOOTER,
				EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager()) {
			@Override
			public void onAttached() {
				EntityUtils.alignEntity(this, this.getWidth(), this.getHeight(), HorizontalAlign.LEFT, VerticalAlign.BOTTOM, 4, 4);
				AdUtils.pushEntities(this);
			}
		};
		FOOTER.setScale(1.75f);
		FOOTER.setColor(DialogBox.MSG_FOOTER_COLOR);
		FOOTER.setTag(SessionScene.TAG_PRESERVE);
		return FOOTER;
	}

	public void dispose() {
		this.stContent.dispose();
		this.stFooter.dispose();
		this.sContainer.dispose();

		if (this.rBackgroundTint != null)
			this.rBackgroundTint.dispose();

		if (this.sIcon != null)
			this.sIcon.dispose();

		this.rBackgroundTint = null;
		this.stContent = null;
		this.stFooter = null;
		this.sContainer = null;
		this.dmMessageWait = null;
		this.sIcon = null;
	}

	public Sprite getBackground() {
		return this.sBackgroundIcon;
	}

	public Sprite getContainer() {
		return this.sContainer;
	}

	public Sprite getIcon() {
		return this.sIcon;
	}

	public void hide() {
		final AlphaModifier ALPHA_MOD = new AlphaModifier(0.15f, 1, 0);

		ALPHA_MOD.setAutoUnregisterWhenFinished(true);
		ALPHA_MOD.addModifierListener(new IModifierListener<IEntity>() {
			@Override
			public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
				EnvironmentVars.MAIN_CONTEXT.runOnUpdateThread(new Runnable() {
					@Override
					public void run() {
						DialogBox.this.isShowingMessage = false;
						DialogBox.this.isHiding = false;
						DialogBox.this.onClosed();
					}
				});
			}

			@Override
			public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
			}
		});
		this.stFooter.detachSelf();
		if (this.rBackgroundTint != null)
			this.rBackgroundTint.detachSelf();
		this.sContainer.registerEntityModifier(ALPHA_MOD);

		this.isHiding = true;
	}

	public boolean isHiding() {
		return this.isHiding;
	}

	public boolean isShowing() {
		return this.isShowingMessage;
	}

	protected void onClosed() {
		this.sContainer.detachSelf();
		this.stContent.detachSelf();

		if (this.rOnHide != null)
			EnvironmentVars.MAIN_CONTEXT.runOnUpdateThread(new Runnable() {

				@Override
				public void run() {
					if (DialogBox.this.rOnHide != null)
						DialogBox.this.rOnHide.run();
				}
			});
	}

	public boolean onSceneTouchEvent(TouchEvent touchEvent, boolean... additionalClosingConditions) {

		if (this.isShowing() && this.canClose()) {
			final int i = TouchEvent.ACTION_DOWN;

			if (touchEvent.isActionDown()) {
				this.hasPressedWhileShowingMessage = true;
				return true;
			}

			boolean allConditionsTrue = true;

			for (final boolean b : additionalClosingConditions)
				if (!b) {
					allConditionsTrue = false;
					break;
				}

			if (allConditionsTrue && this.hasPressedWhileShowingMessage && touchEvent.isActionUp()) {
				this.hide();
				this.hasPressedWhileShowingMessage = false;
				return true;
			}
		}

		return false;
	}

	public void resetTouch() {
		this.hasPressedWhileShowingMessage = false;
	}

	public void runOnHide(Runnable action) {
		this.rOnHide = action;
	}

	public void setAlpha(float alpha) {
		this.msgAlpha = alpha;
	}

	public void setBackground(ITextureRegion background) {
		if (background == null) {
			if (this.sBackgroundIcon != null) {
				this.sBackgroundIcon.detachSelf();
				this.sBackgroundIcon.dispose();
				this.sBackgroundIcon = null;
				return;
			}
			return;
		}

		this.sBackgroundIcon = new Sprite(0, 0, background, EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager());
		this.sBackgroundIcon.setScaleCenter(0, 0);
		this.sBackgroundIcon.setScale(2f);
		this.sBackgroundIcon.setX((this.sContainer.getWidth() - this.sBackgroundIcon.getWidthScaled()) / 2f);
		this.sBackgroundIcon.setY((this.sContainer.getHeight() - this.sBackgroundIcon.getHeightScaled()) / 2f);

		this.sContainer.attachChild(this.sBackgroundIcon);
	}

	public void setIcon(ITextureRegion icon) {
		if (icon == null) {
			if (this.sIcon != null) {
				this.sIcon.detachSelf();
				this.sIcon.dispose();
				this.sIcon = null;
				return;
			}
			return;
		}

		this.sIcon = new Sprite(8 / this.sContainer.getScaleX(), 0, icon, EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager());
		this.sIcon.setY((this.sContainer.getHeight() - this.sIcon.getHeightScaled()) / 2f);

		this.sContainer.attachChild(this.sIcon);
	}

	public void show(Entity context, String message, final HorizontalAlign horizontalAlignment, final VerticalAlign verticalAlignment,
			ITextureRegion background) {
		this.show(context, message, horizontalAlignment, verticalAlignment, background, true);
	}

	public void show(Entity context, String message, final HorizontalAlign horizontalAlignment, final VerticalAlign verticalAlignment,
			ITextureRegion background, final boolean showFooter) {
		this.show(context, message, horizontalAlignment, verticalAlignment, background, showFooter, false);
	}

	public void show(Entity context, String message, final HorizontalAlign horizontalAlignment, final VerticalAlign verticalAlignment,
			ITextureRegion background, final boolean showFooter, boolean tintBackground) {

		if (this.stFooter == null)
			this.stFooter = this.createFooter();

		if (this.sContainer == null || this.sContainer.getTextureRegion() != background) {

			if (this.sContainer != null) {
				this.sContainer.detachSelf();
				this.sContainer.dispose();
			}

			this.sContainer = new Sprite(0, 0, background, EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager()) {

				@Override
				public void setAlpha(float pAlpha) {
					super.setAlpha(pAlpha);
					try {

						IEntity index;

						for (int i = 0; i < this.getChildCount(); i++) {
							index = this.getChildByIndex(i);
							if (index == DialogBox.this.sBackgroundIcon)
								index.setAlpha(pAlpha * 0.4f);
							else
								index.setAlpha(pAlpha);
						}

					} catch (final Exception ex) {
					}
				}
			};

			this.sContainer.setScale(2f);
			this.sContainer.setScaleCenter(this.sContainer.getWidth() / 2f, this.sContainer.getHeight() / 2f);
			this.sContainer.setTag(SessionScene.TAG_PRESERVE);
		}

		if (this.stContent == null || this.stContent.getText().length() < message.length()) {

			if (this.stContent != null) {
				this.stContent.detachSelf();
				this.stContent.dispose();
			}

			this.stContent = new ShadowedText(0, 0, ResourceManager.fFontMain, message,
					EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager());
			this.stContent.setHorizontalAlign(HorizontalAlign.CENTER);
			this.stContent.setTag(SessionScene.TAG_PRESERVE);

		} else
			this.stContent.setText(message);

		this.stContent.setScale(DialogBox.MSG_CONTENT_SCALE);
		this.stContent.setX((this.sContainer.getWidth() - this.stContent.getWidth()) / 2f);
		this.stContent.setY((this.sContainer.getHeight() - this.stContent.getHeight()) / 2f);

		this.canCloseMessage = false;

		if (this.dmMessageWait == null) {
			this.dmMessageWait = new DelayModifier(DialogBox.MSG_WAIT_DURATION);
			this.dmMessageWait.addModifierListener(new IModifierListener<IEntity>() {
				@Override
				public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
					DialogBox.this.canCloseMessage = true;
				}

				@Override
				public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
				}
			});
		}
		this.dmMessageWait.reset();
		context.registerEntityModifier(this.dmMessageWait);

		final Entity CONTEXT = context;
		final boolean TINT_BACKGROUND = tintBackground;

		EnvironmentVars.MAIN_CONTEXT.runOnUpdateThread(new Runnable() {
			@Override
			public void run() {

				final DelayModifier SCALE_DELAY = new DelayModifier(0.1f);

				final float START_SCALE = DialogBox.this.sContainer.getScaleX();

				SCALE_DELAY.addModifierListener(new IModifierListener<IEntity>() {
					@Override
					public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
						DialogBox.this.sContainer.setScale(START_SCALE);
					}

					@Override
					public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
					}
				});
				DialogBox.this.sContainer.clearEntityModifiers();

				DialogBox.this.sContainer.registerEntityModifier(SCALE_DELAY);

				DialogBox.this.sContainer.detachSelf();
				DialogBox.this.stContent.detachSelf();

				if (TINT_BACKGROUND) {
					if (DialogBox.this.rBackgroundTint == null) {
						DialogBox.this.rBackgroundTint = new Rectangle(0, 0, EnvironmentVars.MAIN_CONTEXT.width(),
								EnvironmentVars.MAIN_CONTEXT.height(), EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager());
						DialogBox.this.rBackgroundTint.setColor(Color.BLACK);
						DialogBox.this.rBackgroundTint.setAlpha(0.45f);
					}

					DialogBox.this.rBackgroundTint.detachSelf();
					CONTEXT.attachChild(DialogBox.this.rBackgroundTint);
				}

				final Sprite CONTAINER = DialogBox.this.sContainer;

				CONTEXT.attachChild(CONTAINER);

				CONTAINER.attachChild(DialogBox.this.stContent);
				CONTAINER.setAlpha(DialogBox.this.msgAlpha);
				CONTAINER.setScale(START_SCALE);

				EntityUtils.alignEntity(CONTAINER, CONTAINER.getWidth(), CONTAINER.getHeight(), horizontalAlignment, verticalAlignment,
						((horizontalAlignment != HorizontalAlign.CENTER) ? DialogBox.MSG_X_PADDING : 0),
						((verticalAlignment != VerticalAlign.CENTER) ? DialogBox.MSG_Y_PADDING : 0));

				DialogBox.this.sContainer.setScale(START_SCALE * 1.1f);

				if (showFooter)
					CONTEXT.attachChild(DialogBox.this.stFooter);
			}
		});

		this.isShowingMessage = true;
		DialogBox.SND_SHOW.play();
	}
}
