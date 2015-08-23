package com.partlight.ms.session.character.skin.zombie;

import org.andengine.util.adt.array.ArrayUtils;

import com.partlight.ms.Direction;
import com.partlight.ms.resource.ResourceManager.SharedCharRegions;
import com.partlight.ms.resource.ResourceManager.ZombieTextureRegions.Zombie01;
import com.partlight.ms.session.character.skin.CharacterSkin;

public class Zombie1CharacterSkin extends CharacterSkin {

	public static final float[][] SKIN = {
			new float[] {
					134f / 255f,
					145f / 255f,
					128f / 255f
			},
			new float[] {
					113f / 255f,
					123f / 255f,
					106f / 255f
			},
			new float[] {
					139f / 255f,
					128f / 255f,
					145f / 255f
			},
	};

	public static final float[][] TORSO = {
			new float[] {
					35f / 255f,
					35f / 255f,
					35f / 255f
			},
			new float[] {
					55f / 255f,
					35f / 255f,
					35f / 255f
			},
			new float[] {
					35f / 255f,
					55f / 255f,
					35f / 255f
			},
			new float[] {
					35f / 255f,
					35f / 255f,
					55f / 255f
			},
	};

	public static final float[][] LEGS = {
			new float[] {
					37f / 255f,
					37f / 255f,
					37f / 255f
			},
			new float[] {
					37f / 255f,
					37f / 255f,
					57f / 255f
			},
			new float[] {
					54f / 255f,
					51f / 255f,
					51f / 255f
			},
	};

	public Zombie1CharacterSkin(boolean useWalkAnimation) {
		super(SharedCharRegions.region_skin01, ((useWalkAnimation) ? SharedCharRegions.region_lb_walk : SharedCharRegions.region_lb),
				SharedCharRegions.region_ub[0], Zombie01.region_a01, null, null);
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
	public void revalidateColors() {
		final float[] SKIN = ArrayUtils.random(Zombie1CharacterSkin.SKIN);
		{
			this.getSkin().setColor(SKIN[0], SKIN[1], SKIN[2]);
			this.getArms().setColor(this.getSkin().getColor());
		}

		final float[] TORSO = ArrayUtils.random(Zombie1CharacterSkin.TORSO);
		{
			this.getTorso().setColor(TORSO[0], TORSO[1], TORSO[2]);
		}

		final float[] LEGS = ArrayUtils.random(Zombie1CharacterSkin.LEGS);
		{
			this.getLegs().setColor(LEGS[0], LEGS[1], LEGS[2]);
		}
	}
}
