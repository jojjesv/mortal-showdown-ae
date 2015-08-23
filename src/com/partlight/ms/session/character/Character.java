package com.partlight.ms.session.character;

import java.util.Random;

import org.andengine.entity.Entity;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.util.modifier.IModifier;
import org.andengine.util.modifier.IModifier.IModifierListener;

import com.partlight.ms.Direction;
import com.partlight.ms.entity.Sortable;
import com.partlight.ms.resource.EnvironmentVars;
import com.partlight.ms.resource.ResourceManager;
import com.partlight.ms.resource.ResourceManager.BloodRegions;
import com.partlight.ms.resource.ResourceManager.HudRegions;
import com.partlight.ms.scene.DialogLevelScene;
import com.partlight.ms.scene.session.SessionScene;
import com.partlight.ms.session.character.ai.AI;
import com.partlight.ms.session.character.listener.MovementListener;
import com.partlight.ms.session.character.player.Player;
import com.partlight.ms.session.character.skin.CharacterSkin;
import com.partlight.ms.session.environment.EnvironmentObject;
import com.partlight.ms.session.hud.JoyStick;
import com.partlight.ms.util.Ruler;
import com.partlight.ms.util.boundary.Boundary;

/**
 * 
 * A Character is a movable Entity with a {@link Boundary}. The
 * {@link AnimatedSprite} makes the Character's legs. Therefor, they may be
 * animated using a spritesheet.<br>
 * <br>
 * Note that the Character's implementation of {@link Boundary} is used in
 * association with <em>combat.</em><br>
 * For interaction with the environment, consider using
 * {@link Character#getInteractionBoundary()} instead.
 * 
 * 
 * @see Player
 * @see Zombie
 * @author Johan Svensson - partLight Entertainment
 *
 */
public abstract class Character extends Entity implements Boundary, Sortable {

	private static final float	HBAR_LENGTH		= 32f;
	private static final float	HBAR_Y;
	public static final float	HBAR_TIMEOUT	= 1f;
	public static final float	MAX_SPEED		= 1.5f;
	public static final float	MIN_SPEED		= 0.35f;
	private static int			healthBarsShowing;

	static {
		HBAR_Y = -18f;
	}

	/**
	 * 
	 * @param angle
	 * @return <i>A direction code:</i><br>
	 *         0 = left<br>
	 *         1 = down<br>
	 *         2 = right<br>
	 *         3 = up
	 */
	public static Direction angleToDirection(int angle) {

		if (angle < 0)
			angle = 360 + angle;

		if (angle >= 360 - 24 || angle <= 24)
			return Direction.EAST;
		else if (angle < 90 + 24 && angle > 90 - 24)
			return Direction.SOUTH;
		else if (angle < 180 + 24 && angle > 180 - 24)
			return Direction.WEST;
		else if (angle < 270 + 24 && angle > 270 - 24)
			return Direction.NORTH;
		else if (angle > 315 - 24 && angle < 315 + 24)
			return Direction.NORTHEAST;
		else if (angle < 45 + 24 && angle > 45 - 24)
			return Direction.SOUTHEAST;
		else if (angle < 135 + 24 && angle > 135 - 24)
			return Direction.SOUTHWEST;
		else if (angle < 225 + 24 && angle > 225 - 24)
			return Direction.NORTHWEST;

		return Direction.NONE;
	}

	public static float directionToDegrees(Direction direction) {

		float out = 0;

		switch (direction) {
		case WEST:
			out = 180f;
			break;
		case EAST:
			out = 0f;
			break;
		case SOUTH:
			out = 90f;
			break;
		case NORTH:
			out = 270f;
			break;
		case NORTHWEST:
			out = 225f;
			break;
		case NORTHEAST:
			out = 315f;
			break;
		case SOUTHWEST:
			out = 135f;
			break;
		case SOUTHEAST:
			out = 45f;
			break;
		default:
			break;
		}

		return out;
	}

	public static int directionToStandIndex(Direction direction) {

		switch (direction) {

		case NORTH:
			return 40;

		case NORTHEAST:
		case NORTHWEST:
			return 41;

		case EAST:
		case WEST:
			return 42;

		case SOUTHEAST:
		case SOUTHWEST:
			return 43;

		case SOUTH:
			return 44;

		default:
			return 0;
		}
	}

