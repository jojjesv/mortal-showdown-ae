package com.partlight.ms.session.character;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.AnimatedSprite.IAnimationListener;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.util.adt.list.ListUtils;

import com.partlight.ms.activity.GameActivity.GooglePlayConstants;
import com.partlight.ms.resource.EnvironmentVars;
import com.partlight.ms.resource.EnvironmentVars.StaticData;
import com.partlight.ms.resource.ResourceManager.HudRegions;
import com.partlight.ms.scene.DialogLevelScene;
import com.partlight.ms.scene.session.SessionScene;
import com.partlight.ms.session.achievement.AchievementsManager;
import com.partlight.ms.session.character.ai.ZombieAI;
import com.partlight.ms.session.character.collectible.AmmoAllCollectible;
import com.partlight.ms.session.character.collectible.AmmoCollectible;
import com.partlight.ms.session.character.collectible.Collectible;
import com.partlight.ms.session.character.collectible.HealthCollectible;
import com.partlight.ms.session.character.collectible.SpeedCollectible;
import com.partlight.ms.session.character.skin.CharacterSkin;
import com.partlight.ms.session.hazard.Bullet;
import com.partlight.ms.session.hazard.BulletTarget;

public class Zombie extends Character implements BulletTarget {

	public static final float	WALK_FRAME_DURATION_FACTOR	= 2.5f;
	private static List<Zombie>	aliveZombies;
	private static final int	COMBO_INCREMENT				= 65;

	static {
		Zombie.aliveZombies = new ArrayList<Zombie>();
	}

	/**
	 * Returns all Zombies that are attached to the current {@link SessionScene}
	 * .<br>
	 * 
	 * @return All "alive zombies."
	 */
	public static List<Zombie> getAliveZombies() {
		return Zombie.aliveZombies;
	}

	private boolean	isAttacking;
	private boolean	skipComboAndScoreIncrement;
	private int		scoreValue;
	private boolean	isDisoriented;
	private boolean	isDummy;

	// CONSTRUCTORS
	// *********************************

	// Number of zombie-targets while disoriented.
	private int disorientTargetCount;

	// *********************************
	//

	// GET & SET METHODS
	// *********************************

	private int itemSpawnChance;

	/**
	 * Creates a new Zombie.<br>
	 * <br>
	 * A Zombie will follow it's Character target
	 */
	public Zombie(float pX, float pY, CharacterSkin skin, int health, DialogLevelScene context) {
		super(pX, pY, skin, health, context);
		Zombie.getAliveZombies().add(this);
		if (this.isContextSessionScene())
			((SessionScene) context).addBulletTarget(this);
	}

	public void attackTarget() {

		if (this.isAttacking)
			return;

		this.isAttacking = true;

		this.stopMoving();

		final int STARTFRAME = this.getAnimatedArmsStartFrame();

		this.getSkin().getArms().animate(new long[] {
				150,
				150,
				100,
				100
		}, STARTFRAME, STARTFRAME + 3, false, new IAnimationListener() {
			@Override
			public void onAnimationFinished(AnimatedSprite pAnimatedSprite) {
				Zombie.this.getSkin().getArms().setCurrentTileIndex(STARTFRAME - 1);
				Zombie.this.isAttacking = false;

				if (Zombie.this.getAI() instanceof ZombieAI)
					if (!((ZombieAI) Zombie.this.getAI()).hasHitTargetSinceAttack()) {
						Zombie.this.startMoving();
						Zombie.this.getAI().setIgnoreTarget(false);
					}
			}

			@Override
			public void onAnimationFrameChanged(AnimatedSprite pAnimatedSprite, int pOldFrameIndex, int pNewFrameIndex) {
			}

			@Override
			public void onAnimationLoopFinished(AnimatedSprite pAnimatedSprite, int pRemainingLoopCount, int pInitialLoopCount) {
			}

			@Override
			public void onAnimationStarted(AnimatedSprite pAnimatedSprite, int pInitialLoopCount) {
			}
		});
	}

