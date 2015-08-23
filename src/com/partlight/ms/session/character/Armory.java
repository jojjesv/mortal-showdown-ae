package com.partlight.ms.session.character;

import org.andengine.entity.sprite.Sprite;

import com.partlight.ms.resource.EnvironmentVars;
import com.partlight.ms.resource.ResourceManager;
import com.partlight.ms.resource.ResourceManager.HudRegions;
import com.partlight.ms.session.character.player.weapons.Firearm;
import com.partlight.ms.session.character.player.weapons.Grenade;
import com.partlight.ms.session.character.player.weapons.Tossable;
import com.partlight.ms.session.character.player.weapons.TossableAmmoManager;

public class Armory {

	public static final Firearm[]	WEP_ARRAY;
	public static final int			WEP_PISTOL		= 0;
	public static final int			WEP_SMG			= 1;
	public static final int			WEP_DOMINADOR	= 2;
	public static final int			WEP_SHOTGUN		= 3;
	public static final int			WEP_SNIPER		= 4;
	public static final int			WEP_CALTROP		= 5;
	public static final int			WEP_GRENADE		= 6;

	static {

		//@formatter:off
		WEP_ARRAY = new Firearm[] {
			new Firearm(Armory.WEP_PISTOL, "PISTOL", 128f, 8, false, 0.25f, 1, 0),
			new Firearm(Armory.WEP_SMG, "SMG", 128f, 5, true, 0.105f, 1, 0.05f, 1, 0),
			new Firearm(Armory.WEP_DOMINADOR, "EL DOMINADOR", 164f, 25, false, 0.375f, 1, 0),
			new Firearm(Armory.WEP_SHOTGUN, "SHOTGUN", 96f, 30, false, 0.5f, 2, 0.6f, 2, 0),
			new Firearm(Armory.WEP_SNIPER, "LR-SR", 640f, 35, false, 0.8f, 1, 0f, 1, 0),
			new Tossable(Armory.WEP_CALTROP, "CALTROP", 64f, 21, false, 0.45f, 1, 0.15f),
			new Grenade(Armory.WEP_GRENADE, "GRENADE", 128f, 32, false, 0.45f, 1, 0.15f),
		};
		//@formatter:on
	}

	public static final void onVertexBufferObjectManagerInitialised() {

		final TossableAmmoManager CALTROP_AMMO_MANAGER = new TossableAmmoManager((Tossable) Armory.WEP_ARRAY[Armory.WEP_CALTROP], 16,
				EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager(), HudRegions.region_projectile_map.getTextureRegion(0));
		final TossableAmmoManager GRENADE_AMMO_MANAGER = new TossableAmmoManager((Grenade) Armory.WEP_ARRAY[Armory.WEP_GRENADE], 16,
				EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager(), HudRegions.region_projectile_map.getTextureRegion(1)) {
			@Override
			protected void onUpdateProjectile(Sprite projectile, float secondsElapsed, float totalSecondsElapsed) {
				if (totalSecondsElapsed >= 2f)
					this.remove(projectile);
			}
		};

		((Tossable) Armory.WEP_ARRAY[Armory.WEP_CALTROP]).setAmmoManager(CALTROP_AMMO_MANAGER);
		((Tossable) Armory.WEP_ARRAY[Armory.WEP_GRENADE]).setAmmoManager(GRENADE_AMMO_MANAGER);

		Armory.WEP_ARRAY[Armory.WEP_PISTOL].setFireSounds(ResourceManager.sFire0);
		Armory.WEP_ARRAY[Armory.WEP_SMG].setFireSounds(ResourceManager.sFire1);
		Armory.WEP_ARRAY[Armory.WEP_DOMINADOR].setFireSounds(ResourceManager.sFire2);
		Armory.WEP_ARRAY[Armory.WEP_SHOTGUN].setFireSounds(ResourceManager.sFire3);
		Armory.WEP_ARRAY[Armory.WEP_SNIPER].setFireSounds(ResourceManager.sFire4);
		Armory.WEP_ARRAY[Armory.WEP_CALTROP].setFireSounds(ResourceManager.sThrow);
		Armory.WEP_ARRAY[Armory.WEP_GRENADE].setFireSounds(ResourceManager.sThrow);
	}
}
