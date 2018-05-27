package com.bornander.platformer;

import static com.bornander.libgdx.MapUtils.getMapObjectPosition;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.bornander.libgdx.MapUtils;
import com.bornander.libgdx.Timeout;

public class PumpkinSpawner extends GameActor {
	private final World world;
	private final OrthographicCamera camera;
	private final Vector2 spawnPosition;
	private final float lifeSpan;
	private final float directionModifier;
	private final Timeout timeout;
	
	private float alternateDirectionModifier = 1.0f;

	public PumpkinSpawner(World world, OrthographicCamera camera, RectangleMapObject mapObject, float unitScale) {
		super(GameActorType.PUMPKIN_SPAWNER);
		this.world = world;
		this.camera = camera;
		
		timeout = new Timeout(MapUtils.getFloat(mapObject, "interval", 8.0f));
		lifeSpan = MapUtils.getFloat(mapObject, "lifeSpan", 7.0f);
		directionModifier = MapUtils.getFloat(mapObject, "directionModifier", 0.0f);
		
		spawnPosition = getMapObjectPosition(mapObject, unitScale);
	}
	
	public void update(float delta, Array<GameActor> actors, Array<GameActor> newActorStore) {
		timeout.update(delta);
		if (timeout.hasElapsed()) {
			if (Math.abs(camera.position.x - spawnPosition.x) < (camera.viewportWidth / 2.0f) + 1)
				Assets.instance.sounds.playPumpkinSpawn();
			
			timeout.reset();
			float direction = directionModifier != 0.0f ? directionModifier : alternateDirectionModifier;
			alternateDirectionModifier *= -1.0f;
			// TODO: Make sure this is pooled and configured in the constructor so that no allocations happen here at runtime
			newActorStore.add(new Pumpkin(world, camera, spawnPosition, lifeSpan, direction));
		}
	}
}