	public boolean canDropItem() {
		return this.getBoundaryX() > 0f && this.getBoundaryY() > 0f
				&& this.getBoundaryX() + this.getBoundaryWidth() < EnvironmentVars.MAIN_CONTEXT.width()
				&& this.getBoundaryY() + this.getBoundaryHeight() < EnvironmentVars.MAIN_CONTEXT.height();
	}

	public void dieWithoutScoreAndComboIncrement() {
		this.skipComboAndScoreIncrement = true;
		this.setHealthAmount(0);
	}

	public int getAnimatedArmsStartFrame() {
		switch (this.getMoveDirection()) {
		case NORTH:
			return 1;

		case NORTHEAST:
		case NORTHWEST:
			return 6;

		case EAST:
		case WEST:
			return 11;

		case SOUTHEAST:
		case SOUTHWEST:
			return 16;

		case SOUTH:
			return 21;

		default:
			return 0;
		}
	}

	public int getItemSpawnChance() {
		return this.itemSpawnChance;
	}

	public int getScoreValue() {
		return this.scoreValue;
	}

	public boolean isAttacking() {
		return this.isAttacking;
	}

	public boolean isDummy() {
		return this.isDummy;
	}

	public boolean isWalking() {
		return this.getMoveSpeed() < 0.5f;
	}

	// *********************************
	//

	// EVENT METHODS
	// *********************************

	@Override
	public void onDetached() {
		Zombie.getAliveZombies().remove(this);
		if (this.isContextSessionScene())
			((SessionScene) this.getContext()).removeBulletTarget(this);
		super.onDetached();
	}

	@Override
	public void onHealthDepleted() {
		super.onHealthDepleted();

		if (!this.isContextSessionScene())
			return;

		final SessionScene SESSION = ((SessionScene) this.getContext());

		for (final Zombie z : Zombie.getAliveZombies())
			if (z.getAI().getTarget() == this && z.isDisoriented && z.disorientTargetCount > 0) {
				z.setTargetRandomZombie();
				z.disorientTargetCount--;
				if (z.disorientTargetCount <= 0)
					z.getAI().setTarget(SESSION.getPlayer());
			}

		if (!this.skipComboAndScoreIncrement) {

			SESSION.getSessionData().sessionScore += this.getScoreValue() * SESSION.getComboTracker().getMultiplier();
			SESSION.getComboTracker().increment(Zombie.COMBO_INCREMENT);
			SESSION.getScoreTracker().updateScoreText();

			if (!this.isDummy) {
				AchievementsManager.incrementZombiesKilled();
				if (this.isAttacking)
					AchievementsManager.unlockAchievement(GooglePlayConstants.ACHIEVEMENT_6_ID);

				if (this.itemSpawnChance > 0)
					if (new Random().nextInt(this.itemSpawnChance) == 0)
						this.onSpawnItem();
			}

		} else
			this.skipComboAndScoreIncrement = false;
	}

	@Override
	public void onHit(Bullet b) {
		if (!this.isContextSessionScene())
			return;

		final Bullet BULLET = b;
		final SessionScene SESSION = ((SessionScene) this.getContext());

		EnvironmentVars.MAIN_CONTEXT.runOnUpdateThread(new Runnable() {

			@Override
			public void run() {
				Zombie.this.setHealthAmount(Zombie.this.getHealthAmount()
						- (SESSION.ARMORY_DATA.ARRAY_DAMAGE[BULLET.getSenderWeapon().getIndex()] + (int) (5 * new Random().nextFloat())));
				Zombie.this.drawBlood();

				if (BULLET.getSenderWeapon().getIndex() == Armory.WEP_SNIPER)
					if (StaticData.sniper_injectionTips)
						if (!Zombie.this.isDisoriented && Zombie.getAliveZombies().size() > 1)
							if (new Random().nextBoolean()) {
								Zombie.this.setTargetRandomZombie();
								Zombie.this.disorientTargetCount = 1 + new Random().nextInt(5);
								Zombie.this.isDisoriented = true;
							}
			}
		});
	}

