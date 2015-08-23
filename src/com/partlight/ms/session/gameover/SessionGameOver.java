package com.partlight.ms.session.gameover;

import static com.partlight.ms.resource.EnvironmentVars.MAIN_CONTEXT;

import java.nio.ByteBuffer;

import org.andengine.audio.BaseAudioEntity;
import org.andengine.audio.music.Music;
import org.andengine.entity.Entity;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.modifier.MoveXModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.exception.OutOfCharactersException;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.FontUtils;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.opengl.texture.render.RenderTexture;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.VerticalAlign;
import org.andengine.util.color.Color;
import org.andengine.util.modifier.IModifier;
import org.andengine.util.modifier.ease.EaseCubicOut;
import org.andengine.util.modifier.ease.EaseLinear;
import org.andengine.util.modifier.ease.EaseSineInOut;
import org.andengine.util.modifier.ease.EaseSineOut;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.partlight.ms.activity.GameActivity.GooglePlayConstants;
import com.partlight.ms.activity.GameActivity.ScreenTextureCallback;
import com.partlight.ms.entity.ShadowedText;
import com.partlight.ms.entity.session.gameover.ScrapPartsSplash;
import com.partlight.ms.entity.touch.swipe.ModifierSwipeHandler;
import com.partlight.ms.entity.touch.swipe.SwipeHandler.SwipeDirections;
import com.partlight.ms.resource.ResourceManager;
import com.partlight.ms.scene.mainmenu.MainMenuScene;
import com.partlight.ms.scene.session.SessionScene;
import com.partlight.ms.session.SessionData;
import com.partlight.ms.util.EaseJump;
import com.partlight.ms.util.EntityModifierAdapter;
import com.partlight.ms.util.EntityUtils;
import com.partlight.ms.util.Fade;
import com.partlight.ms.util.TextureManagedSprite;
import com.partlight.ms.util.updatehandler.FloatValueModifier;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

public class SessionGameOver implements ScreenTextureCallback, Runnable {

	private static final int	STATE_SHOW_TITLE	= 0;
	private static final int	STATE_TO_MAIN_MENU	= 1;

	private Bitmap					bBackgroundBlurred;
	private BitmapTextureAtlas		btaBackgroundTexture;
	private ByteBuffer				bbBackgroundBlurredBuffer;
	private DelayModifier			dmScoreAnim1;
	private DelayModifier			dmScoreAnim3;
	private ShadowedText			stSwipeTopLeft;
	private ShadowedText			stSwipeBottomLeft;
	private ShadowedText			stSwipeTopRight;
	private ShadowedText			stSwipeBottomRight;
	private Entity					eSwipeContainer;
	private Fade					fFade;
	private FloatValueModifier		fvmScoreAnim2;
	private final SessionData		sdSessionData;
	private final SessionScene		ssSessionScene;
	private ShadowedText			stScore;
	private Sprite					sBackgroundBlurred;
	private int						backgroundHeight;
	private int						backgroundWidth;
	private ModifierSwipeHandler	mshSwipeHandler;
	private final Music				MUSIC;
	private int						runnableState;
	private String					mainMenuMessage;

	public SessionGameOver(SessionScene sessionScene) {
		this.ssSessionScene = sessionScene;
		this.sdSessionData = sessionScene.getSessionData();
		this.MUSIC = ResourceManager.mGameOver;
		this.onFadeOutAudio();
	}

	protected boolean attemptSubmitScore() {
		final GoogleApiClient CLIENT = MAIN_CONTEXT.getGoogleApiClient();
		final boolean CLIENT_CONNECTED = CLIENT.isConnected();

		if (CLIENT_CONNECTED)
			this.submitScore(CLIENT, this.sdSessionData.sessionScore);
		else {
			this.ssSessionScene.showDialog("YOU NEED TO SIGN IN WITH\nA GOOGLE PROFILE TO SUBMIT\nYOUR SCORE.\nSIGN IN NOW?\n\n");
			this.ssSessionScene.addDialogButtons();
		}

		return CLIENT_CONNECTED;
	}

