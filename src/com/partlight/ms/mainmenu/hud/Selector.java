package com.partlight.ms.mainmenu.hud;

import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.batch.SpriteBatch;
import org.andengine.entity.text.exception.OutOfCharactersException;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.util.modifier.ease.EaseCubicIn;

import com.partlight.ms.entity.ShadowedText;
import com.partlight.ms.entity.mainmenu.PriceTag;
import com.partlight.ms.entity.touch.scroll.ScrollContainer;
import com.partlight.ms.resource.EnvironmentVars;
import com.partlight.ms.resource.ResourceManager;
import com.partlight.ms.resource.ResourceManager.StoreTextureRegions;
import com.partlight.ms.util.EntityUtils;
import com.partlight.ms.util.Fade;

public abstract class Selector implements IOnSceneTouchListener {

	public static final class Strings {
		public static final String	STRING_MAKE_SELECTION		= "TOUCH A SELECTION";
		public static final String	STRING_CONFIRM_SELECTION	= "TOUCH AGAIN TO CONFIRM";
		public static final String	STRING_CANCEL_SELECTION		= "TOUCH HERE TO CANCEL";
	}

	public static final float	MAP_WIDTH			= 128f;
	public static final float	MAP_Y				= 32f;
	public static final float	MAP_Y_PADDING		= 32f;
	public static final float	CONFIRM_PEAK_SCALE	= 2.25f;

	private Fade					fBackgroundFade;
	private Rectangle				rItemsBackground;
	private ScaleModifier			smCheckmarkScaleModifier;
	private ShadowedText			stCancel;
	private ShadowedText			stConfirm;
	private Sprite					sConfirmCheckmark;
	private SpriteBatch				sbList;
	private SpriteBatch				sbMapBackground;
	private boolean					isShowing;
	private boolean					purchaseCanceled;
	private boolean[]				disabledIndicies;
	private final Runnable			SELECTION_CONFIRM_EVENT;
	private final ScrollContainer	scItemsContainer;
	private int						selectedIndex;
	protected PriceTag[]			ptPriceTags;
	protected ITexture				listTexture;
	private float					listHeight;
	protected float					listRotation;
	protected float					listWidth;
	protected int					listLength;
	private boolean					manageTexture;

	public Selector() {

		this.SELECTION_CONFIRM_EVENT = new Runnable() {
			@Override
			public void run() {
				Selector.this.onClosed();
			}
		};

		this.scItemsContainer = new ScrollContainer(8f) {
			@Override
			public void setY(float pY) {
				super.setY(pY);
				try {
					Selector.this.sbMapBackground.setY(pY);
				} catch (final NullPointerException ex) {
				}
			}
		};
	}

	public void addPriceTags(final boolean[] indiciesWithPriceTag, final int[] prices) {
		EnvironmentVars.MAIN_CONTEXT.runOnUpdateThread(new Runnable() {
			@Override
			public void run() {

				if (Selector.this.ptPriceTags != null)
					for (int i = 0; i < Selector.this.ptPriceTags.length; i++) {
						Selector.this.ptPriceTags[i].detachSelf();
						Selector.this.ptPriceTags[i].dispose();
					}

				int length = 0;
				for (final boolean b : indiciesWithPriceTag)
					if (b)
						length++;

				Selector.this.ptPriceTags = new PriceTag[length];

				if (length < 1)
					return;

				int x = 0;
				for (int i = 0; i < indiciesWithPriceTag.length; i++)
					if (indiciesWithPriceTag[i]) {
						Selector.this.ptPriceTags[x] = new PriceTag(Selector.this.getListItemX(i), Selector.this.getListItemY(i),
								prices[i]);
						Selector.this.ptPriceTags[x].setScale(2);
						Selector.this.sbList.attachChild(Selector.this.ptPriceTags[x]);
						x++;
					}
			}
		});
	}

	public Fade getFade() {
		return this.fBackgroundFade;
	}

	public float getListHeight() {
		return this.listHeight;
	}

	protected float getListItemA(int index) {
		return ((this.disabledIndicies[index]) ? 0.5f : 1);
	}

	protected float getListItemB(int index) {
		return ((this.disabledIndicies[index]) ? 0.75f : 1);
	}

	protected float getListItemG(int index) {
		return ((this.disabledIndicies[index]) ? 0.75f : 1);
	}

	protected float getListItemR(int index) {
		return 1f;
	}

	protected float getListItemScale(int index) {
		return 2f;
	}

	protected abstract ITextureRegion getListItemTextureRegion(int index);

	protected float getListItemX(int index) {
		return 24;
	}

	protected float getListItemY(int index) {
		return Selector.MAP_Y + (this.listHeight / this.listLength) * index;
	}

