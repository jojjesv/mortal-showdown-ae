package com.partlight.ms.mainmenu.hud;

import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;

public class TileSelector extends Selector {

	private final ITiledTextureRegion ttrTiledTextureRegion;

	public TileSelector(ITiledTextureRegion tiledTextureRegion) {
		this.ttrTiledTextureRegion = tiledTextureRegion;
		super.postConstructor();
	}

	@Override
	protected ITextureRegion getListItemTextureRegion(int index) {
		return this.ttrTiledTextureRegion.getTextureRegion(index);
	}

	public ITiledTextureRegion getTiledTextureRegion() {
		return this.ttrTiledTextureRegion;
	}

	@Override
	protected void initVariables() {
		super.listTexture = this.ttrTiledTextureRegion.getTexture();
		super.listLength = this.ttrTiledTextureRegion.getTileCount();
		super.listWidth = this.ttrTiledTextureRegion.getWidth() + this.getListItemX(0);
	}
}
