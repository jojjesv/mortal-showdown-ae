package com.partlight.ms.session.hud;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.modifier.MoveYModifier;
import org.andengine.entity.modifier.ParallelEntityModifier;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.util.modifier.IModifier;
import org.andengine.util.modifier.IModifier.IModifierListener;
import org.andengine.util.modifier.ease.EaseCubicOut;

import com.partlight.ms.entity.ShadowedText;
import com.partlight.ms.resource.EnvironmentVars;
import com.partlight.ms.resource.ResourceManager;
import com.partlight.ms.resource.ResourceManager.HudRegions;
import com.partlight.ms.scene.session.SessionScene;
import com.partlight.ms.session.character.player.Player;
import com.partlight.ms.session.character.player.powerup.Powerup;
import com.partlight.ms.session.character.player.powerup.Powerups;
import com.partlight.ms.util.updatehandler.FlashUpdateHandler;

import android.util.Log;

public class Inventory extends BaseScreenComponent {

	private static final float	POWERUP_ICON_ANIMATION_DURATION	= 0.5f;
	public static final float	INVENTORY_ICON_ROTATION			= 30f;
	private TiledSprite			sInvSprite;
	private Sprite				sAmmoIcon;
	private ShadowedText		stAmmoText;

	private float origoTouchX;

	private int powerupIconCount;

	public Inventory(ITiledTextureRegion sheet, ITextureRegion background, SessionScene context) {
		super(0, 0, background);

		this.setContext(context);
		this.createInvSprite(sheet);
		this.createAmmoIcon();
		this.createAmmoText();
	}

	protected void createAmmoIcon() {

		final float h = this.getHeight();

		this.sAmmoIcon = new Sprite(0f, h - 6f, HudRegions.region_ammo_icon, EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager()) {
			@Override
			public void setAlpha(float pAlpha) {
				super.setAlpha(pAlpha);
				Inventory.this.stAmmoText.setAlpha(pAlpha);
			}
		};
		this.sAmmoIcon.setRotation(10f);
	}

	protected void createAmmoText() {

		this.stAmmoText = new ShadowedText(this.sAmmoIcon.getWidth() + 4f, 0f, ResourceManager.fFontMain, "XXXX",
				EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager());
		this.stAmmoText.setY((this.sAmmoIcon.getHeight() - this.stAmmoText.getHeight()) / 2f);
		this.stAmmoText.setScale(1f);
	}

	protected void createInvSprite(ITiledTextureRegion sheet) {

		final float w = this.getWidth();
		final float h = this.getHeight();
		final float sw = sheet.getWidth();
		final float sh = sheet.getHeight();

		this.sInvSprite = new TiledSprite((w - sw) / 2f, (h - sh) / 2f, sheet, EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager());
		this.sInvSprite.setRotationCenter(sw / 2f, sh / 2f);
		this.sInvSprite.setRotation(Inventory.INVENTORY_ICON_ROTATION);

	}

	@Override
	public float getScaleCenterX() {
		return 0;
	}

	@Override
	public float getScaleCenterY() {
		return 0;
	}

	@Override
	public void onAttached() {
		super.onAttached();
		this.attachChild(this.sInvSprite);
		this.attachChild(this.sAmmoIcon);
		this.sAmmoIcon.attachChild(this.stAmmoText);
	}

	@Override
	public void onDetached() {
		super.onDetached();
		this.sInvSprite.detachSelf();
		this.sAmmoIcon.detachSelf();
		this.stAmmoText.detachSelf();
	}

	@Override
	protected void onPress(float x, float y) {
		super.onPress(x, y);
		this.origoTouchX = x;
	}

