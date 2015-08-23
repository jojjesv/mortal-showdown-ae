package com.partlight.ms.scene;

import org.andengine.entity.scene.Scene;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.VerticalAlign;

import com.partlight.ms.entity.ShadowedText;
import com.partlight.ms.entity.dialog.DialogBox;
import com.partlight.ms.entity.dialog.DialogButton;
import com.partlight.ms.resource.EnvironmentVars;
import com.partlight.ms.resource.EnvironmentVars.StaticData;
import com.partlight.ms.resource.ResourceManager;
import com.partlight.ms.resource.ResourceManager.DialogRegions;
import com.partlight.ms.resource.ResourceManager.MiscRegions;
import com.partlight.ms.scene.mainmenu.MainMenuStore;
import com.partlight.ms.session.hud.ConnectionNotifier;
import com.partlight.ms.util.ColorConstants;

public abstract class DialogScene extends Scene {

	private ConnectionNotifier	cnConnectionStatus;
	protected DialogBox			hdDialog;
	protected DialogButton		dbDialogAccept;
	protected DialogButton		dbDialogDecline;
	private ShadowedText		stConfirmDescription;
	private ShadowedText		stConfirmTitle;
	private boolean				touchActionHasBeenDown;
	private int					lastConnectionCode;

	public DialogScene() {

	}

	public void addDialogButtons() {
		this.addDialogButtons("SURE", "NOPE");
	}

