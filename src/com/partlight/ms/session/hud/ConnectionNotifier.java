package com.partlight.ms.session.hud;

import java.util.ArrayList;

import org.andengine.entity.Entity;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.modifier.MoveXModifier;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.util.modifier.IModifier;
import org.andengine.util.modifier.IModifier.IModifierListener;
import org.andengine.util.modifier.ease.EaseCubicIn;
import org.andengine.util.modifier.ease.EaseCubicOut;

import com.partlight.ms.resource.EnvironmentVars;

public class ConnectionNotifier extends TiledSprite {

	public static final int				CODE_CONNECTION_SUCCESSFUL	= 0;
	public static final int				CODE_CONNECTION_FAILED		= 1;
	public static final int				CODE_NOT_SIGNED_IN			= 2;
	public static final int				CODE_MISCONFIGURED_APP		= 3;
	private final ArrayList<Integer>	queue;
	private MoveXModifier				modSlideIn;
	private MoveXModifier				modSlideOut;
	private DelayModifier				modDelay;
	private final Entity				eContext;

	public ConnectionNotifier(float y, ITiledTextureRegion iconSet, Entity context) {
		super(EnvironmentVars.MAIN_CONTEXT.width(), y, iconSet, EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager());

		this.setScaleCenter(0, 0);
		this.setScale(2f);

		this.queue = new ArrayList<>();

		this.initMods();
		this.eContext = context;
	}

	public ConnectionNotifier(ITiledTextureRegion iconSet, Entity context) {
		this(16f, iconSet, context);
	}

	private void initMods() {
		this.modSlideIn = new MoveXModifier(0.5f, this.getX(), this.getX() - this.getWidthScaled() - 16f, EaseCubicOut.getInstance());
		this.modSlideOut = new MoveXModifier(this.modSlideIn.getDuration(), this.modSlideIn.getToValue(), this.modSlideIn.getFromValue(),
				EaseCubicIn.getInstance());
		this.modDelay = new DelayModifier(2f);

		// @formatter:off
		this.modSlideOut
				.addModifierListener(new IModifierListener<IEntity>() {
					@Override
					public void onModifierFinished(
							IModifier<IEntity> pModifier,
							IEntity pItem) {
						ConnectionNotifier.this.queue.remove(0);
						ConnectionNotifier.this.readFromQueue();
					}
					
					@Override
					public void onModifierStarted(
							IModifier<IEntity> pModifier,
							IEntity pItem) {
					}
				});
		this.modSlideIn
				.addModifierListener(new IModifierListener<IEntity>() {
					@Override
					public void onModifierFinished(
							IModifier<IEntity> pModifier,
							IEntity pItem) {
						ConnectionNotifier.this.registerEntityModifier(ConnectionNotifier.this.modDelay);
					}
					
					@Override
					public void onModifierStarted(
							IModifier<IEntity> pModifier,
							IEntity pItem) {
					}
				});
		this.modDelay.addModifierListener(new IModifierListener<IEntity>() {
			
			@Override
			public void onModifierFinished(
					IModifier<IEntity> pModifier, IEntity pItem) {
				ConnectionNotifier.this.registerEntityModifier(ConnectionNotifier.this.modSlideOut);
				
			}
			
			@Override
			public void onModifierStarted(
					IModifier<IEntity> pModifier, IEntity pItem) {
				
			}
		});
		//@formatter:on
	}

	public void queue(int code) {

		final boolean firstEntry = this.queue.size() == 0;

		this.queue.add(code);

		if (firstEntry)
			this.readFromQueue();
	}

	protected void readFromQueue() {

		EnvironmentVars.MAIN_CONTEXT.runOnUpdateThread(new Runnable() {

			@Override
			public void run() {

				final ITexture TEXTURE = ConnectionNotifier.this.getTiledTextureRegion().getTexture();

				if (ConnectionNotifier.this.queue.size() > 0) {
					if (!TEXTURE.isLoadedToHardware())
						TEXTURE.load();
					ConnectionNotifier.this.show(ConnectionNotifier.this.queue.get(0));
				} else
					ConnectionNotifier.this.detachSelf();
			}
		});

	}

	private void show(final int code) {

		this.detachSelf();
		this.eContext.attachChild(this);
		this.eContext.sortChildren();
		this.modSlideIn.reset();
		this.modSlideOut.reset();
		this.modDelay.reset();
		this.setCurrentTileIndex(code);
		this.registerEntityModifier(this.modSlideIn);
	}
}
