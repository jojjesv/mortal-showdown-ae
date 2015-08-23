package com.partlight.ms.scene.mainmenu;

import java.util.Locale;

import com.partlight.ms.mainmenu.hud.ColorSelector;
import com.partlight.ms.mainmenu.hud.ColorSelectorLibrary;
import com.partlight.ms.resource.EnvironmentVars;
import com.partlight.ms.resource.EnvironmentVars.PreferenceKeys;
import com.partlight.ms.resource.EnvironmentVars.StaticData;
import com.partlight.ms.resource.ResourceManager;
import com.partlight.ms.util.EntityUtils;

public class MainMenuDyeClothes extends MainMenuEditPlayer {

	private ColorSelector	csLegs;
	private ColorSelector	csHair;
	private ColorSelector	csSkin;
	private ColorSelector	csTorso;

	private float	tempHairR;
	private float	tempHairG;
	private float	tempHairB;
	private float	tempSkinR;
	private float	tempSkinG;
	private float	tempSkinB;
	private float	tempSleevesR;
	private float	tempSleevesG;
	private float	tempSleevesB;
	private float	tempTorsoR;
	private float	tempTorsoG;
	private float	tempTorsoB;
	private float	tempLegsR;
	private float	tempLegsG;
	private float	tempLegsB;

	public MainMenuDyeClothes(MainMenuScene context) {
		super(context);
	}

	@Override
	protected boolean assertCanPurchase() {
		return StaticData.clothDyeAmount > 0;
	}

	@Override
	protected MainMenuDialogBoxState getDialogFailedState() {
		return MainMenuDialogBoxState.PURCHASE_COLORS_FAILED;
	}

	@Override
	protected String getPurchaseMessage(boolean canPurchase) {
		if (canPurchase)
			return String.format(Locale.ENGLISH, "\nAPPLY CURRENT COLORS?\nYOU HAVE %d DYE%s LEFT\n\n\n", StaticData.clothDyeAmount,
					StaticData.clothDyeAmount > 1 ? "S" : "");
		else
			return "YOU DON'T HAVE ANY\nDYES LEFT. YOU NEED ATLEAST\n1 TO SAVE YOUR APPEARENCE\n\nYOU CAN PURCHASE DYES\nIN THE STORE";
	}

	@Override
	protected void goBack() {
		if (super.purchaseCanceled) {
			StaticData.playerHairR = this.tempHairR;
			StaticData.playerHairG = this.tempHairG;
			StaticData.playerHairB = this.tempHairB;

			StaticData.playerSkinR = this.tempSkinR;
			StaticData.playerSkinG = this.tempSkinG;
			StaticData.playerSkinB = this.tempSkinB;

			StaticData.playerSleevesR = this.tempSleevesR;
			StaticData.playerSleevesG = this.tempSleevesG;
			StaticData.playerSleevesB = this.tempSleevesB;

			StaticData.playerTorsoR = this.tempTorsoR;
			StaticData.playerTorsoG = this.tempTorsoG;
			StaticData.playerTorsoB = this.tempTorsoB;

			StaticData.playerLegsR = this.tempLegsR;
			StaticData.playerLegsG = this.tempLegsG;
			StaticData.playerLegsB = this.tempLegsB;
		}

		super.goBack();
	}

	@Override
	protected void onButtonClick(int buttonIndex) {
		Float[][] colors = null;

		switch (buttonIndex) {
		case 0:
		case 2:
		case 3:
			colors = ColorSelectorLibrary.COLORS_COMMON;
			break;
		case 1:
			colors = ColorSelectorLibrary.COLORS_SKIN;
			break;
		}

		this.showColorSelector(buttonIndex, colors);
	}

	@Override
	protected void onContainerAttached() {
		super.onContainerAttached();

		this.setTemporaryVariables();
	}

	@Override
	public void savePlayerData() {
		super.savePlayerData();

		EnvironmentVars.PREFERENCES_EDITOR.putFloat(PreferenceKeys.KEY_PLAYER_HAIR_R, StaticData.playerHairR);
		EnvironmentVars.PREFERENCES_EDITOR.putFloat(PreferenceKeys.KEY_PLAYER_HAIR_G, StaticData.playerHairG);
		EnvironmentVars.PREFERENCES_EDITOR.putFloat(PreferenceKeys.KEY_PLAYER_HAIR_B, StaticData.playerHairB);

		EnvironmentVars.PREFERENCES_EDITOR.putFloat(PreferenceKeys.KEY_PLAYER_SKIN_R, StaticData.playerSkinR);
		EnvironmentVars.PREFERENCES_EDITOR.putFloat(PreferenceKeys.KEY_PLAYER_SKIN_G, StaticData.playerSkinG);
		EnvironmentVars.PREFERENCES_EDITOR.putFloat(PreferenceKeys.KEY_PLAYER_SKIN_B, StaticData.playerSkinB);

		EnvironmentVars.PREFERENCES_EDITOR.putFloat(PreferenceKeys.KEY_PLAYER_TORSO_R, StaticData.playerTorsoR);
		EnvironmentVars.PREFERENCES_EDITOR.putFloat(PreferenceKeys.KEY_PLAYER_TORSO_G, StaticData.playerTorsoG);
		EnvironmentVars.PREFERENCES_EDITOR.putFloat(PreferenceKeys.KEY_PLAYER_TORSO_B, StaticData.playerTorsoB);

		EnvironmentVars.PREFERENCES_EDITOR.putFloat(PreferenceKeys.KEY_PLAYER_LEGS_R, StaticData.playerLegsR);
		EnvironmentVars.PREFERENCES_EDITOR.putFloat(PreferenceKeys.KEY_PLAYER_LEGS_G, StaticData.playerLegsG);
		EnvironmentVars.PREFERENCES_EDITOR.putFloat(PreferenceKeys.KEY_PLAYER_LEGS_B, StaticData.playerLegsB);

		StaticData.clothDyeAmount -= 1;

		EnvironmentVars.PREFERENCES_EDITOR.putInt(PreferenceKeys.KEY_CLOTH_DYE_AMOUNT, StaticData.clothDyeAmount);

		EnvironmentVars.PREFERENCES_EDITOR.commit();
	}

