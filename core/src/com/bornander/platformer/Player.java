package com.bornander.platformer;

import static com.bornander.libgdx.Utils.createBodyDef;
import static com.bornander.libgdx.Utils.createCircleShape;
import static com.bornander.libgdx.Utils.createEmptySensorFixtureDef;
import static com.bornander.libgdx.Utils.createFixtureDef;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.bornander.libgdx.Collidable;
import com.bornander.libgdx.Contactable;
import com.bornander.libgdx.MapUtils;
import com.bornander.libgdx.Timeout;

public class Player extends GameActor implements Contactable, Collidable {
	public static final float bodyRadius = 0.40f;
	private static final float armRadius = bodyRadius * 0.5f;
	private static final float armOffsetX = armRadius * 1.05f;
	private static final float feetRadius = bodyRadius * 0.9f;
	private static final float headRadius = bodyRadius * 0.9f;
	private static final float feetOffsetY = 2 * (bodyRadius - feetRadius);
	private static final float headOffsetY = (bodyRadius - headRadius * 0.85f);
	
	private final Body body;
	private final Fixture bodyFixture;
	private final Fixture feetFixture;
	private final Fixture headFixture;
	private final Fixture leftArmFixture;
	private final Fixture rightArmFixture;
	/*private final float unitScale;*/
	private final Controller controller;
	
	private boolean leftArmInContact = false;
	private boolean rightArmInContact = false;
	
	private float elapsedTime;
	private Animation<TextureRegion> currentAnimation;
	private boolean movingRight = true;
	private boolean inJump = false;
	private float jumpDuration = 0;
	private Vector2 lastPosition = new Vector2();
	
	private boolean fallThroughOneWayPlatforms = false;
	private boolean dead = false;
	private boolean hasWon = false;
	
	private final Timeout fadeInTimeout = new Timeout(0.25f);

	public Player(World world, RectangleMapObject mapObject, float unitScale, Controller controller) {
		super(GameActorType.PLAYER);
		/*this.unitScale = unitScale;*/
		this.controller = controller;
		
		CircleShape bodyShape = new CircleShape();
		bodyShape.setRadius(bodyRadius);

		CircleShape feetShape = createCircleShape(feetRadius, 0, -feetOffsetY); 
		CircleShape headShape = createCircleShape(headRadius, 0,  headOffsetY);
		CircleShape leftArmShape = createCircleShape(armRadius, -armOffsetX, 0.0f); 
		CircleShape rightArmShape = createCircleShape(armRadius, armOffsetX, 0.0f); 
		
		BodyDef bodyDefinition = createBodyDef(BodyType.DynamicBody);
		FixtureDef bodyFixtureDefinition = createFixtureDef(bodyShape, 1.0f, 0.0f, 0.0f, false, Physics.CATEGORY_PLAYER, Physics.MASK_PLAYER);

		FixtureDef feetFixtureDefinition = createEmptySensorFixtureDef(feetShape, Physics.CATEGORY_PLAYER, Physics.MASK_PLAYER);
		FixtureDef headFixtureDefinition = createEmptySensorFixtureDef(headShape, Physics.CATEGORY_PLAYER, Physics.MASK_PLAYER_SENSOR);
		FixtureDef leftArmFixtureDefinition = createFixtureDef(leftArmShape, 0.0f, 0.0f, 0.0f, false, Physics.CATEGORY_PLAYER, Physics.MASK_PLAYER);
		FixtureDef rightArmFixtureDefinition = createFixtureDef(rightArmShape, 0.0f, 0.0f, 0.0f, false, Physics.CATEGORY_PLAYER, Physics.MASK_PLAYER);
		
		body = world.createBody(bodyDefinition);
		body.setTransform(MapUtils.getMapObjectPosition(mapObject, unitScale), 0);
		body.setUserData(this);
		body.setLinearDamping(0.5f);
		body.setSleepingAllowed(false);
		
		body.setFixedRotation(true);
		bodyFixture = body.createFixture(bodyFixtureDefinition);
		bodyFixture.setUserData(this);
		
		feetFixture = body.createFixture(feetFixtureDefinition);
		feetFixture.setUserData(new Contactable() {
			@Override
			public void handleContact(Fixture sender, Object other) {
				if (body.getLinearVelocity().y <= 0.01f) {
					jumpDuration = 0.0f;
					inJump = false;
				}
			}
			
			@Override
			public String toString() {
				return "FeetFixture";
			}
		});
		
		headFixture = body.createFixture(headFixtureDefinition);
		headFixture.setUserData(new Contactable() {
			@Override
			public void handleContact(Fixture sender, Object other) {
				//if (body.getLinearVelocity().y <= 0.01f) {
				jumpDuration = Float.MAX_VALUE;
				//	inJump = false;
				//}
			}
			
			@Override
			public String toString() {
				return "HeadFixture";
			}
		});		
		
		leftArmFixture = body.createFixture(leftArmFixtureDefinition);
		leftArmFixture.setUserData(this);
		rightArmFixture = body.createFixture(rightArmFixtureDefinition);
		rightArmFixture.setUserData(this);
	
		
		bodyShape.dispose();		
		feetShape.dispose();
		headShape.dispose();
		leftArmShape.dispose();
		rightArmShape.dispose();
	}
	