	public void onSwitchWeapon(boolean nextWeapon) {
		final Player PLAYER = ((SessionScene) this.getContext()).getPlayer();

		if (PLAYER.getWeaponCount() < 2)
			return;

		int newWepIndex = PLAYER.getWeapon().getIndex();

		if (nextWeapon) {
			if (newWepIndex >= PLAYER.getWeaponCount() - 1)
				newWepIndex = 0;
			else
				newWepIndex++;
		} else if (newWepIndex == 0)
			newWepIndex = PLAYER.getWeaponCount() - 1;
		else
			newWepIndex--;

		PLAYER.setWeapon(newWepIndex);
		this.sInvSprite.setCurrentTileIndex(PLAYER.getWeapon().getIndex());
	}

	@Override
	public void onTouchUp(float x, float y) {
		super.onTouchUp(x, y);

		if (!((SessionScene) this.getContext()).getPlayer().canFire())
			return;

		final float DELTA_X = x - this.origoTouchX;
		this.onSwitchWeapon(DELTA_X < 16f);
	}

	public void setAmmo(int ammo) {
		this.setAmmo(String.valueOf(ammo));
	}

	public void setAmmo(String ammo) {
		this.stAmmoText.setText(ammo);
	}

	public void showPowerupIcon(Powerup powerup) {

		Sprite icon = null;
		final float ANIMATION_DELTA_Y = 96f;

		ITextureRegion iconTextureRegion = null;

		switch (powerup.getId()) {
		case Powerups.ID_SPEED_BOOST:
			iconTextureRegion = HudRegions.region_boost_icon;
			break;

		default:
			Log.e("Mortal Showdown", powerup.getId() + " is an invalid power-up id!");
			break;
		}

		if (iconTextureRegion != null)
			icon = new Sprite(16f * this.powerupIconCount, 20, iconTextureRegion,
					EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager());

		if (icon != null) {

			final DelayModifier[] ICON_DELAYS = new DelayModifier[2];

			ICON_DELAYS[0] = new DelayModifier(powerup.getDuration() * 0.8f);
			ICON_DELAYS[1] = new DelayModifier(powerup.getDuration() * 0.2f);

			final Sprite ICON = icon;

			//@formatter:off
			ICON_DELAYS[0].addModifierListener(new IModifierListener<IEntity>() {
						@Override
						public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
							EnvironmentVars.MAIN_CONTEXT.runOnUpdateThread(new Runnable() {
										@Override
										public void run() {
											ICON.registerEntityModifier(ICON_DELAYS[1]);
										}
									});
						}
						@Override
						public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) { }
					});
			ICON_DELAYS[0].setAutoUnregisterWhenFinished(true);
			ICON_DELAYS[1]
					.addModifierListener(new IModifierListener<IEntity>() {
						
						final FlashUpdateHandler FLASH	= new FlashUpdateHandler(0.2f, -1);
						@Override
						public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
							EnvironmentVars.MAIN_CONTEXT.runOnUpdateThread(new Runnable() {
									@Override
									public void run() {
										Inventory.this.powerupIconCount--;
										ICON.unregisterUpdateHandler(FLASH);
										ICON.detachSelf();
										ICON.dispose();
									}
							});
						}
						@Override
						public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
							this.FLASH.runOnSwitch(new Runnable() {
								@Override
								public void run() {
									ICON.setVisible(!ICON.isVisible());
								}
							});
							ICON.registerUpdateHandler(this.FLASH);
						}
					});
			ICON_DELAYS[1].setAutoUnregisterWhenFinished(true);
			//@formatter:on

			//@formatter:off
			final ParallelEntityModifier ICON_MOD = new ParallelEntityModifier(
					new AlphaModifier(Inventory.POWERUP_ICON_ANIMATION_DURATION, 0f, 1f),
					new MoveYModifier(Inventory.POWERUP_ICON_ANIMATION_DURATION, icon.getY() + ANIMATION_DELTA_Y, icon.getY(), EaseCubicOut.getInstance()),
					ICON_DELAYS[0]
			);
			
			icon.setAlpha(0f);
			icon.setY(icon.getY() + ANIMATION_DELTA_Y);
			icon.registerEntityModifier(ICON_MOD);
			this.sAmmoIcon.attachChild(icon);
			//@formatter:on
			this.powerupIconCount++;
		}
	}

}
