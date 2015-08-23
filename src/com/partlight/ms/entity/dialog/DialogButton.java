package com.partlight.ms.entity.dialog;

import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.partlight.ms.entity.ShadowedText;
import com.partlight.ms.resource.ResourceManager;
import com.partlight.ms.resource.ResourceManager.DialogRegions;

public class DialogButton extends Sprite {

	private final ShadowedText stText;

	public DialogButton(float x, float y, String text, VertexBufferObjectManager vbom) {
		super(x, y, DialogRegions.region_dialog_btn, vbom);

		this.stText = new ShadowedText(0, 0, ResourceManager.fFontMain, text, vbom);
		this.stText.setX((this.getWidth() - this.stText.getWidth()) / 2f);
		this.stText.setY((this.getHeight() - this.stText.getHeight()) / 2f);

		this.attachChild(this.stText);
	}

	@Override
	public void setAlpha(float pAlpha) {
		super.setAlpha(pAlpha);
		this.stText.setAlpha(pAlpha);
	}
}
