package com.partlight.ms.session;

import com.partlight.ms.session.character.Armory;

public class SessionArmoryData {
	public final boolean[]	ARRAY_REPEATED_FIRE;
	public final float[]	ARRAY_FIRING_DELAY;
	public final float[]	ARRAY_RANGE;
	public final float[]	ARRAY_SPREAD;
	public final int[]		ARRAY_DAMAGE;
	public final int[]		ARRAY_BULLET_COUNT;

	public SessionArmoryData() {

		final int LENGTH = Armory.WEP_ARRAY.length;

		this.ARRAY_REPEATED_FIRE = new boolean[LENGTH];
		this.ARRAY_FIRING_DELAY = new float[LENGTH];
		this.ARRAY_RANGE = new float[LENGTH];
		this.ARRAY_SPREAD = new float[LENGTH];
		this.ARRAY_DAMAGE = new int[LENGTH];
		this.ARRAY_BULLET_COUNT = new int[LENGTH];

		for (int i = 0; i < LENGTH; i++) {
			this.ARRAY_REPEATED_FIRE[i] = Armory.WEP_ARRAY[i].hasRepeatedFire();
			this.ARRAY_FIRING_DELAY[i] = Armory.WEP_ARRAY[i].getFiringDelay();
			this.ARRAY_RANGE[i] = Armory.WEP_ARRAY[i].getRange();
			this.ARRAY_SPREAD[i] = Armory.WEP_ARRAY[i].getSpread();
			this.ARRAY_DAMAGE[i] = Armory.WEP_ARRAY[i].getDamage();
			this.ARRAY_BULLET_COUNT[i] = Armory.WEP_ARRAY[i].getBulletCount();
		}
	}
}