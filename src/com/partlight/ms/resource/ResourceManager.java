package com.partlight.ms.resource;

import org.andengine.audio.music.Music;
import org.andengine.audio.sound.Sound;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.bitmap.BitmapTexture;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;

/**
 * Contains all used resources and their respective asset paths.
 * 
 * @author Johan Svensson - partLight Entertainment
 */
public final class ResourceManager {

	/**
	 * Contains all resources respective asset paths.
	 * 
	 * @author Johan Svensson - partLight Entertainment
	 */
	public static class AssetPaths {
		public static final String	BGM_GAMEOVER	= "audio/bgm/bgm_gameover.mp3";
		public static final String	BGM_MAINTHEME	= "audio/bgm/bgm_theme.mp3";
		public static final String	BGM_FIRST_WAVE	= "audio/bgm/bgm_first_wave.wav";
		public static final String	BGM_NEW_WAVE	= "audio/bgm/bgm_new_wave.wav";

		public static final String	SND_AMB_WIND		= "audio/snd/snd_amb_wind.wav";
		public static final String	SND_DIALOG			= "audio/snd/snd_dialog.wav";
		public static final String	SND_EXPLOSION		= "audio/snd/snd_explosion.wav";
		public static final String	SND_FIRE0A			= "audio/snd/snd_fire0a.wav";
		public static final String	SND_FIRE0B			= "audio/snd/snd_fire0b.wav";
		public static final String	SND_FIRE0C			= "audio/snd/snd_fire0c.wav";
		public static final String	SND_FIRE1A			= "audio/snd/snd_fire1a.wav";
		public static final String	SND_FIRE1B			= "audio/snd/snd_fire1b.wav";
		public static final String	SND_FIRE1C			= "audio/snd/snd_fire1c.wav";
		public static final String	SND_FIRE2A			= "audio/snd/snd_fire2a.wav";
		public static final String	SND_FIRE2B			= "audio/snd/snd_fire2b.wav";
		public static final String	SND_FIRE2C			= "audio/snd/snd_fire2c.wav";
		public static final String	SND_FIRE3A			= "audio/snd/snd_fire3a.wav";
		public static final String	SND_FIRE3B			= "audio/snd/snd_fire3b.wav";
		public static final String	SND_FIRE4			= "audio/snd/snd_fire4.wav";
		public static final String	SND_FOOT0			= "audio/snd/snd_foot0.wav";
		public static final String	SND_FOOT1			= "audio/snd/snd_foot1.wav";
		public static final String	SND_FOOT2			= "audio/snd/snd_foot2.wav";
		public static final String	SND_HAWK			= "audio/snd/snd_hawk.wav";
		public static final String	SND_MEDKIT			= "audio/snd/snd_medkit.wav";
		public static final String	SND_NOTIF0			= "audio/snd/snd_notif0.wav";
		public static final String	SND_NOTIF1			= "audio/snd/snd_notif1.wav";
		public static final String	SND_RAIN			= "audio/snd/snd_rain.wav";
		public static final String	SND_SHOTGUN_PUMP	= "audio/snd/snd_shotgun_pump.wav";
		public static final String	SND_SIREN			= "audio/snd/snd_siren.wav";
		public static final String	SND_SLAM0			= "audio/snd/snd_slam0.wav";
		public static final String	SND_THROW			= "audio/snd/snd_throw.wav";
		public static final String	SND_DRYFIRE			= "audio/snd/snd_dryfire.wav";

		public static final String TMX_LEVEL01 = "graphics/tilemaps/tmx_level01.tmx";

		public static final String FONT_MAIN = "graphics/font/font_main.otf";

