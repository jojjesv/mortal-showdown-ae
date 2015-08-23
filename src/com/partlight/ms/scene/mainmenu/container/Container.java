package com.partlight.ms.scene.mainmenu.container;

import com.partlight.ms.entity.AlphaFriendlyEntity;
import com.partlight.ms.resource.EnvironmentVars;

public class Container extends AlphaFriendlyEntity {

	public Container() {
		this.setScaleCenter(EnvironmentVars.MAIN_CONTEXT.width() / 2f, EnvironmentVars.MAIN_CONTEXT.height() / 2f);
	}
}
