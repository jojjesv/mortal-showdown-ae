package com.partlight.ms.scene.mainmenu;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.IEntity;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.opengl.font.FontUtils;
import org.andengine.opengl.util.GLState;
import org.andengine.util.color.Color;
import org.andengine.util.modifier.IModifier;
import org.andengine.util.modifier.ease.EaseCubicIn;
import org.andengine.util.modifier.ease.EaseSineInOut;

import com.partlight.ms.entity.DissolveAnimatedSprite;
import com.partlight.ms.entity.LoadingSprite;
import com.partlight.ms.resource.EnvironmentVars;
import com.partlight.ms.resource.ResourceManager;
import com.partlight.ms.resource.ResourceManager.MainMenuRegions;
import com.partlight.ms.resource.load.ResourceLoadingTask;
import com.partlight.ms.shader.TintShaderProgram;
import com.partlight.ms.util.EntityModifierAdapter;
import com.partlight.ms.util.EntityUtils;

class MainMenuLauncher {

	private static final String	START_TEXT	= "TOUCH THE SCREEN";
	private static final Color	LOGO_COLOR_0;
	private static final Color	LOGO_COLOR_1;

	static {
		LOGO_COLOR_0 = Color.WHITE;
		LOGO_COLOR_1 = new Color(0.7f, 0.7f, 0.7f);
	}

	public DissolveAnimatedSprite	dasLogoBackground;
	public Sprite					sLogo;
	public Text						tTouchToStart;
	public LoadingSprite			sLoading;
	private final MainMenuScene		mmsContext;
	private boolean					canFadeOutLoadingIcon;

	public MainMenuLauncher(MainMenuScene context) {
		this.mmsContext = context;
	}

	public void beginLoadingResources() {
		final ResourceLoadingTask TASK = new ResourceLoadingTask(this.mmsContext);
		TASK.execute();
	}

	private void createTouchText() {
		float textX = 0f, textY = 0f;

		textX = (EnvironmentVars.MAIN_CONTEXT.width() - FontUtils.measureText(ResourceManager.fFontMain, MainMenuLauncher.START_TEXT)) / 2f;
		textY = EnvironmentVars.MAIN_CONTEXT.height() * 0.533f;

		this.tTouchToStart = new Text(textX, textY, ResourceManager.fFontMain, MainMenuLauncher.START_TEXT,
				EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager()) {
			@Override
			protected void onManagedUpdate(float pSecondsElapsed) {
				super.onManagedUpdate(pSecondsElapsed);
				EntityUtils.animateSineColor(this, MainMenuLauncher.LOGO_COLOR_0, MainMenuLauncher.LOGO_COLOR_1, 2.5f / 2f);
			}

			@Override
			public void setColor(float pRed, float pGreen, float pBlue, float pAlpha) {
				super.setColor(pRed, pGreen, pBlue);
			}
		};
		this.tTouchToStart.setScaleCenterY(EnvironmentVars.MAIN_CONTEXT.height() / 2f - textY);
		this.tTouchToStart.setScale(2.35f);

		EntityUtils.animateEntity(this.tTouchToStart, 0.25f, EntityUtils.ANIMATION_JUMP_IN_MEDIUM_INTESTIVITY);

		EnvironmentVars.MAIN_CONTEXT.getHud().attachChild(this.tTouchToStart);
	}

