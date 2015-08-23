package com.partlight.ms.scene.session;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.andengine.audio.BaseAudioEntity;
import org.andengine.audio.music.Music;
import org.andengine.audio.sound.Sound;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.AnimatedSprite.IAnimationListener;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.VerticalAlign;
import org.andengine.util.color.Color;
import org.andengine.util.modifier.ease.EaseCubicInOut;
import org.andengine.util.modifier.ease.EaseLinear;
import org.andengine.util.modifier.ease.EaseSineInOut;

import com.partlight.ms.Direction;
import com.partlight.ms.entity.EntitySorter;
import com.partlight.ms.entity.LoadingSpriteFade;
import com.partlight.ms.entity.session.EnvironmentSoundTrigger;
import com.partlight.ms.resource.EnvironmentVars;
import com.partlight.ms.resource.EnvironmentVars.PreferenceKeys;
import com.partlight.ms.resource.EnvironmentVars.StaticData;
import com.partlight.ms.resource.ResourceManager;
import com.partlight.ms.resource.ResourceManager.HudRegions;
import com.partlight.ms.resource.ResourceManager.MiscRegions;
import com.partlight.ms.scene.DialogLevelScene;
import com.partlight.ms.scene.mainmenu.MainMenuScene;
import com.partlight.ms.session.SessionArmoryData;
import com.partlight.ms.session.SessionData;
import com.partlight.ms.session.achievement.AchievementsManager;
import com.partlight.ms.session.camera.CameraManager;
import com.partlight.ms.session.character.Armory;
import com.partlight.ms.session.character.Character;
import com.partlight.ms.session.character.CharacterOutOfViewArrow;
import com.partlight.ms.session.character.Zombie;
import com.partlight.ms.session.character.player.Player;
import com.partlight.ms.session.character.player.weapons.Firearm;
import com.partlight.ms.session.character.player.weapons.Grenade;
import com.partlight.ms.session.character.player.weapons.Grenade.OnExplodeListener;
import com.partlight.ms.session.character.player.weapons.Tossable;
import com.partlight.ms.session.environment.EnvironmentDeltaObject;
import com.partlight.ms.session.environment.EnvironmentObject;
import com.partlight.ms.session.environment.EnvironmentSpriteGroup;
import com.partlight.ms.session.gameover.SessionGameOver;
import com.partlight.ms.session.hazard.BulletTarget;
import com.partlight.ms.session.hud.BaseScreenComponent;
import com.partlight.ms.session.hud.BaseScreenComponentTouchManager;
import com.partlight.ms.session.hud.ComboTracker;
import com.partlight.ms.session.hud.ComboTracker.NotificationConstants;
import com.partlight.ms.session.hud.FireButton;
import com.partlight.ms.session.hud.Inventory;
import com.partlight.ms.session.hud.JoyStick;
import com.partlight.ms.session.hud.JoyStick.OnDirectionChangeListener;
import com.partlight.ms.session.hud.JoyStick.OnStickPositionChangeListener;
import com.partlight.ms.session.hud.RewardGuideStructure;
import com.partlight.ms.session.hud.ScoreTracker;
import com.partlight.ms.session.hud.listener.ComponentAdapter;
import com.partlight.ms.session.level.Level;
import com.partlight.ms.session.round.DefaultRoundSystem;
import com.partlight.ms.session.round.RoundSystem;
import com.partlight.ms.shader.LinearGradientShaderProgram;
import com.partlight.ms.util.EntityUtils;
import com.partlight.ms.util.Fade;
import com.partlight.ms.util.Ruler;
import com.partlight.ms.util.TextureManagedSprite;
import com.partlight.ms.util.boundary.Boundary;
import com.partlight.ms.util.list.TextureManagedArrayList;
import com.partlight.ms.util.listener.OnBackPressedListener;
import com.partlight.ms.util.updatehandler.FlashUpdateHandler;
import com.partlight.ms.util.updatehandler.FloatValueModifier;
import com.partlight.ms.util.updatehandler.FloatValueModifier.OnValueChangeListener;

import android.graphics.PointF;
import static com.partlight.ms.resource.EnvironmentVars.MAIN_CONTEXT;
/**
 * The <em>session</em> is the actual gameplay part.
 * 
 * @author Johan Svensson - partLight Entertainment
 * 
 */
public class SessionScene extends DialogLevelScene implements OnBackPressedListener, IOnSceneTouchListener {

	private static final HUD				HUD;
	private static final Music				GAME_OVER_MUSIC						= ResourceManager.mGameOver;
	private static final Music				NEW_ROUND_MUSIC						= ResourceManager.mNewWave;
	private static final Sound				EXPLOSION_SOUND						= ResourceManager.sExplosion;
	private static final float				AMBIENCE_VOLUME						= 0.75f;
	private static final float				TARGET_ALIVE_EXPLOSION_MARK_TIME	= 7f;
	protected static final BaseAudioEntity	AMBIENCE							= ResourceManager.mAmbWind;
	public static final String				GAME_OVER_TITLE						= "END OF SHOWDOWN";
	public static final boolean				USE_LASER_CROSSHAIR					= false;
	public static final float				JOYSTICK_MOVE_DEADZONE				= 24f;
	public static final int					MAX_EXPLOSION_MARKS					= 10;
	public static final int					MAX_EXPLOSION_SMOKE					= 30;
	public static final int					TAG_PRESERVE						= 850;

	static {
		HUD = EnvironmentVars.MAIN_CONTEXT.getCamera().getHUD();
	}

	private ArrayList<EnvironmentObject>		alBlood;
	private ArrayList<EnvironmentObject>		alCollectibleSmoke;
	private ArrayList<EnvironmentObject>		alSmoke;
	private CameraManager						cmCameraManager;
	private CharacterOutOfViewArrow				characterViewArrow;
	protected ComboTracker						ctComboTracker;
	private EntitySorter						eysEntityYSorter;
	private EnvironmentSoundTrigger				estEnvSoundTrigger;
	private EnvironmentSpriteGroup				esgBlood;
	private EnvironmentSpriteGroup				esgCollectibleSmoke;
	private EnvironmentSpriteGroup				esgSmoke;
	private Fade								fFade;
	protected FireButton						fbFireButton;
	protected Inventory							iInventory;
	protected JoyStick							jsJoyStick;
	private Level								lLevel;
	protected Player							pPlayer;
	private PointF[][]							laserSightLocations;
	private RewardGuideStructure				rgsRewardGuide;
	protected RoundSystem						rsRoundSystem;
	private ScoreTracker						hstScoreTracker;
	private SessionData							sdSessionData;
	private Sprite								sFirstWave;
	private Sprite								sFirstWaveSub;
	private Sprite								sLaserCrosshair;
	private Sprite								sLaserSight;
	private final LinearGradientShaderProgram	LASER_SIGHT_SHADER;
	private final List<Boundary>				lObstacles;
	private final List<BulletTarget>			BULLET_TARGETS;
	private final int[]							wepClipSizes;
	private int									explosionCount;
	protected BaseScreenComponentTouchManager	hudTouchManager;
	public final SessionArmoryData				ARMORY_DATA;

