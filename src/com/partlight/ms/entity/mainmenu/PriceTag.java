package com.partlight.ms.entity.mainmenu;

import org.andengine.entity.shape.RectangularShape;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.shader.PositionTextureCoordinatesUniformColorShaderProgram;
import org.andengine.opengl.vbo.IVertexBufferObject;

import com.partlight.ms.entity.ShadowedText;
import com.partlight.ms.resource.EnvironmentVars;
import com.partlight.ms.resource.ResourceManager;
import com.partlight.ms.resource.ResourceManager.StoreTextureRegions;
import com.partlight.ms.util.ColorConstants;

public class PriceTag extends RectangularShape {

	private final Sprite		sIcon;
	private final ShadowedText	sPrice;

	public PriceTag(float x, float y, int price) {
		super(x, y, 1, 1, PositionTextureCoordinatesUniformColorShaderProgram.getInstance());
		this.sIcon = new Sprite(0, 0, StoreTextureRegions.region_scrap_parts_icon,
				EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager());
		this.sPrice = new ShadowedText(this.sIcon.getWidth(), 0, ResourceManager.fFontMain, String.valueOf(price),
				EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager());

		this.sPrice.setColor(ColorConstants.SCRAP_PARTS);

		this.attachChild(this.sIcon);
		this.attachChild(this.sPrice);

		this.setSize(this.sIcon.getWidth() + this.sPrice.getWidth(), this.sIcon.getHeight());
	}

	@Override
	public IVertexBufferObject getVertexBufferObject() {
		return this.sIcon.getVertexBufferObject();
	}

	@Override
	protected void onUpdateVertices() {
	}

	@Override
	public void setAlpha(float pAlpha) {
		super.setAlpha(pAlpha);
		this.sIcon.setAlpha(pAlpha);
		this.sPrice.setAlpha(pAlpha);
	}
}
