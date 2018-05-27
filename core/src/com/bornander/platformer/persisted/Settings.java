package com.bornander.platformer.persisted;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.bornander.libgdx.Log;
import com.bornander.platformer.Assets;

public class Settings {
	private final Preferences preferences;
	
	public boolean sound;
	public boolean music;
	private int controllerSize;
	private long lastScoreRequest;
	private long lastAchievementRequest;
	private boolean wasSignedIn;
	
	
	
	public Settings(Preferences preferences) {
		this.preferences = preferences;
		sound = preferences.getBoolean("settings.sound", true);
		music = preferences.getBoolean("settings.music", true);
		controllerSize = preferences.getInteger("settings.controllerSize", 2);
		lastScoreRequest = preferences.getLong("settings.lastScoreRequest", 0);
		lastAchievementRequest = preferences.getLong("settings.lastAchievementRequest", 0);
		wasSignedIn = preferences.getBoolean("settings.wasSignedIn", false);
	}
	
	
	public void toogleSound() {
		sound = !sound;
	}

	public void toogleMusic() {
		music = !music;
	}
	
	public void setControllerSize(int size) {
		controllerSize = size;
	}
	
	public void setLastScoreRequest() {
		lastScoreRequest = System.currentTimeMillis();
	}
	public void setLastAchievementRequest() {
		lastAchievementRequest = System.currentTimeMillis();
	}
	
	public void setWasSignedIn(boolean value) {
		wasSignedIn = value;
	}
	
	public float getControllerFactor() {
		switch (controllerSize) {
		case 1: return 1.0f;
		case 2: return 1.25f;
		case 3: return 1.5f;
		default:
			return 2.0f;
		}
	}
	
	public static Settings load() {
		return new Settings(Gdx.app.getPreferences("com.bornander.platformer.settings"));
	}
	
	public void save() {
		preferences.putBoolean("settings.sound", sound);
		preferences.putBoolean("settings.music", music);
		preferences.putInteger("settings.controllerSize", controllerSize);
		preferences.putLong("settings.lastScoreRequest", lastScoreRequest);
		preferences.putLong("settings.lastAchievementRequest", lastAchievementRequest);
		preferences.putBoolean("settings.wasSignedIn", wasSignedIn);
		preferences.flush();
		Log.info("Saving settings %s", preferences);
		Assets.instance.sounds.setEnabled(sound);
	}

	public boolean wasSignedIn() {
		return wasSignedIn;
	}

	public boolean isControllerSmall() {
		return controllerSize == 1;
	}

	public boolean isControllerMedium() {
		return controllerSize == 2;
	}
	
	public boolean isControllerLarge() {
		return controllerSize == 3;
	}

	public boolean isControllerVeryLarge() {
		return controllerSize == 4;
	}	
	
	public boolean isScoreRequestDue() {
		Log.info("isScoreRequestDue A " + System.currentTimeMillis());
		Log.info("isScoreRequestDue B " + lastScoreRequest);
		return (System.currentTimeMillis() - lastScoreRequest) > 1000 * 60 * 10;
	}

	public boolean isAchievementRequestDue() { return (System.currentTimeMillis() - lastAchievementRequest) > 1000 * 60 * 60;}



}