	public SessionScene(Level level) {

		this.ARMORY_DATA = new SessionArmoryData();
		this.LASER_SIGHT_SHADER = new LinearGradientShaderProgram(Color.RED, Color.TRANSPARENT,
				LinearGradientShaderProgram.DIRECTION_LEFT_TO_RIGHT);
		this.readSessionArmoryData();
		this.BULLET_TARGETS = new ArrayList<BulletTarget>();
		this.lObstacles = new ArrayList<Boundary>();
		this.wepClipSizes = new int[Armory.WEP_ARRAY.length];
		this.initLevel(level);

		this.setBackground(new Background(Color.BLACK));
	}

	public void addBulletTarget(BulletTarget target) {
		this.BULLET_TARGETS.add(target);
	}

	protected void alignFireButton() {
		EntityUtils.alignEntity(this.fbFireButton, this.fbFireButton.getWidth(), this.fbFireButton.getHeight(), HorizontalAlign.RIGHT,
				VerticalAlign.BOTTOM, 32, 24);
	}

	protected void alignInventory() {
		EntityUtils.alignEntity(this.iInventory, this.iInventory.getWidth(), this.iInventory.getHeight(), HorizontalAlign.RIGHT,
				VerticalAlign.TOP, 24, 32);
	}

	protected void alignJoyStick() {
		EntityUtils.alignEntity(this.jsJoyStick, this.jsJoyStick.getWidth(), this.jsJoyStick.getHeight(), HorizontalAlign.LEFT,
				VerticalAlign.BOTTOM, 32, 24);
	}

	protected boolean assertPlayerCanShoot() {
		return true;
	}

	protected void attachHudComponents() {
		SessionScene.HUD.attachChild(this.characterViewArrow);
		SessionScene.HUD.attachChild(this.jsJoyStick);
		SessionScene.HUD.attachChild(this.iInventory);
		SessionScene.HUD.attachChild(this.fbFireButton);
		SessionScene.HUD.attachChild(this.hstScoreTracker);
		SessionScene.HUD.attachChild(this.ctComboTracker);
		SessionScene.HUD.attachChild(this.fFade);

		this.alignJoyStick();
		this.alignFireButton();
		this.alignInventory();

		SessionScene.HUD.sortChildren();
	}

	@SuppressWarnings("unused")
	private void clampLaserCrosshair() {
		if (this.sLaserCrosshair == null || !SessionScene.USE_LASER_CROSSHAIR)
			return;

		final float LASER_CROSSHAIR_X = this.getLaserCrosshairX();
		final float LASER_CROSSHAIR_Y = this.getLaserCrosshairY();

		this.sLaserCrosshair.setPosition(LASER_CROSSHAIR_X, LASER_CROSSHAIR_Y);

		if (false) {
			if (this.pPlayer.getX() + this.sLaserCrosshair.getX() * this.pPlayer.getScaleX() < EnvironmentVars.MAIN_CONTEXT.getCamera()
					.getXMin())
				this.sLaserCrosshair.setX(LASER_CROSSHAIR_X - (this.pPlayer.getX() + LASER_CROSSHAIR_X * this.pPlayer.getScaleX()
						- EnvironmentVars.MAIN_CONTEXT.getCamera().getXMin()) / this.pPlayer.getScaleX());

			if (this.pPlayer.getX() + this.sLaserCrosshair.getX() * this.pPlayer.getScaleX() > this.lLevel.getMapWidth()
					- EnvironmentVars.MAIN_CONTEXT.getCamera().getXMin() - this.sLaserCrosshair.getWidthScaled())
				this.sLaserCrosshair.setX(LASER_CROSSHAIR_X
						- (this.pPlayer.getX() + LASER_CROSSHAIR_X * this.pPlayer.getScaleX()) / this.pPlayer.getScaleX());

			if (this.pPlayer.getY() + this.sLaserCrosshair.getY() * this.pPlayer.getScaleY() < EnvironmentVars.MAIN_CONTEXT.getCamera()
					.getYMin())
				this.sLaserCrosshair.setY(LASER_CROSSHAIR_Y - (this.pPlayer.getY() + LASER_CROSSHAIR_Y * this.pPlayer.getScaleY()
						- EnvironmentVars.MAIN_CONTEXT.getCamera().getYMin()) / this.pPlayer.getScaleY());
		}
	}

	protected final void clearPlayerControl() {
		this.cmCameraManager.unregisterUpdateHandler();
		this.getJoyStick().setOnDirectionChange(null);
		this.getJoyStick().setOnStickPositionChange(null);
	}

	public void createExplosion(float x, float y, float radius, float damage) {
		this.createExplosion(x, y, radius, damage, false);
	}

