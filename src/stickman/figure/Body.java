package stickman.figure;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import stickman.engine.Collider;
import stickman.engine.IGameObject;
import stickman.engine.ILevel;
import stickman.engine.Sound;
import stickman.engine.Texture;
import stickman.engine.Transformation;
import stickman.landscape.ObjectList.Types;
import stickman.level.Level;
import stickman.logic.Controlls;
import stickman.render.ShaderProgram;

public class Body implements IGameObject {

	private Part[] parts = new Part[10];
	private Head head;
	private String partNames[] = { "be", "ch", "lu", "ru", "lf", "rf", "lt", "rt", "ls", "rs" };

	private Vector3f posBody;
	private Weapon weapon;

	private Collider col;

	String curMove = "wl";

	private int player;
	public float health = 100;
	public boolean dead;
	private int disappear = 200;
	private int targetPlayer = 0;
	private Sound sound;

	public Body(Vector2f pos, Stances stances, Sound sound, int player, AllWeapons allWeapons, ILevel level, ShaderProgram[] programs, Texture partTex, Texture headTex) {

		this.player = player;
		this.sound = sound;
		Vector3f color;

		if (player == 1) {
			color = new Vector3f(0.2f, 1, 1);
		} else if (player == 2) {
			color = new Vector3f(0.2f, 1, 0.2f);
		} else {
			color = new Vector3f(0, 0, 0);
			targetPlayer = (int) (Math.random() * 2);
		}
		head = new Head(stances, color, player != 0,programs[0], headTex);
		posBody = new Vector3f(pos.x, pos.y, calcZ(pos.y) - Level.objCount++ / 10000);
		for (int i = 0; i < parts.length; i++) {
			parts[i] = new Part(partNames[i], stances, color, player != 0,programs[1],partTex);
		}
		parts[0].getGameItem().rot(270);
		parts[1].getGameItem().rot(270);
		parts[2].getGameItem().rot(10);
		parts[3].getGameItem().rot(20);
		parts[4].getGameItem().rot(340);
		parts[5].getGameItem().rot(350);
		parts[6].getGameItem().rot(70);
		parts[7].getGameItem().rot(110);
		parts[8].getGameItem().rot(80);
		parts[9].getGameItem().rot(100);

		head.getGameItem().rot(270);

		if (player != 0)
			weapon = new Weapon(sound, allWeapons, level, player,programs[4]);

		col = new Collider(0.07f, 0.182f, 0.05f, true);
	}

	private float calcZ(float y) {
		float z = y + 1 - 0.16f;
		z *= -400;

		return z - 50;
	}

	@Override
	public void render(Matrix4f projectionMatrix, Transformation transformation, int width, int height) {
		for (Part part : parts) {
			part.render(projectionMatrix, transformation, width, height);
		}
		head.render(projectionMatrix, transformation, width, height);
		if (player != 0)
			weapon.render(projectionMatrix, transformation, width, height);
		col.render(projectionMatrix, transformation, width, height);
	}

	private float enemSpeed = (float) Math.random() / 2 + 0.5f;
	private float sunkDeath = 0;

