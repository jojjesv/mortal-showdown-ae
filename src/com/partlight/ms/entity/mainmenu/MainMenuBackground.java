package com.partlight.ms.entity.mainmenu;

import java.util.Random;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.Entity;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.tmx.TMXLayer;
import org.andengine.opengl.util.GLState;
import org.andengine.util.color.Color;

import com.partlight.ms.resource.EnvironmentVars;
import com.partlight.ms.resource.ResourceManager.MiscRegions;
import com.partlight.ms.scene.mainmenu.MainMenuScene;
import com.partlight.ms.session.character.Zombie;
import com.partlight.ms.session.character.ai.IdleAI;
import com.partlight.ms.session.character.skin.zombie.Zombie1CharacterSkin;
import com.partlight.ms.session.level.Level;
import com.partlight.ms.session.level.Levels;
import com.partlight.ms.shader.LinearGradientShaderProgram;
import com.partlight.ms.util.ColorConstants;
import com.partlight.ms.util.listener.OnResumeListener;

public class MainMenuBackground extends Entity implements OnResumeListener {

	public static final int	ZOMBIE_COUNT	= 10;
	private static Level	lLevel;

	public static Level getLevel() {
		return MainMenuBackground.lLevel;
	}

	public static final void loadResources() {
		MainMenuBackground.lLevel = Levels.loadLevelFromId(Levels.ID_LEVEL1);

		final TMXLayer LAYER = MainMenuBackground.lLevel.getMainLayer();
		LAYER.setScaleCenter(0, 0);
		LAYER.setScale(2);
	}

	private Sprite sGradient;

	private final MainMenuScene mmsContext;

	public MainMenuBackground(MainMenuScene context) {
		MainMenuBackground.lLevel.getMainLayer().detachSelf();
		this.attachChild(MainMenuBackground.lLevel.getMainLayer());
		this.mmsContext = context;

		for (int i = 0; i < MainMenuBackground.ZOMBIE_COUNT; i++)
			this.addGeneratedZombie();

		this.initGradient();
	}

	public void addGeneratedZombie() {
		final Random RANDOM = new Random();
		final Zombie ZOMBIE = new Zombie(MainMenuBackground.lLevel.getMapWidth() * RANDOM.nextFloat(),
				MainMenuBackground.lLevel.getMapHeight() * RANDOM.nextFloat(), new Zombie1CharacterSkin(true), 1, this.mmsContext);

		ZOMBIE.setMoveSpeed(0.2f + 0.4f * RANDOM.nextFloat());

		final IdleAI AI = new IdleAI(ZOMBIE, 0, 0, MainMenuBackground.lLevel.getMapWidth(), MainMenuBackground.lLevel.getMapHeight());
		AI.generateTargetPosition();

		ZOMBIE.setAI(AI);
		ZOMBIE.startMoving();

		this.attachChild(ZOMBIE);
	}

	private void initGradient() {
		this.sGradient = new Sprite(0f, 0f, MiscRegions.region_empty, EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager()) {
			@Override
			protected void preDraw(GLState pGLState, Camera pCamera) {
				super.preDraw(pGLState, pCamera);
				pGLState.enableDither();
			}
		};
		this.sGradient.setShaderProgram(
				new LinearGradientShaderProgram(Color.BLACK, ColorConstants.MAROON, LinearGradientShaderProgram.DIRECTION_TOP_TO_BOTTOM));
		this.sGradient.setScaleCenter(0, 0);
		this.sGradient.setScale(EnvironmentVars.MAIN_CONTEXT.width(), EnvironmentVars.MAIN_CONTEXT.height());
		this.sGradient.setAlpha(0.75f);
		this.sGradient.setZIndex((int) EnvironmentVars.MAIN_CONTEXT.height() + 96);
		this.attachChild(this.sGradient);
	}

	@Override
	public void onAttached() {
		EnvironmentVars.MAIN_CONTEXT.addOnResumeListener(this);
	}

	@Override
	public void onDetached() {
		EnvironmentVars.MAIN_CONTEXT.removeOnResumeListener(this);
	}

	@Override
	public void onResume() {
		this.sGradient.getShaderProgram().setCompiled(false);
	}
}