	public void createExplosion(float x, float y, float radius, float damage, boolean harmPlayer) {

		final float X = x, Y = y;

		final HashMap<Character, Float> CHARACTERS_IN_RANGE = new HashMap<Character, Float>();

		for (final Zombie z : Zombie.getAliveZombies())
			this.onExplosionCheckCharacterCollision(x, y, radius, z, CHARACTERS_IN_RANGE);

		if (harmPlayer)
			this.onExplosionCheckCharacterCollision(x, y, radius, this.pPlayer, CHARACTERS_IN_RANGE);

		final Iterator<Character> CHARACTER_ITERATOR = CHARACTERS_IN_RANGE.keySet().iterator();
		final Iterator<Float> DAMAGE_ITERATOR = CHARACTERS_IN_RANGE.values().iterator();

		while (CHARACTER_ITERATOR.hasNext()) {

			final Character CHAR = CHARACTER_ITERATOR.next();
			final Float DAMAGE = DAMAGE_ITERATOR.next();

			CHAR.setHealthAmount((int) (CHAR.getHealthAmount() - damage * DAMAGE));

			if (CHAR.getHealthAmount() <= 0)
				AchievementsManager.incrementZombiesKilledByExplosion();
		}

		final ITiledTextureRegion EXPLOSION_TEXTURE = HudRegions.region_explosion;
		final AnimatedSprite EXPLOSION = new AnimatedSprite(x, y, EXPLOSION_TEXTURE,
				EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager());

		if (this.explosionCount == 0)
			EXPLOSION_TEXTURE.getTexture().load();

		EXPLOSION.setScaleCenter(0, 0);
		EXPLOSION.setScale(2f);
		EXPLOSION.setPosition(EXPLOSION.getX() - EXPLOSION.getWidthScaled() / 2f, EXPLOSION.getY() - EXPLOSION.getHeightScaled() / 2f);
		EXPLOSION.setZIndex(this.getLevel().getMapHeight());
		this.attachChild(EXPLOSION);
		this.explosionCount++;

		EXPLOSION.animate(30L, false, new IAnimationListener() {
			private boolean usedEffects;

			@Override
			public void onAnimationFinished(AnimatedSprite pAnimatedSprite) {
				EnvironmentVars.MAIN_CONTEXT.runOnUpdateThread(new Runnable() {
					@Override
					public void run() {
						SessionScene.this.explosionCount--;
						if (SessionScene.this.explosionCount == 0)
							EXPLOSION_TEXTURE.getTexture().unload();
						EXPLOSION.detachSelf();
						EXPLOSION.dispose();
					}
				});
			}

			@Override
			public void onAnimationFrameChanged(AnimatedSprite pAnimatedSprite, int pOldFrameIndex, int pNewFrameIndex) {

				if (pNewFrameIndex >= 3)
					if (!this.usedEffects) {
						SessionScene.this.createExplosionMark(X, Y);
						SessionScene.this.onExplosionCreateSmoke(X, Y);
						this.usedEffects = true;
					}
			}

			@Override
			public void onAnimationLoopFinished(AnimatedSprite pAnimatedSprite, int pRemainingLoopCount, int pInitialLoopCount) {
			}

			@Override
			public void onAnimationStarted(AnimatedSprite pAnimatedSprite, int pInitialLoopCount) {
			}
		});

		this.getCameraManager().shakeRandomDirection();

		SessionScene.EXPLOSION_SOUND.play();
	}

	protected void createExplosionMark(float x, float y) {

		final Sprite MARK = new TextureManagedSprite(0, 0, HudRegions.region_explosion_mark,
				EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager()) {
			private float elapsedAliveTime;

			@Override
			protected void onManagedUpdate(float pSecondsElapsed) {
				this.elapsedAliveTime += pSecondsElapsed;

				if (this.elapsedAliveTime >= SessionScene.TARGET_ALIVE_EXPLOSION_MARK_TIME) {
					this.setAlpha(this.getAlpha() - 0.002f);
					if ((int) (this.getAlpha() * 1000) <= 0)
						EntityUtils.safetlyDetachAndDispose(this);
				}

				super.onManagedUpdate(pSecondsElapsed);
			}
		};
		MARK.setScale(2f);
		MARK.setX(x - MARK.getWidth() / 2f);
		MARK.setY(y - MARK.getHeight() / 2f);

		this.attachChild(MARK);
	}

	public void createSmoke(float x, float y) {

		final Random RANDOM = new Random();

		final EnvironmentDeltaObject SMOKE = new EnvironmentDeltaObject(x, y, 1,
				HudRegions.region_smoke.getTextureRegion(RANDOM.nextInt(HudRegions.region_smoke.getTileCount())));
		SMOKE.delta = new PointF(0.05f + RANDOM.nextFloat() * 0.05f, -(0.05f + RANDOM.nextFloat() * 0.05f));
		SMOKE.ticksUntilFadeOut = 0;
		SMOKE.fadeOut = true;
		SMOKE.fadeOutFactor = 0.005;
		this.alSmoke.add(SMOKE);
		this.esgSmoke.updateDrawing();
	}

	protected void detachAndDisposeEntities(boolean onlyPreserved) {
		IEntity index;

		final int CHILD_COUNT = this.getChildCount();
		final int HUD_CHILD_COUNT = SessionScene.HUD.getChildCount();

		for (int i = 0; i < CHILD_COUNT; i++) {
			index = this.getChildByIndex(0);
			if (onlyPreserved ? index.getTag() != SessionScene.TAG_PRESERVE : true) {
				index.detachSelf();
				if (!index.isDisposed())
					index.dispose();
			}
		}

		for (int i = 0; i < HUD_CHILD_COUNT; i++) {
			index = SessionScene.HUD.getChildByIndex(0);
			if (onlyPreserved ? index.getTag() != SessionScene.TAG_PRESERVE : true) {
				index.detachSelf();
				if (!index.isDisposed())
					index.dispose();
			}
		}
	}

	/**
	 * Detaches and disposes all Entities that does not have the tag
	 * <em>TAG_PRESERVE</em> ({@value #TAG_PRESERVE}) in the HUD and in this
	 * scene.
	 */
	public void detachAndDisposeNonPreservedEntities() {
		EnvironmentVars.MAIN_CONTEXT.runOnUpdateThread(new Runnable() {
			@Override
			public void run() {
				SessionScene.this.detachAndDisposeEntities(true);
			}
		});
	}

	protected void detachHudEntities() {
		SessionScene.HUD.detachChild(this.characterViewArrow);
		SessionScene.HUD.detachChild(this.jsJoyStick);
		SessionScene.HUD.detachChild(this.iInventory);
		SessionScene.HUD.detachChild(this.fbFireButton);
		SessionScene.HUD.detachChild(this.hstScoreTracker);
		SessionScene.HUD.detachChild(this.ctComboTracker);
		SessionScene.HUD.detachChild(this.fFade);
	}

	@Override
	public void dispose() {
		this.detachHudEntities();
		super.dispose();
	}

	public EnvironmentSpriteGroup getBlood() {
		return this.esgBlood;
	}

	public BulletTarget[] getBulletTargets() {
		return this.BULLET_TARGETS.toArray(new BulletTarget[this.BULLET_TARGETS.size()]);
	}

	public CameraManager getCameraManager() {
		return this.cmCameraManager;
	}

