package com.partlight.ms.session.character.collectible;

import org.andengine.opengl.texture.region.ITiledTextureRegion;

import com.partlight.ms.resource.ResourceManager;
import com.partlight.ms.scene.session.SessionScene;
import com.partlight.ms.session.character.player.Player;
import com.partlight.ms.session.character.player.powerup.Powerups;
import com.partlight.ms.session.hud.ComboTracker.NotificationConstants;

public class SpeedCollectible extends Collectible {

	public static final String NOTIFICATION = "PICKED UP SPEED BOOST";

	public static final float	DURATION	= 10f;
	public static int			COLLECTIBLE_COUNT;

	public SpeedCollectible(float x, float y, ITiledTextureRegion textureRegion, int tileIndex, float boundaryWidth, float boundaryHeight,
			SessionScene context) {
		super(x, y, textureRegion, tileIndex, boundaryWidth, boundaryHeight, context);
		SpeedCollectible.COLLECTIBLE_COUNT++;
	}

	@Override
	public void onCollected(Player p) {

		if (p != null) {
			if (!p.isContextSessionScene())
				return;

			p.boostMoveSpeed(1.5f, SpeedCollectible.DURATION);
			((SessionScene) p.getContext()).getComboTracker().notify(SpeedCollectible.NOTIFICATION,
					NotificationConstants.NOTIFICATION_COLOR_MESSAGE);
			((SessionScene) p.getContext()).getInventoryButton().showPowerupIcon(Powerups.POWER_UPS[Powerups.ID_SPEED_BOOST]);
			ResourceManager.sNotif0.play();
		}

		SpeedCollectible.COLLECTIBLE_COUNT--;

		super.onCollected(p);
	}
}
