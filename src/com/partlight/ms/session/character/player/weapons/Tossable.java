package com.partlight.ms.session.character.player.weapons;

import java.util.Random;

import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.MoveXModifier;
import org.andengine.entity.modifier.MoveYModifier;
import org.andengine.entity.modifier.RotationModifier;
import org.andengine.entity.sprite.Sprite;
import org.andengine.util.adt.array.ArrayUtils;
import org.andengine.util.modifier.ease.EaseCubicOut;

import com.partlight.ms.resource.EnvironmentVars;
import com.partlight.ms.session.character.Zombie;
import com.partlight.ms.session.character.player.Player;
import com.partlight.ms.session.hazard.Bullet;
import com.partlight.ms.util.EntityUtils;
import com.partlight.ms.util.TextureManagedSprite;
import com.partlight.ms.util.boundary.Boundary;
import com.partlight.ms.util.boundary.Boundary.BoundaryUtils;
import com.partlight.ms.util.boundary.SpriteBoundary;

public class Tossable extends Firearm {

	private TossableAmmoManager tamAmmoManager;

	public Tossable(int index, String friendlyName, float range, int damage, boolean repeatedThrow, float throwingDelay, int throwCount,
			float spread) {
		this(index, friendlyName, range, damage, repeatedThrow, throwingDelay, throwCount, spread, null);
	}

	public Tossable(int index, String friendlyName, float range, int damage, boolean repeatedThrow, float throwingDelay, int throwCount,
			float spread, TossableAmmoManager manager) {
		super(index, friendlyName, range, damage, repeatedThrow, throwingDelay, throwCount, spread);
		this.tamAmmoManager = manager;
	}

	public TossableAmmoManager getAmmoManager() {
		return this.tamAmmoManager;
	}

	@Override
	public String getAmmoPickupNotificationText() {
		return "PICKED UP A BOX OF " + this.getFriendlyName() + "S";
	}

	@Override
	public void onFire(Player p, float degrees) {

		final Sprite PROJECTILE = new TextureManagedSprite(p.getBoundaryCenterX(), p.getBoundaryCenterY(),
				ArrayUtils.random(this.tamAmmoManager.getProjectileRegionVariations()),
				EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager()) {
			private Boundary boundary;

			@Override
			protected void onManagedUpdate(float pSecondsElapsed) {
				super.onManagedUpdate(pSecondsElapsed);

				if (this.boundary == null)
					this.boundary = new SpriteBoundary(this);

				for (final Zombie z : Zombie.getAliveZombies())
					if (BoundaryUtils.isIntersecting(z, this.boundary))
						if (Tossable.this.onProjectileCollide(z))
							EntityUtils.safetlyDetachAndDispose(this);
			}
		};

		PROJECTILE.setScaleCenter(0, 0);
		PROJECTILE.setScale(2f);

		PROJECTILE.setRotationCenter(PROJECTILE.getWidthScaled() / 2f, PROJECTILE.getHeightScaled() / 2f);

		PROJECTILE.setPosition(PROJECTILE.getX() - PROJECTILE.getWidthScaled() / 2f, PROJECTILE.getY() - PROJECTILE.getHeightScaled() / 2f);

		final float X = PROJECTILE.getX(), Y = PROJECTILE.getY();
		final float ROT = 360f * new Random().nextFloat();

		final MoveXModifier AMMO_MOVE_X_MOD = new MoveXModifier(this.getRange() * 0.005f, X,
				X + (float) Math.cos(Math.toRadians(degrees)) * this.getRange(), EaseCubicOut.getInstance());

		final MoveYModifier AMMO_MOVE_Y_MOD = new MoveYModifier(AMMO_MOVE_X_MOD.getDuration(), Y - 8f,
				Y + (float) Math.sin(Math.toRadians(degrees)) * this.getRange(), EaseCubicOut.getInstance());

		final AlphaModifier AMMO_ALPHA_MOD = new AlphaModifier(AMMO_MOVE_X_MOD.getDuration(), 0, 1);
		final RotationModifier AMMO_ROT_MOD = new RotationModifier(AMMO_MOVE_X_MOD.getDuration(), ROT,
				ROT + (128f * AMMO_MOVE_X_MOD.getDuration()), EaseCubicOut.getInstance());

		AMMO_MOVE_X_MOD.setAutoUnregisterWhenFinished(true);
		AMMO_MOVE_Y_MOD.setAutoUnregisterWhenFinished(true);
		AMMO_ALPHA_MOD.setAutoUnregisterWhenFinished(true);
		AMMO_ROT_MOD.setAutoUnregisterWhenFinished(true);

		PROJECTILE.registerEntityModifier(AMMO_MOVE_X_MOD);
		PROJECTILE.registerEntityModifier(AMMO_MOVE_Y_MOD);
		PROJECTILE.registerEntityModifier(AMMO_ALPHA_MOD);
		PROJECTILE.registerEntityModifier(AMMO_ROT_MOD);

		PROJECTILE.setAlpha(0f);
		PROJECTILE.setRotation(ROT);
		PROJECTILE.setY(AMMO_MOVE_Y_MOD.getFromValue());

		this.tamAmmoManager.add(PROJECTILE);
	}

	public boolean onProjectileCollide(Zombie z) {
		if (!z.isMoveSpeedBoosted()) {
			z.boostMoveSpeed(0.6f, 8f);
			z.onHit(new Bullet(this));
			return true;
		}

		return false;
	}

	public void onRemoved(Sprite projectile) {

	}

	public void setAmmoManager(TossableAmmoManager manager) {
		this.tamAmmoManager = manager;
		this.tamAmmoManager.setCheckProjectileCollision(true);
	}
}
