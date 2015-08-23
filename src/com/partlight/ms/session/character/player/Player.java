package com.partlight.ms.session.character.player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.andengine.audio.sound.Sound;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.util.modifier.IModifier;
import org.andengine.util.modifier.IModifier.IModifierListener;

import com.partlight.ms.resource.EnvironmentVars;
import com.partlight.ms.resource.EnvironmentVars.StaticData;
import com.partlight.ms.resource.ResourceManager;
import com.partlight.ms.resource.ResourceManager.PlayerRegions;
import com.partlight.ms.scene.session.SessionScene;
import com.partlight.ms.session.character.Armory;
import com.partlight.ms.session.character.Character;
import com.partlight.ms.session.character.InterpolatedMovementCharacter;
import com.partlight.ms.session.character.Zombie;
import com.partlight.ms.session.character.collectible.Collectible;
import com.partlight.ms.session.character.player.libraries.PlayerArms;
import com.partlight.ms.session.character.player.weapons.Firearm;
import com.partlight.ms.session.character.skin.CharacterSkin;
import com.partlight.ms.session.character.skin.player.PlayerCharacterSkin;
import com.partlight.ms.session.hazard.BulletTarget;
import com.partlight.ms.session.hud.ComboTracker;
import com.partlight.ms.session.hud.FireButton;
import com.partlight.ms.session.hud.notification.NotificationRewards;
import com.partlight.ms.util.Ruler;
import com.partlight.ms.util.SoundUtils;
import com.partlight.ms.util.updatehandler.FlashUpdateHandler;

import android.util.Log;

public class Player extends InterpolatedMovementCharacter {

	@SuppressWarnings("unused")
	private static final Sound[]	FOOTSTEPS		= ResourceManager.sFootsteps;
	public static final boolean		USE_FOOTSTEPS	= false;

	private FlashUpdateHandler	fuhInvisible;
	private List<Firearm>		weps;
	private Firearm				wepCurrent;
	private boolean				canFire	= true;
	private boolean				invisible;
	private float				totalSecondsElapsedSinceShoot;
	private boolean				hasShotOnce;

	private final int[] ammo;

	private boolean isUsingAutomaticShotgun;

	private boolean isUsingSniperExplosiveTips;

	private TiledSprite tsWepMuzzleFlash;

	public Player(float pX, float pY, SessionScene context) {
		super(pX, pY, new PlayerCharacterSkin(), 100, context);
		this.ammo = new int[Armory.WEP_ARRAY.length];

		if (this.isContextSessionScene())
			this.getContextAsSession().ARMORY_DATA.ARRAY_REPEATED_FIRE[Armory.WEP_DOMINADOR] = StaticData.dominador_repeatedFire;
		this.getSkin().getLegs().setCurrentTileIndex(Character.directionToStandIndex(this.getMoveDirection()));
	}

	protected boolean assertCanShoot() {
		return true;
	}

	public boolean canFire() {
		return this.canFire;
	}

	public boolean containsWeaponInInventory(int index) {
		return this.weps.size() > index;
	}

	private void detachMuzzleFlash() {
		if (this.tsWepMuzzleFlash != null && this.tsWepMuzzleFlash.getParent() != null) {
			this.tsWepMuzzleFlash.detachSelf();
			this.tsWepMuzzleFlash.getTextureRegion().getTexture().unload();
		}
	}

