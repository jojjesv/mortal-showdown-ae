package com.partlight.ms.scene;

import org.andengine.entity.IEntity;

import com.partlight.ms.entity.EntitySorter;

public abstract class DialogLevelScene extends DialogScene implements LevelScene {
	private EntitySorter eysEntitySorter;

	public EntitySorter getEntityYSorter() {
		if (this.eysEntitySorter == null)
			this.eysEntitySorter = new EntitySorter(this);
		return this.eysEntitySorter;
	}

	public void setEntityMaxZIndex(IEntity entity) {
		entity.setZIndex(this.getLevel().getMapHeight());
	}
}