		public static final String	S_ACHIEVEMENTS			= "graphics/icons/s_achievements.png";
		public static final String	S_ALREADY_BOUGHT		= "graphics/mainmenu/store/s_already_bought.png";
		public static final String	S_BG_SHADOW				= "graphics/mainmenu/s_bg_shadow.png";
		public static final String	S_BLOOD					= "graphics/gfx/s_blood.png";
		public static final String	S_CHAR_A01				= "graphics/char/a/s_char_a01.png";
		public static final String	S_CHAR_A01_MUZZLE		= "graphics/char/a/s_char_a01_muzzle.png";
		public static final String	S_CHAR_A02				= "graphics/char/a/s_char_a02.png";
		public static final String	S_CHAR_A02_MUZZLE		= "graphics/char/a/s_char_a02_muzzle.png";
		public static final String	S_CHAR_A03				= "graphics/char/a/s_char_a03.png";
		public static final String	S_CHAR_A03_MUZZLE		= "graphics/char/a/s_char_a03_muzzle.png";
		public static final String	S_CHAR_A04				= "graphics/char/a/s_char_a04.png";
		public static final String	S_CHAR_A04_MUZZLE		= "graphics/char/a/s_char_a04_muzzle.png";
		public static final String	S_CHAR_A05				= "graphics/char/a/s_char_a05.png";
		public static final String	S_CHAR_A_D				= "graphics/char/a/s_char_a_d.png";
		public static final String	S_CHAR_HAIR01			= "graphics/char/hair/s_char_hair01.png";
		public static final String	S_CHAR_HAIR01_D			= "graphics/char/hair/s_char_hair01_d.png";
		public static final String	S_CHAR_HAIR02			= "graphics/char/hair/s_char_hair02.png";
		public static final String	S_CHAR_LB				= "graphics/char/s_char_lb.png";
		public static final String	S_CHAR_LB_D				= "graphics/char/s_char_lb_d.png";
		public static final String	S_CHAR_LB_WALK			= "graphics/char/s_char_lb_walk.png";
		public static final String	S_CHAR_SKIN				= "graphics/char/s_char_skin.png";
		public static final String	S_CHAR_SKIN_D			= "graphics/char/s_char_skin_d.png";
		public static final String	S_CHAR_SLEEVES01		= "graphics/char/sleeves/s_char_sleeves01.png";
		public static final String	S_CHAR_SLEEVES01_D		= "graphics/char/sleeves/s_char_sleeves01_d.png";
		public static final String	S_CHAR_SLEEVES02		= "graphics/char/sleeves/s_char_sleeves02.png";
		public static final String	S_CHAR_UB01				= "graphics/char/ub/s_char_ub01.png";
		public static final String	S_CHAR_UB02				= "graphics/char/ub/s_char_ub02.png";
		public static final String	S_CHAR_UB01_D			= "graphics/char/ub/s_char_ub01_d.png";
		public static final String	S_CHASE_ICON			= "graphics/hud/s_chase_icon.png";
		public static final String	S_COLOR_SPLASH			= "graphics/icons/s_color_splash.png";
		public static final String	S_COL_MAP				= "graphics/collectibles/s_col_map.png";
		public static final String	S_ICON_JAMMER			= "graphics/icons/store/s_icon_jammer.png";
		public static final String	S_DIALOG01				= "graphics/hud/dialog_box/s_dialog01.png";
		public static final String	S_DIALOG02				= "graphics/hud/dialog_box/s_dialog02.png";
		public static final String	S_DIALOG_BTN			= "graphics/hud/dialog_box/s_dialog_btn.png";
		public static final String	S_DISSOLVE_MAP			= "graphics/gfx/maps/s_dissolve_map.jpeg";
		public static final String	S_EXPLOSION				= "graphics/gfx/s_explosion.png";
		public static final String	S_EXPLOSION_MARK		= "graphics/gfx/s_explosion_mark.png";
		public static final String	S_FIRE_BTN				= "graphics/hud/session/s_fire_btn.png";
		public static final String	S_HAIR_DISPLAY			= "graphics/icons/s_hair_display.png";
		public static final String	S_TORSO_DISPLAY			= "graphics/icons/s_torso_display.png";
		public static final String	S_HBAR					= "graphics/gfx/s_hbar.png";
		public static final String	S_HUD_BOUNDS_INDICATOR	= "graphics/hud/s_bounds_indicator.png";
		public static final String	S_HUD_COMBO_BACKGROUND	= "graphics/hud/s_hud_combo_background.png";
		public static final String	S_HUD_COMBO_CANVAS		= "graphics/hud/s_hud_combo_canvas.png";
		public static final String	S_HUD_COMBO_FOREGROUND	= "graphics/hud/s_hud_combo_foreground.png";
		public static final String	S_HUD_LOGIN_STATES01	= "graphics/hud/s_hud_login_states01.png";
		public static final String	S_HUD_LOGIN_STATES02	= "graphics/hud/s_hud_login_states02.png";
		public static final String	S_HUD_WEPFRAME			= "graphics/hud/s_hud_wepframe.png";
		public static final String	S_HUD_WEPS				= "graphics/hud/s_hud_weps.png";
		public static final String	S_ICON_CREDITS			= "graphics/icons/options/s_icon_credits.png";
		public static final String	S_ICON_DYE				= "graphics/icons/store/s_icon_dye.png";
		public static final String	S_ICON_DYE_PACK			= "graphics/icons/store/s_icon_dye_pack.png";
		public static final String	S_ICON_GLITCH_CLIP		= "graphics/icons/store/s_icon_glitch_clip.png";
		public static final String	S_ICON_LASER			= "graphics/icons/store/s_icon_laser.png";
		public static final String	S_ICON_WARDROBE			= "graphics/icons/store/s_icon_wardrobe.png";
		public static final String	S_ICO_AMMO				= "graphics/hud/session/s_ico_ammo.png";
		public static final String	S_ICO_BOOST				= "graphics/hud/session/s_ico_boost.png";
		public static final String	S_JOYSTICK_BASE			= "graphics/hud/session/s_joystick_base.png";
		public static final String	S_JOYSTICK_STICK		= "graphics/hud/session/s_joystick_stick.png";
		public static final String	S_LASER_CROSSHAIR		= "graphics/hud/session/s_laser_crosshair.png";
		public static final String	S_LEADERBOARD			= "graphics/icons/s_leaderboard.png";
		public static final String	S_LICENSE				= "graphics/mainmenu/s_license.png";
		public static final String	S_LOADING				= "graphics/icons/loading/s_loading.png";
		public static final String	S_LOGO					= "graphics/mainmenu/s_logo.png";
		public static final String	S_LOGO_BACKGROUND		= "graphics/mainmenu/s_logo_background.png";
		public static final String	S_OPTIONS				= "graphics/icons/s_options.png";
		public static final String	S_STROKE1				= "graphics/strokes/s_stroke1.png";
		public static final String	S_STROKE2				= "graphics/strokes/s_stroke2.png";
		public static final String	S_STROKE3				= "graphics/strokes/s_stroke3.png";
		public static final String	S_STROKE4				= "graphics/strokes/s_stroke4.png";
		public static final String	S_STROKE5				= "graphics/strokes/s_stroke5.png";
		public static final String	S_STROKE6				= "graphics/strokes/s_stroke6.png";
		public static final String	S_OPTIONS_MUSIC			= "graphics/hud/options/s_options_music.png";
		public static final String	S_OPTIONS_SOUND			= "graphics/hud/options/s_options_sound.png";
		public static final String	S_OPTIONS_TOGGLE		= "graphics/hud/options/s_options_toggle.png";
		public static final String	S_PROJECTILE_MAP		= "graphics/projectile/s_projectile_map.png";
		public static final String	S_RAIN					= "graphics/gfx/rain/s_rain.png";
		public static final String	S_ROTATE_CHAR			= "graphics/mainmenu/store/s_rotate_char.png";
		public static final String	S_SCRAP_PART_BIG		= "graphics/icons/scrap_parts/s_scrap_part_big.png";
		public static final String	S_SCRAP_PART_ICON		= "graphics/icons/scrap_parts/s_scrap_part_icon.png";
		public static final String	S_SCRAP_PART_SMALL		= "graphics/icons/scrap_parts/s_scrap_part_small.png";
		public static final String	S_SMOKE					= "graphics/gfx/s_smoke.png";
		public static final String	S_SMOKE_SMALL			= "graphics/gfx/s_smoke_small.png";
		public static final String	S_STORE					= "graphics/icons/s_store.png";
		public static final String	S_STROKE_MAP			= "graphics/gfx/maps/s_stroke_map.jpeg";
		public static final String	S_WAVE1					= "graphics/hud/s_wave1.png";
		public static final String	S_WEPSEL_BACKGROUND		= "graphics/mainmenu/store/s_wepsel_background.png";
		public static final String	S_WEPSEL_CHECKMARK		= "graphics/icons/store/s_wepsel_checkmark.png";
		public static final String	S_ZCHAR_A01				= "graphics/char/a/s_zchar_a01.png";
		public static final String	S_ZCHAR_HAIR01			= "graphics/char/hair/s_zchar_hair01.png";
		public static final String	S_ZCHAR_HAIR01_D		= "graphics/char/hair/s_zchar_hair01_d.png";
		public static final String	S_ZOMBIE01_A			= "graphics/char/zombie01/a/s_zombie01_a.png";
	}

