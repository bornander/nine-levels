package com.bornander.platformer.screens;

import static com.bornander.libgdx.FontRenderer.drawGlyphs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
import com.bornander.libgdx.GameScreen;
import com.bornander.libgdx.Timeout;
import com.bornander.platformer.Assets;
import com.bornander.platformer.PlatformerGame;

public class SplashScreen extends GameScreen {
	
	private final OrthographicCamera camera;
	private final SpriteBatch batch = new SpriteBatch();
	private final GlyphLayout title = new GlyphLayout();
	private final GlyphLayout message = new GlyphLayout();
	private final Timeout enter = new Timeout(2.0f);
	
	
	public SplashScreen(PlatformerGame game) {
		super(game);
		camera = createCamera();
		
		title.setText(Assets.instance.fonts.levelH1, "NINE LEVELS", Color.WHITE, camera.viewportWidth, Align.topLeft, false);
		message.setText(Assets.instance.fonts.levelH2, "LOADING", Color.WHITE, camera.viewportWidth, Align.topLeft, false);
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
	}

	@Override
	public void render() {
		clear(1, 1, 1, 1);
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		
		if (enter.didJustElapse())
			game.setScreen(new CrossFadeScreen(game, this, new MenuScreen(game)));
		
		drawGlyphs(batch, 0, title.height, Align.center, Align.center, Assets.instance.fonts.levelH1, title);
		drawGlyphs(batch, 0, -title.height*2.1f, Align.center, Align.center, Assets.instance.fonts.levelH2, message);
		
		batch.end();
	}
}