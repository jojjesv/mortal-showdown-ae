package com.partlight.ms.entity;

import org.andengine.entity.Entity;

public class AlphaFriendlyEntity extends Entity {

	public AlphaFriendlyEntity() {
	}

	public AlphaFriendlyEntity(float x, float y) {
		super(x, y);
	}

	@Override
	public void setAlpha(float pAlpha) {
		super.setAlpha(pAlpha);
		this.setChildrenAlpha(pAlpha);
	}

	public void setChildrenAlpha(float alpha) {
		for (int i = 0; i < this.getChildCount(); i++)
			this.getChildByIndex(i).setAlpha(alpha);
	}

	@Override
	public void setColor(float pRed, float pGreen, float pBlue, float pAlpha) {
		super.setColor(pRed, pGreen, pBlue, pAlpha);
		this.setChildrenAlpha(pAlpha);
	}
}
