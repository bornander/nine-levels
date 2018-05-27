package com.bornander.platformer;

import static com.badlogic.gdx.math.MathUtils.PI;
import static com.badlogic.gdx.math.MathUtils.sin;
import static com.bornander.libgdx.Utils.createBodyDef;
import static com.bornander.libgdx.Utils.createBoxShape;
import static com.bornander.libgdx.Utils.createFixtureDef;
import static com.bornander.libgdx.Utils.renderFrame;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.bornander.libgdx.Contactable;
import com.bornander.libgdx.InvalidCaseException;
import com.bornander.libgdx.MapUtils;
import com.bornander.libgdx.Timeout;

public class Brick extends GameActor implements Contactable{
	
	private enum State {
		READY,
		PINGING,
		BREAKING,
		DONE
	}
	
	private static final RandomXS128 RND = new RandomXS128(); 
	
	private static final int MAX_PARALELL = 3;
	private final World world;
	private final Body body;
	private final float invUnitScale;
	
	private final Body partBodyA;
	private final Body partBodyB;
	private final Body partBodyC;
	private final Body partBodyD;
	
	
	private final Level level;
	private final TextureRegion texture;
	private final TextureRegion partTexture;
	private final Animation<TextureRegion> coinAnimation;
	private int coinCount = 4;
	
	private State state = State.READY;
	private Timeout pingTimeout = new Timeout(1);
	
	private float[] coinState = new float[MAX_PARALELL];
	private int coinStateIndex = 0;

	private boolean isBroken = false;

	protected Brick(Level level, World world, RectangleMapObject mapObject, float unitScale) {
		super(GameActorType.BRICK);
		this.world = world;
		this.level = level;
		this.invUnitScale = 1.0f / unitScale;
		
		PolygonShape bodyShape = createBoxShape(1, 1);
 		BodyDef bodyDefinition = createBodyDef(BodyType.StaticBody);
		FixtureDef bodyFixtureDefinition = createFixtureDef(bodyShape, 1, 1, 0.1f, false, Physics.CATEGORY_TERRAIN, Physics.MASK_TERRAIN);
		
		body = world.createBody(bodyDefinition);
		body.setTransform(MapUtils.getCenter(mapObject, unitScale), 0);
		body.setUserData(this);
		body.setFixedRotation(true);
		Fixture fixture = body.createFixture(bodyFixtureDefinition);
		fixture.setUserData(this);
		
		bodyShape.dispose();
		
		partBodyA = createPart(world, mapObject, unitScale, -0.25f, -0.25f);
		partBodyB = createPart(world, mapObject, unitScale, -0.25f,  0.25f);
		partBodyC = createPart(world, mapObject, unitScale,  0.25f, -0.25f);
		partBodyD = createPart(world, mapObject, unitScale,  0.25f,  0.25f);
		
		texture = Assets.instance.sprites.brick;
		partTexture = Assets.instance.sprites.brick_fragment;
		coinAnimation = Assets.instance.sprites.coin.spinAndSparkle;
		
		for(int i = 0; i < coinState.length; ++i)
			coinState[i] = Float.NaN;
	}
	
