package com.partlight.ms.entity;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.entity.text.vbo.ITextVertexBufferObject;
import org.andengine.opengl.font.IFont;
import org.andengine.opengl.shader.ShaderProgram;
import org.andengine.opengl.util.GLState;
import org.andengine.opengl.vbo.DrawType;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class ShadowedText extends Text {

	public static String getPlaceholderString(int charCount) {
		String out = "";
		for (int i = 0; i < charCount; i++)
			out += "X";
		return out;
	}

	private boolean isShowingShadow = true;

	public ShadowedText(float pX, float pY, IFont pFont, CharSequence pText, int pCharactersMaximum, TextOptions pTextOptions,
			ITextVertexBufferObject pTextVertexBufferObject) {
		super(pX, pY, pFont, pText, pCharactersMaximum, pTextOptions, pTextVertexBufferObject);
	}

	public ShadowedText(float pX, float pY, IFont pFont, CharSequence pText, int pCharactersMaximum, TextOptions pTextOptions,
			ITextVertexBufferObject pTextVertexBufferObject, ShaderProgram pShaderProgram) {
		super(pX, pY, pFont, pText, pCharactersMaximum, pTextOptions, pTextVertexBufferObject, pShaderProgram);
	}

	public ShadowedText(float pX, float pY, IFont pFont, CharSequence pText, int pCharactersMaximum, TextOptions pTextOptions,
			VertexBufferObjectManager pVertexBufferObjectManager) {
		super(pX, pY, pFont, pText, pCharactersMaximum, pTextOptions, pVertexBufferObjectManager);
	}

	public ShadowedText(float pX, float pY, IFont pFont, CharSequence pText, int pCharactersMaximum, TextOptions pTextOptions,
			VertexBufferObjectManager pVertexBufferObjectManager, DrawType pDrawType) {
		super(pX, pY, pFont, pText, pCharactersMaximum, pTextOptions, pVertexBufferObjectManager, pDrawType);
	}

	public ShadowedText(float pX, float pY, IFont pFont, CharSequence pText, int pCharactersMaximum, TextOptions pTextOptions,
			VertexBufferObjectManager pVertexBufferObjectManager, DrawType pDrawType, ShaderProgram pShaderProgram) {
		super(pX, pY, pFont, pText, pCharactersMaximum, pTextOptions, pVertexBufferObjectManager, pDrawType, pShaderProgram);
	}

	public ShadowedText(float pX, float pY, IFont pFont, CharSequence pText, int pCharactersMaximum,
			VertexBufferObjectManager pVertexBufferObjectManager) {
		super(pX, pY, pFont, pText, pCharactersMaximum, pVertexBufferObjectManager);
	}

	public ShadowedText(float pX, float pY, IFont pFont, CharSequence pText, int pCharactersMaximum,
			VertexBufferObjectManager pVertexBufferObjectManager, DrawType pDrawType) {
		super(pX, pY, pFont, pText, pCharactersMaximum, pVertexBufferObjectManager, pDrawType);
	}

	public ShadowedText(float pX, float pY, IFont pFont, CharSequence pText, int pCharactersMaximum,
			VertexBufferObjectManager pVertexBufferObjectManager, DrawType pDrawType, ShaderProgram pShaderProgram) {
		super(pX, pY, pFont, pText, pCharactersMaximum, pVertexBufferObjectManager, pDrawType, pShaderProgram);
	}

	public ShadowedText(float pX, float pY, IFont pFont, CharSequence pText, int pCharactersMaximum,
			VertexBufferObjectManager pVertexBufferObjectManager, ShaderProgram pShaderProgram) {
		super(pX, pY, pFont, pText, pCharactersMaximum, pVertexBufferObjectManager, pShaderProgram);
	}

	public ShadowedText(float pX, float pY, IFont pFont, CharSequence pText, TextOptions pTextOptions,
			VertexBufferObjectManager pVertexBufferObjectManager) {
		super(pX, pY, pFont, pText, pTextOptions, pVertexBufferObjectManager);
	}

	public ShadowedText(float pX, float pY, IFont pFont, CharSequence pText, TextOptions pTextOptions,
			VertexBufferObjectManager pVertexBufferObjectManager, DrawType pDrawType) {
		super(pX, pY, pFont, pText, pTextOptions, pVertexBufferObjectManager, pDrawType);
	}

	public ShadowedText(float pX, float pY, IFont pFont, CharSequence pText, TextOptions pTextOptions,
			VertexBufferObjectManager pVertexBufferObjectManager, DrawType pDrawType, ShaderProgram pShaderProgram) {
		super(pX, pY, pFont, pText, pTextOptions, pVertexBufferObjectManager, pDrawType, pShaderProgram);
	}

	public ShadowedText(float pX, float pY, IFont pFont, CharSequence pText, TextOptions pTextOptions,
			VertexBufferObjectManager pVertexBufferObjectManager, ShaderProgram pShaderProgram) {
		super(pX, pY, pFont, pText, pTextOptions, pVertexBufferObjectManager, pShaderProgram);
	}

	public ShadowedText(float pX, float pY, IFont pFont, CharSequence pText, VertexBufferObjectManager pVertexBufferObjectManager) {
		super(pX, pY, pFont, pText, pVertexBufferObjectManager);
	}

	public ShadowedText(float pX, float pY, IFont pFont, CharSequence pText, VertexBufferObjectManager pVertexBufferObjectManager,
			DrawType pDrawType) {
		super(pX, pY, pFont, pText, pVertexBufferObjectManager, pDrawType);
	}

	public ShadowedText(float pX, float pY, IFont pFont, CharSequence pText, VertexBufferObjectManager pVertexBufferObjectManager,
			DrawType pDrawType, ShaderProgram pShaderProgram) {
		super(pX, pY, pFont, pText, pVertexBufferObjectManager, pDrawType, pShaderProgram);
	}

	public ShadowedText(float pX, float pY, IFont pFont, CharSequence pText, VertexBufferObjectManager pVertexBufferObjectManager,
			ShaderProgram pShaderProgram) {
		super(pX, pY, pFont, pText, pVertexBufferObjectManager, pShaderProgram);
	}

	public void hideShadow() {
		this.isShowingShadow = false;
	}

	@Override
	protected void onManagedDraw(GLState pGLState, Camera pCamera) {
		if (!this.isShowingShadow) {
			super.onManagedDraw(pGLState, pCamera);
			return;
		}

		final float COLOR_R = this.getRed();
		final float COLOR_G = this.getGreen();
		final float COLOR_B = this.getBlue();
		final float COLOR_A = this.getAlpha();

		final float OFFSET_X = this.getScaleX();
		final float OFFSET_Y = this.getScaleY();

		{
			this.mX += OFFSET_X;
			this.mY += OFFSET_Y;
			this.setColor(0, 0, 0, COLOR_A);
			super.onManagedDraw(pGLState, pCamera);
		}
		{
			this.mX -= OFFSET_X;
			this.mY -= OFFSET_Y;
			this.setColor(COLOR_R, COLOR_G, COLOR_B, COLOR_A);
			super.onManagedDraw(pGLState, pCamera);
		}
	}

	public void showShadow() {
		this.isShowingShadow = true;
	}
}
