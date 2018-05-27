package com.bornander.platformer;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import static com.bornander.libgdx.Utils.renderFrame;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.bornander.libgdx.Contactable;
import com.bornander.libgdx.InvalidCaseException;
import com.bornander.libgdx.Timeout;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static com.bornander.libgdx.Utils.createBodyDef;
import static com.bornander.libgdx.Utils.createFixtureDef;
import static com.bornander.libgdx.Utils.createEmptySensorFixtureDef;

public class Pumpkin extends GameActor {
	
	private enum State {
		WALK_LEFT,
		WALK_RIGHT,
		FALL_LEFT,
		FALL_RIGHT,
		WAIT,
		EXPLODE,
		EXPIRED;
		
		public boolean isAnyLeft() {
			return this == WALK_LEFT || this == FALL_LEFT;
		}
		
		public boolean isAnyWalk() {
			return this == WALK_LEFT || this == WALK_RIGHT;
		}
	}
	
	private static final Vector2 tempVector = new Vector2();
	private final float bodyRadius = 0.45f;
	
	private final World world;
	private final OrthographicCamera camera;
	private final Body body;
	private final Fixture fixture;
	private final Fixture playerDetectorFixture;
	private final Fixture leftFixture;
	private final Fixture rightFixture;
	
	private final Vector2 position = new Vector2();
	
	private float elapsedTime;
	private Animation<TextureRegion> animation;
	private State state = State.FALL_RIGHT;
	private Timeout timeout;
	private Player player;

	public Pumpkin(World world, OrthographicCamera camera, Vector2 spawnPosition, float lifeSpan, float directionModifier) {
		super(GameActorType.PUMPKIN);
		this.world = world;
		this.camera = camera;
		
		timeout = new Timeout(lifeSpan);
		
		// TODO: Change directionModifier to be boolean instead of float.
		state = directionModifier == 1.0f ? State.FALL_LEFT : State.FALL_RIGHT;
		
		CircleShape bodyShape = new CircleShape(); 
		bodyShape.setRadius(bodyRadius);

		CircleShape playerDetectorShape = new CircleShape(); 
		playerDetectorShape.setRadius(bodyRadius * 1.01f);
		
		CircleShape leftShape = new CircleShape(); 
		leftShape.setRadius(bodyRadius * 0.1f);
		leftShape.setPosition(tempVector.set(-bodyRadius, 0));

		CircleShape rightShape = new CircleShape(); 
		rightShape.setRadius(bodyRadius * 0.1f);
		rightShape.setPosition(tempVector.set(bodyRadius, 0));
		
		BodyDef bodyDefinition = createBodyDef(BodyType.DynamicBody);
		
		FixtureDef bodyFixtureDefinition = createFixtureDef(bodyShape, 1.0f, 1.0f, 0.0f, false, Physics.CATEGORY_ENEMY, Physics.MASK_ENEMY);
		FixtureDef playerDetectorFixtureDefinition = createEmptySensorFixtureDef(playerDetectorShape, Physics.CATEGORY_ENEMY, Physics.MASK_ENEMY);
		FixtureDef leftFixtureDefinition = createEmptySensorFixtureDef(leftShape, Physics.CATEGORY_ENEMY, Physics.MASK_ENEMY);
		FixtureDef rightFixtureDefinition = createEmptySensorFixtureDef(rightShape, Physics.CATEGORY_ENEMY, Physics.MASK_ENEMY);
		
		body = world.createBody(bodyDefinition);
		body.setTransform(spawnPosition.x, spawnPosition.y - (1.0f - 2.0f * bodyRadius) / 2.0f, 0);
		body.setUserData(this);
		body.setFixedRotation(true);
		fixture = body.createFixture(bodyFixtureDefinition);
		fixture.setUserData(this);
		
		playerDetectorFixture = body.createFixture(playerDetectorFixtureDefinition);
		playerDetectorFixture.setUserData(new Contactable() {
			@Override
			public void handleContact(Fixture sender, Object other) {
				if (other instanceof Player) 
					((Player)other).kill();
			}
		});		
		
		leftFixture = body.createFixture(leftFixtureDefinition);
		leftFixture.setUserData(new Contactable() {
			@Override
			public void handleContact(Fixture sender, Object other) {
				if (other instanceof Player) {
					Player player = (Player)other;
					player.kill();
				}
				
				
				if (shouldChangeDirectionOnCollision(other) && state.isAnyLeft()) {
					flipDirection();
				}
			}
		});
		
		rightFixture = body.createFixture(rightFixtureDefinition);
		rightFixture.setUserData(new Contactable() {
			@Override
			public void handleContact(Fixture sender, Object other) {
				
				if (other instanceof Player) {
					Player player = (Player)other;
					player.kill();
				}
				
				if (shouldChangeDirectionOnCollision(other) && !state.isAnyLeft()) {
					flipDirection();
				}
			}
		});
		
		leftShape.dispose();
		rightShape.dispose();
		playerDetectorShape.dispose();
		bodyShape.dispose();		
		
		animation = Assets.instance.sprites.pumpkin.fall_right;		
	}
	
