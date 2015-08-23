package com.partlight.ms.session.character.skin.player;

import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.opengl.texture.region.ITiledTextureRegion;

import com.partlight.ms.Direction;
import com.partlight.ms.resource.EnvironmentVars.StaticData;
import com.partlight.ms.resource.ResourceManager.PlayerRegions;
import com.partlight.ms.resource.ResourceManager.SharedCharRegions;
import com.partlight.ms.scene.mainmenu.MainMenuStore;
import com.partlight.ms.session.character.skin.CharacterSkin;

public class PlayerCharacterSkin extends CharacterSkin {

	public PlayerCharacterSkin() {
		this.revalidateBodyParts();
		this.revalidateColors();
	}

	@SuppressWarnings("incomplete-switch")
	@Override
	protected int getIdleArmsTileIndex(Direction dir) {
		switch (dir) {
		case NORTH:
			return 0;
		case NORTHEAST:
		case NORTHWEST:
			return 1;
		case EAST:
		case WEST:
			return 2;
		case SOUTHEAST:
		case SOUTHWEST:
			return 3;
		case SOUTH:
			return 4;
		}
		return 0;
	}

	public void revalidateBodyParts() {
		StaticData.playerSleevesIndex = MainMenuStore.TORSO_SLEEVES_INDICIES[StaticData.playerTorsoIndex];

		int actualTorsoIndex = 0;

		switch (StaticData.playerTorsoIndex) {
		case 2:
			actualTorsoIndex = 0;
			break;

		default:
			actualTorsoIndex = StaticData.playerTorsoIndex;
			break;
		}

		super.revalidateBodyParts(SharedCharRegions.region_skin01, SharedCharRegions.region_lb,
				SharedCharRegions.region_ub[actualTorsoIndex], PlayerRegions.region_a01,
				PlayerRegions.region_sleeves[StaticData.playerSleevesIndex], SharedCharRegions.region_hair[StaticData.playerHairIndex]);
	}

	@Deprecated
	@Override
	public void revalidateBodyParts(ITiledTextureRegion skin1TextureRegion, ITiledTextureRegion legsTextureRegion,
			ITiledTextureRegion torsoTextureRegion, ITiledTextureRegion skin2TextureRegion, ITiledTextureRegion sleevesTextureRegion,
			ITiledTextureRegion hairTextureRegion) {
		super.revalidateBodyParts(skin1TextureRegion, legsTextureRegion, torsoTextureRegion, skin2TextureRegion, sleevesTextureRegion,
				hairTextureRegion);
	}

	@Override
	public void revalidateColors() {
		final AnimatedSprite SLEEVES = this.getSleeves();
		final AnimatedSprite TORSO = this.getTorso();

		switch (StaticData.playerTorsoIndex) {
		case 1:
		case 2:
			StaticData.playerSleevesR = StaticData.playerTorsoR;
			StaticData.playerSleevesG = StaticData.playerTorsoG;
			StaticData.playerSleevesB = StaticData.playerTorsoB;
			break;
		}

		this.getLegs().setColor(StaticData.playerLegsR, StaticData.playerLegsG, StaticData.playerLegsB);
		this.getHair().setColor(StaticData.playerHairR, StaticData.playerHairG, StaticData.playerHairB);

		if (TORSO != null)
			TORSO.setColor(StaticData.playerTorsoR, StaticData.playerTorsoG, StaticData.playerTorsoB);

		this.getSkin().setColor(StaticData.playerSkinR, StaticData.playerSkinG, StaticData.playerSkinB);
		this.getArms().setColor(this.getSkin().getColor());

		if (SLEEVES != null)
			SLEEVES.setColor(StaticData.playerSleevesR, StaticData.playerSleevesG, StaticData.playerSleevesB);
	}
}
