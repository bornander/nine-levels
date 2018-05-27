package com.bornander.platformer.assets;

import static com.bornander.libgdx.Utils.buildIndexedAnimation;
import static com.bornander.libgdx.Utils.buildAnimation;
import static com.bornander.libgdx.Utils.copy;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;

public class PumpkinAssets {

	public final Animation<TextureRegion> walk_right;
	public final Animation<TextureRegion> walk_left;
	public final Animation<TextureRegion> fall_right;
	public final Animation<TextureRegion> fall_left;
	public final Animation<TextureRegion> wait;
	
	public PumpkinAssets(TextureAtlas atlas) {
		walk_right = buildIndexedAnimation(atlas, 0.16f, true, PlayMode.LOOP, "pumpkin_walk");
		walk_left = buildIndexedAnimation(atlas, 0.16f, false, PlayMode.LOOP, "pumpkin_walk");
		fall_right = buildAnimation(atlas, 0.16f, true, PlayMode.LOOP, "pumpkin_fall");
		fall_left = buildAnimation(atlas, 0.16f, false, PlayMode.LOOP, "pumpkin_fall");
		
		wait = new Animation<TextureRegion>(0.16f, atlas.findRegion("pumpkin_walk", 0), copy(atlas.findRegion("pumpkin_walk", 0), true));
		wait.setPlayMode(PlayMode.LOOP);
	}	
}