	public static class BloodRegions {
		public static TextureRegion	region_blood01;
		public static TextureRegion	region_blood02;
		public static TextureRegion	region_blood03;
	}

	public static class DialogRegions {
		public static TextureRegion	region_dialog01;
		public static TextureRegion	region_dialog02;
		public static TextureRegion	region_dialog_btn;

	}

	public static class HudRegions {
		public static TextureRegion			region_ammo_icon;
		public static TextureRegion			region_laser_crosshair;
		public static TextureRegion			region_boost_icon;
		public static TextureRegion			region_bounds_indicator;
		public static TiledTextureRegion	region_col_map;
		public static TextureRegion			region_combo_back;
		public static TextureRegion			region_combo_canvas;
		public static TextureRegion			region_combo_fore;
		public static TextureRegion			region_js_foreground;
		public static TiledTextureRegion	region_projectile_map;
		public static TiledTextureRegion	region_explosion_mark;
		public static TiledTextureRegion	region_smoke;
		public static TextureRegion			region_smoke_small01;
		public static TextureRegion			region_smoke_small02;
		public static TiledTextureRegion	region_explosion;
		public static TiledTextureRegion	region_fire;
		public static TiledTextureRegion	region_js_background;
		public static TiledTextureRegion	region_wepframe;
		public static TiledTextureRegion	region_weps;
		public static TiledTextureRegion	region_hbar;
		public static TiledTextureRegion	region_options_toggle;
		public static TiledTextureRegion	region_options_music;
		public static TiledTextureRegion	region_options_sound;
		public static TiledTextureRegion	region_options_chase;
		public static TextureRegion[]		region_wave_1;
		public static TextureRegion[]		region_rain;
	}

