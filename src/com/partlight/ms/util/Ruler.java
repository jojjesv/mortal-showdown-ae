package com.partlight.ms.util;

public class Ruler {

	public static float clamp(float val, float min, float max) {
		if (val < min)
			return min;
		else if (val > max)
			return max;
		return val;
	}

	public static int clamp(int val, int min, int max) {
		if (val < min)
			return min;
		else if (val > max)
			return max;
		return val;
	}

	public static float getAngle(float x1, float y1, float x2, float y2) {
		return (float) Math.atan2(y2 - y1, x2 - x1);
	}

	public static float getDistance(float x1, float y1, float x2, float y2) {
		return (float) Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
	}
}
