package com.partlight.ms.util.script;

import java.io.IOException;

import android.content.res.AssetManager;
import android.util.Log;

public final class VertexShaderScripts {

	public static final String[]	SCRIPTS;
	public static final int			SCRIPT_WAVE	= 0;

	static {
		SCRIPTS = new String[1];
	}

	public static final void loadShaders(AssetManager assetManager) {
		try {

			VertexShaderScripts.SCRIPTS[VertexShaderScripts.SCRIPT_WAVE] = ScriptLibrary.loadShader(assetManager, "vert/wave_shader.glsl");

		} catch (final IOException e) {
			Log.e("Mortal Showdown", "One or more shaders were not found!", e);
		}
	}
}