	public int getClipSize(int wepIndex) {
		return this.wepClipSizes[wepIndex];
	}

	public EnvironmentSpriteGroup getCollectibleSmoke() {
		return this.esgCollectibleSmoke;
	}

	public ComboTracker getComboTracker() {
		return this.ctComboTracker;
	}

	@Override
	public EntitySorter getEntityYSorter() {
		return this.eysEntityYSorter;
	}

	public Fade getFade() {
		return this.fFade;
	}

	public FireButton getFireButton() {
		return this.fbFireButton;
	}

	public BaseScreenComponentTouchManager getHudTouchManager() {
		return this.hudTouchManager;
	}

	public Inventory getInventoryButton() {
		return this.iInventory;
	}

	public JoyStick getJoyStick() {
		return this.jsJoyStick;
	}

	// *********************************
	//

	// GAME METHODS
	// *********************************

	public float getLaserCrosshairX() {
		float x = this.pPlayer.getX();

		x += this.sLaserSight.getX() * this.pPlayer.getScaleX();

		x += (float) (Math.cos(Math.toRadians(this.sLaserSight.getRotation()))
				* this.ARMORY_DATA.ARRAY_RANGE[this.pPlayer.getWeaponIndex()]);
		x -= this.sLaserCrosshair.getWidthScaled() / 2f;

		return x;
	}

	public float getLaserCrosshairY() {
		float y = this.pPlayer.getY();

		y += this.sLaserSight.getY() * this.pPlayer.getScaleY();

		y += (float) (Math.sin(Math.toRadians(this.sLaserSight.getRotation()))
				* this.ARMORY_DATA.ARRAY_RANGE[this.pPlayer.getWeaponIndex()]);
		y -= this.sLaserCrosshair.getHeightScaled() / 2f;

		return y;
	}

	@Override
	public Level getLevel() {
		return this.lLevel;
	}

	public List<Boundary> getObstacles() {
		return this.lObstacles;
	}

	// *********************************
	//

	// EVENT METHODS
	// *********************************

	public Player getPlayer() {
		return this.pPlayer;
	}

	public float getPlayerSpawnX() {
		return (this.getLevel().getMapWidth() - this.pPlayer.getSkin().getTorso().getWidthScaled()) / 2f;
	}

	public float getPlayerSpawnY() {
		return (this.getLevel().getMapHeight() - this.pPlayer.getSkin().getTorso().getHeightScaled()) / 2f;
	}

	public RewardGuideStructure getRewardGuide() {
		return this.rgsRewardGuide;
	}

	public RoundSystem getRoundSystem() {
		return this.rsRoundSystem;
	}

	public ScoreTracker getScoreTracker() {
		return this.hstScoreTracker;
	}

	public SessionData getSessionData() {
		return this.sdSessionData;
	}

	public PointF getWeaponTipPosition() {
		final PointF LOCATION = this.laserSightLocations[this.pPlayer.getWeapon().getIndex()][this.pPlayer.getSkin().getTorso()
				.getCurrentTileIndex()];
		final PointF out = new PointF(LOCATION.x, LOCATION.y);

		if (this.pPlayer.getSkin().getArms().isFlippedHorizontal())
			out.x = 32f - LOCATION.x;

		return out;
	}

	public void init() {

		this.sdSessionData = new SessionData();

		this.alBlood = new TextureManagedArrayList<EnvironmentObject>(ResourceManager.btBlood);
		this.alCollectibleSmoke = new TextureManagedArrayList<EnvironmentObject>(ResourceManager.btSmokeSmall);
		this.alSmoke = new TextureManagedArrayList<EnvironmentObject>(ResourceManager.btSmoke);

		this.initEnvSmokeSpriteBatch();

		this.esgBlood = new EnvironmentSpriteGroup(ResourceManager.btBlood, 30);
		this.esgBlood.setList(this.alBlood);

		this.esgCollectibleSmoke = new EnvironmentSpriteGroup(ResourceManager.btSmokeSmall, 30);
		this.esgCollectibleSmoke.setList(this.alCollectibleSmoke);

		this.registerUpdateHandler(this.esgBlood);
		this.registerUpdateHandler(this.esgCollectibleSmoke);
		this.registerUpdateHandler(this.esgSmoke);

		this.attachChild(this.esgBlood.getSpriteGroup());
		this.attachChild(this.esgCollectibleSmoke.getSpriteGroup());
		this.attachChild(this.esgSmoke.getSpriteGroup());

		this.initEntityYSorter();
		this.initCameraManager();
		this.initHud();
		this.initFade();
		this.initPlayer();
		this.characterViewArrow.setCharacter(this.getPlayer());
		this.attachHudComponents();
		this.rsRoundSystem = this.initRoundSystem();
		this.getCameraManager().registerUpdateHandler();
		this.setStandardClipSizes();

		for (final Firearm wep : Armory.WEP_ARRAY)
			try {
				final Tossable TOSSABLE = (Tossable) wep;
				TOSSABLE.getAmmoManager().register(this);
			} catch (final Exception ex) {

			}

		((Grenade) Armory.WEP_ARRAY[Armory.WEP_GRENADE]).setOnExplodeListener(new OnExplodeListener() {
			@Override
			public void onExplode(float x, float y) {
				SessionScene.this.onGrenadeExplosion(x, y);
			}
		});
		// @formatter:on

		SessionScene.HUD.setOnSceneTouchListener(this);

		this.estEnvSoundTrigger = new EnvironmentSoundTrigger();
		this.attachChild(this.estEnvSoundTrigger);
	}

	protected void initCameraManager() {
		this.cmCameraManager = new CameraManager(EnvironmentVars.MAIN_CONTEXT.getCamera(), this.lLevel, this);
		this.cmCameraManager.setShakeForce(4f);
	}

	protected void initComboTracker() {
		this.ctComboTracker = new ComboTracker(0, 0, EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager(), this);
		this.ctComboTracker.setComponentListener(new ComponentAdapter() {
			@Override
			public void onComponentReleased(BaseScreenComponent component, float x, float y) {
				SessionScene.this.getRewardGuide().show();
			}
		});
	}

	protected void initEntityYSorter() {
		this.eysEntityYSorter = new EntitySorter(this);
	}

	private void initEnvSmokeSpriteBatch() {
		this.esgSmoke = new EnvironmentSpriteGroup(ResourceManager.btSmoke, SessionScene.MAX_EXPLOSION_SMOKE);
		this.esgSmoke.setList(this.alSmoke);
		this.esgSmoke.getSpriteGroup().setZIndex(this.getLevel().getMapHeight() + 1);
	}

