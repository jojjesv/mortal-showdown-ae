package com.partlight.ms.session.hud.notification;

import org.andengine.util.color.Color;

import com.partlight.ms.session.hud.ComboTracker.NotificationConstants;

public class NotificationRewards {

	public static final String[]	NOTIFICATION_DESCRIPTIONS;
	public static final int[]		NOTIFICATION_MULTIPLIERS;
	public static final Color[]		NOTIFICATION_COLORS;

	static {
		//@formatter:off
		NOTIFICATION_DESCRIPTIONS = new String[] {
				"PISTOL - REPEATED FIRE",
				"NEW WEAPON - SMG",
				"PISTOL - INCREASED DAMAGE",
				"NEW WEAPON - EL DOMINADOR",
				"SMG - INCREASED CLIP SIZE",
				"EL DOMINADOR - INCREASED DAMAGE",
				"NEW WEAPON - SHOTGUN",
				"NEW WEAPON - LR-SR",
				"NEW WEAPON - CALTROP",
				"SHOTGUN - AUTOMATIC FIRE",
				"NEW WEAPON - GRENADE",
				"LR-SR - INCREASED FIRING RATE",
				"GRENADE - INCREASED DAMAGE",
				"LR-SR - EXPLOSIVE TIPS",
				"SHOTGUN - INCREASED CLIP SIZE",
		};
		
		NOTIFICATION_MULTIPLIERS = new int[] {
//				2,
//				3,
//				4,
//				5,
//				6,
//				7,
//				8,
//				9,
//				10,
//				11,
//				12,
//				13,
//				14,
//				15,
//				16,
				3,
				6,
				10,
				16,
				20,
				24,
				26,
				29,
				32,
				38,
				41,
				44,
				47,
				50,
				52,
		};
		
		NOTIFICATION_COLORS = new Color[] {
				NotificationConstants.NOTIFICATION_COLOR_UPGRADE,
				NotificationConstants.NOTIFICATION_COLOR_NEW,
				NotificationConstants.NOTIFICATION_COLOR_UPGRADE,
				NotificationConstants.NOTIFICATION_COLOR_NEW,
				NotificationConstants.NOTIFICATION_COLOR_UPGRADE,
				NotificationConstants.NOTIFICATION_COLOR_UPGRADE,
				NotificationConstants.NOTIFICATION_COLOR_NEW,
				NotificationConstants.NOTIFICATION_COLOR_NEW,
				NotificationConstants.NOTIFICATION_COLOR_NEW,
				NotificationConstants.NOTIFICATION_COLOR_UPGRADE,
				NotificationConstants.NOTIFICATION_COLOR_NEW,
				NotificationConstants.NOTIFICATION_COLOR_UPGRADE,
				NotificationConstants.NOTIFICATION_COLOR_UPGRADE,
				NotificationConstants.NOTIFICATION_COLOR_UPGRADE,
				NotificationConstants.NOTIFICATION_COLOR_UPGRADE,
		};
		//@formatter:on
	}
}