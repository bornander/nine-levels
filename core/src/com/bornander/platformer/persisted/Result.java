package com.bornander.platformer.persisted;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.IntMap;

public class Result {

	public final int level;
	private final Preferences prefs;
	
	private long personal;
	private long world;
	private boolean unlockedAchievement;
	
	public Result(int level, Preferences prefs) {
		this.level = level;
		this.prefs = prefs;
		personal = prefs.getLong(String.format("result.%d.personal", level), Long.MIN_VALUE);
		world = prefs.getLong(String.format("result.%d.world", level), Long.MIN_VALUE);
		unlockedAchievement = prefs.getBoolean(String.format("result.%d.unlockedAchievement", level), false);
	}
	
	public static IntMap<Result> load() {
		Preferences prefs =  Gdx.app.getPreferences("com.bornander.platformer");
		IntMap<Result> results = new IntMap<Result>(9);
		for(int i = 1; i <= 9; ++i) {
			results.put(i, new Result(i, prefs));
		}
		
		return results;
	}
	
	private String toString(long time) {
		if (time == Long.MIN_VALUE)
			return "--:--.--";
		float elapsed = time / 1000.0f;
		int minutes = ((int)elapsed) / 60;
		int seconds = ((int)elapsed) % 60;
		int milliSeconds = (int)((elapsed - Math.floor(elapsed)) * 100);
		return String.format("%02d:%02d.%02d", minutes, seconds, milliSeconds);
	}
	
	public String getPersonal() {
		return toString(personal);
	}
	
	public String getWorld() {
		return toString(world);
	}

	public boolean hasUnlockedAchievement() { return unlockedAchievement; }

	public boolean isWorldBest(long elapsed) {
		return world != Long.MIN_VALUE && elapsed < world;
	}

	public boolean isPersonalBest(long elapsed) {
		return personal == Long.MIN_VALUE || elapsed < personal;
	}
	
	public boolean hasPersonal() {
		return personal != Long.MIN_VALUE;
	}
	
	public float getSplit(long elapsed) {
		return (elapsed - personal) / 1000.0f;
	}

	public void save(long time) {
		prefs.putLong(String.format("result.%d.personal", level), time);
		prefs.flush();
	}


	public void saveWorld(long time) {
		prefs.putLong(String.format("result.%d.world", level), time);
		prefs.flush();
	}

	public void markAchievementAsUnlocked() {
		prefs.putBoolean(String.format("result.%d.unlockedAchievement", level), true);
		prefs.flush();
	}
}
