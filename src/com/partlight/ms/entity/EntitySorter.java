package com.partlight.ms.entity;

import java.util.ArrayList;
import java.util.List;

import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.IEntity;

/**
 * @author Johan Svensson - partLight Entertainment
 *
 */
public class EntitySorter implements IUpdateHandler {

	private boolean					doUpdate;
	private final List<Sortable>	entries;
	private IEntity					eContext;

	public EntitySorter(IEntity context) {
		this.entries = new ArrayList<>();
		this.setContext(context);
	}

	public void add(Sortable sortable) {
		this.entries.add(sortable);
	}

	public IEntity getContext() {
		return this.eContext;
	}

	@Override
	public void onUpdate(float pSecondsElapsed) {
		if (this.doUpdate) {
			Sortable index;
			for (int i = 0; i < this.entries.size(); i++) {
				index = this.entries.get(i);
				index.setZIndex((int) (index.getSortingY()));
			}

			this.eContext.sortChildren();

			this.doUpdate = false;
		}
	}

	public void remove(Sortable sortable) {
		this.entries.remove(sortable);
	}

	public void requestUpdate() {
		this.doUpdate = true;
	}

	@Override
	public void reset() {

	}

	public void setContext(IEntity context) {
		this.eContext = context;
	}
}