	public void addDialogButtons(String acceptText, String declineText) {

		if (this.dbDialogAccept != null)
			this.dbDialogAccept.detachSelf();

		if (this.dbDialogDecline != null)
			this.dbDialogDecline.detachSelf();

		this.dbDialogAccept = new DialogButton(10, 88, acceptText, EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager()) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {

				if (!DialogScene.this.hdDialog.isShowing())
					return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);

				if (!DialogScene.this.hdDialog.isHiding() && pSceneTouchEvent.isActionUp())
					DialogScene.this.onDialogAccept();

				return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
			}
		};
		this.dbDialogDecline = new DialogButton(140, 88, declineText, EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager()) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {

				if (!DialogScene.this.hdDialog.isShowing())
					return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);

				if (!DialogScene.this.hdDialog.isHiding() && pSceneTouchEvent.isActionUp())
					DialogScene.this.onDialogDecline();

				return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
			}
		};

		this.hdDialog.getContainer().attachChild(this.dbDialogAccept);
		this.hdDialog.getContainer().attachChild(this.dbDialogDecline);

		ResourceManager.btDialogBtn.load();

		this.registerTouchArea(this.dbDialogAccept);
		this.registerTouchArea(this.dbDialogDecline);
	}

	public DialogBox getDialog() {
		return this.hdDialog;
	}

	public int lastConnectionCode() {
		return this.lastConnectionCode;
	}

	public void onConnectionChanged(int code) {
		this.lastConnectionCode = code;
		if (this.cnConnectionStatus == null) {
			this.cnConnectionStatus = new ConnectionNotifier(MiscRegions.region_hud_login_states01,
					EnvironmentVars.MAIN_CONTEXT.getCamera().getHUD());
			this.cnConnectionStatus.setZIndex(99);
		}
		this.cnConnectionStatus.queue(code);
	}

	protected void onDialogAccept() {
		this.hdDialog.hide();
	}

	protected void onDialogClosed() {
		if (this.dbDialogAccept != null)
			this.unregisterTouchArea(this.dbDialogAccept);
		if (this.dbDialogDecline != null)
			this.unregisterTouchArea(this.dbDialogDecline);

		ResourceManager.btDialogBtn.unload();

		ResourceManager.btDialog01.unload();
		ResourceManager.btDialog02.unload();
	}

	protected void onDialogDecline() {
		this.hdDialog.hide();
	}

	protected boolean onDialogSceneTouchEvent(TouchEvent sceneTouchEvent, boolean... additionalClosingConditions) {
		return this.getDialog().onSceneTouchEvent(sceneTouchEvent, additionalClosingConditions);
	}

	/**
	 * Occurs when an exception was caught while the engine was drawing to this
	 * scene.
	 */
	public abstract void onEngineDrawError();

	/**
	 * Occurs when an exception was caught while the engine was updating this
	 * scene.
	 */
	public abstract void onEngineUpdateError();

	public void onPause() {

	}

	public void onResume() {

	}

	@Override
	public boolean onSceneTouchEvent(TouchEvent pSceneTouchEvent) {

		final boolean HAS_BUTTONS = this.dbDialogAccept != null || this.dbDialogDecline != null;

		if (HAS_BUTTONS) {
			if (pSceneTouchEvent.isActionDown()) {
				this.touchActionHasBeenDown = true;
				return true;
			}

			if (!this.touchActionHasBeenDown)
				return false;

			if (HAS_BUTTONS)
				return super.onSceneTouchEvent(pSceneTouchEvent);
		}

		if (this.getDialog() == null)
			return super.onSceneTouchEvent(pSceneTouchEvent);

		return this.onDialogSceneTouchEvent(pSceneTouchEvent);
	}

	public void showDialog(String message) {
		this.showDialog(message, true);
	}

	public void showDialog(String message, boolean showFooter) {
		this.showDialog(message, showFooter, HorizontalAlign.CENTER, VerticalAlign.CENTER);
	}

	public void showDialog(String message, boolean showFooter, HorizontalAlign horizontalAlignment, VerticalAlign verticalAlignment) {
		this.showDialog(message, showFooter, horizontalAlignment, verticalAlignment, true);
	}

	public void showDialog(String message, boolean showFooter, HorizontalAlign horizontalAlignment, VerticalAlign verticalAlignment,
			boolean tintBackground) {

		final boolean LONG_MESSAGE = message.split("\n").length >= 3;

		if (LONG_MESSAGE)
			ResourceManager.btDialog02.load();
		else
			ResourceManager.btDialog01.load();

		if (this.hdDialog == null)
			this.hdDialog = new DialogBox() {
				@Override
				public void hide() {
					DialogScene.this.touchActionHasBeenDown = false;
					super.hide();
				};
			};
		this.hdDialog.runOnHide(new Runnable() {
			@Override
			public void run() {
				if (DialogScene.this.dbDialogAccept != null) {
					DialogScene.this.dbDialogAccept.detachSelf();
					DialogScene.this.dbDialogDecline.detachSelf();
					DialogScene.this.dbDialogAccept.dispose();
					DialogScene.this.dbDialogDecline.dispose();
					DialogScene.this.dbDialogAccept = null;
					DialogScene.this.dbDialogDecline = null;
				}

				try {
					DialogScene.this.hdDialog.getContainer().detachSelf();
					DialogScene.this.hdDialog.dispose();
					DialogScene.this.hdDialog = null;
				} catch (final NullPointerException ex) {

				}
				
				DialogScene.this.onDialogClosed();
			}
		});

		this.hdDialog.show(EnvironmentVars.MAIN_CONTEXT.getHud(), message, horizontalAlignment, verticalAlignment,
				(LONG_MESSAGE) ? DialogRegions.region_dialog02 : DialogRegions.region_dialog01, showFooter, tintBackground);
	}

	public boolean showPurchaseDialog(String itemTitle, String itemDescription, int itemPrice, ITextureRegion itemIcon) {
		return this.showPurchaseDialog(itemTitle, itemDescription, itemPrice, itemIcon, 0f);
	}

	public boolean showPurchaseDialog(String itemTitle, String itemDescription, int itemPrice, ITextureRegion itemIcon,
			float itemIconRotation) {
		return this.showPurchaseDialog(itemTitle, itemDescription, itemPrice, itemIcon, itemIconRotation, itemIcon.getWidth());
	}

	public boolean showPurchaseDialog(String itemTitle, String itemDescription, int itemPrice, ITextureRegion itemIcon,
			float itemIconRotation, float itemIconWidth) {
		return this.showPurchaseDialog(itemTitle, itemDescription, itemPrice, itemIcon, itemIconRotation, itemIconWidth,
				String.format(MainMenuStore.ITEM_CONFIRMATION, itemPrice));
	}

	public boolean showPurchaseDialog(String itemTitle, String itemDescription, int itemPrice, ITextureRegion itemIcon,
			float itemIconRotation, float itemIconWidth, String confirmation) {

		if (StaticData.scrapPartsAmount < itemPrice) {
			this.showDialog("YIKES! INSUFFICIENT PARTS.\nYOU NEED " + (itemPrice - StaticData.scrapPartsAmount) + " MORE", false);
			return false;
		}

		this.showDialog(confirmation, false);

		this.hdDialog.setIcon(itemIcon);
		this.hdDialog.getIcon().setRotation(itemIconRotation);
		this.hdDialog.getIcon().setX(this.hdDialog.getIcon().getX()
				- (this.hdDialog.getIcon().getWidthScaled() - itemIconWidth) / this.hdDialog.getContainer().getScaleX());

		if (this.stConfirmTitle != null) {
			this.stConfirmTitle.detachSelf();
			this.stConfirmTitle.dispose();
		}

		if (this.stConfirmDescription != null) {
			this.stConfirmDescription.detachSelf();
			this.stConfirmDescription.dispose();
		}

		this.stConfirmTitle = new ShadowedText(0f, 0f, ResourceManager.fFontMain, itemTitle.concat("\n "),
				EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager());
		this.stConfirmTitle.setScaleCenter(0, 0);
		this.stConfirmTitle.setColor(ColorConstants.SCRAP_PARTS);

		this.stConfirmDescription = new ShadowedText(0f, 0f, ResourceManager.fFontMain, "\n" + itemDescription,
				EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager());
		this.stConfirmDescription.setScaleCenter(0, 0);
		this.stConfirmDescription.setScale(0.5f);
		this.stConfirmDescription.setColor(0.8f, 0.8f, 0.8f);

		this.hdDialog.attachText(this.stConfirmTitle);
		this.hdDialog.attachText(this.stConfirmDescription);

		this.addDialogButtons();

		return true;
	}
}
