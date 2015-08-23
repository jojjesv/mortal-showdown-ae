package com.partlight.ms.scene.mainmenu;

import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.VerticalAlign;
import org.andengine.util.color.Color;
import org.andengine.util.modifier.ease.EaseCubicIn;

import com.partlight.ms.activity.GameActivity;
import com.partlight.ms.entity.EntityAdPosition;
import com.partlight.ms.entity.ShadowedText;
import com.partlight.ms.entity.mainmenu.MainMenuBackground;
import com.partlight.ms.entity.mainmenu.MainMenuEntity;
import com.partlight.ms.entity.mainmenu.StartLabel;
import com.partlight.ms.mainmenu.touch.MainMenuTouchHandler;
import com.partlight.ms.resource.EnvironmentVars;
import com.partlight.ms.resource.EnvironmentVars.PreferenceKeys;
import com.partlight.ms.resource.ResourceManager;
import com.partlight.ms.resource.ResourceManager.MainMenuRegions;
import com.partlight.ms.scene.mainmenu.container.Container;
import com.partlight.ms.shader.TintShaderProgram;
import com.partlight.ms.util.ColorConstants;
import com.partlight.ms.util.EntityUtils;
import com.partlight.ms.util.Fade;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

import static com.partlight.ms.resource.EnvironmentVars.MAIN_CONTEXT;

class MainMenuInitializer {

	private final MainMenuScene mmsContext;

	public MainMenuInitializer(MainMenuScene context) {
		this.mmsContext = context;
	}

	public void init() {

		if (!MainMenuScene.areResourcesLoaded) {
			this.mmsContext.preLaunch();
			return;
		}

		if (EnvironmentVars.PREFERENCES.getBoolean(PreferenceKeys.KEY_IS_FIRST_LAUNCH, true)) {
			this.mmsContext.isNavDemoShowing = true;
			EnvironmentVars.PREFERENCES_EDITOR.putBoolean(PreferenceKeys.KEY_IS_FIRST_LAUNCH, false);
			EnvironmentVars.PREFERENCES_EDITOR.commit();
		}

		ResourceManager.btBgShadow.load();
		ResourceManager.btCharA01.load();
		ResourceManager.btCharLb.load();
		ResourceManager.btCharLbWalk.load();
		ResourceManager.btCharSkin.load();
		ResourceManager.btCharUb01.load();
		ResourceManager.btDissolveMap.load();
		ResourceManager.btZCharA01.load();

		this.mmsContext.currentSection = MainMenuScene.SECTION_MAIN;

		if (MainMenuScene.postSession) {
			this.initTouchHandler();
			this.mmsContext.playThemeSong();
		}

		this.initBackground();

		this.mmsContext.getEntityYSorter().setContext(this.mmsContext.mmbBackground);
		this.mmsContext.registerUpdateHandler(this.mmsContext.getEntityYSorter());

		this.initContainers();
		this.initVersionLabel();
		this.initDevMark();
		this.initFade();
		this.initSwipeIcons();

		this.mmsContext.getSectionContainer(MainMenuScene.SECTION_MAIN).setY(0);

		this.initStartLabel();

		EnvironmentVars.MAIN_CONTEXT.addOnBackPressedListener(this.mmsContext);

		MainMenuScene.loadContainerTextures(this.mmsContext.currentSection);

		this.mmsContext.sortChildren();
	}

	protected void initBackground() {
		this.mmsContext.setBackground(MainMenuScene.BACKGROUND);
		this.mmsContext.mmbBackground = new MainMenuBackground(this.mmsContext);
		this.mmsContext.attachChild(this.mmsContext.mmbBackground);
	}

	protected void initContainers() {
		this.mmsContext.cMainContainer = new Container() {
			@Override
			public void onAttached() {
				ResourceManager.btStroke4.load();
			}

			@Override
			public void onDetached() {
				ResourceManager.btStroke4.unload();
			}

			@Override
			public void setScale(float pScale) {
				super.setScale(pScale);
				MainMenuInitializer.this.mmsContext.onMainContainerSetScale(pScale);
			}

			@Override
			public void setY(float pY) {
				super.setY(pY);
				MainMenuInitializer.this.mmsContext.onMainContainerSetY(pY);
			}
		};
		this.mmsContext.attachChild(this.mmsContext.cMainContainer);
	}

	protected void initDevMark() {
		final ShadowedText devMark = this.mmsContext.stDevMark = new ShadowedText(0, 0, ResourceManager.fFontMain,
				"COPYRIGHT 2015 PARTLIGHT", EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager());
		devMark.setColor(ColorConstants.LIGHT_RED);
		this.mmsContext.cMainContainer.attachChild(devMark);
		EntityUtils.alignEntity(devMark, devMark.getWidth(), devMark.getHeight(), HorizontalAlign.LEFT, VerticalAlign.TOP, 4, 4);
	}

