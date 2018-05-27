package com.bornander.platformer.assets;

import static com.bornander.libgdx.Utils.buildIndexedAnimation;
import static com.bornander.libgdx.Utils.buildAnimation;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;

public class BirdAssets {

	public final Animation<TextureRegion> perched_left;
	public final Animation<TextureRegion> perched_right;
	public final Animation<TextureRegion> fly_left;
	public final Animation<TextureRegion> fly_right;
	public final Animation<TextureRegion> dart_left;
	public final Animation<TextureRegion> dart_right;
	public final Animation<TextureRegion> dead;
	
	public BirdAssets(TextureAtlas atlas) {
		perched_left = buildAnimation(atlas, 0.3f, false, PlayMode.LOOP, "bird_perched"); 
		perched_right = buildAnimation(atlas, 0.3f, true, PlayMode.LOOP, "bird_perched");
	
		fly_left = buildIndexedAnimation(atlas, 0.16f, false, PlayMode.LOOP_PINGPONG, "bird_fly");
		fly_right = buildIndexedAnimation(atlas, 0.16f, true, PlayMode.LOOP_PINGPONG, "bird_fly");
		
		dart_left = buildAnimation(atlas, 0.3f, false, PlayMode.LOOP, "bird_dart"); 
		dart_right = buildAnimation(atlas, 0.3f, true, PlayMode.LOOP, "bird_dart");
		
		dead = buildAnimation(atlas, 0.3f, true, PlayMode.LOOP, "bird_dead");
	}	
}
