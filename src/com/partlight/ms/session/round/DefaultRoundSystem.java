package com.partlight.ms.session.round;

import java.util.Random;

import org.andengine.opengl.texture.region.ITiledTextureRegion;

import com.partlight.ms.activity.GameActivity.GooglePlayConstants;
import com.partlight.ms.entity.session.notification.Notification.NotificationEffects;
import com.partlight.ms.resource.ResourceManager;
import com.partlight.ms.resource.ResourceManager.ZombieTextureRegions.Zombie01;
import com.partlight.ms.resource.ResourceManager.ZombieTextureRegions.Zombie02;
import com.partlight.ms.scene.session.SessionScene;
import com.partlight.ms.session.achievement.AchievementsManager;
import com.partlight.ms.session.character.Zombie;
import com.partlight.ms.session.character.skin.CharacterSkin;
import com.partlight.ms.session.character.skin.zombie.Zombie1CharacterSkin;
import com.partlight.ms.session.character.skin.zombie.Zombie2CharacterSkin;
import com.partlight.ms.session.hud.ComboTracker.NotificationConstants;

public class DefaultRoundSystem extends RoundSystem {

	public static final Integer[]	SPECIAL_SPAWN_COUNTS	= {
			0,
			0,
			0,
			0,
			0,
			1,
			1,
			1,
			1,
			1,
			2,
			2,
			2,
			2,
			3
																};
	public static final Integer[]	SPECIAL_SPAWN_HEALTH	= {
			0,
			0,
			0,
			0,
			60,
			60,
			65,
			65,
			80,
			90,
			90,
			90,
			90
																};
	public static final Integer[]	SPAWN_COUNTS			= {
			3,
			6,
			7,
			9,
			12,
			15,
			18,
			20,
			22,
			25,
			30,
			35,
			38,
			42,
			48,
			52,
			56,
			60,
			62,
																};
	public static final Integer[]	SPAWN_MIN_HEALTH		= {
			20,
			20,
			20,
			20,
			23,
			23,
			20,
			22,
			22,
			22,
			25,
			25,
			25,
			30
																};
	public static final Integer[]	SPAWN_MAX_HEALTH		= {
			25,
			25,
			25,
			25,
			30,
			30,
			30,
			30,
			30,
			30,
			35,
			35,
			35,
			40,
			40,
			40,
			40,
			40,
			45,
			45,
			45,
			50,
																};

	public static final Float[]	SPAWN_INTERVALS	= {
			3.0f,
			1.0f,
			1.0f,
			0.9f,
			0.9f,
			0.9f,
			0.75f,
			0.75f,
			0.75f,
			0.75f,
			0.7f,
			0.7f,
			0.7f
													};
	public static final Float[]	SPAWN_MIN_SPEED	= {
			0.2f,
			0.2f,
			0.2f,
			0.2f,
			0.2f,
			0.2f,
			0.35f,
			0.35f,
			0.4f,
			0.4f,
			0.45f,
			0.45f
													};
	public static final Float[]	SPAWN_MAX_SPEED	= {
			0.45f,
			0.45f,
			0.5f,
			0.5f,
			0.5f,
			0.5f,
			0.6f,
			0.6f,
			0.75f,
			0.75f,
			0.8f,
			0.8f,
			0.85f,
			0.85f,
			0.85f,
			0.9f,
			0.9f,
			1f,
			1f,
			1f,
			1.25f,
			1.25f,
			1.4f,
			1.4f,
			1.7f,
			1.7f,
			1.7f,
			1.7f,
			2f,
			2f,
			2f,
			2.25f,
			2.25f,
			2.25f,
			2.5f,
			2.5f,
			2.5f,
			2.8f,
													};

	private boolean		spawnSpecial;
	private int			spawnedSpecials;
	private int			spawnedRegular;
	private final int	startRound;

	public DefaultRoundSystem(SessionScene context) {
		this(context, 1);
	}

	public DefaultRoundSystem(SessionScene context, int startRound) {
		super(context);
		this.startRound = startRound;
	}

	@Override
	protected Zombie addNPC(float spawnX, float spawnY) {
		this.spawnSpecial = (Integer) this.fromArray(DefaultRoundSystem.SPECIAL_SPAWN_COUNTS) > 0
				&& this.spawnedSpecials < (Integer) this.fromArray(DefaultRoundSystem.SPECIAL_SPAWN_COUNTS) && new Random().nextInt(4) == 0;

		if (this.spawnSpecial) {
			if (this.spawnedSpecials == 0)
				this.getSpawnSkin(false).loadTextures();
			this.spawnedSpecials++;
		} else {
			if (this.spawnedRegular == 0)
				this.getSpawnSkin(false).loadTextures();
			this.spawnedRegular++;
		}

		return super.addNPC(spawnX, spawnY);
	}

