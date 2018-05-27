package com.bornander.platformer.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.utils.Disposable;

@SuppressWarnings("FieldCanBeLocal")
public class FontAssets implements Disposable {
    private static final String FONT_PRESS_START = 	"graphics/fonts/prstartk.ttf";
    private static final String FONT_KENNEY = 		"graphics/fonts/KENVECTOR_FUTURE_THIN.TTF";
    private static final String FONT_HUD = 			"graphics/fonts/hud.fnt";

	private static final String CHAR_SET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890\"!`?'.,;:()[]{}<>|/@\\^$-%+=#_&~*";
	public BitmapFont levelH1;
	public BitmapFont levelH2;

	public BitmapFont gameHud;
	public BitmapFont menuFontLarge;
	public BitmapFont menuFontMedium;
	public BitmapFont menuFontSmall;
	public BitmapFont levelButtonLarge;
	public BitmapFont levelButtonSmall;

	public FontAssets() {
		float f = Gdx.graphics.getWidth()/100.0f;
		
		levelH1 = buildOutlined(FONT_PRESS_START, (int)(f * 3.0f), (int)(0.5f*f), Color.BLACK, CHAR_SET);
		levelH2 = buildOutlined(FONT_PRESS_START, (int)(f * 1.0f), (int)(0.2f*f), Color.BLACK, CHAR_SET);
		
		//menuFontLarge = buildDropShadowed(FONT_KENNEY, (int)(f * 8.0f), Color.GRAY, CHAR_SET);
		menuFontLarge = buildOutlined(FONT_PRESS_START, (int)(f * 4.0f), (int)(0.5f*f), Color.BLACK, CHAR_SET);
		menuFontMedium = buildDropShadowed(FONT_KENNEY, (int)(f * 3.0f ), Color.BLACK, CHAR_SET);
		menuFontSmall = buildDropShadowed(FONT_KENNEY, (int)(f * 2.0f ), Color.GRAY, CHAR_SET);
		levelButtonLarge = buildDropShadowed(FONT_KENNEY, (int)(f * 3.0f), Color.GRAY, CHAR_SET);
		levelButtonSmall = buildDropShadowed(FONT_KENNEY, (int)(f * 1.5f), Color.GRAY, CHAR_SET);
		
		gameHud = new BitmapFont(Gdx.files.internal(FONT_HUD));
		gameHud.getData().setScale(4, 4);		
	}
	
	private static BitmapFont buildOutlined(String filename, int size, float borderWidth, Color borderColor, String characters) {
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(filename));
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.size = size;
		parameter.borderColor = borderColor;
		parameter.borderWidth = borderWidth;
		parameter.characters = characters;
		parameter.spaceX =(int)(borderWidth / 2.0f);
		
		parameter.magFilter = TextureFilter.Nearest;
		parameter.minFilter = TextureFilter.Nearest;
		
		BitmapFont font = generator.generateFont(parameter);
		
		font.getData().markupEnabled = true;
		generator.dispose();
		return font;
	}
	
	private static BitmapFont buildDropShadowed(String filename, int size, Color dropShadowColor, String characters) {
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(filename));
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.size = size;
		parameter.characters = characters;
		
		parameter.magFilter = TextureFilter.Nearest;
		parameter.minFilter = TextureFilter.Nearest;
		parameter.shadowOffsetX = parameter.shadowOffsetY = Math.max(1, size / 10);
		
		parameter.shadowColor = Color.GRAY;
		BitmapFont font = generator.generateFont(parameter);
		
		font.getData().markupEnabled = true;
		generator.dispose();
		return font;
	}
	
	@Override
	public void dispose() {
		levelH1.dispose();
		levelH2.dispose();
		gameHud.dispose();
        menuFontLarge.dispose();
        menuFontMedium.dispose();
        menuFontSmall.dispose();
        levelButtonLarge.dispose();
        levelButtonSmall.dispose();
	}
}