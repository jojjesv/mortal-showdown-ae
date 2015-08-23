package com.partlight.ms.session.level;

import org.andengine.extension.tmx.TMXLoader;
import org.andengine.extension.tmx.TMXTiledMap;
import org.andengine.extension.tmx.util.exception.TMXLoadException;

import com.partlight.ms.R;
import com.partlight.ms.resource.EnvironmentVars;
import com.partlight.ms.resource.ResourceManager.AssetPaths;

import android.util.Log;

public class Levels {

	public static final String ID_LEVEL1 = "level1";

	public static Level loadLevelFromId(String id) {

		Level returnLevel = null;

		switch (id) {
		case ID_LEVEL1:

			TMXTiledMap outMap = null;

			final TMXLoader TMX_LOADER = new TMXLoader(EnvironmentVars.MAIN_CONTEXT.getAssets(),
					EnvironmentVars.MAIN_CONTEXT.getTextureManager(), EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager());
			try {
				outMap = TMX_LOADER.loadFromAsset(AssetPaths.TMX_LEVEL01);
			} catch (final TMXLoadException e) {

			}

			returnLevel = new Level(outMap);

			break;

		default:

			Log.e(EnvironmentVars.MAIN_CONTEXT.getString(R.string.app_name), "Error: A level with the id \"" + id + "\" was not found!");

			break;
		}

		return returnLevel;
	}
}
