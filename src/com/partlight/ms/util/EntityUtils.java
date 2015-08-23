package com.partlight.ms.util;

import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.shape.RectangularShape;
import org.andengine.entity.sprite.Sprite;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.VerticalAlign;
import org.andengine.util.color.Color;
import org.andengine.util.modifier.IModifier;
import org.andengine.util.modifier.ease.EaseSineInOut;
import org.andengine.util.modifier.ease.IEaseFunction;

import com.partlight.ms.resource.EnvironmentVars;

public final class EntityUtils {

	private static IEntityModifierListener	detachListener;
	private static IEntityModifierListener	detachDisposeListener;
	private static IEntityModifierListener	detachDisposeUnloadTextureListener;

	public static final int	ANIMATION_FADE_IN						= 0;
	public static final int	ANIMATION_FADE_OUT						= 1;
	public static final int	ANIMATION_JUMP_IN_HIGH_INTESTIVITY		= 2;
	public static final int	ANIMATION_JUMP_IN_MEDIUM_INTESTIVITY	= 3;
	public static final int	ANIMATION_JUMP_IN_LOW_INTESTIVITY		= 4;
	public static final int	ANIMATION_JUMP_OUT_HIGH_INTESTIVITY		= 10;
	public static final int	ANIMATION_JUMP_OUT_MEDIUM_INTESTIVITY	= 11;
	public static final int	ANIMATION_JUMP_OUT_LOW_INTESTIVITY		= 12;
	public static final int	ANIMATION_SCALE_IN_FADE_IN				= 5;
	public static final int	ANIMATION_SCALE_IN_FADE_OUT				= 6;
	public static final int	ANIMATION_SCALE_OUT_FADE_IN				= 7;
	public static final int	ANIMATION_SCALE_OUT_FADE_OUT			= 8;
	public static final int	ANIMATION_SCALE_OUT_JAGGED				= 9;

	/**
	 * Aligns an entity.<br>
	 * <em>Note: entity.getParent() must not be equal to null!</em>
	 * 
	 * @param entity
	 *            Entity to align.
	 * @param entityWidth
	 *            Entity width <em>(do not take scaling into account)</em>
	 * @param entityHeight
	 *            Entity height <em>(do not take scaling into account)</em>
	 * @param horizontal
	 *            Horizontal alignment.
	 * @param vertical
	 *            Vertical alignment.
	 * @param xPadding
	 *            Additional X-padding.
	 * @param yPadding
	 *            Addition Y-padding.
	 */
	public static void alignEntity(IEntity entity, float entityWidth, float entityHeight, HorizontalAlign horizontal,
			VerticalAlign vertical, float xPadding, float yPadding) {
		final IEntity parent = entity.getParent();

		final float widthScaled = entityWidth * entity.getScaleX();
		final float heightScaled = entityHeight * entity.getScaleY();

		float xScaleCenterFactor = 0;
		float yScaleCenterFactor = 0;

		xScaleCenterFactor = entity.getScaleCenterX() / entityWidth;
		yScaleCenterFactor = entity.getScaleCenterY() / entityHeight;

		if (entityWidth > 0)
			xPadding += (widthScaled - entityWidth) * xScaleCenterFactor / parent.getScaleX();
		if (entityHeight > 0)
			yPadding += (heightScaled - entityHeight) * yScaleCenterFactor / parent.getScaleY();

		switch (horizontal) {
		case LEFT:
			entity.setX(xPadding);
			break;
		case RIGHT:
			if (entityWidth > 0)
				xPadding += entityWidth * ((1 - xScaleCenterFactor) * 2) / parent.getScaleX();
			entity.setX(EnvironmentVars.MAIN_CONTEXT.width() - xPadding);
			break;
		case CENTER:
			entity.setX(EnvironmentVars.MAIN_CONTEXT.width() / 2f - xPadding);
			break;
		}

		switch (vertical) {
		case TOP:
			entity.setY(yPadding);
			break;
		case BOTTOM:
			if (entityHeight > 0)
				yPadding += entityHeight * ((1 - yScaleCenterFactor) * 2) / parent.getScaleY();
			entity.setY(EnvironmentVars.MAIN_CONTEXT.height() - yPadding);
			break;
		case CENTER:
			entity.setY(EnvironmentVars.MAIN_CONTEXT.height() / 2f - yPadding);
			break;
		}
	}

