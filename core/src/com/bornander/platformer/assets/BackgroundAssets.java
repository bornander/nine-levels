package com.bornander.platformer.assets;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;

public class BackgroundAssets {
	
	private final static AssetDescriptor<Texture> GRASS = new AssetDescriptor<Texture>("graphics/backgrounds/grass.png", Texture.class);
	private final static AssetDescriptor<Texture> SNOW = new AssetDescriptor<Texture>("graphics/backgrounds/snow.png", Texture.class);
	
	public final Texture grass;
	public final Texture snow;
	
	public BackgroundAssets(AssetManager manager) {
		manager.load(GRASS);
		manager.load(SNOW);
		manager.finishLoading();
		grass = manager.get(GRASS);
		snow = manager.get(SNOW);
	}

	public Texture get(String type) {
		if (type.equals("grass"))
			return grass;
		if (type.equals("snow"))
			return snow;
		
		return null;
	}
}
