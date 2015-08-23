package com.partlight.ms.session.round;

import java.util.Random;

import com.partlight.ms.scene.session.SessionScene;
import com.partlight.ms.session.character.Zombie;
import com.partlight.ms.session.character.skin.CharacterSkin;
import com.partlight.ms.session.character.skin.zombie.Zombie1CharacterSkin;

public class TutorialRoundSystem extends RoundSystem {

	public TutorialRoundSystem(SessionScene context) {
		super(context);
	}

	@Override
	protected Zombie addNPC(float spawnX, float spawnY) {
		final Zombie NPC = super.addNPC(spawnX, spawnY);
		NPC.setDummy(true);
		return NPC;
	}

	@Override
	public int getSpawnCount() {
		return 16;
	}

	@Override
	public int getSpawnHealth() {
		return 18;
	}

	@Override
	public float getSpawnInterval() {
		return 1f;
	}

	@Override
	public int getSpawnScoreValue() {
		return 0;
	}

	@Override
	public CharacterSkin getSpawnSkin(boolean walkAnimation) {
		return new Zombie1CharacterSkin(walkAnimation);
	}

	@Override
	public float getSpawnSpeed() {
		return 0.35f;
	}

	@Override
	public float getSpawnX() {
		return 32 + (this.getContext().getLevel().getMapWidth() - 64) * new Random().nextFloat();
	}

	@Override
	public float getSpawnY() {
		return this.getContext().getLevel().getMapHeight();
	}
}
