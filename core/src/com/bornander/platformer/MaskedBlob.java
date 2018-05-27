package com.bornander.platformer;

import static com.bornander.libgdx.Utils.createBodyDef;
import static com.bornander.libgdx.Utils.createFixtureDef;
import static com.bornander.libgdx.Utils.renderFrame;
import static com.bornander.libgdx.Utils.scale;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.bornander.libgdx.Collidable;
import com.bornander.libgdx.Contactable;
import com.bornander.libgdx.MapUtils;
import com.bornander.libgdx.Timeout;

public class MaskedBlob extends GameActor implements Contactable {
	
	private enum State {
		MOVING_LEFT,
		MOVING_RIGHT,
		WAITING_TO_MOVE_LEFT,
		WAITING_TO_MOVE_RIGHT,
		DYING,
		DEAD
	}
	
	private final static float bottomWidth = 0.35f; 
	private final static float bottomHeight = 0.45f;
	
	private final static float topWidth = 0.25f;
	private final static float topHeight = 0.20f;
	private final static float velocity = 0.5f;
	
	private final Body body;
	private final Fixture bottomFixture;
	private final Fixture topFixture;
	private final Rectangle bounds;
	
	private final Vector2 maskPosition = new Vector2();
	private final Vector2 maskVelocity = new Vector2();
	private float maskRotation = 0.0f;
	private Animation<TextureRegion> maskAnimation = null;
	
	private float elapsedTime;
	private Animation<TextureRegion> animation;
	
	private boolean isMasked = true;
	private boolean unmaskPending = false;
	
	private State state = State.MOVING_LEFT;
	
	private final Timeout timeout = new Timeout(1); 

	public MaskedBlob(World world, RectangleMapObject mapObject, float unitScale) {
		super(GameActorType.MASKED_BLOB);
		this.bounds = scale(mapObject.getRectangle(), unitScale);
		
		
		PolygonShape bottomShape = new PolygonShape();
		bottomShape.setAsBox(bottomWidth, bottomHeight);
		PolygonShape topShape = new PolygonShape();
		topShape.setAsBox(topWidth, topHeight, new Vector2(0.0f, bottomHeight + topHeight), 0.0f);
		
		
		BodyDef bodyDefinition = createBodyDef(BodyType.KinematicBody);
		FixtureDef bottomFixtureDefinition = createFixtureDef(bottomShape, 1.0f, 1.0f, 0.0f, false, Physics.CATEGORY_ENEMY, Physics.MASK_ENEMY);
		FixtureDef topFixtureDefinition = createFixtureDef(topShape, 1.0f, 1.0f, 0.0f, false, Physics.CATEGORY_ENEMY, Physics.MASK_ENEMY);
		
		body = world.createBody(bodyDefinition);
		
		Vector2 position = MapUtils.getMapObjectPosition(mapObject, unitScale);
		position.y -= (1.0f - 2.0f * bottomHeight) / 2.0f;
		
		body.setTransform(position, 0);
		body.setUserData(this);
		body.setFixedRotation(true);
		bottomFixture = body.createFixture(bottomFixtureDefinition);
		bottomFixture.setUserData(new Collidable() {
			
			@Override
			public boolean collides(Object other) {
				if (isDead())
					return true;
				if (other instanceof Player) {
					Player player = (Player)other;
					if (!player.isDead()) {
						float dy = player.getPosition().y - body.getWorldCenter().y;
						if (!isMasked && dy > 0.55f) {
							player.applyPush(0f, 4.0f);
							state = State.DYING;
							Assets.instance.sounds.playMaskedBlobDamage();
						}
						else {
							player.kill();
						}
					}
				}
				return false;
			}
		});
		
		
		topFixture = body.createFixture(topFixtureDefinition);
		topFixture.setUserData(new Collidable() {
			@Override
			public boolean collides(Object other) {
				if (isDead())
					return true;
				if (other instanceof Player) {
					Player player = (Player)other;
					if (!player.isDead()) {
						float dy = player.getPosition().y - body.getWorldCenter().y;
						if (dy > 1.00f) {
							player.applyPush(0, 4.0f);
							unmaskPending = true;
							maskPosition.set(body.getWorldCenter());
							maskVelocity.set(isInLeftState() ? 1.0f : -1.0f, 4.0f);
							
							maskAnimation = isInLeftState() ? Assets.instance.sprites.maskedBlob.mask_left : Assets.instance.sprites.maskedBlob.mask_left;
							Assets.instance.sounds.playMaskedBlobDamage();
						}
						else {
							player.kill();
						}
					}
				}
				return false;
			}
		});
		
		
		bottomShape.dispose();
		topShape.dispose();		
		
		body.setLinearVelocity(-velocity, 0); // TODO: Make this configurable from the MapObejct via TileD
		animation = Assets.instance.sprites.maskedBlob.walk_left;
	}
	
