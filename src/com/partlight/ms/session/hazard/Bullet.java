package com.partlight.ms.session.hazard;

import com.partlight.ms.session.character.player.weapons.Ammunition;
import com.partlight.ms.session.character.player.weapons.Firearm;

public class Bullet implements Ammunition {

	public static final int MAX_BULLETS_ON_SCREEN = 8;

	private final Firearm faSender;

	public Bullet(Firearm sender) {
		this.faSender = sender;
	}

	public Firearm getSenderWeapon() {
		return this.faSender;
	}
}
