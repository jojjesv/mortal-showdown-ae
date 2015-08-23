package com.partlight.ms.scene;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.scene.Scene;
import org.andengine.util.modifier.IModifier;
import org.andengine.util.modifier.IModifier.IModifierListener;

import com.partlight.ms.resource.EnvironmentVars;

public class DelayScene extends DialogScene {

	private final Scene		sNewScene;
	private float			duration;
	private final Runnable	rAlsoRun;

	public DelayScene(float duration, Scene to, Runnable alsoRun) {
		this.sNewScene = to;
		this.rAlsoRun = alsoRun;
	}

	@Override
	public void onEngineDrawError() {

	}

	@Override
	public void onEngineUpdateError() {

	}

	public void start() {
		final DelayModifier DELAY = new DelayModifier(this.duration);

		DELAY.addModifierListener(new IModifierListener<IEntity>() {

			@Override
			public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
				EnvironmentVars.MAIN_CONTEXT.getEngine().setScene(DelayScene.this.sNewScene);
				if (DelayScene.this.rAlsoRun != null)
					DelayScene.this.rAlsoRun.run();
			}

			@Override
			public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
			}
		});
		DELAY.setAutoUnregisterWhenFinished(true);
		this.registerEntityModifier(DELAY);
	}

}
