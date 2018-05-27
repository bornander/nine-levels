package com.bornander.platformer;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.IntMap;
import com.bornander.platformer.persisted.Result;
import com.bornander.platformer.persisted.Settings;
import com.bornander.platformer.screens.SplashScreen;

public class PlatformerGame extends Game implements  OnlineConnectivityCallback {
	
	public final OnlineConnectivity onlineConnectivity;
	
	public PlatformerGame(OnlineConnectivity onlineConnectivity)
	{
		this.onlineConnectivity = onlineConnectivity;
	}
	
	@Override
	public void create () {
		Assets.instance.initialize(new AssetManager());
		setScreen(new SplashScreen(this));
		//setScreen(new MenuScreen(this));
		//setScreen(new SettingsScreen(this, 10, 10));

		Gdx.app.setLogLevel(Application.LOG_DEBUG);
	}
	
	@Override
	public void dispose () {
		Assets.instance.dispose();
	}

	@Override
	public void onlineSignIn() {
		Settings settings = Settings.load();
		settings.setWasSignedIn(true);
		settings.save();
		Screen screen = getScreen();
		if (screen instanceof OnlineConnectivityCallback) {
			((OnlineConnectivityCallback)screen).onlineSignIn();
		}
	}

	@Override
	public void onlineSignOut() {
		Settings settings = Settings.load();
		settings.setWasSignedIn(false);
		settings.save();
		Screen screen = getScreen();
		if (screen instanceof OnlineConnectivityCallback) {
			((OnlineConnectivityCallback)screen).onlineSignOut();
		}

	}

	@Override
	public void onlineError() {
		Screen screen = getScreen();
		if (screen instanceof OnlineConnectivityCallback) {
			((OnlineConnectivityCallback)screen).onlineError();
		}

	}

	@Override
	public void onlineTopScore(int scoreIndex, long score) {
	    IntMap<Result> result = Result.load();
	    result.get(scoreIndex).saveWorld(score);
	    
	    if (screen instanceof OnlineConnectivityCallback) {
	    	((OnlineConnectivityCallback)screen).onlineTopScore(scoreIndex, score);
	    }
	}

	@Override
	public void onlineAchivementUnlocked(int index) {
		IntMap<Result> results = Result.load();
		Result result = results.get(index);
		result.markAchievementAsUnlocked();
	}
}
