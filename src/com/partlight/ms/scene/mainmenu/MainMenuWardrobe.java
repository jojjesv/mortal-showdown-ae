package com.partlight.ms.scene.mainmenu;

import java.util.Locale;

import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;

import com.partlight.ms.mainmenu.hud.PlayerBodyPartSelector;
import com.partlight.ms.mainmenu.hud.TileSelector;
import com.partlight.ms.resource.EnvironmentVars;
import com.partlight.ms.resource.EnvironmentVars.PreferenceKeys;
import com.partlight.ms.resource.EnvironmentVars.PreferenceKeysMeta;
import com.partlight.ms.resource.EnvironmentVars.StaticData;
import com.partlight.ms.resource.ResourceManager;
import com.partlight.ms.resource.ResourceManager.StoreTextureRegions;
import com.partlight.ms.util.EntityUtils;

public class MainMenuWardrobe extends MainMenuEditPlayer {

	private TileSelector	csHair;
	private TileSelector	csTorso;
	private int				lastSelectorType;

	public MainMenuWardrobe(MainMenuScene context) {
		super(context);
	}

	public int getLastSelectorType() {
		return this.lastSelectorType;
	}

	@Override
	protected String getPurchaseMessage(boolean canPurchase) {
		if (canPurchase)
			return String.format(Locale.ENGLISH, "\nAPPLY CURRENT COLORS?\nYOU HAVE %d DYE%s LEFT\n\n\n", StaticData.clothDyeAmount,
					((StaticData.clothDyeAmount > 1) ? "S" : ""));
		else
			return "YOU DON'T HAVE ANY\nDYES LEFT. YOU NEED ATLEAST\n1 TO SAVE YOUR APPEARENCE\n\nYOU CAN PURCHASE DYES\nIN THE STORE";
	}

	@Override
	protected void onButtonClick(int buttonIndex) {
		switch (buttonIndex) {
		case 0:
		case 2:
			this.showSelector(buttonIndex);
			break;
		}
	}

	@Override
	protected void postInitialized() {
		super.postInitialized();
		this.bSkin.setEnabled(false);
		this.bLegs.setEnabled(false);
	}

	@Override
	public void savePlayerData() {
		super.savePlayerData();

		switch (this.lastSelectorType) {
		case 0:
			StaticData.purchasedHairStyles[this.mmsContext.dialogConsideredIndex] = true;
			break;

		case 2:
			StaticData.purchasedTorsoStyles[this.mmsContext.dialogConsideredIndex] = true;
			break;
		}

		this.savePlayerHairIndex(false);
		EnvironmentVars.PREFERENCES_EDITOR.putInt(PreferenceKeys.KEY_PLAYER_TORSO_INDEX, StaticData.playerTorsoIndex);
		// Automatically commits
		PreferenceKeysMeta.encodeBooleanArray(PreferenceKeys.KEY_PURCHASED_TORSO_STYLES, EnvironmentVars.PREFERENCES_EDITOR,
				StaticData.purchasedTorsoStyles);
		PreferenceKeysMeta.encodeBooleanArray(PreferenceKeys.KEY_PURCHASED_HAIR_STYLES, EnvironmentVars.PREFERENCES_EDITOR,
				StaticData.purchasedHairStyles);

		this.revalidatePlayerSkinBodyParts();
	}

	protected void savePlayerHairIndex(boolean commit) {
		EnvironmentVars.PREFERENCES_EDITOR.putInt(PreferenceKeys.KEY_PLAYER_HAIR_INDEX, StaticData.playerHairIndex);
		if (commit)
			EnvironmentVars.PREFERENCES_EDITOR.commit();
	}

	protected void savePlayerTorsoIndex(boolean commit) {
		EnvironmentVars.PREFERENCES_EDITOR.putInt(PreferenceKeys.KEY_PLAYER_TORSO_INDEX, StaticData.playerTorsoIndex);
		if (commit)
			EnvironmentVars.PREFERENCES_EDITOR.commit();
	}

