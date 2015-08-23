package com.partlight.ms.entity;

import org.andengine.entity.sprite.batch.DynamicSpriteBatch;
import org.andengine.opengl.font.Letter;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.util.color.Color;
import org.andengine.util.modifier.ease.EaseLinear;
import org.andengine.util.modifier.ease.IEaseFunction;

import com.partlight.ms.resource.EnvironmentVars;
import com.partlight.ms.resource.ResourceManager;

@Deprecated
public class AnimatedShadowedText extends DynamicSpriteBatch {

	public static class TransitionData {
		private float	alpha;
		private float	blue;
		private float	green;
		private float	red;
		private float	rotation;
		private float	totalSecondsElapsed;
		private float	xScale;
		private float	yScale;

		public Color			colorFrom		= Color.WHITE;
		public Color			colorTo			= Color.WHITE;
		public IEaseFunction	ease			= EaseLinear.getInstance();
		public float			alphaFrom		= 1f;
		public float			alphaTo			= 1f;
		public float			duration		= 0.5f;
		public float			rotationFrom	= 0f;
		public float			rotationTo		= 0f;
		public float			xScaleFrom		= 1f;
		public float			xScaleTo		= 1f;
		public float			yScaleFrom		= 1f;
		public float			yScaleTo		= 1f;
	}

	private final Letter[]			LETTERS;
	private final TransitionData[]	LETTER_MODIFIERS;
	private final ITextureRegion[]	LETTER_TEXTURE_REGIONS;
	private final float				animationInterval;
	private float					animationIntervalElapsed;
	private int						letterIndex;
	private boolean					hasCalledOnFinished;

	private boolean isFinished;

	public AnimatedShadowedText(float x, float y, String text, float animationInterval, TransitionData transitionData,
			boolean skipWhiteSpaces) {
		super(x, y, ResourceManager.fFontMain.getTexture(), text.length() * 2, EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager());

		this.LETTERS = new Letter[text.length()];
		this.LETTER_MODIFIERS = new TransitionData[this.LETTERS.length];
		this.LETTER_TEXTURE_REGIONS = new ITextureRegion[this.LETTERS.length];

		for (int i = 0; i < this.LETTERS.length; i++) {
			this.LETTERS[i] = ResourceManager.fFontMain.getLetter(text.charAt(i));

			if ((skipWhiteSpaces) ? this.LETTERS[i].mCharacter != ' ' : true) {

				this.LETTER_TEXTURE_REGIONS[i] = TextureRegionFactory.extractFromTexture(ResourceManager.fFontMain.getTexture(),
						this.LETTERS[i].mTextureX, this.LETTERS[i].mTextureY, this.LETTERS[i].mWidth + 1 + (int) this.LETTERS[i].mOffsetX,
						this.LETTERS[i].mHeight + (int) this.LETTERS[i].mOffsetY);

				this.LETTER_MODIFIERS[i] = new TransitionData();
				this.LETTER_MODIFIERS[i].alphaFrom = transitionData.alphaFrom;
				this.LETTER_MODIFIERS[i].alphaTo = transitionData.alphaTo;
				this.LETTER_MODIFIERS[i].colorFrom = transitionData.colorFrom;
				this.LETTER_MODIFIERS[i].colorTo = transitionData.colorTo;
				this.LETTER_MODIFIERS[i].duration = transitionData.duration;
				this.LETTER_MODIFIERS[i].ease = transitionData.ease;
				this.LETTER_MODIFIERS[i].rotationFrom = transitionData.rotationFrom;
				this.LETTER_MODIFIERS[i].rotationTo = transitionData.rotationTo;
				this.LETTER_MODIFIERS[i].xScaleFrom = transitionData.xScaleFrom;
				this.LETTER_MODIFIERS[i].xScaleTo = transitionData.xScaleTo;
				this.LETTER_MODIFIERS[i].yScaleFrom = transitionData.yScaleFrom;
				this.LETTER_MODIFIERS[i].yScaleTo = transitionData.yScaleTo;
			}
		}

		this.animationInterval = animationInterval;
	}

	protected void drawLetter(int index, float x, float y, float width, float height, float rotation, float red, float green, float blue,
			float alpha) {
		this.draw(this.LETTER_TEXTURE_REGIONS[index], x, y, width, height, rotation, red, green, blue, alpha);
	}

	public boolean isFinished() {
		return this.isFinished;
	}

	protected void onDrawLetter(int index, float x, float y, float width, float height, float rotation, float red, float green, float blue,
			float alpha) {
		this.drawLetter(index, x + 1f, y + 1f, width, height, rotation, 0f, 0f, 0f, alpha);
		this.drawLetter(index, x, y, width, height, rotation, red, green, blue, alpha);
	}

