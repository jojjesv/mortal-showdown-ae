package com.partlight.ms.session.character.collectible;

import org.andengine.opengl.texture.region.ITiledTextureRegion;

import com.partlight.ms.scene.session.SessionScene;
import com.partlight.ms.session.character.Armory;
import com.partlight.ms.session.character.player.Player;

public class AmmoCollectible extends Collectible {

	private final int	wepIndex;
	private final int	ammoAmount;

	public AmmoCollectible(float x, float y, ITiledTextureRegion textureRegion, int tileIndex, float boundaryWidth, float boundaryHeight,
			SessionScene context, int wepIndex, int ammoAmount) {
		super(x, y, textureRegion, tileIndex, boundaryWidth, boundaryHeight, context);
		this.wepIndex = wepIndex;
		this.ammoAmount = ammoAmount;
	}

	@Override
	public void onCollected(Player p) {

		if (p != null) {
			if (!p.containsWeaponInInventory(this.wepIndex))
				p.giveWeapon(this.wepIndex);

			p.setAmmo(this.wepIndex, p.getAmmo(this.wepIndex) + this.ammoAmount);
			Armory.WEP_ARRAY[this.wepIndex].onAmmoPickup(p);
		}

		super.onCollected(p);
	}
}