package com.partlight.ms.session.character.listener;

import com.partlight.ms.session.character.Character;

public interface MovementListener {
	public void onStartedMoving(Character character);

	public void onStoppedMoving(Character character);
}
