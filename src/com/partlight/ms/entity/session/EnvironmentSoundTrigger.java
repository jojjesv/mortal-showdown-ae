package com.partlight.ms.entity.session;

import java.util.Random;

import org.andengine.entity.Entity;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.DelayModifier;

import com.partlight.ms.resource.EnvironmentVars;
import com.partlight.ms.resource.ResourceManager;

public class EnvironmentSoundTrigger extends Entity {

	private DelayModifier dmHawkSoundTrigger;

	public EnvironmentSoundTrigger() {
	}

	@Override
	public void onAttached() {
		this.resetHawkSoundTrigger();
	}

	@Override
	public void onDetached() {
		this.unregisterEntityModifier(this.dmHawkSoundTrigger);
	}

	protected void playHawkSound() {
		ResourceManager.sHawk.play();
		EnvironmentVars.MAIN_CONTEXT.runOnUpdateThread(new Runnable() {
			@Override
			public void run() {
				EnvironmentSoundTrigger.this.resetHawkSoundTrigger();
			}
		});
	}

	protected void resetHawkSoundTrigger() {
		this.dmHawkSoundTrigger = new DelayModifier(35f + 45f * new Random().nextFloat()) {
			@Override
			protected void onModifierFinished(IEntity pItem) {
				super.onModifierFinished(pItem);
				EnvironmentSoundTrigger.this.playHawkSound();
			}
		};

		this.registerEntityModifier(this.dmHawkSoundTrigger);
	}
}
