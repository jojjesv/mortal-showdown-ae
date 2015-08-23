package com.partlight.ms.scene.mainmenu;

import org.andengine.entity.Entity;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.util.color.Color;

import com.partlight.ms.Direction;
import com.partlight.ms.activity.ad.AdUtils;
import com.partlight.ms.entity.mainmenu.button.Button;
import com.partlight.ms.entity.touch.scroll.ScrollContainer;
import com.partlight.ms.mainmenu.hud.Selector;
import com.partlight.ms.resource.EnvironmentVars;
import com.partlight.ms.resource.ResourceManager;
import com.partlight.ms.resource.ResourceManager.MiscRegions;
import com.partlight.ms.resource.ResourceManager.StoreTextureRegions;
import com.partlight.ms.resource.ResourceManager.StrokeTextureRegions;
import com.partlight.ms.scene.DialogScene;
import com.partlight.ms.session.character.Zombie;
import com.partlight.ms.session.character.skin.player.PlayerCharacterSkin;
import com.partlight.ms.session.hud.BaseScreenComponent;
import com.partlight.ms.session.hud.listener.ComponentAdapter;
import com.partlight.ms.shader.RadialGradientShaderProgram;
import com.partlight.ms.util.boundary.Boundary;
import com.partlight.ms.util.boundary.BoundaryAdapter;

import android.util.Log;

class MainMenuEditPlayer extends SubSection {

	private static final float	ROTATE_IDLE_ALPHA		= 0.125f;
	private static final float	ROTATE_PRESSED_ALPHA	= 0.33f;
	private static final float	ROTATE_MOVE_SENSITIVITY	= 32f;

	private static int skinMoveDirectionIndex;

	private static final String[] BUTTON_STRINGS = {
			"BACK",
			"HAIR",
			"SKIN",
			"TORSO",
			"LEGS",
	};

	static {
		MainMenuEditPlayer.skinMoveDirectionIndex = Direction.SOUTHEAST.ordinal();
	}

	private BoundaryAdapter			baPlayerBoundary;
	private BoundaryAdapter			baTouchBoundary;
	protected Button				bHair;
	protected Button				bLegs;
	protected Button				bSkin;
	protected Button				bTorso;
	protected PlayerCharacterSkin	pcsPlayerSkinPreview;
	private Sprite					sPlayerSkinGradient;
	private Sprite					sRotateChar;
	private boolean					isRotatingSkin;
	private boolean					needsPurchaseConfirmation;
	private float					rotateCharInitalTouchX;
	private boolean					hasPushedEntities;
	private ScrollContainer			scLeftSideContainer;
	protected Selector				sCurrent;
	protected boolean				purchaseCanceled;

	public MainMenuEditPlayer(MainMenuScene context) {
		super(context);
	}

	protected boolean assertCanPurchase() {
		return true;
	}

	public void cancelPurchase() {
		this.purchaseCanceled = true;
		this.goBack();
	}

	public void pushEntities() {
		if (this.hasPushedEntities)
			return;

		AdUtils.pushEntities(this.getPushedEntities());
		AdUtils.pushScrollContainer(this.scLeftSideContainer);

		this.hasPushedEntities = true;
	}

	protected Entity[] getPushedEntities() {
		return new Entity[] {
				this.pcsPlayerSkinPreview,
				this.sPlayerSkinGradient,
				this.sRotateChar
		};
	}

	private Button createButton(int index) {

		final int INDEX = index;
		final float Y = Button.BUTTON_HEIGHT * index;
		final Button BUTTON = new Button(48, Y, StrokeTextureRegions.region_stroke_2, null, MainMenuEditPlayer.BUTTON_STRINGS[index + 1]);
		BUTTON.setComponentListener(new ComponentAdapter() {
			@Override
			public void onComponentReleased(BaseScreenComponent component, float x, float y) {
				MainMenuEditPlayer.this.onButtonClick(INDEX);
			}
		});

		BUTTON.setX(12f);

		return BUTTON;
	}

	protected void directPlayerSkin() {
		this.pcsPlayerSkinPreview.directSkin(Direction.values()[MainMenuEditPlayer.skinMoveDirectionIndex], true, 2.1f);
	}

	protected MainMenuDialogBoxState getDialogFailedState() {
		return null;
	}

	protected String getPurchaseMessage(boolean canPurchase) {
		return null;
	}

