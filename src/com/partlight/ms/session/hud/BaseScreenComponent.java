package com.partlight.ms.session.hud;

import org.andengine.entity.IEntity;
import org.andengine.entity.shape.RectangularShape;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;

import com.partlight.ms.entity.AlphaFriendlyEntity;
import com.partlight.ms.resource.EnvironmentVars;
import com.partlight.ms.scene.DialogScene;
import com.partlight.ms.session.hud.listener.ComponentListener;
import com.partlight.ms.util.boundary.Boundary;

public class BaseScreenComponent extends AlphaFriendlyEntity implements Boundary {

	private boolean				hasPointerId;
	private boolean				isAttached;
	private boolean				suspendTouch;
	private DialogScene			dsContext;
	private int					currentPointerId;
	protected boolean			isPressed;
	private ComponentListener	clComponentListener;
	private RectangularShape	rsBackground;
	private float				secondsElapsedPressed;
	private float				secondsTotalForLongPress	= 0.75f;
	private boolean				isLongPressed;

	public BaseScreenComponent(float x, float y) {
		super(x, y);
	}

	public BaseScreenComponent(float x, float y, ITextureRegion textureRegion) {
		super(x, y);
		this.setBackground(new Sprite(0, 0, textureRegion, EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager()));
	}

	public BaseScreenComponent(float x, float y, ITiledTextureRegion tiledTextureRegion) {
		super(x, y);
		this.setBackground(new TiledSprite(0, 0, tiledTextureRegion, EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager()));
	}

	public boolean canCallPerformClick() {
		return true;
	}

	public RectangularShape getBackground() {
		return this.rsBackground;
	}

	@Override
	public float getBoundaryHeight() {
		return this.getHeightScaled();
	}

	@Override
	public float getBoundaryWidth() {
		return this.getWidthScaled();
	}

	@Override
	public float getBoundaryX() {
		final IEntity PARENT = this.getParent();
		final RectangularShape BACKGROUND = this.getBackground();
		return ((PARENT != null) ? PARENT.getX() : 0) + this.getX() + ((BACKGROUND != null) ? BACKGROUND.getX() : 0);
	}

	@Override
	public float getBoundaryY() {
		final IEntity PARENT = this.getParent();
		final RectangularShape BACKGROUND = this.getBackground();
		return ((PARENT != null) ? PARENT.getY() : 0) + this.getY() + ((BACKGROUND != null) ? BACKGROUND.getY() : 0);
	}

	public ComponentListener getComponentListener() {
		return this.clComponentListener;
	}

	public DialogScene getContext() {
		return this.dsContext;
	}

	public float getHeight() {
		if (this.rsBackground == null)
			return 0;
		else
			return this.rsBackground.getHeight();
	}

	public float getHeightScaled() {
		if (this.rsBackground == null)
			return 0;
		else
			return this.rsBackground.getHeightScaled() * this.getScaleY();
	}

	@Override
	public float getScaleCenterX() {
		if (this.rsBackground != null)
			return this.rsBackground.getScaleCenterX();
		return super.getScaleCenterX();
	}

	@Override
	public float getScaleCenterY() {
		if (this.rsBackground != null)
			return this.rsBackground.getScaleCenterY();
		return super.getScaleCenterY();
	}

	public float getSecondsTotalForLongPress() {
		return this.secondsTotalForLongPress;
	}

	public float getWidth() {
		if (this.rsBackground == null)
			return 0;
		else
			return this.rsBackground.getWidth();
	}

	public float getWidthScaled() {
		if (this.rsBackground == null)
			return 0;
		else
			return this.rsBackground.getWidthScaled() * this.getScaleX();
	}

