package com.partlight.ms.entity;

import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.partlight.ms.resource.EnvironmentVars;
import com.partlight.ms.resource.ResourceManager.MainMenuRegions;
import com.partlight.ms.util.TextureManagedSprite;

public class LoadingSprite extends TextureManagedSprite {

	public LoadingSprite(float x, float y, VertexBufferObjectManager pVertexBufferObjectManager) {
		super(x, y, MainMenuRegions.region_loading, pVertexBufferObjectManager);
		this.onManagedUpdate(0);
	}

	@Override
	protected void onManagedUpdate(float pSecondsElapsed) {
		super.onManagedUpdate(pSecondsElapsed);
		super.setRotation(EnvironmentVars.MAIN_CONTEXT.getEngine().getSecondsElapsedTotal() * 480);
	}

}