	public void onLoadingResourcesLoaded() {
		ResourceManager.btLogo.load();
		ResourceManager.btLogoBackground.load();
		ResourceManager.btStrokeMap.load();

		float loadingX = 0f, loadingY = 0f, logoBackgroundX = 0f, logoBackgroundY = 0f;

		loadingX = (EnvironmentVars.MAIN_CONTEXT.width() - MainMenuRegions.region_loading.getWidth()) / 2f;
		loadingY = EnvironmentVars.MAIN_CONTEXT.height() * 0.75f - MainMenuRegions.region_loading.getHeight() / 2f;

		this.sLoading = new LoadingSprite(loadingX, loadingY, EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager()) {

			@Override
			protected void onManagedUpdate(float pSecondsElapsed) {
				super.onManagedUpdate(pSecondsElapsed);

				if (MainMenuLauncher.this.canFadeOutLoadingIcon)
					if (this.getEntityModifierCount() == 0) {
						MainMenuLauncher.this.canFadeOutLoadingIcon = false;
						EntityUtils.animateEntity(this, 0.25f, EntityUtils.ANIMATION_FADE_OUT, EaseSineInOut.getInstance(),
								new EntityModifierAdapter() {
							@Override
							public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
								super.onModifierFinished(pModifier, pItem);
								EntityUtils.getDetachDisposeListener().onModifierFinished(pModifier, pItem);
								MainMenuLauncher.this.createTouchText();
								MainMenuLauncher.this.sLoading = null;
							}
						});
					}
			}
		};

		EntityUtils.animateEntity(this.sLoading, 0.5f, EntityUtils.ANIMATION_FADE_IN);
		this.mmsContext.attachChild(this.sLoading);

		logoBackgroundX = (EnvironmentVars.MAIN_CONTEXT.width() - MainMenuRegions.region_logo_background.getWidth()) / 2f;
		logoBackgroundY = EnvironmentVars.MAIN_CONTEXT.height() * 0.15f;

		this.dasLogoBackground = new DissolveAnimatedSprite(logoBackgroundX, logoBackgroundY, MainMenuRegions.region_logo_background,
				ResourceManager.btStrokeMap, EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager(), true) {

			private float totalSecondsElapsed;

			@Override
			protected void draw(GLState pGLState, Camera pCamera) {
				pGLState.enableDither();
				super.draw(pGLState, pCamera);
			}

			@Override
			protected void onAnimationFinish() {
				super.setShaderProgram(TintShaderProgram.getMultipliedInstance());
			}

			@Override
			protected void onManagedUpdate(float pSecondsElapsed) {
				super.onManagedUpdate(pSecondsElapsed);

				if (this.totalSecondsElapsed >= 0.33f && MainMenuLauncher.this.sLogo == null) {
					float logoX = 0f, logoY = 0f;

					logoX = MainMenuLauncher.this.dasLogoBackground.getX()
							+ (MainMenuRegions.region_logo_background.getWidth() - MainMenuRegions.region_logo.getWidth()) / 2f;
					logoY = MainMenuLauncher.this.dasLogoBackground.getY()
							+ (MainMenuRegions.region_logo_background.getHeight() - MainMenuRegions.region_logo.getHeight()) / 2f;

					final Sprite LOGO = MainMenuLauncher.this.sLogo = new Sprite(logoX, logoY, MainMenuRegions.region_logo,
							EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager());

					LOGO.setScaleCenterY(EnvironmentVars.MAIN_CONTEXT.height() / 2f - LOGO.getY());
					LOGO.setShaderProgram(TintShaderProgram.getMultipliedInstance());

					EntityUtils.animateEntity(LOGO, 0.15f, EntityUtils.ANIMATION_SCALE_OUT_FADE_IN, EaseCubicIn.getInstance());
					EnvironmentVars.MAIN_CONTEXT.getHud().attachChild(LOGO);

				} else
					this.totalSecondsElapsed += pSecondsElapsed;

			}

			@Override
			public void setColor(float pRed, float pGreen, float pBlue, float pAlpha) {
				super.setColor(pRed, pGreen, pBlue, this.getAlpha());
				if (MainMenuLauncher.this.sLogo != null)
					MainMenuLauncher.this.sLogo.setColor(pRed, pGreen, pBlue);
			}
		};
		this.dasLogoBackground.setScaleCenterY(EnvironmentVars.MAIN_CONTEXT.height() / 2f - this.dasLogoBackground.getY());
		this.dasLogoBackground.animate(0.4f, EaseSineInOut.getInstance());
		EnvironmentVars.MAIN_CONTEXT.getHud().attachChild(this.dasLogoBackground);
	}

	public void onResourcesLoaded() {
		this.canFadeOutLoadingIcon = true;
	}
}
