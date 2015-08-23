package com.partlight.ms.util.updatehandler;

import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.util.modifier.ease.IEaseFunction;

public class FloatValueModifier implements IUpdateHandler {

	public interface OnValueChangeListener {
		public void valueChanged(float newValue);
	}

	private float				percent;
	private float				value;
	private Runnable			rOnFinish;
	private final IEaseFunction	ease;
	private float				from;
	private float				to;
	private final float			duration;
	private float				totalSecondsElapsed;

	private OnValueChangeListener valueChangeListener;

	public FloatValueModifier(float from, float to, IEaseFunction ease, float duration) {
		this.ease = ease;
		this.from = from;
		this.to = to;
		this.duration = duration;
		this.reset();
	}

	public float getValue() {
		return this.value;
	}

	protected void onFinished() {
		if (this.rOnFinish != null)
			this.rOnFinish.run();
	}

	@Override
	public void onUpdate(float pSecondsElapsed) {
		if (this.totalSecondsElapsed >= this.duration) {
			this.onValueChanged(this.value = this.to);
			this.onFinished();
		} else {
			this.percent = this.ease.getPercentage(this.totalSecondsElapsed, this.duration);
			this.value = this.from + (this.to - this.from) * this.percent;
			this.totalSecondsElapsed += pSecondsElapsed;
			this.onValueChanged(this.value);
		}
	}

	protected void onValueChanged(float value) {
		if (this.valueChangeListener != null)
			this.valueChangeListener.valueChanged(value);
	}

	@Override
	public void reset() {
		this.totalSecondsElapsed = 0f;
		this.percent = 0f;
	}

	public void runOnFinish(Runnable onFinish) {
		this.rOnFinish = onFinish;
	}

	public void setFrom(float from) {
		this.from = from;
	}

	public void setOnValueChangeListener(OnValueChangeListener listener) {
		this.valueChangeListener = listener;
	}

	public void setTo(float to) {
		this.to = to;
	}
}