	/**
	 * Aligns an entity.<br>
	 * <em>Note: entity.getParent() must not be equal to null!</em>
	 * 
	 * @param entity
	 *            Entity to align.
	 * @param horizontal
	 *            Horizontal alignment.
	 * @param vertical
	 *            Vertical alignment.
	 */
	public static void alignEntity(IEntity entity, HorizontalAlign horizontal, VerticalAlign vertical) {
		EntityUtils.alignEntity(entity, horizontal, vertical, 0, 0);
	}

	public static void alignEntity(IEntity entity, HorizontalAlign horizontal, VerticalAlign vertical, float xPadding, float yPadding) {
		EntityUtils.alignEntity(entity, 0, 0, horizontal, vertical, xPadding, yPadding);
	}

	public static void animateEntity(IEntity entity, float duration, int animation) {
		EntityUtils.animateEntity(entity, duration, animation, EaseSineInOut.getInstance());
	}

	public static void animateEntity(IEntity entity, float duration, int animation, IEaseFunction easeFunction) {
		EntityUtils.animateEntity(entity, duration, animation, easeFunction, null);
	}

	public static void animateEntity(IEntity entity, float duration, int animation, IEaseFunction easeFunction,
			IEntityModifierListener listener) {

		// final float X = entity.getX();
		// final float Y = entity.getY();
		final float SCALE_X = entity.getScaleX();
		final float SCALE_Y = entity.getScaleY();
		final float ALPHA = entity.getAlpha();
		// final float ROTATION = entity.getRotation();

		boolean callOnUpdate = true;

		IEntityModifier mod = null;

		switch (animation) {

		case EntityUtils.ANIMATION_FADE_IN:
			mod = new AlphaModifier(duration, 0f, ALPHA, easeFunction);
			break;

		case EntityUtils.ANIMATION_FADE_OUT:
			mod = new AlphaModifier(duration, ALPHA, 0f, easeFunction);
			break;

		case EntityUtils.ANIMATION_JUMP_IN_HIGH_INTESTIVITY:
			mod = EntityUtils.createJumpAnimation(entity, duration, 1f, false);
			break;

		case EntityUtils.ANIMATION_JUMP_IN_MEDIUM_INTESTIVITY:
			mod = EntityUtils.createJumpAnimation(entity, duration, 0.75f, false);
			break;

		case EntityUtils.ANIMATION_JUMP_IN_LOW_INTESTIVITY:
			mod = EntityUtils.createJumpAnimation(entity, duration, 0.5f, false);
			break;

		case EntityUtils.ANIMATION_JUMP_OUT_HIGH_INTESTIVITY:
			mod = EntityUtils.createJumpAnimation(entity, duration, 1f, true);
			callOnUpdate = false;
			break;

		case EntityUtils.ANIMATION_JUMP_OUT_MEDIUM_INTESTIVITY:
			mod = EntityUtils.createJumpAnimation(entity, duration, 0.75f, true);
			callOnUpdate = false;
			break;

		case EntityUtils.ANIMATION_JUMP_OUT_LOW_INTESTIVITY:
			mod = EntityUtils.createJumpAnimation(entity, duration, 0.5f, true);
			callOnUpdate = false;
			break;

		case EntityUtils.ANIMATION_SCALE_IN_FADE_IN:
			mod = new AlphaModifier(duration, 0f, ALPHA, easeFunction) {
				@Override
				protected void onSetValue(IEntity pEntity, float pPercentageDone, float pAlpha) {
					super.onSetValue(pEntity, pPercentageDone, pAlpha);
					pEntity.setScale(SCALE_X * (1f - 0.25f * (1f - pPercentageDone)), SCALE_Y * (1f - 0.25f * (1f - pPercentageDone)));
				}
			};
			break;

		case EntityUtils.ANIMATION_SCALE_IN_FADE_OUT:
			mod = new AlphaModifier(duration, ALPHA, 0f, easeFunction) {
				@Override
				protected void onSetValue(IEntity pEntity, float pPercentageDone, float pAlpha) {
					super.onSetValue(pEntity, pPercentageDone, pAlpha);
					pEntity.setScale(SCALE_X * (1f - 0.25f * pPercentageDone), SCALE_Y * (1f - 0.25f * pPercentageDone));
				}
			};
			break;

		case EntityUtils.ANIMATION_SCALE_OUT_JAGGED:
			mod = new DelayModifier(duration) {

				@Override
				protected void onModifierFinished(IEntity pItem) {
					super.onModifierFinished(pItem);
					pItem.setScale(SCALE_X, SCALE_Y);
				}

				@Override
				protected void onModifierStarted(IEntity pItem) {
					super.onModifierStarted(pItem);
					pItem.setScale(SCALE_X * 1.25f, SCALE_Y * 1.25f);
				}
			};
			break;

		case EntityUtils.ANIMATION_SCALE_OUT_FADE_IN:
			mod = new AlphaModifier(duration, 0f, ALPHA, easeFunction) {
				@Override
				protected void onSetValue(IEntity pEntity, float pPercentageDone, float pAlpha) {
					super.onSetValue(pEntity, pPercentageDone, pAlpha);
					pEntity.setScale(SCALE_X * (1f + 0.25f * (1f - pPercentageDone)), SCALE_Y * (1f + 0.25f * (1f - pPercentageDone)));
				}
			};
			break;

		case EntityUtils.ANIMATION_SCALE_OUT_FADE_OUT:
			mod = new AlphaModifier(duration, ALPHA, 0f, easeFunction) {
				@Override
				protected void onSetValue(IEntity pEntity, float pPercentageDone, float pAlpha) {
					super.onSetValue(pEntity, pPercentageDone, pAlpha);
					pEntity.setScale(SCALE_X * (1f + 0.25f * pPercentageDone), SCALE_Y * (1f + 0.25f * pPercentageDone));
				}
			};
			break;
		}

		if (mod == null)
			return;

		mod.addModifierListener(listener);
		if (callOnUpdate)
			mod.onUpdate(0f, entity);
		mod.setAutoUnregisterWhenFinished(true);
		entity.registerEntityModifier(mod);
	}

