package com.bornander.platformer.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;

public class EffectAssets {

	private final ParticleEffectPool firework;
	
	public EffectAssets() {
		firework = new ParticleEffectPool(load("firework.effect"), 16, 16);
	}
	
	private ParticleEffect load(String name) {
		ParticleEffect effect = new ParticleEffect();
		effect.load(Gdx.files.internal(String.format("graphics/effects/%s", name)), Gdx.files.internal("graphics/effects/"));
		return effect;
	}
	
	public PooledEffect obtainFirework(boolean start) {
		PooledEffect effect = firework.obtain();
		if (start)
			effect.start();
		
		return effect;
	}
}