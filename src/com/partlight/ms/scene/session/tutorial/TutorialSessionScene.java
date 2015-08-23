package com.partlight.ms.scene.session.tutorial;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.text.Text;
import org.andengine.entity.util.FPSLogger;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.VerticalAlign;
import org.andengine.util.color.Color;
import org.andengine.util.modifier.ease.EaseSineInOut;

import com.partlight.ms.entity.session.notification.Notification.NotificationEffects;
import com.partlight.ms.entity.session.tutorial.SkipTutorialButton;
import com.partlight.ms.entity.touch.swipe.SwipeHandler;
import com.partlight.ms.entity.touch.swipe.SwipeHandler.SwipeDirections;
import com.partlight.ms.resource.EnvironmentVars;
import com.partlight.ms.resource.EnvironmentVars.PreferenceKeys;
import com.partlight.ms.scene.session.SessionScene;
import com.partlight.ms.session.hud.BaseScreenComponent;
import com.partlight.ms.session.hud.ComboTracker;
import com.partlight.ms.session.hud.ComboTracker.NotificationConstants;
import com.partlight.ms.session.hud.FireButton;
import com.partlight.ms.session.hud.listener.ComponentAdapter;
import com.partlight.ms.session.level.Level;
import com.partlight.ms.session.round.RoundSystem;
import com.partlight.ms.session.round.TutorialRoundSystem;
import com.partlight.ms.util.EntityUtils;
import com.partlight.ms.util.Fade;

public class TutorialSessionScene extends SessionScene implements Runnable {

	private static final String		MSG_0				= "GREETINGS, MORTAL ONE\n\nA COLD AND UNDEAD\nWELCOME TO YOUR\nSHOWDOWN";
	private static final String		MSG_1				= "FIRST TIME IN THE\nFIELD, HM?\n\nLET'S GET THE BASICS\nON TRACK";
	private static final String		MSG_2				= "MOVE YOURSELF AROUND\nUSING THE JOYSTICK";
	private static final String		MSG_3				= "YOU CAN ALSO TAKE AIM BY\nSLIGHTLY TURNING\nTHE JOYSTICK";
	private static final String		MSG_4				= "TOUCH THIS ICON TO\nFIRE YOUR WEAPON";
	private static final String		MSG_5				= "KILLING THE UNDEAD FILLS UP\nYOUR COMBO METER\n\nHIGHER MULTIPLIERS\nMEANS NEW\nWEAPONS AND UPGRADES";
	private static final String		MSG_6				= "BE QUICK THOUGH!\n\nAS THE COMBO METER\nDROPS WITH TIME";
	private static final String		MSG_7				= "SEE ALL AVAILABLE COMBO\nGOODIES BY TOUCHING\nTHE COMBO METER";
	private static final String		MSG_8				= "SOUNDS LIKE\nTHEY'RE COMING\n\nLET'S PUT SOME POWER\nINTO THAT FIREARM";
	private static final String		MSG_9				= "GET A MULTIPLIER OF\n3 FOR A FIREARM UPGRADE";
	private static final String		MSG_10				= "WELL DONE, YOUR PISTOL\nCAN NOW FIRE CONTINOUSLY\n(HOLD DOWN THE FIRE ICON)\n\nBUT THAT PIECE OF METAL IS\nONLY GOOD FOR STARTERS";
	private static final String		MSG_11				= "YOU'LL NEED ANOTHER\nFIREARM OUT THERE\nLATER ON";
	private static final String		MSG_12				= "GET A MULTIPLIER OF\n6 FOR A NEW WEAPON";
	private static final String		MSG_13				= "YOU'VE GOT A NEW\nFIREARM\n\nLET'S HAVE A LOOK\nAT YOUR ARSENAL";
	private static final String		MSG_14				= "YOU'VE CURRENTLY GOT\nYOUR PISTOL SELECTED\n\nTOUCH THIS ICON\nTO SWITCH TO YOUR SMG";
	private static final String		MSG_15				= "ADDITIONALLY, YOU CAN\nSWIPE THE ICON\nTO THE RIGHT\nTO SWITCH TO THE\nPREVIOUS WEAPON";
	private static final String		MSG_16				= "ALRIGHT, YOU SEEM FIT\nENOUGH TO HANDLE THIS\nSHOWDOWN\n\nBRING 'EM DOWN";
	private static final String		NOTFICATION_FINAL	= "YOUR ARSENAL APPEARS JAMMED";
	private static final String		MSG_17				= "TUTORIAL\nFINISHED";
	private static final String[]	MESSAGES;
	private static final Color		ANIM_C_1;
	private static final Color		ANIM_C_2;

