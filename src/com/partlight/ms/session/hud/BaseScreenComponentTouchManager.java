package com.partlight.ms.session.hud;

import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.input.touch.TouchEvent;

public class BaseScreenComponentTouchManager implements IOnSceneTouchListener {

	private BaseScreenComponent[] hbHudButtons;

	public BaseScreenComponentTouchManager(BaseScreenComponent... components) {
		this.setComponents(components);
	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {

		for (final BaseScreenComponent button : this.hbHudButtons)
			button.handleInput(pSceneTouchEvent);

		return true;
	}

	public void setComponents(BaseScreenComponent... components) {
		this.hbHudButtons = components;
	}

}
