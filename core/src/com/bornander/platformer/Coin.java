package com.bornander.platformer;

import static com.bornander.libgdx.Utils.createBodyDef;
import static com.bornander.libgdx.Utils.createCircleShape;
import static com.bornander.libgdx.Utils.createEmptySensorFixtureDef;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.bornander.libgdx.Contactable;
import com.bornander.libgdx.MapUtils;

public class Coin extends GameActor implements Contactable {
	private final float coinRadius; 
	private final Body body;
	private final Level level;
	private float elapsedTime = 0;
	
	private Animation<TextureRegion> animation;
	private boolean taken = false;

	public Coin(Level level, World world, EllipseMapObject mapObject, float unitScale) {
		super(GameActorType.COIN);
		this.level = level;
		
		coinRadius = 0.5f * mapObject.getEllipse().width * unitScale;
		CircleShape bodyShape = createCircleShape(coinRadius, 0, 0); 
		
		BodyDef bodyDefinition = createBodyDef(BodyType.StaticBody);
		FixtureDef bodyFixtureDefinition = createEmptySensorFixtureDef(bodyShape, Physics.CATEGORY_ENEMY, Physics.MASK_ENEMY);
		
		body = world.createBody(bodyDefinition);
		body.setTransform(MapUtils.getCenter(mapObject, unitScale), 0);
		body.setUserData(this);
		body.setFixedRotation(true);
		Fixture ixture = body.createFixture(bodyFixtureDefinition);
		fixture.setUserData(this);
		
		bodyShape.dispose();		
		
		animation = Assets.instance.sprites.coin.spinning;
	}
	
	@Override
	protected void update(float delta) {
		elapsedTime += delta;
	}
	
	@Override
	public void render(Batch batch) {
		TextureRegion frame = animation.getKeyFrame(elapsedTime);
		float w = coinRadius * 2.0f;
		float h = coinRadius * 2.0f;
		float hw = w / 2.0f;
		float hh = h / 2.0f;
		float x = body.getPosition().x - hw;
		float y = body.getPosition().y - hh;
		batch.draw(frame, x, y, hw, hh, w, h, 1, 1, 0);			
	}

	@Override
	public void handleContact(Fixture sender, Object other) {
		if (taken)
			return;
		
		if (other instanceof Player) {
			taken = true;
			elapsedTime = 0.0f;
			animation = Assets.instance.sprites.coin.sparkle;
			level.grabCoin();
		}
	}
}