	public static void unloadTextures() {
		ResourceManager.btCharA01.unload();
		ResourceManager.btCharHair01.unload();
		ResourceManager.btCharHair02.unload();
		ResourceManager.btCharLb.unload();
		ResourceManager.btCharLbWalk.unload();
		ResourceManager.btCharSkin.unload();
		ResourceManager.btCharUb01.unload();
	}

	private AI									aAI;
	private CharacterSkin						csSkin;
	private Boundary							bInteractBoundary;
	private DelayModifier						dmHbarMod;
	private JoyStick.OnDirectionChangeListener	rOnMoveDirectionChanged;
	private Runnable							rOnHealthDepleted;
	private DialogLevelScene					dlsContext;
	private TiledSprite							tsHbarBack;
	private TiledSprite							tsHbarFore;
	private boolean								hasHealthDepleted;
	private boolean								hasSetMaxHealth;
	private boolean								isMoving;
	private boolean								isMoveSpeedBoosted;
	private boolean								isUsingGodMode;
	private int									healthAmount;
	private int									maxHealthAmount;
	private DelayModifier						dmBoostDelay;
	private MovementListener					mlMovementListener;

	protected Direction dMoveDirection;

	protected boolean	canMoveDown;
	protected boolean	canMoveLeft;
	protected boolean	canMoveRight;
	protected boolean	canMoveUp;
	protected float		moveSpeed;

	public Character(float x, float y, CharacterSkin skin, int health, DialogLevelScene context) {
		super(x, y);

		this.attachChild(this.csSkin = skin);
		this.setScaleCenter(0, 0);
		this.setScale(3f);
		this.setHealthAmount(health);
		this.dMoveDirection = this.getStartMoveDirection();
		this.dlsContext = context;

		this.setInteractBoundary(new Boundary() {
			@Override
			public float getBoundaryHeight() {
				return Character.this.getInteractionBoundaryHeight();
			}

			@Override
			public float getBoundaryWidth() {
				return Character.this.getInteractionBoundaryWidth();
			}

			@Override
			public float getBoundaryX() {
				return Character.this.getInteractionBoundaryX();
			}

			@Override
			public float getBoundaryY() {
				return Character.this.getInteractionBoundaryY();
			}
		});
	}

	protected boolean assertCanMove() {
		return true;
	}

	/**
	 * Modifies this Character's move speed so that it is multiplied by a
	 * certain factor for a certain duration. After that, this Character's move
	 * speed will be set back to normal.
	 * 
	 * @param factor
	 *            Multiplication factor.<br>
	 *            <i>E.g. 0.5f will result in half the move speed.</i>
	 * @param duration
	 *            Duration.
	 * @see Character#getMoveSpeed()
	 * @see Character#setMoveSpeed(float)
	 */
	public void boostMoveSpeed(float factor, float duration) {

		if (!this.isMoveSpeedBoosted)
			this.isMoveSpeedBoosted = true;
		else
			return;

		final float START_MOVE_SPEED = this.moveSpeed;
		final float FACTOR = factor;

		if (this.dmBoostDelay != null)
			this.unregisterEntityModifier(this.dmBoostDelay);
		this.dmBoostDelay = new DelayModifier(duration) {
			@Override
			protected void onModifierFinished(IEntity pItem) {
				super.onModifierFinished(pItem);
				Character.this.setMoveSpeed(START_MOVE_SPEED);
				Character.this.isMoveSpeedBoosted = false;
			}

			@Override
			protected void onModifierStarted(IEntity pItem) {
				super.onModifierStarted(pItem);
				Character.this.setMoveSpeed(START_MOVE_SPEED * FACTOR);
			}
		};
		this.dmBoostDelay.setAutoUnregisterWhenFinished(true);
		this.registerEntityModifier(this.dmBoostDelay);
	}

	public void directSkin() {
		this.csSkin.directSkin(this.dMoveDirection, this.isMoving, this.moveSpeed);
	}

