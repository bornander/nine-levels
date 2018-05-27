package com.bornander.platformer;


import static com.bornander.libgdx.Utils.createBodyDef;
import static com.bornander.libgdx.Utils.createFixtureDef;
import static com.bornander.libgdx.Utils.renderFrame;
import static java.lang.Math.abs;
import static java.lang.Math.signum;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.bornander.libgdx.Collidable;
import com.bornander.libgdx.MapUtils;
import com.bornander.libgdx.Timeout;

public class Bird extends GameActor implements Collidable {
	
	private enum State {
		PERCHED,
		FLY,
		DASH,
		DYING,
		DEAD
	}
	
	private final static float bodyRadius = 0.45f;
	private final static float velocity = 0.5f;
	
	private final Body body;

	private float elapsedTime;
	private Animation<TextureRegion> animation;
	
	private State state = State.PERCHED;
	
	private final Timeout timeout = new Timeout(1);
	
	private final Vector2 positionDelta = new Vector2(1000, 1000);
	
	private Player player;

	public Bird(World world, RectangleMapObject mapObject, float unitScale) {
		super(GameActorType.BIRD);
		
		CircleShape bodyShape = new CircleShape(); 
		bodyShape.setRadius(bodyRadius);
		
		
		BodyDef bodyDefinition = createBodyDef(BodyType.KinematicBody);
		FixtureDef bodyFixtureDefinition = createFixtureDef(bodyShape, 1.0f, 1.0f, 0.0f, false, Physics.CATEGORY_ENEMY, Physics.MASK_ENEMY);
		
		body = world.createBody(bodyDefinition);
		
		Vector2 position = MapUtils.getMapObjectPosition(mapObject, unitScale);
		position.y -= (1.0f - 2.0f * bodyRadius) / 2.0f;
		
		body.setTransform(position, 0);
		body.setUserData(this);
		body.setFixedRotation(true);
		Fixture fixture = body.createFixture(bodyFixtureDefinition);
		fixture.setUserData(this);
		
		bodyShape.dispose();		
		
		animation = Assets.instance.sprites.bird.perched_left;
	}
	
	private void setState(State newState, Player player) {
		elapsedTime = 0;
		state = newState;
		switch(newState) {
		case FLY:
			timeout.reset(1.0f);
			break;
		case DASH:
			body.setLinearVelocity(signum(positionDelta.x) * velocity * 5.0f, 0.0f);
			timeout.reset(2.0f);
			break;
		case DEAD:
			player.applyUpPush();
			body.setType(BodyType.DynamicBody);
			float torque = Math.abs(body.getLinearVelocity().x) > 0.1f ? Math.signum(body.getLinearVelocity().x) : 1.0f;
			body.setAngularVelocity(2 * torque);
			animation = Assets.instance.sprites.bird.dead;
			break;
		default:
		}
	}
	
	@Override
	protected void update(float delta, Array<GameActor> actors) {
		elapsedTime += delta;
		timeout.update(delta);
		
		player = (Player)getIfNotAlreadySet(GameActorType.PLAYER, player, actors);
		
		if (player != null)
			positionDelta.set(player.getPosition()).sub(body.getPosition());
		
		switch(state) {
		case PERCHED:
			if (positionDelta.len() < 4.0f) 
				setState(State.FLY, player);
			break;
		case FLY:
			animation = body.getLinearVelocity().x < 0 ? Assets.instance.sprites.bird.fly_left : Assets.instance.sprites.bird.fly_right;
			if (abs(positionDelta.y) < bodyRadius * 0.5f && abs(positionDelta.x) < 2.0f && timeout.hasElapsed()) {
				setState(State.DASH, player);
			}
			else {			
				body.setLinearVelocity(positionDelta.nor().scl(velocity));
			}
			
			break;
		case DASH:
			animation = body.getLinearVelocity().x < 0 ? Assets.instance.sprites.bird.dart_left: Assets.instance.sprites.bird.dart_right;
			if (timeout.hasElapsed()) 
				setState(State.FLY, player);
			break;
		case DYING:
			setState(State.DEAD, player);
			break;
		default:
		}
	}
	
	public void render(Batch batch) {
		if (animation == null)
			return;
		
		renderFrame(batch, body, animation.getKeyFrame(elapsedTime), 0, (1.0f - 2.0f * bodyRadius) / 2.0f, 16.0f);
	}

	@Override
	public boolean collides(Object other) {
		if (state == State.DEAD) {
			return true;
		}
		else {
			if (state != State.DEAD &&  other instanceof Player) {
				Player player = (Player)other;
				if (!player.isDead()) {
					positionDelta.set(player.getPosition()).sub(body.getPosition());
					if (abs(positionDelta.x) < bodyRadius*1.5f && positionDelta.y > 0) {
						player.applyUpPush();
						setState(State.DYING, player);
					}
					else {
						player.kill();
					}
				}
			}
			return false;
		}
	}
}
