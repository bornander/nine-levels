package com.bornander.platformer.assets;

import static com.bornander.libgdx.Utils.buildIndexedAnimation;
import static com.bornander.libgdx.Utils.buildAnimation;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;

public class SlimeAssets {

	public final Animation<TextureRegion> walk_right;
	public final Animation<TextureRegion> walk_left;
	public final Animation<TextureRegion> wait_right;
	public final Animation<TextureRegion> wait_left;
	
	public SlimeAssets(TextureAtlas atlas) {
		walk_right = buildIndexedAnimation(atlas, 0.16f, true, PlayMode.LOOP, "slime_walk");
		walk_left = buildIndexedAnimation(atlas, 0.16f, false, PlayMode.LOOP, "slime_walk");
		wait_right = buildAnimation(atlas, 0.16f, true, PlayMode.LOOP, "slime_wait");
		wait_left = buildAnimation(atlas, 0.16f, false, PlayMode.LOOP, "slime_wait");
	}	
}