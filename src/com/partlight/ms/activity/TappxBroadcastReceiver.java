package com.partlight.ms.activity;

import com.tappx.TrackInstall;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class TappxBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent == null)
			return;

		if (intent.getAction().contentEquals("com.android.vending.INSTALL_REFERRER")) {
			try {
				final TrackInstall ti = new TrackInstall();
				ti.onReceive(context, intent);
			} catch (Exception ex) {

			}
		}
	}

}
