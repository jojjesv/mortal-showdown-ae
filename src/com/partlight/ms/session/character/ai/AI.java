package com.partlight.ms.session.character.ai;

import org.andengine.engine.handler.IUpdateHandler;

import com.partlight.ms.session.character.Character;
import com.partlight.ms.util.Ruler;
import com.partlight.ms.util.boundary.Boundary.BoundaryUtils;

public class AI implements IUpdateHandler {

	private boolean	isIgnoringTarget;
	private boolean	isTargetCharacter;
	private float	oldContextAngleToTarget;
	private float	targetX;
	private float	targetY;

	protected Character	cContext;
	protected Character	cTarget;

	public AI(Character character) {
		this.cContext = character;
	}

	public Character getTarget() {
		return this.cTarget;
	}

	public float getTargetX() {
		if (this.isTargetCharacter)
			return this.cTarget.getBoundaryCenterX();
		return this.targetX;
	}

	public float getTargetY() {
		if (this.isTargetCharacter)
			return this.cTarget.getBoundaryCenterY();
		return this.targetY;
	}

	public boolean isIgnoringTarget() {
		return this.isIgnoringTarget;
	}

	public boolean isTargetColliding() {
		if (this.isTargetCharacter)
			return BoundaryUtils.isIntersecting(this.cContext.getInteractionBoundary(), this.cTarget.getInteractionBoundary());
		else
			return Ruler.getDistance(this.cContext.getBoundaryCenterX(), this.cContext.getBoundaryCenterY(), this.targetX,
					this.targetY) < 16f;
	}

	public void onTargetCollide() {

	}

	@Override
	public void onUpdate(float pSecondsElapsed) {

		if (!this.isIgnoringTarget) {
			float targetX = 0f;
			float targetY = 0f;

			if (this.isTargetColliding())
				this.onTargetCollide();

			if (this.isTargetCharacter) {

				targetX = this.cTarget.getBoundaryCenterX();
				targetY = this.cTarget.getBoundaryCenterY();
			} else {
				targetX = this.targetX;
				targetY = this.targetY;
			}

			final float angleToTarget = (int) Math
					.toDegrees(Ruler.getAngle(this.cContext.getBoundaryCenterX(), this.cContext.getBoundaryCenterY(), targetX, targetY))
					/ 20f * 20f;

			if (Math.abs(angleToTarget - this.oldContextAngleToTarget) > 20f) {
				this.oldContextAngleToTarget = angleToTarget;
				this.setMoveDirectionToTarget();
			}
		}
	}

	@Override
	public void reset() {

	}

	public void setIgnoreTarget(boolean ignoreTarget) {
		this.isIgnoringTarget = ignoreTarget;
	}

	public void setMoveDirectionToTarget() {

		float angle = this.oldContextAngleToTarget;

		if (angle < 0f)
			angle = 360 + angle;

		this.cContext.setMoveDirection(Character.angleToDirection((int) angle));
	}

	public void setTarget(Character target) {
		this.cTarget = target;
		this.isTargetCharacter = true;
	}

	public void setTarget(float x, float y) {
		this.targetX = x;
		this.targetY = y;
		this.isTargetCharacter = false;
	}
}
