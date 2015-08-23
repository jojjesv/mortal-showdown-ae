package com.partlight.ms.util.list;

import java.util.ArrayList;

import org.andengine.opengl.texture.ITexture;

@SuppressWarnings("serial")
public class TextureManagedArrayList<E> extends ArrayList<E> {

	private final ITexture[] tTextures;

	public TextureManagedArrayList(ITexture... textures) {
		this.tTextures = textures;
	}

	@Override
	public boolean add(E object) {
		if (this.size() == 0)
			for (final ITexture tex : this.tTextures)
				tex.load();

		return super.add(object);
	}

	@Override
	public boolean remove(Object object) {
		if (this.size() == 1)
			for (final ITexture tex : this.tTextures)
				tex.unload();

		return super.remove(object);
	}
}
