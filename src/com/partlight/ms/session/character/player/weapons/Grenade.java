package com.partlight.ms.session.character.player.weapons;

import org.andengine.entity.sprite.Sprite;

import com.partlight.ms.session.achievement.AchievementsManager;
import com.partlight.ms.session.character.Zombie;
import com.partlight.ms.session.character.player.Player;

public class Grenade extends Tossable {

	public interface OnExplodeListener {
		public void onExplode(float x, float y);
	}

	private OnExplodeListener oelExplodeListener;

	public Grenade(int index, String friendlyName, float range, int damage, boolean repeatedThrow, float throwingDelay, int throwCount,
			float spread) {
		super(index, friendlyName, range, damage, repeatedThrow, throwingDelay, throwCount, spread);
	}

	@Override
	public void onFire(Player p, float degrees) {
		super.onFire(p, degrees);
		AchievementsManager.onThrowGrenade();
	}

	@Override
	public boolean onProjectileCollide(Zombie z) {
		return false;
	}

	@Override
	public void onRemoved(Sprite projectile) {
		if (this.oelExplodeListener != null)
			this.oelExplodeListener.onExplode(projectile.getX() + projectile.getWidthScaled() / 2f,
					projectile.getY() + projectile.getHeightScaled() / 2f);
	}

	public void setOnExplodeListener(OnExplodeListener listener) {
		this.oelExplodeListener = listener;
	}
}
