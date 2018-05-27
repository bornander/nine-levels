package com.bornander.platformer.assets;

import static com.bornander.libgdx.Utils.buildIndexedAnimation;
import static com.bornander.libgdx.Utils.buildAnimation;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;

public class MaskedBlobAssets {
	
	public final Animation<TextureRegion> stand_left;
	public final Animation<TextureRegion> stand_right;
	public final Animation<TextureRegion> walk_left;
	public final Animation<TextureRegion> walk_right;
	public final Animation<TextureRegion> mask_left;
	public final Animation<TextureRegion> mask_right;
	
	public MaskedBlobAssets(TextureAtlas atlas) {
		stand_left = buildAnimation(atlas, 0.24f, false, PlayMode.LOOP, "masked_blob_stand");
		stand_right = buildAnimation(atlas, 0.24f, true, PlayMode.LOOP, "masked_blob_stand");
		walk_left = buildIndexedAnimation(atlas, 0.24f, false, PlayMode.LOOP, "masked_blob_walk");
		walk_right = buildIndexedAnimation(atlas, 0.24f, true, PlayMode.LOOP, "masked_blob_walk");
		mask_left = buildIndexedAnimation(atlas, 0.24f, false, PlayMode.LOOP, "masked_blob_mask");
		mask_right = buildIndexedAnimation(atlas, 0.24f, true, PlayMode.LOOP, "masked_blob_mask");
	}
}
