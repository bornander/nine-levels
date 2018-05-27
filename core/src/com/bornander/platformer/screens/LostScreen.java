package com.bornander.platformer.screens;

import static com.bornander.libgdx.FontRenderer.drawGlyphs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.utils.Align;
import com.bornander.libgdx.GameScreen;
import com.bornander.libgdx.RepeatingBackground;
import com.bornander.libgdx.Timeout;
import com.bornander.platformer.Assets;
import com.bornander.platformer.PlatformerGame;

public class LostScreen extends GameScreen {

	private final OrthographicCamera camera;
	private final SpriteBatch batch = new SpriteBatch();
	private final GlyphLayout message = new GlyphLayout();
	
	private final Timeout enter = new Timeout(2.0f);
	private final Timeout wait = new Timeout(0.5f, enter);
	private final Timeout exit = new Timeout(2.0f, wait);
	
	private final RepeatingBackground background = new RepeatingBackground(Assets.instance.backgrounds.snow);
	
	public LostScreen(PlatformerGame game) {
		super(game);
		camera = createCamera();
		
		message.setText(Assets.instance.fonts.levelH1, "[RED]GAME OVER", Color.WHITE, camera.viewportWidth, Align.topLeft, false);
	}
	
    private static OrthographicCamera createCamera() {
        float aspectRatio = (float)Gdx.graphics.getHeight() / (float)Gdx.graphics.getWidth();
        float verticalHeight = 600.0f;
        float viewportWidth = verticalHeight / aspectRatio;

        OrthographicCamera camera = new OrthographicCamera(viewportWidth, verticalHeight);
        camera.update();
        return camera;
    }	

	@Override
	public void update(float delta) {
		enter.update(delta);
		wait.update(delta);
		exit.update(delta);
	}

	@Override
	public void render() {
		//clear();
		background.render();
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		
		float h = camera.viewportHeight;
		
		float y = 2000;
		if (!enter.hasElapsed()) {
			y = Interpolation.bounceOut.apply(h / 2.0f + message.height, 0, enter.getElapsed());
		}
		else {
			if (!wait.hasElapsed()) {
				y = 0;
			}
			else 
			{
				if (!exit.hasElapsed()) {
					y = Interpolation.circleIn.apply(0, -(h / 2.0f + message.height), exit.getElapsed());
				}
			}
		}
		
		if (wait.didJustElapse())
			game.setScreen(new CrossFadeScreen(game, this, new MenuScreen(game)));
		
		drawGlyphs(batch, 0, y, Align.center, Align.center, Assets.instance.fonts.levelH1, message);
		
		batch.end();
	}
}