	private void blurBackground(Bitmap background) {

		this.backgroundWidth = background.getWidth();
		this.backgroundHeight = background.getHeight();

		final Bitmap BLURRED_BACKGROUND = Bitmap.createBitmap(this.backgroundWidth, this.backgroundHeight, background.getConfig());

		final RenderScript SCRIPT = RenderScript.create(MAIN_CONTEXT);
		final Allocation IN = Allocation.createFromBitmap(SCRIPT, background);
		final Allocation OUT = Allocation.createFromBitmap(SCRIPT, BLURRED_BACKGROUND);
		final ScriptIntrinsicBlur BLUR_SCRIPT = ScriptIntrinsicBlur.create(SCRIPT, Element.U8_4(SCRIPT));

		BLUR_SCRIPT.setInput(IN);
		BLUR_SCRIPT.setRadius(8f);
		BLUR_SCRIPT.forEach(OUT);
		OUT.copyTo(BLURRED_BACKGROUND);
		SCRIPT.destroy();

		this.bbBackgroundBlurredBuffer = ByteBuffer.allocate(BLURRED_BACKGROUND.getRowBytes() * BLURRED_BACKGROUND.getHeight());
		BLURRED_BACKGROUND.copyPixelsToBuffer(this.bbBackgroundBlurredBuffer);
		BLURRED_BACKGROUND.recycle();

		this.createBlurredBackground();

		if (this.fFade != null)
			this.fFade.detachSelf();

		this.fFade = new Fade(MAIN_CONTEXT.getVertexBufferObjectManager());
		this.fFade.setColor(Color.TRANSPARENT);
		this.fFade.hideInstantly();
		this.fFade.setEase(EaseSineInOut.getInstance());
		this.fFade.setDuration(0.75f);
		this.fFade.show(0);
		this.fFade.runOnFadeIn(this);
		this.fFade.attachChild(this.sBackgroundBlurred);
		MAIN_CONTEXT.getHud().attachChild(this.fFade);

		this.runnableState = SessionGameOver.STATE_SHOW_TITLE;
	}

	private void createBlurredBackground() {
		if (this.sBackgroundBlurred != null)
			this.sBackgroundBlurred.detachSelf();

		final IBitmapTextureAtlasSource ATLAS = new IBitmapTextureAtlasSource() {
			@Override
			public IBitmapTextureAtlasSource deepCopy() {
				return null;
			}

			@Override
			public int getTextureHeight() {
				return SessionGameOver.this.backgroundHeight;
			}

			@Override
			public int getTextureWidth() {
				return SessionGameOver.this.backgroundWidth;
			}

			@Override
			public int getTextureX() {
				return 0;
			}

			@Override
			public int getTextureY() {
				return 0;
			}

			@Override
			public Bitmap onLoadBitmap(Config pBitmapConfig) {
				SessionGameOver.this.bBackgroundBlurred = Bitmap.createBitmap(SessionGameOver.this.backgroundWidth,
						SessionGameOver.this.backgroundHeight, pBitmapConfig);
				SessionGameOver.this.bbBackgroundBlurredBuffer.rewind();
				SessionGameOver.this.bBackgroundBlurred.copyPixelsFromBuffer(SessionGameOver.this.bbBackgroundBlurredBuffer);
				return SessionGameOver.this.bBackgroundBlurred;
			}

			@Override
			public void setTextureHeight(int pTextureHeight) {
			}

			@Override
			public void setTextureWidth(int pTextureWidth) {
			}

			@Override
			public void setTextureX(int pTextureX) {
			}

			@Override
			public void setTextureY(int pTextureY) {
			}
		};

		this.btaBackgroundTexture = new BitmapTextureAtlas(MAIN_CONTEXT.getTextureManager(), this.backgroundWidth,
				this.backgroundHeight);
		this.btaBackgroundTexture.addTextureAtlasSource(ATLAS, 0, 0);
		this.sBackgroundBlurred = new TextureManagedSprite(0, 0, TextureRegionFactory.extractFromTexture(this.btaBackgroundTexture),
				MAIN_CONTEXT.getVertexBufferObjectManager());
		this.sBackgroundBlurred.setTag(SessionScene.TAG_PRESERVE);
		this.sBackgroundBlurred.setFlippedVertical(true);
	}

