package com.partlight.ms.shader;

import org.andengine.opengl.shader.PositionColorTextureCoordinatesShaderProgram;
import org.andengine.opengl.shader.ShaderProgram;
import org.andengine.opengl.shader.constants.ShaderProgramConstants;
import org.andengine.opengl.shader.exception.ShaderProgramException;
import org.andengine.opengl.shader.exception.ShaderProgramLinkException;
import org.andengine.opengl.util.GLState;
import org.andengine.opengl.vbo.attribute.VertexBufferObjectAttributes;
import org.andengine.util.color.Color;

import com.partlight.ms.util.script.FragmentShaderScripts;

import android.opengl.GLES20;

/**
 * @author Johan Svensson - partLight Entertainment
 * 
 */
public class LinearGradientShaderProgram extends ShaderProgram {

	public static final int	DIRECTION_TOP_TO_BOTTOM	= 0;
	public static final int	DIRECTION_BOTTOM_TO_TOP	= 1;
	public static final int	DIRECTION_LEFT_TO_RIGHT	= 2;
	public static final int	DIRECTION_RIGHT_TO_LEFT	= 3;

	public static final String	UNIFORM_COLOR_0		= "u_color_0";
	public static final String	UNIFORM_COLOR_1		= "u_color_1";
	public static final String	UNIFORM_DIRECTION	= "u_direction";

	private final Color[]	COLORS;
	private int				direction;

	private int	u_directionLocation			= ShaderProgramConstants.LOCATION_INVALID;
	private int	u_color0Location			= ShaderProgramConstants.LOCATION_INVALID;
	private int	u_color1Location			= ShaderProgramConstants.LOCATION_INVALID;
	private int	u_projectionMatrixLocation	= ShaderProgramConstants.LOCATION_INVALID;

	public LinearGradientShaderProgram(Color color0, Color color1, int direction) {
		super(PositionColorTextureCoordinatesShaderProgram.VERTEXSHADER,
				FragmentShaderScripts.SCRIPTS[FragmentShaderScripts.SCRIPT_LINEAR_GRADIENT]);
		this.COLORS = new Color[2];
		this.COLORS[0] = color0;
		this.COLORS[1] = color1;
		this.direction = direction;
	}

	@Override
	public void bind(GLState pGLState, VertexBufferObjectAttributes pVertexBufferObjectAttributes) throws ShaderProgramException {

		super.bind(pGLState, pVertexBufferObjectAttributes);

		GLES20.glUniformMatrix4fv(this.u_projectionMatrixLocation, 1, false, pGLState.getModelViewProjectionGLMatrix(), 0);
		GLES20.glUniform1i(this.u_directionLocation, this.direction);
		GLES20.glUniform4f(this.u_color0Location, this.COLORS[0].getRed(), this.COLORS[0].getGreen(), this.COLORS[0].getBlue(),
				this.COLORS[0].getAlpha());
		GLES20.glUniform4f(this.u_color1Location, this.COLORS[1].getRed(), this.COLORS[1].getGreen(), this.COLORS[1].getBlue(),
				this.COLORS[1].getAlpha());
	}

	public Color[] getColors() {
		return this.COLORS;
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
		this.u_color0Location = this.getUniformLocation(LinearGradientShaderProgram.UNIFORM_COLOR_0);
		this.u_color1Location = this.getUniformLocation(LinearGradientShaderProgram.UNIFORM_COLOR_1);
		this.u_directionLocation = this.getUniformLocation(LinearGradientShaderProgram.UNIFORM_DIRECTION);
	}

	public void setColors(Color color0, Color color1) {
		this.COLORS[0] = color0;
		this.COLORS[1] = color1;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}
}
