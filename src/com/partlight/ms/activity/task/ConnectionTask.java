package com.partlight.ms.activity.task;

import com.google.android.gms.common.api.GoogleApiClient;

import android.os.AsyncTask;

public class ConnectionTask extends AsyncTask<GoogleApiClient, Void, Void> {

	@Override
	protected Void doInBackground(GoogleApiClient... params) {
		params[0].connect();
		return null;
	}
}
