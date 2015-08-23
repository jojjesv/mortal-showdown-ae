package com.partlight.ms.activity;

import com.partlight.ms.R;
import com.partlight.ms.activity.GameActivity.Values;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager.TaskDescription;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.View;
import android.view.Window;

/**
 * A set of protocols, preferred methods that each activity should adapt to and
 * follow.
 * 
 * @author Johan Svensson - partLight Entertainment
 *
 */
final class ActivityProtocols {

	@SuppressLint("InlinedApi")
	public static void setDecorViewFlags(Window window) {
		window.getDecorView()
				.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
						| View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
						| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
	}

	@SuppressLint("NewApi")
	public static void setTaskDescription(Activity activity) {

		if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP)
			return;

		final int cPrimaryColor = Color.rgb(Values.TASK_DESCRIPTION_PRIMARY_COLOR_R, Values.TASK_DESCRIPTION_PRIMARY_COLOR_G,
				Values.TASK_DESCRIPTION_PRIMARY_COLOR_B);

		final TaskDescription tdNewDescription = new TaskDescription(activity.getString(R.string.app_name),
				BitmapFactory.decodeResource(activity.getResources(), R.drawable.ic_launcher), cPrimaryColor);

		activity.setTaskDescription(tdNewDescription);
	}
}