	protected void onFadeOutAudio() {
		final BaseAudioEntity[] AUDIO_PLAYING = MAIN_CONTEXT.getAudioRegistered();
		final float[] AUDIO_PLAYING_VOLUME = new float[AUDIO_PLAYING.length];

		for (int i = 0; i < AUDIO_PLAYING_VOLUME.length; i++)
			AUDIO_PLAYING_VOLUME[i] = AUDIO_PLAYING[i].getVolume();

		this.MUSIC.setVolume(0);
		this.MUSIC.play();

		MAIN_CONTEXT.registerSound(this.MUSIC);

		this.ssSessionScene.registerUpdateHandler(new FloatValueModifier(1, 0, EaseLinear.getInstance(), 1) {
			@Override
			protected void onFinished() {
				super.onFinished();
				EntityUtils.safetlyUnregisterUpdateHandler(SessionGameOver.this.ssSessionScene, this);
				for (final BaseAudioEntity audio : AUDIO_PLAYING)
					MAIN_CONTEXT.unregisterSound(audio);

			}

			@Override
			protected void onValueChanged(float value) {
				super.onValueChanged(value);
				for (int i = 0; i < AUDIO_PLAYING.length; i++)
					AUDIO_PLAYING[i].setVolume(AUDIO_PLAYING_VOLUME[i] * value);
				SessionGameOver.this.MUSIC.setVolume(1 - value);
			}
		});
	}

	@Override
	public void onScreenTextureReceived(RenderTexture texture, Bitmap bmp) {
		final Bitmap BITMAP = bmp;
		new Thread(new Runnable() {
			@Override
			public void run() {
				// SessionGameOverScene.this.createNonBlurredBackground(RENDER_TEXTURE);
				SessionGameOver.this.blurBackground(BITMAP);
			}
		}).start();
	}

	private void preDelayPartsObtained() {
		this.ssSessionScene.registerEntityModifier(this.dmScoreAnim3);
	}

	@Override
	public void run() {
		switch (this.runnableState) {
		case SessionGameOver.STATE_TO_MAIN_MENU:
			this.toMainMenu(this.mainMenuMessage);
			break;
		case SessionGameOver.STATE_SHOW_TITLE:
			this.sBackgroundBlurred.detachSelf();
			MAIN_CONTEXT.getHud().attachChild(this.sBackgroundBlurred);
			this.ssSessionScene.detachAndDisposeNonPreservedEntities();
			this.showTitle();
			break;
		}
	}

	private void setTouchListener() {
		this.mshSwipeHandler = new ModifierSwipeHandler(this.ssSessionScene, MainMenuScene.SWIPE_DISTANCE, SwipeDirections.LEFT,
				SwipeDirections.RIGHT) {
			@Override
			public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
				if (SessionGameOver.this.ssSessionScene.getDialog() != null && SessionGameOver.this.ssSessionScene.getDialog().isShowing())
					return SessionGameOver.this.ssSessionScene.onSceneTouchEvent(pSceneTouchEvent);
				return super.onSceneTouchEvent(pScene, pSceneTouchEvent);
			}

			@SuppressWarnings("incomplete-switch")
			@Override
			public void onSwipe(SwipeDirections direction) {
				super.onSwipe(direction);

				this.release();

				String message = "";

				switch (direction) {
				case LEFT:
					if (SessionGameOver.this.attemptSubmitScore()) {
						if (SessionGameOver.this.sdSessionData.sessionScore < 1)
							message = "CANNOT PUBLISH A SCORE OF 0";
						else
							message = "SCORE PUBLISHED";

						SessionGameOver.this.transitionToMainMenu(message);
					}

					break;
				case RIGHT:
					SessionGameOver.this.transitionToMainMenu(message);
					break;
				}
			}

			@Override
			public void onTransformationPercentChanged(float percent) {
				super.onTransformationPercentChanged(percent);

				final float scale = 1 + 0.005f * Math.abs(percent);
				SessionGameOver.this.sBackgroundBlurred.setScale(scale);
				SessionGameOver.this.eSwipeContainer.setX(48 * percent);
			}
		};

