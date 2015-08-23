package com.partlight.ms.session.character.player.powerup;

public class Powerups {

	public static final Powerup[]	POWER_UPS;
	public static final int			ID_SPEED_BOOST	= 0;

	static {
		POWER_UPS = new Powerup[] {
				new Powerup(10f, Powerups.ID_SPEED_BOOST)
		};
	}
}
