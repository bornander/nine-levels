package com.bornander.libgdx;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;

public class Utils {
	public static Animation<TextureRegion> buildIndexedAnimation(TextureAtlas atlas, float frameDuration, boolean flipX, PlayMode playMode, String regionName) {
		Array<AtlasRegion> temp = atlas.findRegions(regionName);
		Array<AtlasRegion> regions = new Array<AtlasRegion>(temp.size);
		for(AtlasRegion region : temp) {
			AtlasRegion copy = new AtlasRegion(region);
			copy.flip(flipX, false);
			regions.add(copy);
		}
		return new Animation<TextureRegion>(frameDuration, regions, playMode);
	}
	
	public static Animation<TextureRegion> buildIndexedAnimation(TextureAtlas atlas, float frameDuration, boolean flipX, PlayMode playMode, String regionA, String regionB) {
		Array<AtlasRegion> tempA = atlas.findRegions(regionA);
		Array<AtlasRegion> tempB = atlas.findRegions(regionB);
		Array<AtlasRegion> regions = new Array<AtlasRegion>(tempA.size + tempB.size);
		for(AtlasRegion region : tempA) {
			AtlasRegion copy = new AtlasRegion(region);
			copy.flip(flipX, false);
			regions.add(copy);
		}
		
		for(AtlasRegion region : tempB) {
			AtlasRegion copy = new AtlasRegion(region);
			copy.flip(flipX, false);
			regions.add(copy);
		}
		
		
		return new Animation<TextureRegion>(frameDuration, regions, playMode);
	}	
	
	public static Animation<TextureRegion> buildAnimation(TextureAtlas atlas, float frameDuration, boolean flipX, PlayMode playMode, String...regionNames) {
		Array<TextureRegion> regions = new Array<TextureRegion>(regionNames.length);
		for(String regionName : regionNames) {
			TextureRegion region = new TextureRegion(atlas.findRegion(regionName));
			region.flip(flipX, false);
			regions.add(region);
		}
		return new Animation<TextureRegion>(frameDuration, regions, playMode);
	}
	
	public static TextureRegion copy(TextureRegion source, boolean flipX) {
		TextureRegion target = new TextureRegion(source);
		target.flip(flipX, false);
		return target;
	}
	
	public static Rectangle scale(Rectangle source, float unitScale) {
		return new Rectangle(source.x * unitScale, source.y * unitScale, source.width * unitScale, source.height * unitScale);
	}
	
	public static BodyDef createBodyDef(BodyType type) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = type;
		return bodyDef;
	}
	
	public static FixtureDef createFixtureDef(Shape shape, float friction, float density, float restitution, boolean isSensor, short category, short mask) {
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.friction = friction; 
		fixtureDef.density = density; 
		fixtureDef.restitution = restitution;
		fixtureDef.isSensor = isSensor;
		if (category != -1 || mask != -1) {
			fixtureDef.filter.categoryBits = category;
			fixtureDef.filter.maskBits = mask;
		}
		return fixtureDef;
	}
	
	public static FixtureDef createEmptySensorFixtureDef(Shape shape, short category, short mask) {
		return createFixtureDef(shape, 0.0f, 0.0f, 0.0f, true, category, mask);
	}
	
	public static CircleShape createCircleShape(float radius, float x, float y) {
		CircleShape shape = new CircleShape(); 
		shape.setRadius(radius);
		Pool<Vector2> pool = Pools.get(Vector2.class);
		Vector2 offset = pool.obtain().set(x, y);
		shape.setPosition(offset);
		pool.free(offset);
		return shape;
	}
	
	public static CircleShape createCircleShape(float radius) {
		return createCircleShape(radius, 0, 0);
	}
	
	public static PolygonShape createBoxShape(float width, float height) {
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(width / 2.0f, height / 2.0f);
		return shape;
	}
	
	public static void renderFrame(Batch batch, Body body, TextureRegion frame, float offsetX, float offsetY, float unitScale) {
		renderFrame(batch, body.getWorldCenter(), body.getAngle(), frame, offsetX, offsetY, unitScale);
	}
	
	public static void renderFrame(Batch batch, Body body, TextureRegion frame, float offsetX, float offsetY, float unitScale, float width, float height) {
		renderFrame(batch, body.getWorldCenter(), body.getAngle(), frame, offsetX, offsetY, unitScale, width, height);
	}
	
	public static void renderFrame(Batch batch, Vector2 position, float angle, TextureRegion frame, float offsetX, float offsetY, float unitScale) {
		renderFrame(batch, position, angle, frame, offsetX, offsetY, unitScale, 1.0f, 1.0f);
	}	
	
	public static void renderFrame(Batch batch, Vector2 position, float angle, TextureRegion frame, float offsetX, float offsetY, float unitScale, float width, float height) {
		float w = frame.getRegionWidth() / unitScale;
		float h = frame.getRegionHeight() / unitScale;
		float hw = w / 2.0f;
		float hh = h / 2.0f;
		float x = position.x - hw + offsetX;
		float y = position.y - hh + offsetY;
		batch.draw(frame, x, y, hw, hh, w, h, width, height, angle * com.badlogic.gdx.math.MathUtils.radDeg);	
	}	
}

