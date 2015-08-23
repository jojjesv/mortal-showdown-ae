package com.partlight.ms.entity.mainmenu;

import com.partlight.ms.scene.mainmenu.MainMenuScene;

public interface MainMenuEntity {
	public void attachToScene(MainMenuScene scene);

	public void detach();

	public void dispose();

	public void init();
}