	@Override
	public void update(float delta, Array<GameActor> actors) {
		fallThroughOneWayPlatforms = false;
		elapsedTime += delta;
		fadeInTimeout.update(delta);
		
		final float maxHorizontalSpeed = 3.5f;
		final float horizontalImpulse = 100.0f;
		final float verticalImpulse = 85f;
		final float maxJumpDuration = 0.35f;
		
		if (hasWon) {
			lastPosition.y += 1.0f * delta;
		}
		else {			
			float horizontalFactor = inJump ? 0.4f : 1.0f;
			float horizontalSpeed = body.getLinearVelocity().x;
			
			if (!dead && controller.isRightPressed()) {
				if (isMovingHorizontally(0.1f))
					movingRight = true;
				if (horizontalSpeed < maxHorizontalSpeed)
					body.applyLinearImpulse(horizontalImpulse * delta * horizontalFactor, 0, 0, 0, true);
			}
	
			if (!dead && controller.isLeftPressed()) {
				if (isMovingHorizontally(0.1f))
					movingRight = false;
				if (horizontalSpeed > -maxHorizontalSpeed)
					body.applyLinearImpulse(-horizontalImpulse * delta * horizontalFactor, 0, 0, 0, true);
			}
	
			if (inJump)
				jumpDuration += delta;
			
			if (!inJump) {
				if (!dead &&  controller.isDropPressed()) {
					body.setAwake(true);
					fallThroughOneWayPlatforms = true;
				}
			}
			//Log.info("inJump=%b duration=%.2f maxDuration=%.2f isMovingDown=%b  dy=%.4f ", inJump, jumpDuration, maxJumpDuration, isMovingDown(), body.getLinearVelocity().y);
			if (!dead && controller.isJumpPressed() && jumpDuration < maxJumpDuration && !isMovingDown()) {
				if (!inJump)
					Assets.instance.sounds.playJump();
				inJump = true;
				float fraction = 1.0f - Math.min(1.0f, jumpDuration / maxJumpDuration);
				body.applyLinearImpulse(0, verticalImpulse * delta * fraction, 0, 0, true);
			}
			
			if (!dead) { 
				if (isMovingDown() && isMovingVertically()) {
					currentAnimation = movingRight ? Assets.instance.sprites.player.fall_right : Assets.instance.sprites.player.fall_left;
				}
				else {
					if (isMovingUp() && isMovingVertically()) {
						currentAnimation = movingRight ? Assets.instance.sprites.player.jump_right : Assets.instance.sprites.player.jump_left;
					}
					else {
							if (isMovingHorizontally() && (controller.isLeftPressed() || controller.isRightPressed()))
							currentAnimation = movingRight ? Assets.instance.sprites.player.walk_right : Assets.instance.sprites.player.walk_left;
						else
							currentAnimation = movingRight ? Assets.instance.sprites.player.stand_right : Assets.instance.sprites.player.stand_left;
					}
				}
				if (getPosition().y < 0)
					kill();
			}
			
			if (leftArmInContact && !controller.isLeftPressed())
				leftArmInContact = false;
	
			if (rightArmInContact && !controller.isRightPressed())
				rightArmInContact = false;

			lastPosition.set(body.getWorldCenter());
		}
	}
	
