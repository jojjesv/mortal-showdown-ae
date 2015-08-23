package com.partlight.ms.util.updatehandler;

import org.andengine.engine.handler.IUpdateHandler;

public class FlashUpdateHandler implements IUpdateHandler {

	private final float	duration;
	private float		totalElapsedSeconds;
	private float		intervalElapsedSeconds;
	private final float	interval;
	private int			flashCount;
	private boolean		isFinished;

	private Runnable	rOnFinish;
	private Runnable	rOnSwitch;

	public FlashUpdateHandler(float interval, int flashCount) {
		this.interval = interval;
		this.flashCount = flashCount * 2;
		this.duration = interval * this.flashCount;
	}

	public boolean isFinished() {
		return this.isFinished;
	}

	protected void onFinished() {
		if (this.rOnFinish != null)
			this.rOnFinish.run();
	}

	protected void onSwitch() {
		if (this.rOnSwitch != null)
			this.rOnSwitch.run();
		this.intervalElapsedSeconds = 0;
	}

	@Override
	public void onUpdate(float pSecondsElapsed) {
		this.totalElapsedSeconds += pSecondsElapsed;
		this.intervalElapsedSeconds += pSecondsElapsed;

		if (this.intervalElapsedSeconds >= this.interval)
			this.onSwitch();

		if (this.flashCount != -1 && this.totalElapsedSeconds >= this.duration)
			if (!this.isFinished) {
				this.onFinished();
				this.isFinished = true;
			}
	}

	@Override
	public void reset() {
		this.flashCount = 0;
		this.totalElapsedSeconds = 0;
		this.isFinished = false;
	}

	public void runOnFinish(Runnable onFinish) {
		this.rOnFinish = onFinish;
	}

	public void runOnSwitch(Runnable onSwitch) {
		this.rOnSwitch = onSwitch;
	}
}