	protected void initFade() {
		this.fFade = new LoadingSpriteFade(EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager()) {
			@Override
			protected void onFadeOut() {
				super.onFadeOut();
				EntityUtils.safetlyDetach(this);
			}
		};
		this.fFade.setAlpha(1f);
	}

	protected void initFireButton() {
		this.fbFireButton = new FireButton(0, 0, HudRegions.region_fire);
		this.fbFireButton.setScale(2);
	}

	protected void initHud() {

		this.characterViewArrow = new CharacterOutOfViewArrow(HudRegions.region_bounds_indicator);

		this.initInventoryButton();
		this.initFireButton();
		this.initComboTracker();
		this.initJoyStick();
		this.initHudRewardGuide();
		this.initScoreTracker();
		this.initHudTouchManager();
	}

	protected void initHudRewardGuide() {
		this.rgsRewardGuide = new RewardGuideStructure(this, EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager());
	}

	protected void initHudTouchManager() {
		this.hudTouchManager = new BaseScreenComponentTouchManager(this.jsJoyStick, this.fbFireButton, this.iInventory,
				this.ctComboTracker);
	}

	protected void initInventoryButton() {
		this.iInventory = new Inventory(HudRegions.region_weps, HudRegions.region_wepframe, this) {
			@Override
			public void onSwitchWeapon(boolean nextWeapon) {
				super.onSwitchWeapon(nextWeapon);
				SessionScene.this.onInventoryWeaponSwitch(nextWeapon);
			}
		};
		this.iInventory.setAmmo(Firearm.STRING_INFINITE);
		this.iInventory.setScale(2);
	}

	protected void initJoyStick() {
		this.jsJoyStick = new JoyStick(0, 0, HudRegions.region_js_background, HudRegions.region_js_foreground) {
			@Override
			public void onTouchUp(float x, float y) {
				super.onTouchUp(x, y);
				SessionScene.this.onJoyStickTouchUp(x, y);
			}
		};
		this.jsJoyStick.setOnStickPositionChange(new OnStickPositionChangeListener() {

			@Override
			public void onStickPositionChange(float distance) {

				if (SessionScene.this.pPlayer.getHealthAmount() <= 0)
					return;

				if (distance < SessionScene.JOYSTICK_MOVE_DEADZONE) {
					if (SessionScene.this.pPlayer.isMoving()) {
						SessionScene.this.pPlayer.stopMoving();
						SessionScene.this.pPlayer.setMoveDirection(SessionScene.this.pPlayer.getMoveDirection());
					}
				} else if (!SessionScene.this.pPlayer.isMoving()) {
					SessionScene.this.pPlayer.startMoving();
					SessionScene.this.pPlayer.setMoveDirection(SessionScene.this.jsJoyStick.getDirection());
				}

			}
		});
		this.jsJoyStick.setOnDirectionChange(new OnDirectionChangeListener() {

			@Override
			public void onDirectionChange(Direction newDirection, Direction oldDirection) {

				if (SessionScene.this.pPlayer.getHealthAmount() <= 0)
					return;

				SessionScene.this.pPlayer.setMoveDirection(newDirection);
				SessionScene.this.cmCameraManager.updateChase();
				SessionScene.this.transformLaserSight();
			}
		});
		this.jsJoyStick.setScale(2);
	}

	protected void initLevel(Level level) {
		this.lLevel = level;

		level.getMainLayer().setScaleCenter(0, 0);
		level.getMainLayer().setScale(2f);

		this.attachChild(level.getMainLayer());
	}

	protected void initPlayer() {

		this.pPlayer = new Player(0, 0, this) {
			@Override
			protected boolean assertCanShoot() {
				return SessionScene.this.assertPlayerCanShoot();
			}

			@Override
			public void giveWeapon(int wep) {
				super.giveWeapon(wep);

				if (wep != Armory.WEP_PISTOL && new Random().nextInt(3) == 0)
					SessionScene.this.sdSessionData.sessionParts++;
			}

			@Override
			public void setAlpha(float pAlpha) {
				super.setAlpha(pAlpha);

				if (SessionScene.this.sLaserSight != null) {
					SessionScene.this.sLaserSight.setAlpha(pAlpha);
					if (SessionScene.USE_LASER_CROSSHAIR)
						SessionScene.this.sLaserCrosshair.setAlpha(pAlpha);
				}
			}

			@Override
			public void setX(float pX) {
				super.setX(pX);
				SessionScene.this.clampLaserCrosshair();
			}

			@Override
			public void setY(float pY) {
				super.setY(pY);
				SessionScene.this.clampLaserCrosshair();
			}
		};

		this.pPlayer.setX(this.getPlayerSpawnX());
		this.pPlayer.setY(this.getPlayerSpawnY());
		this.pPlayer.setMoveSpeed(3f);
		this.pPlayer.giveWeapon(Armory.WEP_PISTOL);
		this.pPlayer.setWeapon(Armory.WEP_PISTOL);
		this.pPlayer.setAmmo(Armory.WEP_PISTOL, -1);
		this.attachChild(this.pPlayer);

		//@formatter:off
		this.getCameraManager().followEntity(this.pPlayer,
				(this.pPlayer.getSkin().getTorso().getWidth() * this.pPlayer.getScaleX()) / 2f,
				(this.pPlayer.getSkin().getTorso().getHeight() * this.pPlayer.getScaleY()) / 2f);
		//@formatter:on

		this.laserSightLocations = new PointF[Armory.WEP_ARRAY.length][5];
		this.laserSightLocations[Armory.WEP_PISTOL][0] = new PointF(16f, 9f);
		this.laserSightLocations[Armory.WEP_PISTOL][1] = new PointF(23f, 10f);
		this.laserSightLocations[Armory.WEP_PISTOL][2] = new PointF(29f, 12f);
		this.laserSightLocations[Armory.WEP_PISTOL][3] = new PointF(23f, 20f);
		this.laserSightLocations[Armory.WEP_PISTOL][4] = new PointF(17f, 24f);
		this.laserSightLocations[Armory.WEP_SMG][0] = new PointF(16f, 8f);
		this.laserSightLocations[Armory.WEP_SMG][1] = new PointF(23f, 9f);
		this.laserSightLocations[Armory.WEP_SMG][2] = new PointF(28f, 13f);
		this.laserSightLocations[Armory.WEP_SMG][3] = new PointF(24f, 20f);
		this.laserSightLocations[Armory.WEP_SMG][4] = new PointF(15f, 24f);
		this.laserSightLocations[Armory.WEP_DOMINADOR] = this.laserSightLocations[Armory.WEP_PISTOL].clone();
		this.laserSightLocations[Armory.WEP_SNIPER][0] = new PointF(16f, 6f);
		this.laserSightLocations[Armory.WEP_SNIPER][1] = new PointF(23f, 8f);
		this.laserSightLocations[Armory.WEP_SNIPER][2] = new PointF(30f, 12f);
		this.laserSightLocations[Armory.WEP_SNIPER][3] = new PointF(24f, 21f);
		this.laserSightLocations[Armory.WEP_SNIPER][4] = new PointF(13f, 23f);
		this.laserSightLocations[Armory.WEP_SHOTGUN][0] = new PointF(16f, 6f);
		this.laserSightLocations[Armory.WEP_SHOTGUN][1] = new PointF(25f, 8f);
		this.laserSightLocations[Armory.WEP_SHOTGUN][2] = new PointF(29f, 13f);
		this.laserSightLocations[Armory.WEP_SHOTGUN][3] = new PointF(24f, 22f);
		this.laserSightLocations[Armory.WEP_SHOTGUN][4] = new PointF(14f, 23f);

		this.onCheckIsLaserSightEnabled();
	}

