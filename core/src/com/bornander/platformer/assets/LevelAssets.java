package com.bornander.platformer.assets;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.bornander.libgdx.Log;
import com.bornander.platformer.Level;

public class LevelAssets {
	private final AssetManager manager;

	private String name;
	private String loaded;
	
	public LevelAssets(AssetManager manager) {
		this.manager = manager;
	}
	
	public void load(String file, String name) {
		Log.info("Loading level from file %s", file);
		if (loaded != null) {
			Log.info("Unloading loaded level %s", loaded);
			manager.unload(loaded);
			if (manager.isLoaded(loaded)) {
				Log.info("File " + loaded + " is still loaded!!!");
			}
		}
		this.name = name;
		manager.load(file, TiledMap.class);
	}
	
	public Level get(String file) {
		TiledMap map = (TiledMap)manager.get(file);
		loaded = file;
		return new Level(map, name);
	}
}