	private boolean isInLeftState() {
		return state == State.MOVING_LEFT || state == State.WAITING_TO_MOVE_RIGHT;
	}
	
	private boolean isDead() {
		return state == State.DEAD;
	}
	
	private void setAnimation() {
		switch(state) {
		case MOVING_LEFT: animation = isMasked ? Assets.instance.sprites.maskedBlob.walk_left : Assets.instance.sprites.blob.walk_left; break;
		case MOVING_RIGHT: animation = isMasked ? Assets.instance.sprites.maskedBlob.walk_right : Assets.instance.sprites.blob.walk_right; break;
		case WAITING_TO_MOVE_LEFT: animation = isMasked ? Assets.instance.sprites.maskedBlob.stand_left : Assets.instance.sprites.blob.stand_left; break;
		case WAITING_TO_MOVE_RIGHT: animation = isMasked ? Assets.instance.sprites.maskedBlob.stand_right : Assets.instance.sprites.blob.stand_right; break;
		case DEAD: animation = Assets.instance.sprites.blob.dead; break;
		default:
		}
	}
	
	@Override
	protected void update(float delta) {
		elapsedTime += delta;
		float x = body.getPosition().x;
		
		timeout.update(delta);
		
		if (!isMasked) {
			maskVelocity.add(0.0f, -10f * delta);
			maskPosition.add(maskVelocity.x * delta, maskVelocity.y * delta);
			maskRotation += delta;
		}
		
		if (unmaskPending) {
			body.destroyFixture(topFixture);
			unmaskPending = false;
			isMasked = false;
		}
		
		switch(state) {
		case MOVING_LEFT:
			if (x < bounds.x + 0.5f) {
				state = State.WAITING_TO_MOVE_RIGHT;
				body.setLinearVelocity(Vector2.Zero);
				timeout.reset(2.0f);
			}
			break;
		case MOVING_RIGHT:
			if (x > bounds.x + bounds.width - 0.5f) {
				state = State.WAITING_TO_MOVE_LEFT;
				body.setLinearVelocity(Vector2.Zero);
				timeout.reset(1.0f);
			}
			break;
		case WAITING_TO_MOVE_LEFT:
			if (timeout.hasElapsed()) {
				state = State.MOVING_LEFT;
				body.setLinearVelocity(-velocity, 0);
			}
			break;
		case WAITING_TO_MOVE_RIGHT:
			if (timeout.hasElapsed()) {
				state = State.MOVING_RIGHT;
				body.setLinearVelocity(velocity, 0);
			}
			break;			
		case DYING:
			body.setType(BodyType.DynamicBody);
			body.setAngularVelocity(2.0f);
			body.applyLinearImpulse(0, 2, 0, 0, true);
			state = State.DEAD;
			break;
		default:
		}
		
		setAnimation();
	}
	
	@Override
	public void render(Batch batch) {
		if (animation == null)
			return;
		
		renderFrame(batch, body, animation.getKeyFrame(elapsedTime), 0, 0.225f, 16.0f);
		if (!isMasked) {
			renderFrame(batch, maskPosition, maskRotation, maskAnimation.getKeyFrame(elapsedTime), 0, .25f, 16.0f);
		}
	}

	@Override
	public void handleContact(Fixture sender, Object other) {
		if (other instanceof Player) {
			((Player)other).kill();
		}
	}
}