	protected RoundSystem initRoundSystem() {
		return new DefaultRoundSystem(this) {

			@Override
			protected void showWaveNotification() {
				if ((this.getStartRound() == 1) ? this.getRoundIndex() == 0 : false)
					return;

				EnvironmentVars.MAIN_CONTEXT.registerSound(SessionScene.NEW_ROUND_MUSIC);
				SessionScene.NEW_ROUND_MUSIC.seekTo(0);
				SessionScene.NEW_ROUND_MUSIC.play();

				super.showWaveNotification();
			}
		};
	}

	protected void initScoreTracker() {
		this.hstScoreTracker = new ScoreTracker(this);
		this.hstScoreTracker.setScorePosition(this.ctComboTracker.getBoundaryX(),
				this.ctComboTracker.getBoundaryHeight() + this.ctComboTracker.getBoundaryY() + 8f);
		this.hstScoreTracker.updateScoreText();
	}

	/**
	 * Kills all alive Zombies without incrementing either the score or the
	 * combo.
	 */
	public final void killAllZombies() {
		final int ALIVE_ZOMBIE_COUNT = Zombie.getAliveZombies().size();

		for (int i = 0; i < ALIVE_ZOMBIE_COUNT; i++)
			try {
				Zombie.getAliveZombies().get(i).dieWithoutScoreAndComboIncrement();
			} catch (final Exception ex) {
			}
	}

	protected void loadTextures() {
		ResourceManager.btAmmoIcon.load();
		ResourceManager.btComboBack.load();
		ResourceManager.btComboCanvas.load();
		ResourceManager.btComboFore.load();
		ResourceManager.btJoyStickBack.load();
		ResourceManager.btJoyStickFore.load();
		ResourceManager.btShootButton.load();
		ResourceManager.btSpeedBoostIcon.load();
		ResourceManager.btWepFrame.load();
		ResourceManager.btWeps.load();
		ResourceManager.btCharSkin.load();
		ResourceManager.btCharLb.load();
		ResourceManager.btCharA01.load();
	}

	@Override
	public void onBackPressed() {

		try {
			if (this.getFade().isShowing())
				this.getRewardGuide().hide();
		} catch (final Exception ex) {

		}
	}

	private void onCheckIsLaserSightEnabled() {

		if (StaticData.laserSightItems[this.pPlayer.getWeaponIndex()]) {
			if (this.sLaserSight == null) {
				this.sLaserSight = new Sprite(0f, 0f, MiscRegions.region_empty,
						EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager());
				this.sLaserSight.setShaderProgram(this.LASER_SIGHT_SHADER);
				this.sLaserSight.setZIndex(-1);
				this.sLaserSight.setScaleCenter(0, 0);
				this.sLaserSight.setScale(16f, 0.5f);
				this.sLaserSight.setRotationCenter(0f, this.sLaserSight.getHeightScaled() / 2f);
				this.resetLaserSightShader();

				if (SessionScene.USE_LASER_CROSSHAIR) {
					this.sLaserCrosshair = new Sprite(0f, 0f, HudRegions.region_laser_crosshair,
							EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager());
					this.sLaserCrosshair.setZIndex(this.lLevel.getMapHeight());
					this.sLaserCrosshair.setScaleCenter(0, 0);
					this.sLaserCrosshair.setScale(2f);
				}
			}

			this.transformLaserSight();

			try {
				if (SessionScene.USE_LASER_CROSSHAIR) {
					ResourceManager.btLaserCrosshair.load();
					this.attachChild(this.sLaserCrosshair);
				}
				this.pPlayer.attachChild(this.sLaserSight);
				this.pPlayer.sortChildren();
			} catch (final IllegalStateException ex) {

			}
		} else {

			if (this.sLaserSight == null)
				return;

			if (SessionScene.USE_LASER_CROSSHAIR) {
				ResourceManager.btLaserCrosshair.unload();
				this.sLaserCrosshair.detachSelf();
			}

			this.sLaserSight.detachSelf();
		}
	}

	@Override
	protected void onDialogAccept() {
		super.onDialogAccept();

		// User chose to sign in
		EnvironmentVars.MAIN_CONTEXT.getGoogleApiClient().connect();
	}

	@Override
	public void onEngineDrawError() {
	}

	@Override
	public void onEngineUpdateError() {
	}

	protected void onExplosionCheckCharacterCollision(float x, float y, float explosionRadius, Character character,
			HashMap<Character, Float> map) {
		final float DISTANCE = Ruler.getDistance(x, y, character.getBoundaryCenterX(), character.getBoundaryCenterY());

		if (DISTANCE <= explosionRadius)
			map.put(character, (1f - DISTANCE / explosionRadius));
	}

	protected void onExplosionCreateSmoke(float x, float y) {

		final float DISTANCE = 32f;

		for (int i = 0; i < 360; i += 360 / 10) {

			final float RADIANS = (float) Math.toRadians(i);

			this.createSmoke(x + (float) Math.cos(RADIANS) * DISTANCE, y + (float) Math.sin(RADIANS) * DISTANCE);
		}
	}