	private void setTemporaryVariables() {
		this.tempHairR = StaticData.playerHairR;
		this.tempHairG = StaticData.playerHairG;
		this.tempHairB = StaticData.playerHairB;

		this.tempSkinR = StaticData.playerSkinR;
		this.tempSkinG = StaticData.playerSkinG;
		this.tempSkinB = StaticData.playerSkinB;

		this.tempSleevesR = StaticData.playerSleevesR;
		this.tempSleevesG = StaticData.playerSleevesG;
		this.tempSleevesB = StaticData.playerSleevesB;

		this.tempTorsoR = StaticData.playerTorsoR;
		this.tempTorsoG = StaticData.playerTorsoG;
		this.tempTorsoB = StaticData.playerTorsoB;

		this.tempLegsR = StaticData.playerLegsR;
		this.tempLegsG = StaticData.playerLegsG;
		this.tempLegsB = StaticData.playerLegsB;
	}

	private void showColorSelector(int colorSelectorType, Float[][] colors) {
		boolean isColorSelectorNull = true;

		switch (colorSelectorType) {
		case 0:
			isColorSelectorNull = this.csHair == null;
			break;
		case 1:
			isColorSelectorNull = this.csSkin == null;
			break;
		case 2:
			isColorSelectorNull = this.csTorso == null;
			break;
		case 3:
			isColorSelectorNull = this.csLegs == null;
			break;
		}

		ColorSelector selector = null;

		if (isColorSelectorNull) {

			final int TYPE = colorSelectorType;

			selector = new ColorSelector(colors) {

				@Override
				protected void onClosed() {
					super.onClosed();
					EntityUtils.safetlyDetach(this.getFade());
					ResourceManager.btColorSplash.unload();
				}

				@Override
				protected void onSelectionMade(int index) {
					super.onSelectionMade(index);

					if (!this.purchaseCanceled()) {
						switch (TYPE) {
						case 0:
							StaticData.playerHairR = this.getColors()[index][0];
							StaticData.playerHairG = this.getColors()[index][1];
							StaticData.playerHairB = this.getColors()[index][2];
							break;
						case 1:
							StaticData.playerSkinR = this.getColors()[index][0];
							StaticData.playerSkinG = this.getColors()[index][1];
							StaticData.playerSkinB = this.getColors()[index][2];
							break;
						case 2:
							StaticData.playerTorsoR = this.getColors()[index][0];
							StaticData.playerTorsoG = this.getColors()[index][1];
							StaticData.playerTorsoB = this.getColors()[index][2];
							break;
						case 3:
							StaticData.playerLegsR = this.getColors()[index][0];
							StaticData.playerLegsG = this.getColors()[index][1];
							StaticData.playerLegsB = this.getColors()[index][2];
							break;
						}

						MainMenuDyeClothes.this.requirePurchaseConfirmation();
						MainMenuDyeClothes.this.revalidatePlayerSkinColors();
					}
				}

				@Override
				public void show(boolean... disabledSelections) {
					super.show(disabledSelections);
					ResourceManager.btColorSplash.load();
				}
			};
		}

		switch (colorSelectorType) {
		case 0:
			if (this.csHair == null)
				this.csHair = selector;
			else
				selector = this.csHair;
			break;
		case 1:
			if (this.csSkin == null)
				this.csSkin = selector;
			else
				selector = this.csSkin;
			break;
		case 2:
			if (this.csTorso == null)
				this.csTorso = selector;
			else
				selector = this.csTorso;
			break;
		case 3:
			if (this.csLegs == null)
				this.csLegs = selector;
			else
				selector = this.csLegs;
			break;
		}

		selector.show(-1);

		try {
			super.mmsContext.attachChild(selector.getFade());
		} catch (final IllegalStateException ex) {

		}

		super.sCurrent = selector;
	}
}
