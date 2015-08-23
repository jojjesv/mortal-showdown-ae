package com.partlight.ms.session.hud;

import org.andengine.util.color.Color;

import com.partlight.ms.entity.ShadowedText;
import com.partlight.ms.resource.EnvironmentVars;
import com.partlight.ms.resource.ResourceManager;
import com.partlight.ms.scene.session.SessionScene;

public class ScoreTracker extends BaseScreenComponent {

	private final ShadowedText stScore;

	public ScoreTracker(SessionScene context) {
		super(0, 0);

		this.setContext(context);
		this.setScale(1f);
		this.stScore = new ShadowedText(0f, 0f, ResourceManager.fFontMain, String.valueOf(Long.MAX_VALUE),
				EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager());
		this.stScore.setColor(Color.BLACK);
		this.stScore.setScale(2.5f);
		this.stScore.setPosition(this.getBoundaryX(), this.getBoundaryY());
		this.stScore.setRotation(-1.5f);
		this.stScore.setColor(new Color(0.85f, 0.3f, 0.3f));
		this.setBackground(this.stScore);
		this.updateScoreText();
	}

	@Override
	public float getBoundaryHeight() {
		return this.stScore.getHeightScaled();
	}

	@Override
	public float getBoundaryWidth() {
		return this.stScore.getWidthScaled();
	}

	@Override
	public float getBoundaryX() {
		return 8f;
	}

	@Override
	public float getBoundaryY() {
		return 8f;
	}

	public void setScorePosition(float x, float y) {
		this.stScore.setPosition(x, y);
	}

	public void setScoreX(float x) {
		this.setScorePosition(x, this.stScore.getY());
	}

	public void setScoreY(float y) {
		this.setScorePosition(this.stScore.getX(), y);
	}

	public void updateScoreText() {
		this.stScore.setText(String.valueOf(((SessionScene) this.getContext()).getSessionData().sessionScore));
		this.stScore.setScaleCenter(0, 0);
	}
}
