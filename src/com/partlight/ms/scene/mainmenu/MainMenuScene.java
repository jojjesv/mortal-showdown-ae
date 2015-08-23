package com.partlight.ms.scene.mainmenu;

import java.util.Random;

import org.andengine.audio.music.Music;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.Entity;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.MoveXModifier;
import org.andengine.entity.modifier.MoveYModifier;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.entity.sprite.batch.SpriteBatch;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.FontUtils;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.util.GLState;
import org.andengine.util.color.Color;
import org.andengine.util.modifier.IModifier;
import org.andengine.util.modifier.IModifier.IModifierListener;
import org.andengine.util.modifier.ease.EaseCubicIn;
import org.andengine.util.modifier.ease.EaseCubicInOut;
import org.andengine.util.modifier.ease.EaseCubicOut;
import org.andengine.util.modifier.ease.EaseLinear;
import org.andengine.util.modifier.ease.EaseSineInOut;
import org.andengine.util.modifier.ease.EaseSineOut;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.partlight.ms.activity.GameActivity.GooglePlayConstants;
import com.partlight.ms.activity.ad.TappxAdListener;
import com.partlight.ms.entity.EntityAdPosition;
import com.partlight.ms.entity.LoadingSpriteFade;
import com.partlight.ms.entity.ShadowedText;
import com.partlight.ms.entity.mainmenu.MainMenuBackground;
import com.partlight.ms.entity.mainmenu.MainMenuEntity;
import com.partlight.ms.entity.mainmenu.MainMenuStoreTabSwipeHandler;
import com.partlight.ms.entity.mainmenu.MainMenuVisibilityEntity;
import com.partlight.ms.entity.mainmenu.StartLabel;
import com.partlight.ms.entity.mainmenu.button.Button;
import com.partlight.ms.entity.mainmenu.button.StoreButton;
import com.partlight.ms.entity.mainmenu.button.ToggleButton;
import com.partlight.ms.entity.touch.scroll.ScrollContainer;
import com.partlight.ms.entity.touch.swipe.SwipeHandler.SwipeDirections;
import com.partlight.ms.entity.transition.SimpleDissolveTransition;
import com.partlight.ms.mainmenu.hud.WeaponSelector;
import com.partlight.ms.mainmenu.touch.MainMenuTouchHandler;
import com.partlight.ms.resource.EnvironmentVars;
import com.partlight.ms.resource.EnvironmentVars.PreferenceKeys;
import com.partlight.ms.resource.EnvironmentVars.StaticData;
import com.partlight.ms.resource.ResourceManager;
import com.partlight.ms.resource.ResourceManager.MiscRegions;
import com.partlight.ms.resource.ResourceManager.StoreTextureRegions;
import com.partlight.ms.resource.ResourceManager.StrokeTextureRegions;
import com.partlight.ms.scene.DialogLevelScene;
import com.partlight.ms.scene.mainmenu.container.Container;
import com.partlight.ms.scene.mainmenu.sub.MainMenuCredits;
import com.partlight.ms.scene.session.SessionScene;
import com.partlight.ms.scene.session.tutorial.TutorialSessionScene;
import com.partlight.ms.session.camera.GameCamera;
import com.partlight.ms.session.character.Armory;
import com.partlight.ms.session.environment.rain.Rain;
import com.partlight.ms.session.hud.BaseScreenComponent;
import com.partlight.ms.session.hud.BaseScreenComponentTouchManager;
import com.partlight.ms.session.hud.Inventory;
import com.partlight.ms.session.hud.listener.ComponentAdapter;
import com.partlight.ms.session.level.Level;
import com.partlight.ms.session.level.Levels;
import com.partlight.ms.shader.TintShaderProgram;
import com.partlight.ms.util.ColorConstants;
import com.partlight.ms.util.EntityUtils;
import com.partlight.ms.util.Fade;
import com.partlight.ms.util.listener.OnBackPressedListener;
import com.partlight.ms.util.updatehandler.FlashUpdateHandler;
import com.partlight.ms.util.updatehandler.FloatValueModifier;
import com.partlight.ms.util.updatehandler.FloatValueModifier.OnValueChangeListener;

import android.annotation.SuppressLint;
import android.opengl.GLES20;
import android.os.AsyncTask;

/**
 * 
 * @author Johan Svensson - partLight Entertainment
 * 
 */
public class MainMenuScene extends DialogLevelScene implements OnBackPressedListener, TappxAdListener {

	static boolean							postSession;
	private static boolean					playTutorialSession;
	private static Music					backgroundMusic;
	private static boolean					canDyeClothes;
	private static final GoogleApiClient	CLIENT;
	private static final HUD				HUD;
	private static final String				SMALL_CREDITS	= "CREATED BY JOHAN SVENSSON";
	private static float					backgroundY;
	private static float[][]				storeItemBoughtRotations;
	public static final Background			BACKGROUND;
	public static final float				SWIPE_DISTANCE;
	public static final int					SECTION_MAIN	= 0;
	public static final int					SECTION_OPTIONS	= 2;
	public static final int					SECTION_STORE	= 1;
	static boolean							areResourcesLoaded;
	static final Color						SWIPE_COLOR0;
	static final Color						SWIPE_COLOR1;

	static {

		SWIPE_COLOR0 = StartLabel.LABEL_TOP_COLOR1;
		SWIPE_COLOR1 = ColorConstants.RED;

		SWIPE_DISTANCE = 160f;

		BACKGROUND = new Background(new Color(0.25f, 0.05f, 0.05f));

		CLIENT = EnvironmentVars.MAIN_CONTEXT.getGoogleApiClient();
		HUD = EnvironmentVars.MAIN_CONTEXT.getCamera().getHUD();
	}

	public static boolean areResourcesLoaded() {
		return MainMenuScene.areResourcesLoaded;
	}

	public static void loadContainerTextures(int section) {
		switch (section) {
		case MainMenuScene.SECTION_MAIN:

			ResourceManager.btAchievements.load();
			ResourceManager.btLeaderboard.load();
			ResourceManager.btOptions.load();
			ResourceManager.btStore.load();
			ResourceManager.btStroke2.load();

			break;

		case MainMenuScene.SECTION_STORE:

			ResourceManager.btAlreadyBought.load();
			ResourceManager.btWeps.load();
			ResourceManager.btIconDye.load();
			ResourceManager.btIconDyePack.load();
			ResourceManager.btIconGlitchClip.load();
			ResourceManager.btIconLaser.load();
			ResourceManager.btIconWardrobe.load();
			ResourceManager.btStroke1.load();
			ResourceManager.btOptionsChase.load();
			ResourceManager.btOptionsLoginStates.load();
			ResourceManager.btOptionsMusic.load();
			ResourceManager.btOptionsSound.load();
			ResourceManager.btOptionsToggle.load();
			ResourceManager.btScrapPartsIcon.load();
			ResourceManager.btScrapPartsSmall.load();

			break;

		case MainMenuScene.SECTION_OPTIONS:

			ResourceManager.btOptionsLoginStates.load();
			ResourceManager.btStroke1.load();
			ResourceManager.btOptionsMusic.load();
			ResourceManager.btOptionsSound.load();
			ResourceManager.btOptionsToggle.load();
			ResourceManager.btOptionsChase.load();
			ResourceManager.btIconCredits.load();

			break;
		}
	}

	public static void unloadContainerTextures(int section) {
		switch (section) {
		case MainMenuScene.SECTION_MAIN:

			ResourceManager.btAchievements.unload();
			ResourceManager.btLeaderboard.unload();
			ResourceManager.btOptions.unload();
			ResourceManager.btStore.unload();
			ResourceManager.btStroke2.unload();

			break;

		case MainMenuScene.SECTION_STORE:

			ResourceManager.btAlreadyBought.unload();
			ResourceManager.btWeps.unload();
			ResourceManager.btIconDye.unload();
			ResourceManager.btIconDyePack.unload();
			ResourceManager.btIconGlitchClip.unload();
			ResourceManager.btIconLaser.unload();
			ResourceManager.btIconWardrobe.unload();
			ResourceManager.btStroke1.unload();
			ResourceManager.btOptionsChase.unload();
			ResourceManager.btOptionsLoginStates.unload();
			ResourceManager.btOptionsMusic.unload();
			ResourceManager.btOptionsSound.unload();
			ResourceManager.btOptionsToggle.unload();
			ResourceManager.btScrapPartsIcon.unload();
			ResourceManager.btScrapPartsSmall.unload();

			break;

		case MainMenuScene.SECTION_OPTIONS:

			ResourceManager.btOptionsLoginStates.unload();
			ResourceManager.btStroke1.unload();
			ResourceManager.btOptionsMusic.unload();
			ResourceManager.btOptionsSound.unload();
			ResourceManager.btOptionsToggle.unload();
			ResourceManager.btOptionsChase.unload();
			ResourceManager.btIconCredits.unload();

			break;
		}
	}