	static {
		MESSAGES = new String[] {
				TutorialSessionScene.MSG_0,
				TutorialSessionScene.MSG_1,
				TutorialSessionScene.MSG_2,
				TutorialSessionScene.MSG_3,
				TutorialSessionScene.MSG_4,
				TutorialSessionScene.MSG_5,
				TutorialSessionScene.MSG_6,
				TutorialSessionScene.MSG_7,
				TutorialSessionScene.MSG_8,
				TutorialSessionScene.MSG_9,
				TutorialSessionScene.MSG_10,
				TutorialSessionScene.MSG_11,
				TutorialSessionScene.MSG_12,
				TutorialSessionScene.MSG_13,
				TutorialSessionScene.MSG_14,
				TutorialSessionScene.MSG_15,
				TutorialSessionScene.MSG_16,
				TutorialSessionScene.MSG_17,
		};

		ANIM_C_1 = new Color(1, 1, 1, 0.5f);
		ANIM_C_2 = new Color(1, 1, 1, 1f);
	}

	private int					dialogMessageIndex;
	private String				dialogMessage;
	private float				dialogDelaySeconds;
	private HorizontalAlign		dialogHAlign;
	private VerticalAlign		dialogVAlign;
	private float				dialogElapsedDelaySeconds;
	private Text				tSkipText;
	private SkipTutorialButton	stbSkipTutorialButton;
	private boolean				isDialogDelayed;
	private int					actionIndex;
	private boolean				flashJoyStick;
	private SwipeHandler		shSkipButtonSwipeHandler;
	private boolean				flashFireButton;
	private boolean				flashInventoryButton;
	private boolean				flashComboTracker;

	public TutorialSessionScene(Level level) {
		super(level);
		this.registerUpdateHandler(new FPSLogger());
		this.actionIndex = 0;
	}

	@Override
	protected boolean assertPlayerCanShoot() {
		if (this.actionIndex == 17)
			return false;
		return super.assertPlayerCanShoot();
	}

	@Override
	protected void attachHudComponents() {
		final HUD HUD = EnvironmentVars.MAIN_CONTEXT.getCamera().getHUD();
		HUD.attachChild(this.getFade());
	}

	private boolean canHideDialog() {
		switch (this.actionIndex) {
		case 2:
			return false;
		case 4:
			return false;
		case 7:
			return false;
		case 9:
			return false;
		case 12:
			return false;
		case 14:
			return false;
		case 15:
			return false;
		case 17:
			return this.getInventoryButton().getParent() == null;

		}
		return true;
	}

	private void fadeInComponent(BaseScreenComponent component) {

		component.setTouchSuspended(false);
		component.setAlpha(1);
		EntityUtils.animateEntity(component, 0.25f, EntityUtils.ANIMATION_FADE_IN, EaseSineInOut.getInstance());
	}

	private void fadeOutComponent(BaseScreenComponent component) {
		this.fadeOutComponent(component, true);
	}

	private void fadeOutComponent(BaseScreenComponent component, boolean resetTouch) {

		component.setTouchSuspended(true);
		if (resetTouch)
			component.resetTouch();

		EntityUtils.animateEntity(component, 0.75f, EntityUtils.ANIMATION_FADE_OUT, EaseSineInOut.getInstance(),
				EntityUtils.getDetachListener());
	}

	@Override
	protected void initComboTracker() {
		super.initComboTracker();
		super.ctComboTracker.setTouchSuspended(true);
	}

	@Override
	protected void initFireButton() {
		super.initFireButton();
		this.fbFireButton.setComponentListener(new ComponentAdapter() {
			@Override
			public void onComponentReleased(BaseScreenComponent component, float x, float y) {
				TutorialSessionScene.this.getDialog().hide();
				TutorialSessionScene.this.fbFireButton.setComponentListener(null);
			}
		});
		this.getFireButton().setTouchSuspended(true);
	}

