package com.partlight.ms.scene.mainmenu;

import org.andengine.opengl.texture.region.ITiledTextureRegion;

import com.partlight.ms.resource.ResourceManager.HudRegions;
import com.partlight.ms.resource.ResourceManager.StoreTextureRegions;

public class MainMenuStore {

	public static final int	ID_DOMINADOR_REPEATED_FIRE	= 0;
	public static final int	ID_LASER_SIGHT				= 1;
	public static final int	ID_GLITCH_CLIP				= 2;
	public static final int	ID_INJECTION_TIPS			= 3;
	public static final int	ID_WARDROBE					= 0;
	public static final int	ID_DYE_APPLICATOR			= 1;
	public static final int	ID_CLOTH_DYE				= 2;

	private static MainMenuStoreTab[]	mmstTabs;
	public static final String			ITEM_CONFIRMATION	= "PURCHASE FOR %s PARTS?\n\n\n\n ";
	public static final int				STORE_TABS			= 2;
	public static final int				CLOTH_DYE_PACK		= 5;

	public static final int[]		HAIR_PRICES			= {
			0,
			2,
															};
	public static final String[]	HAIR_TITLES			= {
			"",
			"IVY LEAGUE",
															};
	public static final String		HAIR_DESCRIPTION	= "A TYPE OF HAIRSTYLE";

	public static final String[]	TORSO_TITLES			= {
			"",
			"OPEN SHIRT",
			"CLOSED SHIRT",
																};
	public static final String		TORSO_DESCRIPTION		= "UPPER BODY CLOTHING";
	public static final int[]		TORSO_PRICES			= {
			0,
			2,
			2,
																};
	public static final int[]		TORSO_SLEEVES_INDICIES	= {
			0,
			1,
			1,
																};

	public static final MainMenuStoreTab getTab(int index) {
		if (MainMenuStore.mmstTabs == null) {
			MainMenuStore.mmstTabs = new MainMenuStoreTab[MainMenuStore.STORE_TABS];

			//@formatter:off
			MainMenuStore.mmstTabs[0] = new MainMenuStoreTab(
					"WEAPONRY",
					new Integer[]{
						5,
						10,
						12,
						13,
					},
					new String[]{
						"REPEATED FIRE",
						"LASER SIGHT",
						"GLITCH CLIP",
						"INJECTION TIPS",
					},
					new String[]{
							"FOR EL DOMINADOR",
							"33% LONGER RANGE",
							"25% CHANCE OF NOT CONSUMING AMMO",
							"50% CHANCE OF DISORIENTING TARGET",
					},
					new Boolean[]{
						false,
						true,
						true,
						false,		
					},
					new ITiledTextureRegion[]{
						HudRegions.region_weps,
						StoreTextureRegions.region_icon_laser,
						StoreTextureRegions.region_icon_glitch_clip,
						HudRegions.region_weps
					}
			);
			
			MainMenuStore.mmstTabs[1] = new MainMenuStoreTab(
					"PERSONALIZE",
					new Integer[]{
						0,
						5,
						2,
					},
					new String[]{
						"WARDROBE",
						"DYE APPLICATOR",
						"CLOTH DYE (X" + MainMenuStore.CLOTH_DYE_PACK + ")",
					},
					new String[]{
						"TOUCH TO CHANGE STYLE",
						"FABULOUSLY DYE YOUR CLOTHES|TOUCH TO APPLY DYES",	
						"REQUIRES AN APPLICATOR",	
					},
					new Boolean[]{
						false,
						false,
						false,
					},
					new ITiledTextureRegion[]{
						StoreTextureRegions.region_icon_wardrobe,
						StoreTextureRegions.region_icon_dye,
						StoreTextureRegions.region_icon_dye_pack,
					}
			);
			//@formatter:on
		}

		return MainMenuStore.mmstTabs[index];
	}
}
