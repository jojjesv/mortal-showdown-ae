package com.partlight.ms.session.hud.listener;

import com.partlight.ms.session.hud.BaseScreenComponent;

public interface ComponentListener {
	public void onComponentLongPress(BaseScreenComponent component);

	public void onComponentMoved(BaseScreenComponent component, float x, float y);

	public void onComponentPressed(BaseScreenComponent component, float x, float y);

	public void onComponentReleased(BaseScreenComponent component, float x, float y);

	public void onComponentTouchStateReset(BaseScreenComponent component);
}
