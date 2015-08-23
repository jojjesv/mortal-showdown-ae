package com.partlight.ms.scene.mainmenu;

import org.andengine.opengl.texture.region.ITiledTextureRegion;

import com.partlight.ms.entity.mainmenu.button.Button;
import com.partlight.ms.resource.EnvironmentVars;
import com.partlight.ms.resource.ResourceManager.HudRegions;
import com.partlight.ms.resource.ResourceManager.MiscRegions;
import com.partlight.ms.resource.ResourceManager.OptionsTextureRegions;

public class MainMenuOptions {

	public static final String	STRING_SYNCING			= "SYNCING...";
	public static final String	STRING_SYNC_HOLD_DOWN	= "HOLD DOWN TO SIGN OUT";
	public static final String	STRING_SYNC_RELEASE		= "RELEASE TO SIGN OUT";
	public static final int		TAG_TOGGLE_CHASE_CAMERA	= 200;
	public static final int		TAG_TOGGLE_MUSIC		= 201;
	public static final int		TAG_TOGGLE_SOUND		= 202;
	public static final int		TAG_SYNC_ACCOUNT		= 203;
	public static final int		TAG_CREDITS				= 204;

	public static final int[] BUTTON_TAGS = {
			MainMenuOptions.TAG_TOGGLE_CHASE_CAMERA,
			MainMenuOptions.TAG_TOGGLE_MUSIC,
			MainMenuOptions.TAG_TOGGLE_SOUND,
			MainMenuOptions.TAG_SYNC_ACCOUNT,
			MainMenuOptions.TAG_CREDITS,
	};

	public static final String[] BUTTON_TITLES = {
			"CHASE CAMERA IS TURNED ON/CHASE CAMERA IS TURNED OFF",
			"MUSIC IS TURNED ON/MUSIC IS TURNED OFF",
			"SOUND IS TURNED ON/SOUND IS TURNED OFF",
			"DESYNC GOOGLE ACCOUNT/SYNC GOOGLE ACCOUNT",
			"GAME CREDITS",
	};

	public static final boolean[] BUTTON_IS_TOGGLE = {
			true,
			true,
			true,
			false,
			false
	};

	public static final ITiledTextureRegion getButtonIcon(int buttonTag) {
		switch (buttonTag) {
		case MainMenuOptions.TAG_TOGGLE_CHASE_CAMERA:
			return HudRegions.region_options_chase;
		case MainMenuOptions.TAG_TOGGLE_MUSIC:
			return HudRegions.region_options_music;
		case MainMenuOptions.TAG_TOGGLE_SOUND:
			return HudRegions.region_options_sound;
		case MainMenuOptions.TAG_SYNC_ACCOUNT:
			return MiscRegions.region_hud_login_states02;
		case MainMenuOptions.TAG_CREDITS:
			return OptionsTextureRegions.region_icon_credits;
		}
		return null;
	}

	public static final void processButtonClick(Button button) {
		switch (button.getTag()) {
		case MainMenuOptions.TAG_TOGGLE_CHASE_CAMERA:
			EnvironmentVars.MAIN_CONTEXT.onToggleChaseCamera();
			break;
		case MainMenuOptions.TAG_TOGGLE_MUSIC:
			EnvironmentVars.MAIN_CONTEXT.onToggleMusic();
			break;
		case MainMenuOptions.TAG_TOGGLE_SOUND:
			EnvironmentVars.MAIN_CONTEXT.onToggleSound();
			break;
		case MainMenuOptions.TAG_SYNC_ACCOUNT:
			EnvironmentVars.MAIN_CONTEXT.onToggleAccountSync(button.isLongPressed());
			break;
		}
	}
}