	protected void initFade() {
		final Fade fade = (this.mmsContext.fFade = new Fade(EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager()) {
			@Override
			protected void onFadeOut() {
				EnvironmentVars.MAIN_CONTEXT.getHud().setOnSceneTouchListener(MainMenuInitializer.this.mmsContext.mmthTouchHandler);
				super.onFadeOut();
			}
		});
		fade.setColor(Color.BLACK);
		fade.setEase(EaseCubicIn.getInstance());

		if (MainMenuScene.postSession) {
			fade.hide();
			EnvironmentVars.MAIN_CONTEXT.getHud().attachChild(fade);
		}

	}

	protected void initStartLabel() {
		final MainMenuEntity startLabel = (this.mmsContext.mmcStartLabel = new StartLabel());
		startLabel.init();
		startLabel.attachToScene(this.mmsContext);
	}

	protected void initSwipeIcons() {
		this.mmsContext.swipeAchievementsBrightness = 1f;
		this.mmsContext.swipeLeaderboardBrightness = 1f;
		this.mmsContext.swipeOptionsBrightness = 1f;
		this.mmsContext.swipeStoreBrightness = 1f;

		this.mmsContext.sSwipeLeaderboard = new Sprite(0f, 0f, MainMenuRegions.region_leaderboard,
				EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager()) {
			@Override
			protected void onManagedUpdate(float pSecondsElapsed) {
				super.onManagedUpdate(pSecondsElapsed);
				EntityUtils.animateSineColor(this, MainMenuScene.SWIPE_COLOR0, MainMenuScene.SWIPE_COLOR1, 2.25f);
			}

			@Override
			public void setAlpha(float pAlpha) {
				if (MainMenuInitializer.this.mmsContext.isNavDemoShowing)
					return;
				super.setAlpha(pAlpha);
			}

			@Override
			public void setColor(float pRed, float pGreen, float pBlue, float pAlpha) {
				super.setColor(pRed * MainMenuInitializer.this.mmsContext.swipeLeaderboardBrightness,
						pGreen * MainMenuInitializer.this.mmsContext.swipeLeaderboardBrightness,
						pBlue * MainMenuInitializer.this.mmsContext.swipeLeaderboardBrightness);
				MainMenuInitializer.this.mmsContext.sSwipeAchievements.setColor(
						pRed * MainMenuInitializer.this.mmsContext.swipeAchievementsBrightness,
						pGreen * MainMenuInitializer.this.mmsContext.swipeAchievementsBrightness,
						pBlue * MainMenuInitializer.this.mmsContext.swipeAchievementsBrightness);
				MainMenuInitializer.this.mmsContext.sSwipeOptions.setColor(
						pRed * MainMenuInitializer.this.mmsContext.swipeOptionsBrightness,
						pGreen * MainMenuInitializer.this.mmsContext.swipeOptionsBrightness,
						pBlue * MainMenuInitializer.this.mmsContext.swipeOptionsBrightness);
				MainMenuInitializer.this.mmsContext.sSwipeStore.setColor(pRed * MainMenuInitializer.this.mmsContext.swipeStoreBrightness,
						pGreen * MainMenuInitializer.this.mmsContext.swipeStoreBrightness,
						pBlue * MainMenuInitializer.this.mmsContext.swipeStoreBrightness);
			}
		};
		this.mmsContext.sSwipeLeaderboard.setScale(2f);
		this.mmsContext.sSwipeLeaderboard.setX(24f + this.mmsContext.sSwipeLeaderboard.getWidth() / 2f);
		this.mmsContext.sSwipeLeaderboard
				.setY((EnvironmentVars.MAIN_CONTEXT.height() - this.mmsContext.sSwipeLeaderboard.getHeight()) / 2f);
		this.mmsContext.sSwipeLeaderboard.setShaderProgram(TintShaderProgram.getMultipliedInstance());

		this.mmsContext.sSwipeAchievements = new Sprite(0f, 0f, MainMenuRegions.region_achievements,
				EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager()) {
			@Override
			public void setAlpha(float pAlpha) {
				if (MainMenuInitializer.this.mmsContext.isNavDemoShowing)
					return;
				super.setAlpha(pAlpha);
			}
		};
		this.mmsContext.sSwipeAchievements.setScale(2f);
		this.mmsContext.sSwipeAchievements
				.setX(EnvironmentVars.MAIN_CONTEXT.width() - 24f - this.mmsContext.sSwipeAchievements.getWidth() * 1.5f);
		this.mmsContext.sSwipeAchievements
				.setY((EnvironmentVars.MAIN_CONTEXT.height() - this.mmsContext.sSwipeAchievements.getHeight()) / 2f);
		this.mmsContext.sSwipeAchievements.setShaderProgram(TintShaderProgram.getMultipliedInstance());

		this.mmsContext.sSwipeOptions = new Sprite(0f, 0f, MainMenuRegions.region_options,
				EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager()) {
			@Override
			public void setAlpha(float pAlpha) {
				if (MainMenuInitializer.this.mmsContext.isNavDemoShowing)
					return;
				super.setAlpha(pAlpha);
			}
		};
		this.mmsContext.sSwipeOptions.setScale(2f);
		this.mmsContext.sSwipeOptions.setX((EnvironmentVars.MAIN_CONTEXT.width() - this.mmsContext.sSwipeOptions.getWidth()) / 2f);
		this.mmsContext.sSwipeOptions.setY(24f + this.mmsContext.sSwipeOptions.getHeight() / 2f);
		this.mmsContext.sSwipeOptions.setShaderProgram(TintShaderProgram.getMultipliedInstance());

		this.mmsContext.sSwipeStore = new Sprite(0f, 0f, MainMenuRegions.region_store,
				EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager()) {
			@Override
			public void setAlpha(float pAlpha) {
				if (MainMenuInitializer.this.mmsContext.isNavDemoShowing)
					return;
				super.setAlpha(pAlpha);
			}
		};
		this.mmsContext.sSwipeStore.setScale(2f);
		this.mmsContext.sSwipeStore.setX((EnvironmentVars.MAIN_CONTEXT.width() - this.mmsContext.sSwipeStore.getWidth()) / 2f);
		this.mmsContext.sSwipeStore.setY(EnvironmentVars.MAIN_CONTEXT.height() - 24f - this.mmsContext.sSwipeStore.getHeight() * 1.5f);
		this.mmsContext.sSwipeStore.setShaderProgram(TintShaderProgram.getMultipliedInstance());
		
		if (EnvironmentVars.MAIN_CONTEXT.isAdVisible())
			EnvironmentVars.MAIN_CONTEXT.runOnUiThread(new EntityAdPosition(this.mmsContext.sSwipeStore));

		this.mmsContext.reattachSwipeIcons();
	}

