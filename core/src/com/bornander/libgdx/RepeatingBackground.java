package com.bornander.libgdx; 

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class RepeatingBackground {
	private final SpriteBatch batch = new SpriteBatch();
	private final Texture texture;
	private final OrthographicCamera camera;
	
	private static float offset = 0;
	 
	public RepeatingBackground(Texture texture) {
		this.texture = texture;
		float w = (float)Gdx.graphics.getWidth();
		float h = (float)Gdx.graphics.getHeight();
		float aspectRatio = w / h;
		float th = texture.getHeight();
		
		camera = new OrthographicCamera(th * aspectRatio, th);
		camera.position.set(th / 2.0f, th / 2.0f,0);
	}
	
	public void render() {
		offset += 30.0f * Gdx.graphics.getDeltaTime();
		if (offset > texture.getWidth())
			offset -= texture.getWidth();
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		float cx = camera.position.x;
		float sx = cx - texture.getWidth() - offset;
		
		for(float tx = sx; tx < cx + texture.getWidth(); tx += texture.getWidth())
			batch.draw(texture, tx, 0);
		batch.end();
	}
}