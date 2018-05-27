package com.bornander.libgdx;

import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;

public class PolygonPath {

	private static class Segment {
		private final Vector2 a;
		private final Vector2 b;
		private final float oa;
		private final float ob;
		
		private final Vector2 delta;
		
		public Segment(Vector2 a, Vector2 b, float oa, float ob)
		{
			this.a = a;
			this.b = b;
			this.oa = oa;
			this.ob = ob;
			
			delta = new Vector2(b).sub(a).nor();
		}
		
		public boolean contains(float distance) {
			return distance >= oa && distance < ob;
		}
		
		public Vector2 getPosition(float distance) {
			float d = distance - oa;
			return  (new Vector2(delta)).scl(d).add(a);
		}
	}
	
	private final Segment[] segments;
	private final float totalDistance;
	
	public PolygonPath(Polygon polygon, float unitScale) {
		float[] tv = polygon.getTransformedVertices();
		Vector2[] vertices = new Vector2[tv.length  / 2];
		segments = new Segment[vertices.length];
		
		for(int i = 0; i < tv.length; i += 2) {
			float x = tv[i + 0] * unitScale;
			float y = tv[i + 1] * unitScale;
			vertices[i / 2] = new Vector2(x, y);
		}
		
		float distance = 0;
		for(int i = 0; i < vertices.length; ++i ) {
			Vector2 a = vertices[i];
			Vector2 b = vertices[(i + 1) % vertices.length];
			
			float oa = distance;
			float ob = distance + Vector2.dst(a.x, a.y, b.x, b.y);
			segments[i] = new Segment(a, b, oa, ob);
			distance = ob;
		}
		
		totalDistance = distance;
	}
	
	public Vector2 getPosition(float distance) {
		float modDistance = distance % totalDistance;
		
		for(Segment segment : segments) {
			if (segment.contains(modDistance)) {
				return segment.getPosition(modDistance);
			}
		}
		return null;
	}
}