	protected void onGrenadeExplosion(float x, float y) {
		this.createExplosion(x, y, 192f, this.ARMORY_DATA.ARRAY_DAMAGE[Armory.WEP_GRENADE], true);
	}

	protected void onInventoryWeaponSwitch(boolean next) {
		this.onCheckIsLaserSightEnabled();
	}

	protected void onJoyStickTouchUp(float x, float y) {

	}

	@Override
	protected void onManagedUpdate(float pSecondsElapsed) {
		super.onManagedUpdate(pSecondsElapsed);

		if (this.eysEntityYSorter != null)
			this.eysEntityYSorter.onUpdate(pSecondsElapsed);

		AchievementsManager.onUpdate(pSecondsElapsed);
	}

	/**
	 * Occurs when a new multiplier in the attached {@link#HudComboTracker} has
	 * been reached.
	 * 
	 * @param multiplier
	 */
	public void onNewMultiplier(int multiplier) {
		if (this.pPlayer != null)
			this.pPlayer.onNewMultiplier(multiplier);
	}

	/**
	 * Occurs when the attached {@link#Player} has called
	 * {@link#Character.onHealthDepleted()}.
	 */
	public void onPlayerDead() {

		this.clearPlayerControl();

		for (int i = 0; i < this.getChildCount(); i++)
			this.getChildByIndex(i).setIgnoreUpdate(true);

		SessionScene.HUD.setOnSceneTouchListener(null);

		this.rsRoundSystem.setSuspendSpawn(true);
		this.rsRoundSystem.finish();

		if (this.sdSessionData.sessionScore > 12750)
			for (int i = 0; i < (int) (this.sdSessionData.sessionScore / 12750); i++)
				if (new Random().nextFloat() >= 0.33f)
					this.sdSessionData.sessionParts++;

		EnvironmentVars.PREFERENCES_EDITOR
				.putInt(PreferenceKeys.KEY_SCRAP_PARTS_AMOUNT,
						EnvironmentVars.PREFERENCES.getInt(PreferenceKeys.KEY_SCRAP_PARTS_AMOUNT, 0) + this.sdSessionData.sessionParts)
				.commit();

		StaticData.scrapPartsAmount += this.sdSessionData.sessionParts;

		final SessionGameOver GAME_OVER_SCENE = new SessionGameOver(this);
		GAME_OVER_SCENE.start();
	}

	@Override
	public void onResume() {
		super.onResume();
		this.resetLaserSightShader();
	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		this.hudTouchManager.onSceneTouchEvent(pScene, pSceneTouchEvent);
		return super.onSceneTouchEvent(pSceneTouchEvent);
	}

	/**
	 * Occurs also when the blur effect has faded in.
	 */
	protected void onSessionFinish() {

		this.detachAndDisposeNonPreservedEntities();
		this.getRoundSystem().finish();
		this.setIgnoreUpdate(false);

	}

	/**
	 * Occurs when all fields have been initialised.
	 */
	public void onStart() {
		this.fFade.hide();
		this.loadTextures();
		this.registerUpdateHandler(this.eysEntityYSorter);
		this.onStartRound();

		this.showSurviveNotification();
		this.playSirenSound();

		SessionScene.AMBIENCE.play();
		EnvironmentVars.MAIN_CONTEXT.registerSound(SessionScene.AMBIENCE);

		this.registerFloatValueModifier(true, new OnValueChangeListener() {
			@Override
			public void valueChanged(float value) {
				EnvironmentVars.MAIN_CONTEXT.getSoundManager().setMasterVolume(value);
			}
		}, new Runnable() {
			@Override
			public void run() {
				SessionScene.AMBIENCE.setVolume(SessionScene.AMBIENCE_VOLUME);
			}
		});
	}

	protected void onStartRound() {
		final DelayModifier DELAY_START = new DelayModifier(1.5f) {
			@Override
			protected void onModifierFinished(IEntity pItem) {
				super.onModifierFinished(pItem);
				SessionScene.this.showFirstWave();
			}
		};
		DELAY_START.setAutoUnregisterWhenFinished(true);

		this.registerEntityModifier(DELAY_START);
	}

	// *********************************
	//

	protected void playSirenSound() {
		ResourceManager.sSiren.setVolume(1);
		ResourceManager.sSiren.play();
		EnvironmentVars.MAIN_CONTEXT.registerSound(ResourceManager.sSiren);
	}

	protected void readSessionArmoryData() {
		for (int i = 0; i < Armory.WEP_ARRAY.length; i++)
			if (StaticData.laserSightItems[i])
				this.ARMORY_DATA.ARRAY_RANGE[i] *= 1.33f;
	}

	private void registerFloatValueModifier(boolean in, OnValueChangeListener valueChangeListener, Runnable onFinish) {

		final FloatValueModifier MOD = new FloatValueModifier(in ? 0f : 1f, in ? 1f : 0f, EaseSineInOut.getInstance(), 1.25f);
		MOD.setOnValueChangeListener(valueChangeListener);
		final Runnable ON_FINISH = onFinish;
		MOD.runOnFinish(new Runnable() {
			@Override
			public void run() {
				if (ON_FINISH != null)
					ON_FINISH.run();
				SessionScene.this.unregisterUpdateHandler(MOD);
			}
		});
		this.registerUpdateHandler(MOD);
	}

	public void removeBulletTarget(BulletTarget target) {
		this.BULLET_TARGETS.remove(target);
	}

	private void resetLaserSightShader() {

		if (this.sLaserSight == null)
			return;

		this.LASER_SIGHT_SHADER.setCompiled(false);
	}

	public void setClipSize(int wepIndex, int clipSize) {
		this.wepClipSizes[wepIndex] = clipSize;
	}

	public void setStandardClipSizes() {
		this.setClipSize(Armory.WEP_SMG, 50);
		this.setClipSize(Armory.WEP_DOMINADOR, 20);
		this.setClipSize(Armory.WEP_SHOTGUN, 20);
		this.setClipSize(Armory.WEP_SNIPER, 7);
		this.setClipSize(Armory.WEP_CALTROP, 10);
		this.setClipSize(Armory.WEP_GRENADE, 5);
	}