	protected void flashWeaponMuzzle() {
		ITiledTextureRegion muzzleTextureRegion = null;

		switch (this.getWeaponIndex()) {
		case Armory.WEP_PISTOL:
		case Armory.WEP_DOMINADOR:
			muzzleTextureRegion = PlayerRegions.region_a01_muzzle;
			break;
		case Armory.WEP_SMG:
			muzzleTextureRegion = PlayerRegions.region_a02_muzzle;
			break;
		case Armory.WEP_SHOTGUN:
			muzzleTextureRegion = PlayerRegions.region_a03_muzzle;
			break;
		case Armory.WEP_SNIPER:
			muzzleTextureRegion = PlayerRegions.region_a04_muzzle;
			break;
		}

		if (this.tsWepMuzzleFlash != null && !this.tsWepMuzzleFlash.isDisposed()) {
			this.tsWepMuzzleFlash.detachSelf();
			this.tsWepMuzzleFlash.getTextureRegion().getTexture().unload();
			this.tsWepMuzzleFlash.dispose();
		}

		final AnimatedSprite ARMS = this.getSkin().getArms();

		if (ARMS.getCurrentTileIndex() < 6 || muzzleTextureRegion == null)
			return;

		this.tsWepMuzzleFlash = new TiledSprite(0, 0, muzzleTextureRegion, EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager());
		this.tsWepMuzzleFlash.setCurrentTileIndex(ARMS.getCurrentTileIndex() - 6);

		if (ARMS.isFlippedHorizontal())
			this.tsWepMuzzleFlash.setPosition(-this.wepCurrent.getMuzzleFlashDeltaX(), -this.wepCurrent.getMuzzleFlashDeltaY());
		else
			this.tsWepMuzzleFlash.setPosition(this.wepCurrent.getMuzzleFlashDeltaX(), this.wepCurrent.getMuzzleFlashDeltaY());

		this.tsWepMuzzleFlash.setFlippedHorizontal(ARMS.isFlippedHorizontal());
		this.getSkin().attachChild(this.tsWepMuzzleFlash);

		muzzleTextureRegion.getTexture().load();
	}

	public int getAmmo(int wepIndex) {
		return this.ammo[wepIndex];
	}

	public Firearm getWeapon() {
		return this.wepCurrent;
	}

	public int getWeaponCount() {
		return this.weps.size();
	}

	public int getWeaponIndex() {
		return this.wepCurrent.getIndex();
	}

	public void giveWeapon(int wep) {
		if (this.weps == null)
			this.weps = new ArrayList<Firearm>();

		this.weps.add(Armory.WEP_ARRAY[wep]);
	}

	public boolean isDamageInvisible() {
		return this.invisible;
	}

	public boolean isFiring() {
		return this.getSkin().getArms().getCurrentTileIndex() > 5;
	}

	public void onBulletTargetHit(BulletTarget target) {
		if (this.getWeaponIndex() == Armory.WEP_SNIPER && Player.this.isUsingSniperExplosiveTips)
			((SessionScene) this.getContext()).createExplosion(target.getBoundaryX() + target.getBoundaryWidth() / 2f,
					target.getBoundaryY() + target.getBoundaryHeight() / 2f, 96f, 128f, false);
	}

	@Override
	public void onDetached() {
		this.detachChildren();
		super.onDetached();
	}

	public void onFireOnEmptyClip() {
		SoundUtils.playRandomVolume(ResourceManager.sDryfire);
	}

	public void onGetCollectible(Collectible c) {
		c.onCollected(this);
	}

	// @Override
	// public void setCurrentTileIndex(int pCurrentTileIndex) {
	// super.setCurrentTileIndex(pCurrentTileIndex);
	//
	// if (Player.USE_FOOTSTEPS) {
	// if ((pCurrentTileIndex + 1) % 2 == 0) {
	// ArrayUtils.random(Player.FOOTSTEPS).play();
	// }
	// }
	// }

