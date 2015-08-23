package com.partlight.ms.util;

import org.andengine.util.math.MathConstants;
import org.andengine.util.modifier.ease.IEaseFunction;

public class EaseJump implements IEaseFunction {

	private static EaseJump INSTANCE;

	public static EaseJump getInstance() {
		if (EaseJump.INSTANCE == null)
			EaseJump.INSTANCE = new EaseJump();
		return EaseJump.INSTANCE;
	}

	private EaseJump() {

	}

	@Override
	public float getPercentage(float pSecondsElapsed, float pDuration) {

		final float value = (float) Math.sin(MathConstants.PI * (1f - pSecondsElapsed / pDuration));

		return value;
	}
}
