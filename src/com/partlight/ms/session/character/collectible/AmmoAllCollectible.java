package com.partlight.ms.session.character.collectible;

import org.andengine.opengl.texture.region.ITiledTextureRegion;

import com.partlight.ms.resource.ResourceManager;
import com.partlight.ms.scene.session.SessionScene;
import com.partlight.ms.session.character.player.Player;
import com.partlight.ms.session.hud.ComboTracker.NotificationConstants;

public class AmmoAllCollectible extends Collectible {

	public static final String NOTIFICATION = "PICKED UP AMMO FOR ALL WEAPONS";

	public AmmoAllCollectible(float x, float y, ITiledTextureRegion textureRegion, int tileIndex, float boundaryWidth, float boundaryHeight,
			SessionScene context) {
		super(x, y, textureRegion, tileIndex, boundaryWidth, boundaryHeight, context);
	}

	@Override
	public void onCollected(Player p) {

		if (p != null) {

			if (!p.isContextSessionScene())
				return;

			final int WEP_COUNT = p.getWeaponCount();

			for (int i = 0; i < WEP_COUNT; i++)
				p.setAmmo(i, p.getAmmo(i) + ((SessionScene) p.getContext()).getClipSize(i));

			((SessionScene) p.getContext()).getComboTracker().notify(AmmoAllCollectible.NOTIFICATION,
					NotificationConstants.NOTIFICATION_COLOR_MESSAGE);
			ResourceManager.sNotif0.play();
		}

		super.onCollected(p);
	}
}
