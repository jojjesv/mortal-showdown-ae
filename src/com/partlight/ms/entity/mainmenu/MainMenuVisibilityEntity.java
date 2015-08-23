package com.partlight.ms.entity.mainmenu;

public abstract class MainMenuVisibilityEntity implements MainMenuEntity {

	private boolean isVisible;

	public boolean isVisible() {
		return this.isVisible;
	}

	public void setVisible(boolean visible) {
		this.isVisible = visible;
	}
}