	protected void initBackgroundBatch() {

		final int BACKGROUND_TILE_COUNT = (int) Math
				.ceil((EnvironmentVars.MAIN_CONTEXT.height() / (ResourceManager.btSelectorBackground.getHeight() * 2f))) + 1;
		this.sbMapBackground = new SpriteBatch(ResourceManager.btSelectorBackground, BACKGROUND_TILE_COUNT,
				EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager()) {
			@Override
			public void setY(float pY) {

				final float MIN = ResourceManager.btSelectorBackground.getHeight() * -2f;

				while (pY < MIN)
					pY += -MIN;

				super.setY(pY);
			}
		};

		final TextureRegion REGION = StoreTextureRegions.region_selector_background;

		for (int i = 0; i < BACKGROUND_TILE_COUNT; i++)
			//@formatter:off
			this.sbMapBackground.draw(
					REGION,
					0f,
					REGION.getHeight() * 2f * i,
					REGION.getWidth() * 2f,
					REGION.getHeight() * 2f,
					0f,
					1f, 1f, 1f, 1f);
			//@formatter:on

		this.sbMapBackground.submit();
	}

	protected void initFade() {
		this.fBackgroundFade = new Fade(EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager()) {
			@Override
			public void setAlpha(float pAlpha) {
				Selector.this.sbList.setX((Selector.MAP_WIDTH + ((Selector.this.listWidth * 2f) / 2f)) * (1f - pAlpha));
				Selector.this.sbMapBackground.setX(Selector.this.scItemsContainer.getX() + Selector.this.sbList.getX());
				Selector.this.rItemsBackground
						.setX(Selector.this.sbMapBackground.getX() + ResourceManager.btSelectorBackground.getWidth() * 2f);
				super.setAlpha(pAlpha);
			}
		};
		this.fBackgroundFade.setDuration(0.2f);
	}

	protected void initList() {
		this.sbList = new SpriteBatch(this.listTexture, this.listLength, EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager());
	}

	protected void initVariables() {

	}

	public boolean isManageTexture() {
		return this.manageTexture;
	}

	public boolean isShowing() {
		return this.isShowing;
	}

	protected void onClosed() {
		ResourceManager.btSelectorCheckmark.unload();
		ResourceManager.btSelectorBackground.unload();
		if (this.manageTexture)
			this.listTexture.unload();
		this.sConfirmCheckmark.detachSelf();

		if (!this.purchaseCanceled)
			this.onSelectionMade(this.selectedIndex);
	}

