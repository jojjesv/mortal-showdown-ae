package com.partlight.ms.session.character.ai;

import com.partlight.ms.session.character.Zombie;

public class ZombieAI extends AI {

	private final Zombie	ZOMBIE;
	private boolean			hasCalledHarmTarget;
	private boolean			hasHitTargetSinceAttack;

	public ZombieAI(Zombie zombie) {
		super(zombie);
		this.ZOMBIE = zombie;
	}

	public boolean hasHitTargetSinceAttack() {
		return this.hasHitTargetSinceAttack;
	}

	public void onHarmTarget() {
		if (super.cTarget == null)
			return;
		this.hasHitTargetSinceAttack = true;
		super.cTarget.setHealthAmount(super.cTarget.getHealthAmount() - 20);
	}

	@Override
	public void onTargetCollide() {
		super.onTargetCollide();

		if (!this.ZOMBIE.isAttacking() && !this.isIgnoringTarget()) {
			this.ZOMBIE.attackTarget();
			this.hasHitTargetSinceAttack = false;
		}

		if (this.ZOMBIE.isAttacking())
			if (this.ZOMBIE.getSkin().getArms().getCurrentTileIndex() == this.ZOMBIE.getAnimatedArmsStartFrame() + 2) {
				if (!this.hasCalledHarmTarget) {
					this.onHarmTarget();
					this.hasCalledHarmTarget = true;
				}
			} else
				this.hasCalledHarmTarget = false;
	}

	@Override
	public void onUpdate(float pSecondsElapsed) {
		if (!this.ZOMBIE.isAttacking())
			super.onUpdate(pSecondsElapsed);
		else if (this.isTargetColliding())
			this.onTargetCollide();
	}
}
