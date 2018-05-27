package com.bornander.platformer;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;

public class Background {

	private final Texture texture;
	
	private final float w;
	private final float h;
	
	public Background(Texture texture, float unitScale) {
		this.texture = texture;
		w = texture.getWidth() * unitScale;
		h = texture.getHeight() * unitScale;
	}
	
	public void render(Batch batch, Camera camera) {
		
		//float cx1 = camera.position.x - camera.viewportWidth;
		//float cx2 = camera.position.x + camera.viewportWidth;

		for(float x = -camera.viewportWidth; x < camera.position.x + camera.viewportWidth; x += w) {
			batch.draw(texture, x, 0, w, h);	
		}

		
		
		
		
		
		//for(float x = 0; x < camera.position.x + camera.viewportWidth; x += w) {
		//	batch.draw(texture, x, 0, w, h);	
		//}
	}
}
