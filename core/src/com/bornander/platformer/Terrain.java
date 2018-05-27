package com.bornander.platformer;

import static com.bornander.libgdx.MapUtils.getEnum;
import static com.bornander.libgdx.MapUtils.getFloat;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.bornander.libgdx.Collidable;
import com.bornander.libgdx.InvalidCaseException;

public class Terrain implements Collidable {
	
	private enum TerrainType {
		NONE,
		SOLID,
		ONE_WAY, 
	}

	private final Body body;
	private final Fixture fixture;
	private final TerrainType type;
	private final Material material;
	
	
	private static Shape buildShape(float x, float y, float width, TerrainType type) {
		switch (type) {
		case SOLID:
			float m = y * 0.005f;
			PolygonShape box = new PolygonShape();
			box.setAsBox((width / 2.0f)-m, 0.5f);
			return box;
		case ONE_WAY:
			EdgeShape line = new EdgeShape();
			line.set(0, 0, width, 0);
			return line;
		default:
			throw new InvalidCaseException(type);
		}
	}
	
	private static Vector2 getPosition(float x, float y, float width, TerrainType type) {
		switch (type) {
		case SOLID:
			return new Vector2(x + (float)width / 2.0f, y + 0.5f);
		case ONE_WAY:
			return new Vector2(x, y + 1.0f); 
		default:
			throw new InvalidCaseException(type);
		}
	}
	
	private Terrain(World world, float x, float y, float width, TiledMapTile tile) {
		MapProperties properties = tile.getProperties();
		type = getEnum(properties, "type", TerrainType.NONE);
		material = getEnum(properties, "material", Material.GRASS);
		
		Shape shape = buildShape(x, y, width, type);
		
		// TODO: Use helper functions here
		BodyDef bodyDefinition = new BodyDef();
		bodyDefinition.type = BodyType.StaticBody;
		
		FixtureDef fixtureDefinition = new FixtureDef();
		fixtureDefinition.shape = shape;
		
		fixtureDefinition.friction = getFloat(properties, "friction", material.getFriction());
		fixtureDefinition.density = getFloat(properties, "density", material.getDensity());
		fixtureDefinition.restitution = getFloat(properties, "restitution", material.getRestitution());
		fixtureDefinition.filter.categoryBits = type == TerrainType.SOLID ? Physics.CATEGORY_TERRAIN : Physics.CATEGORY_TERRAIN_ONEWAY;
		fixtureDefinition.filter.maskBits = Physics.MASK_TERRAIN;
		
		body = world.createBody(bodyDefinition);
		body.setTransform(getPosition(x, y, width, type), 0);
		body.setUserData(this);
		fixture = body.createFixture(fixtureDefinition);
		
		fixture.setUserData(this);
		shape.dispose();
	}
	
	
	@Override
	public String toString() {
		return String.format("Terrain(%s/%s @ %s)", type, material, body.getPosition());
	}

	public boolean isOneWay() {
		return type == TerrainType.ONE_WAY; 
	}
	
	public float getVerticalPosition() {
		assert(type == TerrainType.ONE_WAY);
		return body.getPosition().y;
	}

	@Override
	public boolean collides(Object other) {
		return false;
	}
	
	public static Array<Terrain> buildTerrain(World world, TiledMapTileLayer layer) {
		Array<Terrain> terrains = new Array<Terrain>();
		Material currentMaterial = Material.NONE;
		for(int y = 0; y < layer.getHeight(); ++y) {
			int sx = 0;
			TiledMapTile spanTile = null;
			for(int x = 0; x < layer.getWidth(); ++x) {
				TiledMapTile tile = null;
				Cell cell = layer.getCell(x, y);
				if (cell != null) 
					tile = cell.getTile();
				
				if (spanTile == null) { // No current span
					if (tile != null) { // Start span
						sx = x;
						spanTile = tile;
						currentMaterial = getEnum(spanTile.getProperties(), "material", Material.NONE);
					}
				}
				else {
					Material material = tile != null ? getEnum(tile.getProperties(), "material", Material.NONE) : Material.NONE;
					TerrainType spanType = getEnum(spanTile.getProperties(), "type", TerrainType.NONE); 
					if (tile == null || spanType != getEnum(tile.getProperties(), "type", TerrainType.NONE) || material != currentMaterial) { // End of span
						int w = x - sx;
						if (spanType != TerrainType.NONE) {
							if (spanType == TerrainType.ONE_WAY) 
								terrains.add(new Terrain(world, sx - 0.01f, y, w + 0.02f, spanTile));
							else 
								terrains.add(new Terrain(world, sx, y, w, spanTile));
						}
						spanTile = tile;
						currentMaterial = material;
						sx = x;
					}
				}
				
			}
		}
		return terrains;
	}
}