	public void drawBlood() {
		final Random GENERATOR = new Random();
		ITextureRegion bloodTextureRegion = null;

		if (GENERATOR.nextBoolean())
			bloodTextureRegion = BloodRegions.region_blood01;
		else if (GENERATOR.nextBoolean())
			bloodTextureRegion = BloodRegions.region_blood02;
		else
			bloodTextureRegion = BloodRegions.region_blood03;

		final EnvironmentObject OBJECT = new EnvironmentObject(this.getBoundaryCenterX() - bloodTextureRegion.getWidth() / 2f,
				this.getBoundaryCenterY() - bloodTextureRegion.getHeight() / 2f, 1.0f, bloodTextureRegion);
		OBJECT.fadeOut = true;
		OBJECT.ticksUntilFadeOut = 300;
		OBJECT.fadeOutFactor = 0.05;

		if (this.isContextSessionScene()) {
			if (this.getContextAsSession().getBlood().getList().size() == 0)
				ResourceManager.btBlood.load();

			this.getContextAsSession().getBlood().getList().add(OBJECT);
			this.getContextAsSession().getBlood().updateDrawing();
		}
	}

	public AI getAI() {
		return this.aAI;
	}

	public final float getBoundaryCenterX() {
		return this.getBoundaryX() + this.getBoundaryWidth() / 2f;
	}

	public final float getBoundaryCenterY() {
		return this.getBoundaryY() + this.getBoundaryHeight() / 2f;
	}

	@Override
	public float getBoundaryHeight() {
		return this.getHeightScaled() * 0.75f;
	}

	@Override
	public float getBoundaryWidth() {
		return this.getBoundaryHeight();
	}

	@Override
	public float getBoundaryX() {
		return this.getX() + (this.getWidthScaled() - this.getBoundaryWidth()) / 2f;
	}

	@Override
	public float getBoundaryY() {
		return this.getY() + this.getHeightScaled() / 2f;
	}

	/**
	 * Returns this Character's context {@link SessionScene}.
	 * 
	 * @return This Character's context {@link SessionScene}.
	 */
	public DialogLevelScene getContext() {
		return this.dlsContext;
	}

	public SessionScene getContextAsSession() {
		return (SessionScene) this.getContext();
	}

	// *********************************
	//

	// GET & SET & IS METHODS
	// *********************************

	/**
	 * Returns this Character's health amount.
	 * 
	 * @return This Character's health amount.
	 * @see Character#setHealthAmount(int)
	 */
	public int getHealthAmount() {
		return this.healthAmount;
	}

	protected TiledSprite getHealthBarBack() {
		return this.tsHbarBack;
	}

	protected TiledSprite getHealthBarFore() {
		return this.tsHbarFore;
	}

	public float getHeightScaled() {
		return this.csSkin.getLegs().getHeight() * this.getScaleY();
	}

	/**
	 * Gets this Character's interaction boundary.
	 * 
	 * @return This Character's interaction boundary.
	 */
	public Boundary getInteractionBoundary() {
		return this.bInteractBoundary;
	}

	public float getInteractionBoundaryHeight() {
		return this.getBoundaryHeight() - 32f;
	}

	public float getInteractionBoundaryWidth() {
		return this.getBoundaryWidth() - 32f;
	}

	public float getInteractionBoundaryX() {
		return this.getBoundaryX() + 16f;
	}

	public float getInteractionBoundaryY() {
		return this.getBoundaryY() + 16f;
	}

	public int getMaxHealthAmount() {
		return this.maxHealthAmount;
	}

	public Direction getMoveDirection() {
		return this.dMoveDirection;
	}

	public MovementListener getMovementListener() {
		return this.mlMovementListener;
	}

	/**
	 * Returns this Character's move speed, in pixels per update.
	 * 
	 * @return This Character's move speed, in pixels per update.
	 */
	public float getMoveSpeed() {
		return this.moveSpeed;
	}

	public CharacterSkin getSkin() {
		return this.csSkin;
	}

	@Override
	public float getSortingY() {
		return this.getY() + this.getHeightScaled();
	}

