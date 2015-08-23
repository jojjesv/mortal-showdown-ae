package com.partlight.ms.session.character.player.weapons;

import java.util.HashMap;
import java.util.Iterator;

import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.Entity;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.partlight.ms.resource.EnvironmentVars;
import com.partlight.ms.scene.session.SessionScene;
import com.partlight.ms.session.character.Zombie;
import com.partlight.ms.util.boundary.Boundary;

public class TossableAmmoManager {

	private final IUpdateHandler			updateHandler;
	private final HashMap<Sprite, Float>	projectiles;
	private final ITextureRegion[]			trProjectileRegionVariations;
	private Entity							eContext;
	private final Tossable					tTossable;
	private boolean							checkCollision;

	public TossableAmmoManager(Tossable tossable, int capacity, VertexBufferObjectManager vertexBufferObjectManager,
			ITextureRegion... textureRegionVariations) {
		this.tTossable = tossable;
		this.trProjectileRegionVariations = textureRegionVariations;
		this.updateHandler = new IUpdateHandler() {
			@Override
			public void onUpdate(float pSecondsElapsed) {
				TossableAmmoManager.this.onUpdateProjectiles(pSecondsElapsed);
			}

			@Override
			public void reset() {

			}
		};

		this.projectiles = new HashMap<>();
	}

	public void add(Sprite ammo) {
		this.projectiles.put(ammo, 0f);
		this.eContext.attachChild(ammo);
	}

	public ITextureRegion[] getProjectileRegionVariations() {
		return this.trProjectileRegionVariations;
	}

	public boolean isCheckingProjectileCollision() {
		return this.checkCollision;
	}

	protected void onUpdateProjectile(Sprite projectile, float secondsElapsed, float totalSecondsElapsed) {

		projectile.setZIndex((int) (projectile.getY() + projectile.getHeightScaled() / 2f));

		try {
			((SessionScene) TossableAmmoManager.this.eContext).getEntityYSorter().requestUpdate();
		} catch (final Exception ex) {

		}

		if (this.isCheckingProjectileCollision()) {

			final Sprite PROJECTILE = projectile;

			final Boundary SPRITE_BOUNDARY = new Boundary() {

				@Override
				public float getBoundaryHeight() {
					return PROJECTILE.getHeightScaled();
				}

				@Override
				public float getBoundaryWidth() {
					return PROJECTILE.getWidthScaled();
				}

				@Override
				public float getBoundaryX() {
					return PROJECTILE.getX();
				}

				@Override
				public float getBoundaryY() {
					return PROJECTILE.getY();
				}
			};

			for (final Zombie z : Zombie.getAliveZombies())
				if (Boundary.BoundaryUtils.isIntersecting(SPRITE_BOUNDARY, z.getInteractionBoundary())) {
					if (this.tTossable.onProjectileCollide(z))
						this.remove(PROJECTILE);
					return;
				}
		}

	}

	protected void onUpdateProjectiles(float secondsElapsed) {
		//@formatter:off
		final Iterator<Sprite> PROJECTILE_ITERATOR = this.projectiles.keySet().iterator();
		final Iterator<Float> ELAPSED_SECONDS_ITERATOR = this.projectiles.values().iterator();
		
		Sprite INDEX;
		
		while (PROJECTILE_ITERATOR.hasNext()) {
			INDEX = PROJECTILE_ITERATOR.next();
			final Float ELAPSED_SECONDS = ELAPSED_SECONDS_ITERATOR.next();
			
			this.onUpdateProjectile(INDEX, secondsElapsed, ELAPSED_SECONDS);
			this.projectiles.put(INDEX, ELAPSED_SECONDS + secondsElapsed);
		}
		//@formatter:on
	}

	public void register(Entity context) {
		this.eContext = context;
		context.registerUpdateHandler(this.updateHandler);
	}

	public void remove(final Sprite projectile) {
		EnvironmentVars.MAIN_CONTEXT.runOnUpdateThread(new Runnable() {

			@Override
			public void run() {
				TossableAmmoManager.this.projectiles.remove(projectile);

				projectile.detachSelf();
				TossableAmmoManager.this.tTossable.onRemoved(projectile);
				try {
					projectile.dispose();
				} catch (final Exception ex) {

				}
			}
		});

	}

	public void setCheckProjectileCollision(boolean checkCollision) {
		this.checkCollision = checkCollision;
	}

	public void unregister() {
		this.eContext.unregisterUpdateHandler(this.updateHandler);
	}
}