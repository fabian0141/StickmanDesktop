package stickman.level;

import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.lwjgl.BufferUtils;

import stickman.engine.IGameObject;
import stickman.engine.ILevel;
import stickman.engine.Sound;
import stickman.engine.Texture;
import stickman.engine.Transformation;
import stickman.figure.Body;
import stickman.figure.Bullet;
import stickman.landscape.GameObject;
import stickman.logic.Controlls;
import static org.lwjgl.glfw.GLFW.glfwGetCursorPos;
import static org.lwjgl.glfw.GLFW.*;

public class Level implements ILevel{
	
	protected List<IGameObject> objects;
	private List<Bullet> bullets = new ArrayList<>();

	protected float mouseX, mouseY;

	protected float levelWidth;
	protected float levelPos = 0;
	protected final Sound sound;

	private float time;	
	public static float objCount = 0;
	protected int deleteWeapon = -1;

	protected List<GameObject> objIDs = new ArrayList<>();
	protected List<Float> objDurs = new ArrayList<>();
	
    protected int enemies;
    
    protected Texture partTex;
    protected Texture headTex;

	protected Level(Sound sound) {
		this.sound = sound;
	}

	public void update(float time) {
		this.time = time;

		for (int i = 0; i < objDurs.size(); i++) {
			if (objDurs.get(i) < time) {
				objects.remove(objIDs.get(i));
				objIDs.remove(i);
				objDurs.remove(i);
				i--;
			}
		}

		for (IGameObject object : objects) {
			object.update(time,this);
		}
		
		for (int i = 0; i < objects.size(); i++) {
			if(objects.get(i).disappeared()) {
				objects.remove(i--);
				enemies--;
			}
		}
		
		for (IGameObject object : objects) {
			for (int i = 0; i < bullets.size(); i++) {
				if(object.hit(bullets.get(i).getPos(), bullets.get(i).getRot(),bullets.get(i).flames))
					bullets.remove(i--);
			}
		}
	}

	public void render(Matrix4f projectionMatrix, Transformation transformation, int width, int height) {
		TreeMap<Float, IGameObject> gameObjects = sortObjects();
		for (Map.Entry<Float, IGameObject> object : gameObjects.entrySet()) {
			object.getValue().render(projectionMatrix, transformation, width, height);
		}
		
		for (int i = 0; i < bullets.size(); i++) {
			if (bullets.get(i).dead) {
				bullets.remove(i);
			} 
		}
		TreeMap<Float, Bullet> bulletsSorted = sortBullets();
		for (Map.Entry<Float, Bullet> bullet : bulletsSorted.entrySet()) {
			bullet.getValue().render(projectionMatrix, transformation, width, height);
		}
	}

	private TreeMap<Float, IGameObject> sortObjects() {
		TreeMap<Float, IGameObject> gameObjects = new TreeMap<Float, IGameObject>();

		for (IGameObject object : objects) {
			gameObjects.put(object.getZPos(), object);
		}
		return gameObjects;
	}
	
	private TreeMap<Float, Bullet> sortBullets() {
		TreeMap<Float, Bullet> bulletsSorted = new TreeMap<Float, Bullet>();

		for (Bullet bullet : bullets) {
			bulletsSorted.put(bullet.getPos().z, bullet);
		}
		return bulletsSorted;
	}

	public void input(Controlls controlls) {
		DoubleBuffer posX = BufferUtils.createDoubleBuffer(1);
		DoubleBuffer posY = BufferUtils.createDoubleBuffer(1);
		glfwGetCursorPos(controlls.window.getID(), posX, posY);
		mouseX = (float) posX.get(0);
		mouseY = (float) posY.get(0);

		for (IGameObject object : objects) {
			object.input(controlls, mouseX, mouseY, new Vector2f(objects.get(0).getXPos(), objects.get(0).getYPos()));
		}

		float deltaDisPlayer = objects.get(0).getXPos() + objects.get(1).getXPos();
		deltaDisPlayer /= -2; 

		if(levelPos + deltaDisPlayer > -levelWidth && levelPos + deltaDisPlayer < levelWidth) {
			levelPos += deltaDisPlayer; 
			for (int i = 0; i < objects.size(); i++) {
				objects.get(i).move(deltaDisPlayer, 0);
			}
		}
		
		
		for (int i = 0; i < 2; i++) {
			if (controlls.isRight(i == 0)) {
				if(objects.get(i).getXPos() < 2 && checkArea(i,0.016f, 0))
					objects.get(i).move(0.016f, 0);
				
				if(objects.get(i).checkMove(objects.toArray(new IGameObject[objects.size()])))
					objects.get(i).move(-0.016f, 0);
				
			}
			
			if (controlls.isLeft(i == 0)) {
				if(objects.get(i).getXPos() > -2 && checkArea(i,-0.016f, 0))
					objects.get(i).move(-0.016f, 0);
				
				if(objects.get(i).checkMove(objects.toArray(new IGameObject[objects.size()]))) 
					objects.get(i).move(0.016f, 0);
				
			}
			
			if (controlls.isTop(i == 0)) {
				if(checkArea(i,0,0.004f))
					objects.get(i).move(0, 0.004f);
				
				if(objects.get(i).checkMove(objects.toArray(new IGameObject[objects.size()])))
					objects.get(i).move(0, -0.004f);
			}
			
			if (controlls.isBottom(i == 0)) {
				if(checkArea(i,0,-0.004f))
					objects.get(i).move(0, -0.004f);
				
				if(objects.get(i).checkMove(objects.toArray(new IGameObject[objects.size()])))
					objects.get(i).move(0, 0.004f);
			}
		}		
	}

	protected boolean checkArea(int i,float moveX,float moveY) {
		return true;
	}

	protected void addTempObj(GameObject gameObject, float dur) {
		objects.add(gameObject);
		objDurs.add(time + dur);
		objIDs.add(gameObject);
	}

	public void cleanup() {
		for (IGameObject obj : objects) {
			obj.cleanup();
		}
	}

	@Override
	public void addBullet(Bullet bul) {
		bullets.add(bul);
	}
	
	public Vector2f getPlayerPos(int i) {
		return new Vector2f(objects.get(i).getXPos(),objects.get(i).getYPos());
	}

	public void removeEnemy(Body body) {
		objects.remove(body);
	}

	public boolean nextLevel() {
		return enemies == 0;
	}
	
	public void collision(IGameObject obj, IGameObject collider) {
		
	}
	
	public IGameObject[] getObjects() {
		return objects.toArray(new IGameObject[objects.size()]);
	}
	
}
