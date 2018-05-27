package com.bornander.platformer.screens;

import com.bornander.libgdx.GameScreen;
import com.bornander.platformer.Assets;
import com.bornander.platformer.Level;
import com.bornander.platformer.PlatformerGame;
import com.bornander.platformer.persisted.Result;

public class PlayScreen extends GameScreen {
	private final Level level;
	private final Result result;
	
	private float accumulator = 0;
	private static final float STEP_SIZE = 1.0f / 300.0f;
	
	public PlayScreen(PlatformerGame game, Result result, Level level) {
		super(game);
		this.result = result;
		this.level = level;
		
		this.level.setPlayScreen(this);
	}

	@Override
	public void update(float delta) {
		accumulator += delta;
		while(accumulator >= STEP_SIZE) {
			level.update(STEP_SIZE);
			accumulator -= STEP_SIZE;
		}
		
		//level.update(delta);
	}

	@Override
	public void render() {
		level.render();
	}


	public void won(float elapsedTime) {
		long elapsed = (long)(elapsedTime * 1000);
		
 		game.setScreen(new CrossFadeScreen(game, this, new WonScreen(game, result, elapsed), 0.4f, 0.4f));
 		Assets.instance.music.fadeOut();
	}
	
	public void lost() {
		game.setScreen(new CrossFadeScreen(game, this, new LostScreen(game), 0.4f, 0.4f));
		Assets.instance.music.fadeOut();
	}
}