	private static boolean shouldChangeDirectionOnCollision(Object other) {
		return !(other instanceof Coin);
	}
	
	private void flipDirection() {
		switch(state) {
		case WALK_LEFT: setState(State.WALK_RIGHT); break;
		case WALK_RIGHT: setState(State.WALK_LEFT); break;
		case FALL_LEFT: setState(State.FALL_RIGHT); break;
		case FALL_RIGHT: setState(State.FALL_LEFT); break;
		default:
			break;
		}
	}
	
	private void setState(State newState) {
		elapsedTime = 0.0f;
		state = newState;
		switch(state) {
		case WALK_LEFT:
			animation = Assets.instance.sprites.pumpkin.walk_left;
			break;
		case WALK_RIGHT:
			animation = Assets.instance.sprites.pumpkin.walk_right;
			break;
		case FALL_LEFT:
			animation = Assets.instance.sprites.pumpkin.fall_left;
			break;
		case FALL_RIGHT:
			animation = Assets.instance.sprites.pumpkin.fall_right;
			break;
		case WAIT:
			animation = Assets.instance.sprites.pumpkin.wait;
			timeout.reset(1.0f);
			break;
		case EXPLODE:
			world.destroyBody(body);
			animation = Assets.instance.sprites.smoke.puff;
			timeout.reset(1.0f);
			break;
		case EXPIRED:
			//currentAnimation = null;
			
			break;
		}
	}
	
	public void update(float delta, Array<GameActor> actors) {
		elapsedTime += delta;
		timeout.update(delta);
		if (state == State.EXPIRED || state == State.EXPLODE)
			return;
		
		player = (Player)getIfNotAlreadySet(GameActorType.PLAYER, player, actors);
		position.set(body.getWorldCenter());
		
		final float fallThresholdSpeed = 0.5f;
		final float maxHorizontalSpeed = 1.0f;
		final float horizontalImpulse = 20.0f;
		
		
		float horizontalSpeed = body.getLinearVelocity().x;
		float verticalSpeed = abs(body.getLinearVelocity().y);
		
		if (state.isAnyWalk() && timeout.hasElapsed()) {
			setState(State.WAIT);
		}
			
		switch(state) {
		case WALK_LEFT:
			
			if (verticalSpeed > fallThresholdSpeed)
				setState(State.FALL_LEFT);
			else {
				if (horizontalSpeed > -maxHorizontalSpeed)
					body.applyLinearImpulse(-horizontalImpulse * delta, 0, 0, 0, true);
			}
			break;
		case WALK_RIGHT:
			if (verticalSpeed > fallThresholdSpeed)
				setState(State.FALL_RIGHT);
			else {
				if (horizontalSpeed < maxHorizontalSpeed)
					body.applyLinearImpulse(horizontalImpulse * delta, 0, 0, 0, true);
			}
			break;
		case FALL_LEFT:
		case FALL_RIGHT:
			if (verticalSpeed < 0.01f) 
				setState(state == State.FALL_LEFT ? State.WALK_LEFT : State.WALK_RIGHT);
			break;
		case WAIT:
			if (timeout.hasElapsed()) {
				final float explosionRadius = 4.0f;
				
				tempVector.set(player.getPosition()).sub(body.getPosition().x, body.getPosition().y - 2.0f * bodyRadius);
				float distance = tempVector.len();
				if (distance < explosionRadius) {
					tempVector.nor().scl(max(0.0f, min(explosionRadius, explosionRadius - distance)));

					player.applyPush(tempVector.scl(2f));
				}
				setState(State.EXPLODE);
				if (Math.abs(body.getWorldCenter().x - camera.position.x) < camera.viewportWidth / 2.0f + 1.0f)
					Assets.instance.sounds.playPumpkinExplode();
			}
			break;
		case EXPLODE:
			if (timeout.hasElapsed()) 
				setState(State.EXPIRED);
			break;
		default:
			throw new InvalidCaseException(state);
		}
	}
	
	public void render(Batch batch) {
		if (animation == null)
			return;
		
		// Can't use body.getPosition() here as the Body is destroyed before the Pumpkin object is destroyed
		renderFrame(batch, position, body.getAngle(), animation.getKeyFrame(elapsedTime), 0, 0.05f, 16.0f);
	}
}
