package com.bornander.platformer.assets;

import static com.bornander.libgdx.Utils.buildIndexedAnimation;
import static com.bornander.libgdx.Utils.buildAnimation;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;

public class BlobAssets {
	public final Animation<TextureRegion> stand_left;
	public final Animation<TextureRegion> stand_right;
	public final Animation<TextureRegion> walk_left;
	public final Animation<TextureRegion> walk_right;
	public final Animation<TextureRegion> dead;
	
	public BlobAssets(TextureAtlas atlas) {
		stand_left = buildAnimation(atlas, 0.24f, false, PlayMode.LOOP, "blob_stand");
		stand_right = buildAnimation(atlas, 0.24f, true, PlayMode.LOOP, "blob_stand");
		walk_left = buildIndexedAnimation(atlas, 0.24f, false, PlayMode.LOOP, "blob_walk");
		walk_right = buildIndexedAnimation(atlas, 0.24f, true, PlayMode.LOOP, "blob_walk");
		dead = buildIndexedAnimation(atlas, 0.24f, false, PlayMode.LOOP, "blob_dead");
	}
}