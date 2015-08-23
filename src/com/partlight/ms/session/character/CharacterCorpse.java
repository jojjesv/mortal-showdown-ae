package com.partlight.ms.session.character;

import java.util.HashMap;
import java.util.Random;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.util.modifier.ease.EaseSineInOut;

import com.partlight.ms.entity.AlphaFriendlyEntity;
import com.partlight.ms.resource.ResourceManager.SharedCharRegions;
import com.partlight.ms.resource.ResourceManager.ZombieTextureRegions.Zombie02;
import com.partlight.ms.session.character.skin.CharacterSkin;
import com.partlight.ms.util.EntityUtils;

public class CharacterCorpse extends AlphaFriendlyEntity {

	private static final HashMap<ITiledTextureRegion, ITiledTextureRegion> CORPSE_COUNTER_PARTS;

	static {
		CORPSE_COUNTER_PARTS = new HashMap<ITiledTextureRegion, ITiledTextureRegion>();
	}

	public static final void onTexturesLoaded() {
		CharacterCorpse.CORPSE_COUNTER_PARTS.put(SharedCharRegions.region_hair[0], SharedCharRegions.region_hair_d[0]);
		CharacterCorpse.CORPSE_COUNTER_PARTS.put(SharedCharRegions.region_hair[1], SharedCharRegions.region_hair_d[0]);
		CharacterCorpse.CORPSE_COUNTER_PARTS.put(SharedCharRegions.region_skin01, SharedCharRegions.region_skin01_d);
		CharacterCorpse.CORPSE_COUNTER_PARTS.put(SharedCharRegions.region_sleeves[0], SharedCharRegions.region_sleeves_d[0]);
		CharacterCorpse.CORPSE_COUNTER_PARTS.put(SharedCharRegions.region_ub[0], SharedCharRegions.region_ub_d[0]);
		CharacterCorpse.CORPSE_COUNTER_PARTS.put(SharedCharRegions.region_ub[1], SharedCharRegions.region_ub_d[0]);
		CharacterCorpse.CORPSE_COUNTER_PARTS.put(Zombie02.region_hair, Zombie02.region_hair_d);
	}

	private final DelayModifier dmDelay;

	private final AlphaModifier amFadeOut;

	private final CharacterSkin csSkin;

	public CharacterCorpse(Character contextCharacter) {
		super(contextCharacter.getX(), contextCharacter.getY() + 8);

		final CharacterSkin SKIN = contextCharacter.getSkin();
		//@formatter:off
		this.csSkin = new CharacterSkin(
				CharacterCorpse.CORPSE_COUNTER_PARTS.get(SKIN.getSkin().getTiledTextureRegion()),
				SharedCharRegions.region_lb_d,
				((SKIN.getTorso() == null) ? null : CharacterCorpse.CORPSE_COUNTER_PARTS.get(SKIN.getTorso().getTiledTextureRegion())),
				((SKIN.getArms() == null) ? null : SharedCharRegions.region_a_d),
				((SKIN.getSleeves() == null) ? null : CharacterCorpse.CORPSE_COUNTER_PARTS.get(SKIN.getSleeves().getTiledTextureRegion())),
				((SKIN.getHair() == null) ? null : CharacterCorpse.CORPSE_COUNTER_PARTS.get(SKIN.getHair().getTiledTextureRegion())));

		//@formatter:on

		this.csSkin.setColorsFrom(SKIN);
		this.csSkin.setAllCurrentTileIndex(new Random().nextInt(4));

		this.csSkin.loadTextures();

		this.attachChild(this.csSkin);

		this.dmDelay = new DelayModifier(2.5f) {
			@Override
			protected void onModifierFinished(org.andengine.entity.IEntity pItem) {
				super.onModifierFinished(pItem);
				CharacterCorpse.this.registerEntityModifier(CharacterCorpse.this.amFadeOut);
			};
		};

		this.amFadeOut = new AlphaModifier(0.5f, 1f, 0f, EaseSineInOut.getInstance()) {
			@Override
			protected void onModifierFinished(IEntity pItem) {
				super.onModifierFinished(pItem);
				EntityUtils.safetlyDetachAndDispose(CharacterCorpse.this);
			}
		};

		this.dmDelay.setAutoUnregisterWhenFinished(true);
		this.amFadeOut.setAutoUnregisterWhenFinished(true);

		this.registerEntityModifier(this.dmDelay);
	}
}
