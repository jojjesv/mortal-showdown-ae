package com.partlight.ms.session.character.collectible;

import java.util.Random;

import org.andengine.audio.sound.Sound;
import org.andengine.opengl.texture.region.ITiledTextureRegion;

import com.partlight.ms.resource.ResourceManager;
import com.partlight.ms.scene.session.SessionScene;
import com.partlight.ms.session.character.player.Player;
import com.partlight.ms.session.hud.ComboTracker.NotificationConstants;

public class HealthCollectible extends Collectible {

	private static final Sound	MEDKIT_SOUND	= ResourceManager.sMedkit;
	public static int			COLLECTIBLE_COUNT;

	public static final String[] NOTIFICATIONS = new String[] {
			"PICKED UP FIRST AID",
			"MUCH BETTER",
			"GOOD TO GO"
	};

	private final int healthAmount;

	public HealthCollectible(float x, float y, ITiledTextureRegion textureRegion, int tileIndex, float boundaryWidth, float boundaryHeight,
			SessionScene context, int healthAmount) {
		super(x, y, textureRegion, tileIndex, boundaryWidth, boundaryHeight, context);
		this.healthAmount = healthAmount;
		HealthCollectible.COLLECTIBLE_COUNT++;
	}

	@Override
	public void onCollected(Player p) {

		if (!p.isContextSessionScene())
			return;

		if (p != null) {
			final int P_HEALTH = p.getHealthAmount();
			final int P_MAX_HEALTH = p.getMaxHealthAmount();

			if (P_HEALTH + this.healthAmount > P_MAX_HEALTH)
				p.setHealthAmount(P_MAX_HEALTH);
			else
				p.setHealthAmount(P_HEALTH + this.healthAmount);

			((SessionScene) p.getContext()).getComboTracker().notify(
					HealthCollectible.NOTIFICATIONS[new Random().nextInt(HealthCollectible.NOTIFICATIONS.length)],
					NotificationConstants.NOTIFICATION_COLOR_MESSAGE);

			HealthCollectible.MEDKIT_SOUND.play();
		}

		HealthCollectible.COLLECTIBLE_COUNT--;

		super.onCollected(p);
	}
}