	@Override
	protected void onMoveRequest() {
		if (this.isAttacking)
			return;

		super.onMoveRequest();
	}

	public void onSpawnItem() {

		if (!this.canDropItem() || !this.isContextSessionScene())
			return;

		final Random GENERATOR = new Random();
		final int WEAPON_COUNT = this.getContextAsSession().getPlayer().getWeaponCount();

		// Can spawn a health kit
		final boolean HEALTH_CRITICAL = HealthCollectible.COLLECTIBLE_COUNT <= 2
				&& (float) this.getContextAsSession().getPlayer().getHealthAmount()
						/ (float) this.getContextAsSession().getPlayer().getMaxHealthAmount() < 0.5f;

		final boolean SPAWN_AMMO = HEALTH_CRITICAL ? GENERATOR.nextBoolean() : true;

		final TiledTextureRegion ITEM_TEXTURE_REGION = HudRegions.region_col_map;
		final float X = this.getBoundaryX() + (this.getBoundaryWidth() - ITEM_TEXTURE_REGION.getWidth()) / 2f;
		final float Y = this.getBoundaryCenterY() - ITEM_TEXTURE_REGION.getHeight() / 2f;

		Collectible item = null;

		if (GENERATOR.nextInt(3) == 0)
			switch (GENERATOR.nextInt(2)) {
			case 0:
				if (!this.getContextAsSession().getPlayer().isMoveSpeedBoosted())
					item = new SpeedCollectible(X, Y, ITEM_TEXTURE_REGION, 2, 0, 0, this.getContextAsSession());
				break;
			case 1:
				item = new AmmoAllCollectible(X, Y, ITEM_TEXTURE_REGION, 3, 0, 0, this.getContextAsSession());
				break;
			}
		else if (SPAWN_AMMO) {
			if (WEAPON_COUNT > 1) {
				int wepIndex = 1;

				final ArrayList<Integer> EMPTY_CLIP_INDICIES = new ArrayList<Integer>();

				for (int i = 1; i < WEAPON_COUNT; i++)
					if (this.getContextAsSession().getPlayer().getAmmo(i) == 0)
						EMPTY_CLIP_INDICIES.add(i);

				if (EMPTY_CLIP_INDICIES.size() > 0)
					wepIndex = EMPTY_CLIP_INDICIES.get(GENERATOR.nextInt(EMPTY_CLIP_INDICIES.size()));
				else
					wepIndex = GENERATOR.nextInt(WEAPON_COUNT - 1) + 1;

				item = new AmmoCollectible(X, Y, ITEM_TEXTURE_REGION, 0, 0, 0, this.getContextAsSession(), wepIndex,
						this.getContextAsSession().getClipSize(wepIndex));
			}

		} else if (WEAPON_COUNT == 0 || HEALTH_CRITICAL)
			item = new HealthCollectible(X, Y, ITEM_TEXTURE_REGION, 1, 0, 0, this.getContextAsSession(), 35);

		if (item != null) {
			item.setZIndex((int) (item.getY() + item.getHeight()));
			this.getContext().attachChild(item);
			this.getContext().sortChildren();
		}
	}

	// *********************************
	//

	/**
	 * If this Zombie is marked as a dummy, it will not increase the session
	 * score nor progress any achievements.
	 * 
	 * @param dummy
	 */
	public void setDummy(boolean dummy) {
		this.isDummy = dummy;
	}

	public void setItemSpawnChance(int itemSpawnChance) {
		this.itemSpawnChance = itemSpawnChance;
	}

	public void setScoreValue(int score) {
		this.scoreValue = score;
	}

	public void setTargetRandomZombie() {

		final ArrayList<Zombie> POSSIBLE_TARGETS = new ArrayList<Zombie>();

		for (int i = 0; i < Zombie.getAliveZombies().size(); i++)
			if (Zombie.getAliveZombies().get(i) != this)
				POSSIBLE_TARGETS.add(Zombie.getAliveZombies().get(i));

		Zombie.this.getAI().setTarget(ListUtils.random(POSSIBLE_TARGETS));
	}
}
