package com.partlight.ms.session.character.skin;

import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.partlight.ms.Direction;
import com.partlight.ms.entity.AlphaFriendlyEntity;
import com.partlight.ms.resource.EnvironmentVars;
import com.partlight.ms.session.character.Character;
import com.partlight.ms.shader.TintShaderProgram;
import com.partlight.ms.util.AnimationAdapter;

public class CharacterSkin extends AlphaFriendlyEntity {

	private boolean reanimateLegs;

	private DelayModifier dmLegsManualAnimation;

	private AnimatedSprite	asSleeves;
	private AnimatedSprite	asHair;
	private AnimatedSprite	asLegs;
	private AnimatedSprite	asSkin1;
	private AnimatedSprite	asSkin2;
	private AnimatedSprite	asTorso;

	public CharacterSkin() {

	}

	public CharacterSkin(ITiledTextureRegion skin1TextureRegion, ITiledTextureRegion legsTextureRegion,
			ITiledTextureRegion torsoTextureRegion, ITiledTextureRegion skin2TextureRegion, ITiledTextureRegion sleevesTextureRegion,
			ITiledTextureRegion hairTextureRegion) {
		this.revalidateBodyParts(skin1TextureRegion, legsTextureRegion, torsoTextureRegion, skin2TextureRegion, sleevesTextureRegion,
				hairTextureRegion);
		this.revalidateColors();
	}

	protected AnimatedSprite createBodyPart(ITiledTextureRegion textureRegion) {
		final AnimatedSprite AS = new AnimatedSprite(0f, 0f, textureRegion, EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager());
		AS.setShaderProgram(TintShaderProgram.getMultipliedInstance());
		return AS;
	}

	public void directSkin(Direction direction) {
		this.directSkin(direction, false, 1f);
	}

