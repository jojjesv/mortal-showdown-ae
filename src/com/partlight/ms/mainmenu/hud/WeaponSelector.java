package com.partlight.ms.mainmenu.hud;

import com.partlight.ms.resource.ResourceManager.HudRegions;
import com.partlight.ms.session.hud.Inventory;

public class WeaponSelector extends TileSelector {

	public WeaponSelector() {
		super(HudRegions.region_weps);
		super.setManageTexture(false);
	}

	@Override
	protected void initVariables() {
		super.initVariables();
		this.listRotation = Inventory.INVENTORY_ICON_ROTATION;
	}
}