	Container								cMainContainer;
	Container								cOptionsContainer;
	Container								cStoreContainer;
	Container								cTransitionToContainer;
	IOnSceneTouchListener					ostlTransitionToTouchListener;
	MainMenuBackground						mmbBackground;
	MainMenuDialogBoxState					mmdbsDialogBoxState;
	MainMenuEntity							mmcStartLabel;
	MainMenuNavDemo							mmndNavDemo;
	MainMenuTouchHandler					mmthTouchHandler;
	OnBackPressedListener					obplTransitionToBackPressedListener;
	ShadowedText							stDevMark;
	ShadowedText							stVersion;
	Sprite									sSwipeAchievements;
	Sprite									sSwipeLeaderboard;
	Sprite									sSwipeOptions;
	Sprite									sSwipeStore;
	boolean									isNavDemoShowing;
	float									swipeAchievementsBrightness;
	float									swipeLeaderboardBrightness;
	float									swipeOptionsBrightness;
	float									swipeStoreBrightness;
	int										currentSection;
	private boolean							isSessionLoaded;
	private BaseScreenComponentTouchManager	tmOptions_touchManager;
	private BaseScreenComponentTouchManager	tmStore_touchManager;
	private Entity							eTransitionFromContainer;
	private FlashUpdateHandler				fuhOnPressFlash;
	private FloatValueModifier				fvmScrapPartHandler;
	private FloatValueModifier				fvmTransition;
	private int								currentStoreTabIndex;
	private MainMenuCredits					mmcCredits;
	private MainMenuEditPlayer				mmepDyeClothes;
	private MainMenuWardrobe				mmwWardrobe;
	private MainMenuLauncher				mmlInitialLaunchLibrary;
	private MainMenuStoreTabSwipeHandler	mmstshStoreTabSwipeHandler;
	private MoveXModifier					mxmStoreTabXModifier;
	private Rain							rBackgroundRain;
	private Button[]						bOptionsButtons;
	private ScrollContainer					scOptionsScrollContainer;
	private ScrollContainer					scStoreScrollContainer;
	private ShadowedText					cstStoreTabTitle;
	private ShadowedText					stScrapPartLabel;
	private SimpleDissolveTransition		sdtStartTransition;
	private Sprite							sBackgroundShadow;
	private Sprite							sScrapPartIcon;
	private SpriteBatch						sbStoreButtonPostBought;
	private StoreButton[][]					sbStoreTabs;
	private String							newStoreTabTitle;
	private String							oldStoreTabTitle;
	private Text							tSmallCredits;
	private WeaponSelector					wsStoreWeaponSelector;
	private boolean							hasUsedStartTransition;
	private boolean							isChangingStoreTab;
	private boolean							isOptionsInitialised;
	private boolean							isStoreInitialised;
	private boolean[][]						storeItemsBought;
	private final MainMenuInitializer		mmInit;
	private final TintShaderProgram			DENIED_PURCHASE_SHADER;
	private float							newStoreTabTitleWidth;
	private float							oldStoreTabTitleWidth;
	private float							storeScrollContainerY;
	private float[]							storeTabTitleWidths;
	protected Fade							fFade;
	public int								dialogConsideredIndex;
	public int								dialogConsideredWeaponIndex	= -1;

	public MainMenuScene() {

		EnvironmentVars.SESSION_SCENE = null;
		EnvironmentVars.TUTORIAL_SESSION_SCENE = null;

		if (!MainMenuScene.canDyeClothes)
			MainMenuScene.canDyeClothes = EnvironmentVars.PREFERENCES.getBoolean(PreferenceKeys.KEY_CAN_DYE_CLOTHES, false);

		this.DENIED_PURCHASE_SHADER = new TintShaderProgram(true);
		this.DENIED_PURCHASE_SHADER.setTintPercent(0.75f);

		this.mmInit = new MainMenuInitializer(this);

		this.setOnSceneTouchListener(null);

		if (MainMenuScene.areResourcesLoaded)
			this.hasUsedStartTransition = true;

		this.mmdbsDialogBoxState = MainMenuDialogBoxState.NONE;
		
		EnvironmentVars.MAIN_CONTEXT.addAdListener(this);
		
		if (EnvironmentVars.MAIN_CONTEXT.isAdLoaded())
			this.onAdLoaded();
	}

	@Override
	public void attachChild(IEntity pEntity) throws IllegalStateException {
		if (pEntity != this.mmbBackground)
			this.setEntityMaxZIndex(pEntity);

		super.attachChild(pEntity);
	}

