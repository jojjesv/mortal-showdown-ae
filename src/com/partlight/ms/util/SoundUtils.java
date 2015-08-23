package com.partlight.ms.util;

import java.util.Random;

import org.andengine.audio.sound.Sound;

public final class SoundUtils {

	public static void playRandomVolume(Sound sound) {
		sound.setVolume(0.8f + new Random().nextFloat() * 0.2f);
		sound.play();
	}
}
