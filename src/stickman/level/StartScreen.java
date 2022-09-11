package stickman.level;

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

import java.util.ArrayList;

public class StartScreen extends Level {

	private GameObject[] gameObjects;
	private AllWeapons allWeapon;
	private Stances stances;
	private ShaderProgram[] programs;

	public StartScreen(ObjectList objList, Stances stances, Sound sound, AllWeapons allWeapon, ShaderProgram[] programs) throws Exception {
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
			addTempObj(gameObjects[7].newGObj(-1.6f, -0.6f, 1), 30f);
		}
	}

	private boolean end;

	private void makeObjectList() {
		enemies = -1;
		objects = new ArrayList<>();
		objects.add(new Body(new Vector2f(-0.2f, -0.5f), stances, sound, 1, allWeapon, this,programs, partTex, headTex)); // main character
		objects.add(new Body(new Vector2f(0.2f, -0.5f), stances, sound, 2, allWeapon, this,programs, partTex, headTex)); // main character

		objects.add(gameObjects[0].newGObj(0f, 0.2f, -1)); // island
		objects.add(gameObjects[2].newGObj(0, 0.4f, 1)); // theme
		objects.add(gameObjects[4].newGObj(-0.5f, -0.6f, 0)); // crate
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
	}
	
	@Override
	protected boolean checkArea(int i, float moveX, float moveY) {
		boolean inArea = objects.get(i).getXPos() + moveX < 1 - levelPos;
		inArea &= objects.get(i).getXPos() + moveX > -1 - levelPos;
		
		inArea &= objects.get(i).getYPos() + moveY < -0.4f;
		inArea &= objects.get(i).getYPos() + moveY > -0.6f;
		return inArea;
	}
}
