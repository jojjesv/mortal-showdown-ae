package com.partlight.ms.entity.mainmenu.button;

import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;

import com.partlight.ms.resource.EnvironmentVars;
import com.partlight.ms.resource.ResourceManager.HudRegions;

public class ToggleButton extends Button {

	private final AnimatedSprite	asToggle;
	private boolean					checked;

	public ToggleButton(float x, float y, ITextureRegion background, ITiledTextureRegion icon, String text) {
		super(x, y, background, icon, text);
		this.asToggle = new AnimatedSprite(0, 0, HudRegions.region_options_toggle,
				EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager());
		this.getIcon().attachChild(this.asToggle);
		this.setChecked(false, false);
	}

	@Override
	public void dispose() {
		if (!this.asToggle.isDisposed())
			this.asToggle.dispose();
		super.dispose();
	}

	public boolean isChecked() {
		return this.checked;
	}

	@Override
	public void onAttached() {
		super.onAttached();
	}

	@Override
	public void performClick() {
		super.performClick();
		this.setChecked(!this.checked, true);
	}

	public void setChecked(boolean checked) {
		this.setChecked(checked, true);
	}

	public void setChecked(boolean checked, boolean animate) {

		final int[] FRAMES = new int[5];

		if (!this.checked) {
			FRAMES[0] = 4;
			FRAMES[1] = 3;
			FRAMES[2] = 2;
			FRAMES[3] = 1;
			FRAMES[4] = 0;
		} else {
			FRAMES[0] = 0;
			FRAMES[1] = 1;
			FRAMES[2] = 2;
			FRAMES[3] = 3;
			FRAMES[4] = 4;
		}

		if (animate)
			this.asToggle.animate(new long[] {
					50,
					50,
					25,
					25,
					50
			}, FRAMES, false);
		else if (!checked)
			this.asToggle.setCurrentTileIndex(0);
		else
			this.asToggle.setCurrentTileIndex(4);

		this.checked = checked;
	}
}
