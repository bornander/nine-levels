package com.bornander.platformer.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Interpolation;
import com.bornander.libgdx.GameScreen;
import com.bornander.libgdx.Timeout;
import com.bornander.platformer.PlatformerGame;

public class CrossFadeScreen extends GameScreen {
	private final GameScreen previous;
	private final GameScreen next;
	private final ShapeRenderer shapeRenderer;
	private final OrthographicCamera camera;
	private final Timeout fadeOutTimeout;
	private final Timeout fadeInTimeout;
	
	public CrossFadeScreen(PlatformerGame game, GameScreen previous, GameScreen next, float outDuration, float inDuration)
	{
		super(game);
		fadeOutTimeout = new Timeout(outDuration);
		fadeInTimeout = new Timeout(inDuration, fadeOutTimeout);
		
		this.previous = previous;
		this.next = next;
		 
		shapeRenderer = new ShapeRenderer();
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}
	
	public CrossFadeScreen(PlatformerGame game, GameScreen previous, GameScreen next)
	{
		this(game, previous, next, 1.0f, 1.0f);
	}

	@Override
	public void update(float delta) {
		fadeOutTimeout.update(delta);
		fadeInTimeout.update(delta);
		
		if (!fadeOutTimeout.hasElapsed())
			previous.update(delta);
		else 
			next.update(delta);
		
		
		if (fadeInTimeout.didJustElapse())
			game.setScreen(next);
		
		camera.update();
	}

	@SuppressWarnings("static-access")
	@Override
	public void render() {
		if (fadeOutTimeout.hasElapsed())
			next.render();
		else
			previous.render();
		
		Gdx.gl.glEnable(Gdx.gl.GL_BLEND);
		Gdx.gl.glBlendFunc(Gdx.gl.GL_SRC_ALPHA, Gdx.gl.GL_ONE_MINUS_SRC_ALPHA);		
		
		shapeRenderer.setProjectionMatrix(camera.combined);
		if (fadeOutTimeout.hasElapsed())
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
}