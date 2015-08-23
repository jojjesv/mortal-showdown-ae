package com.partlight.ms.activity;

import com.partlight.ms.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Toast;
import android.widget.VideoView;

/**
 * Developer splash activity used to display a short developer themed video.<br>
 * This is the first activity to appear in the application.
 * 
 * @author Johan Svensson - partLight Entertainment
 * 
 */
public class SplashActivity extends Activity implements OnCompletionListener, OnTouchListener, OnErrorListener, OnPreparedListener {

	private static final boolean TOAST_ON_ERROR = true;

	private VideoView vvSplashView;

	private VideoView getView() {
		return (VideoView) this.findViewById(R.id.splash);
	}

	private void intent() {
		if (this.vvSplashView.isPlaying()) {
			this.vvSplashView.setVisibility(View.GONE);
			this.vvSplashView.stopPlayback();
		}
		final Intent GAME_INTENT = new Intent(this, GameActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
				.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		this.startActivity(GAME_INTENT);
		this.finish();
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		this.intent();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		this.getWindow().setContentView(R.layout.splash);
		this.vvSplashView = this.getView();
		this.vvSplashView.setOnTouchListener(this);
		this.vvSplashView.setOnPreparedListener(this);
		this.vvSplashView.setOnErrorListener(this);
		super.onCreate(savedInstanceState);
		ActivityProtocols.setTaskDescription(this);
		this.startVideo();
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {

		if (SplashActivity.TOAST_ON_ERROR) {
			final Toast T = Toast.makeText(this, R.string.toast_video_error, Toast.LENGTH_LONG);
			T.getView().setBackgroundColor(Color.RED);
			T.show();
		}

		this.intent();

		return true;
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		this.vvSplashView.start();
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouch(View v, MotionEvent event) {

		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			this.intent();
			return true;
		}

		return false;
	}

	@Override
	public synchronized void onWindowFocusChanged(boolean pHasWindowFocus) {
		super.onWindowFocusChanged(pHasWindowFocus);
		if (pHasWindowFocus)
			ActivityProtocols.setDecorViewFlags(this.getWindow());
	}

	private void startVideo() {
		final Uri splashURI = Uri.parse("android.resource://" + this.getPackageName() + "/" + R.raw.devintro);

		this.vvSplashView.setVideoURI(splashURI);
		this.vvSplashView.setOnCompletionListener(this);
	}
}
