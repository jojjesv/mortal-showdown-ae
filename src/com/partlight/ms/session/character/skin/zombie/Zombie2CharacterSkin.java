package com.partlight.ms.session.character.skin.zombie;

import org.andengine.util.adt.array.ArrayUtils;

import com.partlight.ms.Direction;
import com.partlight.ms.resource.ResourceManager.SharedCharRegions;
import com.partlight.ms.resource.ResourceManager.ZombieTextureRegions.Zombie01;
import com.partlight.ms.resource.ResourceManager.ZombieTextureRegions.Zombie02;
import com.partlight.ms.session.character.skin.CharacterSkin;

public class Zombie2CharacterSkin extends CharacterSkin {

	public static final float[][] SKIN = {
			new float[] {
					124f / 255f,
					49f / 255f,
					49f / 255f
			},
			new float[] {
					140f / 255f,
					48f / 255f,
					48f / 255f
			},
			new float[] {
					117f / 255f,
					38f / 255f,
					38f / 255f
			},
	};

	public static final float[][] LEGS = {
			new float[] {
					37f / 255f,
					37f / 255f,
					37f / 255f
			},
			new float[] {
					51f / 255f,
					51f / 255f,
					51f / 255f
			},
			new float[] {
					25f / 255f,
					25f / 255f,
					25f / 255f
			},
	};

	public static final float[][] HAIR_SLEEVES = {
			new float[] {
					41 / 255f,
					41 / 255f,
					41 / 255f
			},
			new float[] {
					60f / 255f,
					41f / 255f,
					41f / 255f
			},
			new float[] {
					41f / 255f,
					41f / 255f,
					60f / 255f
			},
	};

	private final boolean isWalking;

	public Zombie2CharacterSkin(boolean useWalkAnimation) {
		super(SharedCharRegions.region_skin01, ((useWalkAnimation) ? SharedCharRegions.region_lb_walk : SharedCharRegions.region_lb), null,
				Zombie01.region_a01, SharedCharRegions.region_sleeves[0], Zombie02.region_hair);
		this.isWalking = useWalkAnimation;
	}

	@Override
	@SuppressWarnings("incomplete-switch")
	protected int getIdleArmsTileIndex(Direction dir) {
		switch (dir) {
		case NORTH:
			return 0;
		case NORTHEAST:
		case NORTHWEST:
			return 5;
		case EAST:
		case WEST:
			return 10;
		case SOUTHEAST:
		case SOUTHWEST:
			return 15;
		case SOUTH:
			return 20;
		}
		return 0;
	}

	@Override
	protected long[] getLegsFrameDurations(int frames, long frameDuration) {
		if (this.isWalking)
			frameDuration /= (8.3f / 3.1f);

		return super.getLegsFrameDurations(frames, frameDuration);
	}

	@Override
	public void revalidateColors() {
		final float[] SKIN = ArrayUtils.random(Zombie2CharacterSkin.SKIN);
		{
			this.getSkin().setColor(SKIN[0], SKIN[1], SKIN[2]);
			this.getArms().setColor(this.getSkin().getColor());
		}

		final float[] LEGS = ArrayUtils.random(Zombie2CharacterSkin.LEGS);
		{
			this.getLegs().setColor(LEGS[0], LEGS[1], LEGS[2]);
		}

		final float[] HAIR_SLEEVES = ArrayUtils.random(Zombie2CharacterSkin.HAIR_SLEEVES);
		{
			this.getHair().setColor(HAIR_SLEEVES[0], HAIR_SLEEVES[1], HAIR_SLEEVES[2]);
			this.getSleeves().setColor(this.getHair().getColor());
		}
	}
}
