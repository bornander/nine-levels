package com.bornander.platformer;

import java.util.Comparator;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public abstract class GameActor {
	
	private static class ActorRenderComparator implements Comparator<GameActor> {

		@Override
		public int compare(GameActor left, GameActor right) {
			return left.type.getSortKey() - right.type.getSortKey();
		}
	}
	
	public static Comparator<GameActor> RenderSorter = new ActorRenderComparator();
	
	public final GameActorType type;
	
	protected GameActor(GameActorType type) {
		this.type = type;
	}
	
	protected int getSortOrder() {
		return 0;
	}

	protected void update(float delta) {
	}
	
	protected void update(float delta, Array<GameActor> actors) {
	}
	
	public void update(float delta, Array<GameActor> actors, Array<GameActor> newActorStore) {
		update(delta, actors);
		update(delta);
	}
	
	public void render(Batch batch) {
	}
	
	public Vector2 getPosition() {
		throw new UnsupportedOperationException();
	}
	
	public boolean isType(GameActorType type) {
		return this.type == type;
	}
	
	public static GameActor getIfNotAlreadySet(GameActorType type, GameActor current, Array<GameActor> actors) {
		if (current != null)
			return current;
		
		for(int i = 0; i < actors.size; ++i) {
			GameActor actor = actors.get(i);
			if (actor.type == type)
				return actor;
		}
		return null;
	}
	
	public static GameActor getActor(GameActorType type, Array<GameActor> actors) {
		for(int i = 0; i < actors.size; ++i) {
			GameActor actor = actors.get(i);
			if (actor.type == type)
				return actor;
		}
		return null; // TODO: Throw exception?
	}
	
}