	@Override
	public void update(float time, Level level) {

		checkCollider(level.getObjects(), level);

		if (player == 0 && !dead) {
			Vector2f plPos = level.getPlayerPos(targetPlayer);
			plPos.sub(posBody.x, posBody.y);
			plPos.mul(1 / plPos.length() / 400 * enemSpeed);
			
			boolean moving = false;
			move(plPos.x, 0);
			if (checkMove(level.getObjects())) {
				move(-plPos.x, 0);
				moving = true;
			}
			move(0, plPos.y);
			if (checkMove(level.getObjects())) {
				move(0, -plPos.y);
				moving = true;
			}
			if(!moving) {
				enemyMovement(posBody.x < level.getPlayerPos(targetPlayer).x ? 1 : 2);
			}
			
			int rand = (int)(Math.random() * 50000);
			
			switch(rand) {
				case 0:
					sound.playSound("/sounds/zombie1.ogg", 0.3f,3);
					break;
				case 1:
					sound.playSound("/sounds/zombie2.ogg", 0.3f,5);
					break;
				case 2:
					sound.playSound("/sounds/zombie3.ogg", 0.3f,3);
					break;
			}
			
			if(rand == 1) {
				
			}
		} else if (player == 0) {
			disappear--;
			enemyMovement(5);
			sunkDeath += 0.0009f;
			if (sunkDeath < 0.04f)
				posBody.y -= 0.0025f;
		}

		head.update(time);

		for (Part part : parts) {
			part.update(time);
		}
		if (player != 0)
			weapon.update(time);

		parts[0].getGameItem().setPosition(posBody); // belly
		Vector3f pos = parts[0].getEndPos();
		parts[1].getGameItem().setPosition(pos.x, pos.y, posBody.z + 0.001f); // chest
		pos = parts[1].getEndPos();
		head.getGameItem().setPosition(pos.x, pos.y, posBody.z + 0.002f); // head
		parts[2].getGameItem().setPosition(pos.x, pos.y, posBody.z + 0.0011f); // left upper arm
		parts[3].getGameItem().setPosition(pos.x, pos.y, posBody.z + 0.0012f); // right upper arm

		parts[6].getGameItem().setPosition(posBody.x, posBody.y, posBody.z + 0.0015f); // left upper leg
		parts[7].getGameItem().setPosition(posBody.x, posBody.y, posBody.z + 0.0016f); // right upper leg
		pos = parts[6].getEndPos();
		parts[8].getGameItem().setPosition(pos.x, pos.y, posBody.z + 0.0017f); // left under leg
		pos = parts[7].getEndPos();
		parts[9].getGameItem().setPosition(pos.x, pos.y, posBody.z + 0.0018f); // right under leg

		if (player == 0) {
			setZombieArms();
		} else {
			setWeaponStance();
		}

		col.update(new Vector3f(posBody).add(0, 0.025f, 0));
	}

	private void enemyMovement(int move) {
		String movement = "st";
		switch (move) {
		case 0:
			movement = "st";
			break;
		case 1:
			movement = "wr";
			break;
		case 2:
			movement = "wl";
			break;
		case 3:
			movement = "fl";
			break;
		case 4:
			movement = "fr";
			break;
		case 5:
			movement = "de";
			break;
		}

		if (dead)
			movement = "de";

		head.changeMove(movement);
		for (Part part : parts) {
			part.changeMove(movement);
		}
	}

	private void setZombieArms() {
		Vector3f pos = parts[3].getEndPos();
		parts[5].getGameItem().setPosition(pos.x, pos.y, posBody.z + 0.0014f); // right under arm
		pos = parts[2].getEndPos();
		parts[4].getGameItem().setPosition(pos.x, pos.y, posBody.z + 0.0013f); // left under arm

	}

	private void setWeaponStance() {
		Vector3f pos = head.getEndPos();
		weapon.setPosition(pos.x, pos.y, posBody.z + 0.0021f);

		float alpha = getPosDelta(parts[1].getEndPos(), weapon.getFirstHandPos());
		float beta = 180 - 2 * alpha;
		float gamma;

		float alpha2 = getPosDelta(parts[1].getEndPos(), weapon.getSecondHandPos());
		float beta2 = 180 - 2 * alpha2;
		float gamma2;

		if (weapon.flipped) {
			gamma = weapon.getRot() + beta / 2 - 180;
			gamma2 = weapon.getRot() + beta2 / 2 - 180;
		} else {
			gamma = weapon.getRot() - beta / 2 - 180;
			gamma2 = weapon.getRot() - beta2 / 2 - 180;
		}

		parts[3].rotPart(gamma);
		parts[2].rotPart(gamma2);

		pos = parts[3].getEndPos();
		parts[5].getGameItem().setPosition(pos.x, pos.y, posBody.z + 0.0014f); // right under arm
		pos = parts[2].getEndPos();
		parts[4].getGameItem().setPosition(pos.x, pos.y, posBody.z + 0.0013f); // left under arm
		if (weapon.flipped) {
			parts[5].rotPart(gamma - beta);
			parts[4].rotPart(gamma2 - beta2);
		} else {
			parts[5].rotPart(gamma + beta);
			parts[4].rotPart(gamma2 + beta2);
		}
	}

