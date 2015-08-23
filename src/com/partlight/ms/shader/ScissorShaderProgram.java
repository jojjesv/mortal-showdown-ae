package com.partlight.ms.shader;

import org.andengine.opengl.shader.PositionColorTextureCoordinatesShaderProgram;
import org.andengine.opengl.shader.ShaderProgram;
import org.andengine.opengl.shader.constants.ShaderProgramConstants;
import org.andengine.opengl.shader.exception.ShaderProgramException;
import org.andengine.opengl.shader.exception.ShaderProgramLinkException;
import org.andengine.opengl.util.GLState;
import org.andengine.opengl.vbo.attribute.VertexBufferObjectAttributes;

import android.opengl.GLES20;

public class ScissorShaderProgram extends ShaderProgram {

	//@formatter:on
	public static final String UNIFORM_PERCENT = "u_percent";

	// @formatter:off
    private static final String FRAGMENT_SHADER = String.format(
	"precision mediump float;\n" +
	"uniform float %s;\n" +
	"uniform sampler2D %s;\n" + 
	"varying mediump vec2 %s;\n" + 
	"varying lowp vec4 %s;\n" +
	"vec4 src_color;\n" +
	"\n" +
	"void main()\n" +
	"{\n" + 
	"	gl_FragColor = texture2D(%s, %s);\n" +
	"	gl_FragColor.a *= %s.a;\n" +
	"	if (%s.x >= %s)" +
	"	{" +
	"		gl_FragColor.a = 0.0;" +
	"	}" +
	"}\n",
	ScissorShaderProgram.UNIFORM_PERCENT,
	ShaderProgramConstants.UNIFORM_TEXTURE_0,
	ShaderProgramConstants.VARYING_TEXTURECOORDINATES,
	ShaderProgramConstants.VARYING_COLOR,
	ShaderProgramConstants.UNIFORM_TEXTURE_0,
	ShaderProgramConstants.VARYING_TEXTURECOORDINATES,
	ShaderProgramConstants.VARYING_COLOR,
	ShaderProgramConstants.VARYING_TEXTURECOORDINATES,
	ScissorShaderProgram.UNIFORM_PERCENT);

	private float percent;

	private int u_percentLocation = ShaderProgramConstants.LOCATION_INVALID;

    //@formatter:on
	public ScissorShaderProgram() {
		this(0f);
	}

	public ScissorShaderProgram(float percent) {
		super(PositionColorTextureCoordinatesShaderProgram.VERTEXSHADER, ScissorShaderProgram.FRAGMENT_SHADER);
		this.percent = percent;
	}

	@Override
	public void bind(GLState pGLState, VertexBufferObjectAttributes pVertexBufferObjectAttributes) throws ShaderProgramException {

		super.bind(pGLState, pVertexBufferObjectAttributes);

		GLES20.glUniformMatrix4fv(PositionColorTextureCoordinatesShaderProgram.sUniformModelViewPositionMatrixLocation, 1, false,
				pGLState.getModelViewProjectionGLMatrix(), 0);
		GLES20.glUniform1i(PositionColorTextureCoordinatesShaderProgram.sUniformTexture0Location, 0);
		GLES20.glUniform1f(this.u_percentLocation, this.percent);

	}

	public float getPercent() {
		return this.percent;
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

		PositionColorTextureCoordinatesShaderProgram.sUniformModelViewPositionMatrixLocation = this
				.getUniformLocation(ShaderProgramConstants.UNIFORM_MODELVIEWPROJECTIONMATRIX);
		PositionColorTextureCoordinatesShaderProgram.sUniformTexture0Location = this
				.getUniformLocation(ShaderProgramConstants.UNIFORM_TEXTURE_0);
		this.u_percentLocation = this.getUniformLocation(ScissorShaderProgram.UNIFORM_PERCENT);

	}

	public void setPercent(float percent) {
		this.percent = percent;
	}
}