	public static class MainMenuRegions {
		public static TextureRegion	region_achievements;
		public static TextureRegion	region_bg_shadow;
		public static TextureRegion	region_leaderboard;
		public static TextureRegion	region_loading;
		public static TextureRegion	region_logo;
		public static TextureRegion	region_logo_background;
		public static TextureRegion	region_options;
		public static TextureRegion	region_store;
		public static TextureRegion	region_license;
	}

	public static class MiscRegions {
		public static TiledTextureRegion	region_empty;
		public static TiledTextureRegion	region_tiled_empty;
		public static TiledTextureRegion	region_hud_login_states01;
		public static TiledTextureRegion	region_hud_login_states02;
		public static TextureRegion			region_scrap_part;
		public static TextureRegion			region_scrap_part_icon02;
	}

	public static class OptionsTextureRegions {
		public static TiledTextureRegion region_icon_credits;
	}

	public static class PlayerRegions {
		public static TiledTextureRegion	region_a01;
		public static TiledTextureRegion	region_a01_muzzle;
		public static TiledTextureRegion	region_a02;
		public static TiledTextureRegion	region_a02_muzzle;
		public static TiledTextureRegion	region_a03;
		public static TiledTextureRegion	region_a03_muzzle;
		public static TiledTextureRegion	region_a04;
		public static TiledTextureRegion	region_a04_muzzle;
		public static TiledTextureRegion	region_a05;
		public static TiledTextureRegion[]	region_sleeves;
	}