		MAIN_CONTEXT.getHud().setOnSceneTouchListener(this.mshSwipeHandler);
	}

	protected void showScore() {
		MAIN_CONTEXT.showAd();
		
		if (this.stScore != null)
			throw new IllegalStateException();

		final long SCORE = this.sdSessionData.sessionScore;

		this.stScore = new ShadowedText(0f, 160f, ResourceManager.fFontMain, String.valueOf(SCORE),
				MAIN_CONTEXT.getVertexBufferObjectManager()) {
			@Override
			public void setText(CharSequence pText) throws OutOfCharactersException {
				super.setText(pText);
				try {
					final float WIDTH = FontUtils.measureText(super.mFont, super.mText);
					this.setX((MAIN_CONTEXT.width() - WIDTH) / 2f);
				} catch (final NullPointerException ex) {
				}
			}
		};
		this.stScore.setScale(4f);
		this.stScore.setText("0");

		this.dmScoreAnim1 = new DelayModifier(0.1f) {
			@Override
			protected void onModifierFinished(IEntity pItem) {
				super.onModifierFinished(pItem);
				if (SCORE > 0)
					SessionGameOver.this.ssSessionScene.registerUpdateHandler(SessionGameOver.this.fvmScoreAnim2);
				else {
					SessionGameOver.this.showZeroScoreComment();
					SessionGameOver.this.preDelayPartsObtained();
				}
			}
		};
		this.dmScoreAnim1.setAutoUnregisterWhenFinished(true);
		this.fvmScoreAnim2 = new FloatValueModifier(0f, 1f, EaseLinear.getInstance(), 0.25f) {
			@Override
			protected void onFinished() {
				super.onFinished();
				SessionGameOver.this.stScore.setText(String.valueOf(SCORE));

				final ScaleModifier SCALE_JUMP = new ScaleModifier(0.2f, 4f, 4.45f, EaseJump.getInstance());
				SCALE_JUMP.setAutoUnregisterWhenFinished(true);

				SessionGameOver.this.stScore.registerEntityModifier(SCALE_JUMP);
				EntityUtils.safetlyUnregisterUpdateHandler(SessionGameOver.this.ssSessionScene, SessionGameOver.this.fvmScoreAnim2);

				SessionGameOver.this.preDelayPartsObtained();
			};

			@Override
			protected void onValueChanged(float value) {
				super.onValueChanged(value);
				SessionGameOver.this.stScore.setText(String.valueOf((int) (SCORE * value)));
			}
		};
		this.dmScoreAnim3 = new DelayModifier(0.25f) {
			@Override
			protected void onModifierFinished(IEntity pItem) {
				super.onModifierFinished(pItem);
				SessionGameOver.this.showScrapPartsObtained();
			}
		};
		this.dmScoreAnim3.setAutoUnregisterWhenFinished(true);

		EntityUtils.animateEntity(this.stScore, 0.2f, EntityUtils.ANIMATION_SCALE_OUT_FADE_IN, EaseSineOut.getInstance(),
				new EntityModifierAdapter() {
					@Override
					public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
						SessionGameOver.this.ssSessionScene.registerEntityModifier(SessionGameOver.this.dmScoreAnim1);
					}
				});
		this.eSwipeContainer.attachChild(this.stScore);
	}

	protected void showScrapPartsObtained() {
		if (this.sdSessionData.sessionParts < 1) {
			this.showSwipeOptions();
			return;
		}

		final ScrapPartsSplash SP_SPLASH = new ScrapPartsSplash(this.sdSessionData.sessionParts,
				MAIN_CONTEXT.getVertexBufferObjectManager()) {
			@Override
			protected void onAnimationFinished() {
				SessionGameOver.this.showSwipeOptions();
			}
		};
		MAIN_CONTEXT.getHud().attachChild(SP_SPLASH);
	}

	protected void showSwipeOptions() {

		this.stSwipeBottomRight = new ShadowedText(0, 0, ResourceManager.fFontMain, "TO PUBLISH SCORE",
				MAIN_CONTEXT.getVertexBufferObjectManager());
		this.stSwipeBottomRight.setScale(1.5f);

		this.stSwipeTopRight = new ShadowedText(0, 0, ResourceManager.fFontMain, "SWIPE LEFT",
				MAIN_CONTEXT.getVertexBufferObjectManager());
		this.stSwipeTopRight.setScale(2f);

		this.stSwipeBottomLeft = new ShadowedText(0, 0, ResourceManager.fFontMain, "TO RETURN",
				MAIN_CONTEXT.getVertexBufferObjectManager());
		this.stSwipeBottomLeft.setScale(1.5f);

		this.stSwipeTopLeft = new ShadowedText(0, 0, ResourceManager.fFontMain, "SWIPE RIGHT",
				MAIN_CONTEXT.getVertexBufferObjectManager());
		this.stSwipeTopLeft.setScale(2);

		this.eSwipeContainer.attachChild(this.stSwipeTopLeft);
		this.eSwipeContainer.attachChild(this.stSwipeTopRight);
		this.eSwipeContainer.attachChild(this.stSwipeBottomLeft);
		this.eSwipeContainer.attachChild(this.stSwipeBottomRight);

		EntityUtils.alignEntity(this.stSwipeBottomRight, this.stSwipeBottomRight.getWidth(), this.stSwipeBottomRight.getHeight(),
				HorizontalAlign.RIGHT, VerticalAlign.BOTTOM, 16, 16);

		EntityUtils.alignEntity(this.stSwipeTopRight, this.stSwipeTopRight.getWidth(), this.stSwipeTopRight.getHeight(),
				HorizontalAlign.RIGHT, VerticalAlign.BOTTOM, 16, 32 + this.stSwipeBottomRight.getHeightScaled());

		EntityUtils.alignEntity(this.stSwipeBottomLeft, this.stSwipeBottomLeft.getWidth(), this.stSwipeBottomLeft.getHeight(),
				HorizontalAlign.LEFT, VerticalAlign.BOTTOM, 16, 16);

		EntityUtils.alignEntity(this.stSwipeTopLeft, this.stSwipeTopLeft.getWidth(), this.stSwipeTopLeft.getHeight(), HorizontalAlign.LEFT,
				VerticalAlign.BOTTOM, 16, 32 + this.stSwipeBottomLeft.getHeightScaled());

		final ShadowedText topLeft = this.stSwipeTopLeft;
		final ShadowedText topRight = this.stSwipeTopRight;
		final ShadowedText bottomRight = this.stSwipeBottomRight;
		final ShadowedText bottomLeft = this.stSwipeBottomLeft;

		final float topLeftXTo = topLeft.getX();
		final float topRightXTo = topRight.getX();
		final float bottomRightXTo = bottomRight.getX();
		final float bottomLeftXTo = bottomLeft.getX();

		final float width = MAIN_CONTEXT.width();

		final float topLeftXFrom = -topLeft.getWidthScaled() + EntityUtils.getXDelta(topLeft);
		final float topRightXFrom = width + topRight.getWidthScaled() - EntityUtils.getXDelta(topRight);
		final float bottomRightXFrom = width + bottomRight.getWidthScaled() - EntityUtils.getXDelta(bottomRight);
		final float bottomLeftXFrom = -bottomLeft.getWidthScaled() + EntityUtils.getXDelta(bottomRight);

		final MoveXModifier xmod = new MoveXModifier(0.3f, topLeftXFrom, topLeftXTo, EaseCubicOut.getInstance()) {

			@Override
			protected void onModifierFinished(IEntity pItem) {
				super.onModifierFinished(pItem);
				SessionGameOver.this.setTouchListener();
			}

			@Override
			protected void onSetValue(IEntity pEntity, float pPercentageDone, float pX) {
				super.onSetValue(pEntity, pPercentageDone, pX);

				topRight.setX(topRightXFrom + (topRightXTo - topRightXFrom) * pPercentageDone);
				bottomRight.setX(bottomRightXFrom + (bottomRightXTo - bottomRightXFrom) * pPercentageDone);
				bottomLeft.setX(bottomLeftXFrom + (bottomLeftXTo - bottomLeftXFrom) * pPercentageDone);
			}
		};
		xmod.setAutoUnregisterWhenFinished(true);
		xmod.onUpdate(0, this.stSwipeTopLeft);

		this.stSwipeTopLeft.registerEntityModifier(xmod);
	}

	protected void showTitle() {
		final ShadowedText TITLE = new ShadowedText(0f, 64f, ResourceManager.fFontMain, SessionScene.GAME_OVER_TITLE,
				MAIN_CONTEXT.getVertexBufferObjectManager());

		TITLE.setScale(3.5f);
		TITLE.setZIndex(10);
		TITLE.setX((MAIN_CONTEXT.width() - TITLE.getWidth()) / 2f);
		MAIN_CONTEXT.getCamera().setCenter(MAIN_CONTEXT.width() / 2f,
				MAIN_CONTEXT.height() / 2f);

		this.eSwipeContainer = new Entity();
		MAIN_CONTEXT.getCamera().getHUD().attachChild(this.eSwipeContainer);
		this.eSwipeContainer.attachChild(TITLE);

		EntityUtils.animateEntity(TITLE, 0.25f, EntityUtils.ANIMATION_JUMP_IN_LOW_INTESTIVITY, EaseLinear.getInstance());

		final DelayModifier SCORE_PRE_DELAY = new DelayModifier(0.75f) {
			@Override
			protected void onModifierFinished(IEntity pItem) {
				super.onModifierFinished(pItem);
				SessionGameOver.this.showScore();
			}
		};
		SCORE_PRE_DELAY.setAutoUnregisterWhenFinished(true);
		this.ssSessionScene.registerEntityModifier(SCORE_PRE_DELAY);
	}

	protected void showZeroScoreComment() {
		final ShadowedText ZERO_SCORE_COMMENT = new ShadowedText(0f, this.stScore.getY() + 48f, ResourceManager.fFontMain,
				"THE HECK WAS THAT", MAIN_CONTEXT.getVertexBufferObjectManager());
		ZERO_SCORE_COMMENT.setScale(1.5f);
		ZERO_SCORE_COMMENT.setColor(0.97f, 0.72f, 0.72f);
		ZERO_SCORE_COMMENT.setX((MAIN_CONTEXT.width() - ZERO_SCORE_COMMENT.getWidth()) / 2f);

		EntityUtils.animateEntity(ZERO_SCORE_COMMENT, 0.15f, EntityUtils.ANIMATION_SCALE_OUT_JAGGED);

		this.eSwipeContainer.attachChild(ZERO_SCORE_COMMENT);
	}

	public void start() {
		MAIN_CONTEXT.requestScreenTexture(this);
	}

	protected void submitScore(GoogleApiClient client, long score) {
		if (score > 0)
			Games.Leaderboards.submitScore(client, GooglePlayConstants.LEADERBOARD_ID, score);
	}

	protected void toMainMenu(final String message) {
		this.ssSessionScene.toMainMenu(message);
	}

	protected void transitionToMainMenu(final String message) {
		MAIN_CONTEXT.getHud().setOnSceneTouchListener(null);

		this.mainMenuMessage = message;

		this.fFade.hideInstantly();
		this.fFade.setColor(Color.BLACK);
		this.fFade.detachChildren();
		MAIN_CONTEXT.getHud().attachChild(this.fFade);
		this.fFade.show();
		this.fFade.runOnFadeIn(this);
		this.runnableState = SessionGameOver.STATE_TO_MAIN_MENU;
	}
}
