package com.partlight.ms.session.character.collectible;

import java.util.ArrayList;
import java.util.Random;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.util.GLState;
import org.andengine.util.modifier.ease.EaseLinear;

import com.partlight.ms.resource.EnvironmentVars;
import com.partlight.ms.resource.ResourceManager;
import com.partlight.ms.resource.ResourceManager.HudRegions;
import com.partlight.ms.scene.session.SessionScene;
import com.partlight.ms.session.character.player.Player;
import com.partlight.ms.session.environment.EnvironmentDeltaObject;
import com.partlight.ms.util.EntityUtils;
import com.partlight.ms.util.SoundUtils;
import com.partlight.ms.util.boundary.Boundary;
import com.partlight.ms.util.updatehandler.FloatValueModifier;

import android.graphics.PointF;

public abstract class Collectible extends TiledSprite {

	private static ArrayList<Collectible> collectibles;

	public static final float	TARGET_ALIVE_TIME	= 18f;
	private static final int	SMOKE_EMIT_COUNT	= 4;

	static {
		Collectible.collectibles = new ArrayList<>();
	}

	public static Collectible[] getCollectiblesOnScreen() {
		return Collectible.collectibles.toArray(new Collectible[Collectible.collectibles.size()]);
	}

	private Boundary bBoundArea;

	private boolean	isCollected;
	private float	elapsedAliveTime;

	private final SessionScene ssContext;

	private float animY;

	public Collectible(float x, float y, ITiledTextureRegion textureRegion, int tileIndex, float boundaryWidth, float boundaryHeight,
			SessionScene context) {
		super(x, y, textureRegion, EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager());

		if (!textureRegion.getTexture().equals(ResourceManager.btColMap))
			throw new IllegalArgumentException();

		this.ssContext = context;
		this.setCurrentTileIndex(tileIndex);
		this.setScaleCenter(0, 0);
		this.setScale(2f);

		final float WIDTH = this.getWidthScaled();
		final float HEIGHT = this.getHeightScaled();

		this.bBoundArea = new Boundary() {
			@Override
			public float getBoundaryHeight() {
				return HEIGHT + 16f;
			}

			@Override
			public float getBoundaryWidth() {
				return WIDTH + 16f;
			}

			@Override
			public float getBoundaryX() {
				return Collectible.this.getX() - 8f;
			}

			@Override
			public float getBoundaryY() {
				return Collectible.this.getY() - 8f;
			}
		};

		this.registerUpdateHandler(new FloatValueModifier(0f, 1f, EaseLinear.getInstance(), 0.5f) {
			@Override
			protected void onFinished() {
				super.onFinished();
				Collectible.this.emitSmoke();
				SoundUtils.playRandomVolume(ResourceManager.sSlam0);
				EntityUtils.safetlyUnregisterUpdateHandler(Collectible.this, this);
			}

			@Override
			protected void onValueChanged(float value) {
				super.onValueChanged(value);
				Collectible.this.animY = -16f * (float) Math.sin(Math.PI * value);
			}
		});
	}

	protected void emitSmoke() {

		final float centerX = this.getX() + this.getWidth() / 2f;
		final float centerY = this.getY() + this.getHeightScaled() / 2f;
		Random r = null;

		for (int i = 0; i < Collectible.SMOKE_EMIT_COUNT; i++) {
			ITextureRegion tr = null;

			r = new Random();

			if (r.nextBoolean())
				tr = HudRegions.region_smoke_small01;
			else
				tr = HudRegions.region_smoke_small02;

			final EnvironmentDeltaObject SMOKE = new EnvironmentDeltaObject(0f, this.getY() + this.getHeightScaled(), 0.25f, tr);

			final float percent = (float) (i + 1) / (float) Collectible.SMOKE_EMIT_COUNT;

			SMOKE.location.x = this.getX() + (this.getWidth() * percent) * i;

			final float rads = (float) Math.atan2(centerY - SMOKE.location.y, centerX - SMOKE.location.x);

			SMOKE.delta = new PointF((float) -Math.cos(rads) * 0.3f, (float) -Math.sin(rads) * 0.15f);
			SMOKE.location.x -= tr.getWidth() / 2f;
			SMOKE.location.y -= tr.getHeight() / 2f;
			SMOKE.fadeOut = true;
			SMOKE.fadeOutFactor = 0.0075;
			SMOKE.ticksUntilFadeOut = 10;

			this.ssContext.getCollectibleSmoke().getList().add(SMOKE);
			this.ssContext.getCollectibleSmoke().updateDrawing();
		}
	}

	public Boundary getBoundary() {
		return this.bBoundArea;
	}

	@Override
	public void onAttached() {
		super.onAttached();

		if (Collectible.collectibles.size() == 0)
			ResourceManager.btColMap.load();

		Collectible.collectibles.add(this);
	}

	public void onCollected(Player p) {
		if (this.isCollected)
			return;

		EntityUtils.safetlyDetachAndDispose(this);
		this.isCollected = true;
	}

	@Override
	public void onDetached() {
		super.onDetached();
		Collectible.collectibles.remove(this);

		if (Collectible.collectibles.size() == 0)
			ResourceManager.btColMap.unload();
	}

	@Override
	protected void onManagedUpdate(float pSecondsElapsed) {
		this.elapsedAliveTime += pSecondsElapsed;

		if (this.elapsedAliveTime >= Collectible.TARGET_ALIVE_TIME * 0.75f)
			this.setVisible((int) (this.elapsedAliveTime * 4f) % 2 == 0);

		if (this.elapsedAliveTime >= Collectible.TARGET_ALIVE_TIME)
			this.onCollected(null);

		super.onManagedUpdate(pSecondsElapsed);
	}

	@Override
	protected void preDraw(GLState pGLState, Camera pCamera) {
		pGLState.translateModelViewGLMatrixf(0f, this.animY, 0f);
		super.preDraw(pGLState, pCamera);
	}
}
