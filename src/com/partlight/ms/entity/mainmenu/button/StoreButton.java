package com.partlight.ms.entity.mainmenu.button;

import org.andengine.opengl.shader.ShaderProgram;
import org.andengine.opengl.texture.region.ITiledTextureRegion;

import com.partlight.ms.entity.mainmenu.PriceTag;
import com.partlight.ms.resource.ResourceManager.StrokeTextureRegions;
import com.partlight.ms.scene.mainmenu.MainMenuScene;
import com.partlight.ms.util.EntityUtils;

public class StoreButton extends Button {

	public static float calculateY(int buttonIndex) {
		return Button.BUTTON_HEIGHT * buttonIndex;
	}

	private final int		itemPrice;
	private final int		itemId;
	private final PriceTag	ptPriceTag;

	public StoreButton(float x, float y, int itemId, ITiledTextureRegion icon, String text, MainMenuScene context, int price) {
		super(x, y, StrokeTextureRegions.region_stroke_1, icon, text, Button.STYLE_ICON_LEFT);
		super.setVirtualIconWidth(48);

		this.setContext(context);

		this.itemId = itemId;
		this.itemPrice = price;

		this.ptPriceTag = new PriceTag(0, 0, price);
		this.ptPriceTag.setScale(2);
		this.ptPriceTag.setX(this.getBoundaryWidth() - this.ptPriceTag.getWidthScaled());
		this.attachChild(this.ptPriceTag);
	}

	public int getPrice() {
		return this.itemPrice;
	}

	@Override
	public void onDetached() {
		super.onDetached();
	}

	@Override
	public void performClick() {
		((MainMenuScene) this.getContext()).onStoreItemClicked(this.itemId);
	}

	public void removePriceTag() {
		EntityUtils.safetlyDetachAndDispose(this.ptPriceTag);
	}

	@Override
	public void setAlpha(float pAlpha) {
		super.setAlpha(pAlpha);
		this.ptPriceTag.setAlpha(pAlpha);
	}

	@Override
	public void setShaderProgram(ShaderProgram pShaderProgram) {
		super.setShaderProgram(pShaderProgram);
		this.ptPriceTag.setShaderProgram(pShaderProgram);
	}
}