	public static void animateSineColor(IEntity entity, Color c1, Color c2, float duration) {
		final float FACTOR = EntityUtils.getSineValue(duration);
		final float r = Ruler.clamp(c1.getRed() + (c2.getRed() - c1.getRed()) * FACTOR, 0, 1);
		final float g = Ruler.clamp(c1.getGreen() + (c2.getGreen() - c1.getGreen()) * FACTOR, 0, 1);
		final float b = Ruler.clamp(c1.getBlue() + (c2.getBlue() - c1.getBlue()) * FACTOR, 0, 1);
		final float a = Ruler.clamp(c1.getAlpha() + (c2.getAlpha() - c1.getAlpha()) * FACTOR, 0, 1);

		entity.setColor(r, g, b, a);
	}

	private static DelayModifier createJumpAnimation(IEntity entity, float duration, final float intensivity, boolean reversed) {
		final float X = entity.getX();
		final float Y = entity.getY();
		final float SCALE_X = entity.getScaleX();
		final float SCALE_Y = entity.getScaleY();
		final float ALPHA = entity.getAlpha();
		final float ROTATION = entity.getRotation();
		final boolean REVERSED = reversed;

		return new DelayModifier(duration) {
			private float totalSecondsElapsed;

			@Override
			protected void onManagedUpdate(float pSecondsElapsed, IEntity pEntity) {
				super.onManagedUpdate(pSecondsElapsed, pEntity);

				this.totalSecondsElapsed += (REVERSED) ? -pSecondsElapsed : pSecondsElapsed;

				switch ((int) Math.floor((this.totalSecondsElapsed / this.mDuration) / 0.33f)) {
				case 0:
					pEntity.setX(X - 128f);
					pEntity.setY(Y + 32f);
					pEntity.setRotation(ROTATION + 50f * intensivity);
					pEntity.setScaleX(SCALE_X * (1f + 0.6f * intensivity));
					pEntity.setScaleY(SCALE_Y * (1f + 0.6f * intensivity));
					pEntity.setAlpha(ALPHA * 0.25f);
					break;
				case 1:
					pEntity.setX(X + 100f * intensivity);
					pEntity.setY(Y + 48f * intensivity);
					pEntity.setRotation(ROTATION - 33f * intensivity);
					pEntity.setScaleX(SCALE_X * (1f + 0.5f * intensivity));
					pEntity.setScaleY(SCALE_Y * (1f + 0.5f * intensivity));
					pEntity.setAlpha(ALPHA * 0.25f);
					break;
				case 2:
					pEntity.setX(X);
					pEntity.setY(Y + 24f * intensivity);
					pEntity.setRotation(ROTATION);
					pEntity.setScale(SCALE_X * (1f + 0.65f * intensivity));
					pEntity.setScale(SCALE_Y * (1f + 0.65f * intensivity));
					pEntity.setAlpha(ALPHA * 0.66f);
					break;
				}
			}

			@Override
			protected void onModifierFinished(IEntity pItem) {
				if (!REVERSED) {
					pItem.setX(X);
					pItem.setY(Y);
					pItem.setRotation(ROTATION);
					pItem.setScale(SCALE_X, SCALE_Y);
					pItem.setAlpha(ALPHA);
				}

				super.onModifierFinished(pItem);
			}

			@Override
			protected void onModifierStarted(IEntity pItem) {
				super.onModifierStarted(pItem);

				if (REVERSED)
					this.totalSecondsElapsed = this.mDuration;
			}
		};
	}

