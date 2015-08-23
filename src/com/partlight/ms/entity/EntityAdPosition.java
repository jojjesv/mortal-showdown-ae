package com.partlight.ms.entity;

import org.andengine.entity.Entity;
import static com.partlight.ms.resource.EnvironmentVars.MAIN_CONTEXT;

public class EntityAdPosition implements Runnable {

	private Entity[] eEntities;

	public EntityAdPosition(Entity... entity) {
		this.eEntities = entity;
	}

	@Override
	public void run() {
		if (MAIN_CONTEXT.isUsingAd())
			for (Entity e : this.eEntities)
				if (e != null)
					e.setY(e.getY() - MAIN_CONTEXT.getAdHeight());
	}
}
