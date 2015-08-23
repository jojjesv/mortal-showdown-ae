package com.partlight.ms.entity.mainmenu.button;

import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.entity.text.exception.OutOfCharactersException;
import org.andengine.opengl.shader.ShaderProgram;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;

import com.partlight.ms.entity.ShadowedText;
import com.partlight.ms.resource.EnvironmentVars;
import com.partlight.ms.resource.ResourceManager;
import com.partlight.ms.session.hud.BaseScreenComponent;
import com.partlight.ms.util.EntityUtils;

public class Button extends BaseScreenComponent {

	public static final float	INACTIVE_ALPHA		= 0.5f;
	private static final float	TEXT_PADDING		= 16f;
	public static final float	BUTTON_HEIGHT		= 112f;
	public static final int		STYLE_ICON_CENTER	= 0;
	public static final int		STYLE_ICON_LEFT		= 1;
	private ShadowedText		stDescription;
	private final ShadowedText	stTitle;
	private final TiledSprite	tsIcon;
	private final int			style;
	private float				virtualIconWidth;
	private boolean				emptyDescription;
	private boolean				enabled;

	public Button(float x, float y, ITextureRegion background, ITiledTextureRegion icon, String text) {
		this(x, y, background, icon, text, Button.STYLE_ICON_CENTER);
	}

	public Button(float x, float y, ITextureRegion background, ITiledTextureRegion icon, String text, int style) {
		this(x, y, background, icon, text, style, (icon == null) ? 0f : icon.getWidth());
	}

	public Button(float x, float y, ITextureRegion background, ITiledTextureRegion icon, String text, int style, float iconWidth) {
		super(x, y, background);

		final Sprite BACKGROUND = (Sprite) this.getBackground();
		{
			BACKGROUND.setScaleCenter(0, 0);
			BACKGROUND.setScale(2);
		}

		this.stTitle = new ShadowedText(0, 0, ResourceManager.fFontMain, text, EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager());
		{
			this.stTitle.setScaleCenter(0, 0);
			this.stTitle.setScale(2);
			this.attachChild(this.stTitle);
		}
		if (icon != null) {
			this.tsIcon = new TiledSprite(0, 0, icon, EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager());
			{
				this.tsIcon.setScaleCenter(0, 0);
				this.tsIcon.setScale(2);
				this.tsIcon.setRotationCenter(this.tsIcon.getWidthScaled() / 2f, this.tsIcon.getHeightScaled() / 2f);
				this.attachChild(this.tsIcon);
			}
		} else
			this.tsIcon = null;

		this.setEnabled(true);
		this.style = style;
		this.virtualIconWidth = iconWidth;

		this.updateAlignment();
	}

	protected void createDescription(String description) {
		this.stDescription = new ShadowedText(0, 0, ResourceManager.fFontMain, description,
				EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager()) {
			@Override
			public void setText(CharSequence pText) throws OutOfCharactersException {
				super.setText(pText);
			}
		};
		this.attachChild(this.stDescription);
	}

	@Override
	public void dispose() {
		super.dispose();
		if (!this.tsIcon.isDisposed())
			this.tsIcon.dispose();
		if (!this.stTitle.isDisposed())
			this.stTitle.dispose();
	}

	@Override
	public float getBoundaryHeight() {
		return Button.BUTTON_HEIGHT;
	}

	public int getCurrentTileIndex() {
		return this.tsIcon.getCurrentTileIndex();
	}

	public CharSequence getDescription() {
		return this.stDescription.getText();
	}

	public TiledSprite getIcon() {
		return this.tsIcon;
	}

	public CharSequence getTitle() {
		return this.stTitle.getText();
	}

	public float getVirtualIconWidth() {
		return this.virtualIconWidth;
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	@Override
	protected void onPress(float x, float y) {
		if (this.enabled)
			super.onPress(x, y);
	}

	@Override
	public void setAlpha(float pAlpha) {
		if (!this.enabled)
			pAlpha *= Button.INACTIVE_ALPHA;
		super.setAlpha(pAlpha);
	}

	public void setCurrentTileIndex(int pCurrentTileIndex) {
		if (this.tsIcon != null)
			this.tsIcon.setCurrentTileIndex(pCurrentTileIndex);
	}

	public void setDescription(String text) {
		this.emptyDescription = text.contentEquals("");

		if (!this.emptyDescription) {
			if (this.stDescription != null && text.length() > this.stDescription.getText().length()) {
				this.stDescription.detachSelf();
				this.stDescription.dispose();
				this.stDescription = null;
			}
			if (this.stDescription == null)
				this.createDescription(text);
			this.stDescription.setText(text);
		}
		this.updateAlignment();
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;

		if (enabled)
			this.stTitle.hideShadow();
		else
			this.stTitle.showShadow();
	}

	@Override
	public void setRotation(float pRotation) {
		if (this.tsIcon != null)
			this.tsIcon.setRotation(pRotation);
	}

	public void setShaderProgram(ShaderProgram pShaderProgram) {
		if (this.tsIcon != null)
			this.tsIcon.setShaderProgram(pShaderProgram);

		this.stTitle.setShaderProgram(pShaderProgram);

		if (this.stDescription != null)
			this.stDescription.setShaderProgram(pShaderProgram);
	}

	public void setTitle(String text) {
		this.setTitle(text, true);
	}

	public void setTitle(String text, boolean reposition) {
		this.stTitle.setText(text);
		if (reposition)
			this.updateAlignment();
	}

	public void setVirtualIconWidth(float virtualWidth) {
		this.virtualIconWidth = virtualWidth;
		this.updateAlignment();
	}

	protected void updateAlignment() {

		if (this.style == Button.STYLE_ICON_CENTER) {

			if (this.tsIcon == null)
				this.stTitle.setX((this.getWidthScaled() - this.stTitle.getWidthScaled()) / 2f);
			else {
				final float WIDTH = this.tsIcon.getWidthScaled() + Button.TEXT_PADDING + this.stTitle.getWidthScaled();

				// Position in center
				this.tsIcon.setX((this.getWidthScaled() - WIDTH) / 2f);

				float titleX = this.tsIcon.getX() + this.tsIcon.getWidthScaled() + Button.TEXT_PADDING;
				titleX += EntityUtils.getXDelta(this.stTitle);
				this.stTitle.setX(titleX);
			}

		} else if (this.style == Button.STYLE_ICON_LEFT) {
			this.stTitle.setX((this.getWidthScaled() - this.stTitle.getWidthScaled()) / 2f);

			if (this.tsIcon != null) {
				final float PADDING = 4f;

				this.tsIcon.setX(4f - (this.tsIcon.getWidth() - this.virtualIconWidth));

				if (this.stTitle.getX() < this.tsIcon.getX() + (this.virtualIconWidth) + PADDING)
					this.stTitle.setX(this.tsIcon.getX() + (this.virtualIconWidth) + PADDING);
			}
		}

		float titleY = (this.getHeightScaled() - this.stTitle.getHeightScaled()) / 2f;
		final float titleYDelta = EntityUtils.getYDelta(this.stTitle);
		titleY += titleYDelta;

		titleY -= 2f;

		if (this.stDescription != null && !this.emptyDescription) {
			titleY -= this.stTitle.getHeightScaled() / 2f;
			titleY += titleYDelta;
			this.stDescription.setY(this.getHeightScaled() / 2f);
			this.stDescription.setX((this.getWidthScaled() - this.stDescription.getWidthScaled()) / 2f);
		}

		this.stTitle.setY(titleY);
	}
}
