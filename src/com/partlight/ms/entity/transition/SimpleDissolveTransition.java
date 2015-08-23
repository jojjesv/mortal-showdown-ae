package com.partlight.ms.entity.transition;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.modifier.ease.EaseSineInOut;
import org.andengine.util.modifier.ease.IEaseFunction;

import com.partlight.ms.resource.ResourceManager;
import com.partlight.ms.util.EntityUtils;

public class SimpleDissolveTransition extends DissolveTransition {

	private float			totalSecondsElapsed;
	private AlphaModifier	amFadeModifier;

	public SimpleDissolveTransition(VertexBufferObjectManager vertexBufferObjectManager) {
		super(vertexBufferObjectManager);
	}

	@Override
	public void animate(float duration, IEaseFunction ease) {
		ResourceManager.btDissolveMap.load();
		super.animate(duration, ease);
	}

	@Override
	protected void onAnimationFinish() {
		super.onAnimationFinish();
		ResourceManager.btDissolveMap.unload();
	}

	@Override
	protected void onManagedUpdate(float pSecondsElapsed) {
		super.onManagedUpdate(pSecondsElapsed);

		if (this.isAnimating()) {

			this.totalSecondsElapsed += pSecondsElapsed;

			if (this.totalSecondsElapsed > 0.75f)
				if (this.amFadeModifier == null) {
					this.amFadeModifier = new AlphaModifier(0.75f, 1f, 0f, EaseSineInOut.getInstance()) {
						@Override
						protected void onModifierFinished(IEntity pItem) {
							super.onModifierFinished(pItem);
							EntityUtils.safetlyDetach(SimpleDissolveTransition.this);
						}
					};
					this.amFadeModifier.setAutoUnregisterWhenFinished(true);
					this.registerEntityModifier(this.amFadeModifier);
				}
		}
	}
}
