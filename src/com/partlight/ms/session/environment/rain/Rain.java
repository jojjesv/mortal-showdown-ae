package com.partlight.ms.session.environment.rain;

import org.andengine.entity.particle.SpriteParticleSystem;
import org.andengine.entity.particle.emitter.IParticleEmitter;
import org.andengine.entity.particle.emitter.RectangleParticleEmitter;
import org.andengine.entity.particle.initializer.VelocityParticleInitializer;
import org.andengine.entity.particle.modifier.ExpireParticleInitializer;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;

import com.partlight.ms.entity.AlphaFriendlyEntity;
import com.partlight.ms.resource.EnvironmentVars;
import com.partlight.ms.resource.ResourceManager;
import com.partlight.ms.resource.ResourceManager.HudRegions;
import com.partlight.ms.util.Ruler;

public class Rain extends AlphaFriendlyEntity {

	private static final float RAIN_VELOCITY = 600f;

	private final SpriteParticleSystem[]	spsRainParticleSystems;
	private boolean							hasUpdatedParticleSystems;

	public Rain() {
		this.spsRainParticleSystems = new SpriteParticleSystem[HudRegions.region_rain.length];

		for (int i = 0; i < this.spsRainParticleSystems.length; i++) {
			this.spsRainParticleSystems[i] = this.createSpriteParticleSystem(HudRegions.region_rain[i]);
			this.attachChild(this.spsRainParticleSystems[i]);
		}
	}

	private SpriteParticleSystem createSpriteParticleSystem(ITextureRegion textureRegion) {

		final float X_VELOCITY = Rain.RAIN_VELOCITY * (float) Math.cos(Math.toRadians(120f));
		final float Y_VELOCITY = Rain.RAIN_VELOCITY;
		final float PARTICLE_TIME_ALIVE = 60f / (EnvironmentVars.MAIN_CONTEXT.height() / (Rain.RAIN_VELOCITY * (1f / 60f)));

		//@formatter:off
		final float EMITTER_WIDTH = EnvironmentVars.MAIN_CONTEXT.width()
				+ (float) Math.sqrt(
						Math.pow(
								Ruler.getDistance(
										0f,
										0f,
										(float) Math.cos(Math.toRadians(120f)) * EnvironmentVars.MAIN_CONTEXT.height(),
										EnvironmentVars.MAIN_CONTEXT.height())
								, 2)
						- Math.pow(
								EnvironmentVars.MAIN_CONTEXT.height(),
								2));
		//@formatter:on

		final IParticleEmitter PARTICLE_EMITTER = new RectangleParticleEmitter(EMITTER_WIDTH / 2f, -textureRegion.getHeight(),
				EMITTER_WIDTH, 1f);

		final SpriteParticleSystem PARTICLE_SYSTEM = new SpriteParticleSystem(PARTICLE_EMITTER, 27.5f, 30f, 100, textureRegion,
				EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager());

		PARTICLE_SYSTEM.addParticleInitializer(new VelocityParticleInitializer<Sprite>(X_VELOCITY, Y_VELOCITY));
		PARTICLE_SYSTEM.addParticleInitializer(new ExpireParticleInitializer<Sprite>(PARTICLE_TIME_ALIVE));

		return PARTICLE_SYSTEM;
	}

	@Override
	public void onAttached() {
		super.onAttached();
		ResourceManager.btRain.load();
		ResourceManager.sRain.play();
	}

	@Override
	public void onDetached() {
		super.onDetached();
		ResourceManager.btRain.unload();
		ResourceManager.sRain.pause();
	}

	@Override
	protected void onManagedUpdate(float pSecondsElapsed) {
		if (!this.hasUpdatedParticleSystems && this.spsRainParticleSystems != null) {
			for (int ticks = 0; ticks < 60; ticks++)
				for (final SpriteParticleSystem sps : this.spsRainParticleSystems)
					sps.onUpdate(pSecondsElapsed);
			this.hasUpdatedParticleSystems = true;
		}

		super.onManagedUpdate(pSecondsElapsed);
	}
}
