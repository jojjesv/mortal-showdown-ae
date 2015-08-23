package com.partlight.ms.entity.touch.swipe;

import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.input.touch.TouchEvent;

import com.partlight.ms.resource.EnvironmentVars;
import com.partlight.ms.util.Ruler;

import android.view.MotionEvent;

public class SwipeHandler implements IOnSceneTouchListener {

	public enum SwipeDirections {
		LEFT, RIGHT, UP, DOWN, NONE
	}

	private float		startX;
	private float		startY;
	private float		xSwipePercent;
	private float		ySwipePercent;
	private final float	distance;
	private final float	deadzone;
	private boolean		isSwiping;
	private boolean		hasPressed;
	private boolean		canSwipeLeft;
	private boolean		canSwipeRight;
	private boolean		canSwipeUp;
	private boolean		canSwipeDown;
	private boolean		isSwipingHorizontally;

	private boolean useAspectRatioOnVertical;

	public SwipeHandler(float maxDistance, float minDistance, SwipeDirections... directions) {
		this.distance = maxDistance;
		this.deadzone = minDistance;
		this.setSwipeDirections(directions);
	}

	public SwipeHandler(float maxDistance, SwipeDirections... directions) {
		this(maxDistance, 16f, directions);
	}

	protected boolean assertCanSwipe() {
		return true;
	}

	public float getSwipeDistance() {
		return this.distance;
	}

	public float getXSwipePercent() {
		return this.xSwipePercent;
	}

	public float getYSwipePercent() {
		return this.ySwipePercent;
	}

	public boolean hasPressed() {
		return this.hasPressed;
	}

	public boolean isSwiping() {
		return this.isSwiping;
	}

	public boolean isSwipingHorizontally() {
		return this.isSwipingHorizontally;
	}

	public boolean isUsingAspectRatioVertically() {
		return this.useAspectRatioOnVertical;
	}

	public void onNonSwipeRelease() {

	}

	public void onPercentageChange() {

	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {

		final float X = pSceneTouchEvent.getX();
		final float Y = pSceneTouchEvent.getY();

		if (pSceneTouchEvent.isActionDown() && pSceneTouchEvent.getAction() != MotionEvent.ACTION_HOVER_ENTER) {
			if (!this.assertCanSwipe())
				return false;

			this.startX = X;
			this.startY = Y;
			this.hasPressed = true;
			return false;
		} else if (pSceneTouchEvent.isActionMove()) {

			final float OLD_X_PERCENT = this.xSwipePercent;
			final float OLD_Y_PERCENT = this.ySwipePercent;

			this.xSwipePercent = (X - this.startX) / this.distance;
			this.ySwipePercent = (Y - this.startY) / this.distance;

			if (this.isUsingAspectRatioVertically()) {
				final float ASPECT_RATIO = EnvironmentVars.MAIN_CONTEXT.width() / EnvironmentVars.MAIN_CONTEXT.height();

				this.ySwipePercent *= ASPECT_RATIO;
			}

			if (!this.canSwipeLeft)
				if (this.xSwipePercent < 0)
					this.xSwipePercent = 0;

			if (!this.canSwipeRight)
				if (this.xSwipePercent > 0)
					this.xSwipePercent = 0;

			if (!this.canSwipeUp)
				if (this.ySwipePercent < 0)
					this.ySwipePercent = 0;

			if (!this.canSwipeDown)
				if (this.ySwipePercent > 0)
					this.ySwipePercent = 0;

			if (!this.hasPressed)
				return false;

			if (!this.isSwiping)
				if (Ruler.getDistance(this.startX, this.startY, X, Y) > this.deadzone) {

					this.isSwipingHorizontally = Math.abs(this.startX - X) > Math.abs(this.startY - Y);
					this.isSwiping = true;

					if (!this.isSwipingHorizontally && !this.canSwipeUp && !this.canSwipeDown)
						this.isSwiping = false;

					if (this.isSwipingHorizontally && !this.canSwipeLeft && !this.canSwipeRight)
						this.isSwiping = false;

					if (this.isSwiping)
						this.onSwipingStarted();
				}

			if (this.isSwiping) {
				this.onPercentageChange();

				if (this.xSwipePercent <= -1f && OLD_X_PERCENT > -1f)
					this.onSwipe(SwipeDirections.LEFT);
				else if (this.xSwipePercent >= 1f && OLD_X_PERCENT < 1f)
					this.onSwipe(SwipeDirections.RIGHT);

				if (this.ySwipePercent <= -1f && OLD_Y_PERCENT > -1f)
					this.onSwipe(SwipeDirections.UP);
				else if (this.ySwipePercent >= 1f && OLD_Y_PERCENT < 1f)
					this.onSwipe(SwipeDirections.DOWN);
			}

			return true;
		} else if (this.isSwiping && pSceneTouchEvent.isActionUp()) {
			this.release();
			return true;
		}
		return false;
	}

	public void onSwipe(SwipeDirections direction) {
		this.isSwiping = false;
		this.isSwipingHorizontally = false;
		this.xSwipePercent = 0f;
		this.ySwipePercent = 0f;
	}

	public void onSwipeCanceled() {

	}

	protected void onSwipingStarted() {

	}

	public void release() {
		if (!this.hasPressed)
			return;

		if (this.isSwiping)
			this.onSwipeCanceled();
		else
			this.onNonSwipeRelease();
		this.hasPressed = false;
		this.isSwiping = false;
		this.xSwipePercent = 0f;
		this.ySwipePercent = 0f;
	}

	public void reset() {
		this.hasPressed = false;
		this.isSwiping = false;
		this.isSwipingHorizontally = false;
		this.xSwipePercent = 0;
		this.ySwipePercent = 0;
		this.onPercentageChange();
	}

	public void setSwipeDirections(SwipeDirections... directions) {

		this.canSwipeDown = this.canSwipeUp = this.canSwipeRight = this.canSwipeLeft = false;

		for (final SwipeDirections dir : directions) {
			if (!this.canSwipeUp)
				this.canSwipeUp = dir == SwipeDirections.UP;

			if (!this.canSwipeDown)
				this.canSwipeDown = dir == SwipeDirections.DOWN;

			if (!this.canSwipeLeft)
				this.canSwipeLeft = dir == SwipeDirections.LEFT;

			if (!this.canSwipeRight)
				this.canSwipeRight = dir == SwipeDirections.RIGHT;

			if (dir == SwipeDirections.NONE) {
				this.canSwipeUp = false;
				this.canSwipeDown = false;
				this.canSwipeLeft = false;
				this.canSwipeRight = false;
				break;
			}
		}
	}

	public void setUseAspectRatioVertically(boolean useAspectRatio) {
		this.useAspectRatioOnVertical = useAspectRatio;
	}
}
