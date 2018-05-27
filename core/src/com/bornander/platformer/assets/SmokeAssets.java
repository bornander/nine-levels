package com.bornander.platformer.assets;

import static com.bornander.libgdx.Utils.buildIndexedAnimation;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;

public class SmokeAssets {
	
	public final Animation<TextureRegion> puff;
	
	public SmokeAssets(TextureAtlas atlas) {
		puff = buildIndexedAnimation(atlas, 0.14f, false, PlayMode.NORMAL, "smoke");
	}
}
