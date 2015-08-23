package com.partlight.ms.activity;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.andengine.audio.BaseAudioEntity;
import org.andengine.audio.music.Music;
import org.andengine.audio.music.MusicFactory;
import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.engine.Engine;
import org.andengine.engine.LimitedFPSEngine;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.IEntity;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.shape.RectangularShape;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.bitmap.BitmapTexture;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.texture.render.RenderTexture;
import org.andengine.opengl.util.GLState;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.adt.io.in.IInputStreamOpener;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.doubleclick.PublisherAdView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesActivityResultCodes;
import com.partlight.ms.R;
import com.partlight.ms.activity.ad.TappxAdListener;
import com.partlight.ms.activity.task.ConnectionTask;
import com.partlight.ms.mainmenu.hud.ColorSelectorLibrary;
import com.partlight.ms.resource.EnvironmentVars;
import com.partlight.ms.resource.EnvironmentVars.PreferenceKeys;
import com.partlight.ms.resource.EnvironmentVars.PreferenceKeysMeta;
import com.partlight.ms.resource.EnvironmentVars.StaticData;
import com.partlight.ms.resource.ResourceManager;
import com.partlight.ms.resource.ResourceManager.AssetPaths;
import com.partlight.ms.resource.ResourceManager.BloodRegions;
import com.partlight.ms.resource.ResourceManager.DialogRegions;
import com.partlight.ms.resource.ResourceManager.HudRegions;
import com.partlight.ms.resource.ResourceManager.MainMenuRegions;
import com.partlight.ms.resource.ResourceManager.MiscRegions;
import com.partlight.ms.resource.ResourceManager.OptionsTextureRegions;
import com.partlight.ms.resource.ResourceManager.PlayerRegions;
import com.partlight.ms.resource.ResourceManager.SharedCharRegions;
import com.partlight.ms.resource.ResourceManager.StoreTextureRegions;
import com.partlight.ms.resource.ResourceManager.StrokeTextureRegions;
import com.partlight.ms.resource.ResourceManager.ZombieTextureRegions.Zombie01;
import com.partlight.ms.resource.ResourceManager.ZombieTextureRegions.Zombie02;
import com.partlight.ms.scene.DialogScene;
import com.partlight.ms.scene.mainmenu.MainMenuScene;
import com.partlight.ms.scene.mainmenu.MainMenuStore;
import com.partlight.ms.scene.session.SessionScene;
import com.partlight.ms.session.camera.GameCamera;
import com.partlight.ms.session.character.Armory;
import com.partlight.ms.session.character.player.Player;
import com.partlight.ms.session.hud.ConnectionNotifier;
import com.partlight.ms.shader.RadialGradientShaderProgram;
import com.partlight.ms.shader.TintShaderProgram;
import com.partlight.ms.util.listener.OnBackPressedListener;
import com.partlight.ms.util.listener.OnResumeListener;
import com.partlight.ms.util.script.FragmentShaderScripts;
import com.partlight.ms.util.script.VertexShaderScripts;
import com.tappx.TAPPXAdBanner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

/**
 * Main activity used for playing Mortal Showdown.
 * 
 * @author Johan Svensson - partLight Entertainment
 * 
 */
@SuppressLint("NewApi")
public class GameActivity extends SimpleBaseGameActivity implements TappxAdListener {

	/**
	 * Contains constant ID's and request codes for managing Google Play
	 * Services content.
	 * 
	 * @author Johan Svensson, partLight
	 * 
	 */
	public final class GooglePlayConstants {

		/**
		 * Little Hitman
		 */
		public static final String	ACHIEVEMENT_0_ID	= "CgkIv9r_mcYcEAIQAg";
		/**
		 * An Awkward Slap On The Back
		 */
		public static final String	ACHIEVEMENT_1_ID	= "CgkIv9r_mcYcEAIQBA";
		/**
		 * First Grade Spring Break
		 */
		public static final String	ACHIEVEMENT_2_ID	= "CgkIv9r_mcYcEAIQAw";

		/**
		 * Second Mate Warfare
		 */
		public static final String ACHIEVEMENT_3_ID = "CgkIv9r_mcYcEAIQBQ";

		/**
		 * The Day Of The Dead
		 */
		public static final String	ACHIEVEMENT_4_ID			= "CgkIv9r_mcYcEAIQBg";
		/**
		 * Bring On The Carbon
		 */
		public static final String	ACHIEVEMENT_5_ID			= "CgkIv9r_mcYcEAIQCA";
		/**
		 * Undeniable Prosperity
		 */
		public static final String	ACHIEVEMENT_6_ID			= "CgkIv9r_mcYcEAIQBw";
		/**
		 * Explosions 'R Us
		 */
		public static final String	ACHIEVEMENT_7_ID			= "CgkIv9r_mcYcEAIQCQ";
		public static final String	LEADERBOARD_ID				= "CgkIv9r_mcYcEAIQAQ";
		public static final int		ACHIEVEMENTS_REQUEST_CODE	= 1003;
		public static final int		GOOGLE_PLAY_REQUEST_CODE	= 1001;
		public static final int		LEADERBOARD_REQUEST_CODE	= 1002;
	}

	public interface ScreenTextureCallback {
		public void onScreenTextureReceived(RenderTexture texture, Bitmap bmp);
	}

	public static class Values {
		public static final int	TASK_DESCRIPTION_PRIMARY_COLOR_B	= 37;
		public static final int	TASK_DESCRIPTION_PRIMARY_COLOR_G	= 37;
		public static final int	TASK_DESCRIPTION_PRIMARY_COLOR_R	= 82;
	}

	private static final String	PREFERENCES_NAME		= "partlight_ms_store";
	private static final String	SHARED_PREFERENCES_NAME	= "partlight_ms_memory";
	public static final boolean	USE_TAPPX				= true;
	public static boolean		userCanceledSignIn;

	private GameCamera					gcCamera;
	private GoogleApiClient				gacGoogleGamesClient;
	private List<BaseAudioEntity>		lCurrentPlayingAudio;
	private List<Integer>				lConnectionCodeQueue;
	private List<OnBackPressedListener>	lOnBackPressedListeners;
	private List<TappxAdListener>		lTappxAdListeners;
	private List<OnResumeListener>		lOnResumeListeners;
	private List<Runnable>				onCameraBoundsChanged;
	private NetworkInfo					niNetworkInfo;
	private RenderTexture				rtScreenRenderer;
	private ScreenTextureCallback		screenTextureCallback;
	private boolean						isResolvingClientIssue;
	private boolean						useMusic;
	private boolean						useSound;
	private boolean						useChaseCamera;
	private PublisherAdView				pavAdView;
	private int							currentConnectionCode	= -1;
	private boolean						adLoaded;

	public void addAdListener(TappxAdListener adListener) {
		this.lTappxAdListeners.add(adListener);
	}

	public void addConnectionCode(int code) {
		this.lConnectionCodeQueue.add(code);
	}

	public void addOnBackPressedListener(OnBackPressedListener onBackPressedListener) {
		this.lOnBackPressedListeners.add(onBackPressedListener);
	}

	public void addOnResumeListener(OnResumeListener onResumeListener) {
		this.lOnResumeListeners.add(onResumeListener);
	}

	public boolean assertCanMakeConnection() {

		this.initNetworkInfo();

		if (!this.niNetworkInfo.isConnected())
			return false;

		if (EnvironmentVars.PREFERENCES.getBoolean(PreferenceKeys.KEY_USER_CANCELED_SIGN_IN, false))
			return false;

		return true;
	}