	@Override
	protected void initInventoryButton() {
		super.initInventoryButton();
		super.iInventory.setTouchSuspended(true);
	}

	@Override
	protected void initJoyStick() {
		super.initJoyStick();
		super.jsJoyStick.setTouchSuspended(true);
	}

	@Override
	protected void initPlayer() {
		super.initPlayer();
		this.pPlayer.setGodMode(true);
	}

	@Override
	protected RoundSystem initRoundSystem() {
		return new TutorialRoundSystem(this);
	}

	protected void initSkipButton() {
		if (true || this.stbSkipTutorialButton != null)
			return;

		this.stbSkipTutorialButton = new SkipTutorialButton();
		EnvironmentVars.MAIN_CONTEXT.getHud().attachChild(this.stbSkipTutorialButton);

		this.shSkipButtonSwipeHandler = new SwipeHandler(32, SwipeDirections.DOWN) {
			@Override
			public void onSwipe(SwipeDirections direction) {
				super.onSwipe(direction);

				this.release();

				if (direction != SwipeDirections.DOWN)
					return;

				TutorialSessionScene.this.stbSkipTutorialButton.show();
			}

			@Override
			protected void onSwipingStarted() {
				TutorialSessionScene.this.hdDialog.resetTouch();
			}
		};
	}

	@Override
	protected void onDialogClosed() {
		super.onDialogClosed();
		this.actionIndex++;
		this.progress();
	}

	@Override
	protected boolean onDialogSceneTouchEvent(TouchEvent sceneTouchEvent, boolean... additionalClosingConditions) {
		return super.onDialogSceneTouchEvent(sceneTouchEvent, this.canHideDialog());
	}

	protected void onFinalAction() {
		final HUD HUD = EnvironmentVars.MAIN_CONTEXT.getHud();

		final BaseScreenComponent[] COMPONENTS = new BaseScreenComponent[] {
				super.jsJoyStick,
				super.fbFireButton,
				super.ctComboTracker,
				super.iInventory
		};

		for (final BaseScreenComponent com : COMPONENTS) {
			HUD.attachChild(com);
			com.setAlpha(1);
			com.setTouchSuspended(false);
			com.clearEntityModifiers();
			EntityUtils.animateEntity(com, 0.25f, EntityUtils.ANIMATION_FADE_IN, EaseSineInOut.getInstance());
		}

		super.pPlayer.setGodMode(false);

		super.ctComboTracker.notify(TutorialSessionScene.NOTFICATION_FINAL, NotificationConstants.NOTIFICATION_COLOR_MESSAGE,
				NotificationConstants.NOTIFICATION_SCALE, NotificationEffects.PULSATE, true);
		super.rsRoundSystem.start();
	}

	@Override
	protected void onInventoryWeaponSwitch(boolean next) {
		if (this.actionIndex == 14 && next)
			super.hdDialog.hide();
		if (this.actionIndex == 15 && !next)
			super.hdDialog.hide();
	}

	@Override
	protected void onJoyStickTouchUp(float x, float y) {
		if (this.actionIndex == 2)
			this.hdDialog.hide();
	}

	@Override
	protected void onManagedUpdate(float pSecondsElapsed) {
		super.onManagedUpdate(pSecondsElapsed);

		if (this.isDialogDelayed)
			if ((this.dialogElapsedDelaySeconds += pSecondsElapsed) >= this.dialogDelaySeconds) {
				this.showDialog(this.dialogMessage, this.canHideDialog(), this.dialogHAlign, this.dialogVAlign, this.canHideDialog());
				this.dialogElapsedDelaySeconds = 0;
				this.isDialogDelayed = false;
			}

		if (this.flashJoyStick)
			EntityUtils.animateSineColor(this.jsJoyStick, TutorialSessionScene.ANIM_C_1, TutorialSessionScene.ANIM_C_2, 1);
		if (this.flashFireButton)
			EntityUtils.animateSineColor(this.fbFireButton, TutorialSessionScene.ANIM_C_1, TutorialSessionScene.ANIM_C_2, 1);
		if (this.flashComboTracker)
			EntityUtils.animateSineColor(this.ctComboTracker, TutorialSessionScene.ANIM_C_1, TutorialSessionScene.ANIM_C_2, 1);
		if (this.flashInventoryButton)
			EntityUtils.animateSineColor(this.iInventory, TutorialSessionScene.ANIM_C_1, TutorialSessionScene.ANIM_C_2, 1);
	}

