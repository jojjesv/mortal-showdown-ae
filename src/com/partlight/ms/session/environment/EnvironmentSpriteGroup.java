package com.partlight.ms.session.environment;

import java.util.ArrayList;

import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.sprite.batch.SpriteGroup;
import org.andengine.opengl.texture.ITexture;

import com.partlight.ms.resource.EnvironmentVars;

public class EnvironmentSpriteGroup implements IUpdateHandler {

	private final SpriteGroup					sgItems;
	private boolean								updateDrawing;
	private ArrayList<EnvironmentObject>		alEnvObjList;
	private final ArrayList<EnvironmentObject>	OBJS_TO_REMOVE;
	private boolean								centerDrawing;
	private final int							itemCapacity;

	public boolean debug;

	public EnvironmentSpriteGroup(ITexture pTexture, int pCapacity) {
		this.sgItems = new SpriteGroup(pTexture, pCapacity, EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager());
		this.itemCapacity = pCapacity;
		this.OBJS_TO_REMOVE = new ArrayList<>();
		this.centerDrawing = true;
	}

	public ArrayList<EnvironmentObject> getList() {
		return this.alEnvObjList;
	}

	public SpriteGroup getSpriteGroup() {
		return this.sgItems;
	}

	public boolean isCenteringDrawing() {
		return this.centerDrawing;
	}

	protected void onDrawObject(EnvironmentObject object) {
		if (object.alpha > 0)
			//@formatter:off
			this.sgItems.draw(object.textureRegion,
					object.location.x - ((this.centerDrawing) ? (object.textureRegion.getWidth()) : 0f),
					object.location.y - ((this.centerDrawing) ? (object.textureRegion.getHeight()) : 0f),
					object.textureRegion.getWidth() * 2f,
					object.textureRegion.getHeight() * 2f, 0f, 1f, 1f,
					1f, (float)object.alpha);
			//@formatter:on
	}

	@Override
	public void onUpdate(float pSecondsElapsed) {

		while (this.alEnvObjList.size() > this.itemCapacity)
			this.alEnvObjList.remove(0);

		for (int i = 0; i < this.alEnvObjList.size(); i++)
			this.alEnvObjList.get(i).onUpdate(this, pSecondsElapsed);

		if (this.OBJS_TO_REMOVE.size() > 0) {

			for (int i = 0; i < this.OBJS_TO_REMOVE.size(); i++)
				this.alEnvObjList.remove(this.OBJS_TO_REMOVE.get(i));

			this.OBJS_TO_REMOVE.clear();
		}

		if (this.updateDrawing) {

			for (int i = 0; i < this.alEnvObjList.size(); i++)
				this.onDrawObject(this.alEnvObjList.get(i));

			this.sgItems.submit();

			this.updateDrawing = false;
		}
	}

	void removeEnvironmentObject(EnvironmentObject object) {
		this.OBJS_TO_REMOVE.add(object);
	}

	@Override
	public void reset() {

	}

	public void setCenterDrawing(boolean centerDrawing) {
		this.centerDrawing = centerDrawing;
	}

	@SuppressWarnings("unchecked")
	public void setList(ArrayList<? extends EnvironmentObject> list) {
		this.alEnvObjList = (ArrayList<EnvironmentObject>) list;
	}

	public void updateDrawing() {
		this.updateDrawing = true;
	}
}
