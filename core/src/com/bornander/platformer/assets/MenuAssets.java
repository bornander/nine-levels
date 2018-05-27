package com.bornander.platformer.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox.CheckBoxStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class MenuAssets {
	private final AssetDescriptor<TextureAtlas> SHEET = new AssetDescriptor<TextureAtlas>("graphics/sheets/menu.atlas", TextureAtlas.class);
	private final TextureAtlas atlas;
	
	public final Skin skin;
	public final NinePatchDrawable button_up;
	public final NinePatchDrawable button_down;
	
	public final Drawable checkbox_on;
	public final Drawable checkbox_off;

	public final Drawable radiobutton_on;
	public final Drawable radiobutton_off;
	
	public final CheckBoxStyle checkStyle;
	public final CheckBoxStyle radioStyle;
	
	public final LabelStyle largeMenuFontStyle;
	public final LabelStyle mediumMenuFontStyle;
	public final LabelStyle smallMenuFontStyle;
	
	
	public MenuAssets(AssetManager manager, FontAssets fontAssets) {
		manager.load(SHEET);
		manager.finishLoading();
		atlas = manager.get(SHEET);
		
		skin = new Skin(Gdx.files.internal("graphics/sheets/menu.skin"));
		
		button_up = new NinePatchDrawable(new NinePatch(atlas.findRegion("button_up"),  6, 6, 5, 9));
		button_down = new NinePatchDrawable(new NinePatch(atlas.findRegion("button_down"),  6, 6, 5, 9));
		
		checkbox_on = new TextureRegionDrawable(atlas.findRegion("checkbox_on"));
		checkbox_off = new TextureRegionDrawable(atlas.findRegion("checkbox_off"));
		radiobutton_on = new TextureRegionDrawable(atlas.findRegion("radiobutton_on"));
		radiobutton_off = new TextureRegionDrawable(atlas.findRegion("radiobutton_off"));
		
		checkStyle = new CheckBoxStyle(checkbox_off, checkbox_on, fontAssets.menuFontMedium, Color.WHITE); 
		radioStyle = new CheckBoxStyle(radiobutton_off, radiobutton_on, fontAssets.menuFontMedium, Color.WHITE);
		
		largeMenuFontStyle = new LabelStyle(fontAssets.menuFontLarge, Color.WHITE);
		mediumMenuFontStyle = new LabelStyle(fontAssets.menuFontMedium, Color.WHITE);
		smallMenuFontStyle = new LabelStyle(fontAssets.menuFontSmall, Color.WHITE);
	}
}
