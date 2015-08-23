package com.partlight.ms.mainmenu.hud;

import org.andengine.opengl.texture.region.ITiledTextureRegion;

public class PlayerBodyPartSelector extends TileSelector {

	public PlayerBodyPartSelector(ITiledTextureRegion tiledTextureRegion) {
		super(tiledTextureRegion);
	}

	@Override
	protected float getListItemScale(int index) {
		return 8f;
	}
}
