package stickman.engine;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import stickman.landscape.ObjectList.Types;
import stickman.level.Level;
import stickman.logic.Controlls;
import stickman.render.Window;

public interface IGameObject {

	void update(float time, Level level);
    
    void render(Matrix4f projectionMatrix, Transformation transformation, int width, int height);
    
    float getZPos();
    float getXPos();
    float getYPos();
    
    void move(float x, float y);

	void input(Controlls controlls, float mouseX, float mouseY, Vector2f target);
	
	boolean checkMove(IGameObject[] objects);
	Collider getCollider();

	void cleanup();

	boolean hit(Vector3f pos, float rot, boolean flames);

	boolean disappeared();
	
	Types getType();
	
}