	protected Direction getStartMoveDirection() {
		return Direction.NORTH;
	}

	// *********************************
	//

	// MOVEMENT CONTROL
	// *********************************

	public float getWidthScaled() {
		return this.csSkin.getLegs().getWidth() * this.getScaleX();
	}

	protected boolean hasHealthDepleted() {
		return this.hasHealthDepleted;
	}

	public boolean isContextSessionScene() {
		return this.dlsContext instanceof SessionScene;
	}

	/**
	 * Returns if this Character's move speed has previously been multiplied.
	 * 
	 * @return If this Character's move speed has previously been multiplied.
	 */
	public boolean isMoveSpeedBoosted() {
		return this.isMoveSpeedBoosted;
	}

	/**
	 * Returns if this Character is moving.
	 * 
	 * @return If this Character is moving.
	 */
	public boolean isMoving() {
		return this.isMoving;
	}

	/**
	 * Returns this Character's usage of God Mode.
	 * 
	 * @return Whether this Character is using God Mode or not.
	 * @see Character#setGodMode(boolean)
	 */
	public boolean isUsingGodMode() {
		return this.isUsingGodMode;
	}

	@Override
	public void onAttached() {
		this.getContext().getEntityYSorter().add(this);
		super.onAttached();
	}

	@Override
	public void onDetached() {
		this.getContext().getEntityYSorter().remove(this);
		super.onDetached();
	}