	protected void initTouchHandler() {
		this.mmsContext.mmthTouchHandler = new MainMenuTouchHandler(this.mmsContext) {
			@Override
			public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
				if (MainMenuInitializer.this.mmsContext.isNavDemoShowing)
					if (MainMenuInitializer.this.mmsContext.mmndNavDemo != null)
						return MainMenuInitializer.this.mmsContext.mmndNavDemo.onTouchEvent(pSceneTouchEvent);
					else
						return false;
				if (MainMenuInitializer.this.mmsContext.getDialog() != null && MainMenuInitializer.this.mmsContext.getDialog().isShowing())
					return MainMenuInitializer.this.mmsContext.onSceneTouchEvent(pSceneTouchEvent);

				return super.onSceneTouchEvent(pScene, pSceneTouchEvent);
			}

			@Override
			protected void onSwipingStarted() {
				MainMenuInitializer.this.mmsContext.onTouchHandlerSwipingStarted();
			}

			@Override
			public void onTransformationFinished() {
				MainMenuInitializer.this.mmsContext.onTouchHandlerTransformationFinished();
			}
		};

		if (!MainMenuScene.postSession)
			EnvironmentVars.MAIN_CONTEXT.getHud().setOnSceneTouchListener(this.mmsContext.mmthTouchHandler);
	}

	protected void initVersionLabel() {
		String versionString = "VERSION ";

		try {

			final String PACKAGE_NAME = EnvironmentVars.MAIN_CONTEXT.getPackageName();
			final PackageInfo PACKAGE_INFO = EnvironmentVars.MAIN_CONTEXT.getPackageManager().getPackageInfo(PACKAGE_NAME, 0);

			final char[] VERSION_CODE_CHARS = String.valueOf(PACKAGE_INFO.versionCode).toCharArray();
			String versionCode = "";

			for (int i = 0; i < VERSION_CODE_CHARS.length; i++)
				versionCode += String.valueOf(VERSION_CODE_CHARS[i]) + ".";

			versionCode = versionCode.substring(0, versionCode.length() - 1);

			versionString += PACKAGE_INFO.versionName + " " + versionCode;

		} catch (final NameNotFoundException e) {
			e.printStackTrace();
		}

		final ShadowedText version = this.mmsContext.stVersion = new ShadowedText(0, 0, ResourceManager.fFontMain, versionString,
				EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager());
		version.setColor(ColorConstants.RED);
		this.mmsContext.cMainContainer.attachChild(version);

		EntityUtils.alignEntity(version, version.getWidth(), version.getHeight(), HorizontalAlign.RIGHT, VerticalAlign.BOTTOM, 4, 4);

		if (EnvironmentVars.MAIN_CONTEXT.isAdVisible())
			EnvironmentVars.MAIN_CONTEXT.runOnUiThread(new EntityAdPosition(version));
	}
}
