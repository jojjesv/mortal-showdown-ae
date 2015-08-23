package com.partlight.ms.util.script;

import java.io.IOException;
import java.io.InputStream;

import android.content.res.AssetManager;

abstract class ScriptLibrary {

	public static final String loadShader(AssetManager assetManager, String assetName) throws IOException {
		String out = "";

		final InputStream STREAM = assetManager.open("scripts/".concat(assetName));
		int i;

		while ((i = STREAM.read()) != -1)
			out = out.concat(String.valueOf((char) i));

		return out;
	}
}
