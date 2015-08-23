package com.partlight.ms.shader;

import org.andengine.opengl.shader.PositionColorTextureCoordinatesShaderProgram;
import org.andengine.opengl.shader.ShaderProgram;
import org.andengine.opengl.shader.constants.ShaderProgramConstants;
import org.andengine.opengl.shader.exception.ShaderProgramException;
import org.andengine.opengl.shader.exception.ShaderProgramLinkException;
import org.andengine.opengl.util.GLState;
import org.andengine.opengl.vbo.attribute.VertexBufferObjectAttributes;

import com.partlight.ms.util.script.FragmentShaderScripts;

import android.opengl.GLES20;

public class TintShaderProgram extends ShaderProgram {

	private static TintShaderProgram	tspNonMultipliedInstance;
	private static TintShaderProgram	tspMultipliedInstance;

	public static final String	UNIFORM_PERCENT		= "u_percent";
	public static final String	UNIFORM_MULTIPLY	= "u_multiply";

	public static TintShaderProgram getMultipliedInstance() {
		if (TintShaderProgram.tspMultipliedInstance == null)
			TintShaderProgram.tspMultipliedInstance = new TintShaderProgram(true);
		return TintShaderProgram.tspMultipliedInstance;
	}

	public static TintShaderProgram getNonMultipliedInstance() {
		if (TintShaderProgram.tspNonMultipliedInstance == null)
			TintShaderProgram.tspNonMultipliedInstance = new TintShaderProgram(false);
		return TintShaderProgram.tspNonMultipliedInstance;
	}

	private float	percent;
	private boolean	multiply;
	private int		u_multiplyLocation					= ShaderProgramConstants.LOCATION_INVALID;
	private int		u_modelViewProjectionMatrixLocation	= ShaderProgramConstants.LOCATION_INVALID;

	private int u_texture0Location = ShaderProgramConstants.LOCATION_INVALID;

	private int u_percentLocation = ShaderProgramConstants.LOCATION_INVALID;

	public TintShaderProgram() {
		this(false);
	}

	public TintShaderProgram(boolean multiplyColor) {
		super(PositionColorTextureCoordinatesShaderProgram.VERTEXSHADER, FragmentShaderScripts.SCRIPTS[FragmentShaderScripts.SCRIPT_TINT]);
		this.percent = 1f;
		this.setMultiplying(multiplyColor);
	}

	@Override
	public void bind(GLState pGLState, VertexBufferObjectAttributes pVertexBufferObjectAttributes) throws ShaderProgramException {

		super.bind(pGLState, pVertexBufferObjectAttributes);

		GLES20.glUniformMatrix4fv(this.u_modelViewProjectionMatrixLocation, 1, false, pGLState.getModelViewProjectionGLMatrix(), 0);
		GLES20.glUniform1i(this.u_texture0Location, 0);
		GLES20.glUniform1i(this.u_multiplyLocation, (this.multiply) ? 1 : 0);
		GLES20.glUniform1f(this.u_percentLocation, this.percent);
	}

	public float getPercent() {
		return this.percent;
	}

	public boolean isMultiplying() {
		return this.multiply;
	}

	@Override
	protected void link(GLState pGLState) throws ShaderProgramLinkException {

		GLES20.glBindAttribLocation(this.mProgramID, ShaderProgramConstants.ATTRIBUTE_POSITION_LOCATION,
				ShaderProgramConstants.ATTRIBUTE_POSITION);
		GLES20.glBindAttribLocation(this.mProgramID, ShaderProgramConstants.ATTRIBUTE_TEXTURECOORDINATES_LOCATION,
				ShaderProgramConstants.ATTRIBUTE_TEXTURECOORDINATES);
		GLES20.glBindAttribLocation(this.mProgramID, ShaderProgramConstants.ATTRIBUTE_COLOR_LOCATION,
				ShaderProgramConstants.ATTRIBUTE_COLOR);

		super.link(pGLState);

		this.u_modelViewProjectionMatrixLocation = this.getUniformLocation(ShaderProgramConstants.UNIFORM_MODELVIEWPROJECTIONMATRIX);
		this.u_multiplyLocation = this.getUniformLocation(TintShaderProgram.UNIFORM_MULTIPLY);
		this.u_texture0Location = this.getUniformLocation(ShaderProgramConstants.UNIFORM_TEXTURE_0);
		this.u_percentLocation = this.getUniformLocation(TintShaderProgram.UNIFORM_PERCENT);
	}

	public void setMultiplying(boolean multiply) {
		this.multiply = multiply;
	}

	public void setTintPercent(float percent) {
		this.percent = percent;
	}
}
