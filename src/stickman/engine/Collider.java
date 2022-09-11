package stickman.engine;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import stickman.render.ShaderProgram;

public class Collider extends ShaderProgram {

	private final float sizeX, sizeY, sizeZ;
	private boolean solid;
	private Vector3f colPos = new Vector3f(0);
	private Vector3f colPos2 = new Vector3f(0);

	public Collider(float x, float y, float z, boolean solid) {
		super();
		sizeX = x;
		sizeY = y;
		sizeZ = z / 4;
		this.solid = solid;
	}

	public Collider(Vector3f size, boolean solid) {
		this(size.x, size.y, size.z, solid);
	}

	public boolean getSolid() {
		return solid;
	}

	public Vector3f getSize() {
		return new Vector3f(sizeX, sizeY, sizeZ);
	}

	public void update(Vector3f pos) {
		colPos2 = new Vector3f(pos);
		colPos = pos.add(0, -sizeY + sizeZ, 0.0022f);
	}

	public void render(Matrix4f projectionMatrix, Transformation transformation, float width, float height) {
	
	}

	private Vector2f getPos() {
		return new Vector2f(colPos.x, colPos.y);
	}

	public boolean checkColliders(Collider collider) {
		if (collider != this) {
			Vector2f thisCol = getPos();
			Vector2f otherCol = collider.getPos();
			if (Math.abs(thisCol.x - otherCol.x) < collider.sizeX + sizeX)
				if (Math.abs(thisCol.y - otherCol.y) < collider.sizeZ + sizeZ)
					return true;
		}
		return false;
	}

	public boolean checkHit(Vector3f pos) {

		Vector2f thisCol = new Vector2f(colPos2.x,colPos2.y);
		Vector2f otherCol = new Vector2f(pos.x, pos.y);

		if (Math.abs(thisCol.x - otherCol.x) < sizeX)
			if (Math.abs(thisCol.y - otherCol.y) < sizeY)
				return true;
		
		return false;
	}
}