	protected void onMapSelectionMade(int selection) {

		float checkmarkY = this.getListItemY(selection);

		checkmarkY += (this.getListItemTextureRegion(selection).getHeight() * this.getListItemScale(selection)
				- this.sConfirmCheckmark.getHeightScaled()) / 2f;
		checkmarkY += EntityUtils.getYDelta(this.sConfirmCheckmark);

		float checkmarkX = this.getListItemX(selection);

		checkmarkX += (this.getListItemTextureRegion(selection).getWidth() * this.getListItemScale(selection)
				- this.sConfirmCheckmark.getWidthScaled()) / 2f;
		checkmarkX += EntityUtils.getXDelta(this.sConfirmCheckmark);

		this.sConfirmCheckmark.setPosition(checkmarkX, checkmarkY);

		if (!this.stConfirm.getText().toString().contentEquals(Strings.STRING_CONFIRM_SELECTION)) {
			this.sbList.attachChild(this.sConfirmCheckmark);
			this.stConfirm.setText(Strings.STRING_CONFIRM_SELECTION);
		}

		this.sConfirmCheckmark.setScale(Selector.CONFIRM_PEAK_SCALE);

		if (this.smCheckmarkScaleModifier == null) {
			this.smCheckmarkScaleModifier = new ScaleModifier(0.15f, Selector.CONFIRM_PEAK_SCALE, 2f, EaseCubicIn.getInstance());
			this.smCheckmarkScaleModifier.setAutoUnregisterWhenFinished(true);
		}
		this.smCheckmarkScaleModifier.reset();
		this.sConfirmCheckmark.registerEntityModifier(this.smCheckmarkScaleModifier);
	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {

		if (!this.isShowing() || this.fBackgroundFade.isAnimating())
			return false;

		if (this.smCheckmarkScaleModifier != null && !this.smCheckmarkScaleModifier.isFinished())
			return false;

		final float X = pSceneTouchEvent.getX();
		final float Y = pSceneTouchEvent.getY();

		final boolean CONTAINER_EVENT = this.scItemsContainer.getMinY() < 0 && this.scItemsContainer.onTouchEvent(pSceneTouchEvent);

		if (!CONTAINER_EVENT)
			if (pSceneTouchEvent.isActionUp()) {
				if (X > this.scItemsContainer.getX()) {
					if (Y > Selector.MAP_Y && Y < this.getListHeight()) {

						final float HEIGHT = this.listHeight;

						final int SELECTION = (int) Math
								.floor(this.listLength * ((Y - Selector.MAP_Y - this.scItemsContainer.getY()) / HEIGHT));

						if (this.disabledIndicies[SELECTION])
							return false;

						if (this.selectedIndex != SELECTION)
							this.onMapSelectionMade(SELECTION);
						else {
							this.purchaseCanceled = false;
							this.isShowing = false;
							this.fBackgroundFade.hide();
						}

						this.selectedIndex = SELECTION;
					}
				} else {
					this.purchaseCanceled = true;
					this.isShowing = false;
					this.fBackgroundFade.hide();
				}
				return true;
			}
		return false;
	}

	protected void onSelectionMade(int index) {

	}

	protected void postConstructor() {
		this.initVariables();

		for (int i = 0; i < this.listLength; i++)
			this.listHeight += this.getListItemTextureRegion(i).getHeight() * this.getListItemScale(i) + Selector.MAP_Y_PADDING;

		this.scItemsContainer.setX(EnvironmentVars.MAIN_CONTEXT.width() - Selector.MAP_WIDTH - ((this.listWidth * 2f) / 2f));
		this.scItemsContainer.setMinY(EnvironmentVars.MAIN_CONTEXT.height() - Selector.MAP_Y - this.listHeight);

		this.stConfirm = new ShadowedText(16, 8, ResourceManager.fFontMain, Strings.STRING_CONFIRM_SELECTION,
				EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager()) {
			@Override
			public void setText(CharSequence pText) throws OutOfCharactersException {
				super.setText(pText);
				this.setScaleCenter(0, 0);
			}
		};
		this.stConfirm.setText(Strings.STRING_MAKE_SELECTION);
		this.stConfirm.setScale(2);

		this.stCancel = new ShadowedText(16f, 0f, ResourceManager.fFontMain, Strings.STRING_CANCEL_SELECTION,
				EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager());
		this.stCancel.setScaleCenter(0, 0);
		this.stCancel.setScale(2f);
		this.stCancel.setY(EnvironmentVars.MAIN_CONTEXT.height() - this.stConfirm.getY() - this.stCancel.getHeightScaled());
		this.stCancel.setColor(0.75f, 0.75f, 0.75f);
		this.initBackgroundBatch();
		this.rItemsBackground = new Rectangle(0f, 0f, 1f, EnvironmentVars.MAIN_CONTEXT.height(),
				EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager()) {
			@Override
			public void setX(float pX) {
				super.setX(pX);
				this.setWidth(EnvironmentVars.MAIN_CONTEXT.width() - pX);
			}
		};

		// final float COLOR_CHANNEL = 0.0705882f;
		// this.rMapBackground.setColor(COLOR_CHANNEL, COLOR_CHANNEL,
		// COLOR_CHANNEL);
		this.rItemsBackground.setColor(43f / 255f, 27f / 255f, 27f / 255f);

		this.initList();
		this.initFade();

		this.sConfirmCheckmark = new Sprite(0f, 0f, StoreTextureRegions.region_checkmark,
				EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager());
		// this.sConfirmCheckmark.setX(112f);
		this.sConfirmCheckmark.setZIndex(1);

		this.scItemsContainer.attachChild(this.sbList);
		this.fBackgroundFade.attachChild(this.rItemsBackground);
		this.fBackgroundFade.attachChild(this.sbMapBackground);
		this.fBackgroundFade.attachChild(this.scItemsContainer);
		this.fBackgroundFade.attachChild(this.stConfirm);
		this.fBackgroundFade.attachChild(this.stCancel);
	}

	public boolean purchaseCanceled() {
		return this.purchaseCanceled;
	}

	protected void redrawList() {
		ITextureRegion textureRegion;
		float scale;

		for (int i = 0; i < this.listLength; i++) {
			textureRegion = this.getListItemTextureRegion(i);
			scale = this.getListItemScale(i);

			//@formatter:off
			this.sbList.draw(textureRegion,
					this.getListItemX(i),
					this.getListItemY(i),
					textureRegion.getWidth() * scale,
					textureRegion.getHeight() * scale,
					this.listRotation,
					this.getListItemR(i),
					this.getListItemG(i),
					this.getListItemB(i),
					this.getListItemA(i));
			//@formatter:on
		}

		this.sbList.submit();
	}

	public void setManageTexture(boolean manageTexture) {
		this.manageTexture = manageTexture;
	}

	public void show(boolean... disabledSelections) {
		this.disabledIndicies = disabledSelections;
		ResourceManager.btSelectorCheckmark.load();
		ResourceManager.btSelectorBackground.load();
		if (this.manageTexture)
			this.listTexture.load();

		this.redrawList();
		this.scItemsContainer.setY(this.scItemsContainer.getMaxY());
		this.stConfirm.setText(Strings.STRING_MAKE_SELECTION);
		this.selectedIndex = -1;
		this.purchaseCanceled = false;
		this.fBackgroundFade.runOnFadeOut(null);
		this.fBackgroundFade.hideInstantly();
		this.fBackgroundFade.runOnFadeOut(this.SELECTION_CONFIRM_EVENT);
		this.fBackgroundFade.show(0.5f);
		this.isShowing = true;
	}

	public void show(int... disabledSelections) {
		int disabledSelectionsLength = disabledSelections.length;
		final boolean enableAll = disabledSelections.length > 0 && disabledSelections[0] == -1;

		if (enableAll)
			disabledSelectionsLength = this.listLength;

		this.disabledIndicies = new boolean[disabledSelectionsLength];

		if (!enableAll)
			for (final int i : disabledSelections)
				this.disabledIndicies[i] = true;

		this.show(this.disabledIndicies);
	}
}
