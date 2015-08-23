package com.partlight.ms.resource.load;

import java.io.IOException;

import com.partlight.ms.entity.mainmenu.MainMenuBackground;
import com.partlight.ms.resource.EnvironmentVars;
import com.partlight.ms.scene.mainmenu.MainMenuScene;
import com.partlight.ms.session.character.Armory;
import com.partlight.ms.session.character.CharacterCorpse;

import android.os.AsyncTask;

/**
 * This implementation of {@link AsyncTask} will make calls to
 * {@link #MAIN_CONTEXT}, to load all required resources for the application.
 * 
 * @author Johan Svensson - partLight Entertainment
 * 
 */
public class ResourceLoadingTask extends AsyncTask<Void, Void, Void> {

	private final MainMenuScene mmsContext;

	public ResourceLoadingTask(MainMenuScene context) {
		this.mmsContext = context;
	}

	@Override
	protected Void doInBackground(Void... params) {

		try {
			EnvironmentVars.MAIN_CONTEXT.onLoadLoadingTextures();
			this.mmsContext.onLoadingResourcesLoaded();
			EnvironmentVars.MAIN_CONTEXT.onLoadTextures();
		} catch (final IOException ex) {
			EnvironmentVars.MAIN_CONTEXT.onTextureNotFound();
		}

		EnvironmentVars.MAIN_CONTEXT.getStoredData();

		try {
			EnvironmentVars.MAIN_CONTEXT.onLoadFonts();
		} catch (final IOException ex) {
			EnvironmentVars.MAIN_CONTEXT.onFontNotFound();
		}

		try {
			EnvironmentVars.MAIN_CONTEXT.onLoadMusic();
		} catch (final IOException ex) {
			EnvironmentVars.MAIN_CONTEXT.onMusicNotFound();
		}

		try {
			EnvironmentVars.MAIN_CONTEXT.onLoadSounds();
		} catch (final IOException ex) {
			EnvironmentVars.MAIN_CONTEXT.onSoundNotFound();
		}

		EnvironmentVars.MAIN_CONTEXT.initTextureRegions();
		EnvironmentVars.MAIN_CONTEXT.onLoadTexturesToHardware();

		MainMenuBackground.loadResources();
		Armory.onVertexBufferObjectManagerInitialised();

		CharacterCorpse.onTexturesLoaded();
		this.mmsContext.onResourcesLoaded();

		return null;
	}

}
