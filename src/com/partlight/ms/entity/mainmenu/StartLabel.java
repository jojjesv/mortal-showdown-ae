package com.partlight.ms.entity.mainmenu;

import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.shader.PositionColorTextureCoordinatesShaderProgram;
import org.andengine.util.color.Color;

import com.partlight.ms.entity.ShadowedText;
import com.partlight.ms.resource.EnvironmentVars;
import com.partlight.ms.resource.ResourceManager;
import com.partlight.ms.resource.ResourceManager.StrokeTextureRegions;
import com.partlight.ms.scene.mainmenu.MainMenuScene;
import com.partlight.ms.util.ColorConstants;
import com.partlight.ms.util.EntityUtils;

public class StartLabel extends MainMenuVisibilityEntity {

	public static final String	LABEL_TOP_STRING	= "TOUCH TO BEGIN YOUR";
	public static final String	LABEL_BOTTOM_STRING	= "SHOWDOWN";
	public static final Color	LABEL_TOP_COLOR1;
	public static final Color	LABEL_TOP_COLOR2;
	public static final Color	LABEL_BOTTOM_COLOR1;
	public static final Color	LABEL_BOTTOM_COLOR2;

	static {
		LABEL_TOP_COLOR1 = ColorConstants.LIGHT_RED;
		LABEL_TOP_COLOR2 = ColorConstants.DARK_RED;
		LABEL_BOTTOM_COLOR1 = ColorConstants.RED;
		LABEL_BOTTOM_COLOR2 = new Color(0.6f, 0.1f, 0.1f);
	}

	private Sprite			sStroke;
	private ShadowedText	stBeginShowdownTop;

	private ShadowedText stBeginShowdownBottom;

	@Override
	public void attachToScene(MainMenuScene scene) {
		scene.getSectionContainer(MainMenuScene.SECTION_MAIN).attachChild(this.sStroke);
		scene.getSectionContainer(MainMenuScene.SECTION_MAIN).attachChild(this.stBeginShowdownTop);
		scene.getSectionContainer(MainMenuScene.SECTION_MAIN).attachChild(this.stBeginShowdownBottom);
	}

	@Override
	public void detach() {
		this.sStroke.detachSelf();
		this.stBeginShowdownTop.detachSelf();
		this.stBeginShowdownBottom.detachSelf();
	}

	@Override
	public void dispose() {
		this.sStroke.dispose();
		this.stBeginShowdownTop.dispose();
		this.stBeginShowdownBottom.dispose();
	}

	@Override
	public void init() {
		this.sStroke = new Sprite(0f, 0f, StrokeTextureRegions.region_stroke_4,
				EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager());
		this.sStroke.setScale(2f);
		this.sStroke.setX((EnvironmentVars.MAIN_CONTEXT.width() - this.sStroke.getWidth()) / 2f);
		this.sStroke.setY((EnvironmentVars.MAIN_CONTEXT.height() - this.sStroke.getHeight()) / 2f - 6f);

		this.stBeginShowdownTop = new ShadowedText(0f, 0f, ResourceManager.fFontMain, StartLabel.LABEL_TOP_STRING,
				EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager()) {
			@Override
			protected void onManagedUpdate(float pSecondsElapsed) {
				super.onManagedUpdate(pSecondsElapsed);
				EntityUtils.animateSineColor(this, StartLabel.LABEL_TOP_COLOR1, StartLabel.LABEL_TOP_COLOR2, 1.0f);
			}
		};

		this.stBeginShowdownBottom = new ShadowedText(0f, 0f, ResourceManager.fFontMain, StartLabel.LABEL_BOTTOM_STRING,
				EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager()) {
			@Override
			protected void onManagedUpdate(float pSecondsElapsed) {
				super.onManagedUpdate(pSecondsElapsed);
				EntityUtils.animateSineColor(this, StartLabel.LABEL_BOTTOM_COLOR1, StartLabel.LABEL_BOTTOM_COLOR2, 0.66f);
			}
		};
		this.stBeginShowdownTop.setShaderProgram(PositionColorTextureCoordinatesShaderProgram.getInstance());
		this.stBeginShowdownBottom.setShaderProgram(PositionColorTextureCoordinatesShaderProgram.getInstance());
		this.stBeginShowdownTop.setScale(2f);
		this.stBeginShowdownBottom.setScale(3f);
		this.stBeginShowdownTop.setX((EnvironmentVars.MAIN_CONTEXT.width() - this.stBeginShowdownTop.getWidth()) / 2f);
		this.stBeginShowdownBottom.setX((EnvironmentVars.MAIN_CONTEXT.width() - this.stBeginShowdownBottom.getWidth()) / 2f);
		this.stBeginShowdownTop.setY(EnvironmentVars.MAIN_CONTEXT.height() / 2f - this.stBeginShowdownTop.getHeightScaled());
		this.stBeginShowdownBottom.setY(EnvironmentVars.MAIN_CONTEXT.height() / 2f);
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		this.stBeginShowdownTop.setVisible(visible);
		this.stBeginShowdownBottom.setVisible(visible);
	}
}
