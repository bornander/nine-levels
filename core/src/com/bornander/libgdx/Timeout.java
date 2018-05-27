package com.bornander.libgdx;

public class Timeout {
	
	private float duration;
	private float elapsed;
	
	private boolean justElapsed = true;
	private final Timeout waitFor;
	
	public Timeout(float duration, Timeout waitFor) {
		this.duration = duration;
		this.waitFor = waitFor;
	}
	
	public Timeout(float duration) {
		this(duration, null);
	}
	
	
	
	
	public void reset(float duration) {
		this.duration = duration;
		elapsed = 0;
		justElapsed = true;
	}
	
	public void reset() {
		reset(duration);
	}
	
	public void update(float delta) {
		if (waitFor == null || waitFor.hasElapsed())
			elapsed += delta;
	}
	
	public boolean didJustElapse() {
		if (hasElapsed()) {
			boolean result = justElapsed;
			justElapsed = false;
			return result;
		}
		else {
			return false;
		}
	}
	
	public boolean hasElapsed() {
		return elapsed > duration;
	}
	
	public float getElapsed() {
		return Math.min(elapsed / duration, 1.0f);
	}
	
	public float getInvElapsed() {
		return 1.0f - Math.min(elapsed / duration, 1.0f);
	}
}
