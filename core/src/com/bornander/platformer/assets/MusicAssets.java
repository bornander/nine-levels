package com.bornander.platformer.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Disposable;
import com.bornander.libgdx.Timeout;
import com.bornander.platformer.persisted.Settings;

public class MusicAssets implements Disposable {

	private enum State {
		STOPPED, 
		FADE_IN, 
		PLAYING, 
		FADE_OUT
	}
	
	private final float FADE_DURATION = 1.0f;
	
	private final Music grass;
	private final Music snow;
	private final Music menu;
	private final Music[] allMusic;
	
	private final Timeout fadeTimeout = new Timeout(FADE_DURATION);
	private State state = State.STOPPED;
	private Music current;
	private Music next;
	
	private boolean enabled;
	
	public MusicAssets() {
		allMusic = new Music[] {
				grass = load("Grass.mp3"),
				snow = load("Snow.mp3"),
				menu = load("Menu.mp3"),
		};
		
		enabled = Settings.load().music;
	}
	
	private static Music load(String filename) {
		return Gdx.audio.newMusic(Gdx.files.internal(String.format("audio/music/%s", filename)));
	}
	
	public void update(float delta) {
		final float musicVolume = 0.5f; 
		fadeTimeout.update(delta);
		float progress = fadeTimeout.getElapsed();
		if (current == null)
			return;
		
		switch (state) {
		case FADE_IN:
			current.setVolume(MathUtils.lerp(0, musicVolume, progress));
			if (fadeTimeout.didJustElapse())
				state = State.PLAYING;
			break;
		case PLAYING:
			break;
		case FADE_OUT:
			current.setVolume(MathUtils.lerp(musicVolume, 0.0f, progress));
			if (fadeTimeout.didJustElapse()) {
				state = State.STOPPED;
				current.stop();
				current = null;
				if (next != null) {
					play(next);
				}
			}
			break;
		case STOPPED:
			break;
		}
	}	
	
	private void play(Music toPlay) {
		if (!enabled)
			return;
		
		if (toPlay == current)
			return;
		
		if (current == null || !current.isPlaying()) {
			fadeTimeout.reset(FADE_DURATION);
			state = State.FADE_IN;
			current = toPlay;
			if (enabled) {
				current.setVolume(0);
				current.setLooping(true);
				current.play();
			}
			next = null;
		} else {
			next = toPlay;
			fadeTimeout.reset(FADE_DURATION);
			state = State.FADE_OUT;
		}	
	}
	
	public void playGrass() { play(grass); }

	public void playSnow() { play(snow); }

	public void playMenu() { play(menu); }
	
	public void resume() {
		if (next != null) {
			play(next);
		}
	}	
	
	public void stopAll() {
		for (Music music : allMusic)
			music.stop();
		
		current = null;
		next = null;
	}	
	
	
	@Override
	public void dispose() {
		for(Music music : allMusic)
			music.dispose();
		
	}

	public void fadeOut() {
		next = null;
		fadeTimeout.reset(FADE_DURATION);
		state = State.FADE_OUT;
		
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		if (!enabled)
			stopAll();
	}
}
