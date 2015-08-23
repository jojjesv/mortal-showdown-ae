package com.partlight.ms.session.character.player.weapons;

import java.util.ArrayList;

import org.andengine.audio.sound.Sound;
import org.andengine.entity.primitive.Line;
import org.andengine.util.adt.array.ArrayUtils;
import org.andengine.util.algorithm.collision.RectangularShapeCollisionChecker;

import com.partlight.ms.resource.EnvironmentVars;
import com.partlight.ms.resource.ResourceManager;
import com.partlight.ms.scene.session.SessionScene;
import com.partlight.ms.session.character.player.Player;
import com.partlight.ms.session.hazard.Bullet;
import com.partlight.ms.session.hazard.BulletTarget;
import com.partlight.ms.session.hud.ComboTracker.NotificationConstants;
import com.partlight.ms.util.EntityUtils;
import com.partlight.ms.util.SoundUtils;
import com.partlight.ms.util.boundary.Boundary.BoundaryUtils;

import android.util.Log;

public class Firearm {

	public static final String STRING_INFINITE = "INF";

	private Sound[]			sndFire;
	private final String	friendlyName;
	private final boolean	repeatedFire;
	private final float		firingDelay;
	private final float		range;
	private final float		spread;
	private final int		damage;
	private final int		fireBulletCount;
	private final int		index;
	private final float		muzzleFlashDeltaX;
	private final float		muzzleFlashDeltaY;

	public Firearm(int index, String friendlyName, float range, int damage, boolean repeatedFire, float firingDelay, int fireBulletCount,
			float spread) {
		this(index, friendlyName, range, damage, repeatedFire, firingDelay, fireBulletCount, spread, 0, 0);
	}

	public Firearm(int index, String friendlyName, float range, int damage, boolean repeatedFire, float firingDelay, int fireBulletCount,
			float spread, float muzzleFlashDeltaX, float muzzleFlashDeltaY) {
		this.index = index;
		this.friendlyName = friendlyName;
		this.fireBulletCount = fireBulletCount;
		this.range = range;
		this.damage = damage;
		this.repeatedFire = repeatedFire;
		this.firingDelay = firingDelay;
		this.spread = spread;
		this.muzzleFlashDeltaX = muzzleFlashDeltaX;
		this.muzzleFlashDeltaY = muzzleFlashDeltaY;
	}

	public String getAmmoPickupNotificationText() {
		return "PICKED UP AMMO FOR " + this.getFriendlyName();
	}

	/**
	 * @return The amount of bullets this weapon will fire per shot.
	 */
	public int getBulletCount() {
		return this.fireBulletCount;
	}

	public int getDamage() {
		return this.damage;
	}

	public Sound[] getFireSoundVariations() {
		return this.sndFire;
	}

	public float getFiringDelay() {
		return this.firingDelay;
	}

	public String getFriendlyName() {
		return this.friendlyName;
	}

	public int getIndex() {
		return this.index;
	}

	public float getMuzzleFlashDeltaX() {
		return this.muzzleFlashDeltaX;
	}

	public float getMuzzleFlashDeltaY() {
		return this.muzzleFlashDeltaY;
	}

	public float getRange() {
		return this.range;
	}

	public float getSpread() {
		return this.spread;
	}

	public boolean hasRepeatedFire() {
		return this.repeatedFire;
	}

	public void onAmmoPickup(Player p) {
		if (!p.isContextSessionScene())
			return;
		((SessionScene) p.getContext()).getComboTracker().notify(this.getAmmoPickupNotificationText(),
				NotificationConstants.NOTIFICATION_COLOR_MESSAGE);
		ResourceManager.sNotif0.play();
	}

	public void onFire(Player p, float degrees) {

		if (this.sndFire != null) {
			if (this.sndFire.length > 1)
				SoundUtils.playRandomVolume(ArrayUtils.random(this.sndFire));
			else
				SoundUtils.playRandomVolume(this.sndFire[0]);
		} else
			Log.e("Mortal Showdown", "Firearm's fire sound array seems to be null!");

		final float SPREAD_RANGE = 90f * this.spread;

		final ArrayList<BulletTarget> HIT_TARGETS = new ArrayList<>();

		final Line BULLET_PATH = new Line(0f, 0f, 1f, 1f, EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager());
		final float BULLET_X1 = p.getBoundaryCenterX();
		final float BULLET_Y1 = p.getBoundaryCenterY();
		final float BULLET_RANGE = p.getWeapon().getRange();

		float radians;

		for (int i = 0; i < this.getBulletCount(); i++) {

			radians = (float) Math.toRadians(degrees - (SPREAD_RANGE * this.getBulletCount()) / 2f + SPREAD_RANGE * i);

			BULLET_PATH.setPosition(BULLET_X1, BULLET_Y1, BULLET_X1 + (float) Math.cos(radians) * BULLET_RANGE,
					BULLET_Y1 + (float) Math.sin(radians) * BULLET_RANGE);

			for (final BulletTarget target : ((SessionScene) p.getContext()).getBulletTargets())
				if (RectangularShapeCollisionChecker.checkCollision(BoundaryUtils.toRectangularShape(target), BULLET_PATH))
					HIT_TARGETS.add(target);

			if (HIT_TARGETS.size() > 0) {
				final Bullet BULLET = new Bullet(p.getWeapon());

				HIT_TARGETS.get(0).onHit(BULLET);

				p.onBulletTargetHit(HIT_TARGETS.get(0));

				HIT_TARGETS.clear();
			}
		}

		EntityUtils.safetlyDetachAndDispose(BULLET_PATH);
	}

	public void setFireSounds(Sound... variations) {
		this.sndFire = variations;
	}
}
