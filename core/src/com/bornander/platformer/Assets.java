package com.bornander.platformer;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.Disposable;
import com.bornander.libgdx.Log;
import com.bornander.platformer.assets.BackgroundAssets;
import com.bornander.platformer.assets.EffectAssets;
import com.bornander.platformer.assets.FontAssets;
import com.bornander.platformer.assets.LevelAssets;
import com.bornander.platformer.assets.MenuAssets;
import com.bornander.platformer.assets.MusicAssets;
import com.bornander.platformer.assets.SoundAssets;
import com.bornander.platformer.assets.SpriteAssets;

public class Assets implements Disposable {
	
	public final static Assets instance = new Assets();
	
	public LevelAssets levels;
	public BackgroundAssets backgrounds;
	public SpriteAssets sprites;
	public FontAssets fonts;
	public EffectAssets effects;
	public SoundAssets sounds;
	public MusicAssets music;
	public MenuAssets menu;
	private AssetManager manager;
	 
	private Assets() {
	}
	
	public void initialize(AssetManager assetManager) {
		Log.info("Initializing assets with manager %s", assetManager);
		manager = assetManager;
		manager.setLoader(TiledMap.class, new TmxMapLoader());
		
		levels = new LevelAssets(manager);
		backgrounds = new BackgroundAssets(manager);
		sprites = new SpriteAssets(manager);
		fonts = new FontAssets();
		effects = new EffectAssets();
		sounds = new SoundAssets(assetManager);
		music = new MusicAssets();
		menu = new MenuAssets(assetManager, fonts);
	}
	
	public boolean update() {
		return manager.update();
	}
	
	@Override
	public void dispose() {
		if (fonts != null)
			fonts.dispose();
		
		if (music != null)
			music.dispose();

		if (manager != null)
			manager.dispose();
	}
}