	@Override
	public void render(Batch batch) {
		TextureRegion frame = currentAnimation.getKeyFrame(elapsedTime);
		Vector2 position = lastPosition;
		float angle = body.getAngle();
		float w = 1.0f; //frame.getRegionWidth() * unitScale;
		float h = 1.0f; //frame.getRegionHeight() * unitScale;
		float hw = w / 2.0f;
		float hh = h / 2.0f;
		float x = position.x - hw + 0.0f;
		float y = position.y - hh + 0.10f;
		
		if (!isMovingVertically() && !inJump && (y-(int)y)<0.5f && !hasWon)
			y = (int)y;
		
		batch.setColor(1, 1, 1, fadeInTimeout.getElapsed());
		batch.draw(frame, x, y, hw, hh, w, h, 1, 1, angle * com.badlogic.gdx.math.MathUtils.radDeg);
		batch.setColor(1, 1, 1, 1);
		
	}

	@Override
	public Vector2 getPosition() {
		return lastPosition;
	}

	// TODO: Change these isMoving methods to accept a epsilon value
	public boolean isMovingUp() {
		return body.getLinearVelocity().y > 0;
	}
	
	public boolean isMovingDown() {
		float v = body.getLinearVelocity().y;
		return v < -0.1f;
	}
	
	public boolean isMovingVertically() {
		return Math.abs(body.getLinearVelocity().y) > 0.2f;
	}
	
	public boolean isMovingHorizontally(float threshold) {
		return Math.abs(body.getLinearVelocity().x) > threshold;
	}
	
	public boolean isMovingHorizontally() {
		return isMovingHorizontally(1.5f);
	}
	
	public boolean isMovingLeft() {
		return body.getLinearVelocity().x < 0;
	}

	public boolean isMovingRight() {
		return body.getLinearVelocity().x > 0;
	}
	
	public boolean isFallThroughOneWayPlatformsEnabled() {
		return fallThroughOneWayPlatforms;
	}
	
	public boolean isDead() {
		return dead;
	}
	
	public void applyPush(float x, float y) {
		Vector2 worldCenter = body.getWorldCenter();
		body.applyLinearImpulse(x, y, worldCenter.x, worldCenter.y, true);
	}
	
	public void applyPush(Vector2 impulse) {
		applyPush(impulse.x, impulse.y);
	}
	
	public void applyUpPush() {
		if (!isMovingUp()) {
			Vector2 center = body.getWorldCenter();
			float verticalForce = Math.max(2, 1.5f * body.getMass() * -body.getLinearVelocity().y);
			body.applyLinearImpulse(0, verticalForce, center.x, center.y, true);
		}
	}

	public void kill() {
		if (dead || hasWon)
			return;
		
		dead = true;
		applyUpPush();

		body.setFixedRotation(false);
		float torque = isMovingHorizontally() ? Math.signum(body.getLinearVelocity().x) : 1.0f;
		body.setAngularVelocity(2 * torque);
		currentAnimation = movingRight ? Assets.instance.sprites.player.dead_right : Assets.instance.sprites.player.dead_left;
		Assets.instance.sounds.playPlayerDead();
	}

	@Override
	public void handleContact(Fixture sender, Object other) {
		if (other instanceof Terrain) {
			if (leftArmFixture == sender)
				leftArmInContact = true;
			if (rightArmFixture == sender)
				rightArmInContact = true;
		}
	}

	@Override
	public boolean collides(Object other) {
		if (isDead())
			return true;

		if (other instanceof Terrain) {
			Terrain terrain = (Terrain)other;
			
			if (terrain.isOneWay()) {
				if (isFallThroughOneWayPlatformsEnabled())
					return true;
				
				if (isMovingUp())
					return true;
				else {
					float py = body.getPosition().y;
					float ty = terrain.getVerticalPosition();
					boolean playerCenterIsBelowTerrain = py < ty;
					
					if (playerCenterIsBelowTerrain)
						return true;
					else {
						boolean playerCenterIsMostlyAboveTerrain = py - ty < Player.bodyRadius * 0.975f;
						if (playerCenterIsMostlyAboveTerrain)
							return true;
					}
				}
			}
		}
		if (isMovingDown()) {
			inJump=false;
			jumpDuration = 0.0f;			
		}
		return false;
	}

	public void markAsWon() {
		hasWon = true;
		currentAnimation = movingRight ? Assets.instance.sprites.player.jump_right : Assets.instance.sprites.player.jump_left;
	}
} 