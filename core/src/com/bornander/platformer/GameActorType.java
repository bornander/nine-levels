package com.bornander.platformer;

public enum GameActorType {
	NONE,
	BRICK,
	COIN,
	SLIME,
	PUMPKIN_SPAWNER,
	PUMPKIN,
	BIRD,
	MASKED_BLOB,
	COIN_PATH,
	PLAYER,
	PLAYER_SPAWNER;
	
	public int getSortKey() {
		return ordinal();
	}
}