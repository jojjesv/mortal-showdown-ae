package com.partlight.ms.scene.mainmenu;

import org.andengine.opengl.texture.region.ITiledTextureRegion;

public class MainMenuStoreTab {
	String					tabTitle;
	Integer[]				itemPrices;
	String[]				itemTitles;
	Boolean[]				itemNeedsSelection;
	ITiledTextureRegion[]	itemIcons;
	String[]				itemDescriptions;

	public MainMenuStoreTab(String tabTitle, Integer[] itemPrices, String[] itemTitles, String[] itemDescriptions,
			Boolean[] itemNeedsSelection, ITiledTextureRegion[] itemIcons) {
		this.tabTitle = tabTitle;
		this.itemPrices = itemPrices;
		this.itemTitles = itemTitles;
		this.itemDescriptions = itemDescriptions;
		this.itemNeedsSelection = itemNeedsSelection;
		this.itemIcons = itemIcons;
	}
}
