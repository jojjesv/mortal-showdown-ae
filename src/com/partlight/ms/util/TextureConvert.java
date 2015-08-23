package com.partlight.ms.util;

import org.andengine.opengl.texture.TextureManager;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;

public class TextureConvert {

	public static TextureRegion bitmapToTexture(Bitmap src, TextureManager textureManager) {

		TextureRegion out = null;

		final Bitmap SRC = src;

		final IBitmapTextureAtlasSource source = new IBitmapTextureAtlasSource() {
			@Override
			public IBitmapTextureAtlasSource deepCopy() {
				return null;
			}

			@Override
			public int getTextureHeight() {
				return 0;
			}

			@Override
			public int getTextureWidth() {
				return 0;
			}

			@Override
			public int getTextureX() {
				return 0;
			}

			@Override
			public int getTextureY() {
				return 0;
			}

			@Override
			public Bitmap onLoadBitmap(Config pBitmapConfig) {
				return SRC;
			}

			@Override
			public void setTextureHeight(int pTextureHeight) {
			}

			@Override
			public void setTextureWidth(int pTextureWidth) {
			}

			@Override
			public void setTextureX(int pTextureX) {
			}

			@Override
			public void setTextureY(int pTextureY) {
			}
		};

		final BitmapTextureAtlas atlas = new BitmapTextureAtlas(textureManager, src.getWidth(), src.getHeight());
		atlas.addTextureAtlasSource(source, 0, 0);

		out = TextureRegionFactory.extractFromTexture(atlas);

		return out;
	}
}
