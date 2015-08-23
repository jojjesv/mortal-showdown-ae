package com.partlight.ms.session.level;

import org.andengine.extension.tmx.TMXLayer;
import org.andengine.extension.tmx.TMXTiledMap;

public class Level {

	private final TMXTiledMap	tmxMap;
	private final TMXLayer		layerMain;

	public Level(TMXTiledMap map) {
		this.tmxMap = map;
		this.layerMain = map.getTMXLayers().get(0);
	}

	public TMXLayer getMainLayer() {
		return this.layerMain;
	}

	public TMXTiledMap getMap() {
		return this.tmxMap;
	}

	public int getMapHeight() {
		return (int) (this.getMainLayer().getHeight() * this.getMainLayer().getScaleY());
	}

	public int getMapWidth() {
		return (int) (this.getMainLayer().getWidth() * this.getMainLayer().getScaleX());
	}
}
