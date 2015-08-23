package com.partlight.ms.resource;

import com.partlight.ms.activity.GameActivity;
import com.partlight.ms.scene.mainmenu.MainMenuScene;
import com.partlight.ms.scene.session.SessionScene;
import com.partlight.ms.scene.session.tutorial.TutorialSessionScene;

import android.content.SharedPreferences;

/**
 * Environment variables - instances that can be globally accessed from any
 * class.
 * 
 * @author Johan Svensson - partLight Entertainment
 * 
 */
public final class EnvironmentVars {

	public static final class PreferenceKeys {
		public static final String	KEY_CAN_DYE_CLOTHES				= "canDyeClothes";
		public static final String	KEY_CLOTH_DYE_AMOUNT			= "clothDyeAmount";
		public static final String	KEY_DOMINADOR_REPEATED_FIRE		= "dominadorRepeatedFire";
		public static final String	KEY_GLITCH_CLIP_ITEMS			= "glitchClipItems";
		public static final String	KEY_HAS_NOTIFIED_MULTI_TOUCH	= "hasNotifiedMultiTouch";
		public static final String	KEY_HAS_PLAYED_TUTORIAL			= "hasPlayedTutorial";
		public static final String	KEY_IS_FIRST_LAUNCH				= "isFirstLaunch";
		public static final String	KEY_LASER_SIGHT_ITEMS			= "laserSightItems";
		public static final String	KEY_PLAYER_HAIR_B				= "playerHairB";
		public static final String	KEY_PLAYER_HAIR_G				= "playerHairG";
		public static final String	KEY_PLAYER_HAIR_INDEX			= "playerHairIndex";
		public static final String	KEY_PLAYER_HAIR_R				= "playerHairR";
		public static final String	KEY_PLAYER_LEGS_B				= "playerLegsB";
		public static final String	KEY_PLAYER_LEGS_G				= "playerLegsG";
		public static final String	KEY_PLAYER_LEGS_R				= "playerLegsR";
		public static final String	KEY_PLAYER_SKIN_B				= "playerSkinB";
		public static final String	KEY_PLAYER_SKIN_G				= "playerSkinG";
		public static final String	KEY_PLAYER_SKIN_R				= "playerSkinR";
		public static final String	KEY_PLAYER_TORSO_B				= "playerTorsoB";
		public static final String	KEY_PLAYER_TORSO_G				= "playerTorsoG";
		public static final String	KEY_PLAYER_TORSO_INDEX			= "playerTorsoIndex";
		public static final String	KEY_PLAYER_TORSO_R				= "playerTorsoR";
		public static final String	KEY_PURCHASED_HAIR_STYLES		= "purchasedHairStyles";
		public static final String	KEY_PURCHASED_TORSO_STYLES		= "purchasedTorsoStyles";
		public static final String	KEY_SCRAP_PARTS_AMOUNT			= "scrapPartsAmount";
		public static final String	KEY_SNIPER_INJECTION_TIPS		= "sniperInjectionTips";
		public static final String	KEY_USER_CANCELED_SIGN_IN		= "userCanceledSignIn";
		public static final String	KEY_USE_CHASE_CAMERA			= "useChaseCamera";
		public static final String	KEY_USE_MUSIC					= "useMusic";
		public static final String	KEY_USE_SOUND					= "useSound";
		public static final String	KEY_AD_BLOCK					= "adBlocked";
	}

	public static final class PreferenceKeysMeta {

		public static final boolean[] decodeBooleanArray(String preferenceKey, SharedPreferences preferences, int arraySize) {
			final boolean[] OUT = new boolean[arraySize];

			for (int i = 0; i < OUT.length; i++)
				OUT[i] = preferences.getBoolean(preferenceKey + "_" + i, false);

			return OUT;
		}

		public static final boolean encodeBooleanArray(String preferenceKey, SharedPreferences.Editor editor, boolean... values) {

			for (int i = 0; i < values.length; i++)
				editor.putBoolean(preferenceKey + "_" + i, values[i]);

			return editor.commit();
		}
	}

	public static final class StaticData {
		public static boolean	dominador_repeatedFire;
		public static boolean	adblock;
		public static boolean	sniper_injectionTips;
		public static boolean[]	glitchClipItems;
		public static boolean[]	laserSightItems;
		public static boolean[]	purchasedHairStyles;
		public static boolean[]	purchasedTorsoStyles;
		public static float		playerHairB;
		public static float		playerHairG;
		public static float		playerHairR;
		public static float		playerLegsB;
		public static float		playerLegsG;
		public static float		playerLegsR;
		public static float		playerSkinB;
		public static float		playerSkinG;
		public static float		playerSkinR;
		public static float		playerSleevesB;
		public static float		playerSleevesG;
		public static float		playerSleevesR;
		public static float		playerTorsoB;
		public static float		playerTorsoG;
		public static float		playerTorsoR;
		public static int		clothDyeAmount;
		public static int		playerHairIndex;
		public static int		playerSkinIndex;
		public static int		playerSleevesIndex;
		public static int		playerTorsoIndex;
		public static int		scrapPartsAmount;
	}

	public static GameActivity				MAIN_CONTEXT;
	public static MainMenuScene				MAIN_MENU;
	public static SessionScene				SESSION_SCENE;
	public static SharedPreferences			PREFERENCES;
	public static SharedPreferences.Editor	PREFERENCES_EDITOR;
	public static TutorialSessionScene		TUTORIAL_SESSION_SCENE;
}