	private Object fromArray(Object[] array) {
		if (this.getRoundIndex() < array.length)
			return array[this.getRoundIndex()];
		return array[array.length - 1];
	}

	@Override
	protected ITiledTextureRegion getSpawnArmsTextureRegion() {
		if (this.spawnSpecial)
			return Zombie02.region_a;
		else
			return Zombie01.region_a01;
	}

	@Override
	public int getSpawnCount() {
		return (Integer) this.fromArray(DefaultRoundSystem.SPAWN_COUNTS);
	}

	@Override
	public int getSpawnHealth() {
		int health = 0;

		if (this.spawnSpecial)
			health = (Integer) this.fromArray(DefaultRoundSystem.SPECIAL_SPAWN_HEALTH);
		else {
			final int min = (Integer) this.fromArray(DefaultRoundSystem.SPAWN_MIN_HEALTH);
			final int max = (Integer) this.fromArray(DefaultRoundSystem.SPAWN_MAX_HEALTH);
			health = min + (int) ((max - min) * new Random().nextFloat());
		}

		return health;
	}

	@Override
	public float getSpawnInterval() {
		return (Float) this.fromArray(DefaultRoundSystem.SPAWN_INTERVALS);
	}

	@Override
	public int getSpawnItemChance() {
		if (this.spawnSpecial)
			return 2;
		return 14;
	}

	@Override
	public int getSpawnScoreValue() {
		if (this.spawnSpecial)
			return 25;
		else
			return 15;
	}

	@Override
	public CharacterSkin getSpawnSkin(boolean walkAnimation) {
		if (this.spawnSpecial)
			return new Zombie2CharacterSkin(walkAnimation);
		return new Zombie1CharacterSkin(walkAnimation);
	}

	@Override
	public float getSpawnSpeed() {

		float speed;
		int minIndex;
		int maxIndex;

		if (this.getRoundIndex() < DefaultRoundSystem.SPAWN_MIN_SPEED.length)
			minIndex = this.getRoundIndex();
		else
			minIndex = DefaultRoundSystem.SPAWN_MIN_SPEED.length - 1;

		if (this.getRoundIndex() < DefaultRoundSystem.SPAWN_MAX_SPEED.length)
			maxIndex = this.getRoundIndex();
		else
			maxIndex = DefaultRoundSystem.SPAWN_MAX_SPEED.length - 1;

		speed = DefaultRoundSystem.SPAWN_MIN_SPEED[minIndex]
				+ (DefaultRoundSystem.SPAWN_MAX_SPEED[maxIndex] - DefaultRoundSystem.SPAWN_MIN_SPEED[minIndex]) * new Random().nextFloat();

		return speed;
	}

	@Override
	public float getSpawnX() {
		return 32 + (this.getContext().getLevel().getMapWidth() - 64) * new Random().nextFloat();
	}

	@Override
	public float getSpawnY() {
		if (new Random().nextBoolean())
			return this.getContext().getLevel().getMapHeight();
		else
			return -96f;
	}

	@Override
	public int getStartRound() {
		return this.startRound;
	}

	@Override
	protected void onNpcKilled(Zombie npc) {
		super.onNpcKilled(npc);
		if (Zombie.getAliveZombies().size() == 0) {
			this.spawnSpecial = true;
			this.getSpawnSkin(false).unloadTextures();
			this.spawnSpecial = false;
			this.getSpawnSkin(false).unloadTextures();
		}
	}

	protected void showWaveNotification() {
		this.getContext().getComboTracker().notify(String.format("// WAVE %d", this.getRoundIndex() + 1),
				NotificationConstants.NOTIFICATION_COLOR_WAVE, NotificationConstants.NOTIFICATION_SCALE * 1.5f,
				NotificationEffects.PULSATE);
		ResourceManager.sNotif0.play();
	}

	@Override
	protected void startRound() {
		super.startRound();

		this.spawnedSpecials = 0;
		this.spawnedRegular = 0;

		this.showWaveNotification();

		// Unlock achievement "Second Mate Warfare"
		if (this.getRoundIndex() == 11)
			AchievementsManager.unlockAchievement(GooglePlayConstants.ACHIEVEMENT_2_ID);
		else if (this.getRoundIndex() == 18)
			AchievementsManager.unlockAchievement(GooglePlayConstants.ACHIEVEMENT_3_ID);
		else if (this.getRoundIndex() == 27)
			AchievementsManager.unlockAchievement(GooglePlayConstants.ACHIEVEMENT_4_ID);
	}
}
