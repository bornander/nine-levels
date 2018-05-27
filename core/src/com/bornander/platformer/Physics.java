package com.bornander.platformer;

public class Physics {
	public final static short CATEGORY_TERRAIN 			=  1;
	public final static short CATEGORY_TERRAIN_ONEWAY 	=  2;
	public final static short CATEGORY_PLAYER 			=  4;
	public final static short CATEGORY_ENEMY			=  8;
	public final static short CATEGORY_DEBRIS			= 16;
	
	public final static short MASK_TERRAIN			= CATEGORY_TERRAIN | CATEGORY_PLAYER | CATEGORY_ENEMY;
	public final static short MASK_PLAYER			= CATEGORY_TERRAIN | CATEGORY_TERRAIN_ONEWAY | CATEGORY_PLAYER | CATEGORY_ENEMY;
	public final static short MASK_PLAYER_SENSOR    = CATEGORY_TERRAIN | CATEGORY_PLAYER;
	public final static short MASK_ENEMY			= CATEGORY_TERRAIN | CATEGORY_PLAYER | CATEGORY_ENEMY;
	public final static short MASK_DEBRIS			= CATEGORY_TERRAIN;
	
}
