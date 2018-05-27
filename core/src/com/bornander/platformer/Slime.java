package com.bornander.platformer;

import com.badlogic.gdx.graphics.g2d.Animation;
import static com.bornander.libgdx.Utils.renderFrame;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.bornander.libgdx.Contactable;
import com.bornander.libgdx.MapUtils;
import com.bornander.libgdx.Timeout;

import static com.bornander.libgdx.Utils.scale;
import static com.bornander.libgdx.Utils.createBodyDef;
import static com.bornander.libgdx.Utils.createCircleShape;
import static com.bornander.libgdx.Utils.createFixtureDef;

public class Slime extends GameActor implements Contactable {
	private enum State {
		MOVING_LEFT,
		MOVING_RIGHT,
		WAITING_TO_MOVE_LEFT,
		WAITING_TO_MOVE_RIGHT,
		KILLED,
		DYING
	}
	
	private final static float bodyRadius = 0.35f;
	private final float velocity;
	
	private final World world;
	private final Vector2 lastPosition = new Vector2();
	private final Body body;
	private final Fixture fixture;
	private final Rectangle bounds;
	
	private float elapsedTime;
	private Animation<TextureRegion> animation;
	
	private State state;
	private final Timeout timeout = new Timeout(1); 

	public Slime(World world, RectangleMapObject mapObject, float unitScale) {
		super(GameActorType.SLIME);
		this.world = world;
		this.bounds = scale(mapObject.getRectangle(), unitScale);
		
		CircleShape bodyShape = createCircleShape(bodyRadius); 
		BodyDef bodyDefinition = createBodyDef(BodyType.KinematicBody);
		FixtureDef bodyFixtureDefinition = createFixtureDef(bodyShape, 1.0f, 1.0f, 0.0f, false, Physics.CATEGORY_ENEMY, Physics.MASK_ENEMY);
		
		body = world.createBody(bodyDefinition);
		
		Vector2 position = MapUtils.getMapObjectPosition(mapObject, unitScale);
		position.y -= (1.0f - 2.0f * bodyRadius) / 2.0f;
		
		body.setTransform(position, 0);
		body.setUserData(this);
		body.setFixedRotation(true);
		fixture = body.createFixture(bodyFixtureDefinition);
		fixture.setUserData(this);
		
		bodyShape.dispose();		
		
		velocity = MapUtils.getFloat(mapObject, "velocity", 0.5f);
		state = velocity < 0.0f ? State.MOVING_LEFT : State.MOVING_RIGHT;
		animation = velocity < 0.0f ? Assets.instance.sprites.slime.walk_left : Assets.instance.sprites.slime.walk_right;
		body.setLinearVelocity(velocity, 0); 
		
	}
	
	public void update(float delta) {
		elapsedTime += delta;
		timeout.update(delta);
		if (!isExpired())
			lastPosition.set(body.getPosition());
		switch(state) {
		case MOVING_LEFT:
			if (body.getPosition().x < bounds.x + 0.5f) {
				state = State.WAITING_TO_MOVE_RIGHT;
				animation = Assets.instance.sprites.slime.wait_left;
				body.setLinearVelocity(Vector2.Zero);
				timeout.reset(2.0f);
			}
			break;
		case MOVING_RIGHT:
			if (body.getPosition().x > bounds.x + bounds.width - 0.5f) {
				state = State.WAITING_TO_MOVE_LEFT;
				animation = Assets.instance.sprites.slime.wait_right;
				body.setLinearVelocity(Vector2.Zero);
				timeout.reset(1.0f);
			}
			break;
		case WAITING_TO_MOVE_LEFT:
			if (timeout.hasElapsed()) {
				state = State.MOVING_LEFT;
				body.setLinearVelocity(-velocity, 0);
				animation = Assets.instance.sprites.slime.walk_left;
			}
			break;
		case WAITING_TO_MOVE_RIGHT:
			if (timeout.hasElapsed()) {
				state = State.MOVING_RIGHT;
				body.setLinearVelocity(velocity, 0);
				animation = Assets.instance.sprites.slime.walk_right;
			}
			break;
		case KILLED:
			{
				world.destroyBody(body);
				state = State.DYING;
				timeout.reset(0.5f);
				Assets.instance.sounds.playSlimeDead();
			}
			break;

		case DYING:
			animation = Assets.instance.sprites.slime.wait_left;
			break;
		}
	}
	
	public boolean isExpired() {
		return state == State.DYING;
	}
	
	public void render(Batch batch) {
		if (animation == null)
			return;
		
		if (isExpired())
			renderFrame(batch, body, animation.getKeyFrame(elapsedTime), 0, (1.0f - 2.0f * bodyRadius) / 2.0f - timeout.getElapsed()* bodyRadius*1.5f, 16.0f, 1.0f + timeout.getElapsed()*1.5f, 1.0f - timeout.getElapsed());
		else 
			renderFrame(batch, body, animation.getKeyFrame(elapsedTime), 0, (1.0f - 2.0f * bodyRadius) / 2.0f, 16.0f);
	}
	
	@Override
	public Vector2 getPosition() {
		return lastPosition;
	}

	@Override
	public void handleContact(Fixture sender, Object other) {
		if (other instanceof Player) {
			Player player = (Player) other;
			if (!player.isDead()) {
				Vector2 pp = player.getPosition();
				Vector2 sp = getPosition();
				
				if (pp.y - sp.y > bodyRadius) {
					player.applyUpPush();
					state = State.KILLED;
				}
				else  {
					player.kill();
				}
			}
		}
	}
}