	@Override
	protected void goBack() {
		if (this.needsPurchaseConfirmation) {
			super.mmsContext.mmdbsDialogBoxState = MainMenuDialogBoxState.CONFIRM_COLORS;

			if (this.assertCanPurchase()) {
				final String MESSAGE = this.getPurchaseMessage(true);

				super.mmsContext.showDialog(MESSAGE, false);
				super.mmsContext.addDialogButtons();
			} else {
				super.mmsContext.mmdbsDialogBoxState = this.getDialogFailedState();

				final String MESSAGE = this.getPurchaseMessage(false);
				super.mmsContext.showDialog(MESSAGE, false);
			}
			this.needsPurchaseConfirmation = false;
		} else
			super.goBack();
	}

	private void handleRotateCharTouchEvent(TouchEvent touchEvent) {
		if (touchEvent.isActionDown() && Boundary.BoundaryUtils.isIntersecting(this.baPlayerBoundary, this.baTouchBoundary)) {
			this.isRotatingSkin = true;
			this.rotateCharInitalTouchX = touchEvent.getX();
			this.sRotateChar.setAlpha(1f);
		} else if (touchEvent.isActionUp()) {
			this.isRotatingSkin = false;
			this.sRotateChar.setAlpha(1f);
		}

		if (this.isRotatingSkin)
			if (touchEvent.isActionMove()) {
				final float DELTA = this.rotateCharInitalTouchX - touchEvent.getX();
				final float DELTA_ABS = Math.abs(DELTA);

				if (DELTA_ABS > MainMenuEditPlayer.ROTATE_MOVE_SENSITIVITY) {
					final int STEPS = (int) Math.floor(DELTA_ABS / MainMenuEditPlayer.ROTATE_MOVE_SENSITIVITY);

					MainMenuEditPlayer.skinMoveDirectionIndex += (DELTA > 0) ? STEPS : -STEPS;

					if (MainMenuEditPlayer.skinMoveDirectionIndex < 0)
						MainMenuEditPlayer.skinMoveDirectionIndex += Direction.values().length - 1;
					else if (MainMenuEditPlayer.skinMoveDirectionIndex > Direction.values().length - 2)
						MainMenuEditPlayer.skinMoveDirectionIndex -= Direction.values().length - 2;
					this.rotateCharInitalTouchX = touchEvent.getX();
					this.directPlayerSkin();
				}
			}
	}

	private void initButtons() {
		this.bHair = this.createButton(0);
		this.bSkin = this.createButton(1);
		this.bTorso = this.createButton(2);
		this.bLegs = this.createButton(3);

		this.scLeftSideContainer = new ScrollContainer(8) {
			@Override
			public boolean onTouchEvent(TouchEvent event) {
				if (!this.touchActionHasBeenDown && event.getX() > 390)
					return false;
				return super.onTouchEvent(event);
			}
		};
		this.scLeftSideContainer.setY(32);
		this.scLeftSideContainer.setMaxY(this.scLeftSideContainer.getY());

		this.scLeftSideContainer.attachChild(this.bHair);
		this.scLeftSideContainer.attachChild(this.bSkin);
		this.scLeftSideContainer.attachChild(this.bTorso);
		this.scLeftSideContainer.attachChild(this.bLegs);

		super.afeContainer.attachChild(this.scLeftSideContainer);
	}

	private void initRotateChar() {
		this.sRotateChar = new Sprite(0f, 0f, StoreTextureRegions.region_rotate_char,
				EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager()) {
			@Override
			public void setAlpha(float pAlpha) {
				super.setAlpha(pAlpha * ((MainMenuEditPlayer.this.isRotatingSkin) ? MainMenuEditPlayer.ROTATE_PRESSED_ALPHA
						: MainMenuEditPlayer.ROTATE_IDLE_ALPHA));
			}
		};

		this.sRotateChar.setScale(4f);

		this.sRotateChar.setPosition(
				this.sPlayerSkinGradient.getX() + (this.sPlayerSkinGradient.getWidthScaled() - this.sRotateChar.getWidth()) / 2f,
				this.sPlayerSkinGradient.getY() + this.sPlayerSkinGradient.getHeightScaled() * 0.875f);

		super.afeContainer.attachChild(this.sRotateChar);

		//@formatter:off
		this.baPlayerBoundary = new BoundaryAdapter(
				this.sPlayerSkinGradient.getX(),
				this.sPlayerSkinGradient.getY(),
				Math.max(this.sPlayerSkinGradient.getWidthScaled(), this.sRotateChar.getWidthScaled()),
				this.sPlayerSkinGradient.getHeightScaled());
		//@formatter:on
	}

	protected void onButtonClick(int buttonIndex) {
	}

