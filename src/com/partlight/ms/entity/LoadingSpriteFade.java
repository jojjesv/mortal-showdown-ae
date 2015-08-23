package com.partlight.ms.entity;

import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.partlight.ms.resource.EnvironmentVars;
import com.partlight.ms.util.Fade;

public class LoadingSpriteFade extends Fade {

	private final LoadingSprite lsLoadingSprite;

	public LoadingSpriteFade(VertexBufferObjectManager pVertexBufferObjectManager) {
		super(pVertexBufferObjectManager);
		this.lsLoadingSprite = new LoadingSprite(this.getWidthScaled() - 64, this.getHeightScaled() - 64,
				EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager());
		this.attachChild(this.lsLoadingSprite);
	}
}
