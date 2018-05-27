package com.bornander.libgdx;

import com.badlogic.gdx.physics.box2d.World;

public class FixedStepWorldUpdater {

	private static final int VELOCITY_ITERATIONS = 8;
	private static final int POSITION_ITERATIONS = 4;
	private static final float STEP_SIZE = 1.0f / 300.0f;
	
	private final World world;
	private float accumulator = 0;
	
	public FixedStepWorldUpdater(World world) {
		this.world = world;
	}
	
	public void update(float delta) {
		world.step(delta, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
		/*
		accumulator += delta;
		while(accumulator >= STEP_SIZE) {
			world.step(STEP_SIZE, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
			accumulator -= STEP_SIZE;
		}
		*/
	}

	public void doEmptyStep() {
		world.step(0, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
	}
}