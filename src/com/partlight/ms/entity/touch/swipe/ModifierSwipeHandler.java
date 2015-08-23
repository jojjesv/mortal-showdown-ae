package com.partlight.ms.entity.touch.swipe;

import org.andengine.entity.Entity;
import org.andengine.util.modifier.ease.EaseSineInOut;

import com.partlight.ms.util.EntityUtils;
import com.partlight.ms.util.updatehandler.FloatValueModifier;

public class ModifierSwipeHandler extends SwipeHandler {

	private FloatValueModifier	fvmTransformation;
	private float				transformationPercent;
	private final Entity		eParent;

	public ModifierSwipeHandler(Entity parent, float maxDistance, SwipeDirections... directions) {
		super(maxDistance, directions);
		this.eParent = parent;
	}

	public float getTransformationPercent() {
		return this.transformationPercent;
	}

	@Override
	public void onPercentageChange() {
		super.onPercentageChange();

		if (this.isSwiping()) {
			this.transformationPercent = this.isSwipingHorizontally() ? this.getXSwipePercent() : this.getYSwipePercent();
			this.onTransformationPercentChanged(this.transformationPercent);

			if (this.fvmTransformation != null)
				this.eParent.unregisterUpdateHandler(this.fvmTransformation);
		}
	}

	@Override
	public void onSwipeCanceled() {
		super.onSwipeCanceled();

		if (this.transformationPercent == 0f)
			return;

		if (this.fvmTransformation != null)
			this.eParent.unregisterUpdateHandler(this.fvmTransformation);

		this.fvmTransformation = new FloatValueModifier(this.transformationPercent, 0f, EaseSineInOut.getInstance(), 0.25f) {
			@Override
			protected void onFinished() {
				super.onFinished();
				ModifierSwipeHandler.this.onTransformationFinished();
				EntityUtils.safetlyUnregisterUpdateHandler(ModifierSwipeHandler.this.eParent, ModifierSwipeHandler.this.fvmTransformation);
			}

			@Override
			protected void onValueChanged(float value) {
				super.onValueChanged(value);
				ModifierSwipeHandler.this.transformationPercent = value;
				ModifierSwipeHandler.this.onTransformationPercentChanged(value);
			}
		};

		this.fvmTransformation.setFrom(this.transformationPercent);
		this.fvmTransformation.reset();
		this.eParent.registerUpdateHandler(this.fvmTransformation);

	}

	public void onTransformationFinished() {

	}

	public void onTransformationPercentChanged(float percent) {

	}

	@Override
	public void release() {
		super.release();
		this.onSwipeCanceled();
	}

	@Override
	public void reset() {
		super.reset();
		this.transformationPercent = 0f;
	}
}
