package com.partlight.ms.session.camera;

import java.util.Random;

import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.Entity;
import org.andengine.entity.scene.Scene;
import org.andengine.util.modifier.ease.EaseLinear;
import org.andengine.util.modifier.ease.EaseSineOut;

import com.partlight.ms.resource.EnvironmentVars;
import com.partlight.ms.session.character.Character;
import com.partlight.ms.session.level.Level;
import com.partlight.ms.util.updatehandler.FloatValueModifier;
import com.partlight.ms.util.updatehandler.FloatValueModifier.OnValueChangeListener;

import android.graphics.PointF;

public class CameraManager {

	private class CameraManagerUpdateHandler implements IUpdateHandler {

		private void limitBounds(PointF position) {

			final float MIN_X = CameraManager.this.shakeForceDistance;
			final float MIN_Y = CameraManager.this.shakeForceDistance;
			final float MAX_X = CameraManager.this.lLevel.getMapWidth() - CameraManager.this.shakeForceDistance;
			final float MAX_Y = CameraManager.this.lLevel.getMapHeight() - CameraManager.this.shakeForceDistance;

			if (position.x < MIN_X)
				position.x = MIN_X;
			if (position.y < MIN_Y)
				position.y = MIN_Y;

			if (position.x + CameraManager.this.gcCamera.getWidth() > MAX_X)
				position.x = MAX_X - CameraManager.this.gcCamera.getWidth();
			if (position.y + CameraManager.this.gcCamera.getHeight() > MAX_Y)
				position.y = MAX_Y - CameraManager.this.gcCamera.getHeight();
		}

		@Override
		public void onUpdate(float pSecondsElapsed) {

			CameraManager.this.chaseModX.onUpdate(pSecondsElapsed);
			CameraManager.this.chaseModY.onUpdate(pSecondsElapsed);

			final float X = CameraManager.this.eFollowEntity.getX() + CameraManager.this.entityDeltaX
					- EnvironmentVars.MAIN_CONTEXT.width() / 2f;
			final float Y = CameraManager.this.eFollowEntity.getY() + CameraManager.this.entityDeltaY
					- EnvironmentVars.MAIN_CONTEXT.height() / 2f;

			final PointF POSITION = new PointF(X, Y);

			this.limitBounds(POSITION);

			if (EnvironmentVars.MAIN_CONTEXT.useChaseCamera()) {

				POSITION.x += CameraManager.this.chaseModX.getValue();
				POSITION.y += CameraManager.this.chaseModY.getValue();

				this.limitBounds(POSITION);
			}

			POSITION.x += CameraManager.this.shakeDeltaX;
			POSITION.y += CameraManager.this.shakeDeltaY;

			EnvironmentVars.MAIN_CONTEXT.getCamera().set(POSITION.x, POSITION.y, POSITION.x + EnvironmentVars.MAIN_CONTEXT.width(),
					POSITION.y + EnvironmentVars.MAIN_CONTEXT.height());
		}

		@Override
		public void reset() {

		}

	}

	private final CameraManagerUpdateHandler	updateHandler;
	private Entity								eFollowEntity;
	private final GameCamera					gcCamera;
	private final Level							lLevel;
	private final Scene							sContext;
	private float								entityDeltaX;
	private float								entityDeltaY;
	private float								shakeDeltaX;
	private float								shakeDeltaY;

	private float						shakeForceDistance;
	private FloatValueModifier			shakeModX;
	private FloatValueModifier			shakeModY;
	private final FloatValueModifier	chaseModX;
	private final FloatValueModifier	chaseModY;

	public CameraManager(GameCamera camera, Level level, Scene context) {
		this.gcCamera = camera;
		this.lLevel = level;
		this.updateHandler = new CameraManagerUpdateHandler();
		this.sContext = context;
		this.chaseModX = new FloatValueModifier(0f, 1f, EaseSineOut.getInstance(), 0.33f);
		this.chaseModY = new FloatValueModifier(0f, 1f, EaseSineOut.getInstance(), 0.33f);
	}