	@Override
	public void onNewMultiplier(int multiplier) {

		if (multiplier == 3 && this.dialogMessageIndex == 10 || multiplier == 6 && this.dialogMessageIndex == 13) {
			this.killAllZombies();
			super.rsRoundSystem.finish();
			super.rsRoundSystem.setSuspendSpawn(true);

			super.jsJoyStick.setTouchSuspended(true);
			super.fbFireButton.setTouchSuspended(true);
			super.ctComboTracker.setTouchSuspended(true);
			super.ctComboTracker.setAnimateText(false);

			this.fadeOutComponent(super.jsJoyStick);
			this.fadeOutComponent(super.fbFireButton);
			this.fadeOutComponent(super.ctComboTracker);
			this.getDialog().hide();
		}

		super.onNewMultiplier(multiplier);
	}

	@Override
	public void onPlayerDead() {
		final Fade FADE = this.getFade();

		super.clearPlayerControl();
		FADE.detachSelf();
		EnvironmentVars.MAIN_CONTEXT.getCamera().getHUD().attachChild(this.getFade());

		FADE.detachChildren();
		FADE.setDuration(2f);
		FADE.runOnFadeIn(this);
		FADE.show(1f);
	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		final boolean SKIP_SWIPE_HANDLER = this.shSkipButtonSwipeHandler != null
				&& this.shSkipButtonSwipeHandler.onSceneTouchEvent(pScene, pSceneTouchEvent);
		if (!SKIP_SWIPE_HANDLER)
			return super.onSceneTouchEvent(pScene, pSceneTouchEvent);
		return SKIP_SWIPE_HANDLER;
	}

	@Override
	public void onStart() {
		super.onStart();
		this.progress();
	}

	@Override
	protected void onStartRound() {

	}

	@Override
	protected void playSirenSound() {
	}

	protected void progress() {
		HorizontalAlign hAlign = HorizontalAlign.CENTER;
		VerticalAlign vAlign = VerticalAlign.CENTER;
		float seconds = 0;

		switch (this.actionIndex) {
		case 0:
			seconds = 1;
			break;
		case 1:
			seconds = 0.1f;
			break;
		case 2:
			seconds = 1;
			hAlign = HorizontalAlign.RIGHT;
			vAlign = VerticalAlign.TOP;
			break;
		case 3:
			seconds = 0.1f;
			this.flashJoyStick = false;
			this.getJoyStick().setTouchSuspended(true);
			this.fadeOutComponent(this.getJoyStick());
			break;
		case 4:
			seconds = 0.5f;
			hAlign = HorizontalAlign.LEFT;
			vAlign = VerticalAlign.TOP;
			break;
		case 5:
			seconds = 1;
			this.flashFireButton = false;
			this.getFireButton().setTouchSuspended(true);
			this.fadeOutComponent(this.getFireButton());
			break;

		case 6:
			seconds = 0.1f;
			break;
		case 7:
			seconds = 0.1f;
			hAlign = HorizontalAlign.RIGHT;
			vAlign = VerticalAlign.BOTTOM;
			break;
		case 8:
			seconds = 1;
			this.flashComboTracker = false;
			this.ctComboTracker.setTouchSuspended(true);
			this.fadeOutComponent(this.ctComboTracker);
			break;
		case 10:
			seconds = 1;
			break;
		case 9:
		case 12:
			seconds = 0.1f;
			hAlign = HorizontalAlign.RIGHT;
			vAlign = VerticalAlign.CENTER;
			break;
		case 13:
			seconds = 0.5f;
			break;
		case 14:
		case 15:
			seconds = 0.1f;
			hAlign = HorizontalAlign.LEFT;
			vAlign = VerticalAlign.TOP;
			break;
		case 16:
			seconds = 1;
			this.flashInventoryButton = false;
			this.iInventory.setTouchSuspended(true);
			this.fadeOutComponent(this.iInventory);
			break;
		case 18:
			seconds = 1;
			break;
		case 19:
			this.toMainMenu("");
			break;
		}

		if (this.actionIndex != 17 && this.actionIndex != 19) {
			this.showDelayedDialog(TutorialSessionScene.MESSAGES[this.dialogMessageIndex], seconds, hAlign, vAlign);
			this.dialogMessageIndex++;
		} else
			EnvironmentVars.MAIN_CONTEXT.runOnUpdateThread(new Runnable() {
				@Override
				public void run() {
					TutorialSessionScene.this.onFinalAction();
				}
			});
	}