	protected void onFinished() {

	}

	@Override
	protected void onManagedUpdate(float pSecondsElapsed) {
		super.onManagedUpdate(pSecondsElapsed);

		if (this.letterIndex < this.LETTERS.length - 1) {
			this.animationIntervalElapsed += pSecondsElapsed;
			if (this.animationIntervalElapsed >= this.animationInterval) {
				do
					this.letterIndex += 1;
				while (this.LETTER_MODIFIERS[this.letterIndex] == null);

				this.animationIntervalElapsed = 0f;
			}
		}

		TransitionData transitionDataIndex;

		for (int i = 0; i < this.letterIndex + 1; i++) {

			transitionDataIndex = this.LETTER_MODIFIERS[i];

			if (transitionDataIndex == null)
				continue;

			transitionDataIndex.totalSecondsElapsed += pSecondsElapsed;
			if (transitionDataIndex.totalSecondsElapsed > transitionDataIndex.duration) {
				transitionDataIndex.totalSecondsElapsed = transitionDataIndex.duration;

				if (i == this.LETTERS.length - 1)
					if (!this.hasCalledOnFinished) {
						this.isFinished = true;
						this.onFinished();
						this.hasCalledOnFinished = true;
					}
			}

			final float INDEX_PERCENT = transitionDataIndex.ease.getPercentage(transitionDataIndex.totalSecondsElapsed,
					transitionDataIndex.duration);

			transitionDataIndex.alpha = transitionDataIndex.alphaFrom
					+ (transitionDataIndex.alphaTo - transitionDataIndex.alphaFrom) * INDEX_PERCENT;
			transitionDataIndex.red = transitionDataIndex.colorFrom.getRed()
					+ (transitionDataIndex.colorTo.getRed() - transitionDataIndex.colorFrom.getRed()) * INDEX_PERCENT;
			transitionDataIndex.green = transitionDataIndex.colorFrom.getGreen()
					+ (transitionDataIndex.colorTo.getGreen() - transitionDataIndex.colorFrom.getGreen()) * INDEX_PERCENT;
			transitionDataIndex.blue = transitionDataIndex.colorFrom.getBlue()
					+ (transitionDataIndex.colorTo.getBlue() - transitionDataIndex.colorFrom.getBlue()) * INDEX_PERCENT;
			transitionDataIndex.xScale = transitionDataIndex.xScaleFrom
					+ (transitionDataIndex.xScaleTo - transitionDataIndex.xScaleFrom) * INDEX_PERCENT;
			transitionDataIndex.yScale = transitionDataIndex.yScaleFrom
					+ (transitionDataIndex.yScaleTo - transitionDataIndex.yScaleFrom) * INDEX_PERCENT;
			transitionDataIndex.rotation = transitionDataIndex.rotationFrom
					+ (transitionDataIndex.rotationTo - transitionDataIndex.rotationFrom) * INDEX_PERCENT;
		}
	}

	@Override
	protected boolean onUpdateSpriteBatch() {

		final TransitionData LAST_MODIFIER = this.LETTER_MODIFIERS[this.LETTER_MODIFIERS.length - 1];

		if (LAST_MODIFIER.totalSecondsElapsed * 1000 >= LAST_MODIFIER.duration * 1000)
			return false;

		float letterX = 0f;

		for (int x = 0; x < this.letterIndex + 1; x++) {

			final float letterWidth = this.LETTERS[x].mWidth + this.LETTERS[x].mOffsetX;
			final float letterY = this.LETTERS[x].mOffsetY;

			if (this.LETTERS[x].mCharacter != ' ') {
				final float letterHeight = this.LETTER_TEXTURE_REGIONS[x].getHeight();

				//@formatter:off
				this.onDrawLetter(x,
						letterX - (letterWidth * (this.LETTER_MODIFIERS[x].xScale - 1f) / 2f),
						letterY - (letterHeight * (this.LETTER_MODIFIERS[x].yScale - 1f) / 2f),
						letterWidth * this.LETTER_MODIFIERS[x].xScale,
						letterHeight * this.LETTER_MODIFIERS[x].yScale,
						this.LETTER_MODIFIERS[x].rotation,
						this.LETTER_MODIFIERS[x].red,
						this.LETTER_MODIFIERS[x].green,
						this.LETTER_MODIFIERS[x].blue,
						this.LETTER_MODIFIERS[x].alpha);
				//@formatter:on
			}

			if (x > 0)
				letterX += this.LETTERS[x - 1].getKerning(this.LETTERS[x].mCharacter);

			if (x == this.LETTERS.length - 1)
				letterX += this.LETTERS[x].mOffsetX + this.LETTERS[x].mWidth;
			else
				letterX += this.LETTERS[x].mAdvance;
		}

		return true;
	}
}
