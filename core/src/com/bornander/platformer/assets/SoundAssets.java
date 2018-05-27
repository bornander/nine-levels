package com.bornander.platformer.assets;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.RandomXS128;
import com.bornander.platformer.persisted.Settings;

public class SoundAssets {

	private final static RandomXS128 RND = new RandomXS128();
	private final static AssetDescriptor<Sound> COIN = new AssetDescriptor<Sound>("audio/sound/coin.wav", Sound.class); 
	private final static AssetDescriptor<Sound> BRICK = new AssetDescriptor<Sound>("audio/sound/brick.wav", Sound.class);
	private final static AssetDescriptor<Sound> JUMP = new AssetDescriptor<Sound>("audio/sound/jump.wav", Sound.class);
	private final static AssetDescriptor<Sound> MASKED_BLOB_DAMAGE = new AssetDescriptor<Sound>("audio/sound/masked_blob_damage.wav", Sound.class);
	private final static AssetDescriptor<Sound> PLAYER_DEAD = new AssetDescriptor<Sound>("audio/sound/player_dead.wav", Sound.class);
	private final static AssetDescriptor<Sound> PUMPKIN_SPAWN = new AssetDescriptor<Sound>("audio/sound/pumpkin_spawn.wav", Sound.class);
	private final static AssetDescriptor<Sound> PUMPKIN_EXPLODE = new AssetDescriptor<Sound>("audio/sound/pumpkin_explode.wav", Sound.class);
	private final static AssetDescriptor<Sound> FIREWORK = new AssetDescriptor<Sound>("audio/sound/firework.wav", Sound.class);
	private final static AssetDescriptor<Sound> LEVEL_WON = new AssetDescriptor<Sound>("audio/sound/level_won.wav", Sound.class);
	private final static AssetDescriptor<Sound> BUTTON = new AssetDescriptor<Sound>("audio/sound/button.wav", Sound.class);
	private final static AssetDescriptor<Sound> SLIME_DEAD = new AssetDescriptor<Sound>("audio/sound/slime_dead.wav", Sound.class);
	
	private final Sound coin;
	private final Sound brick;
	private final Sound jump;
	private final Sound masked_blob_damage;
	private final Sound player_dead;
	private final Sound pumpkin_spawn;
	private final Sound pumpkin_explode;
	private final Sound firework;
	private final Sound level_won;
	private final Sound button;
	private final Sound slime_dead;
	
	private float volume = 1.0f;
	
	private boolean enabled;
	
	public SoundAssets(AssetManager assetManager) {
		
		assetManager.load(COIN);
		assetManager.load(BRICK);
		assetManager.load(JUMP);
		assetManager.load(MASKED_BLOB_DAMAGE);
		assetManager.load(PLAYER_DEAD);
		assetManager.load(PUMPKIN_SPAWN);
		assetManager.load(PUMPKIN_EXPLODE);
		assetManager.load(FIREWORK);
		assetManager.load(LEVEL_WON);
		assetManager.load(BUTTON);
		assetManager.load(SLIME_DEAD);
		assetManager.finishLoading();
		
		coin = assetManager.get(COIN);
		brick = assetManager.get(BRICK);
		jump = assetManager.get(JUMP);
		masked_blob_damage = assetManager.get(MASKED_BLOB_DAMAGE);
		player_dead = assetManager.get(PLAYER_DEAD);
		pumpkin_spawn = assetManager.get(PUMPKIN_SPAWN);
		pumpkin_explode = assetManager.get(PUMPKIN_EXPLODE);
		firework = assetManager.get(FIREWORK);
		level_won = assetManager.get(LEVEL_WON);
		button = assetManager.get(BUTTON);
		slime_dead = assetManager.get(SLIME_DEAD);
		
		enabled = Settings.load().sound;
	}
	
	
	private static float getPitch(float min, float max) {
		return min + RND.nextFloat() * (max - min);
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}	
	
	public void playCoin() {
		if (enabled)
			coin.play(volume, getPitch(0.95f, 1.05f), 0.0f);
	}
	
	public void playBrick() {
		if (enabled)
			brick.play(volume, getPitch(0.95f, 1.05f), 0.0f);
	}

	public void playJump() {
		if (enabled)
			jump.play(volume * 0.25f, getPitch(0.95f, 1.05f), 0.0f);
	}
	
	public void playMaskedBlobDamage() {
		if (enabled)
			masked_blob_damage.play(volume, getPitch(0.95f, 1.05f), 0.0f);
	}
	
	public void playPlayerDead() {
		if (enabled)
			player_dead.play(volume, getPitch(0.95f, 1.05f), 0.0f);
	}

	public void playPumpkinSpawn() {
		if (enabled)
			pumpkin_spawn.play(volume, getPitch(0.95f, 1.05f), 0.0f);
	}

	public void playPumpkinExplode() {
		if (enabled)
			pumpkin_explode.play(volume * 0.5f, getPitch(0.95f, 1.05f), 0.0f);
	}

	public void playFirework() {
		if (enabled)
			firework.play(volume * 0.5f, getPitch(0.75f, 1.05f), 0.0f);
	}	
	
	public void playLevelWon() {
		if (enabled)
			level_won.play();
	}
	
	public void playButton() {
		if (enabled)
			button.play();
	}
	
	public void playSlimeDead() {
		if (enabled)
			slime_dead.play();
	}
}