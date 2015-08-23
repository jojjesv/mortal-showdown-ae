package com.partlight.ms.entity.mainmenu;

import org.andengine.util.HorizontalAlign;

import com.partlight.ms.entity.ShadowedText;
import com.partlight.ms.resource.EnvironmentVars;
import com.partlight.ms.resource.ResourceManager;

public class ComponentFactory {

	@SuppressWarnings("incomplete-switch")
	public static ShadowedText createLabel(String text, float x, float y, float scale, HorizontalAlign alignment) {
		final ShadowedText OUT = new ShadowedText(x, y, ResourceManager.fFontMain, text,
				EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager());
		OUT.setScale(scale);

		switch (alignment) {
		case CENTER:
			OUT.setX(OUT.getX() - OUT.getWidth() / 2f);
			break;
		case RIGHT:
			OUT.setX(OUT.getX() - OUT.getWidth());
			break;
		}

		OUT.setHorizontalAlign(alignment);

		return OUT;
	}
}