	private Body createPart(World world, RectangleMapObject mapObject, float unitScale, float x, float y) {
		PolygonShape bodyShape = createBoxShape(0.45f, 0.45f);
 		BodyDef bodyDefinition = createBodyDef(BodyType.StaticBody);
		FixtureDef bodyFixtureDefinition = createFixtureDef(bodyShape, 1, 1, 0.1f, false, Physics.CATEGORY_DEBRIS, Physics.MASK_DEBRIS);
		
		Body body = world.createBody(bodyDefinition);
		body.setTransform(MapUtils.getCenter(mapObject, unitScale, x, y), 0);
		body.setUserData(this);
		/*Fixture fixture = */body.createFixture(bodyFixtureDefinition);
		
		bodyShape.dispose();
		return body;
	}
	
	
	@Override
	protected void update(float delta) {
		for(int i = 0; i < coinState.length; ++i) {
			if (!Float.isNaN(coinState[i])) {
				coinState[i] += delta;
			}
		}
		
		if (coinCount == 0 && !isBroken ) {
			Assets.instance.sounds.playBrick();
			isBroken = true;
			world.destroyBody(body);
			partBodyA.setType(BodyType.DynamicBody);
			partBodyB.setType(BodyType.DynamicBody);
			partBodyC.setType(BodyType.DynamicBody);
			partBodyD.setType(BodyType.DynamicBody);
			
			float xa = (RND.nextFloat() - 1.0f) * 1.0f; 
			float xb = (RND.nextFloat() - 1.0f) * 1.0f;
			float xc = (RND.nextFloat()       ) * 1.0f; 
			float xd = (RND.nextFloat()       ) * 1.0f;
			
			float ya = 2.0f * RND.nextFloat() + 1.0f;
			float yb = 2.0f * RND.nextFloat() + 1.0f;
			float yc = 2.0f * RND.nextFloat() + 1.0f;
			float yd = 2.0f * RND.nextFloat() + 1.0f;
			
			partBodyA.applyAngularImpulse(0.1f * RND.nextFloat(), true);
			partBodyB.applyAngularImpulse(0.1f * RND.nextFloat(), true);
			partBodyC.applyAngularImpulse(0.1f * RND.nextFloat(), true);
			partBodyD.applyAngularImpulse(0.1f * RND.nextFloat(), true);
			partBodyA.applyLinearImpulse(xa, ya, partBodyA.getPosition().x, partBodyA.getPosition().y, true);
			partBodyB.applyLinearImpulse(xb, yb, partBodyB.getPosition().x, partBodyB.getPosition().y, true);
			partBodyC.applyLinearImpulse(xc, yc, partBodyC.getPosition().x, partBodyC.getPosition().y, true);
			partBodyD.applyLinearImpulse(xd, yd, partBodyD.getPosition().x, partBodyD.getPosition().y, true);				
		}
		
		switch (state) {
		case READY:
			
			break;
		case PINGING:
			pingTimeout.update(delta);
			if (pingTimeout.didJustElapse())
				state = State.READY;
			break;
		case BREAKING:
			break;
		case DONE:
			break;
		default:
			throw new InvalidCaseException(state);
		}
	}
	
	@Override
	public void render(Batch batch) {
		for(int i = 0; i < coinState.length; ++i) {
			float state = coinState[i];
			if (!Float.isNaN(state)) {
				float s = Math.min(1.0f, state);
				TextureRegion frame = coinAnimation.getKeyFrame(s);
				float coinRadius = 0.25f;
				float w = coinRadius * 2.0f;
				float h = coinRadius * 2.0f;
				float hw = w / 2.0f;
				float hh = h / 2.0f;
				float x = body.getPosition().x - hw;
				float y = body.getPosition().y - hh;
				
				float offset = 0.25f + Interpolation.sineOut.apply(0, 1, s) * 2.0f;
				batch.draw(frame, x, y + offset, hw, hh, w, h, 1, 1, 0);
				if (s >= 1.0f) {
					coinState[i] = Float.NaN;
				}
			}
		}		
		
		if (isBroken) {
			renderFrame(batch, partBodyA, partTexture, 0, 0, invUnitScale);
			renderFrame(batch, partBodyB, partTexture, 0, 0, invUnitScale);
			renderFrame(batch, partBodyC, partTexture, 0, 0, invUnitScale);
			renderFrame(batch, partBodyD, partTexture, 0, 0, invUnitScale);
		}
		
		if (!isBroken) {
			float w = 1;
			float h = 1;
			float hw = w / 2.0f;
			float hh = h / 2.0f;
			float x = body.getPosition().x - hw;
			float y = body.getPosition().y - hh;
			float pingOffset = 0.25f * sin(pingTimeout.getElapsed() * PI); 
			
			batch.draw(texture, x, y+pingOffset, hw, hh, w, h, 1, 1, 0);
		}
	}
	
	@Override
	public void handleContact(Fixture sender, Object other) {
		if (state != State.READY)
			return;
		
		if (other instanceof Player) {
			Player player = (Player)other;
			if (Math.abs(player.getPosition().x - body.getPosition().x) < 0.75f) {
				if (player.getPosition().y < body.getPosition().y) {
					state = State.PINGING;
					pingTimeout.reset(0.25f);
					coinState[coinStateIndex] = 0;
					coinStateIndex = (coinStateIndex + 1) % coinState.length;
					coinCount--;
					level.grabCoin();
				}
			}
		}
	}	
}