package com.partlight.ms.util.script;

import java.io.IOException;

import android.content.res.AssetManager;
import android.util.Log;

public final class FragmentShaderScripts {

	public static final String[]	SCRIPTS;
	public static final int			SCRIPT_TINT				= 0;
	public static final int			SCRIPT_LINEAR_GRADIENT	= 1;
	public static final int			SCRIPT_RADIAL_GRADIENT	= 2;
	public static final int			SCRIPT_BRUSH			= 3;
	public static final int			SCRIPT_DISSOLVE			= 4;

	static {
		SCRIPTS = new String[5];
	}

	public static final void loadShaders(AssetManager assetManager) {
		try {

			FragmentShaderScripts.SCRIPTS[FragmentShaderScripts.SCRIPT_TINT] = ScriptLibrary.loadShader(assetManager,
					"frag/tint_shader.glsl");
			FragmentShaderScripts.SCRIPTS[FragmentShaderScripts.SCRIPT_LINEAR_GRADIENT] = ScriptLibrary.loadShader(assetManager,
					"frag/linear_gradient_shader.glsl");
			FragmentShaderScripts.SCRIPTS[FragmentShaderScripts.SCRIPT_RADIAL_GRADIENT] = ScriptLibrary.loadShader(assetManager,
					"frag/radial_gradient_shader.glsl");
			FragmentShaderScripts.SCRIPTS[FragmentShaderScripts.SCRIPT_BRUSH] = ScriptLibrary.loadShader(assetManager,
					"frag/brush_shader.glsl");
			FragmentShaderScripts.SCRIPTS[FragmentShaderScripts.SCRIPT_DISSOLVE] = ScriptLibrary.loadShader(assetManager,
					"frag/dissolve_shader.glsl");

		} catch (final IOException e) {
			Log.e("Mortal Showdown", "One or more shaders were not found!", e);
		}
	}
}
