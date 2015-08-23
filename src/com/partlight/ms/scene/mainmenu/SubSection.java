package com.partlight.ms.scene.mainmenu;

import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.VerticalAlign;

import com.partlight.ms.entity.mainmenu.button.Button;
import com.partlight.ms.resource.EnvironmentVars;
import com.partlight.ms.scene.mainmenu.container.Container;
import com.partlight.ms.session.hud.BaseScreenComponentTouchManager;
import com.partlight.ms.util.EntityUtils;
import com.partlight.ms.util.listener.OnBackPressedListener;

public abstract class SubSection implements IOnSceneTouchListener, OnBackPressedListener {

	protected Container							afeContainer;
	protected Button							bBack;
	protected MainMenuScene						mmsContext;
	protected BaseScreenComponentTouchManager	tmButtonTouchManager;
	private ITextureRegion						trBackButtonBackground;

	public SubSection(MainMenuScene context) {
		this.mmsContext = context;
	}

	public Container getContainer() {
		return this.afeContainer;
	}

	protected void goBack() {
		this.mmsContext.obplTransitionToBackPressedListener = this.mmsContext;
		this.mmsContext.cTransitionToContainer = this.mmsContext.getSectionContainer(this.mmsContext.getCurrentSection());
		this.mmsContext.ostlTransitionToTouchListener = this.mmsContext.mmthTouchHandler;
		this.mmsContext.transitionFromContainer(this.afeContainer, true);
	}

	public boolean initContainer() {
		if (this.afeContainer != null)
			return false;

		this.afeContainer = new Container() {
			@Override
			public void onAttached() {
				super.onAttached();
				SubSection.this.onContainerAttached();
			}

			@Override
			public void onDetached() {
				super.onDetached();
				SubSection.this.onContainerDetached();
			}
		};
		this.afeContainer.setScaleCenter(EnvironmentVars.MAIN_CONTEXT.width() / 2f, EnvironmentVars.MAIN_CONTEXT.height() / 2f);

		this.tmButtonTouchManager = new BaseScreenComponentTouchManager();

		if (this.shouldCreateBackButton(null)) {
			this.bBack = new Button(0, 0, this.trBackButtonBackground, null, "BACK") {
				@Override
				public void performClick() {
					SubSection.this.goBack();
				}
			};

			this.afeContainer.attachChild(this.bBack);

			EntityUtils.alignEntity(this.bBack, this.bBack.getWidth(), this.bBack.getHeight(), HorizontalAlign.RIGHT, VerticalAlign.TOP, 32,
					32);

			this.tmButtonTouchManager.setComponents(this.bBack);
		}

		this.postInitialized();

		return true;
	}

	@Override
	public void onBackPressed() {
		if (this.bBack != null)
			this.bBack.performClick();
	}

	protected void onContainerAttached() {

	}

	protected void onContainerDetached() {
	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		return this.tmButtonTouchManager.onSceneTouchEvent(pScene, pSceneTouchEvent);
	}

	protected void postInitialized() {

	}

	protected boolean shouldCreateBackButton(ITextureRegion buttonBackground) {
		this.trBackButtonBackground = buttonBackground;
		return buttonBackground != null;
	}
}