	protected void resetTouch() {
		if (this.hdDialog != null)
			this.hdDialog.resetTouch();
		this.fbFireButton.resetTouch();
	}

	@Override
	public void run() {
		if (this.actionIndex == 17) {
			this.detachAndDisposeNonPreservedEntities();

			this.actionIndex++;
			this.progress();
		}
	}

	protected void showDelayedDialog(String message, float secondsDelayed, HorizontalAlign horizontalAlign, VerticalAlign verticalAlign) {
		this.isDialogDelayed = true;
		this.dialogMessage = message;
		this.dialogDelaySeconds = secondsDelayed;
		this.dialogElapsedDelaySeconds = 0;
		this.dialogHAlign = horizontalAlign;
		this.dialogVAlign = verticalAlign;
	}

	@Override
	public void showDialog(String message, boolean showFooter, HorizontalAlign horizontalAlignment, VerticalAlign verticalAlignment,
			boolean tintBackground) {
		super.showDialog(message, showFooter, horizontalAlignment, verticalAlignment, tintBackground);

		final HUD HUD = EnvironmentVars.MAIN_CONTEXT.getHud();

		switch (this.actionIndex) {
		case 1:
			this.initSkipButton();
			break;
		case 2:
			this.jsJoyStick.setTouchSuspended(false);
			HUD.attachChild(this.jsJoyStick);
			super.alignJoyStick();
			this.flashJoyStick = true;
			break;
		case 4:
			final FireButton FIRE_BUTTON = this.getFireButton();

			FIRE_BUTTON.setTouchSuspended(false);
			HUD.attachChild(FIRE_BUTTON);
			this.alignFireButton();
			this.flashFireButton = true;
			break;
		case 7:
			final ComboTracker COMBO_TRACKER = this.getComboTracker();

			COMBO_TRACKER.setTouchSuspended(false);
			HUD.attachChild(COMBO_TRACKER);
			this.flashComboTracker = true;

			this.getRewardGuide().runOnClosed(new Runnable() {
				@Override
				public void run() {
					TutorialSessionScene.this.getDialog().hide();
				}
			});
			break;
		case 8:
			super.playSirenSound();
			break;
		case 9:
		case 12:
			super.ctComboTracker.setAnimateText(true);

			this.hdDialog.setAlpha(0.85f);

			HUD.attachChild(this.jsJoyStick);
			HUD.attachChild(this.fbFireButton);
			HUD.attachChild(this.ctComboTracker);

			this.jsJoyStick.setAlpha(1);
			this.jsJoyStick.setTouchSuspended(false);
			EntityUtils.animateEntity(this.jsJoyStick, 0.25f, EntityUtils.ANIMATION_FADE_IN, EaseSineInOut.getInstance());

			this.fbFireButton.setAlpha(1);
			this.fbFireButton.setTouchSuspended(false);
			EntityUtils.animateEntity(this.fbFireButton, 0.25f, EntityUtils.ANIMATION_FADE_IN, EaseSineInOut.getInstance());

			this.ctComboTracker.setAlpha(1);
			this.ctComboTracker.setTouchSuspended(false);
			EntityUtils.animateEntity(this.ctComboTracker, 0.25f, EntityUtils.ANIMATION_FADE_IN, EaseSineInOut.getInstance());

			super.rsRoundSystem.start();
			super.rsRoundSystem.setSuspendSpawn(false);
			break;
		case 14:

			HUD.attachChild(this.iInventory);
			super.iInventory.setTouchSuspended(false);
			this.alignInventory();
			this.flashInventoryButton = true;
			break;
		}
	}

	@Override
	protected void showSurviveNotification() {
	}

	@Override
	public void toMainMenu(final String optionalMessage) {
		EnvironmentVars.PREFERENCES_EDITOR.putBoolean(PreferenceKeys.KEY_HAS_PLAYED_TUTORIAL, true).commit();
		super.toMainMenu(optionalMessage);
	}
}