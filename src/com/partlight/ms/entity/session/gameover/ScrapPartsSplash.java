package com.partlight.ms.entity.session.gameover;

import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.shader.PositionColorTextureCoordinatesShaderProgram;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.modifier.ease.EaseLinear;

import com.partlight.ms.entity.DissolveAnimatedSprite;
import com.partlight.ms.entity.ShadowedText;
import com.partlight.ms.resource.EnvironmentVars;
import com.partlight.ms.resource.ResourceManager;
import com.partlight.ms.resource.ResourceManager.MiscRegions;
import com.partlight.ms.resource.ResourceManager.StrokeTextureRegions;
import com.partlight.ms.shader.DissolveShaderProgram;
import com.partlight.ms.util.ColorConstants;
import com.partlight.ms.util.EntityUtils;
import com.partlight.ms.util.updatehandler.FloatValueModifier;

public class ScrapPartsSplash extends DissolveAnimatedSprite {

	private float				totalSecondsElapsed;
	private int					state;
	private final int			scrapPartsAmount;
	private Sprite				sScrapPartsIcon;
	private ShadowedText		stScrapPartsText;
	private FloatValueModifier	fvmScrapPartsTextMod;

	public ScrapPartsSplash(int scrapPartsAmount, VertexBufferObjectManager vertexBufferObjectManager) {
		super(0f, 96f, StrokeTextureRegions.region_stroke_5, ResourceManager.btStrokeMap, vertexBufferObjectManager);
		this.scrapPartsAmount = scrapPartsAmount;
		((DissolveShaderProgram) this.getShaderProgram()).setInverted(true);
		this.setScaleCenter(0, 0);
		this.setScale(2f);
	}

	@Override
	protected void onAnimationFinish() {
		super.onAnimationFinish();
		this.setShaderProgram(PositionColorTextureCoordinatesShaderProgram.getInstance());
	}

	protected void onAnimationFinished() {

	}

	@Override
	public void onAttached() {
		super.onAttached();
		ResourceManager.btStroke5.load();
		ResourceManager.btStrokeMap.load();
	}

	@Override
	public void onDetached() {
		super.onDetached();
		ResourceManager.btStroke5.unload();
		ResourceManager.btStrokeMap.unload();
	}

	@Override
	protected void onManagedUpdate(float pSecondsElapsed) {
		super.onManagedUpdate(pSecondsElapsed);

		switch (this.state) {
		case 0:
			this.animate(0.15f);
			this.state = 1;
			break;
		case 1:
			if (this.totalSecondsElapsed >= 0.2f) {
				ResourceManager.btScrapPartsBig.load();

				this.sScrapPartsIcon = new Sprite(4f, 8f, MiscRegions.region_scrap_part_icon02,
						EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager());
				this.attachChild(this.sScrapPartsIcon);
				EntityUtils.animateEntity(this.sScrapPartsIcon, 0.1f, EntityUtils.ANIMATION_SCALE_OUT_JAGGED);

				this.totalSecondsElapsed = 0f;
				this.state = 2;
			}
			break;
		case 2:
			if (this.totalSecondsElapsed >= 0.1f) {
				this.stScrapPartsText = new ShadowedText(32f, 48f, ResourceManager.fFontMain, "+" + this.scrapPartsAmount,
						EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager());
				this.stScrapPartsText.setColor(ColorConstants.SCRAP_PARTS);
				this.stScrapPartsText.setText("+0");
				this.stScrapPartsText.setScale(2f);
				this.stScrapPartsText.setRotation(3.5f);
				this.attachChild(this.stScrapPartsText);
				EntityUtils.animateEntity(this.stScrapPartsText, 0.1f, EntityUtils.ANIMATION_SCALE_OUT_JAGGED);

				this.fvmScrapPartsTextMod = new FloatValueModifier(0f, 1f, EaseLinear.getInstance(), 0.25f) {
					@Override
					protected void onFinished() {
						super.onFinished();
						ScrapPartsSplash.this.unregisterUpdateHandler(ScrapPartsSplash.this.fvmScrapPartsTextMod);
					}

					@Override
					protected void onValueChanged(float value) {
						super.onValueChanged(value);
						ScrapPartsSplash.this.stScrapPartsText.setText("+" + (int) (ScrapPartsSplash.this.scrapPartsAmount * value));
					};
				};
				this.registerUpdateHandler(this.fvmScrapPartsTextMod);
				this.totalSecondsElapsed = 0f;
				this.state = 3;
			}
			break;
		case 3:
			this.onAnimationFinished();
			this.state = 4;
			break;
		}

		if (this.state < 3)
			this.totalSecondsElapsed += pSecondsElapsed;
	}
}