	protected void showFirstWave() {
		if (this.rsRoundSystem.getStartRound() != 1)
			return;

		//@formatter:off
		this.sFirstWave = new Sprite(
				(EnvironmentVars.MAIN_CONTEXT.width() - HudRegions.region_wave_1[0].getWidth()) / 2f,
				(EnvironmentVars.MAIN_CONTEXT.height() - HudRegions.region_wave_1[0].getHeight()) / 2f,
				HudRegions.region_wave_1[0], EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager());
		this.sFirstWave.setScale(4f);
		
		this.sFirstWaveSub = new Sprite(
				(EnvironmentVars.MAIN_CONTEXT.width() - HudRegions.region_wave_1[1].getWidth()) / 2f,
				this.sFirstWave.getY() + this.sFirstWave.getHeightScaled() + 4f,
				HudRegions.region_wave_1[1], EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager()){
			@Override
			public void dispose() {
				ResourceManager.btWave1.unload();
				super.dispose();
			}
		};
		this.sFirstWaveSub.setScale(1.5f);
		//@formatter:on

		final Music FIRST_WAVE_MUSIC = ResourceManager.mFirstWave;
		EnvironmentVars.MAIN_CONTEXT.registerSound(FIRST_WAVE_MUSIC);
		FIRST_WAVE_MUSIC.seekTo(0);
		FIRST_WAVE_MUSIC.play();

		SessionScene.HUD.attachChild(SessionScene.this.sFirstWave);
		SessionScene.HUD.attachChild(SessionScene.this.sFirstWaveSub);

		ResourceManager.btWave1.load();

		final DelayModifier DELAY_LIFETIME = new DelayModifier(3f) {
			@Override
			protected void onModifierFinished(IEntity pItem) {
				super.onModifierFinished(pItem);

				SessionScene.this.rsRoundSystem.start();
				final FlashUpdateHandler FLASH = new FlashUpdateHandler(0.05f, 2);

				FLASH.runOnSwitch(new Runnable() {
					@Override
					public void run() {
						final boolean VISIBLE = SessionScene.this.sFirstWave.isVisible();
						SessionScene.this.sFirstWave.setVisible(!VISIBLE);
					}
				});

				FLASH.runOnFinish(new Runnable() {
					@Override
					public void run() {
						SessionScene.this.unregisterUpdateHandler(FLASH);
						SessionScene.this.sFirstWave.setVisible(true);

						EntityUtils.animateEntity(SessionScene.this.sFirstWave, 0.25f, EntityUtils.ANIMATION_JUMP_OUT_MEDIUM_INTESTIVITY,
								EaseLinear.getInstance(), EntityUtils.getDetachDisposeListener());

						EntityUtils.animateEntity(SessionScene.this.sFirstWaveSub, 1f, EntityUtils.ANIMATION_FADE_OUT,
								EaseCubicInOut.getInstance(), EntityUtils.getDetachDisposeListener());
					}
				});
				SessionScene.this.registerUpdateHandler(FLASH);

			}
		};
		DELAY_LIFETIME.setAutoUnregisterWhenFinished(true);
		this.registerEntityModifier(DELAY_LIFETIME);

		EntityUtils.animateEntity(this.sFirstWave, 1f, EntityUtils.ANIMATION_FADE_IN);
		EntityUtils.animateEntity(this.sFirstWaveSub, 1f, EntityUtils.ANIMATION_FADE_IN);
	}

	protected void showSurviveNotification() {
		this.getComboTracker().notify("Survive...", NotificationConstants.NOTIFICATION_COLOR_MESSAGE);
	}

	/**
	 * Closes this game session and returns to the main menu.
	 */
	public void toMainMenu(final String optionalMessage) {

		EnvironmentVars.MAIN_CONTEXT.runOnUpdateThread(new Runnable() {
			@Override
			public void run() {
				for (final BaseAudioEntity audio : EnvironmentVars.MAIN_CONTEXT.getAudioRegistered()) {
					audio.pause();
					EnvironmentVars.MAIN_CONTEXT.unregisterSound(audio);
				}

				ResourceManager.btScrapPartsBig.unload();

				SessionScene.this.setIgnoreUpdate(false);
				SessionScene.this.detachAndDisposeEntities(false);

				SessionScene.HUD.setOnSceneTouchListener(null);

				EnvironmentVars.MAIN_CONTEXT.getCamera().setCenter(EnvironmentVars.MAIN_CONTEXT.width() / 2f,
						EnvironmentVars.MAIN_CONTEXT.height() / 2f);

				final MainMenuScene SCENE = new MainMenuScene() {
					@Override
					public void init() {
						super.init();
						if (!optionalMessage.contentEquals(""))
							this.showDialog(optionalMessage);
					}
				};

				EnvironmentVars.MAIN_CONTEXT.getEngine().setScene(SCENE);
				if (!SessionScene.this.isDisposed())
					SessionScene.this.dispose();
				System.gc();
				SCENE.init();
			}
		});
	}

	protected void transformLaserSight() {

		if (this.sLaserSight == null)
			return;

		this.sLaserSight.setRotation(Character.directionToDegrees(this.pPlayer.getMoveDirection()));

		try {
			final PointF NEW_LASER_SIGHT_POSITION = SessionScene.this.getWeaponTipPosition();

			this.sLaserSight.setX(NEW_LASER_SIGHT_POSITION.x);
			this.sLaserSight.setY(NEW_LASER_SIGHT_POSITION.y);

		} catch (final Exception ex) {
		}

		this.clampLaserCrosshair();
	}

	protected void unloadTextures() {
		ResourceManager.btAmmoIcon.unload();

		ResourceManager.btCharSkin.unload();
		ResourceManager.btCharLb.unload();
		ResourceManager.btCharA01.unload();
		ResourceManager.btCharA02.unload();
		ResourceManager.btCharSleeves01.unload();
		ResourceManager.btCharA01Muzzle.unload();
		ResourceManager.btCharA02Muzzle.unload();
		ResourceManager.btCharUb01.unload();

		ResourceManager.btComboBack.unload();
		ResourceManager.btComboCanvas.unload();
		ResourceManager.btComboFore.unload();
		ResourceManager.btJoyStickBack.unload();
		ResourceManager.btJoyStickFore.unload();
		ResourceManager.btShootButton.unload();
		ResourceManager.btSpeedBoostIcon.unload();
		ResourceManager.btWepFrame.unload();
		ResourceManager.btWeps.unload();
	}
}