	public static class SharedCharRegions {
		public static TiledTextureRegion	region_a_d;
		public static TiledTextureRegion	region_lb;
		public static TiledTextureRegion	region_lb_d;
		public static TiledTextureRegion	region_lb_walk;
		public static TiledTextureRegion	region_skin01;
		public static TiledTextureRegion	region_skin01_d;
		public static TiledTextureRegion[]	region_hair;
		public static TiledTextureRegion[]	region_hair_d;
		public static TiledTextureRegion[]	region_sleeves;
		public static TiledTextureRegion[]	region_sleeves_d;
		public static TiledTextureRegion[]	region_ub;
		public static TiledTextureRegion[]	region_ub_d;
	}

	public static class StoreTextureRegions {
		public static TextureRegion			region_already_bought;
		public static TextureRegion			region_checkmark;
		public static TextureRegion			region_color_splash;
		public static TextureRegion			region_rotate_char;
		public static TextureRegion			region_scrap_parts_icon;
		public static TextureRegion			region_selector_background;
		public static TiledTextureRegion	region_hair_display;
		public static TiledTextureRegion	region_torso_display;
		public static TiledTextureRegion	region_icon_dye;
		public static TiledTextureRegion	region_icon_dye_pack;
		public static TiledTextureRegion	region_icon_glitch_clip;
		public static TiledTextureRegion	region_icon_laser;
		public static TiledTextureRegion	region_icon_wardrobe;
		public static TiledTextureRegion	region_icon_jammer;

	}

	public static class StrokeTextureRegions {
		public static TextureRegion	region_stroke_1;
		public static TextureRegion	region_stroke_2;
		public static TextureRegion	region_stroke_3;
		public static TextureRegion	region_stroke_4;
		public static TextureRegion	region_stroke_5;
		public static TextureRegion	region_stroke_6;

	}

	public static class ZombieTextureRegions {
		public static class Zombie01 {
			public static TiledTextureRegion region_a01;
		}

		public static class Zombie02 {
			public static TiledTextureRegion	region_ub;
			public static TiledTextureRegion	region_d;
			public static TiledTextureRegion	region_a;
			public static TiledTextureRegion	region_hair;
			public static TiledTextureRegion	region_hair_d;
		}
	}

