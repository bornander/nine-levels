package com.bornander.libgdx;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Polyline;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;

public class MapUtils {
	
	private MapUtils() {
	}
	
	public static float getX(MapObject mapObject) {
		return mapObject.getProperties().get("x", Float.class);
	}

	public static float getY(MapObject mapObject) {
		return mapObject.getProperties().get("y", Float.class);
	}

	public static float getWidth(MapObject mapObject) {
		return mapObject.getProperties().get("width", Float.class);
	}

	public static float getHeight(MapObject mapObject) {
		return mapObject.getProperties().get("height", Float.class);
	}
	
	public static String getString(MapProperties properties, String key, String defaultValue) {
		return properties.get(key, defaultValue, String.class);
	}
	
	public static float getFloat(MapProperties properties, String key, float defaultValue) {
		String value = getString(properties, key, null);
		return value == null ? defaultValue : Float.parseFloat(value);
	}
	
	public static int getInt(MapProperties properties, String key, int defaultValue) {
		String value = getString(properties, key, null);
		return value == null ? defaultValue : Integer.parseInt(value);
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends Enum<?>> T getEnum(MapProperties properties, String key, T defaultValue) {
		String value = getString(properties, key, null);
		return value == null ? defaultValue : (T)Enum.valueOf(defaultValue.getClass(), value);
	}
	
	public static String getString(MapObject mapObject, String key, String defaultValue) {
		return getString(mapObject.getProperties(), key, defaultValue);
	}
	
	public static float getFloat(MapObject mapObject, String key, float defaultValue) {
		return getFloat(mapObject.getProperties(), key, defaultValue);
	}
	
	public static int getInt(MapObject mapObject, String key, int defaultValue) {
		return getInt(mapObject.getProperties(), key, defaultValue);
	}
	
	public static <T extends Enum<?>> T getEnum(MapObject mapObject, String key, T defaultValue) {
		return getEnum(mapObject.getProperties(), key, defaultValue);
	}
	
	public static Shape getShape(MapObject mapObject, float unitScale) {
		if (mapObject instanceof PolygonMapObject) {
			Polygon polygon = ((PolygonMapObject)mapObject).getPolygon();
			
			float[] vertices = polygon.getVertices();
			for(int i = 0; i < vertices.length; ++i) {
				vertices[i] *= unitScale;
			}
			
			PolygonShape shape = new PolygonShape();
			shape.set(vertices);
			return shape;
		}
		
		if (mapObject instanceof PolylineMapObject) {
			Polyline polyline = ((PolylineMapObject)mapObject).getPolyline();
			float[] transformed = polyline.getVertices();
			float[] vertices = new float[transformed.length];
			
			for(int i = 0; i < transformed.length - 1; i += 2) {
				vertices[i + 0] = transformed[i + 0] * unitScale;
				vertices[i + 1] = transformed[i + 1] * unitScale;
				
			}
			ChainShape shape = new ChainShape();
			shape.createChain(vertices);
			return shape;
		}
		
		if (mapObject instanceof RectangleMapObject) {
			Rectangle rectangle = ((RectangleMapObject)mapObject).getRectangle();
			PolygonShape shape = new PolygonShape();
			Vector2 halfSize = new Vector2(rectangle.width, rectangle.height).scl(0.5f).scl(unitScale);
			shape.setAsBox(halfSize.x, halfSize.y, Vector2.Zero, 0.0f);
			return shape;
		}
	
		assert(false);
		return null;
	}
	
	public static Vector2 getMapObjectPosition(MapObject mapObject, float unitScale) {
		if (mapObject instanceof PolygonMapObject) {
			Polygon polygon = ((PolygonMapObject)mapObject).getPolygon();
			return new Vector2(polygon.getX(), polygon.getY()).scl(unitScale);
		}

		if (mapObject instanceof PolylineMapObject) {
			Polyline polyline = ((PolylineMapObject)mapObject).getPolyline();
			return new Vector2(polyline.getX(), polyline.getY()).scl(unitScale);
		}
		
		if (mapObject instanceof RectangleMapObject) {
			Rectangle rectangle = ((RectangleMapObject)mapObject).getRectangle();
			return rectangle.getCenter(new Vector2()).scl(unitScale);
		}
		
		return null;
	}	
	
	public static Vector2 getCenter(MapObject mapObject, float unitScale) {
		return getCenter(mapObject, unitScale, 0, 0);
	}
	
	public static Vector2 getCenter(MapObject mapObject, float unitScale, float xOffset, float yOffset) {
		float x = getX(mapObject) * unitScale;
		float y = getY(mapObject) * unitScale;
		float w = getWidth(mapObject) * unitScale;
		float h = getHeight(mapObject) * unitScale;
		
		return new Vector2(x + w / 2.0f + xOffset, y + h / 2.0f + yOffset);
	}
	
}