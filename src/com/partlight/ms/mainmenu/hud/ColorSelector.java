package com.partlight.ms.mainmenu.hud;

import org.andengine.opengl.texture.region.ITextureRegion;

import com.partlight.ms.resource.ResourceManager;
import com.partlight.ms.resource.ResourceManager.StoreTextureRegions;

public class ColorSelector extends Selector {

	private final Float[][] fColorLibrary;

	public ColorSelector(Float[][] colorLibrary) {
		this.fColorLibrary = colorLibrary;
		super.postConstructor();
	}

	public Float[][] getColors() {
		return this.fColorLibrary;
	}

	@Override
	protected float getListItemB(int index) {
		return this.fColorLibrary[index][2] * super.getListItemB(index);
	}

	@Override
	protected float getListItemG(int index) {
		return this.fColorLibrary[index][1] * super.getListItemG(index);
	}

	@Override
	protected float getListItemR(int index) {
		return this.fColorLibrary[index][0] * super.getListItemR(index);
	}

	@Override
	protected ITextureRegion getListItemTextureRegion(int index) {
		return StoreTextureRegions.region_color_splash;
	}

	@Override
	protected float getListItemX(int index) {
		return 24f;
	}

	@Override
	protected void initVariables() {
		super.listTexture = ResourceManager.btColorSplash;
		super.listLength = this.fColorLibrary.length;
		super.listWidth = StoreTextureRegions.region_color_splash.getWidth();
	}
}