	public static BitmapTexture			btAchievements;
	public static BitmapTexture			btAlreadyBought;
	public static BitmapTexture			btAmmoIcon;
	public static BitmapTexture			btBgShadow;
	public static BitmapTexture			btBlood;
	public static BitmapTexture			btCharA01;
	public static BitmapTexture			btCharA01Muzzle;
	public static BitmapTexture			btCharA02;
	public static BitmapTexture			btCharA02Muzzle;
	public static BitmapTexture			btCharA03;
	public static BitmapTexture			btCharA03Muzzle;
	public static BitmapTexture			btCharA04;
	public static BitmapTexture			btCharA04Muzzle;
	public static BitmapTexture			btCharA05;
	public static BitmapTexture			btCharADead;
	public static BitmapTexture			btCharHair01;
	public static BitmapTexture			btCharHair01D;
	public static BitmapTexture			btCharHair02;
	public static BitmapTexture			btCharLb;
	public static BitmapTexture			btCharLbDead;
	public static BitmapTexture			btCharLbWalk;
	public static BitmapTexture			btCharSkin;
	public static BitmapTexture			btCharSkinDead;
	public static BitmapTexture			btCharSleeves01;
	public static BitmapTexture			btCharSleeves01D;
	public static BitmapTexture			btCharSleeves02;
	public static BitmapTexture			btCharUb01;
	public static BitmapTexture			btCharUb02;
	public static BitmapTexture			btCharUb01D;
	public static BitmapTexture			btColMap;
	public static BitmapTexture			btColorSplash;
	public static BitmapTexture			btComboBack;
	public static BitmapTexture			btComboCanvas;
	public static BitmapTexture			btComboFore;
	public static BitmapTexture			btDialog01;
	public static BitmapTexture			btDialog02;
	public static BitmapTexture			btDialogBtn;
	public static BitmapTexture			btDissolveMap;
	public static BitmapTexture			btExplosion;
	public static BitmapTexture			btExplosionMark;
	public static BitmapTexture			btHBar;
	public static BitmapTexture			btHairDisplay;
	public static BitmapTexture			btTorsoDisplay;
	public static BitmapTexture			btHudBoundsIndicator;
	public static BitmapTexture			btHudLoginStates01;
	public static BitmapTexture			btIconCredits;
	public static BitmapTexture			btIconJammer;
	public static BitmapTexture			btIconDye;
	public static BitmapTexture			btIconDyePack;
	public static BitmapTexture			btIconGlitchClip;
	public static BitmapTexture			btIconLaser;
	public static BitmapTexture			btIconWardrobe;
	public static BitmapTexture			btJoyStickBack;
	public static BitmapTexture			btJoyStickFore;
	public static BitmapTexture			btLaserCrosshair;
	public static BitmapTexture			btLeaderboard;
	public static BitmapTexture			btLicense;
	public static BitmapTexture			btLoading;
	public static BitmapTexture			btLogo;
	public static BitmapTexture			btLogoBackground;
	public static BitmapTexture			btOptions;
	public static BitmapTexture			btOptionsChase;
	public static BitmapTexture			btOptionsLoginStates;
	public static BitmapTexture			btOptionsMusic;
	public static BitmapTexture			btOptionsSound;
	public static BitmapTexture			btOptionsToggle;
	public static BitmapTexture			btProjectileMap;
	public static BitmapTexture			btRain;
	public static BitmapTexture			btRotateChar;
	public static BitmapTexture			btScrapPartsBig;
	public static BitmapTexture			btScrapPartsIcon;
	public static BitmapTexture			btScrapPartsSmall;
	public static BitmapTexture			btSelectorBackground;
	public static BitmapTexture			btSelectorCheckmark;
	public static BitmapTexture			btShootButton;
	public static BitmapTexture			btSmoke;
	public static BitmapTexture			btSmokeSmall;
	public static BitmapTexture			btSpeedBoostIcon;
	public static BitmapTexture			btStore;
	public static BitmapTexture			btStroke1;
	public static BitmapTexture			btStroke2;
	public static BitmapTexture			btStroke3;
	public static BitmapTexture			btStroke4;
	public static BitmapTexture			btStroke5;
	public static BitmapTexture			btStroke6;
	public static BitmapTexture			btStrokeMap;
	public static BitmapTexture			btWepFrame;
	public static BitmapTexture			btWeps;
	public static BitmapTexture			btZCharA01;
	public static BitmapTexture			btZCharHair01;
	public static BitmapTexture			btZCharHair01D;
	public static BitmapTexture			btWave1;
	public static BitmapTextureAtlas	btEmpty;
	public static BitmapTextureAtlas	btWhite;
	public static Font					fFontMain;
	public static Music					mFirstWave;
	public static Music					mGameOver;
	public static Music					mNewWave;
	public static Music					mTheme;
	public static Sound					mAmbWind;
	public static Sound					sExplosion;
	public static Sound					sFire4;
	public static Sound					sMedkit;
	public static Sound					sNotif0;
	public static Sound					sNotif1;
	public static Sound					sRain;
	public static Sound					sShotgunPump;
	public static Sound					sSiren;
	public static Sound					sSlam0;
	public static Sound					sThrow;
	public static Sound					sDryfire;

	/**
	 * Credit to Mike Koenig on SoundBible under the Attribution 3.0 license.
	 */
	public static Sound		sHawk;
	public static Sound[]	sFire0;
	public static Sound[]	sFire1;
	public static Sound[]	sFire2;
	public static Sound[]	sFire3;
	public static Sound[]	sFootsteps;

	public static Sound sDialog;
}