	@Override
	public void onHealthDepleted() {
		super.onHealthDepleted();

		if (!this.isContextSessionScene())
			return;

		final DelayModifier dmDeathDelay = new DelayModifier(0.5f);
		dmDeathDelay.setAutoUnregisterWhenFinished(true);
		dmDeathDelay.addModifierListener(new IModifierListener<IEntity>() {
			@Override
			public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
				((SessionScene) Player.this.getContext()).onPlayerDead();
			}

			@Override
			public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
			}
		});
		this.getContext().registerEntityModifier(dmDeathDelay);
	}

	@Override
	protected void onManagedUpdate(float pSecondsElapsed) {
		super.onManagedUpdate(pSecondsElapsed);

		if (!this.isContextSessionScene())
			return;

		final FireButton CONTEXT_FIRE_BUTTON = ((SessionScene) this.getContext()).getFireButton();

		if (this.totalSecondsElapsedSinceShoot < ((SessionScene) this.getContext()).ARMORY_DATA.ARRAY_FIRING_DELAY[this.getWeapon()
				.getIndex()]) {
			this.totalSecondsElapsedSinceShoot += pSecondsElapsed;
			if (this.totalSecondsElapsedSinceShoot > 0.1f) {
				if (this.isFiring())
					this.getSkin().getArms().setCurrentTileIndex(this.getSkin().getArms().getCurrentTileIndex() - 6);
				this.detachMuzzleFlash();
			}
		} else {
			this.detachMuzzleFlash();
			this.canFire = true;

			if (((((SessionScene) this.getContext()).ARMORY_DATA.ARRAY_REPEATED_FIRE[this.getWeapon().getIndex()]) ? true
					: !this.hasShotOnce) && CONTEXT_FIRE_BUTTON.isPressed()) {
				this.shoot();
				this.hasShotOnce = true;
				this.totalSecondsElapsedSinceShoot = 0f;
			}
		}

		if (!CONTEXT_FIRE_BUTTON.isPressed())
			this.hasShotOnce = false;

		for (final Collectible c : Collectible.getCollectiblesOnScreen())
			if (BoundaryUtils.isIntersecting(this.getInteractionBoundary(), c.getBoundary()))
				this.onGetCollectible(c);
	}

	public void onNewMultiplier(int multiplier) {
		if (!this.isContextSessionScene())
			return;

		final ComboTracker t = ((SessionScene) this.getContext()).getComboTracker();

		for (int i = 0; i < NotificationRewards.NOTIFICATION_MULTIPLIERS.length; i++)
			if (multiplier == NotificationRewards.NOTIFICATION_MULTIPLIERS[i]) {

				t.notify(NotificationRewards.NOTIFICATION_DESCRIPTIONS[i], NotificationRewards.NOTIFICATION_COLORS[i]);

				switch (i) {
				case 0:
					((SessionScene) this.getContext()).ARMORY_DATA.ARRAY_REPEATED_FIRE[Armory.WEP_PISTOL] = true;
					ResourceManager.sNotif0.play();
					break;
				case 1:
					this.giveWeapon(1);
					this.setAmmo(Armory.WEP_SMG, 100);
					ResourceManager.sNotif1.play();
					break;
				case 2:
					((SessionScene) this.getContext()).ARMORY_DATA.ARRAY_DAMAGE[Armory.WEP_PISTOL] = 10;
					ResourceManager.sNotif0.play();
					break;
				case 3:
					this.giveWeapon(Armory.WEP_DOMINADOR);
					this.setAmmo(Armory.WEP_DOMINADOR, 25);
					ResourceManager.sNotif1.play();
					break;
				case 4:
					((SessionScene) this.getContext()).setClipSize(Armory.WEP_SMG, 75);
					ResourceManager.sNotif0.play();
					break;
				case 5:
					((SessionScene) this.getContext()).ARMORY_DATA.ARRAY_DAMAGE[Armory.WEP_DOMINADOR] = 30;
					ResourceManager.sNotif0.play();
					break;
				case 6:
					this.giveWeapon(Armory.WEP_SHOTGUN);
					this.setAmmo(Armory.WEP_SHOTGUN, 26);
					ResourceManager.sNotif1.play();
					break;
				case 7:
					this.giveWeapon(Armory.WEP_SNIPER);
					this.setAmmo(Armory.WEP_SNIPER, 12);
					ResourceManager.sNotif1.play();
					break;
				case 8:
					this.giveWeapon(Armory.WEP_CALTROP);
					this.setAmmo(Armory.WEP_CALTROP, 33);
					ResourceManager.sNotif1.play();
					break;
				case 9:
					this.isUsingAutomaticShotgun = true;
					((SessionScene) this.getContext()).ARMORY_DATA.ARRAY_REPEATED_FIRE[Armory.WEP_SHOTGUN] = true;
					((SessionScene) this.getContext()).ARMORY_DATA.ARRAY_FIRING_DELAY[Armory.WEP_SHOTGUN] = 0.25f;
					ResourceManager.sNotif0.play();
					break;
				case 10:
					this.giveWeapon(Armory.WEP_GRENADE);
					this.setAmmo(Armory.WEP_GRENADE, 10);
					ResourceManager.sNotif1.play();
					break;
				case 11:
					((SessionScene) this.getContext()).ARMORY_DATA.ARRAY_FIRING_DELAY[Armory.WEP_SNIPER] = 0.6f;
					ResourceManager.sNotif0.play();
					break;
				case 12:
					((SessionScene) this.getContext()).ARMORY_DATA.ARRAY_DAMAGE[Armory.WEP_GRENADE] = 164;
					ResourceManager.sNotif0.play();
					break;
				case 13:
					this.isUsingSniperExplosiveTips = true;
					ResourceManager.sNotif0.play();
					break;
				case 14:
					((SessionScene) this.getContext()).setClipSize(Armory.WEP_SHOTGUN, 32);
					ResourceManager.sNotif0.play();
					break;

				default:
					break;
				}
			}
	}

	public void setAmmo(int wepIndex, int ammo) {
		this.ammo[wepIndex] = ammo;
		if (wepIndex == this.getWeapon().getIndex())
			if (ammo != -1)
				try {
					((SessionScene) this.getContext()).getInventoryButton().setAmmo(ammo);
				} catch (final Exception ex) {
				}
			else
				((SessionScene) this.getContext()).getInventoryButton().setAmmo(Firearm.STRING_INFINITE);
	}

	protected void setDamageInvisible() {

		if (this.fuhInvisible == null) {
			this.fuhInvisible = new FlashUpdateHandler(0.1f, 5);
			this.fuhInvisible.runOnSwitch(new Runnable() {
				@Override
				public void run() {
					Player.this.setAlpha(1f - Player.this.getAlpha());
				}
			});
			this.fuhInvisible.runOnFinish(new Runnable() {
				@Override
				public void run() {
					Player.this.setAlpha(1f);
					Player.this.invisible = false;
					for (final Zombie z : Zombie.getAliveZombies()) {
						z.startMoving();
						z.getAI().setIgnoreTarget(false);
					}
					Player.this.getContext().unregisterUpdateHandler(Player.this.fuhInvisible);
				}
			});
		}
		this.fuhInvisible.reset();
		this.getContext().registerUpdateHandler(this.fuhInvisible);

		this.invisible = true;
	}

	@Override
	public void setHealthAmount(int amount) {

		if (this.isDamageInvisible())
			return;

		if (this.getHealthAmount() > amount) {
			this.drawBlood();
			this.setDamageInvisible();
			for (final Zombie z : Zombie.getAliveZombies())
				z.getAI().setIgnoreTarget(true);
		}

		super.setHealthAmount(amount);
	}

	public void setWeapon(int index) {
		if ((this.getWeapon() == null) ? true : this.getWeapon().getIndex() != index)
			this.totalSecondsElapsedSinceShoot = 0f;

		final CharacterSkin SKIN = this.getSkin();

		SKIN.unloadTextures();

		final int newWepTileIndex = SKIN.getArms().getCurrentTileIndex();

		if (this.weps == null || this.weps.isEmpty()) {
			Log.e("Mortal Showdown", "Weapon list is either null or empty!");
			return;
		}

		this.wepCurrent = this.weps.get(index);

		// arms index
		switch (index) {
		case Armory.WEP_PISTOL:
		case Armory.WEP_DOMINADOR:
			SKIN.setArms(PlayerArms.ARMS_PISTOL);
			break;
		case Armory.WEP_SMG:
			SKIN.setArms(PlayerArms.ARMS_SMG);
			break;
		case Armory.WEP_SHOTGUN:
			SKIN.setArms(PlayerArms.ARMS_SHOTGUN);
			break;
		case Armory.WEP_SNIPER:
			SKIN.setArms(PlayerArms.ARMS_SNIPER);
			break;
		case Armory.WEP_CALTROP:
		case Armory.WEP_GRENADE:
			SKIN.setArms(PlayerArms.ARMS_UNARMED);
			break;
		}

		SKIN.loadTextures();

		if (this.ammo[index] == -1)
			((SessionScene) this.getContext()).getInventoryButton().setAmmo(Firearm.STRING_INFINITE);
		else
			((SessionScene) this.getContext()).getInventoryButton().setAmmo(this.ammo[index]);
		SKIN.getArms().setCurrentTileIndex(newWepTileIndex);
	}

	@Override
	public void setX(float pX) {
		final float X_MIN = -this.getSkin().getLegs().getWidthScaled() * 3f;
		final float X_MAX = this.getContext().getLevel().getMapWidth() - X_MIN;
		pX = Ruler.clamp(pX, X_MIN, X_MAX);
		super.setX(pX);
	}

	@Override
	public void setY(float pY) {
		final float Y_MIN = -this.getSkin().getLegs().getHeightScaled() * 3f;
		final float Y_MAX = this.getContext().getLevel().getMapHeight() - Y_MIN;
		pY = Ruler.clamp(pY, Y_MIN, Y_MAX);
		super.setY(pY);
	}

	public void shoot() {

		if (!this.canFire || this.invisible)
			return;

		if (!this.assertCanShoot() || (this.ammo[this.wepCurrent.getIndex()] == -1 ? false : this.ammo[this.wepCurrent.getIndex()] == 0)) {
			this.onFireOnEmptyClip();
			return;
		}

		if (this.wepCurrent.getIndex() == Armory.WEP_SHOTGUN && !this.isUsingAutomaticShotgun) {
			final DelayModifier SHOTGUN_PUMP_DELAY = new DelayModifier(0.25f);

			//@formatter:off
			SHOTGUN_PUMP_DELAY.addModifierListener(new IModifierListener<IEntity>() {
				@Override
				public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
					ResourceManager.sShotgunPump.play();
				}
				@Override
				public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) { }
			});
			//@formatter:on

			SHOTGUN_PUMP_DELAY.setAutoUnregisterWhenFinished(true);
			this.getContext().registerEntityModifier(SHOTGUN_PUMP_DELAY);
		}

		// Set arms
		{
			final AnimatedSprite ARMS = this.getSkin().getArms();
			if (ARMS.getTiledTextureRegion().getTileCount() >= ARMS.getCurrentTileIndex() + 6)
				ARMS.setCurrentTileIndex(ARMS.getCurrentTileIndex() + 6);
		}

		EnvironmentVars.MAIN_CONTEXT.runOnUpdateThread(new Runnable() {

			@Override
			public void run() {
				Player.this.flashWeaponMuzzle();
			}
		});

		this.canFire = false;

		if (this.wepCurrent.getIndex() != Armory.WEP_GRENADE && this.wepCurrent.getIndex() != Armory.WEP_CALTROP)
			((SessionScene) this.getContext()).getCameraManager().shakeRandomDirection();

		this.getWeapon().onFire(this, Character.directionToDegrees(this.getMoveDirection()));

		if (this.ammo[this.wepCurrent.getIndex()] != -1
				&& ((StaticData.glitchClipItems[this.getWeaponIndex()]) ? new Random().nextInt(4) != 0 : true)) {
			this.ammo[this.wepCurrent.getIndex()] -= this.wepCurrent.getBulletCount();

			boolean emptyClip = false;

			if (this.ammo[this.wepCurrent.getIndex()] <= 0) {
				emptyClip = true;
				this.ammo[this.wepCurrent.getIndex()] = 0;
			}

			((SessionScene) this.getContext()).getInventoryButton().setAmmo(this.ammo[this.wepCurrent.getIndex()]);

			if (emptyClip)
				return;
		}

	}
}
