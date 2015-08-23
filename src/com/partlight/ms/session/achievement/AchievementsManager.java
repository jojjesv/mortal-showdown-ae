package com.partlight.ms.session.achievement;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.partlight.ms.activity.GameActivity.GooglePlayConstants;
import com.partlight.ms.resource.EnvironmentVars;

/**
 * Manages all achievement progresses.
 * 
 * @author Johan Svensson - partLight Entertainment
 */
public final class AchievementsManager {

	private static final GoogleApiClient	CLIENT	= EnvironmentVars.MAIN_CONTEXT.getGoogleApiClient();
	private static float					grenadeThrowCountdown;
	private static int						grenadesThrownSinceCountdown;

	public static final boolean assertCanUnlock() {
		return AchievementsManager.CLIENT.isConnected();
	}

	public static final void incrementZombiesKilled() {
		if (!AchievementsManager.assertCanUnlock())
			return;

		Games.Achievements.increment(AchievementsManager.CLIENT, GooglePlayConstants.ACHIEVEMENT_0_ID, 1);
	}

	public static final void incrementZombiesKilledByExplosion() {
		if (!AchievementsManager.assertCanUnlock())
			return;

		Games.Achievements.increment(AchievementsManager.CLIENT, GooglePlayConstants.ACHIEVEMENT_5_ID, 1);
	}

	public static final void onThrowGrenade() {
		AchievementsManager.grenadesThrownSinceCountdown++;
		AchievementsManager.grenadeThrowCountdown = 6f;
	}

	public static final void onUpdate(float elapsedSeconds) {
		if ((int) (AchievementsManager.grenadeThrowCountdown * 1000) > 0) {

			if (AchievementsManager.grenadesThrownSinceCountdown >= 10)
				AchievementsManager.unlockAchievement(GooglePlayConstants.ACHIEVEMENT_7_ID);

			AchievementsManager.grenadeThrowCountdown -= elapsedSeconds;
		} else {
			AchievementsManager.grenadeThrowCountdown = 0f;
			AchievementsManager.grenadesThrownSinceCountdown = 0;
		}
	}

	public static final void unlockAchievement(String id) {
		if (!AchievementsManager.assertCanUnlock())
			return;

		Games.Achievements.unlock(EnvironmentVars.MAIN_CONTEXT.getGoogleApiClient(), id);
	}
}
