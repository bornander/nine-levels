package com.bornander.platformer.assets;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class SpriteAssets {
	private final AssetDescriptor<TextureAtlas> SHEET = new AssetDescriptor<TextureAtlas>("graphics/sheets/sprites.atlas", TextureAtlas.class);
	private final TextureAtlas atlas;
	
	public final PlayerAssets player;
	public final CoinAssets coin;
	public final SlimeAssets slime;
	public final PumpkinAssets pumpkin;
	public final SmokeAssets smoke;
	public final BirdAssets bird;
	public final MaskedBlobAssets maskedBlob;
	public final BlobAssets blob;
	public final TextureRegion white_bar;
	public final TextureRegion brick;
	public final TextureRegion brick_fragment;
	
	public SpriteAssets(AssetManager manager) {
		manager.load(SHEET);
		manager.finishLoading();
		atlas = manager.get(SHEET);
		player = new PlayerAssets(atlas);
		coin = new CoinAssets(atlas);
		slime = new SlimeAssets(atlas);
		pumpkin = new PumpkinAssets(atlas);
		smoke = new SmokeAssets(atlas);
		bird = new BirdAssets(atlas);
		maskedBlob = new MaskedBlobAssets(atlas);
		blob = new BlobAssets(atlas);
		brick = atlas.findRegion("brick");
		brick_fragment = atlas.findRegion("brick_fragment");
		white_bar = atlas.findRegion("white_bar");
		
	}	
}