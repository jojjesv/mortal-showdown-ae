package com.partlight.ms.shader;

import org.andengine.opengl.shader.PositionColorTextureCoordinatesShaderProgram;
import org.andengine.opengl.shader.ShaderProgram;
import org.andengine.opengl.shader.constants.ShaderProgramConstants;
import org.andengine.opengl.shader.exception.ShaderProgramException;
import org.andengine.opengl.shader.exception.ShaderProgramLinkException;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.util.GLState;
import org.andengine.opengl.vbo.attribute.VertexBufferObjectAttributes;

import com.partlight.ms.util.script.FragmentShaderScripts;

import android.opengl.GLES20;

public class DissolveShaderProgram extends ShaderProgram {

	public static final String	UNIFORM_RATIO		= "u_ratio";
	public static final String	UNIFORM_USETEXTURE	= "u_useTexture";
	public static final String	UNIFORM_USEINVERT	= "u_useInvert";

	private static int	u_modelViewProjectionMatrixLocation	= ShaderProgramConstants.LOCATION_INVALID;
	private static int	u_texture0Location					= ShaderProgramConstants.LOCATION_INVALID;
	private static int	u_texture1Location					= ShaderProgramConstants.LOCATION_INVALID;
	private static int	u_ratioLocation						= ShaderProgramConstants.LOCATION_INVALID;
	private static int	u_useTextureLocation				= ShaderProgramConstants.LOCATION_INVALID;
	private static int	u_useInvertLocation					= ShaderProgramConstants.LOCATION_INVALID;

	private final ITexture	tDissolveMap;
	private float			ratio;
	private int				activeTextureId;
	private boolean			useTexture;
	private boolean			invert;

	public DissolveShaderProgram(ITexture dissolveMap) {
		this(dissolveMap, 1);
	}

	public DissolveShaderProgram(ITexture dissolveMap, int activeTextureId) {
		this(dissolveMap, activeTextureId, true);
	}

	public DissolveShaderProgram(ITexture dissolveMap, int activeTextureId, boolean useTexture) {
		super(PositionColorTextureCoordinatesShaderProgram.VERTEXSHADER,
				FragmentShaderScripts.SCRIPTS[FragmentShaderScripts.SCRIPT_DISSOLVE]);
		this.tDissolveMap = dissolveMap;
		this.activeTextureId = activeTextureId;
		this.useTexture = useTexture;
	}

	@Override
	public void bind(GLState pGLState, VertexBufferObjectAttributes pVertexBufferObjectAttributes) throws ShaderProgramException {
		super.bind(pGLState, pVertexBufferObjectAttributes);

		GLES20.glUniformMatrix4fv(DissolveShaderProgram.u_modelViewProjectionMatrixLocation, 1, false,
				pGLState.getModelViewProjectionGLMatrix(), 0);
		GLES20.glUniform1i(DissolveShaderProgram.u_texture0Location, 0);
		GLES20.glUniform1i(DissolveShaderProgram.u_texture1Location, this.activeTextureId);
		GLES20.glUniform1f(DissolveShaderProgram.u_ratioLocation, this.ratio);
		GLES20.glUniform1i(DissolveShaderProgram.u_useTextureLocation, (this.useTexture) ? 1 : 0);
		GLES20.glUniform1i(DissolveShaderProgram.u_useInvertLocation, (this.invert) ? 1 : 0);
	}

	public int getActiveTextureId() {
		return this.activeTextureId;
	}

	public ITexture getDissolveMap() {
		return this.tDissolveMap;
	}

	public float getRatio() {
		return this.ratio;
	}

	public boolean isInverted() {
		return this.useTexture;
	}

	public boolean isUsingTexture() {
		return this.useTexture;
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

		DissolveShaderProgram.u_modelViewProjectionMatrixLocation = this
				.getUniformLocation(ShaderProgramConstants.UNIFORM_MODELVIEWPROJECTIONMATRIX);
		DissolveShaderProgram.u_texture0Location = this.getUniformLocation(ShaderProgramConstants.UNIFORM_TEXTURE_0);
		DissolveShaderProgram.u_texture1Location = this.getUniformLocation(ShaderProgramConstants.UNIFORM_TEXTURE_1);
		DissolveShaderProgram.u_ratioLocation = this.getUniformLocation(DissolveShaderProgram.UNIFORM_RATIO);
		DissolveShaderProgram.u_useTextureLocation = this.getUniformLocation(DissolveShaderProgram.UNIFORM_USETEXTURE);
		DissolveShaderProgram.u_useInvertLocation = this.getUniformLocation(DissolveShaderProgram.UNIFORM_USEINVERT);
	}

	public void setActiveTextureId(int activeTextureId) {
		this.activeTextureId = activeTextureId;
	}

	public void setInverted(boolean invert) {
		this.invert = invert;
	}

	public void setRatio(float ratio) {
		this.ratio = ratio;
	}

	public void setUsingTexture(boolean useTexture) {
		this.useTexture = useTexture;
	}
}