	public void handleInput(TouchEvent touchEvent) {

		final float TOUCH_X = touchEvent.getX();
		final float TOUCH_Y = touchEvent.getY();

		if (touchEvent.isActionUp())
			if (this.isPressed && touchEvent.getPointerID() == this.currentPointerId)
				this.onTouchUp(TOUCH_X, TOUCH_Y);

		if (this.isTouchSuspended()) {
			this.isPressed = false;
			return;
		}

		if (this.isIntersectingWithPoint(TOUCH_X, TOUCH_Y) && touchEvent.isActionDown()) {

			if (!this.hasPointerId) {
				this.currentPointerId = touchEvent.getPointerID();
				this.hasPointerId = true;
			}

			if (this.currentPointerId == touchEvent.getPointerID())
				this.onPress(TOUCH_X, TOUCH_Y);

		}

		if (touchEvent.isActionMove())
			if (this.isPressed && touchEvent.getPointerID() == this.currentPointerId)
				this.onMove(TOUCH_X, TOUCH_Y);
	}

	protected boolean isAttached() {
		return this.isAttached;
	}

	public boolean isIntersectingWithPoint(float x, float y) {
		return x >= this.getBoundaryX() && y >= this.getBoundaryY() && x <= this.getBoundaryX() + this.getBoundaryWidth()
				&& y <= this.getBoundaryY() + this.getBoundaryHeight();
	}

	public boolean isLongPressed() {
		return this.isLongPressed;
	}

	public boolean isPressed() {
		return this.isPressed;
	}

	public boolean isTouchSuspended() {
		return this.suspendTouch;
	}

	@Override
	public void onAttached() {
		super.onAttached();
		this.isAttached = true;
	}

	@Override
	public void onDetached() {
		super.onDetached();
		this.isAttached = false;
	}

	@Override
	protected void onManagedUpdate(float pSecondsElapsed) {
		super.onManagedUpdate(pSecondsElapsed);
		if (this.isPressed) {
			this.secondsElapsedPressed += pSecondsElapsed;

			if (this.secondsElapsedPressed > this.secondsTotalForLongPress)
				if (!this.isLongPressed) {
					if (this.clComponentListener != null)
						this.clComponentListener.onComponentLongPress(this);
					this.isLongPressed = true;
				}
		}
	}

	protected void onMove(float x, float y) {
		if (this.clComponentListener != null)
			this.clComponentListener.onComponentMoved(this, x, y);
	}

	protected void onPress(float x, float y) {

		if (this.rsBackground instanceof TiledSprite)
			((TiledSprite) this.rsBackground).setCurrentTileIndex(1);

		if (this.clComponentListener != null)
			this.clComponentListener.onComponentPressed(this, x, y);

		this.isPressed = true;
	}

	public void onResume() {

	}

	public void onTouchUp(float x, float y) {
		if (this.isIntersectingWithPoint(x, y))
			if (this.canCallPerformClick()) {
				if (this.clComponentListener != null)
					this.clComponentListener.onComponentReleased(this, x, y);
				this.performClick();
			}
		this.resetTouch();
	}

	public void performClick() {
	}

	public void resetTouch() {
		if (this.rsBackground instanceof TiledSprite)
			((TiledSprite) this.rsBackground).setCurrentTileIndex(0);

		if (this.clComponentListener != null)
			this.clComponentListener.onComponentTouchStateReset(this);

		this.hasPointerId = false;
		this.isLongPressed = false;
		this.isPressed = false;
		this.secondsElapsedPressed = 0;
	}

	public void setBackground(RectangularShape background) {
		if (this.rsBackground != null)
			this.rsBackground.detachSelf();
		this.rsBackground = background;
		this.attachChild(this.rsBackground);
	}

	public void setComponentListener(ComponentListener listener) {
		this.clComponentListener = listener;
	}

	public void setContext(DialogScene context) {
		this.dsContext = context;
	}

	public void setSecondsTotalForLongPress(float totalSeconds) {
		this.secondsTotalForLongPress = totalSeconds;
	}

	public void setTouchSuspended(boolean touchSuspended) {
		this.suspendTouch = touchSuspended;
	}
}
