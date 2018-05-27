package com.bornander.platformer.assets;

import static com.bornander.libgdx.Utils.buildIndexedAnimation;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class CoinAssets {
	
	public final Animation<TextureRegion> spinning;
	public final Animation<TextureRegion> sparkle;
	public final Animation<TextureRegion> spinAndSparkle;
	
	public CoinAssets(TextureAtlas atlas) {
		spinning = buildIndexedAnimation(atlas, 0.14f, false, PlayMode.LOOP, "coin");
		sparkle = buildIndexedAnimation(atlas, 0.14f, false, PlayMode.NORMAL, "sparkle");
		spinAndSparkle = buildIndexedAnimation(atlas, 1.0f / 9.0f, false, PlayMode.NORMAL, "coin", "sparkle");
	}
}