	@SuppressWarnings("incomplete-switch")
	public void directSkin(Direction direction, boolean move, float moveSpeed) {
		int torsoIndex = 0, legsIndex = 8;

		switch (direction) {

		case NORTH:
			torsoIndex = 0;
			legsIndex *= 0;
			break;

		case NORTHEAST:
		case NORTHWEST:
			torsoIndex = 1;
			legsIndex *= 1;
			break;

		case EAST:
		case WEST:
			torsoIndex = 2;
			legsIndex *= 2;
			break;

		case SOUTHEAST:
		case SOUTHWEST:
			torsoIndex = 3;
			legsIndex *= 3;
			break;

		case SOUTH:
			torsoIndex = 4;
			legsIndex *= 4;
			break;
		}

		if (move) {
			this.reanimateLegs = true;

			final int LEGS_INDEX = legsIndex;
			final int LEGS_INDEX_CURRENT = this.asLegs.getCurrentTileIndex();
			final int LEGS_FRAMES_LEFT = 8 - (LEGS_INDEX_CURRENT % 8);

			final long FRAME_DURATION = (long) (300 * (Character.MIN_SPEED / (moveSpeed / 2f)));
			final long[] FRAME_DURATIONS = this.getLegsFrameDurations(LEGS_FRAMES_LEFT, FRAME_DURATION);

			final boolean ONE_FRAME_LEFT = LEGS_FRAMES_LEFT == 1;

			if (ONE_FRAME_LEFT) {
				if (this.dmLegsManualAnimation != null)
					this.asLegs.unregisterEntityModifier(this.dmLegsManualAnimation);

				this.dmLegsManualAnimation = new DelayModifier(FRAME_DURATIONS[0] / 1000f) {
					@Override
					protected void onModifierFinished(org.andengine.entity.IEntity pItem) {
						CharacterSkin.this.asLegs.animate(CharacterSkin.this.getLegsFrameDurations(8, FRAME_DURATION), LEGS_INDEX,
								LEGS_INDEX + 7, true);
					};
				};
				this.dmLegsManualAnimation.setAutoUnregisterWhenFinished(true);

				this.asLegs.stopAnimation(LEGS_INDEX);
				this.asLegs.unregisterEntityModifier(this.dmLegsManualAnimation);
				this.asLegs.registerEntityModifier(this.dmLegsManualAnimation);
			} else if (LEGS_FRAMES_LEFT < 8 && LEGS_FRAMES_LEFT > 0)
				this.asLegs.animate(FRAME_DURATIONS, LEGS_INDEX + (8 - LEGS_FRAMES_LEFT), LEGS_INDEX + 7, false, new AnimationAdapter() {
					@Override
					public void onAnimationFinished(AnimatedSprite pAnimatedSprite) {
						if (CharacterSkin.this.reanimateLegs) {
							pAnimatedSprite.animate(CharacterSkin.this.getLegsFrameDurations(8, FRAME_DURATION), LEGS_INDEX, LEGS_INDEX + 7,
									true);
							CharacterSkin.this.reanimateLegs = false;
						}
					}
				});
			else
				this.asLegs.animate(FRAME_DURATIONS, LEGS_INDEX, LEGS_INDEX + 7, true);
		} else {
			if (this.dmLegsManualAnimation != null)
				this.asLegs.unregisterEntityModifier(this.dmLegsManualAnimation);
			this.asLegs.setCurrentTileIndex(Character.directionToStandIndex(direction));
		}

		final boolean FLIP_HORIZONTALLY = direction == Direction.NORTHWEST || direction == Direction.WEST
				|| direction == Direction.SOUTHWEST;

		if (this.asTorso != null) {
			this.asTorso.setCurrentTileIndex(torsoIndex);
			this.asTorso.setFlippedHorizontal(FLIP_HORIZONTALLY);
		}

		if (this.asHair != null) {
			this.asHair.setCurrentTileIndex(torsoIndex);
			this.asHair.setFlippedHorizontal(FLIP_HORIZONTALLY);
		}

		if (this.asSkin1 != null) {
			this.asSkin1.setCurrentTileIndex(torsoIndex);
			this.asSkin1.setFlippedHorizontal(FLIP_HORIZONTALLY);
		}

		if (this.asSkin2 != null) {
			this.asSkin2.setCurrentTileIndex(this.getIdleArmsTileIndex(direction));
			this.asSkin2.setFlippedHorizontal(FLIP_HORIZONTALLY);
		}

		if (this.asSleeves != null) {
			this.asSleeves.setCurrentTileIndex(this.asSkin2.getCurrentTileIndex());
			this.asSleeves.setFlippedHorizontal(FLIP_HORIZONTALLY);
		}

		this.asLegs.setFlippedHorizontal(FLIP_HORIZONTALLY);
	}

	public AnimatedSprite getArms() {
		return this.asSkin2;
	}

	public AnimatedSprite getHair() {
		return this.asHair;
	}

	protected int getIdleArmsTileIndex(Direction dir) {
		return 0;
	}

	public AnimatedSprite getLegs() {
		return this.asLegs;
	}

	protected long[] getLegsFrameDurations(int frames, long frameDuration) {
		final long[] OUT = new long[frames];

		for (int i = 0; i < OUT.length; i++)
			OUT[i] = frameDuration;

		return OUT;
	}

	public AnimatedSprite getSkin() {
		return this.asSkin1;
	}

	public AnimatedSprite getSleeves() {
		return this.asSleeves;
	}

	public AnimatedSprite getTorso() {
		return this.asTorso;
	}

	public void loadTextures() {
		if (this.asHair != null)
			this.asHair.getTextureRegion().getTexture().load();

		if (this.asSkin1 != null)
			this.asSkin1.getTextureRegion().getTexture().load();

		if (this.asSkin2 != null)
			this.asSkin2.getTextureRegion().getTexture().load();

		if (this.asSleeves != null)
			this.asSleeves.getTextureRegion().getTexture().load();

		if (this.asTorso != null)
			this.asTorso.getTextureRegion().getTexture().load();

		if (this.asLegs != null)
			this.asLegs.getTextureRegion().getTexture().load();
	}

