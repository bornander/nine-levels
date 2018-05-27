package com.bornander.libgdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.bornander.platformer.Assets;
import com.bornander.platformer.PlatformerGame;

public abstract class GameScreen implements Screen {
	public final PlatformerGame game;
	
	public GameScreen(PlatformerGame game) {
		this.game = game;
	}
	
	protected void clear(float r, float g, float b, float a) {
		Gdx.gl.glClearColor(r, g, b, a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	}
	
	protected void clear() {
		clear(0, 0, 0, 1);
	}
	
	@Override
	public void show() {
		Log.debug("Showing screen %s", this);
	}
	
	public abstract void update(float delta);
	public abstract void render();
	
	@Override
	public void render(float delta) {
		// Assets.instance.music.update(delta);
		Assets.instance.music.update(delta);
		update(delta);
		render();
	}

	@Override
	public void resize(int width, int height) {
		Log.debug("Resizing screen %s to (%d, %d)", this, width, height);
	}

	@Override
	public void pause() {
		Log.debug("Pause screen %s", this);
	}

	@Override
	public void resume() {
		Log.debug("Resume screen %s", this);
	}

	@Override
	public void hide() {
		Log.debug("Hide screen %s", this);
	}

	@Override
	public void dispose() {
		Log.debug("Dispose screen %s", this);
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
}