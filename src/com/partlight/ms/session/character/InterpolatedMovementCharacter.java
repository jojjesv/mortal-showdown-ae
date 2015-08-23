package com.partlight.ms.session.character;

import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.Entity;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.util.modifier.ease.EaseLinear;

import com.partlight.ms.scene.session.SessionScene;
import com.partlight.ms.session.character.skin.CharacterSkin;
import com.partlight.ms.util.updatehandler.FloatValueModifier;

public class InterpolatedMovementCharacter extends Character {

	protected FloatValueModifier fvmMovementFactor;

	private final Entity eUpdateAndEntityContainer;

	public InterpolatedMovementCharacter(float x, float y, CharacterSkin skin, int health, SessionScene context) {
		super(x, y, skin, health, context);
		this.fvmMovementFactor = new FloatValueModifier(0.5f, 1, EaseLinear.getInstance(), 0.5f);
		this.eUpdateAndEntityContainer = new Entity() {
			@Override
			public void setAlpha(float pAlpha) {
				InterpolatedMovementCharacter.this.setAlpha(pAlpha);
			}

			@Override
			public void setScale(float pScaleX, float pScaleY) {
				InterpolatedMovementCharacter.this.setScale(pScaleX, pScaleY);
			}
		};
	}

	@Override
	public float getMoveSpeed() {
		return super.moveSpeed * this.fvmMovementFactor.getValue();
	}

	@Override
	protected void onManagedUpdate(float pSecondsElapsed) {
		if (this.fvmMovementFactor != null)
			this.fvmMovementFactor.onUpdate(pSecondsElapsed);

		super.onManagedUpdate(pSecondsElapsed * (this.getMoveSpeed() / super.moveSpeed));
		this.eUpdateAndEntityContainer.onUpdate(pSecondsElapsed);
	}

	@Override
	public void registerEntityModifier(IEntityModifier pEntityModifier) {
		this.eUpdateAndEntityContainer.registerEntityModifier(pEntityModifier);
	}

	@Override
	public void registerUpdateHandler(IUpdateHandler pUpdateHandler) {
		this.eUpdateAndEntityContainer.registerUpdateHandler(pUpdateHandler);
	}

	@Override
	public void startMoving() {
		if (!this.isMoving())
			this.fvmMovementFactor.reset();
		super.startMoving();
	}

	@Override
	public void stopMoving() {
		super.stopMoving();
		this.fvmMovementFactor.reset();
	}

	@Override
	public boolean unregisterEntityModifier(IEntityModifier pEntityModifier) {
		return this.eUpdateAndEntityContainer.unregisterEntityModifier(pEntityModifier);
	}

	@Override
	public boolean unregisterUpdateHandler(IUpdateHandler pUpdateHandler) {
		return this.eUpdateAndEntityContainer.unregisterUpdateHandler(pUpdateHandler);
	}
}
