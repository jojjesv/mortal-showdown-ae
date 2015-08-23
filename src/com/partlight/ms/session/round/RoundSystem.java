package com.partlight.ms.session.round;

import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.opengl.texture.region.ITiledTextureRegion;

import com.partlight.ms.resource.ResourceManager.ZombieTextureRegions.Zombie01;
import com.partlight.ms.scene.session.SessionScene;
import com.partlight.ms.session.character.CharacterCorpse;
import com.partlight.ms.session.character.Zombie;
import com.partlight.ms.session.character.ai.ZombieAI;
import com.partlight.ms.session.character.skin.CharacterSkin;

import android.util.Log;

public abstract class RoundSystem {

	private class RoundSystemUpdateHandler implements IUpdateHandler {

		private float elapsedTime;

		@Override
		public void onUpdate(float pSecondsElapsed) {

			if (this.elapsedTime >= RoundSystem.this.getSpawnInterval() && RoundSystem.this.remainingZombiesToSpawn > 0) {
				RoundSystem.this.onSpawn();
				this.elapsedTime = 0;
			}

			this.elapsedTime += pSecondsElapsed;
		}

		@Override
		public void reset() {
		}
	}

	private final SessionScene				sContext;
	private final RoundSystemUpdateHandler	updateHandler;
	private int								remainingZombiesToSpawn;
	private int								remainingZombiesToKill;
	private int								roundIndex;
	private boolean							hasParent;
	private boolean							suspendSpawn;

	public RoundSystem(SessionScene context) {
		this.sContext = context;
		this.updateHandler = new RoundSystemUpdateHandler();
	}

	protected Zombie addNPC(float spawnX, float spawnY) {

		final float MOVE_SPEED = this.getSpawnSpeed();

		final Zombie NPC = this.createNPC(spawnX, spawnY, MOVE_SPEED < 0.5f);
		NPC.setItemSpawnChance(this.getSpawnItemChance());
		NPC.setScoreValue(this.getSpawnScoreValue());

		NPC.setAI(new ZombieAI(NPC));
		NPC.getAI().setTarget(this.sContext.getPlayer());
		NPC.getAI().setIgnoreTarget(false);
		NPC.setMoveSpeed(MOVE_SPEED);
		NPC.startMoving();
		NPC.getAI().setMoveDirectionToTarget();
		return NPC;
	}

	protected Zombie createNPC(float spawnX, float spawnY, boolean useWalkAnimation) {

		final Zombie NPC = new Zombie(spawnX, spawnY, this.getSpawnSkin(useWalkAnimation), this.getSpawnHealth(), this.sContext) {
			@Override
			protected void spawnCorpse(CharacterCorpse corpse) {
				super.spawnCorpse(corpse);
				RoundSystem.this.onNpcKilled(this);
			}
		};

		return NPC;
	}

	public void finish() {
		this.sContext.unregisterUpdateHandler(this.updateHandler);
		this.hasParent = false;
	}

	protected SessionScene getContext() {
		return this.sContext;
	}

	public int getRoundIndex() {
		return this.roundIndex;
	}

	protected ITiledTextureRegion getSpawnArmsTextureRegion() {
		return Zombie01.region_a01;
	}

	public abstract int getSpawnCount();

	public abstract int getSpawnHealth();

	public abstract float getSpawnInterval();

	public int getSpawnItemChance() {
		return 0;
	}

	public abstract int getSpawnScoreValue();

	public abstract CharacterSkin getSpawnSkin(boolean walkAnimation);

	public abstract float getSpawnSpeed();

	public abstract float getSpawnX();

	public abstract float getSpawnY();

	public int getStartRound() {
		return 1;
	}

	protected void onNpcKilled(Zombie npc) {
		this.remainingZombiesToKill--;
		if (this.remainingZombiesToKill <= 0 && !this.hasParent)
			this.onRoundEnd();
	}

	public void onRoundEnd() {

		this.roundIndex++;

		Log.v("Mortal Showdown", "Round ended on Wave " + this.roundIndex);

		this.startRound();
	}

	protected void onSpawn() {

		if (!this.hasParent || this.suspendSpawn)
			return;

		this.sContext.attachChild(this.addNPC(this.getSpawnX(), this.getSpawnY()));

		this.remainingZombiesToSpawn--;

		if (this.remainingZombiesToSpawn <= 0)
			this.finish();
	}

	public void setSuspendSpawn(boolean suspendSpawn) {
		this.suspendSpawn = suspendSpawn;
	}

	public void start() {
		this.roundIndex = this.getStartRound() - 1;
		this.suspendSpawn = false;
		this.startRound();
	}

	protected void startRound() {
		this.sContext.registerUpdateHandler(this.updateHandler);
		this.hasParent = true;
		this.remainingZombiesToSpawn = this.remainingZombiesToKill = this.getSpawnCount();
	}
}