	public void revalidateBodyParts(ITiledTextureRegion skin1TextureRegion, ITiledTextureRegion legsTextureRegion,
			ITiledTextureRegion torsoTextureRegion, ITiledTextureRegion skin2TextureRegion, ITiledTextureRegion sleevesTextureRegion,
			ITiledTextureRegion hairTextureRegion) {
		this.detachChildren();

		final VertexBufferObjectManager vbom = EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager();

		if (skin1TextureRegion != null)
			this.attachChild(this.asSkin1 = this.createBodyPart(skin1TextureRegion));

		if (legsTextureRegion != null)
			this.attachChild(this.asLegs = this.createBodyPart(legsTextureRegion));

		if (torsoTextureRegion != null)
			this.attachChild(this.asTorso = this.createBodyPart(torsoTextureRegion));

		if (skin2TextureRegion != null)
			this.attachChild(this.asSkin2 = new AnimatedSprite(0, 0, skin2TextureRegion, vbom) {
				@Override
				public void setCurrentTileIndex(int pCurrentTileIndex) {
					super.setCurrentTileIndex(pCurrentTileIndex);
					if (CharacterSkin.this.asSleeves != null)
						if (pCurrentTileIndex > 6)
							CharacterSkin.this.asSleeves.setCurrentTileIndex(pCurrentTileIndex - 6);
						else
							CharacterSkin.this.asSleeves.setCurrentTileIndex(pCurrentTileIndex);
				}
			});

		if (sleevesTextureRegion != null)
			this.attachChild(this.asSleeves = this.createBodyPart(sleevesTextureRegion));

		if (hairTextureRegion != null)
			this.attachChild(this.asHair = this.createBodyPart(hairTextureRegion));
	}

	public void revalidateColors() {

	}

	public void setAllCurrentTileIndex(int tileIndex) {
		if (this.asTorso != null)
			this.asTorso.setCurrentTileIndex(tileIndex);

		if (this.asLegs != null)
			this.asLegs.setCurrentTileIndex(tileIndex);

		if (this.asHair != null)
			this.asHair.setCurrentTileIndex(tileIndex);

		if (this.asSkin1 != null)
			this.asSkin1.setCurrentTileIndex(tileIndex);

		if (this.asSkin2 != null)
			this.asSkin2.setCurrentTileIndex(tileIndex);

		if (this.asSleeves != null)
			this.asSleeves.setCurrentTileIndex(tileIndex);
	}

	public void setArms(AnimatedSprite newArms) {
		if (this.asSkin2 != null)
			this.asSkin2.detachSelf();
		this.asSkin2 = newArms;
		this.asSkin2.setFlippedHorizontal(this.asTorso.isFlippedHorizontal());
		this.asSkin2.setX(0f);
		this.asSkin2.setY(0f);
		this.asSkin2.setVisible(this.isVisible());
		this.asSkin2.setAlpha(this.getAlpha());
		newArms.detachSelf();
		this.attachChild(this.asSkin2);

		this.revalidateColors();
	}

	public void setColorsFrom(CharacterSkin otherSkin) {
		if (this.asTorso != null && otherSkin.asTorso != null)
			this.asTorso.setColor(otherSkin.getTorso().getColor());

		if (this.asLegs != null && otherSkin.asLegs != null)
			this.asLegs.setColor(otherSkin.getLegs().getColor());

		if (this.asHair != null && otherSkin.asHair != null)
			this.asHair.setColor(otherSkin.getHair().getColor());

		if (this.asSkin1 != null && otherSkin.asSkin1 != null)
			this.asSkin1.setColor(otherSkin.getSkin().getColor());

		if (this.asSkin2 != null && otherSkin.asSkin2 != null)
			this.asSkin2.setColor(otherSkin.getArms().getColor());

		if (this.asSleeves != null && otherSkin.asSleeves != null)
			this.asSleeves.setColor(otherSkin.getSleeves().getColor());
	}

	public void unloadTextures() {
		if (this.asHair != null)
			this.asHair.getTextureRegion().getTexture().unload();
		if (this.asSkin1 != null)
			this.asSkin1.getTextureRegion().getTexture().unload();
		if (this.asSkin2 != null)
			this.asSkin2.getTextureRegion().getTexture().unload();
		if (this.asSleeves != null)
			this.asSleeves.getTextureRegion().getTexture().unload();
		if (this.asTorso != null)
			this.asTorso.getTextureRegion().getTexture().unload();
		if (this.asLegs != null)
			this.asLegs.getTextureRegion().getTexture().unload();
	}
}
