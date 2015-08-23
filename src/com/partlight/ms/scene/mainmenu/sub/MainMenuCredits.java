package com.partlight.ms.scene.mainmenu.sub;

import org.andengine.entity.scene.Scene;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITextureRegion;

import com.partlight.ms.entity.ShadowedText;
import com.partlight.ms.entity.touch.scroll.ScrollContainer;
import com.partlight.ms.resource.EnvironmentVars;
import com.partlight.ms.resource.ResourceManager;
import com.partlight.ms.resource.ResourceManager.MainMenuRegions;
import com.partlight.ms.resource.ResourceManager.StrokeTextureRegions;
import com.partlight.ms.scene.mainmenu.MainMenuScene;
import com.partlight.ms.scene.mainmenu.SubSection;
import com.partlight.ms.util.ColorConstants;
import com.partlight.ms.util.TextureManagedSprite;

import android.content.Intent;
import android.net.Uri;

public class MainMenuCredits extends SubSection {

	private static final String LICENSE = "MORTAL SHOWDOWN IS DEVELOPED AND PUBLISHED\nBY PARTLIGHT UNDER A ATTRIBUTION-NODERIVATIVES 4.0\nINTERNATIONAL LICENSE\nBLAH BLAH BLAH";

	private final StringBuilder			sbCreditTitles;
	private final StringBuilder			sbCreditNames;
	private final ScrollContainer		scScrollContainer;
	private final ShadowedText			stCreditsHeaders;
	private final Text					tCredits;
	private final ShadowedText			stLicense;
	private final TextureManagedSprite	sLogoBackground;
	private final TextureManagedSprite	sLogoForeground;
	private final TextureManagedSprite	sLicense;

	private boolean hasTouchDownScrollContainer;

	public MainMenuCredits(MainMenuScene context) {
		super(context);
		this.sbCreditNames = new StringBuilder();
		this.sbCreditTitles = new StringBuilder();

		this.addCredits("AUDIO", "JOHAN SVENSSON");
		this.addCredits("ADDITIONAL AUDIO SOURCES", "MIKE KOENIG");
		this.addCredits("DESIGN", "JOHAN SVENSSON");
		this.addCredits("GRAPHICS", "JOHAN SVENSSON");
		this.addCredits("CODE DESIGN", "JOHAN SVENSSON");

		this.sLogoBackground = new TextureManagedSprite(16f, 16f, MainMenuRegions.region_logo_background,
				EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager());
		this.sLogoBackground.setScaleCenter(0, 0);
		this.sLogoBackground.setScale(0.75f);

		// Foreground
		{
			final float logoForeX = (MainMenuRegions.region_logo_background.getWidth() - MainMenuRegions.region_logo.getWidth()) / 2f;
			final float logoForeY = (MainMenuRegions.region_logo_background.getHeight() - MainMenuRegions.region_logo.getHeight()) / 2f;
			this.sLogoForeground = new TextureManagedSprite(logoForeX, logoForeY, MainMenuRegions.region_logo,
					EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager());
			this.sLogoBackground.attachChild(this.sLogoForeground);
		}

		this.stCreditsHeaders = new ShadowedText(24f, 160f, ResourceManager.fFontMain, this.sbCreditTitles.toString(),
				EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager());
		this.stCreditsHeaders.setScaleCenter(0, 0);
		this.stCreditsHeaders.setScale(2f);

		this.tCredits = new Text(this.stCreditsHeaders.getX(), this.stCreditsHeaders.getY(), ResourceManager.fFontMain,
				this.sbCreditNames.toString(), EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager()) {
			@Override
			public void setAlpha(float pAlpha) {
				super.setAlpha(pAlpha * 0.75f);
			}
		};
		this.tCredits.setScale(this.stCreditsHeaders.getScaleX() / 2f, this.stCreditsHeaders.getScaleY() / 2f);

		this.scScrollContainer = new ScrollContainer(8f) {
			@Override
			public void setAlpha(float pAlpha) {
				super.setAlpha(pAlpha);
				MainMenuCredits.this.sLogoForeground.setAlpha(pAlpha);
			}
		};

		this.sLicense = new TextureManagedSprite(24f, this.tCredits.getY() + this.tCredits.getHeightScaled(),
				MainMenuRegions.region_license, EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager());
		this.sLicense.setScaleCenter(0, 0);
		this.sLicense.setScale(1.25f);

		this.stLicense = new ShadowedText(this.sLicense.getWidthScaled(), 0f, ResourceManager.fFontMain, MainMenuCredits.LICENSE,
				EnvironmentVars.MAIN_CONTEXT.getVertexBufferObjectManager());
		this.stLicense.setColor(ColorConstants.LIGHT_RED);
		this.stLicense.setScaleCenter(0, 0);
		this.stLicense.setScale(1f);

		this.sLicense.attachChild(this.stLicense);

		this.scScrollContainer.setY(0f);
		this.scScrollContainer.attachChild(this.sLogoBackground);
		this.scScrollContainer.attachChild(this.stCreditsHeaders);
		this.scScrollContainer.attachChild(this.tCredits);
		this.scScrollContainer.attachChild(this.sLicense);

		this.scScrollContainer.setMinY(this.scScrollContainer.getMinY() - 64f);
	}

	private void addCredits(String title, String... names) {
		this.sbCreditTitles.append(title);
		this.sbCreditTitles.append("\n\n");

		final String[] titleLines = title.split("\n");

		for (int x = 0; x < names.length; x++) {
			for (int y = 0; y < titleLines.length; y++)
				this.sbCreditNames.append("\n\n");
			this.sbCreditNames.append(names[x]);
			this.sbCreditNames.append("\n\n");
		}
	}

	@Override
	public void onBackPressed() {

	}

	@Override
	protected void onContainerAttached() {
		ResourceManager.btStroke6.load();
	}

	@Override
	protected void onContainerDetached() {
		ResourceManager.btStroke6.unload();
	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {

		final boolean scrollContainerEvent = this.scScrollContainer.onTouchEvent(pSceneTouchEvent);
		if (scrollContainerEvent) {
			this.hasTouchDownScrollContainer = false;
			return true;
		} else if (pSceneTouchEvent.getY() > this.scScrollContainer.getY() + this.sLicense.getY() - 8f)
			if (pSceneTouchEvent.isActionDown())
				this.hasTouchDownScrollContainer = true;
			else if (pSceneTouchEvent.isActionUp() && this.hasTouchDownScrollContainer) {
				this.showLicenseDeed();
				this.hasTouchDownScrollContainer = false;
				return true;
			}

		return super.onSceneTouchEvent(pScene, pSceneTouchEvent);
	}

	@Override
	protected void postInitialized() {
		super.postInitialized();
		super.afeContainer.attachChild(this.scScrollContainer);
	}

	@Override
	protected boolean shouldCreateBackButton(ITextureRegion buttonBackground) {
		return super.shouldCreateBackButton(StrokeTextureRegions.region_stroke_6);
	}

	public void showLicenseDeed() {
		EnvironmentVars.MAIN_CONTEXT.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				EnvironmentVars.MAIN_CONTEXT
						.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://creativecommons.org/licenses/by-nd/4.0")));
			}
		});
	}
}