	private float getPosDelta(Vector3f sPos, Vector3f ePos) {
		Vector3f dPos = new Vector3f(sPos);
		dPos.sub(ePos);
		return (float) Math.sinh(dPos.length() * 20);
	}

	@Override
	public float getZPos() {
		return posBody.z;
	}

	@Override
	public void move(float x, float y) {
		posBody.add(x, y, y * -400);
		col.update(new Vector3f(posBody).add(0, 0.025f, 0));
	}

	private boolean notMoving;

	@Override
	public void input(Controlls controlls, float mouseX, float mouseY, Vector2f target) {
		notMoving = true;
		if (controlls.isLeft(player == 1) & player != 0) {
			curMove = "wl";
			head.changeMove("wl");
			for (Part part : parts) {
				part.changeMove("wl");
			}
			notMoving = false;
		}
		if (controlls.isRight(player == 1) & player != 0) {
			curMove = "wr";
			head.changeMove("wr");
			for (Part part : parts) {
				part.changeMove("wr");
			}
			notMoving = false;
		}
		if (controlls.isTop(player == 1) & player != 0) {
			if (notMoving) {
				curMove = "wl";
				head.changeMove("wl");
				for (Part part : parts) {
					part.changeMove("wl");
				}
				notMoving = false;
			}
		}
		if (controlls.isBottom(player == 1) && player != 0) {
			if (notMoving) {
				curMove = "wr";
				head.changeMove("wr");
				for (Part part : parts) {
					part.changeMove("wr");
				}
				notMoving = false;
			}
		}

		if (notMoving && player != 0) {
			head.changeMove("st");
			for (Part part : parts) {
				part.changeMove("st");
			}
		}
		if (player != 0)
			if (weapon.input(controlls, mouseX, mouseY, target)) {

			}
	}

	@Override
	public float getXPos() {
		return posBody.x;
	}

	@Override
	public float getYPos() {
		return posBody.y;
	}

	@Override
	public void cleanup() {
		for (Part part : parts) {
			part.cleanup();
		}
		head.cleanup();
		if (player != 0)
			weapon.cleanup();
	}

	@Override
	public boolean checkMove(IGameObject[] objects) {
		for (IGameObject object : objects) {
			if (!isEnemy(object)) {
				if (isColliding(object.getCollider()))
					return true;
			}
		}
		return false;
	}

	private boolean isColliding(Collider oCol) {
		if (oCol != null)
			if (col.checkColliders(oCol) && oCol.getSolid())
				return true;

		return false;
	}

	private boolean isEnemy(IGameObject object) {
		if (object.getType() == Types.PLAYER) {
			Body body = (Body) object;
			if (isColliding(body.col)) {
				body.health -= 1f / 60;
				if (body.health < 0)
					body.dead = true;
				enemyMovement(posBody.x > body.posBody.x ? 3 : 4);
				return false;
			}
		}
		return object.getType() == Types.ENEMY;
	}

	private void checkCollider(IGameObject[] objects, Level level) {
		for (IGameObject object : objects) {
			Collider oCol = object.getCollider();
			if (oCol != null)
				if (!oCol.getSolid())
					if (col.checkColliders(oCol))
						level.collision(this, object);
		}
	}

	@Override
	public Collider getCollider() {
		return col;
	}

	@Override
	public boolean hit(Vector3f pos, float rot, boolean flames) {
		if (player != 0 || dead)
			return false;

		boolean hitted = col.checkHit(pos);
		if (hitted) {
			if(flames){
				health -= 0.2f;
			} else{
				posBody.add(new Vector3f((float) Math.cos(rot) * 0.01f, (float) Math.sin(rot) * 0.01f, 0));
				health -= 20;
			}
			
			if (health < 0)
				dead = true;
		}
		return hitted && !flames;
	}

	@Override
	public boolean disappeared() {
		return disappear <= 0;
	}

	@Override
	public Types getType() {
		return player == 0 ? Types.ENEMY : Types.PLAYER;
	}

	public void changeWeapon(int i) {
		weapon.changeWeapon(i);
	}

}