	public void followEntity(Entity entity) {
		this.followEntity(entity, 0, 0);
	}

	public void followEntity(Entity entity, float xDelta, float yDelta) {
		this.entityDeltaX = xDelta;
		this.entityDeltaY = yDelta;
		this.eFollowEntity = entity;
	}

	public float getShakeForce() {
		return this.shakeForceDistance;
	}

	public void registerUpdateHandler() {
		this.sContext.registerUpdateHandler(this.updateHandler);
	}

	public void setShakeForce(float distance) {
		this.shakeForceDistance = distance;
	}

	/**
	 * @param direction
	 *            The shake direction, in radians.
	 */
	public void shake(float direction) {
		this.shake(direction, this.shakeForceDistance);
	}

	/**
	 * @param direction
	 *            The shake direction, in radians.
	 * @param distance
	 *            The shake force, in distance (pixels).
	 */
	public void shake(final float direction, final float distance) {
		EnvironmentVars.MAIN_CONTEXT.runOnUpdateThread(new Runnable() {
			@Override
			public void run() {
				CameraManager.this.shakeDeltaX = (float) Math.cos(direction) * distance;
				CameraManager.this.shakeDeltaY = (float) Math.sin(direction) * distance;

				if (CameraManager.this.shakeModX != null)
					CameraManager.this.sContext.unregisterUpdateHandler(CameraManager.this.shakeModX);

				if (CameraManager.this.shakeModY != null)
					CameraManager.this.sContext.unregisterUpdateHandler(CameraManager.this.shakeModY);

				CameraManager.this.shakeModX = new FloatValueModifier(CameraManager.this.shakeDeltaX, 0, EaseLinear.getInstance(), 0.1f);
				CameraManager.this.shakeModX.setOnValueChangeListener(new OnValueChangeListener() {
					@Override
					public void valueChanged(float newValue) {
						CameraManager.this.shakeDeltaX = newValue;
					}
				});
				CameraManager.this.shakeModX.runOnFinish(new Runnable() {
					@Override
					public void run() {
						CameraManager.this.sContext.unregisterUpdateHandler(CameraManager.this.shakeModX);
					}
				});

				CameraManager.this.shakeModY = new FloatValueModifier(CameraManager.this.shakeDeltaY, 0, EaseLinear.getInstance(), 0.1f);
				CameraManager.this.shakeModY.setOnValueChangeListener(new OnValueChangeListener() {
					@Override
					public void valueChanged(float newValue) {
						CameraManager.this.shakeDeltaY = newValue;
					}
				});
				CameraManager.this.shakeModY.runOnFinish(new Runnable() {
					@Override
					public void run() {
						CameraManager.this.sContext.unregisterUpdateHandler(CameraManager.this.shakeModY);
					}
				});

				CameraManager.this.sContext.registerUpdateHandler(CameraManager.this.shakeModX);
				CameraManager.this.sContext.registerUpdateHandler(CameraManager.this.shakeModY);
			}
		});
	}

	public void shakeRandomDirection() {
		this.shake((float) (Math.PI * 2 * new Random().nextFloat()));
	}

	public void unregisterUpdateHandler() {
		this.sContext.unregisterUpdateHandler(this.updateHandler);
	}

	public void updateChase() {
		if (this.eFollowEntity == null || !(this.eFollowEntity instanceof Character) || !EnvironmentVars.MAIN_CONTEXT.useChaseCamera())
			return;

		this.chaseModX.setFrom(this.chaseModX.getValue());
		this.chaseModY.setFrom(this.chaseModY.getValue());
		this.chaseModX.reset();
		this.chaseModY.reset();

		final float DEGREES = (float) Math.toRadians(Character.directionToDegrees(((Character) this.eFollowEntity).getMoveDirection()));

		final float xTo = (float) Math.cos(DEGREES) * 16f;
		final float yTo = (float) Math.sin(DEGREES) * 16f;

		this.chaseModX.setTo(xTo);
		this.chaseModY.setTo(yTo);
	}
}
