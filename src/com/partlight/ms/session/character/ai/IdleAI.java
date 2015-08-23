package com.partlight.ms.session.character.ai;

import java.util.Random;

import com.partlight.ms.session.character.Character;
import com.partlight.ms.session.level.Level;
import com.partlight.ms.util.Ruler;

public class IdleAI extends AI {

	private float	idleSecondsElapsed;
	private float	targetIdleSecondsElapsed;
	private float	xLow;
	private float	xHigh;
	private float	yHigh;
	private float	yLow;

	public IdleAI(Character character, float xlow, float ylow, float xhigh, float yhigh) {
		super(character);
		this.xLow = xlow;
		this.yLow = ylow;
		this.xHigh = xhigh;
		this.yHigh = yhigh;
	}

	public void generateTargetPosition() {
		float x = 0.0f, y = 0.0f;

		final Level LEVEL = this.cContext.getContext().getLevel();
		final Random RANDOM = new Random();

		final float DISTANCE = 240.0f * (0.5f + RANDOM.nextFloat() / 2f);
		final float RADIANS = (float) (Math.PI * 2.0f) * RANDOM.nextFloat();

		x = this.cContext.getX() + (float) Math.cos(RADIANS) * DISTANCE;
		y = this.cContext.getY() + (float) Math.sin(RADIANS) * DISTANCE;

		x = Ruler.clamp(x, 0.0f, LEVEL.getMapWidth());
		y = Ruler.clamp(y, 0.0f, LEVEL.getMapHeight());

		this.setTarget(x, y);
	}

	public float getXHigh() {
		return this.xHigh;
	}

	public float getXLow() {
		return this.xLow;
	}

	public float getYHigh() {
		return this.yHigh;
	}

	public float getYLow() {
		return this.yLow;
	}

	@Override
	public void onTargetCollide() {
		super.onTargetCollide();
		this.generateTargetPosition();
	}

	@Override
	public void onUpdate(float pSecondsElapsed) {
		super.onUpdate(pSecondsElapsed);

		if (this.idleSecondsElapsed >= this.targetIdleSecondsElapsed) {

			this.idleSecondsElapsed = 0.0f;

			this.targetIdleSecondsElapsed = 0.5f + 4.0f * new Random().nextFloat();

			if (this.cContext.isMoving())
				this.cContext.stopMoving();
			else
				this.cContext.startMoving();
		} else
			this.idleSecondsElapsed += pSecondsElapsed;
	}

	@Override
	@Deprecated
	public void setTarget(Character target) {
	}

	public void setXHigh(float xHigh) {
		this.xHigh = xHigh;
	}

	public void setXLow(float xLow) {
		this.xLow = xLow;
	}

	public void setYHigh(float yHigh) {
		this.yHigh = yHigh;
	}

	public void setYLow(float yLow) {
		this.yLow = yLow;
	}
}
