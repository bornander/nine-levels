package com.bornander.platformer.assets;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import static com.bornander.libgdx.Utils.buildAnimation;

public class PlayerAssets {
	public final Animation<TextureRegion> stand_right;
	public final Animation<TextureRegion> stand_left;
	public final Animation<TextureRegion> walk_right;
	public final Animation<TextureRegion> walk_left;
	public final Animation<TextureRegion> jump_right;
	public final Animation<TextureRegion> jump_left;
	public final Animation<TextureRegion> fall_right;
	public final Animation<TextureRegion> fall_left;
	public final Animation<TextureRegion> dead_right;
	public final Animation<TextureRegion> dead_left;
	
	public PlayerAssets(TextureAtlas atlas) {
		stand_right = buildAnimation(atlas, 0.3f, false, PlayMode.LOOP, "player_stand");
		stand_left = buildAnimation(atlas, 0.3f, true, PlayMode.LOOP, "player_stand");
		walk_right = buildAnimation(atlas, 0.10f, false, PlayMode.LOOP, "player_stand", "player_walk1", "player_walk2", "player_walk3");
		walk_left = buildAnimation(atlas, 0.10f, true, PlayMode.LOOP, "player_stand", "player_walk1", "player_walk2", "player_walk3");
		jump_right = buildAnimation(atlas, 0.3f, false, PlayMode.LOOP, "player_jump");
		jump_left = buildAnimation(atlas, 0.3f, true, PlayMode.LOOP, "player_jump");
		fall_right = buildAnimation(atlas, 0.3f, false, PlayMode.LOOP, "player_fall");
		fall_left = buildAnimation(atlas, 0.3f, true, PlayMode.LOOP, "player_fall");
		dead_right = buildAnimation(atlas, 0.3f, false, PlayMode.LOOP, "player_dead");
		dead_left = buildAnimation(atlas, 0.3f, true, PlayMode.LOOP, "player_dead");
	}	
}