	/**
	 * Gets called when this Character's health amount changes.
	 * 
	 * @see Character#getHealthAmount()
	 * @see Character#setHealthAmount(int)
	 */
	public void onHealthChanged() {

		if (Character.healthBarsShowing == 0)
			ResourceManager.btHBar.load();

		Character.healthBarsShowing++;

		if (this.tsHbarBack == null) {

			this.tsHbarBack = new TiledSprite(0f, 0f, HudRegions.region_hbar, EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager());
			this.tsHbarBack.setCurrentTileIndex(1);
			this.tsHbarBack.setScaleCenter(0, 0);
			this.tsHbarBack.setScale(Character.HBAR_LENGTH * this.getScaleX(), 1.5f * this.getScaleY());
		}

		this.setX(this.getX());
		this.setY(this.getY());

		if (this.tsHbarFore == null) {

			this.tsHbarFore = new TiledSprite(0f, 0f, HudRegions.region_hbar, EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager());
			this.tsHbarFore.setCurrentTileIndex(0);
		}

		this.tsHbarFore.setScaleCenter(0, 0);

		final int HEALTH = this.getHealthAmount() < 0 ? 0 : this.getHealthAmount();

		this.tsHbarFore.setScaleX((float) HEALTH / (float) this.getMaxHealthAmount());

		if (this.dmHbarMod == null) {
			this.dmHbarMod = new DelayModifier(Character.HBAR_TIMEOUT);
			this.dmHbarMod.addModifierListener(new IModifierListener<IEntity>() {
				@Override
				public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
					EnvironmentVars.MAIN_CONTEXT.runOnUpdateThread(new Runnable() {
						@Override
						public void run() {
							Character.this.tsHbarBack.detachSelf();
							Character.this.tsHbarFore.detachSelf();

							Character.healthBarsShowing--;

							if (Character.healthBarsShowing == 0)
								ResourceManager.btHBar.unload();
						}
					});
				}

				@Override
				public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
					try {
						if (Character.this.getHealthAmount() > 0) {
							Character.this.getParent().attachChild(Character.this.tsHbarBack);
							Character.this.tsHbarBack.attachChild(Character.this.tsHbarFore);
						}
					} catch (final IllegalStateException ex) {

					}
				}
			});
		}

		this.dmHbarMod.reset();
		this.unregisterEntityModifier(this.dmHbarMod);
		this.registerEntityModifier(this.dmHbarMod);
	}

	/**
	 * Gets called when this Character reaches a health amount of zero or less.
	 */
	public void onHealthDepleted() {

		if (this.healthAmount <= 0)
			return;

		this.hasHealthDepleted = true;

		this.stopMoving();

		this.healthAmount = 0;

		if (this.rOnHealthDepleted != null)
			this.rOnHealthDepleted.run();

		EnvironmentVars.MAIN_CONTEXT.runOnUpdateThread(new Runnable() {

			@Override
			public void run() {
				if (Character.this.tsHbarFore != null) {
					Character.this.tsHbarFore.detachSelf();
					Character.this.tsHbarFore.dispose();
				}

				if (Character.this.tsHbarBack != null) {
					Character.this.tsHbarBack.detachSelf();
					Character.this.tsHbarBack.dispose();
				}

				Character.this.onSpawnCorpse(false);
			}
		});
	}

	@Override
	protected void onManagedUpdate(float pSecondsElapsed) {
		super.onManagedUpdate(pSecondsElapsed);

		if (this.isMoving())
			this.onMoveRequest();

		if (this.aAI != null)
			this.aAI.onUpdate(pSecondsElapsed);
	}

	/**
	 * Gets called inside {@link Character#onManagedUpdate(float)}, requesting
	 * this Character to move.
	 */
	protected void onMoveRequest() {

		if (!this.assertCanMove())
			return;

		switch (this.getMoveDirection()) {
		case WEST:
			this.testPathAndMove(-this.getMoveSpeed(), 0f);
			break;
		case SOUTH:
			this.testPathAndMove(0f, this.getMoveSpeed());
			break;
		case EAST:
			this.testPathAndMove(this.getMoveSpeed(), 0f);
			break;
		case NORTH:
			this.testPathAndMove(0f, -this.getMoveSpeed());
			break;
		case NORTHEAST:
			this.testPathAndMove(this.getMoveSpeed(), -this.getMoveSpeed());
			break;
		case NORTHWEST:
			this.testPathAndMove(-this.getMoveSpeed(), -this.getMoveSpeed());
			break;
		case SOUTHWEST:
			this.testPathAndMove(-this.getMoveSpeed(), this.getMoveSpeed());
			break;
		case SOUTHEAST:
			this.testPathAndMove(this.getMoveSpeed(), this.getMoveSpeed());
			break;
		default:
			break;
		}
	}

	/**
	 * Gets called inside {@link Character#onHealthDepleted()}, spawning a
	 * corpse Entity resembling this Character.<br>
	 * <br>
	 * <strong>Note:</strong> In order to perform this method without any
	 * Exceptions, {@link Character#setCorpseTextureRegion(ITiledTextureRegion)}
	 * must have previously been called.
	 */
	public void onSpawnCorpse(boolean callInUpdateThread) {

		final CharacterCorpse CORPSE = new CharacterCorpse(this);
		CORPSE.setScaleCenter(0, 0);
		CORPSE.setScale(this.getScaleX(), this.getScaleY());
		CORPSE.setZIndex(Ruler.clamp(this.getZIndex() - 4, 0, this.getContext().getLevel().getMapHeight()));

		if (callInUpdateThread)
			EnvironmentVars.MAIN_CONTEXT.runOnUpdateThread(new Runnable() {
				@Override
				public void run() {
					Character.this.spawnCorpse(CORPSE);
				}
			});
		else
			this.spawnCorpse(CORPSE);
	}

	public void setAI(AI ai) {
		this.aAI = ai;
	}

	@Override
	public void setAlpha(float pAlpha) {
		super.setAlpha(pAlpha);
		this.csSkin.setAlpha(pAlpha);
	}

	/**
	 * Sets this Character's usage of God Mode.<br>
	 * If God Mode is enabled, this Character can not drop any amount of health.
	 * 
	 * @param godMode
	 *            Whether to use God Mode or not.
	 * @see Character#isUsingGodMode()
	 */
	public void setGodMode(boolean godMode) {
		this.isUsingGodMode = godMode;
	}

	public void setHealthAmount(int amount) {

		if (!this.hasSetMaxHealth)
			this.maxHealthAmount = amount;

		if (this.isUsingGodMode() && this.healthAmount > amount)
			return;

		if (amount <= 0 && !this.hasHealthDepleted)
			this.onHealthDepleted();

		if (this.healthAmount != amount && this.hasSetMaxHealth) {
			this.healthAmount = amount;
			this.onHealthChanged();
		} else
			this.healthAmount = amount;

		if (!this.hasSetMaxHealth)
			this.hasSetMaxHealth = true;
	}

	/**
	 * Sets this Character's interaction boundary.<br>
	 * An interaction boundary is mostly suitable for interacting with the
	 * environment, e.g. picking up an item from the ground.
	 * 
	 * @param boundary
	 *            New interaction boundary.
	 */
	public void setInteractBoundary(Boundary boundary) {
		this.bInteractBoundary = boundary;
	}

	public void setMoveDirection(Direction direction) {

		if (direction != Direction.NONE) {
			if (this.dMoveDirection.ordinal() != direction.ordinal())
				if (this.rOnMoveDirectionChanged != null)
					this.rOnMoveDirectionChanged.onDirectionChange(direction, this.dMoveDirection);
			this.dMoveDirection = direction;
			this.directSkin();
		}
	}

	public void setMovementListener(MovementListener listener) {
		this.mlMovementListener = listener;
	}

	/**
	 * Sets this Character's move speed, in pixels per update.
	 * 
	 * @param moveSpeed
	 *            New move speed.
	 */
	public void setMoveSpeed(float moveSpeed) {
		this.moveSpeed = moveSpeed;
	}

	public void setOnMoveDirectionChanged(JoyStick.OnDirectionChangeListener onMoveDirectionChanged) {
		this.rOnMoveDirectionChanged = onMoveDirectionChanged;
	}

	@Override
	public void setX(float pX) {
		super.setX(pX);
		if (this.tsHbarBack != null)
			this.tsHbarBack.setX(pX + (this.csSkin.getLegs().getWidth() - Character.HBAR_LENGTH) / 2f);
	}

	@Override
	public void setY(float pY) {

		if (pY != this.getY())
			this.getContext().getEntityYSorter().requestUpdate();

		super.setY(pY);

		if (this.tsHbarBack != null) {

			final float Y_MIN = 8f;
			final float Y_MAX = this.getContext().getLevel().getMapHeight() - this.tsHbarBack.getHeightScaled() - 8f;

			float hbarY = pY + Character.HBAR_Y;

			if (hbarY < Y_MIN)
				hbarY = Y_MIN;

			if (hbarY > Y_MAX)
				hbarY = Y_MAX;

			this.tsHbarBack.setY(hbarY);
			this.tsHbarBack.setZIndex((int) hbarY + this.getContext().getLevel().getMapWidth());
		}
	}

	protected void spawnCorpse(CharacterCorpse corpse) {
		if (this.isDisposed())
			return;

		this.getContext().attachChild(corpse);
		this.getContext().sortChildren();
		this.detachSelf();
		this.dispose();
	}

	public void startMoving() {

		if (!this.isMoving && this.mlMovementListener != null)
			this.mlMovementListener.onStartedMoving(this);

		this.isMoving = true;
		this.canMoveRight = true;
		this.canMoveLeft = true;
		this.canMoveDown = true;
		this.canMoveUp = true;

		this.setMoveDirection(this.dMoveDirection);
	}

	public void stopMoving() {
		if (this.isMoving && this.mlMovementListener != null)
			this.mlMovementListener.onStoppedMoving(this);

		this.isMoving = false;
		this.csSkin.getLegs().stopAnimation(Character.directionToStandIndex(this.getMoveDirection()));
	}

	protected void testPathAndMove(float xd, float yd) {

		final boolean moveToYDelta = true;
		final boolean moveToXDelta = true;

		// if (this.isContextSessionScene()) {
		// for (final Boundary obstacle : ((SessionScene)
		// this.dlsContext).getObstacles()) {
		//
		// final RectangularShape targetShape = new
		// Rectangle(this.getBoundaryX() + xd, this.getBoundaryY() + yd,
		// this.getBoundaryWidth(), this.getBoundaryHeight(),
		// EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager());
		//
		// this.canMoveRight = !((int) obstacle.getBoundaryX() == (int)
		// this.getBoundaryX() + (int) this.getBoundaryWidth()
		// && (int) obstacle.getBoundaryY() > (int) this.getBoundaryY() - (int)
		// obstacle.getBoundaryHeight() && (int) obstacle
		// .getBoundaryY() < (int) this.getBoundaryY() + (int)
		// this.getBoundaryHeight());
		//
		// this.canMoveLeft = !((int) obstacle.getBoundaryX() == (int)
		// this.getX() - (int) obstacle.getBoundaryHeight()
		// && (int) obstacle.getBoundaryY() > (int) this.getY() - (int)
		// obstacle.getBoundaryHeight() && (int) obstacle
		// .getBoundaryY() < (int) this.getY() + (int)
		// this.getBoundaryHeight());
		//
		// this.canMoveDown = !((int) obstacle.getBoundaryY() == (int)
		// this.getBoundaryY() + (int) this.getBoundaryHeight()
		// && (int) obstacle.getBoundaryX() > (int) this.getBoundaryX() - (int)
		// obstacle.getBoundaryWidth() && (int) obstacle
		// .getBoundaryX() < (int) this.getBoundaryX() + (int)
		// this.getBoundaryWidth());
		//
		// this.canMoveUp = !((int) obstacle.getBoundaryY() == (int)
		// this.getBoundaryY() - (int) this.getBoundaryHeight()
		// && (int) obstacle.getBoundaryX() > (int) this.getBoundaryX() - (int)
		// obstacle.getBoundaryWidth() && (int) obstacle
		// .getBoundaryX() < (int) this.getBoundaryX() + (int)
		// this.getBoundaryWidth());
		//
		// if (targetShape.getX() + targetShape.getWidth() >
		// obstacle.getBoundaryX()
		// && targetShape.getX() < obstacle.getBoundaryX() +
		// obstacle.getBoundaryWidth()) {
		//
		// if (targetShape.getY() + targetShape.getHeight() >
		// obstacle.getBoundaryY()
		// && targetShape.getY() < obstacle.getBoundaryY() +
		// obstacle.getBoundaryHeight()) {
		//
		// final Point pCollisionAreaBoundary = new Point();
		//
		// if (targetShape.getX() >= obstacle.getBoundaryX() +
		// obstacle.getBoundaryWidth() / 2f) {
		// pCollisionAreaBoundary.x = (int) (obstacle.getBoundaryX() +
		// obstacle.getBoundaryWidth() - targetShape.getX());
		// } else {
		// pCollisionAreaBoundary.x = (int) (targetShape.getX() +
		// targetShape.getWidthScaled() - obstacle.getBoundaryX());
		// }
		//
		// if (targetShape.getY() >= obstacle.getBoundaryY() +
		// obstacle.getBoundaryHeight() / 2f) {
		// pCollisionAreaBoundary.y = (int) (obstacle.getBoundaryY() +
		// obstacle.getBoundaryHeight() - targetShape.getY());
		// } else {
		// pCollisionAreaBoundary.y = (int) (targetShape.getY() +
		// targetShape.getHeightScaled() - obstacle.getBoundaryY());
		// }
		//
		// if (pCollisionAreaBoundary.x > pCollisionAreaBoundary.y) {
		//
		// if (targetShape.getY() >= obstacle.getBoundaryY() +
		// obstacle.getBoundaryHeight() / 2f) {
		// this.setY(obstacle.getBoundaryY() + obstacle.getBoundaryHeight() +
		// (this.getY() - this.getBoundaryY()));
		// } else {
		// this.setY(obstacle.getBoundaryY() - this.getHeightScaled());
		// }
		// moveToYDelta = false;
		//
		// } else {
		//
		// if (targetShape.getX() >= obstacle.getBoundaryX() +
		// obstacle.getBoundaryWidth() / 2f) {
		// this.setX(obstacle.getBoundaryX() + obstacle.getBoundaryWidth());
		// } else {
		// this.setX(obstacle.getBoundaryX() - this.getBoundaryWidth());
		// }
		// moveToXDelta = false;
		//
		// }
		// }
		// }
		// }
		// }

		if (moveToYDelta)
			this.setY(this.getY() + yd);
		if (moveToXDelta)
			this.setX(this.getX() + xd);
	}
}