	private void changeAdVisibility(final int visibility) {
		if (!GameActivity.USE_TAPPX)
			return;
		this.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				GameActivity.this.pavAdView.setVisibility(visibility);
			}
		});
	}

	public void clearAdListeners() {
		this.lTappxAdListeners.clear();
	}

	public void clearOnBackPressedListeners() {
		this.lOnBackPressedListeners.clear();
	}

	public void clearOnCameraBoundsChanged() {
		if (this.onCameraBoundsChanged != null)
			this.onCameraBoundsChanged.clear();
	}

	public void clearOnResumeListeners() {
		this.lOnResumeListeners.clear();
	}

	public int getAdHeight() {
		if (this.pavAdView == null)
			return 0;
		return this.pavAdView.getAdSize().getHeight();
	}

	public BaseAudioEntity[] getAudioRegistered() {
		return this.lCurrentPlayingAudio.toArray(new BaseAudioEntity[this.lCurrentPlayingAudio.size()]);
	}

	public GameCamera getCamera() {
		return this.gcCamera;
	}

	/**
	 * Gets the {@link GoogleApiClient} for managing Google Play Services
	 * content.
	 * 
	 * @return The client for managing Google Play Services content.
	 */
	public GoogleApiClient getGoogleApiClient() {
		return this.gacGoogleGamesClient;
	}

	public HUD getHud() {
		return this.gcCamera.getHUD();
	}

	private Music getMusicFromAssets(final String fileName) throws IOException {
		Music out = null;

		out = MusicFactory.createMusicFromAsset(this.getMusicManager(), this, fileName);

		return out;
	}

	private Sound getSoundFromAssets(final String fileName) throws IOException {
		Sound out = null;

		out = SoundFactory.createSoundFromAsset(this.getSoundManager(), this, fileName);

		return out;
	}

	public void getStoredData() {
		StaticData.dominador_repeatedFire = EnvironmentVars.PREFERENCES.getBoolean(PreferenceKeys.KEY_DOMINADOR_REPEATED_FIRE, false);
		StaticData.sniper_injectionTips = EnvironmentVars.PREFERENCES.getBoolean(PreferenceKeys.KEY_SNIPER_INJECTION_TIPS, false);

		StaticData.glitchClipItems = PreferenceKeysMeta.decodeBooleanArray(PreferenceKeys.KEY_GLITCH_CLIP_ITEMS,
				EnvironmentVars.PREFERENCES, Armory.WEP_ARRAY.length);
		StaticData.glitchClipItems[Armory.WEP_PISTOL] = true;
		StaticData.glitchClipItems[Armory.WEP_CALTROP] = true;
		StaticData.glitchClipItems[Armory.WEP_GRENADE] = true;

		StaticData.laserSightItems = PreferenceKeysMeta.decodeBooleanArray(PreferenceKeys.KEY_LASER_SIGHT_ITEMS,
				EnvironmentVars.PREFERENCES, Armory.WEP_ARRAY.length);

		StaticData.purchasedHairStyles = PreferenceKeysMeta.decodeBooleanArray(PreferenceKeys.KEY_PURCHASED_HAIR_STYLES,
				EnvironmentVars.PREFERENCES, MainMenuStore.HAIR_TITLES.length);
		StaticData.purchasedHairStyles[0] = true;

		StaticData.purchasedTorsoStyles = PreferenceKeysMeta.decodeBooleanArray(PreferenceKeys.KEY_PURCHASED_TORSO_STYLES,
				EnvironmentVars.PREFERENCES, MainMenuStore.TORSO_TITLES.length);
		StaticData.purchasedTorsoStyles[0] = true;

		StaticData.clothDyeAmount = EnvironmentVars.PREFERENCES.getInt(PreferenceKeys.KEY_CLOTH_DYE_AMOUNT, 0);

		StaticData.scrapPartsAmount = EnvironmentVars.PREFERENCES.getInt(PreferenceKeys.KEY_SCRAP_PARTS_AMOUNT, 0);

		StaticData.playerLegsR = EnvironmentVars.PREFERENCES.getFloat(PreferenceKeys.KEY_PLAYER_LEGS_R,
				ColorSelectorLibrary.COLORS_COMMON[4][0]);
		StaticData.playerLegsG = EnvironmentVars.PREFERENCES.getFloat(PreferenceKeys.KEY_PLAYER_LEGS_G,
				ColorSelectorLibrary.COLORS_COMMON[4][1]);
		StaticData.playerLegsB = EnvironmentVars.PREFERENCES.getFloat(PreferenceKeys.KEY_PLAYER_LEGS_B,
				ColorSelectorLibrary.COLORS_COMMON[4][2]);

		StaticData.playerTorsoIndex = EnvironmentVars.PREFERENCES.getInt(PreferenceKeys.KEY_PLAYER_TORSO_INDEX, 0);
		StaticData.playerTorsoR = EnvironmentVars.PREFERENCES.getFloat(PreferenceKeys.KEY_PLAYER_TORSO_R,
				ColorSelectorLibrary.COLORS_COMMON[15][0]);
		StaticData.playerTorsoG = EnvironmentVars.PREFERENCES.getFloat(PreferenceKeys.KEY_PLAYER_TORSO_G,
				ColorSelectorLibrary.COLORS_COMMON[15][1]);
		StaticData.playerTorsoB = EnvironmentVars.PREFERENCES.getFloat(PreferenceKeys.KEY_PLAYER_TORSO_B,
				ColorSelectorLibrary.COLORS_COMMON[15][2]);

		StaticData.playerSkinR = EnvironmentVars.PREFERENCES.getFloat(PreferenceKeys.KEY_PLAYER_SKIN_R,
				ColorSelectorLibrary.COLORS_SKIN[4][0]);
		StaticData.playerSkinG = EnvironmentVars.PREFERENCES.getFloat(PreferenceKeys.KEY_PLAYER_SKIN_G,
				ColorSelectorLibrary.COLORS_SKIN[4][1]);
		StaticData.playerSkinB = EnvironmentVars.PREFERENCES.getFloat(PreferenceKeys.KEY_PLAYER_SKIN_B,
				ColorSelectorLibrary.COLORS_SKIN[4][2]);

		StaticData.playerHairIndex = EnvironmentVars.PREFERENCES.getInt(PreferenceKeys.KEY_PLAYER_HAIR_INDEX, 0);
		StaticData.playerHairR = EnvironmentVars.PREFERENCES.getFloat(PreferenceKeys.KEY_PLAYER_HAIR_R,
				ColorSelectorLibrary.COLORS_COMMON[4][0]);
		StaticData.playerHairG = EnvironmentVars.PREFERENCES.getFloat(PreferenceKeys.KEY_PLAYER_HAIR_G,
				ColorSelectorLibrary.COLORS_COMMON[4][1]);
		StaticData.playerHairB = EnvironmentVars.PREFERENCES.getFloat(PreferenceKeys.KEY_PLAYER_HAIR_B,
				ColorSelectorLibrary.COLORS_COMMON[4][2]);
	}

	private BitmapTexture getTextureFromAssets(final String fileName) throws IOException {
		BitmapTexture out = null;

		final IInputStreamOpener stream = new IInputStreamOpener() {

			@Override
			public InputStream open() throws IOException {
				return GameActivity.this.getAssets().open(fileName);
			}
		};

		out = new BitmapTexture(this.getTextureManager(), stream);

		return out;
	}

	private void handleMusic(boolean pause) {
		final BaseAudioEntity[] ARRAY = this.lCurrentPlayingAudio.toArray(new BaseAudioEntity[this.lCurrentPlayingAudio.size()]);

		for (final BaseAudioEntity m : ARRAY)
			if (pause)
				m.pause();
			else
				m.resume();
	}

	/**
	 * Returns the height of the screen.
	 * 
	 * @return The height of the screen, in pixels.
	 */
	public float height() {
		return this.getCamera().getYMax() - this.getCamera().getYMin();
	}

	// *********************************
	//

	// INIT METHODS
	// *********************************

	public void hideAd() {
		this.changeAdVisibility(View.GONE);
	}

	/**
	 * 
	 */
	private void initEnvironmentVars() {

		EnvironmentVars.PREFERENCES = this.getSharedPreferences(GameActivity.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
		EnvironmentVars.PREFERENCES_EDITOR = EnvironmentVars.PREFERENCES.edit();

		EnvironmentVars.PREFERENCES = this.getSharedPreferences(GameActivity.PREFERENCES_NAME, Context.MODE_PRIVATE);
		EnvironmentVars.PREFERENCES_EDITOR = EnvironmentVars.PREFERENCES.edit();

		EnvironmentVars.MAIN_CONTEXT = this;
	}

	private void initGoogleApiClient() {
		final ConnectionCallbacks CONNECTION_CALLBACKS = new ConnectionCallbacks() {

			@Override
			public void onConnected(Bundle arg0) {
				GameActivity.this.addConnectionCode(ConnectionNotifier.CODE_CONNECTION_SUCCESSFUL);
			}

			@Override
			public void onConnectionSuspended(int arg0) {
				GameActivity.this.addConnectionCode(ConnectionNotifier.CODE_CONNECTION_FAILED);
			}
		};

		final OnConnectionFailedListener FAILED_CALLBACK = new OnConnectionFailedListener() {

			@Override
			public void onConnectionFailed(ConnectionResult arg0) {
				if (arg0.hasResolution() && !GameActivity.this.isResolvingClientIssue) {
					try {
						GameActivity.this.isResolvingClientIssue = true;
						arg0.startResolutionForResult(GameActivity.this, GooglePlayConstants.GOOGLE_PLAY_REQUEST_CODE);
					} catch (final SendIntentException e) {
					}
					return;
				}

				if (!arg0.hasResolution()) {
					GameActivity.userCanceledSignIn = true;
					GameActivity.this.addConnectionCode(ConnectionNotifier.CODE_NOT_SIGNED_IN);
				}
			}
		};

		//@formatter:off
		this.gacGoogleGamesClient = new GoogleApiClient.Builder(this)
				.addConnectionCallbacks(CONNECTION_CALLBACKS)
				.addOnConnectionFailedListener(FAILED_CALLBACK)
				.addApi(Games.API)
				.addScope(Games.SCOPE_GAMES)
				.build();

		//@formatter:on
	}

	private void initNetworkInfo() {
		if (this.niNetworkInfo == null)
			this.niNetworkInfo = ((ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE))
					.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
	}

	public void initTextureRegions() {

		BloodRegions.region_blood01 = TextureRegionFactory.extractFromTexture(ResourceManager.btBlood, 0, 1, 14, 4);
		BloodRegions.region_blood02 = TextureRegionFactory.extractFromTexture(ResourceManager.btBlood, 14, 0, 12, 4);
		BloodRegions.region_blood03 = TextureRegionFactory.extractFromTexture(ResourceManager.btBlood, 0, 5, 19, 4);

		HudRegions.region_ammo_icon = TextureRegionFactory.extractFromTexture(ResourceManager.btAmmoIcon);
		HudRegions.region_boost_icon = TextureRegionFactory.extractFromTexture(ResourceManager.btSpeedBoostIcon);
		HudRegions.region_bounds_indicator = TextureRegionFactory.extractFromTexture(ResourceManager.btHudBoundsIndicator);
		HudRegions.region_col_map = TextureRegionFactory.extractTiledFromTexture(ResourceManager.btColMap, 4, 1);
		HudRegions.region_combo_back = TextureRegionFactory.extractFromTexture(ResourceManager.btComboBack);
		HudRegions.region_combo_canvas = TextureRegionFactory.extractFromTexture(ResourceManager.btComboCanvas);
		HudRegions.region_combo_fore = TextureRegionFactory.extractFromTexture(ResourceManager.btComboFore);
		HudRegions.region_explosion = TextureRegionFactory.extractTiledFromTexture(ResourceManager.btExplosion, 9, 1);
		HudRegions.region_explosion_mark = TextureRegionFactory.extractTiledFromTexture(ResourceManager.btExplosionMark, 2, 1);
		HudRegions.region_fire = TextureRegionFactory.extractTiledFromTexture(ResourceManager.btShootButton, 2, 1);
		HudRegions.region_hbar = TextureRegionFactory.extractTiledFromTexture(ResourceManager.btHBar, 2, 1);
		HudRegions.region_js_background = TextureRegionFactory.extractTiledFromTexture(ResourceManager.btJoyStickBack, 1, 1);
		HudRegions.region_js_foreground = TextureRegionFactory.extractFromTexture(ResourceManager.btJoyStickFore);
		HudRegions.region_options_chase = TextureRegionFactory.extractTiledFromTexture(ResourceManager.btOptionsChase, 1, 1);
		HudRegions.region_options_music = TextureRegionFactory.extractTiledFromTexture(ResourceManager.btOptionsMusic, 1, 1);
		HudRegions.region_options_sound = TextureRegionFactory.extractTiledFromTexture(ResourceManager.btOptionsSound, 1, 1);
		HudRegions.region_options_toggle = TextureRegionFactory.extractTiledFromTexture(ResourceManager.btOptionsToggle, 5, 1);
		HudRegions.region_projectile_map = TextureRegionFactory.extractTiledFromTexture(ResourceManager.btProjectileMap, 2, 1);
		HudRegions.region_smoke = TextureRegionFactory.extractTiledFromTexture(ResourceManager.btSmoke, 2, 1);
		HudRegions.region_smoke_small01 = TextureRegionFactory.extractFromTexture(ResourceManager.btSmokeSmall, 0, 0, 9, 8);
		HudRegions.region_smoke_small02 = TextureRegionFactory.extractFromTexture(ResourceManager.btSmokeSmall, 9, 0, 7, 7);
		HudRegions.region_wepframe = TextureRegionFactory.extractTiledFromTexture(ResourceManager.btWepFrame, 1, 1);
		HudRegions.region_weps = TextureRegionFactory.extractTiledFromTexture(ResourceManager.btWeps, 1, Armory.WEP_ARRAY.length);

		HudRegions.region_wave_1 = new TextureRegion[2];
		{
			HudRegions.region_wave_1[0] = TextureRegionFactory.extractFromTexture(ResourceManager.btWave1, 0, 0, 59, 12);
			HudRegions.region_wave_1[1] = TextureRegionFactory.extractFromTexture(ResourceManager.btWave1, 0, 12, 148, 12);
		}

		if (SessionScene.USE_LASER_CROSSHAIR)
			HudRegions.region_laser_crosshair = TextureRegionFactory.extractFromTexture(ResourceManager.btLaserCrosshair);

		MainMenuRegions.region_achievements = TextureRegionFactory.extractFromTexture(ResourceManager.btAchievements);
		MainMenuRegions.region_bg_shadow = TextureRegionFactory.extractFromTexture(ResourceManager.btBgShadow);
		MainMenuRegions.region_leaderboard = TextureRegionFactory.extractFromTexture(ResourceManager.btLeaderboard);
		MainMenuRegions.region_options = TextureRegionFactory.extractFromTexture(ResourceManager.btOptions);
		MainMenuRegions.region_store = TextureRegionFactory.extractFromTexture(ResourceManager.btStore);
		MainMenuRegions.region_license = TextureRegionFactory.extractFromTexture(ResourceManager.btLicense);

		OptionsTextureRegions.region_icon_credits = TextureRegionFactory.extractTiledFromTexture(ResourceManager.btIconCredits, 1, 1);

		DialogRegions.region_dialog01 = TextureRegionFactory.extractFromTexture(ResourceManager.btDialog01);
		DialogRegions.region_dialog02 = TextureRegionFactory.extractFromTexture(ResourceManager.btDialog02);
		DialogRegions.region_dialog_btn = TextureRegionFactory.extractFromTexture(ResourceManager.btDialogBtn);

		MiscRegions.region_hud_login_states01 = TextureRegionFactory.extractTiledFromTexture(ResourceManager.btHudLoginStates01, 4, 1);
		MiscRegions.region_hud_login_states02 = TextureRegionFactory.extractTiledFromTexture(ResourceManager.btOptionsLoginStates, 2, 1);
		MiscRegions.region_scrap_part = TextureRegionFactory.extractFromTexture(ResourceManager.btScrapPartsSmall);
		MiscRegions.region_scrap_part_icon02 = TextureRegionFactory.extractFromTexture(ResourceManager.btScrapPartsBig);
		MiscRegions.region_tiled_empty = TextureRegionFactory.extractTiledFromTexture(ResourceManager.btEmpty, 1, 1);

		PlayerRegions.region_sleeves = new TiledTextureRegion[2];
		{
			PlayerRegions.region_sleeves[0] = null;
			PlayerRegions.region_sleeves[1] = TextureRegionFactory.extractTiledFromTexture(ResourceManager.btCharSleeves02, 3, 2);
		}
		PlayerRegions.region_a01 = TextureRegionFactory.extractTiledFromTexture(ResourceManager.btCharA01, 3, 4);
		PlayerRegions.region_a01_muzzle = TextureRegionFactory.extractTiledFromTexture(ResourceManager.btCharA01Muzzle, 3, 2);
		PlayerRegions.region_a02 = TextureRegionFactory.extractTiledFromTexture(ResourceManager.btCharA02, 3, 4);
		PlayerRegions.region_a02_muzzle = TextureRegionFactory.extractTiledFromTexture(ResourceManager.btCharA02Muzzle, 3, 2);
		PlayerRegions.region_a03 = TextureRegionFactory.extractTiledFromTexture(ResourceManager.btCharA03, 3, 4);
		PlayerRegions.region_a03_muzzle = TextureRegionFactory.extractTiledFromTexture(ResourceManager.btCharA03Muzzle, 3, 2);
		PlayerRegions.region_a04 = TextureRegionFactory.extractTiledFromTexture(ResourceManager.btCharA04, 3, 4);
		PlayerRegions.region_a04_muzzle = TextureRegionFactory.extractTiledFromTexture(ResourceManager.btCharA04Muzzle, 3, 2);
		PlayerRegions.region_a05 = TextureRegionFactory.extractTiledFromTexture(ResourceManager.btCharA05, 3, 4);

		SharedCharRegions.region_hair = new TiledTextureRegion[2];
		{
			SharedCharRegions.region_hair[0] = TextureRegionFactory.extractTiledFromTexture(ResourceManager.btCharHair01, 3, 2);
			SharedCharRegions.region_hair[1] = TextureRegionFactory.extractTiledFromTexture(ResourceManager.btCharHair02, 3, 2);
		}
		SharedCharRegions.region_hair_d = new TiledTextureRegion[1];
		{
			SharedCharRegions.region_hair_d[0] = TextureRegionFactory.extractTiledFromTexture(ResourceManager.btCharHair01D, 2, 2);
		}
		SharedCharRegions.region_ub = new TiledTextureRegion[2];
		SharedCharRegions.region_ub_d = new TiledTextureRegion[1];
		{
			SharedCharRegions.region_ub[0] = TextureRegionFactory.extractTiledFromTexture(ResourceManager.btCharUb01, 3, 2);
			SharedCharRegions.region_ub[1] = TextureRegionFactory.extractTiledFromTexture(ResourceManager.btCharUb02, 3, 2);
			SharedCharRegions.region_ub_d[0] = TextureRegionFactory.extractTiledFromTexture(ResourceManager.btCharUb01D, 2, 2);
		}
		SharedCharRegions.region_sleeves = new TiledTextureRegion[1];
		SharedCharRegions.region_sleeves_d = new TiledTextureRegion[1];
		{
			SharedCharRegions.region_sleeves[0] = TextureRegionFactory.extractTiledFromTexture(ResourceManager.btCharSleeves01, 5, 5);
			SharedCharRegions.region_sleeves_d[0] = TextureRegionFactory.extractTiledFromTexture(ResourceManager.btCharSleeves01D, 2, 2);
		}
		SharedCharRegions.region_a_d = TextureRegionFactory.extractTiledFromTexture(ResourceManager.btCharADead, 2, 2);
		SharedCharRegions.region_lb = TextureRegionFactory.extractTiledFromTexture(ResourceManager.btCharLb, 8, 6);
		SharedCharRegions.region_lb_d = TextureRegionFactory.extractTiledFromTexture(ResourceManager.btCharLbDead, 2, 2);
		SharedCharRegions.region_lb_walk = TextureRegionFactory.extractTiledFromTexture(ResourceManager.btCharLbWalk, 8, 6);
		SharedCharRegions.region_skin01 = TextureRegionFactory.extractTiledFromTexture(ResourceManager.btCharSkin, 3, 2);
		SharedCharRegions.region_skin01_d = TextureRegionFactory.extractTiledFromTexture(ResourceManager.btCharSkinDead, 2, 2);

		Zombie01.region_a01 = TextureRegionFactory.extractTiledFromTexture(ResourceManager.btZCharA01, 5, 5);

		Zombie02.region_hair = TextureRegionFactory.extractTiledFromTexture(ResourceManager.btZCharHair01, 3, 2);
		Zombie02.region_hair_d = TextureRegionFactory.extractTiledFromTexture(ResourceManager.btZCharHair01D, 2, 2);

		StoreTextureRegions.region_already_bought = TextureRegionFactory.extractFromTexture(ResourceManager.btAlreadyBought);
		StoreTextureRegions.region_checkmark = TextureRegionFactory.extractFromTexture(ResourceManager.btSelectorCheckmark);
		StoreTextureRegions.region_color_splash = TextureRegionFactory.extractFromTexture(ResourceManager.btColorSplash);
		StoreTextureRegions.region_hair_display = TextureRegionFactory.extractTiledFromTexture(ResourceManager.btHairDisplay, 1,
				MainMenuStore.HAIR_TITLES.length);
		StoreTextureRegions.region_torso_display = TextureRegionFactory.extractTiledFromTexture(ResourceManager.btTorsoDisplay, 1,
				MainMenuStore.TORSO_TITLES.length);
		StoreTextureRegions.region_icon_dye = TextureRegionFactory.extractTiledFromTexture(ResourceManager.btIconDye, 1, 1);
		StoreTextureRegions.region_icon_dye_pack = TextureRegionFactory.extractTiledFromTexture(ResourceManager.btIconDyePack, 1, 1);
		StoreTextureRegions.region_icon_glitch_clip = TextureRegionFactory.extractTiledFromTexture(ResourceManager.btIconGlitchClip, 1, 1);
		StoreTextureRegions.region_icon_laser = TextureRegionFactory.extractTiledFromTexture(ResourceManager.btIconLaser, 1, 1);
		StoreTextureRegions.region_icon_wardrobe = TextureRegionFactory.extractTiledFromTexture(ResourceManager.btIconWardrobe, 1, 1);
		StoreTextureRegions.region_rotate_char = TextureRegionFactory.extractFromTexture(ResourceManager.btRotateChar);
		StoreTextureRegions.region_scrap_parts_icon = TextureRegionFactory.extractFromTexture(ResourceManager.btScrapPartsIcon);
		StoreTextureRegions.region_selector_background = TextureRegionFactory.extractFromTexture(ResourceManager.btSelectorBackground);

		StrokeTextureRegions.region_stroke_1 = TextureRegionFactory.extractFromTexture(ResourceManager.btStroke1);
		StrokeTextureRegions.region_stroke_2 = TextureRegionFactory.extractFromTexture(ResourceManager.btStroke2);
		StrokeTextureRegions.region_stroke_3 = TextureRegionFactory.extractFromTexture(ResourceManager.btStroke3);
		StrokeTextureRegions.region_stroke_4 = TextureRegionFactory.extractFromTexture(ResourceManager.btStroke4);
		StrokeTextureRegions.region_stroke_5 = TextureRegionFactory.extractFromTexture(ResourceManager.btStroke5);
		StrokeTextureRegions.region_stroke_6 = TextureRegionFactory.extractFromTexture(ResourceManager.btStroke6);

		HudRegions.region_rain = new TextureRegion[4];
		HudRegions.region_rain[0] = TextureRegionFactory.extractFromTexture(ResourceManager.btRain, 0, 0, 128, 148);
		HudRegions.region_rain[1] = TextureRegionFactory.extractFromTexture(ResourceManager.btRain, 128, 0, 132, 148);
		HudRegions.region_rain[2] = TextureRegionFactory.extractFromTexture(ResourceManager.btRain, 260, 0, 129, 144);
		HudRegions.region_rain[3] = TextureRegionFactory.extractFromTexture(ResourceManager.btRain, 389, 0, 116, 141);
	}

	public boolean isUsingAd() {
		return GameActivity.USE_TAPPX && this.pavAdView.isShown();
	}

	//
	// *********************************

	// EVENTS
	// *********************************

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		Log.d(this.getString(R.string.app_name),
				this.getClass().getSimpleName() + ".onActivityResult(" + requestCode + ", " + resultCode + ")");

		if (requestCode == GooglePlayConstants.GOOGLE_PLAY_REQUEST_CODE && !this.gacGoogleGamesClient.isConnected()) {
			this.isResolvingClientIssue = false;

			switch (resultCode) {
			case RESULT_OK:

				EnvironmentVars.PREFERENCES_EDITOR.putBoolean(PreferenceKeys.KEY_USER_CANCELED_SIGN_IN, false).commit();

				this.gacGoogleGamesClient.connect();
				break;
			case GamesActivityResultCodes.RESULT_SIGN_IN_FAILED:
			case GamesActivityResultCodes.RESULT_RECONNECT_REQUIRED:
			case GamesActivityResultCodes.RESULT_NETWORK_FAILURE:
			case GamesActivityResultCodes.RESULT_SEND_REQUEST_FAILED:
				this.lConnectionCodeQueue.add(ConnectionNotifier.CODE_CONNECTION_FAILED);
				break;
			case RESULT_CANCELED:

				EnvironmentVars.PREFERENCES_EDITOR.putBoolean(PreferenceKeys.KEY_USER_CANCELED_SIGN_IN, true).commit();

				this.lConnectionCodeQueue.add(ConnectionNotifier.CODE_NOT_SIGNED_IN);
				break;
			case GamesActivityResultCodes.RESULT_APP_MISCONFIGURED:
				this.lConnectionCodeQueue.add(ConnectionNotifier.CODE_MISCONFIGURED_APP);
				break;
			}

		}
	}

	@Override
	public void onAdLoaded() {
		this.adLoaded = true;
	}

	@Override
	public void onBackPressed() {
		for (final OnBackPressedListener b : this.lOnBackPressedListeners)
			b.onBackPressed();
	}

	@Override
	protected void onCreate(Bundle pSavedInstanceState) {
		super.onCreate(pSavedInstanceState);
		ActivityProtocols.setTaskDescription(this);

		VertexShaderScripts.loadShaders(this.getAssets());
		FragmentShaderScripts.loadShaders(this.getAssets());

		this.initEnvironmentVars();
		this.initGoogleApiClient();
		this.useMusic = EnvironmentVars.PREFERENCES.getBoolean(PreferenceKeys.KEY_USE_MUSIC, true);
		this.useSound = EnvironmentVars.PREFERENCES.getBoolean(PreferenceKeys.KEY_USE_SOUND, true);
		this.useChaseCamera = EnvironmentVars.PREFERENCES.getBoolean(PreferenceKeys.KEY_USE_CHASE_CAMERA, true);
		this.onUpdateMusicVolume();
		this.onUpdateSoundVolume();

		this.lTappxAdListeners = new ArrayList<TappxAdListener>();
		this.lOnBackPressedListeners = new ArrayList<OnBackPressedListener>();
		this.lOnResumeListeners = new ArrayList<OnResumeListener>();
		this.lCurrentPlayingAudio = new ArrayList<BaseAudioEntity>();
		this.lConnectionCodeQueue = new ArrayList<Integer>();

		this.addAdListener(this);

		if (GameActivity.USE_TAPPX) {
			GameActivity.this.pavAdView = TAPPXAdBanner.ConfigureAndShowAtBottom(GameActivity.this, GameActivity.this.pavAdView,
					"/120940746/Pub-5641-Android-7559");
			GameActivity.this.pavAdView.setAdListener(new AdListener() {
				@Override
				public void onAdLoaded() {
					for (final TappxAdListener listener : GameActivity.this.lTappxAdListeners)
						listener.onAdLoaded();
				}
			});
		}
	}

	@Override
	public Engine onCreateEngine(EngineOptions pEngineOptions) {
		final Engine ENGINE = new LimitedFPSEngine(pEngineOptions, 60) {
			@Override
			public void onDrawFrame(GLState pGLState) throws InterruptedException {

				if (GameActivity.this.screenTextureCallback != null) {

					if (!GameActivity.this.rtScreenRenderer.isInitialized())
						GameActivity.this.rtScreenRenderer.init(pGLState);
					GameActivity.this.rtScreenRenderer.begin(pGLState, true, true);
					{
						super.onDrawFrame(pGLState);
					}
					GameActivity.this.rtScreenRenderer.end(pGLState);

					GameActivity.this.screenTextureCallback.onScreenTextureReceived(GameActivity.this.rtScreenRenderer,
							GameActivity.this.rtScreenRenderer.getBitmap(pGLState));
					GameActivity.this.screenTextureCallback = null;

				}
				try {
					super.onDrawFrame(pGLState);
				} catch (final Exception ex) {
					Log.e(GameActivity.this.getString(R.string.app_name), "DRAW ERROR", ex);
					try {
						((DialogScene) GameActivity.this.mEngine.getScene()).onEngineDrawError();
					} catch (final ClassCastException castEx) {

					}
				}
			}

			@Override
			protected boolean onTouchScene(Scene pScene, TouchEvent pSceneTouchEvent) {
				return false;
			}

			@Override
			public void onUpdate(long pNanosecondsElapsed) throws InterruptedException {
				try {
					if (GameActivity.this.lConnectionCodeQueue != null)
						GameActivity.this.readFromConnectionCodeQueue();
					super.onUpdate(pNanosecondsElapsed);
				} catch (final Exception ex) {
					Log.e(GameActivity.this.getString(R.string.app_name), "UPDATE ERROR", ex);
					try {
						((DialogScene) GameActivity.this.mEngine.getScene()).onEngineUpdateError();
					} catch (final ClassCastException castEx) {

					}
				}
			}
		};

		return ENGINE;
	}

	@Override
	public EngineOptions onCreateEngineOptions() {
		this.gcCamera = new GameCamera(0, 0, 800, 800);
		this.gcCamera.setHUD(new HUD());

		final EngineOptions OPTIONS = new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new FillResolutionPolicy(),
				this.getCamera());

		OPTIONS.getAudioOptions().getSoundOptions().setMaxSimultaneousStreams(15);
		OPTIONS.getAudioOptions().setNeedsMusic(true).setNeedsSound(true);
		OPTIONS.getTouchOptions().setNeedsMultiTouch(true).setTouchEventIntervalMilliseconds(10);

		return OPTIONS;
	}

	@Override
	protected void onCreateResources() {

	}

	@Override
	protected Scene onCreateScene() {
		EnvironmentVars.MAIN_MENU = new MainMenuScene();
		EnvironmentVars.MAIN_MENU.init();
		return EnvironmentVars.MAIN_MENU;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (this.pavAdView != null)
			TAPPXAdBanner.Destroy(this.pavAdView);

		if (this.isGameLoaded())
			System.exit(0);
	}

	/**
	 * Occurs if a font cannot be found in the "assets" folder.
	 */
	public void onFontNotFound() {
		this.onResourceError(1);
	}

	/**
	 * Loads all fonts needed in the game, via the "assets" folder.<br>
	 * Throws an {@link IOException} and calls
	 * {@link GameActivity#onFontNotFound()} if an asset cannot be found.
	 */
	public void onLoadFonts() throws IOException {
		ResourceManager.fFontMain = FontFactory.createFromAsset(this.getFontManager(), this.getTextureManager(), 128, 128,
				TextureOptions.DEFAULT, this.getAssets(), AssetPaths.FONT_MAIN, 16, false, Color.WHITE);
	}

	/**
	 * Loads all textures needed in the loading section of the game, via the
	 * "assets" folder.<br>
	 * Throws an {@link IOException} and calls
	 * {@link GameActivity#onTextureNotFound()} if an asset cannot be found.
	 */
	public void onLoadLoadingTextures() throws IOException {

		ResourceManager.btStrokeMap = this.getTextureFromAssets(AssetPaths.S_STROKE_MAP);
		ResourceManager.btEmpty = new BitmapTextureAtlas(this.getTextureManager(), 1, 1);
		ResourceManager.btLoading = this.getTextureFromAssets(AssetPaths.S_LOADING);
		ResourceManager.btLogo = this.getTextureFromAssets(AssetPaths.S_LOGO);
		ResourceManager.btLogoBackground = this.getTextureFromAssets(AssetPaths.S_LOGO_BACKGROUND);

		MiscRegions.region_empty = TextureRegionFactory.extractTiledFromTexture(ResourceManager.btEmpty, 1, 1);

		MainMenuRegions.region_logo = TextureRegionFactory.extractFromTexture(ResourceManager.btLogo);
		MainMenuRegions.region_logo_background = TextureRegionFactory.extractFromTexture(ResourceManager.btLogoBackground);
		MainMenuRegions.region_loading = TextureRegionFactory.extractFromTexture(ResourceManager.btLoading);
	}

	/**
	 * Loads all music files needed in the game, via the "assets" folder.<br>
	 * Throws an {@link IOException} and calls
	 * {@link GameActivity#onMusicNotFound()} if an asset cannot be found.
	 */
	public void onLoadMusic() throws IOException {
		ResourceManager.mGameOver = this.getMusicFromAssets(AssetPaths.BGM_GAMEOVER);
		ResourceManager.mTheme = this.getMusicFromAssets(AssetPaths.BGM_MAINTHEME);
		ResourceManager.mFirstWave = this.getMusicFromAssets(AssetPaths.BGM_FIRST_WAVE);
		ResourceManager.mNewWave = this.getMusicFromAssets(AssetPaths.BGM_NEW_WAVE);

		this.setUnregisterOnCompletionListener(ResourceManager.mTheme);
		this.setUnregisterOnCompletionListener(ResourceManager.mFirstWave);
		this.setUnregisterOnCompletionListener(ResourceManager.mNewWave);
		ResourceManager.mGameOver.setLooping(true);
		ResourceManager.mNewWave.setVolume(1.25f);
	}

	/**
	 * Loads all sounds needed in the game, via the "assets" folder.<br>
	 * Throws an {@link IOException} and calls
	 * {@link GameActivity#onSoundNotFound()} if an asset cannot be found.
	 */
	public void onLoadSounds() throws IOException {
		//@formatter:off
		ResourceManager.sFootsteps = new Sound[3];
		ResourceManager.sFire0 = new Sound[3];
		ResourceManager.sFire1 = new Sound[3];
		ResourceManager.sFire2 = new Sound[3];
		ResourceManager.sFire3 = new Sound[2];
		
		ResourceManager.mAmbWind = this.getSoundFromAssets(AssetPaths.SND_AMB_WIND);
		ResourceManager.sNotif0 = this.getSoundFromAssets(AssetPaths.SND_NOTIF0);
		ResourceManager.sDryfire = this.getSoundFromAssets(AssetPaths.SND_DRYFIRE);
		ResourceManager.sNotif1 = this.getSoundFromAssets(AssetPaths.SND_NOTIF1);
		ResourceManager.sDialog = this.getSoundFromAssets(AssetPaths.SND_DIALOG);
		ResourceManager.sExplosion = this.getSoundFromAssets(AssetPaths.SND_EXPLOSION);
		ResourceManager.sSiren = this.getSoundFromAssets(AssetPaths.SND_SIREN);
		ResourceManager.sSlam0 = this.getSoundFromAssets(AssetPaths.SND_SLAM0);
		ResourceManager.sRain = this.getSoundFromAssets(AssetPaths.SND_RAIN);
		ResourceManager.sFire0[0] = this.getSoundFromAssets(AssetPaths.SND_FIRE0A);
		ResourceManager.sFire0[1] = this.getSoundFromAssets(AssetPaths.SND_FIRE0B);
		ResourceManager.sFire0[2] = this.getSoundFromAssets(AssetPaths.SND_FIRE0C);
		ResourceManager.sFire1[0] = this.getSoundFromAssets(AssetPaths.SND_FIRE1A);
		ResourceManager.sFire1[1] = this.getSoundFromAssets(AssetPaths.SND_FIRE1B);
		ResourceManager.sFire1[2] = this.getSoundFromAssets(AssetPaths.SND_FIRE1C);
		ResourceManager.sFire2[0] = this.getSoundFromAssets(AssetPaths.SND_FIRE2A);
		ResourceManager.sFire2[1] = this.getSoundFromAssets(AssetPaths.SND_FIRE2B);
		ResourceManager.sFire2[2] = this.getSoundFromAssets(AssetPaths.SND_FIRE2C);
		ResourceManager.sFire3[0] = this.getSoundFromAssets(AssetPaths.SND_FIRE3A);
		ResourceManager.sFire3[1] = this.getSoundFromAssets(AssetPaths.SND_FIRE3B);
		ResourceManager.sFire4 = this.getSoundFromAssets(AssetPaths.SND_FIRE4);
		ResourceManager.sHawk = this.getSoundFromAssets(AssetPaths.SND_HAWK);
		ResourceManager.sMedkit = this.getSoundFromAssets(AssetPaths.SND_MEDKIT);
		ResourceManager.sShotgunPump = this.getSoundFromAssets(AssetPaths.SND_SHOTGUN_PUMP);
		ResourceManager.sThrow = this.getSoundFromAssets(AssetPaths.SND_THROW);
		
		if (Player.USE_FOOTSTEPS) {
			ResourceManager.sFootsteps[0] = this.getSoundFromAssets(AssetPaths.SND_FOOT0);
			ResourceManager.sFootsteps[1] = this.getSoundFromAssets(AssetPaths.SND_FOOT1);
			ResourceManager.sFootsteps[2] = this.getSoundFromAssets(AssetPaths.SND_FOOT2);
		}
		
		ResourceManager.sRain.setLooping(true);
		ResourceManager.mAmbWind.setLooping(true);
		//@formatter:on
	}

	/**
	 * Loads all textures needed in the game, via the "assets" folder.<br>
	 * Throws an {@link IOException} and calls
	 * {@link GameActivity#onTextureNotFound()} if an asset cannot be found.
	 */
	public void onLoadTextures() throws IOException {
		ResourceManager.btAchievements = this.getTextureFromAssets(AssetPaths.S_ACHIEVEMENTS);
		ResourceManager.btCharSleeves02 = this.getTextureFromAssets(AssetPaths.S_CHAR_SLEEVES02);
		ResourceManager.btAlreadyBought = this.getTextureFromAssets(AssetPaths.S_ALREADY_BOUGHT);
		ResourceManager.btAmmoIcon = this.getTextureFromAssets(AssetPaths.S_ICO_AMMO);
		ResourceManager.btBgShadow = this.getTextureFromAssets(AssetPaths.S_BG_SHADOW);
		ResourceManager.btBlood = this.getTextureFromAssets(AssetPaths.S_BLOOD);
		ResourceManager.btCharA01 = this.getTextureFromAssets(AssetPaths.S_CHAR_A01);
		ResourceManager.btCharA01Muzzle = this.getTextureFromAssets(AssetPaths.S_CHAR_A01_MUZZLE);
		ResourceManager.btCharA02 = this.getTextureFromAssets(AssetPaths.S_CHAR_A02);
		ResourceManager.btCharA02Muzzle = this.getTextureFromAssets(AssetPaths.S_CHAR_A02_MUZZLE);
		ResourceManager.btCharA03 = this.getTextureFromAssets(AssetPaths.S_CHAR_A03);
		ResourceManager.btCharA03Muzzle = this.getTextureFromAssets(AssetPaths.S_CHAR_A03_MUZZLE);
		ResourceManager.btCharA04 = this.getTextureFromAssets(AssetPaths.S_CHAR_A04);
		ResourceManager.btCharA04Muzzle = this.getTextureFromAssets(AssetPaths.S_CHAR_A04_MUZZLE);
		ResourceManager.btCharA05 = this.getTextureFromAssets(AssetPaths.S_CHAR_A05);
		ResourceManager.btCharADead = this.getTextureFromAssets(AssetPaths.S_CHAR_A_D);
		ResourceManager.btCharHair01 = this.getTextureFromAssets(AssetPaths.S_CHAR_HAIR01);
		ResourceManager.btCharHair01D = this.getTextureFromAssets(AssetPaths.S_CHAR_HAIR01_D);
		ResourceManager.btCharHair02 = this.getTextureFromAssets(AssetPaths.S_CHAR_HAIR02);
		ResourceManager.btCharLb = this.getTextureFromAssets(AssetPaths.S_CHAR_LB);
		ResourceManager.btCharLbDead = this.getTextureFromAssets(AssetPaths.S_CHAR_LB_D);
		ResourceManager.btCharLbWalk = this.getTextureFromAssets(AssetPaths.S_CHAR_LB_WALK);
		ResourceManager.btCharSkin = this.getTextureFromAssets(AssetPaths.S_CHAR_SKIN);
		ResourceManager.btCharSkinDead = this.getTextureFromAssets(AssetPaths.S_CHAR_SKIN_D);
		ResourceManager.btCharSleeves01 = this.getTextureFromAssets(AssetPaths.S_CHAR_SLEEVES01);
		ResourceManager.btCharSleeves01D = this.getTextureFromAssets(AssetPaths.S_CHAR_SLEEVES01_D);
		ResourceManager.btCharUb01 = this.getTextureFromAssets(AssetPaths.S_CHAR_UB01);
		ResourceManager.btCharUb02 = this.getTextureFromAssets(AssetPaths.S_CHAR_UB02);
		ResourceManager.btCharUb01D = this.getTextureFromAssets(AssetPaths.S_CHAR_UB01_D);
		ResourceManager.btColMap = this.getTextureFromAssets(AssetPaths.S_COL_MAP);
		ResourceManager.btColorSplash = this.getTextureFromAssets(AssetPaths.S_COLOR_SPLASH);
		ResourceManager.btComboBack = this.getTextureFromAssets(AssetPaths.S_HUD_COMBO_BACKGROUND);
		ResourceManager.btComboCanvas = this.getTextureFromAssets(AssetPaths.S_HUD_COMBO_CANVAS);
		ResourceManager.btComboFore = this.getTextureFromAssets(AssetPaths.S_HUD_COMBO_FOREGROUND);
		ResourceManager.btDialog01 = this.getTextureFromAssets(AssetPaths.S_DIALOG01);
		ResourceManager.btDialog02 = this.getTextureFromAssets(AssetPaths.S_DIALOG02);
		ResourceManager.btDialogBtn = this.getTextureFromAssets(AssetPaths.S_DIALOG_BTN);
		ResourceManager.btDissolveMap = this.getTextureFromAssets(AssetPaths.S_DISSOLVE_MAP);
		ResourceManager.btExplosion = this.getTextureFromAssets(AssetPaths.S_EXPLOSION);
		ResourceManager.btExplosionMark = this.getTextureFromAssets(AssetPaths.S_EXPLOSION_MARK);
		ResourceManager.btHBar = this.getTextureFromAssets(AssetPaths.S_HBAR);
		ResourceManager.btHairDisplay = this.getTextureFromAssets(AssetPaths.S_HAIR_DISPLAY);
		ResourceManager.btTorsoDisplay = this.getTextureFromAssets(AssetPaths.S_TORSO_DISPLAY);
		ResourceManager.btHudBoundsIndicator = this.getTextureFromAssets(AssetPaths.S_HUD_BOUNDS_INDICATOR);
		ResourceManager.btHudLoginStates01 = this.getTextureFromAssets(AssetPaths.S_HUD_LOGIN_STATES01);
		ResourceManager.btIconCredits = this.getTextureFromAssets(AssetPaths.S_ICON_CREDITS);
		ResourceManager.btIconDye = this.getTextureFromAssets(AssetPaths.S_ICON_DYE);
		ResourceManager.btIconDyePack = this.getTextureFromAssets(AssetPaths.S_ICON_DYE_PACK);
		ResourceManager.btIconGlitchClip = this.getTextureFromAssets(AssetPaths.S_ICON_GLITCH_CLIP);
		ResourceManager.btIconLaser = this.getTextureFromAssets(AssetPaths.S_ICON_LASER);
		ResourceManager.btIconWardrobe = this.getTextureFromAssets(AssetPaths.S_ICON_WARDROBE);
		ResourceManager.btJoyStickBack = this.getTextureFromAssets(AssetPaths.S_JOYSTICK_BASE);
		ResourceManager.btJoyStickFore = this.getTextureFromAssets(AssetPaths.S_JOYSTICK_STICK);
		ResourceManager.btLeaderboard = this.getTextureFromAssets(AssetPaths.S_LEADERBOARD);
		ResourceManager.btLicense = this.getTextureFromAssets(AssetPaths.S_LICENSE);
		ResourceManager.btOptions = this.getTextureFromAssets(AssetPaths.S_OPTIONS);
		ResourceManager.btOptionsChase = this.getTextureFromAssets(AssetPaths.S_CHASE_ICON);
		ResourceManager.btOptionsLoginStates = this.getTextureFromAssets(AssetPaths.S_HUD_LOGIN_STATES02);
		ResourceManager.btOptionsMusic = this.getTextureFromAssets(AssetPaths.S_OPTIONS_MUSIC);
		ResourceManager.btOptionsSound = this.getTextureFromAssets(AssetPaths.S_OPTIONS_SOUND);
		ResourceManager.btOptionsToggle = this.getTextureFromAssets(AssetPaths.S_OPTIONS_TOGGLE);
		ResourceManager.btProjectileMap = this.getTextureFromAssets(AssetPaths.S_PROJECTILE_MAP);
		ResourceManager.btRain = this.getTextureFromAssets(AssetPaths.S_RAIN);
		ResourceManager.btRotateChar = this.getTextureFromAssets(AssetPaths.S_ROTATE_CHAR);
		ResourceManager.btScrapPartsBig = this.getTextureFromAssets(AssetPaths.S_SCRAP_PART_BIG);
		ResourceManager.btScrapPartsIcon = this.getTextureFromAssets(AssetPaths.S_SCRAP_PART_ICON);
		ResourceManager.btScrapPartsSmall = this.getTextureFromAssets(AssetPaths.S_SCRAP_PART_SMALL);
		ResourceManager.btSelectorBackground = this.getTextureFromAssets(AssetPaths.S_WEPSEL_BACKGROUND);
		ResourceManager.btSelectorCheckmark = this.getTextureFromAssets(AssetPaths.S_WEPSEL_CHECKMARK);
		ResourceManager.btShootButton = this.getTextureFromAssets(AssetPaths.S_FIRE_BTN);
		ResourceManager.btSmoke = this.getTextureFromAssets(AssetPaths.S_SMOKE);
		ResourceManager.btSmokeSmall = this.getTextureFromAssets(AssetPaths.S_SMOKE_SMALL);
		ResourceManager.btSpeedBoostIcon = this.getTextureFromAssets(AssetPaths.S_ICO_BOOST);
		ResourceManager.btStore = this.getTextureFromAssets(AssetPaths.S_STORE);
		ResourceManager.btStroke1 = this.getTextureFromAssets(AssetPaths.S_STROKE1);
		ResourceManager.btStroke2 = this.getTextureFromAssets(AssetPaths.S_STROKE2);
		ResourceManager.btStroke3 = this.getTextureFromAssets(AssetPaths.S_STROKE3);
		ResourceManager.btStroke4 = this.getTextureFromAssets(AssetPaths.S_STROKE4);
		ResourceManager.btStroke5 = this.getTextureFromAssets(AssetPaths.S_STROKE5);
		ResourceManager.btStroke6 = this.getTextureFromAssets(AssetPaths.S_STROKE6);
		ResourceManager.btWave1 = this.getTextureFromAssets(AssetPaths.S_WAVE1);
		ResourceManager.btWepFrame = this.getTextureFromAssets(AssetPaths.S_HUD_WEPFRAME);
		ResourceManager.btWeps = this.getTextureFromAssets(AssetPaths.S_HUD_WEPS);
		ResourceManager.btZCharA01 = this.getTextureFromAssets(AssetPaths.S_ZCHAR_A01);
		ResourceManager.btZCharHair01 = this.getTextureFromAssets(AssetPaths.S_ZCHAR_HAIR01);
		ResourceManager.btZCharHair01D = this.getTextureFromAssets(AssetPaths.S_ZCHAR_HAIR01_D);

		if (SessionScene.USE_LASER_CROSSHAIR)
			ResourceManager.btLaserCrosshair = this.getTextureFromAssets(AssetPaths.S_LASER_CROSSHAIR);
	}

	public void onLoadTexturesToHardware() {
		ResourceManager.fFontMain.load();
		ResourceManager.btEmpty.load();
	}

	/**
	 * Occurs if a music file cannot be found in the "assets" folder.
	 */
	public void onMusicNotFound() {
		this.onResourceError(3);
	}

	@Override
	protected void onPause() {
		super.onPause();
		this.handleMusic(true);

		if (this.pavAdView != null)
			TAPPXAdBanner.Pause(this.pavAdView);
	}

	protected void onResourceError(int errorCode) {
		String message = "";

		switch (errorCode) {
		case 0:
			message = "ERROR! Texture not found";
			break;
		case 1:
			message = "ERROR! Font not found";
			break;
		case 2:
			message = "ERROR! Sound not found";
			break;
		case 3:
			message = "ERROR! Music not found";
			break;
		}

		this.toastOnUIThread(message);
		this.finish();
	}

	@Override
	protected synchronized void onResume() {
		super.onResume();
		this.handleMusic(false);

		if (this.pavAdView != null)
			TAPPXAdBanner.Resume(this.pavAdView);

		TintShaderProgram.getMultipliedInstance().setCompiled(false);
		TintShaderProgram.getNonMultipliedInstance().setCompiled(false);
		RadialGradientShaderProgram.getInstance().setCompiled(false);
	}

	@Override
	public synchronized void onResumeGame() {
		super.onResumeGame();

		if (this.getEngine().getScene() != null)
			try {
				((DialogScene) this.getEngine().getScene()).onResume();
			} catch (final ClassCastException ex) {
			}

		for (final OnResumeListener listener : this.lOnResumeListeners)
			if (listener != null)
				listener.onResume();
	}

	/**
	 * Occurs if a sound file cannot be found in the "assets" folder.
	 */
	public void onSoundNotFound() {
		this.onResourceError(2);
	}

	public void onStartMakeConnection() {
		if (this.assertCanMakeConnection())
			new ConnectionTask().execute(this.gacGoogleGamesClient);
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (this.gacGoogleGamesClient.isConnected())
			this.gacGoogleGamesClient.disconnect();
	}

	public void onSuperBackPressed() {
		super.onBackPressed();
	}

	@Override
	public synchronized void onSurfaceChanged(GLState pGLState, int pWidth, int pHeight) {

		super.onSurfaceChanged(pGLState, pWidth, pHeight);

		final double ASPECT_RATIO = (double) pWidth / (double) pHeight;

		final float CAMERA_MIN_Y = this.gcCamera.getYMin();

		this.gcCamera.setYMax(CAMERA_MIN_Y + 800 / (float) ASPECT_RATIO);

		if (this.onCameraBoundsChanged != null && this.onCameraBoundsChanged.size() > 0)
			for (int i = this.onCameraBoundsChanged.size() - 1; i > -1; i--)
				this.onCameraBoundsChanged.get(i).run();

		this.rtScreenRenderer = new RenderTexture(this.getTextureManager(), (int) this.width(), (int) this.height());

		final Scene SCENE = this.getEngine().getScene();
		for (int i = 0; i < SCENE.getChildCount(); i++) {
			final IEntity ENTITY = SCENE.getChildByIndex(i);

			if (ENTITY instanceof RectangularShape)
				((RectangularShape) ENTITY).getVertexBufferObject().setDirtyOnHardware();
		}
	}

	/**
	 * Occurs if a texture cannot be found in the "assets" folder.
	 */
	public void onTextureNotFound() {
		this.onResourceError(0);
	}

	public void onToggleAccountSync(final boolean signOut) {
		final GoogleApiClient CLIENT = this.getGoogleApiClient();

		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (CLIENT.isConnected()) {
					if (signOut) {
						Games.signOut(CLIENT);
						GameActivity.this.addConnectionCode(ConnectionNotifier.CODE_CONNECTION_FAILED);
					}
					CLIENT.disconnect();
					if (!signOut)
						GameActivity.this.addConnectionCode(ConnectionNotifier.CODE_NOT_SIGNED_IN);
				} else if (!CLIENT.isConnecting())
					CLIENT.connect();

			}
		});
	}

	public void onToggleChaseCamera() {
		this.useChaseCamera = !this.useChaseCamera;

		this.onUpdateSoundVolume();

		EnvironmentVars.PREFERENCES_EDITOR.putBoolean(PreferenceKeys.KEY_USE_CHASE_CAMERA, this.useChaseCamera).commit();
	}

	public void onToggleMusic() {
		this.useMusic = !this.useMusic;

		this.onUpdateMusicVolume();

		EnvironmentVars.PREFERENCES_EDITOR.putBoolean(PreferenceKeys.KEY_USE_MUSIC, this.useMusic).commit();
	}

	public void onToggleSound() {
		this.useSound = !this.useSound;

		this.onUpdateSoundVolume();

		EnvironmentVars.PREFERENCES_EDITOR.putBoolean(PreferenceKeys.KEY_USE_SOUND, this.useSound).commit();
	}

	private void onUpdateMusicVolume() {
		if (this.useMusic)
			this.getMusicManager().setMasterVolume(1);
		else
			this.getMusicManager().setMasterVolume(0);
	}

	private void onUpdateSoundVolume() {
		if (this.useSound)
			this.getSoundManager().setMasterVolume(1f);
		else
			this.getSoundManager().setMasterVolume(0f);
	}

	@Override
	public synchronized void onWindowFocusChanged(boolean pHasWindowFocus) {
		super.onWindowFocusChanged(pHasWindowFocus);
		if (pHasWindowFocus)
			ActivityProtocols.setDecorViewFlags(this.getWindow());
	}

	private void readFromConnectionCodeQueue() {
		if (this.lConnectionCodeQueue.size() > 0) {

			final Scene SCENE = this.getEngine().getScene();

			if (SCENE != null) {
				if (this.currentConnectionCode != this.lConnectionCodeQueue.get(0)) {
					try {
						((DialogScene) SCENE).onConnectionChanged(this.lConnectionCodeQueue.get(0));
					} catch (final ClassCastException ex) {

					}
					this.currentConnectionCode = this.lConnectionCodeQueue.get(0);
				}
				this.lConnectionCodeQueue.remove(0);
			}
		}
	}

	public void refreshStaticData() {
		PreferenceKeysMeta.encodeBooleanArray(PreferenceKeys.KEY_GLITCH_CLIP_ITEMS, EnvironmentVars.PREFERENCES_EDITOR,
				StaticData.glitchClipItems);
		PreferenceKeysMeta.encodeBooleanArray(PreferenceKeys.KEY_LASER_SIGHT_ITEMS, EnvironmentVars.PREFERENCES_EDITOR,
				StaticData.laserSightItems);
		EnvironmentVars.PREFERENCES_EDITOR.putBoolean(PreferenceKeys.KEY_DOMINADOR_REPEATED_FIRE, StaticData.dominador_repeatedFire);
		EnvironmentVars.PREFERENCES_EDITOR.putBoolean(PreferenceKeys.KEY_SNIPER_INJECTION_TIPS, StaticData.sniper_injectionTips);
	}

	public void registerOnCameraBoundsChanged(Runnable action) {
		if (this.onCameraBoundsChanged == null)
			this.onCameraBoundsChanged = new ArrayList<Runnable>();
		this.onCameraBoundsChanged.add(action);
	}

	/**
	 * Registers a BaseAudioEntity object. <br>
	 * All registered BaseAudioEntity objects will conveniently pause/resume to
	 * adapt to the Activity's lifecycle.
	 * 
	 * @param sound
	 *            Object to register.
	 * @see GameActivity#unregisterSound(Music)
	 */
	public void registerSound(BaseAudioEntity sound) {
		if (this.lCurrentPlayingAudio.contains(sound))
			return;
		this.lCurrentPlayingAudio.add(sound);
	}

	// *********************************
	//

	// SPECIAL METHODS
	// *********************************

	public void removeAdListener(TappxAdListener adListener) {
		this.lTappxAdListeners.remove(adListener);
	}

	public void removeOnBackPressedListener(OnBackPressedListener onBackPressedListener) {
		this.lOnBackPressedListeners.remove(onBackPressedListener);
	}

	public void removeOnResumeListener(OnResumeListener onResumeListener) {
		this.lOnResumeListeners.remove(onResumeListener);
	}

	public void requestScreenTexture(ScreenTextureCallback callback) {
		this.screenTextureCallback = callback;
	}

	@Override
	public void setContentView(View view, LayoutParams params) {
		super.setContentView(view, params);
	}

	// *********************************
	//

	private void setUnregisterOnCompletionListener(Music music) {
		final Music MUSIC = music;

		MUSIC.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				GameActivity.this.unregisterSound(MUSIC);
			}
		});
	}

	public void showAd() {
		this.changeAdVisibility(View.VISIBLE);
	}

	public void simulateHomeButton() {
		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				final Intent INTENT = new Intent(Intent.ACTION_MAIN);
				INTENT.addCategory(Intent.CATEGORY_HOME);
				GameActivity.this.startActivity(INTENT);
			}
		});

	}

	public void unregisterOnCameraBoundsChanged(Runnable action) {
		if (this.onCameraBoundsChanged != null)
			this.onCameraBoundsChanged.remove(action);
	}

	/**
	 * Unregisters a BaseAudioEntity object.
	 * 
	 * @param music
	 *            Object to unregister.
	 * @see GameActivity#registerSound(BaseAudioEntity)
	 */
	public void unregisterSound(BaseAudioEntity sound) {
		this.lCurrentPlayingAudio.remove(sound);
	}

	public boolean useChaseCamera() {
		return this.useChaseCamera;
	}

	public boolean useMusic() {
		return this.useMusic;
	}

	public boolean useSound() {
		return this.useSound;
	}

	public boolean isAdLoaded() {
		if (!this.isUsingAd())
			return false;
		return this.adLoaded;
	}

	/**
	 * Returns the width of the screen.
	 * 
	 * @return The width of the screen, in pixels.
	 */
	public float width() {
		return this.getCamera().getXMax() - this.getCamera().getXMin();
	}
}
