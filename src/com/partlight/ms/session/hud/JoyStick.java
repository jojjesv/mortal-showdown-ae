package com.partlight.ms.session.hud;

import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;

import com.partlight.ms.Direction;
import com.partlight.ms.resource.EnvironmentVars;
import com.partlight.ms.session.character.Character;
import com.partlight.ms.util.Ruler;

public class JoyStick extends BaseScreenComponent {

	public interface OnDirectionChangeListener {
		public void onDirectionChange(Direction newDirection, Direction oldDirection);
	}

	public interface OnStickPositionChangeListener {
		public void onStickPositionChange(float distance);
	}

	private static float					stickX;
	private static float					stickY;
	public static final float				DEADZONE	= 2f;
	public static final float				MAX			= 38f;
	private Direction						dDirection;
	private final Sprite					sStick;
	private float							distance;
	public OnDirectionChangeListener		onDirectionChange;
	public OnStickPositionChangeListener	onStickPositionChange;

	public JoyStick(float x, float y, ITextureRegion background, ITextureRegion foreground) {
		super(x, y, background);

		JoyStick.stickX = (background.getWidth() - foreground.getWidth()) / 2f;
		JoyStick.stickY = (background.getHeight() - foreground.getHeight()) / 2f;

		this.sStick = new Sprite(JoyStick.stickX, JoyStick.stickY, foreground, EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager());
		this.attachChild(this.sStick);

		this.dDirection = Direction.NONE;
	}

	@Override
	public float getBoundaryHeight() {
		return this.getHeightScaled() + 64f;
	}

	@Override
	public float getBoundaryWidth() {
		return this.getWidthScaled() + 64f;
	}

	@Override
	public float getBoundaryX() {
		return this.getX() - 32f;
	}

	@Override
	public float getBoundaryY() {
		return this.getY() - 32f;
	}

	public Direction getDirection() {
		return this.dDirection;
	}

	@Override
	public float getScaleCenterX() {
		return 0;
	}

	@Override
	public float getScaleCenterY() {
		return 0;
	}

	public Sprite getStick() {
		return this.sStick;
	}

	/**
	 * @return The distance between the stick and it's inital position.
	 */
	public float getStickDistance() {
		return this.distance;
	}

	@Override
	protected void onMove(float x, float y) {

		// Position stick
		{
			float newStickX = x - JoyStick.stickX - this.getX();
			float newStickY = y - JoyStick.stickY - this.getY();

			newStickX /= this.getScaleX();
			newStickY /= this.getScaleY();

			this.sStick.setPosition(newStickX, newStickY);
		}

		this.distance = Ruler.getDistance(JoyStick.stickX, JoyStick.stickY, this.sStick.getX(), this.sStick.getY());

		Direction newDirection = Direction.NONE;

		final float angle = (float) Math.toDegrees(Math.atan2(this.sStick.getY() - JoyStick.stickY, this.sStick.getX() - JoyStick.stickX));

		if (this.distance > JoyStick.DEADZONE) {
			newDirection = Character.angleToDirection((int) angle);
			if (this.distance > JoyStick.MAX) {
				this.sStick.setX(JoyStick.stickX + (float) Math.cos(Math.toRadians(angle)) * JoyStick.MAX);
				this.sStick.setY(JoyStick.stickY + (float) Math.sin(Math.toRadians(angle)) * JoyStick.MAX);
			}
		}

		this.setDirection(newDirection);

		if (this.onStickPositionChange != null)
			this.onStickPositionChange.onStickPositionChange(this.distance);

		super.onMove(x, y);
	}

	@Override
	public void onTouchUp(float x, float y) {

		this.dDirection = Direction.NONE;

		if (this.onStickPositionChange != null)
			this.onStickPositionChange.onStickPositionChange(0f);

		this.sStick.setX(JoyStick.stickX);
		this.sStick.setY(JoyStick.stickY);

		super.onTouchUp(x, y);
	}

	public void resetStick() {
		this.onTouchUp(0f, 0f);
	}

	@Override
	public void setAlpha(float pAlpha) {
		this.sStick.setAlpha(pAlpha);
		super.setAlpha(pAlpha);
	}

	protected void setDirection(Direction newDirection) {
		if (newDirection.ordinal() != this.getDirection().ordinal())
			if (this.onDirectionChange != null)
				this.onDirectionChange.onDirectionChange(newDirection, this.dDirection);
		this.dDirection = newDirection;
	}

	public void setOnDirectionChange(OnDirectionChangeListener onDirectionChange) {
		this.onDirectionChange = onDirectionChange;
	}

	public void setOnStickPositionChange(OnStickPositionChangeListener onStickPositionChange) {
		this.onStickPositionChange = onStickPositionChange;
	}
}