	@Override
	protected void onContainerAttached() {
		this.purchaseCanceled = false;
		this.needsPurchaseConfirmation = false;

		ResourceManager.btStroke2.load();
		ResourceManager.btStroke6.load();
		ResourceManager.btRotateChar.load();

		if (EnvironmentVars.MAIN_CONTEXT.isAdVisible())
			this.pushEntities();

		this.scLeftSideContainer.onTouchRelease(false);

		this.revalidatePlayerSkinBodyParts();
	}

	@Override
	protected void onContainerDetached() {
		ResourceManager.btStroke2.unload();
		ResourceManager.btStroke6.unload();
		ResourceManager.btRotateChar.unload();

		this.unloadPlaySkinTextures();
	}

	@Override
	public boolean onSceneTouchEvent(Scene scene, TouchEvent touchEvent) {
		if (super.mmsContext.getDialog() != null && super.mmsContext.getDialog().isShowing()) {
			return ((DialogScene) super.mmsContext).onSceneTouchEvent(touchEvent);
		}

		if (this.sCurrent != null && this.sCurrent.isShowing())
			return this.sCurrent.onSceneTouchEvent(scene, touchEvent);

		if (this.baTouchBoundary == null)
			this.baTouchBoundary = new BoundaryAdapter(touchEvent.getX(), touchEvent.getY(), 1f, 1f);
		else
			this.baTouchBoundary.set(touchEvent.getX(), touchEvent.getY(), 1f, 1f);

		this.handleRotateCharTouchEvent(touchEvent);

		final boolean scrollContainerEvent = !this.isRotatingSkin && this.scLeftSideContainer.onTouchEvent(touchEvent);
		if (!scrollContainerEvent)
			return super.onSceneTouchEvent(scene, touchEvent);
		else
			return true;
	}

	@Override
	protected void postInitialized() {
		super.postInitialized();

		this.pcsPlayerSkinPreview = new PlayerCharacterSkin();
		this.pcsPlayerSkinPreview.setScaleCenter(0, 0);
		this.pcsPlayerSkinPreview.setScale(9f);
		this.pcsPlayerSkinPreview.setX(EnvironmentVars.MAIN_CONTEXT.width() - 32f * 9f - 64f);
		this.pcsPlayerSkinPreview.setY((EnvironmentVars.MAIN_CONTEXT.height() + Button.BUTTON_HEIGHT - 32f * 9f) / 2f);
		this.pcsPlayerSkinPreview.loadTextures();
		this.directPlayerSkin();

		this.sPlayerSkinGradient = new Sprite(this.pcsPlayerSkinPreview.getX() - 24f, this.pcsPlayerSkinPreview.getY() - 24f,
				MiscRegions.region_empty, EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager());
		this.sPlayerSkinGradient.setScaleCenter(0, 0);
		this.sPlayerSkinGradient.setScale(32f * 9f + 48f);
		this.sPlayerSkinGradient.setColor(Color.WHITE);
		this.sPlayerSkinGradient.setShaderProgram(RadialGradientShaderProgram.getInstance());

		super.afeContainer.attachChild(this.sPlayerSkinGradient);
		super.afeContainer.attachChild(this.pcsPlayerSkinPreview);

		this.sPlayerSkinGradient.setAlpha(0);
		this.pcsPlayerSkinPreview.setAlpha(0);

		this.initRotateChar();
		this.initButtons();

		super.tmButtonTouchManager.setComponents(this.bBack, this.bHair, this.bLegs, this.bSkin, this.bTorso);
	}

	protected final void requirePurchaseConfirmation() {
		this.needsPurchaseConfirmation = true;
	}

	public void revalidatePlayerSkinBodyParts() {
		this.unloadPlaySkinTextures();
		this.pcsPlayerSkinPreview.revalidateBodyParts();
		this.revalidatePlayerSkinColors();
		this.directPlayerSkin();

		this.pcsPlayerSkinPreview.loadTextures();
	}

	public void revalidatePlayerSkinColors() {
		this.pcsPlayerSkinPreview.revalidateColors();
	}

	public void savePlayerData() {
		this.needsPurchaseConfirmation = false;
	}

	@Override
	protected boolean shouldCreateBackButton(ITextureRegion buttonBackground) {
		return super.shouldCreateBackButton(StrokeTextureRegions.region_stroke_6);
	}

	protected void unloadPlaySkinTextures() {
		this.pcsPlayerSkinPreview.unloadTextures();
		Zombie.getAliveZombies().get(0).getSkin().loadTextures();
	}
}