	public static IEntityModifierListener getDetachDisposeListener() {
		if (EntityUtils.detachDisposeListener == null)
			EntityUtils.detachDisposeListener = new EntityModifierAdapter() {
				@Override
				public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
					EntityUtils.safetlyDetachAndDispose(pItem);
				}
			};
		return EntityUtils.detachDisposeListener;
	}

	public static IEntityModifierListener getDetachDisposeUnloadTextureListener() {
		if (EntityUtils.detachDisposeUnloadTextureListener == null)
			EntityUtils.detachDisposeUnloadTextureListener = new EntityModifierAdapter() {
				@Override
				public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
					EntityUtils.safetlyDetachAndDispose(pItem);
					try {
						((Sprite) pItem).getTextureRegion().getTexture().unload();
					} catch (final Exception e) {
					}
				}
			};
		return EntityUtils.detachDisposeUnloadTextureListener;
	}

	public static IEntityModifierListener getDetachListener() {
		if (EntityUtils.detachListener == null)
			EntityUtils.detachListener = new EntityModifierAdapter() {
				@Override
				public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
					EntityUtils.safetlyDetach(pItem);
				}
			};
		return EntityUtils.detachListener;
	}

	public static float getSineValue(float duration) {
		float value = (float) Math
				.sin(Math.toRadians((EnvironmentVars.MAIN_CONTEXT.getEngine().getSecondsElapsedTotal() * 250.0f) / duration));

		value *= 0.5f;
		value += 0.5f;

		return value;
	}

	public static final float getXDelta(RectangularShape shape) {
		return (shape.getWidthScaled() - shape.getWidth()) * (shape.getScaleCenterX() / shape.getWidth());
	}

	public static final float getYDelta(RectangularShape shape) {
		return (shape.getHeightScaled() - shape.getHeight()) * (shape.getScaleCenterY() / shape.getHeight());
	}

	public static void safetlyClearEntityModifiers(IEntity entity) {
		final IEntity ENTITY = entity;
		EnvironmentVars.MAIN_CONTEXT.runOnUpdateThread(new Runnable() {
			@Override
			public void run() {
				ENTITY.clearEntityModifiers();
			}
		});
	}

	public static void safetlyDetach(IEntity entity) {
		final IEntity ENTITY = entity;
		EnvironmentVars.MAIN_CONTEXT.runOnUpdateThread(new Runnable() {
			@Override
			public void run() {
				ENTITY.detachSelf();
			}
		});
	}

	public static void safetlyDetachAndDispose(IEntity entity) {
		if (entity == null)
			return;

		final IEntity ENTITY = entity;
		EnvironmentVars.MAIN_CONTEXT.runOnUpdateThread(new Runnable() {
			@Override
			public void run() {
				ENTITY.detachSelf();
				if (!ENTITY.isDisposed())
					ENTITY.dispose();
			}
		});
	}

	public static void safetlyUnregisterUpdateHandler(IEntity entity, IUpdateHandler updateHandler) {
		final IEntity ENTITY = entity;
		final IUpdateHandler UPDATE_HANDLER = updateHandler;
		EnvironmentVars.MAIN_CONTEXT.runOnUpdateThread(new Runnable() {
			@Override
			public void run() {
				ENTITY.unregisterUpdateHandler(UPDATE_HANDLER);
			}
		});
	}
}
