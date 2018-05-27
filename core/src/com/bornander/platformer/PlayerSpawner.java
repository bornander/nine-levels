package com.bornander.platformer;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.bornander.libgdx.MapUtils;
import com.bornander.libgdx.Timeout;

public class PlayerSpawner extends GameActor {

	private final Vector2 position;
	private final Timeout inTimeout = new Timeout(2);
	private final Timeout outTimeout = new Timeout(1);
	private final Player player;
	
	public PlayerSpawner(RectangleMapObject mapObject, World world, float unitScale, Controller controller) {
		super(GameActorType.PLAYER_SPAWNER);
		position = MapUtils.getMapObjectPosition(mapObject, unitScale);
		player = new Player(world, mapObject, unitScale, controller);
	}

	@Override
	public void update(float delta, Array<GameActor> actors) {
		inTimeout.update(delta);
		if (inTimeout.hasElapsed())
			outTimeout.update(delta);
		
		if (inTimeout.didJustElapse()) {
			actors.add(player);
		}
	}
	
	@Override
	public void render(Batch batch) {
		float w = !inTimeout.hasElapsed() ? Interpolation.circleIn.apply(inTimeout.getElapsed()) : 1.0f + Interpolation.circleOut.apply(outTimeout.getElapsed())*2.0f;
		float h = 8.0f;
		float hw = w / 2.0f;
		float hh = 0.5f;
		float x = position.x - hw;
		float y = position.y - hh;
		float alpha = !inTimeout.hasElapsed() ? w : 1.0f - Interpolation.circleOut.apply(outTimeout.getElapsed());
		batch.setColor(1, 1, 1, alpha);
		batch.draw(Assets.instance.sprites.white_bar, x, y, hw, hh, w, h, 1, 1, 0);
		batch.setColor(1, 1, 1, 1f);
	}
	
	@Override
	public Vector2 getPosition() {
		return position;
	}
	
	public boolean hasSpawnedPlayer() {
		return inTimeout.hasElapsed();
	}
	
	public Player getPlayer() {
		return player;
	}
}
