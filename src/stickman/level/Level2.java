package stickman.level;

import java.util.ArrayList;

import org.joml.Matrix4f;
import org.joml.Vector2f;

import stickman.engine.IGameObject;
import stickman.engine.Sound;
import stickman.engine.Texture;
import stickman.engine.Transformation;
import stickman.figure.AllWeapons;
import stickman.figure.Body;
import stickman.figure.Stances;
import stickman.landscape.GameObject;
import stickman.landscape.ObjectList;
import stickman.landscape.ObjectList.Types;
import stickman.logic.Controlls;
import stickman.render.ShaderProgram;

public class Level2 extends Level{

	private GameObject[] gameObjects;
	private AllWeapons allWeapon;
	private Stances stances;
	private ShaderProgram[] programs;

	public Level2(ObjectList objList, Stances stances, Sound sound, AllWeapons allWeapon, ShaderProgram[] programs) throws Exception {
		super(sound);

		this.allWeapon = allWeapon;
		this.stances = stances;
		this.programs = programs;
		
		partTex = new Texture("/textures/part.png");
		headTex = new Texture("/textures/head.png");
		
		gameObjects = objList.getList();
		makeObjectList();
		levelWidth = 3f;
	}

	public void update(float time) {
		super.update(time);
	}

	public void render(Matrix4f projectionMatrix, Transformation transformation, int width, int height) {
		super.render(projectionMatrix, transformation, width, height);
	}

	public void input(Controlls controlls) {
		super.input(controlls);

		if (!end && (dead || ((Body) objects.get(0)).dead)) {
			dead = false;
			end = true;
			addTempObj(gameObjects[7].newGObj(0, 0, 1), 25f);
			sound.endSound("/sounds/nuke.ogg");
		}

		if ((int) (Math.random() * 1500) == 0) {
			
			addTempObj(gameObjects[1].newGObj(0, 0.8f, 0), 20);
		}

		if ((int) (Math.random() * 1500) == 0) {
			addTempObj(gameObjects[8].newGObj(0, 0.8f, 0),20);
		}
		
		if ((int) (Math.random() * 3000) == 0) {
			addTempObj(gameObjects[17].newGObj(0, 0.8f, 0),20);
		}
		
		for (IGameObject obj : objects) {
			if(obj.getType() == Types.M4 || obj.getType() == Types.SHOTGUN || obj.getType() == Types.FLAMETHROWER) {
				if(obj.getYPos() > -0.8) 
					obj.move(0, -0.005f);
			}
		}
		
		if ((int) (Math.random() * 30) == 0 && generatedEnemies > 0) {
			objects.add(new Body(new Vector2f(-4.5f - levelPos, -1f + (float) Math.random() / 8), stances, sound, 0,
					allWeapon, this,programs, partTex, headTex)); // enemy
			generatedEnemies--;
		}
		
		if ((int) (Math.random() * 30) == 0 && generatedEnemies > 0) {
			objects.add(new Body(new Vector2f(4.5f - levelPos, -1f + (float) Math.random() / 8), stances, sound, 0,
					allWeapon, this,programs, partTex, headTex)); // enemy
			generatedEnemies--;
		}
	}

	private boolean end;
	private int generatedEnemies = 200;


	private void makeObjectList() {
		enemies = generatedEnemies;
		objects = new ArrayList<>();
		objects.add(new Body(new Vector2f(-0.2f, -0.6f), stances, sound, 1, allWeapon, this,programs, partTex, headTex)); // main character
		objects.add(new Body(new Vector2f(0.2f, -0.6f), stances, sound, 2, allWeapon, this,programs, partTex, headTex)); // main character
		objects.add(gameObjects[12].newGObj(0, -.5f, -1)); // field
		
		objects.add(gameObjects[10].newGObj(1, -0.5f, 0)); // tree
		objects.add(gameObjects[11].newGObj(-1, -0.5f, 0)); // tree
		objects.add(gameObjects[10].newGObj(-1.8f, -0.2f, 0)); // tree
		objects.add(gameObjects[10].newGObj(2.5f, -0.35f, 0)); // tree
		objects.add(gameObjects[11].newGObj(-4, -0.4f, 0)); // tree
		objects.add(gameObjects[10].newGObj(3.5f, -0.2f, 0)); // tree
	}

	private boolean dead;

	public void collision(IGameObject obj, IGameObject collider) {
		if (collider.getType() == Types.FLAME && (obj.getType() == Types.PLAYER || obj.getType() == Types.ENEMY)) {
			Body body = (Body) obj;

			body.health--;
			if (body.health == 0 && obj.getType() == Types.PLAYER) {
				dead = true;
			}

			if (body.health <= 0) {
				body.dead = true;
			}
		}

		if (collider.getType() == Types.M4 && obj.getType() == Types.PLAYER) {
			Body body = (Body) obj;
			body.changeWeapon(1);
			
			objDurs.set(objIDs.indexOf(collider),0f);
			
		}

		if (collider.getType() == Types.SHOTGUN && obj.getType() == Types.PLAYER) {
			Body body = (Body) obj;
			body.changeWeapon(2);
			
			objDurs.set(objIDs.indexOf(collider),0f);
		}
		
		if (collider.getType() == Types.FLAMETHROWER && obj.getType() == Types.PLAYER) {
			Body body = (Body) obj;
			body.changeWeapon(3);
			
			objDurs.set(objIDs.indexOf(collider),0f);
		}
	}
	
	@Override
	protected boolean checkArea(int i, float moveX, float moveY) {
		boolean inArea = objects.get(i).getXPos() + moveX < 5 - levelPos;
		inArea &= objects.get(i).getXPos() + moveX > -5 - levelPos;
		
		inArea &= objects.get(i).getYPos() + moveY < -0.4f;
		inArea &= objects.get(i).getYPos() + moveY > -0.9f;
		return inArea;
	}
}
