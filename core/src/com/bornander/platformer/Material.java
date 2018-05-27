package com.bornander.platformer;

import com.bornander.libgdx.InvalidCaseException;

public enum Material {
	NONE,
	GRASS,
	ROCK,
	ICE,
	WOOD;
	
	public float getDensity() {
		switch(this) {
		case GRASS: return 1.0f;
		case ROCK: return 1.0f;
		case ICE: 	return 1.0f;
		case WOOD: 	return 1.0f;
		default:
			throw new InvalidCaseException(this); 
		}
	}
	
	public float getRestitution() {
		switch(this) {
		case GRASS: return 0.0f;
		case ROCK:	return 0.0f;
		case ICE: 	return 0.0f;
		case WOOD: 	return 0.0f;
		default:
			throw new InvalidCaseException(this); 
		}
	}

	public float getFriction() {
		switch(this) {
		case GRASS: return 1.0f;
		case ROCK:	return 0.9f;
		case ICE: 	return 0.0f;
		case WOOD: 	return 1.0f;
		default:
			throw new InvalidCaseException(this);  
		}
	}
	
}