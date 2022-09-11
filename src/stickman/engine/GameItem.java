package stickman.engine;

import org.joml.Vector3f;

import stickman.engine.Mesh;

public class GameItem {

    private final Mesh mesh;
    
    private Vector3f position;
    
    private Vector3f scale;

    private Vector3f rotation;

    public GameItem(Mesh mesh) {
        this.mesh = mesh;
        position = new Vector3f(0, 0, 0);
        scale = new Vector3f(1f, 1f, 1);
        rotation = new Vector3f(0, 0, 0);
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(float x, float y, float z) {
        this.position.x = x;
        this.position.y = y;
        this.position.z = z;
    }

    public Vector3f getScale() {
        return scale;
    }

    public void setScale(Vector3f scale2) {
        this.scale = scale2;
    }
    
    public void setScale(float scaleX, float scaleY, float scaleZ) {
        this.scale = new Vector3f(scaleX,scaleY,scaleZ);
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void setRotation(float x, float y, float z) {
        this.rotation.x = x;
        this.rotation.y = y;
        this.rotation.z = z;
    }
    
    public void setRotation(Vector3f rot) {
        this.rotation = rot;
    }
    
    public Mesh getMesh() {
        return mesh;
    }

	public void addPos(Vector3f add) {
		position.add(add);
	}

	public void rot(float f) {
		rotation.z = (rotation.z + f) % 360;
	}

	public void setPosition(Vector3f pos) {
		position = pos;
		
	}
}