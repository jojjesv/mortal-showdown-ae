package com.partlight.ms.shader;

import org.andengine.opengl.shader.PositionTextureCoordinatesShaderProgram;
import org.andengine.opengl.shader.ShaderProgram;
import org.andengine.opengl.shader.constants.ShaderProgramConstants;
import org.andengine.opengl.shader.exception.ShaderProgramException;
import org.andengine.opengl.shader.exception.ShaderProgramLinkException;
import org.andengine.opengl.util.GLState;
import org.andengine.opengl.vbo.attribute.VertexBufferObjectAttributes;

import android.opengl.GLES20;

public class OpaqueShaderProgram extends ShaderProgram {

	private static final String UNIFORM_ALPHA = "u_alpha";

	//@formatter:off
	private static final String FRAGMENT_SHADER = String.format(
			"precision mediump float;\n" +
			"uniform mediump float %s;\n" +
			"uniform sampler2D %s;\n" + 
			"varying mediump vec2 %s;\n" +
			"\n" +
			"void main()\n" +
			"{\n" + 
			"	gl_FragColor = texture2D(%s, %s);\n" +
			"	gl_FragColor = mix(gl_FragColor, vec4(0.0, 0.0, 0.0, 1.0), 1.0 - gl_FragColor.a);\n" +
			"	gl_FragColor.a = %s;\n" +
			"}\n",
			OpaqueShaderProgram.UNIFORM_ALPHA,
			ShaderProgramConstants.UNIFORM_TEXTURE_0,
			ShaderProgramConstants.VARYING_TEXTURECOORDINATES,
			ShaderProgramConstants.UNIFORM_TEXTURE_0,
			ShaderProgramConstants.VARYING_TEXTURECOORDINATES,
			OpaqueShaderProgram.UNIFORM_ALPHA);
	//@formatter:on

	private int		u_projectionMatrixLocation	= ShaderProgramConstants.LOCATION_INVALID;
	private int		u_texture0Location			= ShaderProgramConstants.LOCATION_INVALID;
	private int		u_alphaLocation				= ShaderProgramConstants.LOCATION_INVALID;
	private float	alpha;

	public OpaqueShaderProgram() {
		super(PositionTextureCoordinatesShaderProgram.VERTEXSHADER, OpaqueShaderProgram.FRAGMENT_SHADER);
		this.alpha = 1f;
	}

	@Override
	public void bind(GLState pGLState, VertexBufferObjectAttributes pVertexBufferObjectAttributes) throws ShaderProgramException {

		super.bind(pGLState, pVertexBufferObjectAttributes);

		GLES20.glUniformMatrix4fv(this.u_projectionMatrixLocation, 1, false, pGLState.getModelViewProjectionGLMatrix(), 0);
		GLES20.glUniform1i(this.u_texture0Location, 0);
		GLES20.glUniform1f(this.u_alphaLocation, this.alpha);

	}

	public float getAlpha() {
		return this.alpha;
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

		this.u_projectionMatrixLocation = this.getUniformLocation(ShaderProgramConstants.UNIFORM_MODELVIEWPROJECTIONMATRIX);
		this.u_texture0Location = this.getUniformLocation(ShaderProgramConstants.UNIFORM_TEXTURE_0);
		this.u_alphaLocation = this.getUniformLocation(OpaqueShaderProgram.UNIFORM_ALPHA);

	}

	public void setAlpha(float alpha) {
		this.alpha = alpha;
	}
}