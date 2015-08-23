package com.partlight.ms.shader;

import org.andengine.entity.shape.RectangularShape;
import org.andengine.opengl.shader.PositionColorTextureCoordinatesShaderProgram;
import org.andengine.opengl.shader.ShaderProgram;
import org.andengine.opengl.shader.constants.ShaderProgramConstants;
import org.andengine.opengl.shader.exception.ShaderProgramException;
import org.andengine.opengl.shader.exception.ShaderProgramLinkException;
import org.andengine.opengl.util.GLState;
import org.andengine.opengl.vbo.attribute.VertexBufferObjectAttributes;

import android.opengl.GLES20;

public class BlurShaderProgram extends ShaderProgram {

	public static final String UNIFORM_ENTITY_DIMENSIONS = "u_entityDim";

	//@formatter:off
	private static final String FRAGMENT_SHADER = String.format(
			"precision mediump float;\n" +
			"uniform vec2 %s;\n" + 
			"uniform sampler2D %s;\n" + 
			"varying mediump vec2 %s;\n" + 
			"varying lowp vec4 %s;\n" +
			"vec4 src_color[9];\n" +
			"\n" +
			"void main()\n" +
			"{\n" + 
			"	float y_pixel = 1.0 / %s.y;" +
			"	float x_pixel = 1.0 / %s.x;" +
			"	src_color[0] = texture2D(%s, %s + vec2(-x_pixel, -y_pixel));\n" +
			"	src_color[1] = texture2D(%s, %s + vec2(0.0, -y_pixel));\n" +
			"	src_color[2] = texture2D(%s, %s + vec2(x_pixel, -y_pixel));\n" +
			"	src_color[3] = texture2D(%s, %s + vec2(-x_pixel, 0.0));\n" +
			"	src_color[4] = texture2D(%s, %s + vec2(0.0, 0.0));\n" +
			"	src_color[5] = texture2D(%s, %s + vec2(x_pixel, 0.0));\n" +
			"	src_color[6] = texture2D(%s, %s + vec2(-x_pixel, y_pixel));\n" +
			"	src_color[7] = texture2D(%s, %s + vec2(0.0, y_pixel));\n" +
			"	src_color[8] = texture2D(%s, %s + vec2(x_pixel, y_pixel));\n" +
			"	for (int i = 0; i < 9; i++)\n" +
			"	{" +
			"		gl_FragColor += src_color[i];" +
			"	}" +
			"	gl_FragColor /= 9.0;\n" +
			"}\n",
			BlurShaderProgram.UNIFORM_ENTITY_DIMENSIONS,
			ShaderProgramConstants.UNIFORM_TEXTURE_0,
			ShaderProgramConstants.VARYING_TEXTURECOORDINATES,
			ShaderProgramConstants.VARYING_COLOR,
			BlurShaderProgram.UNIFORM_ENTITY_DIMENSIONS,
			BlurShaderProgram.UNIFORM_ENTITY_DIMENSIONS,
			ShaderProgramConstants.UNIFORM_TEXTURE_0,
			ShaderProgramConstants.VARYING_TEXTURECOORDINATES,
			ShaderProgramConstants.UNIFORM_TEXTURE_0,
			ShaderProgramConstants.VARYING_TEXTURECOORDINATES,
			ShaderProgramConstants.UNIFORM_TEXTURE_0,
			ShaderProgramConstants.VARYING_TEXTURECOORDINATES,
			ShaderProgramConstants.UNIFORM_TEXTURE_0,
			ShaderProgramConstants.VARYING_TEXTURECOORDINATES,
			ShaderProgramConstants.UNIFORM_TEXTURE_0,
			ShaderProgramConstants.VARYING_TEXTURECOORDINATES,
			ShaderProgramConstants.UNIFORM_TEXTURE_0,
			ShaderProgramConstants.VARYING_TEXTURECOORDINATES,
			ShaderProgramConstants.UNIFORM_TEXTURE_0,
			ShaderProgramConstants.VARYING_TEXTURECOORDINATES,
			ShaderProgramConstants.UNIFORM_TEXTURE_0,
			ShaderProgramConstants.VARYING_TEXTURECOORDINATES,
			ShaderProgramConstants.UNIFORM_TEXTURE_0,
			ShaderProgramConstants.VARYING_TEXTURECOORDINATES);
	//@formatter:on

	private int u_EntityDimLocation;

	private final float	entityWidth;
	private final float	entityHeight;

	public BlurShaderProgram(float entityWidth, float entityHeight) {
		super(PositionColorTextureCoordinatesShaderProgram.VERTEXSHADER, BlurShaderProgram.FRAGMENT_SHADER);

		this.entityWidth = entityWidth;
		this.entityHeight = entityHeight;
	}

	public BlurShaderProgram(RectangularShape rectangle) {
		this(rectangle.getWidthScaled(), rectangle.getHeightScaled());
	}

	@Override
	public void bind(GLState pGLState, VertexBufferObjectAttributes pVertexBufferObjectAttributes) throws ShaderProgramException {
		super.bind(pGLState, pVertexBufferObjectAttributes);

		GLES20.glUniformMatrix4fv(PositionColorTextureCoordinatesShaderProgram.sUniformModelViewPositionMatrixLocation, 1, false,
				pGLState.getModelViewProjectionGLMatrix(), 0);
		GLES20.glUniform1i(PositionColorTextureCoordinatesShaderProgram.sUniformTexture0Location, 0);
		GLES20.glUniform2f(this.u_EntityDimLocation, this.entityWidth, this.entityHeight);
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

		this.u_EntityDimLocation = this.getUniformLocation(BlurShaderProgram.UNIFORM_ENTITY_DIMENSIONS);

		PositionColorTextureCoordinatesShaderProgram.sUniformModelViewPositionMatrixLocation = this
				.getUniformLocation(ShaderProgramConstants.UNIFORM_MODELVIEWPROJECTIONMATRIX);
		PositionColorTextureCoordinatesShaderProgram.sUniformTexture0Location = this
				.getUniformLocation(ShaderProgramConstants.UNIFORM_TEXTURE_0);
	}
}
