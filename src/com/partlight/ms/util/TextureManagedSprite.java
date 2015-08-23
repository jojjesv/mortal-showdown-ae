package com.partlight.ms.util;

import java.util.HashMap;

import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.vbo.ISpriteVertexBufferObject;
import org.andengine.opengl.shader.ShaderProgram;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.DrawType;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class TextureManagedSprite extends Sprite {

	private static final HashMap<ITexture, Integer> TEXTURE_USAGE_MAP;

	static {
		TEXTURE_USAGE_MAP = new HashMap<ITexture, Integer>();
	}

	public TextureManagedSprite(float pX, float pY, float pWidth, float pHeight, ITextureRegion pTextureRegion,
			ISpriteVertexBufferObject pSpriteVertexBufferObject) {
		super(pX, pY, pWidth, pHeight, pTextureRegion, pSpriteVertexBufferObject);
	}

	public TextureManagedSprite(float pX, float pY, float pWidth, float pHeight, ITextureRegion pTextureRegion,
			ISpriteVertexBufferObject pSpriteVertexBufferObject, ShaderProgram pShaderProgram) {
		super(pX, pY, pWidth, pHeight, pTextureRegion, pSpriteVertexBufferObject, pShaderProgram);
	}

	public TextureManagedSprite(float pX, float pY, float pWidth, float pHeight, ITextureRegion pTextureRegion,
			VertexBufferObjectManager pVertexBufferObjectManager) {
		super(pX, pY, pWidth, pHeight, pTextureRegion, pVertexBufferObjectManager);
	}

	public TextureManagedSprite(float pX, float pY, float pWidth, float pHeight, ITextureRegion pTextureRegion,
			VertexBufferObjectManager pVertexBufferObjectManager, DrawType pDrawType) {
		super(pX, pY, pWidth, pHeight, pTextureRegion, pVertexBufferObjectManager, pDrawType);
	}

	public TextureManagedSprite(float pX, float pY, float pWidth, float pHeight, ITextureRegion pTextureRegion,
			VertexBufferObjectManager pVertexBufferObjectManager, DrawType pDrawType, ShaderProgram pShaderProgram) {
		super(pX, pY, pWidth, pHeight, pTextureRegion, pVertexBufferObjectManager, pDrawType, pShaderProgram);
	}

	public TextureManagedSprite(float pX, float pY, float pWidth, float pHeight, ITextureRegion pTextureRegion,
			VertexBufferObjectManager pVertexBufferObjectManager, ShaderProgram pShaderProgram) {
		super(pX, pY, pWidth, pHeight, pTextureRegion, pVertexBufferObjectManager, pShaderProgram);
	}

	public TextureManagedSprite(float pX, float pY, ITextureRegion pTextureRegion, ISpriteVertexBufferObject pVertexBufferObject) {
		super(pX, pY, pTextureRegion, pVertexBufferObject);
	}

	public TextureManagedSprite(float pX, float pY, ITextureRegion pTextureRegion, ISpriteVertexBufferObject pVertexBufferObject,
			ShaderProgram pShaderProgram) {
		super(pX, pY, pTextureRegion, pVertexBufferObject, pShaderProgram);
	}

	public TextureManagedSprite(float pX, float pY, ITextureRegion pTextureRegion, VertexBufferObjectManager pVertexBufferObjectManager) {
		super(pX, pY, pTextureRegion, pVertexBufferObjectManager);
	}

	public TextureManagedSprite(float pX, float pY, ITextureRegion pTextureRegion, VertexBufferObjectManager pVertexBufferObjectManager,
			DrawType pDrawType) {
		super(pX, pY, pTextureRegion, pVertexBufferObjectManager, pDrawType);
	}

	public TextureManagedSprite(float pX, float pY, ITextureRegion pTextureRegion, VertexBufferObjectManager pVertexBufferObjectManager,
			DrawType pDrawType, ShaderProgram pShaderProgram) {
		super(pX, pY, pTextureRegion, pVertexBufferObjectManager, pDrawType, pShaderProgram);
	}

	public TextureManagedSprite(float pX, float pY, ITextureRegion pTextureRegion, VertexBufferObjectManager pVertexBufferObjectManager,
			ShaderProgram pShaderProgram) {
		super(pX, pY, pTextureRegion, pVertexBufferObjectManager, pShaderProgram);
	}

	@Override
	public void onAttached() {
		final ITexture TEXTURE = this.mTextureRegion.getTexture();
		final Integer USING_THIS_TEXTURE = TextureManagedSprite.TEXTURE_USAGE_MAP.get(TEXTURE);

		if (USING_THIS_TEXTURE == null) {
			TextureManagedSprite.TEXTURE_USAGE_MAP.put(TEXTURE, Integer.valueOf(1));
			TEXTURE.load();
		} else
			TextureManagedSprite.TEXTURE_USAGE_MAP.put(TEXTURE, Integer.valueOf(USING_THIS_TEXTURE.intValue() + 1));
	}

	@Override
	public void onDetached() {
		final ITexture TEXTURE = this.mTextureRegion.getTexture();
		final Integer USING_THIS_TEXTURE = TextureManagedSprite.TEXTURE_USAGE_MAP.get(TEXTURE);

		if (USING_THIS_TEXTURE == 1) {
			TextureManagedSprite.TEXTURE_USAGE_MAP.remove(TEXTURE);
			TEXTURE.unload();
		} else
			TextureManagedSprite.TEXTURE_USAGE_MAP.put(TEXTURE, Integer.valueOf(USING_THIS_TEXTURE.intValue() - 1));
	}
}
