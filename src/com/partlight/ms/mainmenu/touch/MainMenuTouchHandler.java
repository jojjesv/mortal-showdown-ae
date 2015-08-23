package com.partlight.ms.mainmenu.touch;

import org.andengine.entity.Entity;
import org.andengine.entity.scene.Scene;
import org.andengine.input.touch.TouchEvent;

import com.partlight.ms.entity.touch.scroll.ScrollContainer;
import com.partlight.ms.entity.touch.swipe.ModifierSwipeHandler;
import com.partlight.ms.mainmenu.hud.Selector;
import com.partlight.ms.resource.EnvironmentVars;
import com.partlight.ms.scene.mainmenu.MainMenuMetaObjects;
import com.partlight.ms.scene.mainmenu.MainMenuScene;
import com.partlight.ms.session.hud.BaseScreenComponentTouchManager;
import com.partlight.ms.util.Fade;

public class MainMenuTouchHandler extends ModifierSwipeHandler {

	private final MainMenuScene mmsContext;

	public MainMenuTouchHandler(MainMenuScene context) {
		super(context, 128f, SwipeDirections.LEFT, SwipeDirections.RIGHT, SwipeDirections.DOWN, SwipeDirections.UP);
		this.mmsContext = context;
		// this.setUseAspectRatioVertically(true);
	}

	protected boolean allowTouch() {
		return true;
	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {

		if (!this.mmsContext.hasUsedStartTransition()) {
			if (pSceneTouchEvent.isActionUp()) {
				this.mmsContext.useStartTransition();
				return true;
			}
			return false;
		}

		if (!this.allowTouch())
			return false;

		if (this.mmsContext.getCurrentSection() == MainMenuScene.SECTION_MAIN)
			if (pSceneTouchEvent.isActionUp() && !this.isSwiping()) {
				this.mmsContext.preToSession();
				return true;
			}

		if (this.mmsContext.getCurrentSection() == MainMenuScene.SECTION_OPTIONS) {

			final ScrollContainer OPTIONS_SCROLL_CONTAINER = this.mmsContext.getSectionScrollContainer(MainMenuScene.SECTION_OPTIONS);

			final boolean b = OPTIONS_SCROLL_CONTAINER.onTouchEvent(pSceneTouchEvent);

			if (!b) {
				((BaseScreenComponentTouchManager) this.mmsContext.getMetaObject(MainMenuMetaObjects.META_TM_OPTIONS))
						.onSceneTouchEvent(pScene, pSceneTouchEvent);
				if ((int) OPTIONS_SCROLL_CONTAINER.getY() >= (int) OPTIONS_SCROLL_CONTAINER.getMaxY())
					return super.onSceneTouchEvent(pScene, pSceneTouchEvent);
			}

		} else if (this.mmsContext.getCurrentSection() == MainMenuScene.SECTION_STORE) {

			final ScrollContainer STORE_SCROLL_CONTAINER = this.mmsContext.getSectionScrollContainer(MainMenuScene.SECTION_STORE);
			final Selector WEP_SELECTOR = ((Selector) this.mmsContext.getMetaObject(MainMenuMetaObjects.META_WS));
			final Fade WEP_SELECTOR_FADE = WEP_SELECTOR.getFade();

			if (WEP_SELECTOR_FADE.isShowing() && !WEP_SELECTOR_FADE.isAnimating())
				return WEP_SELECTOR.onSceneTouchEvent(pScene, pSceneTouchEvent);

			if (WEP_SELECTOR_FADE.isAnimating())
				return false;

			final boolean SCROLL_CONTAINER_TOUCH_EVENT = STORE_SCROLL_CONTAINER.onTouchEvent(pSceneTouchEvent);

			if (!SCROLL_CONTAINER_TOUCH_EVENT) {
				if ((int) (super.getTransformationPercent() * 1000) == 0)
					((BaseScreenComponentTouchManager) this.mmsContext.getMetaObject(MainMenuMetaObjects.META_TM_STORE))
							.onSceneTouchEvent(pScene, pSceneTouchEvent);
				if (STORE_SCROLL_CONTAINER.isAtMax())
					return super.onSceneTouchEvent(pScene, pSceneTouchEvent);
			}

			this.release();

			return SCROLL_CONTAINER_TOUCH_EVENT;
		}

		return super.onSceneTouchEvent(pScene, pSceneTouchEvent);
	}

	@Override
	public void onSwipe(SwipeDirections direction) {
		this.release();

		//@formatter:off
		if (this.mmsContext.getCurrentSection() == MainMenuScene.SECTION_MAIN)
			switch (direction) {
				case LEFT:
					this.mmsContext.showAchievements();
					break;
				case RIGHT:
					this.mmsContext.showLeaderboard();
					break;
				case DOWN:
					this.mmsContext.toOptions();
					break;
				case UP:
					this.mmsContext.toStore();
					break;
				case NONE:
					break;
			}
		else if (this.mmsContext.getCurrentSection() == MainMenuScene.SECTION_OPTIONS)
			switch (direction) {
				case UP:
					this.mmsContext.toMain();
					break;
				default:
					break;
			}
		else if (this.mmsContext.getCurrentSection() == MainMenuScene.SECTION_STORE)
		 switch (direction) {
				case DOWN:
					this.mmsContext.toMain();
					break;
				default:
					break;
			}
	}

	@Override
	public void onTransformationPercentChanged(float percent) {
		percent = (1f - Math.abs(percent)) * 0.25f + 0.75f;
		if (percent < 0.75f)
			percent = 0.75f;

		final Entity BASE_CONTAINER = this.mmsContext.getSectionContainer(MainMenuScene.SECTION_MAIN);

		if (this.isSwipingHorizontally())
			BASE_CONTAINER.setScale(percent);
		else {
			if ((int) (BASE_CONTAINER.getScaleX() * 1000) < 1000)
				BASE_CONTAINER.setScale(1f);

			float delta = 0f;

			switch (this.mmsContext.getCurrentSection()) {

			case MainMenuScene.SECTION_MAIN:
				delta = 0f;
				break;

			case MainMenuScene.SECTION_OPTIONS:
				delta = EnvironmentVars.MAIN_CONTEXT.height();
				break;

			case MainMenuScene.SECTION_STORE:
				delta = -EnvironmentVars.MAIN_CONTEXT.height();
				break;
			}

			if (this.getTransformationPercent() > 0f)
				BASE_CONTAINER.setY(delta + 128f * (1f - percent));
			else
				BASE_CONTAINER.setY(delta + 128f * (-1f + percent));
		}

	}
}