	private void showSelector(int selectorType) {
		boolean isSelectorNull = true;
		ITiledTextureRegion selectorTiles = null;

		switch (selectorType) {
		case 0:
			isSelectorNull = this.csHair == null;
			selectorTiles = StoreTextureRegions.region_hair_display;
			break;
		case 2:
			isSelectorNull = this.csTorso == null;
			selectorTiles = StoreTextureRegions.region_torso_display;
			break;
		}

		TileSelector selector = null;

		if (isSelectorNull) {

			final int TYPE = selectorType;

			selector = new PlayerBodyPartSelector(selectorTiles) {

				@Override
				protected void onClosed() {
					super.onClosed();
					EntityUtils.safetlyDetach(this.getFade());
				}

				@Override
				protected void onSelectionMade(int index) {
					super.onSelectionMade(index);

					String title = "", description = "";
					int price = 0;
					final float scale = 3.5f;
					ITextureRegion icon = null;
					boolean showDialog = false;

					if (!this.purchaseCanceled())
						switch (TYPE) {
						case 0:

							if (StaticData.purchasedHairStyles[index]) {
								StaticData.playerHairIndex = index;
								MainMenuWardrobe.this.savePlayerHairIndex(true);
								MainMenuWardrobe.this.revalidatePlayerSkinBodyParts();
								return;
							}

							title = String.format("HAIR (%s)", MainMenuStore.HAIR_TITLES[index]);
							description = MainMenuStore.HAIR_DESCRIPTION;
							price = MainMenuStore.HAIR_PRICES[index];
							icon = MainMenuWardrobe.this.csHair.getTiledTextureRegion().getTextureRegion(index);
							showDialog = true;

							MainMenuWardrobe.this.mmsContext.dialogConsideredIndex = index;
							break;

						case 2:

							if (StaticData.purchasedTorsoStyles[index]) {
								StaticData.playerTorsoIndex = index;

								MainMenuWardrobe.this.savePlayerTorsoIndex(true);
								MainMenuWardrobe.this.revalidatePlayerSkinBodyParts();
								return;
							}

							title = String.format("UPPER BODY (%s)", MainMenuStore.TORSO_TITLES[index]);
							description = MainMenuStore.TORSO_DESCRIPTION;
							price = MainMenuStore.TORSO_PRICES[index];
							icon = MainMenuWardrobe.this.csTorso.getTiledTextureRegion().getTextureRegion(index);
							showDialog = true;

							MainMenuWardrobe.this.mmsContext.dialogConsideredIndex = index;
							break;
						}

					if (showDialog)
						if (MainMenuWardrobe.this.mmsContext.showPurchaseDialog(title, description, price, icon, 0,
								icon.getWidth() * scale)) {
							MainMenuWardrobe.this.lastSelectorType = TYPE;
							MainMenuWardrobe.this.mmsContext.mmdbsDialogBoxState = MainMenuDialogBoxState.CONFIRM_WARDROBE_PURCHASE;
							MainMenuWardrobe.this.mmsContext.getDialog().getIcon().setScale(scale);
						}
				}

				@Override
				public void show(boolean... disabledSelections) {
					super.show(disabledSelections);
					switch (TYPE) {
					case 0:
						ResourceManager.btHairDisplay.load();
						break;
					case 2:
						ResourceManager.btTorsoDisplay.load();
						break;
					}
				}
			};
		}

		switch (selectorType) {
		case 0:
			if (this.csHair == null)
				this.csHair = selector;
			else
				selector = this.csHair;
			break;
		case 2:
			if (this.csTorso == null)
				this.csTorso = selector;
			else
				selector = this.csTorso;
			break;
		}

		selector.show(-1);

		boolean[] priceTagArray = null;
		int[] priceTagPrices = null;

		switch (selectorType) {
		case 0:
			priceTagArray = new boolean[StaticData.purchasedHairStyles.length];
			priceTagPrices = MainMenuStore.HAIR_PRICES;

			for (int i = 0; i < priceTagArray.length; i++)
				priceTagArray[i] = !StaticData.purchasedHairStyles[i];
			break;

		case 2:
			priceTagArray = new boolean[StaticData.purchasedTorsoStyles.length];
			priceTagPrices = MainMenuStore.TORSO_PRICES;

			for (int i = 0; i < priceTagArray.length; i++)
				priceTagArray[i] = !StaticData.purchasedTorsoStyles[i];
			break;
		}

		selector.addPriceTags(priceTagArray, priceTagPrices);

		try {
			super.mmsContext.attachChild(selector.getFade());
		} catch (final IllegalStateException ex) {

		}

		super.sCurrent = selector;
	}
}