	private void changeStoreTab() {

		this.isChangingStoreTab = true;

		this.scStoreScrollContainer.onTouchRelease(false);

		if (this.currentStoreTabIndex > 0 && this.currentStoreTabIndex < MainMenuStore.STORE_TABS - 1)
			this.mmstshStoreTabSwipeHandler.setSwipeDirections(SwipeDirections.LEFT, SwipeDirections.RIGHT);
		else if (this.currentStoreTabIndex == 0)
			this.mmstshStoreTabSwipeHandler.setSwipeDirections(SwipeDirections.LEFT);
		else if (this.currentStoreTabIndex == MainMenuStore.STORE_TABS - 1)
			this.mmstshStoreTabSwipeHandler.setSwipeDirections(SwipeDirections.RIGHT);

		this.newStoreTabTitle = MainMenuStore.getTab(this.currentStoreTabIndex).tabTitle;
		this.newStoreTabTitleWidth = this.storeTabTitleWidths[this.currentStoreTabIndex];

		if (this.mxmStoreTabXModifier == null) {
			this.mxmStoreTabXModifier = new MoveXModifier(0.4f, 0f, 0f, EaseCubicInOut.getInstance()) {
				@Override
				protected void onModifierFinished(IEntity pItem) {
					super.onModifierFinished(pItem);
					MainMenuScene.this.oldStoreTabTitleWidth = MainMenuScene.this.storeTabTitleWidths[MainMenuScene.this.currentStoreTabIndex];
					MainMenuScene.this.cstStoreTabTitle.setAlpha(1f);
					MainMenuScene.this.cstStoreTabTitle.setText(MainMenuStore.getTab(MainMenuScene.this.currentStoreTabIndex).tabTitle);
					MainMenuScene.this.setStoreScrollContainerMinY();
					MainMenuScene.this.isChangingStoreTab = false;
					MainMenuScene.this.mmstshStoreTabSwipeHandler.onStoreTabChanged();
				}

				@Override
				protected void onSetValue(IEntity pEntity, float pPercentageDone, float pX) {
					super.onSetValue(pEntity, pPercentageDone, pX);
					MainMenuScene.this.cstStoreTabTitle.setAlpha(1f - pPercentageDone);
				}
			};
			this.mxmStoreTabXModifier.addModifierListener(new IModifierListener<IEntity>() {
				@Override
				public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
					MainMenuScene.HUD.setOnSceneTouchListener(MainMenuScene.this.mmthTouchHandler);
					MainMenuScene.this.tmStore_touchManager
							.setComponents(MainMenuScene.this.getStoreTab(MainMenuScene.this.currentStoreTabIndex));
				}

				@Override
				public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
					MainMenuScene.HUD.setOnSceneTouchListener(null);
				}
			});
			this.mxmStoreTabXModifier.setAutoUnregisterWhenFinished(true);
		}

		this.mxmStoreTabXModifier.reset(this.mxmStoreTabXModifier.getDuration(), this.scStoreScrollContainer.getX(),
				EnvironmentVars.MAIN_CONTEXT.width() * -this.currentStoreTabIndex);

		this.scStoreScrollContainer.registerEntityModifier(this.mxmStoreTabXModifier);
	}

	public void checkAdStore() {
		if (EnvironmentVars.MAIN_CONTEXT.isAdLoaded()) {
			final ScrollContainer container = this.scStoreScrollContainer;
			if (container == null)
				return;
			EnvironmentVars.MAIN_CONTEXT.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					container.setMinY(container.getMinY() - EnvironmentVars.MAIN_CONTEXT.getAdHeight());
				}
			});
		}
	}

	protected void checkNavDemo() {
		if (this.isNavDemoShowing) {
			this.mmndNavDemo = new MainMenuNavDemo(this);
			this.mmndNavDemo.showDemo();

			try {
				this.attachChild(this.fFade);
			} catch (final IllegalStateException ex) {
			}
		}
	}

	private void checkStoreItemAlreadyPurchased(int tab, int storeItemId) {

		boolean isPurchased = false;

		switch (tab) {
		case 0:
			switch (storeItemId) {
			case 0:
				isPurchased = StaticData.dominador_repeatedFire;
				break;
			case 3:
				isPurchased = StaticData.sniper_injectionTips;
				break;
			}
			break;
		case 1:
			switch (storeItemId) {
			case MainMenuStore.ID_WARDROBE:
				isPurchased = true;
				break;
			case MainMenuStore.ID_DYE_APPLICATOR:
				isPurchased = MainMenuScene.canDyeClothes;
				break;
			}
			break;
		}

		this.storeItemsBought[tab][storeItemId] = isPurchased;

		switch (tab) {
		case 1:
			switch (storeItemId) {
			case MainMenuStore.ID_DYE_APPLICATOR:
				if (MainMenuScene.canDyeClothes) {

					final String DESCRIPTION = MainMenuStore.getTab(1).itemDescriptions[MainMenuStore.ID_DYE_APPLICATOR];
					this.sbStoreTabs[tab][storeItemId]
							.setDescription(DESCRIPTION.substring(DESCRIPTION.indexOf("|") + 1, DESCRIPTION.length()));
				}

				break;
			}
			break;
		}

		if (isPurchased) {

			this.sbStoreTabs[tab][storeItemId].removePriceTag();

			// WARDROBE
			if (tab == 1 && storeItemId == 0)
				return;

			// DYE APPLICATOR
			if (tab == 1 && storeItemId == 1)
				return;

			this.sbStoreTabs[tab][storeItemId].getIcon().setColor(0.5f, 0.55f, 0.5f);
		}
	}

	public int getCurrentSection() {
		return this.currentSection;
	}

	public Integer getCurrentStoreTabIndex() {
		return this.currentStoreTabIndex;
	}

	@Override
	public Level getLevel() {
		return MainMenuBackground.getLevel();
	}

	public Object getMetaObject(int metaobject) {
		switch (metaobject) {
		case MainMenuMetaObjects.META_TM_OPTIONS:
			return this.tmOptions_touchManager;
		case MainMenuMetaObjects.META_TM_STORE:
			return this.tmStore_touchManager;
		case MainMenuMetaObjects.META_WS:
			return this.wsStoreWeaponSelector;
		}
		return null;
	}

	public Container getSectionContainer(int section) {
		switch (section) {
		case MainMenuScene.SECTION_MAIN:
			return this.cMainContainer;
		case MainMenuScene.SECTION_STORE:
			return this.cStoreContainer;
		case MainMenuScene.SECTION_OPTIONS:
			return this.cOptionsContainer;
		}
		return null;
	}

	// private void initBackgroundShadow() {
	// this.sBackgroundShadow = new Sprite(320f, 0f,
	// MainMenuRegions.region_bg_shadow,
	// MAIN_CONTEXT.getVertexBufferObjectManager());
	// this.sBackgroundShadow.setScaleCenter(0, 0);
	// this.sBackgroundShadow.setScale(4f);
	// this.sBackgroundShadow.setAlpha(0f);
	// this.sBackgroundShadow.registerUpdateHandler(new IUpdateHandler() {
	//
	// private float totalElapsedSeconds;
	// private float xDecrement = 0.25f;
	// private boolean hasBackgroundFadedIn = false;
	//
	// @Override
	// public void onUpdate(float pSecondsElapsed) {
	// this.totalElapsedSeconds += pSecondsElapsed;
	//
	// if (this.totalElapsedSeconds > 0.5f) {
	//
	// if (!this.hasBackgroundFadedIn) {
	//
	// final AlphaModifier ALPHA_MODIFIER = new AlphaModifier(2f, 0f, 0.6f,
	// EaseSineInOut.getInstance());
	// ALPHA_MODIFIER.setAutoUnregisterWhenFinished(true);
	//
	// MainMenuScene.this.sBackgroundShadow.registerEntityModifier(ALPHA_MODIFIER);
	//
	// this.hasBackgroundFadedIn = true;
	// }
	// }
	//
	// if ((int) (this.xDecrement * 100) > 0) {
	// MainMenuScene.this.sBackgroundShadow.setX(MainMenuScene.this.sBackgroundShadow.getX()
	// - this.xDecrement);
	//
	// if (MainMenuScene.this.sBackgroundShadow.getX() <= 32f) {
	// this.xDecrement -= 0.025f;
	// }
	// } else {
	// MAIN_CONTEXT.runOnUpdateThread(new Runnable() {
	// @Override
	// public void run() {
	// MainMenuScene.this.sBackgroundShadow.clearUpdateHandlers();
	// }
	// });
	// }
	// }
	//
	// @Override
	// public void reset() {
	//
	// }
	// });
	//
	// }

	public ScrollContainer getSectionScrollContainer(int section) {
		switch (section) {
		case MainMenuScene.SECTION_STORE:
			return this.scStoreScrollContainer;
		case MainMenuScene.SECTION_OPTIONS:
			return this.scOptionsScrollContainer;
		}
		return null;
	}

	public StoreButton[] getStoreTab(int tabIndex) {
		return this.sbStoreTabs[tabIndex];
	}

	public boolean hasUsedStartTransition() {
		return this.hasUsedStartTransition;
	}

	public void init() {
		this.mmInit.init();
	}

	private void initBackgroundRain() {
		if (this.rBackgroundRain != null)
			return;
		this.rBackgroundRain = new Rain();
		this.attachChild(this.rBackgroundRain);
	}

	private void initOptions() {

		if (this.isOptionsInitialised)
			return;

		this.cOptionsContainer = new Container();

		this.scOptionsScrollContainer = new ScrollContainer(8f) {
			@Override
			protected void onStartedScrolling() {
				super.onStartedScrolling();
				for (final Button b : MainMenuScene.this.bOptionsButtons)
					b.resetTouch();
			}
		};

		this.bOptionsButtons = new Button[MainMenuOptions.BUTTON_TITLES.length];

		for (int i = 0; i < this.bOptionsButtons.length; i++) {
			final int TAG = MainMenuOptions.BUTTON_TAGS[i];

			if (!MainMenuOptions.BUTTON_IS_TOGGLE[i])
				this.bOptionsButtons[i] = new Button(0, Button.BUTTON_HEIGHT * i, StrokeTextureRegions.region_stroke_1,
						MainMenuOptions.getButtonIcon(TAG), MainMenuOptions.BUTTON_TITLES[i]);
			else
				this.bOptionsButtons[i] = new ToggleButton(0, Button.BUTTON_HEIGHT * i, StrokeTextureRegions.region_stroke_1,
						MainMenuOptions.getButtonIcon(TAG), MainMenuOptions.BUTTON_TITLES[i]);
			this.bOptionsButtons[i].setTag(TAG);
			this.bOptionsButtons[i].setX((EnvironmentVars.MAIN_CONTEXT.width() - this.bOptionsButtons[i].getWidthScaled()) / 2f);

			this.bOptionsButtons[i].setComponentListener(new ComponentAdapter() {
				@Override
				public void onComponentLongPress(BaseScreenComponent component) {
					MainMenuScene.this.processOptionsButtonLongPress(component.getTag());
				}

				@Override
				public void onComponentReleased(BaseScreenComponent component, float x, float y) {
					MainMenuOptions.processButtonClick((Button) component);
					MainMenuScene.this.processOptionsButtonClick(component.getTag());
				}

				@Override
				public void onComponentTouchStateReset(BaseScreenComponent component) {
					MainMenuScene.this.processOptionsButtonTouchStateReset(component.getTag());
				}
			});

			this.scOptionsScrollContainer.attachChild(this.bOptionsButtons[i]);
		}

		this.tmOptions_touchManager = new BaseScreenComponentTouchManager(this.bOptionsButtons);
		this.scOptionsScrollContainer.setMinY(EnvironmentVars.MAIN_CONTEXT.height() - (Button.BUTTON_HEIGHT * this.bOptionsButtons.length));
		this.cOptionsContainer.attachChild(this.scOptionsScrollContainer);

		this.isOptionsInitialised = true;
	}

	protected void initScrapPartLabel(int value) {

		final String TEXT = "X" + value;

		if (this.stScrapPartLabel != null) {

			if (this.stScrapPartLabel.getText().length() >= TEXT.length()) {
				this.stScrapPartLabel.setText(TEXT);
				this.stScrapPartLabel.setScaleCenter(0, 0);
				this.stScrapPartLabel.setScale(2f);
				return;
			}

			this.stScrapPartLabel.detachSelf();
			if (!this.stScrapPartLabel.isDisposed())
				this.stScrapPartLabel.dispose();
		}

		final float X = 32f + this.sScrapPartIcon.getWidthScaled();

		this.stScrapPartLabel = new ShadowedText(X, 0f, ResourceManager.fFontMain, TEXT,
				EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager());

		this.stScrapPartLabel.setY(16f + (this.sScrapPartIcon.getHeightScaled() - this.stScrapPartLabel.getHeightScaled()) / 2f);

		this.stScrapPartLabel.setColor(ColorConstants.SCRAP_PARTS);

		this.stScrapPartLabel.setText(TEXT);
		this.stScrapPartLabel.setScaleCenter(0, 0);
		this.stScrapPartLabel.setScale(2f);

		this.cStoreContainer.attachChild(this.stScrapPartLabel);
	}

	private void initSmallCredits() {
		this.tSmallCredits = new Text(16f, EnvironmentVars.MAIN_CONTEXT.height() - 32f, ResourceManager.fFontMain,
				MainMenuScene.SMALL_CREDITS, EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager());
		this.tSmallCredits.setColor(0.6f, 0.6f, 0.6f);
		EnvironmentVars.MAIN_CONTEXT.runOnUiThread(new EntityAdPosition(this.tSmallCredits));

		this.tSmallCredits.setScaleCenter(0, 0);
		this.tSmallCredits.setScale(1.1f);

		EnvironmentVars.MAIN_CONTEXT.getHud().attachChild(this.tSmallCredits);
	}

	private void initStore() {

		if (this.isStoreInitialised)
			return;

		this.initStorePostBoughtObjects();
		this.currentStoreTabIndex = 0;
		this.cStoreContainer = new Container();

		this.wsStoreWeaponSelector = new WeaponSelector() {
			@Override
			protected void onClosed() {
				super.onClosed();
				MainMenuScene.this.wsStoreWeaponSelector.getFade().detachSelf();
			}

			@Override
			protected void onSelectionMade(int index) {
				MainMenuScene.this.dialogConsideredWeaponIndex = index;
				MainMenuScene.this.onStoreItemClicked(MainMenuScene.this.dialogConsideredIndex);
			}
		};

		this.sScrapPartIcon = new Sprite(24f, 16f, MiscRegions.region_scrap_part,
				EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager());
		this.sScrapPartIcon.setScaleCenter(0, 0);
		this.sScrapPartIcon.setScale(3f);
		this.sScrapPartIcon.setRotation(5f);

		this.cStoreContainer.attachChild(this.sScrapPartIcon);

		this.storeScrollContainerY = this.sScrapPartIcon.getY() + this.sScrapPartIcon.getHeightScaled() + 32f;

		this.scStoreScrollContainer = new ScrollContainer(8f) {

			@Override
			public float getStartY() {
				return MainMenuScene.this.storeScrollContainerY;
			}

			@Override
			protected void onManagedDraw(GLState pGLState, Camera pCamera) {
				if (!pGLState.isScissorTestEnabled())
					pGLState.enableScissorTest();

				final GameCamera CAMERA = EnvironmentVars.MAIN_CONTEXT.getCamera();
				final float HEIGHT = EnvironmentVars.MAIN_CONTEXT.height();

				float h = CAMERA.getSurfaceHeight();
				h *= (1f - this.getStartY() / HEIGHT);
				h -= HEIGHT - HEIGHT * this.getScaleY();
				h += 16;

				GLES20.glScissor(0, 0, CAMERA.getSurfaceWidth(), (int) h);
				super.onManagedDraw(pGLState, pCamera);

				pGLState.disableScissorTest();
			}

			@Override
			protected void onStartedScrolling() {
				for (final StoreButton sb : MainMenuScene.this.getStoreTab(MainMenuScene.this.currentStoreTabIndex))
					sb.resetTouch();
			}

			@Override
			public boolean onTouchEvent(TouchEvent event) {
				if ((event.isActionDown() && event.getY() < MainMenuScene.this.storeScrollContainerY)
						|| (int) MainMenuScene.this.cStoreContainer.getY() > 0)
					return false;

				if (MainMenuScene.this.isChangingStoreTab)
					return false;

				final boolean TAB_SWIPE_EVENT = MainMenuScene.this.mmstshStoreTabSwipeHandler.onSceneTouchEvent(MainMenuScene.this, event);

				if (MainMenuScene.this.mmstshStoreTabSwipeHandler.isSwiping()
						&& MainMenuScene.this.mmstshStoreTabSwipeHandler.isSwipingHorizontally())
					return TAB_SWIPE_EVENT;

				return super.onTouchEvent(event);
			}
		};

		this.scStoreScrollContainer.setY(this.storeScrollContainerY);

		this.sbStoreTabs = new StoreButton[MainMenuStore.STORE_TABS][];

		for (int x = 0; x < MainMenuStore.STORE_TABS; x++) {
			this.sbStoreTabs[x] = new StoreButton[MainMenuStore.getTab(x).itemTitles.length];

			for (int y = 0; y < this.sbStoreTabs[x].length; y++) {

				this.initStoreItem(x, y);
				this.sbStoreTabs[x][y].setX(this.sbStoreTabs[x][y].getX() + EnvironmentVars.MAIN_CONTEXT.width() * x);
				this.sbStoreTabs[x][y].getIcon().setZIndex(2);
				if (x == 0)
					this.scStoreScrollContainer.attachChild(this.sbStoreTabs[x][y]);
				this.sbStoreTabs[x][y].sortChildren();
			}
		}
		this.redrawPostBoughtSpriteBatch();
		this.setStoreScrollContainerMinY();

		this.sbStoreTabs[0][0].setCurrentTileIndex(Armory.WEP_DOMINADOR);
		this.sbStoreTabs[0][0].setRotation(Inventory.INVENTORY_ICON_ROTATION);

		this.sbStoreTabs[0][3].setCurrentTileIndex(Armory.WEP_SNIPER);
		this.sbStoreTabs[0][3].setRotation(Inventory.INVENTORY_ICON_ROTATION);
		this.sbStoreTabs[0][3].setVirtualIconWidth(64f);
		this.scStoreScrollContainer.setMaxY(this.storeScrollContainerY);

		this.cstStoreTabTitle = new ShadowedText(0f, 64f, ResourceManager.fFontMain, MainMenuStore.getTab(1).tabTitle,
				EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager()) {
			private boolean hasValidatedDraw;

			@Override
			protected void onManagedDraw(GLState pGLState, Camera pCamera) {

				if (!this.hasValidatedDraw || MainMenuScene.this.isChangingStoreTab) {
					this.hasValidatedDraw = true;

					final float CENTER = EnvironmentVars.MAIN_CONTEXT.width() / 2f;
					final boolean TO_NEXT = (MainMenuScene.this.mxmStoreTabXModifier == null) ? false
							: MainMenuScene.this.mxmStoreTabXModifier.getFromValue() > MainMenuScene.this.mxmStoreTabXModifier.getToValue();

					super.mX = CENTER - MainMenuScene.this.oldStoreTabTitleWidth / 2f;
					super.mX -= ((TO_NEXT) ? 128f : -128f) * (1f - super.getAlpha());

					super.onManagedDraw(pGLState, pCamera);

					if (super.getAlpha() < 1) {
						final float ALPHA = super.getAlpha();
						super.mX -= super.mX;
						super.mX += CENTER - MainMenuScene.this.newStoreTabTitleWidth / 2f;
						super.mX += ((TO_NEXT) ? 128f : -128f) * ALPHA;

						super.setText(MainMenuScene.this.newStoreTabTitle);
						super.setAlpha(1f - ALPHA);

						super.onManagedDraw(pGLState, pCamera);

						super.setText(MainMenuScene.this.oldStoreTabTitle);
						super.setAlpha(ALPHA);
					}
				} else
					super.onManagedDraw(pGLState, pCamera);
			}
		};
		this.cstStoreTabTitle.setText(MainMenuStore.getTab(0).tabTitle);
		this.cstStoreTabTitle.setColor(ColorConstants.SCRAP_PARTS);
		this.cstStoreTabTitle.setScale(2.5f);

		this.storeTabTitleWidths = new float[MainMenuStore.STORE_TABS];
		for (int x = 0; x < this.storeTabTitleWidths.length; x++)
			this.storeTabTitleWidths[x] = FontUtils.measureText(ResourceManager.fFontMain, MainMenuStore.getTab(x).tabTitle);

		this.oldStoreTabTitleWidth = this.storeTabTitleWidths[0];

		this.tmStore_touchManager = new BaseScreenComponentTouchManager(this.sbStoreTabs[0]) {
			@Override
			public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {

				if (pSceneTouchEvent.isActionDown() && pSceneTouchEvent.getY() < MainMenuScene.this.scStoreScrollContainer.getMaxY())
					return false;

				return super.onSceneTouchEvent(pScene, pSceneTouchEvent);
			}
		};

		this.cStoreContainer.attachChild(this.cstStoreTabTitle);
		this.scStoreScrollContainer.attachChild(this.sbStoreButtonPostBought);
		this.cStoreContainer.attachChild(this.scStoreScrollContainer);
		this.cStoreContainer.setY(EnvironmentVars.MAIN_CONTEXT.height());

		this.initStoreTabSwipeHandler();
		this.checkAdStore();

		this.isStoreInitialised = true;
	}

	protected void initStoreItem(int tab, int storeItemId) {

		final int TAB = tab;

		// @formatter:off
		final StoreButton ITEM = this.sbStoreTabs[tab][storeItemId] = new StoreButton(0, Button.BUTTON_HEIGHT * storeItemId, storeItemId,
				MainMenuStore.getTab(tab).itemIcons[storeItemId], MainMenuStore.getTab(tab).itemTitles[storeItemId], this, MainMenuStore.getTab(tab).itemPrices[storeItemId]) {
			
			@Override
			public void handleInput(TouchEvent touchEvent) {
				if (!MainMenuScene.this.isChangingStoreTab)
					this.setTouchSuspended(MainMenuScene.this.currentStoreTabIndex != TAB || MainMenuScene.this.mmstshStoreTabSwipeHandler.isSwiping()
							|| MainMenuScene.this.scStoreScrollContainer.isScrolling());
				
				super.handleInput(touchEvent);
			}
		};
		// @formatter:on

		ITEM.setX((EnvironmentVars.MAIN_CONTEXT.width() - ITEM.getWidthScaled()) / 2f);

		String itemDescription = MainMenuStore.getTab(tab).itemDescriptions[storeItemId];

		if (tab == 1 && storeItemId == 1)
			itemDescription = itemDescription.substring(0, itemDescription.indexOf("|"));

		ITEM.setDescription(itemDescription);
		ITEM.setTouchSuspended(true);
		ITEM.getIcon().setShaderProgram(TintShaderProgram.getMultipliedInstance());
		this.checkStoreItemAlreadyPurchased(tab, storeItemId);
	}

	private void initStorePostBoughtObjects() {
		// Bought sprite rotations
		{
			if (MainMenuScene.storeItemBoughtRotations == null) {
				MainMenuScene.storeItemBoughtRotations = new float[MainMenuStore.STORE_TABS][];

				for (int x = 0; x < MainMenuScene.storeItemBoughtRotations.length; x++) {
					MainMenuScene.storeItemBoughtRotations[x] = new float[MainMenuStore.getTab(x).itemTitles.length];
					for (int y = 0; y < MainMenuStore.getTab(x).itemTitles.length; y++)
						MainMenuScene.storeItemBoughtRotations[x][y] = -10f + (10f * 2) * new Random().nextFloat();
				}
			}
		}

		// SpriteBatch
		{
			this.sbStoreButtonPostBought = new SpriteBatch(ResourceManager.btAlreadyBought, 99,
					EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager());
			this.sbStoreButtonPostBought.setZIndex(2);
		}

		// Bought conditions
		{
			this.storeItemsBought = new boolean[MainMenuStore.STORE_TABS][];

			for (int i = 0; i < this.storeItemsBought.length; i++)
				this.storeItemsBought[i] = new boolean[MainMenuStore.getTab(i).itemTitles.length];
		}
	}

	private void initStoreTabSwipeHandler() {
		this.mmstshStoreTabSwipeHandler = new MainMenuStoreTabSwipeHandler(this);
	}

	void nullifyNavDemo() {
		EnvironmentVars.MAIN_CONTEXT.runOnUpdateThread(new Runnable() {
			@Override
			public void run() {
				MainMenuScene.this.mmndNavDemo = null;
			}
		});
	}

	@Override
	public void onAdLoaded() {
		this.checkAdStore();
		EnvironmentVars.MAIN_CONTEXT.runOnUiThread(new EntityAdPosition(this.stVersion, this.sSwipeStore));
	}

	@SuppressLint("NewApi")
	@Override
	public void onBackPressed() {
		if (this.currentSection == MainMenuScene.SECTION_OPTIONS)
			this.toMain();
		else if (this.currentSection == MainMenuScene.SECTION_STORE)
			this.toMain();
		else
			EnvironmentVars.MAIN_CONTEXT.simulateHomeButton();
	}

	@Override
	public void onConnectionChanged(int code) {
		super.onConnectionChanged(code);
		if (this.isOptionsInitialised)
			this.onUpdateOptionsSyncAccount();
	}

	@Override
	protected void onDialogAccept() {

		switch (this.mmdbsDialogBoxState) {

		case CONFIRM_COLORS:
			this.mmepDyeClothes.savePlayerData();
			this.mmepDyeClothes.goBack();
			break;

		case CONFIRM_ITEM_PURCHASE:
			this.onStoreItemPurchased(this.currentStoreTabIndex, this.dialogConsideredIndex);
			this.dialogConsideredIndex = 0;
			break;

		case CONFIRM_WARDROBE_PURCHASE:
			switch (this.mmwWardrobe.getLastSelectorType()) {
			case 0:
				StaticData.playerTorsoIndex = this.dialogConsideredIndex;
				this.subtractScrapParts(MainMenuStore.TORSO_PRICES[this.dialogConsideredIndex]);
				break;
			case 2:
				StaticData.playerTorsoIndex = this.dialogConsideredIndex;
				this.subtractScrapParts(MainMenuStore.TORSO_PRICES[this.dialogConsideredIndex]);
				break;
			}
			this.mmwWardrobe.savePlayerData();
			break;

		case NONE:
			break;
		}
		this.mmdbsDialogBoxState = MainMenuDialogBoxState.NONE;

		super.onDialogAccept();
	}

	@SuppressWarnings("incomplete-switch")
	@Override
	protected void onDialogClosed() {
		super.onDialogClosed();

		switch (this.mmdbsDialogBoxState) {
		case PURCHASE_COLORS_FAILED:
			this.mmepDyeClothes.cancelPurchase();
			break;
		}

		switch (this.currentSection) {
		case MainMenuScene.SECTION_STORE:
			this.dialogConsideredWeaponIndex = -1;
			break;
		}
	}

	@SuppressWarnings("incomplete-switch")
	@Override
	protected void onDialogDecline() {

		switch (this.mmdbsDialogBoxState) {
		case CONFIRM_COLORS:
			this.mmepDyeClothes.cancelPurchase();
			break;
		}
		this.mmdbsDialogBoxState = MainMenuDialogBoxState.NONE;

		super.onDialogDecline();
	}

	@Override
	public void onEngineDrawError() {

	}

	@Override
	public void onEngineUpdateError() {

	}

	protected void onHandleScrapPartHandler(final boolean unregister) {
		MainMenuScene.this.unregisterUpdateHandler(MainMenuScene.this.fvmScrapPartHandler);
		if (!unregister)
			MainMenuScene.this.registerUpdateHandler(MainMenuScene.this.fvmScrapPartHandler);
	}

	public void onLoadingResourcesLoaded() {
		this.mmlInitialLaunchLibrary.onLoadingResourcesLoaded();
	}

	protected void onMainContainerSetScale(float scale) {
		if (this.currentSection != MainMenuScene.SECTION_MAIN)
			return;

		try {

			if (this.mmthTouchHandler.isSwipingHorizontally())
				if (this.mmthTouchHandler.getTransformationPercent() < 0f) {
					if (this.sSwipeAchievements.getParent().equals(this.cMainContainer)) {
						this.sSwipeAchievements.detachSelf();
						this.attachChild(this.sSwipeAchievements);
					}
				} else if (this.mmthTouchHandler.getTransformationPercent() > 0f)
					if (this.sSwipeLeaderboard.getParent().equals(this.cMainContainer)) {
						this.sSwipeLeaderboard.detachSelf();
						this.attachChild(this.sSwipeLeaderboard);
					}

			final float ACTIVE_ICON_SCALE = 2f + (1f - scale) * 1.25f;

			if (this.sSwipeAchievements.getParent().equals(this))
				this.sSwipeAchievements.setScale(ACTIVE_ICON_SCALE);
			else
				this.sSwipeAchievements.setScale(2f);

			if (this.sSwipeLeaderboard.getParent().equals(this))
				this.sSwipeLeaderboard.setScale(ACTIVE_ICON_SCALE);
			else
				this.sSwipeLeaderboard.setScale(2f);

			if (this.sSwipeOptions.getParent().equals(this))
				this.sSwipeOptions.setScale(ACTIVE_ICON_SCALE);
			else
				this.sSwipeOptions.setScale(2f);

		} catch (final NullPointerException ex) {

		}
	}

	protected void onMainContainerSetY(float y) {
		// this.sBackgroundShadow.setY(backgroundY + (MAIN_CONTEXT.height() + y)
		// * 0.1f);

		if (this.cOptionsContainer != null)
			this.cOptionsContainer.setY(y - EnvironmentVars.MAIN_CONTEXT.height());

		if (this.cStoreContainer != null)
			this.cStoreContainer.setY(y + EnvironmentVars.MAIN_CONTEXT.height());

		if (this.currentSection != MainMenuScene.SECTION_MAIN)
			return;

		final float ACTIVE_ICON_SCALE = 2f + (Math.abs(y) / this.mmthTouchHandler.getSwipeDistance()) * 1.25f;

		try {
			if (this.sSwipeOptions.getParent().equals(this))
				this.sSwipeOptions.setScale(ACTIVE_ICON_SCALE);
			else
				this.sSwipeOptions.setScale(2f);

			if (this.sSwipeStore.getParent().equals(this))
				this.sSwipeStore.setScale(ACTIVE_ICON_SCALE);
			else
				this.sSwipeStore.setScale(2f);
		} catch (final Exception ex) {

		}
	}

	@Override
	protected void onManagedUpdate(float pSecondsElapsed) {
		super.onManagedUpdate(pSecondsElapsed);
		if (this.isSessionLoaded) {

			if (MainMenuScene.playTutorialSession) {
				EnvironmentVars.TUTORIAL_SESSION_SCENE.init();
				EnvironmentVars.TUTORIAL_SESSION_SCENE.onStart();
				EnvironmentVars.MAIN_CONTEXT.getEngine().setScene(EnvironmentVars.TUTORIAL_SESSION_SCENE);
			} else {
				EnvironmentVars.SESSION_SCENE.init();
				EnvironmentVars.SESSION_SCENE.onStart();
				EnvironmentVars.MAIN_CONTEXT.getEngine().setScene(EnvironmentVars.SESSION_SCENE);
			}
			this.detachChildren();
			this.dispose();
		}
	}

	protected void onMusicFadeOut() {
		final FloatValueModifier MUSIC_VOLUME_MOD = new FloatValueModifier(1f, 0f, EaseLinear.getInstance(), 1f);
		MUSIC_VOLUME_MOD.setOnValueChangeListener(new OnValueChangeListener() {
			@Override
			public void valueChanged(float newValue) {
				MainMenuScene.backgroundMusic.setVolume(newValue);
			}
		});
		MUSIC_VOLUME_MOD.reset();
		this.registerUpdateHandler(MUSIC_VOLUME_MOD);
	}

	public void onResourcesLoaded() {
		MainMenuScene.areResourcesLoaded = true;
		MainMenuScene.backgroundMusic = ResourceManager.mTheme;
		this.mmlInitialLaunchLibrary.onResourcesLoaded();

		this.mmInit.initTouchHandler();

		this.initBackgroundRain();
		this.initSmallCredits();

		EntityUtils.animateEntity(this.rBackgroundRain, 0.5f, EntityUtils.ANIMATION_FADE_IN);

		this.playThemeSong();
	}

	@Override
	public void onResume() {
		super.onResume();
		this.DENIED_PURCHASE_SHADER.setCompiled(false);
	}

	public void onStoreItemClicked(int storeItemId) {

		switch (this.currentStoreTabIndex) {
		case 1:
			switch (storeItemId) {
			case MainMenuStore.ID_WARDROBE:
				this.toWardrobe();
				return;
			case MainMenuStore.ID_DYE_APPLICATOR:
				if (MainMenuScene.canDyeClothes) {
					this.toDyeClothes();
					return;
				}
			}
			break;
		}

		final StoreButton STOREBUTTON = this.sbStoreTabs[this.currentStoreTabIndex][storeItemId];

		if (this.storeItemsBought[this.currentStoreTabIndex][storeItemId])
			return;

		String confirmTitleString = STOREBUTTON.getTitle() + "";

		final boolean FROM_SELECTOR = this.dialogConsideredWeaponIndex != -1;

		if (FROM_SELECTOR)
			confirmTitleString += " (" + Armory.WEP_ARRAY[this.dialogConsideredWeaponIndex].getFriendlyName() + ")";

		final boolean SUFFICIENT_FUNDS = StaticData.scrapPartsAmount >= STOREBUTTON.getPrice();
		final boolean NEEDS_SELECTION = MainMenuStore.getTab(this.currentStoreTabIndex).itemNeedsSelection[storeItemId];

		if ((NEEDS_SELECTION) ? ((SUFFICIENT_FUNDS) ? FROM_SELECTOR : true) : true)
			this.showPurchaseDialog(confirmTitleString, STOREBUTTON.getDescription().toString(), STOREBUTTON.getPrice(),
					STOREBUTTON.getIcon().getTextureRegion(), STOREBUTTON.getIcon().getRotation(), STOREBUTTON.getVirtualIconWidth());

		if (!FROM_SELECTOR && SUFFICIENT_FUNDS) {
			this.dialogConsideredIndex = storeItemId;
			this.mmdbsDialogBoxState = MainMenuDialogBoxState.CONFIRM_ITEM_PURCHASE;

			if (!NEEDS_SELECTION) {
				this.dialogConsideredWeaponIndex = -1;
				this.onStoreItemSufficientFunds(this.currentStoreTabIndex, storeItemId);
			} else {
				try {
					MainMenuScene.HUD.attachChild(this.wsStoreWeaponSelector.getFade());
				} catch (final IllegalStateException ex) {
				}

				boolean[] disabledSelections = new boolean[Armory.WEP_ARRAY.length];

				if (this.currentStoreTabIndex == 0)
					switch (storeItemId) {

					case 1:
						disabledSelections = StaticData.laserSightItems.clone();

						disabledSelections[Armory.WEP_GRENADE] = true;
						disabledSelections[Armory.WEP_CALTROP] = true;
						break;
					case 2:
						disabledSelections = StaticData.glitchClipItems.clone();

						disabledSelections[Armory.WEP_PISTOL] = true;
						disabledSelections[Armory.WEP_GRENADE] = true;
						disabledSelections[Armory.WEP_CALTROP] = true;
						break;
					}

				this.wsStoreWeaponSelector.show(disabledSelections);
			}
		} else
			this.onStoreItemInsufficientFunds(this.currentStoreTabIndex, storeItemId);
	}

	protected void onStoreItemInsufficientFunds(int tab, int storeItemId) {
	}

	protected void onStoreItemPurchased(int tab, int storeItemId) {
		this.processPurchasedStoreItem(tab, storeItemId);

		EnvironmentVars.MAIN_CONTEXT.refreshStaticData();

		if (this.fvmScrapPartHandler == null) {
			this.fvmScrapPartHandler = new FloatValueModifier(0, 1, EaseLinear.getInstance(), 0.33f);
			this.fvmScrapPartHandler.setOnValueChangeListener(new OnValueChangeListener() {
				@Override
				public void valueChanged(float newValue) {
					MainMenuScene.this.initScrapPartLabel((int) newValue);
				}
			});
			this.fvmScrapPartHandler.runOnFinish(new Runnable() {
				@Override
				public void run() {
					MainMenuScene.this.onHandleScrapPartHandler(true);
				}
			});
		}

		final int PRICE = this.sbStoreTabs[tab][storeItemId].getPrice();

		this.subtractScrapParts(PRICE);

		this.fvmScrapPartHandler.reset();
		this.fvmScrapPartHandler.setFrom(StaticData.scrapPartsAmount);
		StaticData.scrapPartsAmount -= PRICE;
		this.fvmScrapPartHandler.setTo(StaticData.scrapPartsAmount);

		this.onHandleScrapPartHandler(false);
		this.checkStoreItemAlreadyPurchased(tab, storeItemId);
		this.redrawPostBoughtSpriteBatch();
	}

	protected void onStoreItemSufficientFunds(int tab, int storeItemId) {
	}

	protected void onTouchHandlerSwipingStarted() {
		if (this.currentSection != MainMenuScene.SECTION_MAIN)
			return;

		if (this.mmthTouchHandler.getYSwipePercent() < 0f) {

			this.sSwipeStore.detachSelf();
			this.attachChild(this.sSwipeStore);

		} else if (this.mmthTouchHandler.getYSwipePercent() > 0f) {

			this.sSwipeOptions.detachSelf();
			this.attachChild(this.sSwipeOptions);

		} else if (this.mmthTouchHandler.getXSwipePercent() < 0f) {

			if (!this.sSwipeAchievements.getParent().equals(this)) {
				this.sSwipeAchievements.detachSelf();
				this.attachChild(this.sSwipeAchievements);
			}

		} else if (this.mmthTouchHandler.getXSwipePercent() > 0f)
			if (!this.sSwipeLeaderboard.getParent().equals(this)) {
				this.sSwipeLeaderboard.detachSelf();
				this.attachChild(this.sSwipeLeaderboard);
			}
	}

	protected void onTouchHandlerTransformationFinished() {
		if (this.currentSection != MainMenuScene.SECTION_MAIN)
			return;

		if (this.sSwipeAchievements.getParent().equals(this)) {
			this.sSwipeAchievements.detachSelf();
			this.cMainContainer.attachChild(this.sSwipeAchievements);
		}

		if (this.sSwipeLeaderboard.getParent().equals(this)) {
			this.sSwipeLeaderboard.detachSelf();
			this.cMainContainer.attachChild(this.sSwipeLeaderboard);
		}

		if (this.sSwipeOptions.getParent().equals(this)) {
			this.sSwipeOptions.detachSelf();
			this.cMainContainer.attachChild(this.sSwipeOptions);
		}

		if (this.sSwipeStore.getParent().equals(this)) {
			this.sSwipeStore.detachSelf();
			this.cMainContainer.attachChild(this.sSwipeStore);
		}
	}

	protected void onTransitionFinished(int newSection) {
		MainMenuScene.unloadContainerTextures(this.currentSection);
		this.currentSection = newSection;

		switch (newSection) {
		case MainMenuScene.SECTION_STORE:

			this.cMainContainer.setY(-EnvironmentVars.MAIN_CONTEXT.height());
			this.cMainContainer.detachSelf();
			this.mmthTouchHandler.setSwipeDirections(SwipeDirections.DOWN);

			break;

		case MainMenuScene.SECTION_OPTIONS:

			this.cMainContainer.setY(EnvironmentVars.MAIN_CONTEXT.height());
			this.cMainContainer.detachSelf();
			this.mmthTouchHandler.setSwipeDirections(SwipeDirections.UP);

			break;

		case MainMenuScene.SECTION_MAIN:

			this.cMainContainer.setY(0);

			if (this.cStoreContainer != null)
				this.cStoreContainer.detachSelf();

			if (this.cOptionsContainer != null)
				this.cOptionsContainer.detachSelf();

			this.mmthTouchHandler.setSwipeDirections(SwipeDirections.UP, SwipeDirections.DOWN, SwipeDirections.LEFT, SwipeDirections.RIGHT);

			break;
		}

		EnvironmentVars.MAIN_CONTEXT.getHud().setOnSceneTouchListener(this.mmthTouchHandler);
	}

	protected void onUpdateOptionsSyncAccount() {
		final boolean CONDITION = EnvironmentVars.MAIN_CONTEXT.getGoogleApiClient().isConnected();

		final Button SYNC_ACCOUNT_BUTTON = (Button) this.scOptionsScrollContainer.getChildByTag(MainMenuOptions.TAG_SYNC_ACCOUNT);
		final TiledSprite SYNC_ACCOUNT_BUTTON_ICON = SYNC_ACCOUNT_BUTTON.getIcon();

		if (CONDITION) {
			SYNC_ACCOUNT_BUTTON_ICON.setCurrentTileIndex(1);
			SYNC_ACCOUNT_BUTTON.setDescription(MainMenuOptions.STRING_SYNC_HOLD_DOWN);
		} else {
			SYNC_ACCOUNT_BUTTON_ICON.setCurrentTileIndex(0);
			SYNC_ACCOUNT_BUTTON.setDescription("");
		}

		this.updateOptionsButton(MainMenuOptions.TAG_SYNC_ACCOUNT, CONDITION);
	}

	protected void onUpdateOptionsToggleChaseCamera() {
		this.updateOptionsButton(MainMenuOptions.TAG_TOGGLE_CHASE_CAMERA, EnvironmentVars.MAIN_CONTEXT.useChaseCamera());
	}

	protected void onUpdateOptionsToggleMusic() {
		this.updateOptionsButton(MainMenuOptions.TAG_TOGGLE_MUSIC, EnvironmentVars.MAIN_CONTEXT.useMusic());
	}

	protected void onUpdateOptionsToggleSound() {
		this.updateOptionsButton(MainMenuOptions.TAG_TOGGLE_SOUND, EnvironmentVars.MAIN_CONTEXT.useSound());
	}

	protected void playThemeSong() {
		MainMenuScene.backgroundMusic.setVolume(1f);
		MainMenuScene.backgroundMusic.seekTo(0);
		MainMenuScene.backgroundMusic.play();

		EnvironmentVars.MAIN_CONTEXT.registerSound(MainMenuScene.backgroundMusic);
	}

	public void postSubSectionInitialized(SubSection subSection) {
		this.cTransitionToContainer = subSection.getContainer();
		this.ostlTransitionToTouchListener = subSection;
		this.obplTransitionToBackPressedListener = subSection;
		this.transitionFromContainer(this.getSectionContainer(this.currentSection), false);
	}

	protected void preLaunch() {
		this.mmlInitialLaunchLibrary = new MainMenuLauncher(this);
		this.mmlInitialLaunchLibrary.beginLoadingResources();
	}

	public void preToSession() {

		if (this.fuhOnPressFlash == null) {
			this.fuhOnPressFlash = new FlashUpdateHandler(0.05f, 4);
			this.fuhOnPressFlash.runOnSwitch(new Runnable() {
				@Override
				public void run() {
					final MainMenuVisibilityEntity START_LABEL = (MainMenuVisibilityEntity) MainMenuScene.this.mmcStartLabel;
					START_LABEL.setVisible(!START_LABEL.isVisible());
				}
			});
		}
		this.fuhOnPressFlash.reset();

		if (this.fFade != null) {
			this.fFade.detachSelf();
			this.fFade.dispose();
		}

		this.fFade = new LoadingSpriteFade(EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager()) {
			@Override
			protected void onFadeIn() {
				super.onFadeIn();
				MainMenuScene.this.toSession();
			}
		};

		this.attachChild(this.fFade);

		this.fFade.setDuration(1f);
		this.fFade.setEase(EaseCubicOut.getInstance());
		this.fFade.show();

		this.clearUpdateHandlers();
		this.onMusicFadeOut();
		this.registerUpdateHandler(this.fuhOnPressFlash);

		this.sortChildren();

		EnvironmentVars.MAIN_CONTEXT.getHud().setOnSceneTouchListener(null);
	}

	protected void processOptionsButtonClick(int buttonTag) {
		switch (buttonTag) {
		case MainMenuOptions.TAG_TOGGLE_CHASE_CAMERA:
			this.onUpdateOptionsToggleChaseCamera();
			break;

		case MainMenuOptions.TAG_TOGGLE_MUSIC:
			this.onUpdateOptionsToggleMusic();
			break;

		case MainMenuOptions.TAG_TOGGLE_SOUND:
			this.onUpdateOptionsToggleSound();
			break;

		case MainMenuOptions.TAG_SYNC_ACCOUNT:
			final Button SYNC_ACCOUNT_BUTTON = (Button) this.scOptionsScrollContainer.getChildByTag(MainMenuOptions.TAG_SYNC_ACCOUNT);

			if (EnvironmentVars.MAIN_CONTEXT.getGoogleApiClient().isConnected())
				break;
			SYNC_ACCOUNT_BUTTON.setTitle(MainMenuOptions.STRING_SYNCING, false);
			break;

		case MainMenuOptions.TAG_CREDITS:
			this.toCredits();
			break;
		}
	}

	protected void processOptionsButtonLongPress(int buttonTag) {
		switch (buttonTag) {
		case MainMenuOptions.TAG_SYNC_ACCOUNT:
			final Button SYNC_ACCOUNT_BUTTON = (Button) this.scOptionsScrollContainer.getChildByTag(MainMenuOptions.TAG_SYNC_ACCOUNT);
			SYNC_ACCOUNT_BUTTON.setDescription(MainMenuOptions.STRING_SYNC_RELEASE);
			break;
		}
	}

	protected void processOptionsButtonTouchStateReset(int buttonTag) {
		switch (buttonTag) {
		case MainMenuOptions.TAG_SYNC_ACCOUNT:
			this.onUpdateOptionsSyncAccount();
			break;
		}
	}

	public void processPurchasedStoreItem(int tab, int storeItemId) {

		switch (tab) {
		case 0:
			switch (storeItemId) {
			case MainMenuStore.ID_DOMINADOR_REPEATED_FIRE:
				StaticData.dominador_repeatedFire = true;
				break;
			case MainMenuStore.ID_LASER_SIGHT:
				StaticData.laserSightItems[this.dialogConsideredWeaponIndex] = true;
				break;
			case MainMenuStore.ID_GLITCH_CLIP:
				StaticData.glitchClipItems[this.dialogConsideredWeaponIndex] = true;
				break;
			case MainMenuStore.ID_INJECTION_TIPS:
				StaticData.sniper_injectionTips = true;
				break;
			}
			break;
		case 1:
			switch (storeItemId) {
			case MainMenuStore.ID_DYE_APPLICATOR:
				MainMenuScene.canDyeClothes = true;
				EnvironmentVars.PREFERENCES_EDITOR.putBoolean(PreferenceKeys.KEY_CAN_DYE_CLOTHES, true);
				break;
			case MainMenuStore.ID_CLOTH_DYE:
				StaticData.clothDyeAmount += MainMenuStore.CLOTH_DYE_PACK;
				EnvironmentVars.PREFERENCES_EDITOR.putInt(PreferenceKeys.KEY_CLOTH_DYE_AMOUNT, StaticData.clothDyeAmount);
				break;
			}
			break;
		}

		EnvironmentVars.PREFERENCES_EDITOR.commit();
	}

	void reattachSwipeIcons() {
		this.sSwipeAchievements.detachSelf();
		this.sSwipeLeaderboard.detachSelf();
		this.sSwipeOptions.detachSelf();
		this.sSwipeStore.detachSelf();

		this.cMainContainer.attachChild(this.sSwipeAchievements);
		this.cMainContainer.attachChild(this.sSwipeLeaderboard);
		this.cMainContainer.attachChild(this.sSwipeOptions);
		this.cMainContainer.attachChild(this.sSwipeStore);
	}

	public void redrawPostBoughtSpriteBatch() {
		ITextureRegion tr = null;
		StoreButton sb = null;
		boolean submit = false;

		for (int x = 0; x < this.storeItemsBought.length; x++)
			for (int y = 0; y < this.storeItemsBought[x].length; y++)
				if (this.storeItemsBought[x][y]) {

					// WARDROBE
					if (x == 1 && y == MainMenuStore.ID_WARDROBE)
						continue;

					// DYE APPLICATOR
					if (x == 1 && y == MainMenuStore.ID_DYE_APPLICATOR)
						continue;

					tr = StoreTextureRegions.region_already_bought;
					sb = this.sbStoreTabs[x][y];

					//@formatter:off
					this.sbStoreButtonPostBought.draw(
							tr,
							sb.getX() + (sb.getBoundaryWidth() - tr.getWidth() * 4) / 2f,
							sb.getY() + (sb.getBoundaryHeight() - tr.getHeight() * 4) / 2f,
							tr.getWidth() * 4,
							tr.getHeight() * 4,
							MainMenuScene.storeItemBoughtRotations[x][y],
							1, 1, 1, 1);
					//@formatter:on

					submit = true;
				}
		if (submit)
			this.sbStoreButtonPostBought.submit();
	}

	@Override
	public void setEntityMaxZIndex(IEntity entity) {

		if (!MainMenuScene.areResourcesLoaded())
			return;

		super.setEntityMaxZIndex(entity);
	}

	private void setStoreScrollContainerMinY() {
		final float BUTTON_HEIGHT_TOTAL = Button.BUTTON_HEIGHT * this.sbStoreTabs[this.currentStoreTabIndex].length;

		if (BUTTON_HEIGHT_TOTAL > EnvironmentVars.MAIN_CONTEXT.height() - this.storeScrollContainerY)
			this.scStoreScrollContainer.setMinY(this.storeScrollContainerY - BUTTON_HEIGHT_TOTAL
					+ (EnvironmentVars.MAIN_CONTEXT.height() - this.storeScrollContainerY));
		else
			this.scStoreScrollContainer.setMinY(this.storeScrollContainerY);
	}

	public void showAchievements() {

		EnvironmentVars.MAIN_CONTEXT.runOnUiThread(new Runnable() {
			@Override
			public void run() {

				if (MainMenuScene.CLIENT.isConnected())
					EnvironmentVars.MAIN_CONTEXT.startActivityForResult(Games.Achievements.getAchievementsIntent(MainMenuScene.CLIENT),
							GooglePlayConstants.ACHIEVEMENTS_REQUEST_CODE);
				else
					MainMenuScene.CLIENT.connect();
			}
		});
	}

	public void showLeaderboard() {

		EnvironmentVars.MAIN_CONTEXT.runOnUiThread(new Runnable() {
			@Override
			public void run() {

				if (MainMenuScene.CLIENT.isConnected())
					EnvironmentVars.MAIN_CONTEXT.startActivityForResult(
							Games.Leaderboards.getLeaderboardIntent(MainMenuScene.CLIENT, GooglePlayConstants.LEADERBOARD_ID),
							GooglePlayConstants.LEADERBOARD_REQUEST_CODE);
				else
					MainMenuScene.CLIENT.connect();
			}
		});
	}

	public void subtractScrapParts(int subtraction) {
		EnvironmentVars.PREFERENCES_EDITOR.putInt(PreferenceKeys.KEY_SCRAP_PARTS_AMOUNT, StaticData.scrapPartsAmount - subtraction);
		EnvironmentVars.PREFERENCES_EDITOR.commit();
	}

	protected boolean superOnSceneTouchEvent(TouchEvent sceneTouchEvent) {
		return super.onSceneTouchEvent(sceneTouchEvent);
	}

	public void toCredits() {
		if (this.mmcCredits == null)
			this.mmcCredits = new MainMenuCredits(this);
		this.toSubSection(this.mmcCredits);
	}

	public void toDyeClothes() {
		if (this.mmepDyeClothes == null)
			this.mmepDyeClothes = new MainMenuDyeClothes(this);
		this.toSubSection(this.mmepDyeClothes);
	}

	public void toMain() {

		if (this.currentSection == MainMenuScene.SECTION_OPTIONS)
			this.scOptionsScrollContainer.onTouchRelease(false);
		else if (this.currentSection == MainMenuScene.SECTION_STORE)
			this.scStoreScrollContainer.reset();

		this.sSwipeOptions.detachSelf();
		this.sSwipeStore.detachSelf();
		this.cMainContainer.attachChild(this.sSwipeOptions);
		this.cMainContainer.attachChild(this.sSwipeStore);

		this.sSwipeOptions.setAlpha(1);
		this.sSwipeStore.setAlpha(1);

		this.sSwipeAchievements.setScale(2);
		this.sSwipeLeaderboard.setScale(2);
		this.sSwipeOptions.setScale(2);
		this.sSwipeStore.setScale(2);

		this.toSection(MainMenuScene.SECTION_MAIN);
	}

	public void toNextStoreTab() {
		this.oldStoreTabTitle = MainMenuStore.getTab(this.currentStoreTabIndex).tabTitle;
		this.oldStoreTabTitleWidth = this.storeTabTitleWidths[this.currentStoreTabIndex];
		this.currentStoreTabIndex++;
		this.changeStoreTab();
	}

	public void toOptions() {
		try {
			if (this.sSwipeOptions.getParent() != this) {
				this.sSwipeOptions.detachSelf();
				this.attachChild(this.sSwipeOptions);
			}

			EntityUtils.animateEntity(this.sSwipeOptions, 0.45f, EntityUtils.ANIMATION_SCALE_OUT_FADE_OUT, EaseSineOut.getInstance(),
					EntityUtils.getDetachListener());

		} catch (final Exception ex) {

		}

		this.initOptions();

		this.scOptionsScrollContainer.setY(this.scOptionsScrollContainer.getMinY());

		this.toSection(MainMenuScene.SECTION_OPTIONS);

		this.onUpdateOptionsToggleChaseCamera();
		this.onUpdateOptionsToggleMusic();
		this.onUpdateOptionsToggleSound();
		this.onUpdateOptionsSyncAccount();
	}

	public void toPreviousStoreTab() {
		this.oldStoreTabTitle = MainMenuStore.getTab(this.currentStoreTabIndex).tabTitle;
		this.oldStoreTabTitleWidth = this.storeTabTitleWidths[this.currentStoreTabIndex];
		this.currentStoreTabIndex--;
		this.changeStoreTab();
	}

	protected void toSection(final int section) {

		if (this.currentSection == section)
			return;

		MainMenuScene.HUD.setOnSceneTouchListener(null);
		MainMenuScene.loadContainerTextures(section);

		this.attachChild(this.getSectionContainer(section));

		float transitionTo = 0;

		switch (section) {

		case MainMenuScene.SECTION_MAIN:
			transitionTo = 0;
			break;

		case MainMenuScene.SECTION_OPTIONS:
			transitionTo = EnvironmentVars.MAIN_CONTEXT.height();
			break;

		case MainMenuScene.SECTION_STORE:
			transitionTo = -EnvironmentVars.MAIN_CONTEXT.height();
			break;
		}

		final MoveYModifier ANIMATION = new MoveYModifier(0.5f, this.cMainContainer.getY(), transitionTo, EaseCubicInOut.getInstance());

		ANIMATION.setAutoUnregisterWhenFinished(true);

		ANIMATION.addModifierListener(new IModifierListener<IEntity>() {
			@Override
			public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
				EnvironmentVars.MAIN_CONTEXT.runOnUpdateThread(new Runnable() {
					@Override
					public void run() {
						MainMenuScene.this.onTransitionFinished(section);
					}
				});
			}

			@Override
			public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
			}
		});

		this.cMainContainer.registerEntityModifier(ANIMATION);
	}

	/**
	 * Disposes this scene and starts a new session.
	 */
	public void toSession() {

		MainMenuScene.unloadContainerTextures(MainMenuScene.SECTION_MAIN);
		MainMenuScene.unloadContainerTextures(MainMenuScene.SECTION_OPTIONS);
		MainMenuScene.unloadContainerTextures(MainMenuScene.SECTION_STORE);

		ResourceManager.btBgShadow.unload();
		
		EnvironmentVars.MAIN_CONTEXT.removeAdListener(this);

		EnvironmentVars.MAIN_CONTEXT.unregisterSound(MainMenuScene.backgroundMusic);
		MainMenuScene.backgroundMusic.setVolume(0f);
		MainMenuScene.backgroundMusic.pause();
		MainMenuScene.postSession = true;
		MainMenuScene.playTutorialSession = !EnvironmentVars.PREFERENCES.getBoolean(PreferenceKeys.KEY_HAS_PLAYED_TUTORIAL, false);

		EnvironmentVars.MAIN_CONTEXT.hideAd();

		final AsyncTask<Void, Void, Void> SESSION_LOADING_TASK = new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				if (MainMenuScene.playTutorialSession)
					EnvironmentVars.TUTORIAL_SESSION_SCENE = new TutorialSessionScene(Levels.loadLevelFromId(Levels.ID_LEVEL1));
				else
					EnvironmentVars.SESSION_SCENE = new SessionScene(Levels.loadLevelFromId(Levels.ID_LEVEL1));

				MainMenuScene.this.isSessionLoaded = true;
				return null;
			}
		};

		SESSION_LOADING_TASK.execute();
	}

	public void toStore() {

		try {

			if (this.sSwipeStore.getParent() != this) {
				this.sSwipeStore.detachSelf();
				this.attachChild(this.sSwipeStore);
			}

			EntityUtils.animateEntity(this.sSwipeStore, 0.45f, EntityUtils.ANIMATION_SCALE_OUT_FADE_OUT, EaseSineOut.getInstance(),
					EntityUtils.getDetachListener());

		} catch (final Exception ex) {

		}

		this.initStore();
		this.initScrapPartLabel(StaticData.scrapPartsAmount);
		this.toSection(MainMenuScene.SECTION_STORE);
	}

	protected void toSubSection(SubSection subSection) {
		if (subSection.getContainer() == null) {
			final SubSection SUBSECTION = subSection;
			final AsyncTask<Void, Void, Void> CONTAINER_INIT_TASK = new AsyncTask<Void, Void, Void>() {

				@Override
				protected Void doInBackground(Void... params) {
					SUBSECTION.initContainer();
					MainMenuScene.this.postSubSectionInitialized(SUBSECTION);
					return null;
				}
			};

			CONTAINER_INIT_TASK.execute();
		} else
			MainMenuScene.this.postSubSectionInitialized(subSection);
	}

	public void toWardrobe() {
		if (this.mmwWardrobe == null)
			this.mmwWardrobe = new MainMenuWardrobe(this);
		this.toSubSection(this.mmwWardrobe);
	}

	public void transitionFromContainer(final Entity from) {
		this.transitionFromContainer(from, true);
	}

	public void transitionFromContainer(final Entity from, boolean scaleFromUp) {
		EnvironmentVars.MAIN_CONTEXT.getHud().setOnSceneTouchListener(null);
		this.eTransitionFromContainer = from;

		final boolean SCALE_FROM_UP = scaleFromUp;

		if (this.fvmTransition != null)
			this.unregisterUpdateHandler(this.fvmTransition);

		this.fvmTransition = new FloatValueModifier(1f, 0f, EaseSineInOut.getInstance(), 0.25f) {
			@Override
			protected void onFinished() {
				super.onFinished();

				MainMenuScene.this.eTransitionFromContainer.detachSelf();
				EntityUtils.safetlyUnregisterUpdateHandler(MainMenuScene.this, MainMenuScene.this.fvmTransition);

				EnvironmentVars.MAIN_CONTEXT.getHud().setOnSceneTouchListener(MainMenuScene.this.ostlTransitionToTouchListener);
				EnvironmentVars.MAIN_CONTEXT.addOnBackPressedListener(MainMenuScene.this.obplTransitionToBackPressedListener);
			}

			@Override
			protected void onValueChanged(float value) {
				super.onValueChanged(value);

				MainMenuScene.this.eTransitionFromContainer.setAlpha(value);

				if (!SCALE_FROM_UP)
					MainMenuScene.this.eTransitionFromContainer.setScale(0.75f + value * 0.25f);
				else
					MainMenuScene.this.eTransitionFromContainer.setScale(1.25f - value * 0.25f);

				value = 1f - value;
				MainMenuScene.this.cTransitionToContainer.setAlpha(value);

				if (!SCALE_FROM_UP)
					MainMenuScene.this.cTransitionToContainer.setScale(1.25f - value * 0.25f);
				else
					MainMenuScene.this.cTransitionToContainer.setScale(0.75f + value * 0.25f);
			}
		};

		EnvironmentVars.MAIN_CONTEXT.clearOnBackPressedListeners();

		this.fvmTransition.onUpdate(0f);
		this.registerUpdateHandler(this.fvmTransition);

		this.cTransitionToContainer.detachSelf();
		this.attachChild(this.cTransitionToContainer);
	}

	protected void updateOptionsButton(int buttonTag, boolean condition) {
		final Button BUTTON = (Button) this.scOptionsScrollContainer.getChildByTag(buttonTag);
		final String[] TITLES = MainMenuOptions.BUTTON_TITLES[buttonTag - MainMenuOptions.BUTTON_TAGS[0]].split("/");

		if (condition)
			BUTTON.setTitle(TITLES[0]);
		else
			BUTTON.setTitle(TITLES[1]);

		if (BUTTON instanceof ToggleButton)
			((ToggleButton) BUTTON).setChecked(!condition, false);
	}

	public void useStartTransition() {

		if (this.mmlInitialLaunchLibrary.tTouchToStart == null || this.mmlInitialLaunchLibrary.tTouchToStart.getEntityModifierCount() > 0)
			return;

		this.init();

		EntityUtils.animateEntity(this.mmlInitialLaunchLibrary.dasLogoBackground, 0.4f, EntityUtils.ANIMATION_SCALE_OUT_FADE_OUT,
				EaseCubicIn.getInstance(), EntityUtils.getDetachDisposeUnloadTextureListener());
		EntityUtils.animateEntity(this.mmlInitialLaunchLibrary.sLogo, 0.4f, EntityUtils.ANIMATION_SCALE_OUT_FADE_OUT,
				EaseCubicIn.getInstance(), EntityUtils.getDetachDisposeUnloadTextureListener());
		EntityUtils.animateEntity(this.mmlInitialLaunchLibrary.tTouchToStart, 0.4f, EntityUtils.ANIMATION_SCALE_OUT_FADE_OUT,
				EaseCubicIn.getInstance(), EntityUtils.getDetachDisposeListener());

		this.sdtStartTransition = new SimpleDissolveTransition(EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager()) {
			@Override
			protected void onAnimationFinish() {
				super.onAnimationFinish();
				EntityUtils.safetlyDetach(this);

				if (!MainMenuScene.this.isNavDemoShowing)
					EnvironmentVars.MAIN_CONTEXT.onStartMakeConnection();

				MainMenuScene.this.checkNavDemo();
			}
		};
		this.sdtStartTransition.animate(1.25f, EaseLinear.getInstance());
		this.attachChild(this.sdtStartTransition);

		if (this.tSmallCredits != null)
			EntityUtils.animateEntity(this.tSmallCredits, 0.25f, EntityUtils.ANIMATION_FADE_OUT, EaseSineInOut.getInstance(),
					EntityUtils.getDetachDisposeListener());

		EntityUtils.safetlyDetachAndDispose(this.rBackgroundRain);

		this.hasUsedStartTransition = true;
	}
}
