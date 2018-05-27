package com.bornander.platformer.screens;

import static com.bornander.libgdx.FontRenderer.drawGlyphs;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.bornander.libgdx.GameScreen;
import com.bornander.libgdx.RepeatingBackground;
import com.bornander.libgdx.Timeout;
import com.bornander.platformer.Assets;
import com.bornander.platformer.PlatformerGame;
import com.bornander.platformer.persisted.Result;

public class WonScreen extends GameScreen {

	private final static Random RND = new Random();  
	
	private final OrthographicCamera camera;
	private final SpriteBatch batch = new SpriteBatch();
	private final GlyphLayout message = new GlyphLayout();
	
	private final GlyphLayout resultMessage = new GlyphLayout();
	private final GlyphLayout splitMessage = new GlyphLayout();

	private final Timeout effectTimeout = new Timeout(0.25f);
	private final Timeout enter = new Timeout(2.0f);
	private final Timeout wait = new Timeout(2.5f, enter);
	private final Timeout exit = new Timeout(2.0f, wait);
	
	
	private final Array<PooledEffect> effects = new Array<PooledEffect>(); 
	
	private final RepeatingBackground background = new RepeatingBackground(Assets.instance.backgrounds.grass);
	
	public WonScreen(PlatformerGame game, Result result, long elapsed) {
		super(game);
		camera = createCamera();
		
		message.setText(Assets.instance.fonts.levelH1, "LEVEL COMPLETED", Color.WHITE, camera.viewportWidth, Align.topLeft, false);
		
		elapsed = elapsed + 60000;
		
		if (result.isWorldBest(elapsed)) {
			resultMessage.setText(Assets.instance.fonts.levelH1, "[YELLOW]WORLD RECORD!", Color.WHITE, camera.viewportWidth, Align.topLeft, false);
		}
		else {
			if (result.isPersonalBest(elapsed)) {

				resultMessage.setText(Assets.instance.fonts.levelH1, "PERSONAL [YELLOW]BEST!", Color.WHITE, camera.viewportWidth, Align.topLeft, false);
			}
			else {
				resultMessage.setText(Assets.instance.fonts.levelH1, "WELL DONE", Color.WHITE, camera.viewportWidth, Align.topLeft, false);
			}
		}
		
		if (result.hasPersonal()) {
			float split = result.getSplit(elapsed);
			if (split < 0)
				splitMessage.setText(Assets.instance.fonts.levelH2, String.format("SPLIT [GREEN]%.2f", split), Color.WHITE, camera.viewportWidth, Align.topLeft, false);				
			else
				splitMessage.setText(Assets.instance.fonts.levelH2, String.format("SPLIT [RED]+%.2f", split), Color.WHITE, camera.viewportWidth, Align.topLeft, false);
		}
		
		if (result.isWorldBest(elapsed) || result.isPersonalBest(elapsed)) {
			result.save(elapsed);
			game.onlineConnectivity.submitScore(result.level, elapsed);
		}

		if (!result.hasUnlockedAchievement()) {
			game.onlineConnectivity.unlockAchievement(result.level);
		}
	}
	
    private static OrthographicCamera createCamera() {
        float aspectRatio = (float)Gdx.graphics.getHeight() / (float)Gdx.graphics.getWidth();
        float verticalHeight = 600.0f;
        float viewportWidth = verticalHeight / aspectRatio;

        OrthographicCamera camera = new OrthographicCamera(viewportWidth, verticalHeight);
        //camera.position.set(viewportWidth / 2.0f, verticalHeight / 2.0f, 1.0f);
        camera.update();
        return camera;
    }	

	@Override
	public void update(float delta) {
		enter.update(delta);
		wait.update(delta);
		exit.update(delta);
		effectTimeout.update(delta);
		if (effectTimeout.didJustElapse())
		{
			effectTimeout.reset(0.2f + RND.nextFloat() * 0.5f);
			PooledEffect e = Assets.instance.effects.obtainFirework(true);
			
			float x = camera.viewportWidth * 0.75f * (RND.nextFloat() - 0.5f);
			float y = RND.nextFloat() * camera.viewportHeight / 2.0f;
			e.setPosition(x, y);
			effects.add(e);
			Assets.instance.sounds.playFirework();
		}
		for(PooledEffect effect : effects) {
			effect.update(delta);
		}
	}

	@Override
	public void render() {
		//clear();
		
		background.render();
		
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		
		float h = camera.viewportHeight;
		float y1 = 2000;
		float y2 = 2000;
		float y3 = 2000;
		
		if (!enter.hasElapsed()) {
			y1 = Interpolation.bounceOut.apply(h / 2.0f + message.height, message.height, enter.getElapsed());
			y2 = Interpolation.bounceOut.apply(-(h / 2.0f + message.height), -message.height, enter.getElapsed());
			if (splitMessage != null)
				y3 = Interpolation.circleOut.apply(-(h/2.0f + splitMessage.height), -(h/2.0f - splitMessage.height), enter.getElapsed());
		}
		else {
			if (!wait.hasElapsed()) {
				y1 = message.height;
				y2 = -message.height;
				if (splitMessage != null)
					y3 = -(h/2.0f - splitMessage.height);
			}
			else 
			{
				if (!exit.hasElapsed()) {
					y1 = Interpolation.circleIn.apply(message.height, -(h / 2.0f + message.height), exit.getElapsed());
					y2 = Interpolation.circleIn.apply(-message.height, (h / 2.0f + message.height), exit.getElapsed());
					if (splitMessage != null)
						y3 = Interpolation.circleIn.apply(-(h/2.0f - splitMessage.height), -(h/2.0f + splitMessage.height), exit.getElapsed());
				}
			}
		}
		
		if (wait.didJustElapse())
			game.setScreen(new CrossFadeScreen(game, this, new MenuScreen(game)));
		
		drawGlyphs(batch, 0, y1, Align.center, Align.center, Assets.instance.fonts.levelH1, message);
		drawGlyphs(batch, 0, y2, Align.center, Align.center, Assets.instance.fonts.levelH1, resultMessage);
		drawGlyphs(batch, 0, y3, Align.center, Align.center, Assets.instance.fonts.levelH2, splitMessage);
		
		for(PooledEffect effect : effects) {
			effect.draw(batch);
		}
		
		
		batch.end();
	}
	
	@Override
	public void dispose() {
		super.dispose();
		for(PooledEffect effect : effects)
			effect.free();
	}
}