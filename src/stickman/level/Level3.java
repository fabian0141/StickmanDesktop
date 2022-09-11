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

public class Level3 extends Level{

	private GameObject[] gameObjects;
	private AllWeapons allWeapon;
	private Stances stances;
	private ShaderProgram[] programs;

	public Level3(ObjectList objList, Stances stances, Sound sound, AllWeapons allWeapon, ShaderProgram[] programs) throws Exception {
		super(sound);

		this.allWeapon = allWeapon;
		this.stances = stances;
		this.programs = programs;
		
		partTex = new Texture("/textures/part.png");
		headTex = new Texture("/textures/head.png");
		
		gameObjects = objList.getList();
		makeObjectList();
		levelWidth = 2f;
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

		if ((int) (Math.random() * 1200) == 0) {
			addTempObj(gameObjects[1].newGObj(0, 0.8f, 0), 20);
		}

		if ((int) (Math.random() * 1200) == 0) {
			addTempObj(gameObjects[8].newGObj(0, 0.8f, 0),20);
		}
		
		if ((int) (Math.random() * 1500) == 0) {
			addTempObj(gameObjects[17].newGObj(0, 0.8f, 0),20);
		}
		
		for (IGameObject obj : objects) {
			if(obj.getType() == Types.M4 || obj.getType() == Types.SHOTGUN || obj.getType() == Types.FLAMETHROWER) {
				if(obj.getYPos() > -0.8) 
					obj.move(0, -0.005f);
			}
		}
		
		if ((int) (Math.random() * 30) == 0 && generatedEnemies > 0) {
			objects.add(new Body(new Vector2f(-3.5f - levelPos, -1f + (float) Math.random() / 8), stances, sound, 0,
					allWeapon, this,programs, partTex, headTex)); // enemy
			generatedEnemies--;
		}
		
		if ((int) (Math.random() * 30) == 0 && generatedEnemies > 0) {
			objects.add(new Body(new Vector2f(3.5f - levelPos, -1f + (float) Math.random() / 8), stances, sound, 0,
					allWeapon, this,programs, partTex, headTex)); // enemy
			generatedEnemies--;
		}
	}

	private boolean end;
	private int generatedEnemies = 1000;


	private void makeObjectList() {
		enemies = generatedEnemies;
		objects = new ArrayList<>();
		objects.add(new Body(new Vector2f(-0.2f, -0.7f), stances, sound, 1, allWeapon, this,programs, partTex, headTex)); // main character
		objects.add(new Body(new Vector2f(0.2f, -0.7f), stances, sound, 2, allWeapon, this,programs, partTex, headTex)); // main character
		objects.add(gameObjects[13].newGObj(0, -.5f, -1)); // forest
		objects.add(gameObjects[14].newGObj(1.7f, -.6f, 0)); // tank
		objects.add(gameObjects[15].newGObj(-1.5f, -.7f, 0)); // barrel		
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
	public boolean nextLevel() {
		if(super.nextLevel()) {
			enemies = -1;
			objects.add(gameObjects[16].newGObj(0f, 0f, 1)); // end		
			sound.playSound("/sounds/welldone.ogg", 1,3);
		}
		return false;
	}
	
	@Override
	protected boolean checkArea(int i, float moveX, float moveY) {
		boolean inArea = objects.get(i).getXPos() + moveX < 4 - levelPos;
		inArea &= objects.get(i).getXPos() + moveX > -4 - levelPos;
		
		inArea &= objects.get(i).getYPos() + moveY < -0.6f;
		inArea &= objects.get(i).getYPos() + moveY > -0.9f;
		return inArea;
	}
	
}
