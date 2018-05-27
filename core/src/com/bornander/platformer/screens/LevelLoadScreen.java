package com.bornander.platformer.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.utils.IntMap;
import com.bornander.libgdx.GameScreen;
import com.bornander.libgdx.Log;
import com.bornander.libgdx.Timeout;
import com.bornander.platformer.Assets;
import com.bornander.platformer.PlatformerGame;
import com.bornander.platformer.persisted.Result;

public class LevelLoadScreen extends GameScreen {
	private final int levelIndex;
	private final String levelFilename;
	private final GameScreen previous;
	private GameScreen next;
	private final ShapeRenderer shapeRenderer;
	private final OrthographicCamera camera;
	private final Timeout fadeOutTimeout = new Timeout(1);
	private final Timeout fadeInTimeout = new Timeout(1, fadeOutTimeout);
	private boolean isLoaded;
	
	public LevelLoadScreen(PlatformerGame game, int levelIndex, String name, GameScreen previous)
	{
		super(game);
		this.previous = previous;
		this.levelIndex = levelIndex;

		levelFilename = String.format("levels/%d.tmx", levelIndex);
		Assets.instance.levels.load(levelFilename, name);
		
		shapeRenderer = new ShapeRenderer();
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.input.setInputProcessor(null);
	}

	@Override
	public void update(float delta) {
		isLoaded = Assets.instance.update();
		fadeOutTimeout.update(delta);
		fadeInTimeout.update(delta);

		if (fadeOutTimeout.hasElapsed()) {
			
			if (isLoaded) {
			    if (next == null) {
                    IntMap<Result> results = Result.load(); 
                    next = new PlayScreen(game, results.get(levelIndex), Assets.instance.levels.get(levelFilename));
                }

				next.update(delta);
			}
		}
		else {
			previous.update(delta);
		}
		
		if (fadeInTimeout.didJustElapse()) {
			Log.info("time to swap screens");
			game.setScreen(next);
		}
		
		camera.update();
	}

	@SuppressWarnings("static-access")
	@Override
	public void render() {
		if (fadeOutTimeout.hasElapsed() && isLoaded)
			next.render();
		else
			previous.render();
		
		Gdx.gl.glEnable(Gdx.gl.GL_BLEND);
		Gdx.gl.glBlendFunc(Gdx.gl.GL_SRC_ALPHA, Gdx.gl.GL_ONE_MINUS_SRC_ALPHA);		
		
		shapeRenderer.setProjectionMatrix(camera.combined);
		if (fadeOutTimeout.hasElapsed() && isLoaded)
			shapeRenderer.setColor(0, 0, 0, Interpolation.circleOut.apply(fadeInTimeout.getInvElapsed()));
		else 
			shapeRenderer.setColor(0, 0, 0, Interpolation.circleIn.apply(fadeOutTimeout.getElapsed()));

		shapeRenderer.begin(ShapeType.Filled);
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		shapeRenderer.rect(-w / 2.0f, -h / 2.0f, w, h);
		shapeRenderer.end();
		Gdx.gl.glDisable(Gdx.gl.GL_BLEND);
	}
	
	@Override
	public void dispose() {
		previous.dispose();
		//super.dispose();
	}
}