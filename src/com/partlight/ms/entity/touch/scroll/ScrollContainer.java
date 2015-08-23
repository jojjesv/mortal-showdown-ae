package com.partlight.ms.entity.touch.scroll;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.MoveYModifier;
import org.andengine.entity.shape.RectangularShape;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.modifier.ease.EaseCubicOut;

import com.partlight.ms.entity.AlphaFriendlyEntity;
import com.partlight.ms.resource.EnvironmentVars;

public class ScrollContainer extends AlphaFriendlyEntity {

	private float			startY;
	private boolean			hasSetStartY;
	private int				firstTouchY;
	private int				oldTouchY;
	private int				newTouchY;
	private int				firstY;
	private int				minimumY;
	private int				maximumY;
	private final float		scrollDeadZone;
	private boolean			isScrolling;
	protected boolean		touchActionHasBeenDown;
	private MoveYModifier	modReleaseAcceleration;

	public ScrollContainer(float deadzone) {
		this.scrollDeadZone = deadzone;
	}

	@Override
	public void attachChild(IEntity pEntity) throws IllegalStateException {
		super.attachChild(pEntity);

		try {

			final RectangularShape RECTANGLE = (RectangularShape) pEntity;
			final float HEIGHT = RECTANGLE.getY() + RECTANGLE.getHeightScaled();

			if (HEIGHT > EnvironmentVars.MAIN_CONTEXT.height() && -HEIGHT < this.minimumY)
				this.setMinY(EnvironmentVars.MAIN_CONTEXT.height() - HEIGHT);

		} catch (final ClassCastException ex) {

		}
	}

	public float getMaxY() {
		return this.maximumY;
	}

	public float getMinY() {
		return this.minimumY;
	}

	public float getStartY() {
		return this.startY;
	}

	public boolean isAtMax() {
		return (int) this.getY() >= (int) this.getMaxY();
	}

	public boolean isScrolling() {
		return this.isScrolling;
	}

	protected void onAccelerateRelease() {
		this.unregisterEntityModifier(this.modReleaseAcceleration);

		this.modReleaseAcceleration = new MoveYModifier(1f, this.getY(), this.getY() + (this.newTouchY - this.oldTouchY) * 1.75f,
				EaseCubicOut.getInstance()) {
			@Override
			protected void onSetValue(IEntity pEntity, float pPercentageDone, float pY) {

				if (pY < ScrollContainer.this.minimumY)
					pY = ScrollContainer.this.minimumY;

				if (pY > ScrollContainer.this.maximumY)
					pY = ScrollContainer.this.maximumY;

				super.onSetValue(pEntity, pPercentageDone, pY);
			}
		};
		this.modReleaseAcceleration.setAutoUnregisterWhenFinished(true);
		this.registerEntityModifier(this.modReleaseAcceleration);

	}

	@Override
	protected void onManagedUpdate(float pSecondsElapsed) {
		super.onManagedUpdate(pSecondsElapsed);

		this.oldTouchY -= (this.oldTouchY - this.newTouchY) / 5f;
	}

	protected void onStartedScrolling() {

	}

	public boolean onTouchEvent(TouchEvent event) {

		this.newTouchY = (int) event.getY();

		if (event.isActionDown()) {
			this.touchActionHasBeenDown = true;
			this.firstY = (int) this.getY();
			this.firstTouchY = this.newTouchY;
			if (this.modReleaseAcceleration != null)
				this.unregisterEntityModifier(this.modReleaseAcceleration);
			this.oldTouchY = this.newTouchY;

		} else if (event.isActionMove() && this.touchActionHasBeenDown) {
			if (Math.abs(this.newTouchY - this.firstTouchY) > this.scrollDeadZone && !this.isScrolling) {
				this.onStartedScrolling();
				this.isScrolling = true;
			}
			if (this.isScrolling) {

				final int newY = this.firstY + (this.newTouchY - this.firstTouchY);

				if (newY < this.minimumY) {
					this.setY(this.minimumY);
					return false;
				} else if (newY > this.maximumY) {
					this.setY(this.maximumY);
					return false;
				}

				this.setY(newY);

				return true;
			}
		} else if (event.isActionUp() && this.isScrolling) {
			this.onTouchRelease(true);
			return true;
		}

		return false;
	}

	public void onTouchRelease(boolean callAcceleratedRelease) {
		this.touchActionHasBeenDown = false;
		this.isScrolling = false;

		if (callAcceleratedRelease)
			this.onAccelerateRelease();
	}

	@Override
	public void reset() {
		this.isScrolling = false;
	}

	public void setMaxY(float maximum) {
		this.maximumY = (int) maximum;
	}

	public void setMinY(float minimum) {
		this.minimumY = (int) minimum;
	}

	@Override
	public void setY(float pY) {
		if (!this.hasSetStartY) {
			this.startY = pY;
			this.hasSetStartY = true;
		}

		super.setY(pY);
	}
}
