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

/**
 * @author Johan Svensson - partLight Entertainment
 * 
 */
public class RadialGradientShaderProgram extends ShaderProgram {

	private static RadialGradientShaderProgram rgspInstance;

	public static RadialGradientShaderProgram getInstance() {
		if (RadialGradientShaderProgram.rgspInstance == null)
			RadialGradientShaderProgram.rgspInstance = new RadialGradientShaderProgram();
		return RadialGradientShaderProgram.rgspInstance;
	}

	private int u_projectionMatrixLocation = ShaderProgramConstants.LOCATION_INVALID;

	private RadialGradientShaderProgram() {
		super(PositionColorTextureCoordinatesShaderProgram.VERTEXSHADER,
				FragmentShaderScripts.SCRIPTS[FragmentShaderScripts.SCRIPT_RADIAL_GRADIENT]);
	}

	@Override
	public void bind(GLState pGLState, VertexBufferObjectAttributes pVertexBufferObjectAttributes) throws ShaderProgramException {
		super.bind(pGLState, pVertexBufferObjectAttributes);
		GLES20.glUniformMatrix4fv(this.u_projectionMatrixLocation, 1, false, pGLState.getModelViewProjectionGLMatrix(), 0);
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
	}
}
