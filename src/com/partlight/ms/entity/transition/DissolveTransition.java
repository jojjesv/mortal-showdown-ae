package com.partlight.ms.entity.transition;

import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.color.Color;

import com.partlight.ms.entity.DissolveAnimatedSprite;
import com.partlight.ms.resource.EnvironmentVars;
import com.partlight.ms.resource.ResourceManager;
import com.partlight.ms.resource.ResourceManager.MiscRegions;
import com.partlight.ms.shader.DissolveShaderProgram;

public class DissolveTransition extends DissolveAnimatedSprite {

	public DissolveTransition(ITexture dissolveMap, VertexBufferObjectManager vertexBufferObjectManager) {
		super(0f, 0f, MiscRegions.region_empty, dissolveMap, vertexBufferObjectManager);

		((DissolveShaderProgram) this.getShaderProgram()).setUsingTexture(false);

		this.setColor(Color.BLACK);
		this.setScaleCenter(0, 0);
		this.setScale(EnvironmentVars.MAIN_CONTEXT.width(), EnvironmentVars.MAIN_CONTEXT.height());
	}

	public DissolveTransition(VertexBufferObjectManager vertexBufferObjectManager) {
		this(ResourceManager.btDissolveMap, vertexBufferObjectManager);
	}
}
