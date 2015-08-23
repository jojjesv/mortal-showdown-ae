package com.partlight.ms.session.character.player.libraries;

import org.andengine.entity.sprite.AnimatedSprite;

import com.partlight.ms.resource.EnvironmentVars;
import com.partlight.ms.resource.ResourceManager.PlayerRegions;

public final class PlayerArms {

	public static final AnimatedSprite	ARMS_PISTOL;
	public static final AnimatedSprite	ARMS_SMG;
	public static final AnimatedSprite	ARMS_SHOTGUN;
	public static final AnimatedSprite	ARMS_SNIPER;
	public static final AnimatedSprite	ARMS_UNARMED;

	static {
		ARMS_PISTOL = new AnimatedSprite(0, 0, PlayerRegions.region_a01, EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager());
		ARMS_SMG = new AnimatedSprite(0, 0, PlayerRegions.region_a02, EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager());
		ARMS_SHOTGUN = new AnimatedSprite(0, 0, PlayerRegions.region_a03, EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager());
		ARMS_SNIPER = new AnimatedSprite(0, 0, PlayerRegions.region_a04, EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager());
		ARMS_UNARMED = new AnimatedSprite(0, 0, PlayerRegions.region_a05, EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager());
	}
}
