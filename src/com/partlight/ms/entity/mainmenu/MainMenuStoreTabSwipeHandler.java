package com.partlight.ms.entity.mainmenu;

import static com.partlight.ms.resource.EnvironmentVars.MAIN_CONTEXT;

import com.partlight.ms.entity.mainmenu.button.StoreButton;
import com.partlight.ms.entity.touch.scroll.ScrollContainer;
import com.partlight.ms.entity.touch.swipe.ModifierSwipeHandler;
import com.partlight.ms.resource.EnvironmentVars;
import com.partlight.ms.scene.mainmenu.MainMenuScene;
import com.partlight.ms.scene.mainmenu.MainMenuStore;

public class MainMenuStoreTabSwipeHandler extends ModifierSwipeHandler {

	private final ScrollContainer	STORE_SCROLL_CONTAINER;
	private final MainMenuScene		mmsContext;
	private boolean					hasAttachedNextTab;
	private boolean					hasAttachedPreviousTab;

	public MainMenuStoreTabSwipeHandler(MainMenuScene context) {
		super(context, 128f, SwipeDirections.LEFT);
		this.STORE_SCROLL_CONTAINER = context.getSectionScrollContainer(MainMenuScene.SECTION_STORE);
		this.mmsContext = context;
	}

	private void attachNextTab() {

		if (this.mmsContext.getCurrentStoreTabIndex() == MainMenuStore.STORE_TABS - 1)
			return;

		if (!this.hasAttachedNextTab) {
			this.attachTab(this.mmsContext.getCurrentStoreTabIndex() + 1);
			this.hasAttachedNextTab = true;
		}
	}

	private void attachPreviousTab() {
		if (this.mmsContext.getCurrentStoreTabIndex() == 0)
			return;

		if (!this.hasAttachedPreviousTab) {
			this.attachTab(this.mmsContext.getCurrentStoreTabIndex() - 1);
			this.hasAttachedPreviousTab = true;
		}
	}

	private void attachTab(int tab) {
		final StoreButton[] TAB = this.mmsContext.getStoreTab(tab);
		for (int i = 0; i < TAB.length; i++)
			try {
				TAB[i].setY(StoreButton.calculateY(i) + (this.STORE_SCROLL_CONTAINER.getStartY() - this.STORE_SCROLL_CONTAINER.getY()));
				this.STORE_SCROLL_CONTAINER.attachChild(TAB[i]);
			} catch (final IllegalStateException ex) {
			}

		this.mmsContext.redrawPostBoughtSpriteBatch();
		this.STORE_SCROLL_CONTAINER.sortChildren();
	}

	private void detachNextTab() {
		if (this.mmsContext.getCurrentStoreTabIndex() == MainMenuStore.STORE_TABS - 1)
			return;

		this.detachTab(this.mmsContext.getCurrentStoreTabIndex() + 1);
		this.hasAttachedNextTab = false;
	}

	private void detachPreviousTab() {
		if (this.mmsContext.getCurrentStoreTabIndex() == 0)
			return;

		this.detachTab(this.mmsContext.getCurrentStoreTabIndex() - 1);
		this.hasAttachedPreviousTab = false;
	}

	private void detachTab(int tab) {
		final StoreButton[] TAB = this.mmsContext.getStoreTab(tab);
		for (int i = 0; i < TAB.length; i++)
			TAB[i].detachSelf();
	}

	public void onStoreTabChanged() {
		final StoreButton[] CURRENT_STORE_TAB = this.mmsContext.getStoreTab(this.mmsContext.getCurrentStoreTabIndex());

		for (int i = 0; i < CURRENT_STORE_TAB.length; i++)
			CURRENT_STORE_TAB[i].setY(StoreButton.calculateY(i));

		if (this.mmsContext.getCurrentStoreTabIndex() > 0)
			this.detachPreviousTab();
		if (this.mmsContext.getCurrentStoreTabIndex() < MainMenuStore.STORE_TABS - 1)
			this.detachNextTab();

		this.mmsContext.redrawPostBoughtSpriteBatch();

		this.STORE_SCROLL_CONTAINER.onTouchRelease(false);
		this.STORE_SCROLL_CONTAINER.setY(this.STORE_SCROLL_CONTAINER.getStartY());
		
		this.mmsContext.checkAdStore();
	}

	@Override
	public void onSwipe(SwipeDirections direction) {
		super.onSwipe(direction);

		this.reset();

		final StoreButton[] CURRENT_STORE_TAB = this.mmsContext.getStoreTab(this.mmsContext.getCurrentStoreTabIndex());

		for (int i = 0; i < CURRENT_STORE_TAB.length; i++)
			CURRENT_STORE_TAB[i].setTouchSuspended(true);

		if (direction == SwipeDirections.LEFT)
			this.mmsContext.toNextStoreTab();
		else if (direction == SwipeDirections.RIGHT)
			this.mmsContext.toPreviousStoreTab();
	}

	@Override
	protected void onSwipingStarted() {
		for (final StoreButton sb : this.mmsContext.getStoreTab(this.mmsContext.getCurrentStoreTabIndex()))
			sb.resetTouch();
	}

	@Override
	public void onTransformationPercentChanged(float percent) {
		if (percent < 0) {
			this.attachNextTab();
			this.detachPreviousTab();
		} else if (percent > 0) {
			this.attachPreviousTab();
			this.detachNextTab();
		} else if (percent == 0) {
			this.detachPreviousTab();
			this.detachNextTab();
		}

		this.STORE_SCROLL_CONTAINER
				.setX((EnvironmentVars.MAIN_CONTEXT.width() * -this.mmsContext.getCurrentStoreTabIndex()) + 64f * percent);

	}
}
