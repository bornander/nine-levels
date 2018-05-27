package com.bornander.platformer;

import static com.bornander.libgdx.Utils.createBodyDef;
import static com.bornander.libgdx.Utils.createCircleShape;
import static com.bornander.libgdx.Utils.createEmptySensorFixtureDef;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.bornander.libgdx.Contactable;
import com.bornander.libgdx.MapUtils;
import com.bornander.libgdx.PolygonPath;

public class CoinPath extends GameActor  implements Contactable  {
	private float[] elapsedTime;
	private float[] distances;
	private boolean[] taken;
	
	private final Level level;
	private final PolygonPath path;
	private final Body[] bodies;

	public CoinPath(Level level, World world, PolygonMapObject mapObject, float unitScale) {
		super(GameActorType.COIN_PATH);
		this.level = level;

		int count = MapUtils.getInt(mapObject, "count", 1);
		elapsedTime = new float[count];
		distances = new float[count];
		bodies = new Body[count];
		taken = new boolean[count];
		
		float coinRadius = 0.25f;
		CircleShape bodyShape = createCircleShape(coinRadius, 0, 0); 

		for(int i = 0; i < count; ++i) {
			elapsedTime[i] = 0;
			distances[i] = i * 0.5f;
			taken[i] = false;

			BodyDef bodyDefinition = createBodyDef(BodyType.KinematicBody);
			FixtureDef bodyFixtureDefinition = createEmptySensorFixtureDef(bodyShape, Physics.CATEGORY_ENEMY, Physics.MASK_ENEMY);
			
			Body body = world.createBody(bodyDefinition);
			body.setTransform(MapUtils.getCenter(mapObject, unitScale), 0);
			body.setUserData(i);
			body.setFixedRotation(true);
			Fixture fixture = body.createFixture(bodyFixtureDefinition);
			fixture.setUserData(this);
			
			bodies[i] = body;			
		}
		bodyShape.dispose();		

		path = new PolygonPath(mapObject.getPolygon(), unitScale);
	}
	
	
	@Override
	protected void update(float delta) {
		final float speed = 1.0f;
		for(int i = 0; i < distances.length; ++i) {
			elapsedTime[i] += delta;
			if (!taken[i]) {
				distances[i] += speed * delta;
				Vector2 p = path.getPosition(distances[i]);
				bodies[i].setTransform(p, 0);
			}
		}
	}
	
	@Override
	public void render(Batch batch) {
		
		for(int i = 0; i < bodies.length; ++i) {
			Body body = bodies[i];
			TextureRegion frame = !taken[i] ? Assets.instance.sprites.coin.spinning.getKeyFrame(elapsedTime[i]) : Assets.instance.sprites.coin.sparkle.getKeyFrame(elapsedTime[i]);
			
			
			float w = 0.5f;
			float h = 0.5f;
			float hw = w / 2.0f;
			float hh = h / 2.0f;
			float x = body.getPosition().x - hw;
			float y = body.getPosition().y - hh;
			batch.draw(frame, x, y, hw, hh, w, h, 1, 1, 0);						
		}
	}


	@Override
	public void handleContact(Fixture sender, Object other) {
		if (other instanceof Player) {
			int index = (Integer)sender.getBody().getUserData();
			if (taken[index])
				return;
			taken[index] = true;
			elapsedTime[index] = 0.0f;
			level.grabCoin();
		}
	}
	
	public int getCoinCount() {
		return distances.length;
	}
}
