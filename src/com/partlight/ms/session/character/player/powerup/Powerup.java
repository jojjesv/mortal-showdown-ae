package com.partlight.ms.session.character.player.powerup;

import com.partlight.ms.session.character.player.Player;

public class Powerup {

	private final float	duration;
	private final int	id;

	public Powerup(float duration) {
		this(duration, 0);
	}

	public Powerup(float duration, int id) {
		this.duration = duration;
		this.id = id;
	}

	public float getDuration() {
		return this.duration;
	}

	public int getId() {
		return this.id;
	}

	public void onTakeEffect(Player p) {

	}
}
