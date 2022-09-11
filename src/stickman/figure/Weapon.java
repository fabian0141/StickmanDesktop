package stickman.figure;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GLUtil;

import stickman.engine.ILevel;
import stickman.engine.Sound;
import stickman.engine.Texture;
import stickman.engine.Transformation;
import stickman.landscape.GameObject;
import stickman.logic.Controlls;
import stickman.render.ShaderProgram;

public class Weapon {

	private GameObject weapon;
	private Vector3f weaponPos = new Vector3f(0);
	private final Sound sound;
	private ILevel level;
	private AllWeapons allWe;
	private int player;
	
	private int ammoMG = 0;
	private int ammoSG = 0;
	private int ammoFT = 0;
	
	public int coolDownSG = 0;
	public int coolDownGun = 0;
	public int coolDownFT = 0;
	
	private ShaderProgram bulShader;
	private Texture flames;

	public Weapon(Sound sound, AllWeapons allWe, ILevel level, int player, ShaderProgram program){
		this.sound = sound;
		this.level = level;
		this.allWe = allWe;
		this.player = player;
		bulShader = program;
		
		try {
			flames = new Texture("/textures/flames.png");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		changeWeapon();
	}

	public void render(Matrix4f projectionMatrix, Transformation transformation, int width, int height) {
		weapon.render(projectionMatrix, transformation, width, height);
	}

	public void update(float time) {
		
	}

	public boolean flipped = false;
	
	public int curWeapon = 0;

	public boolean input(Controlls controlls, float mouseX, float mouseY, Vector2f target) {		
		
		float rot = controlls.getRot(player == 1);
		
		if (rot > 90 && rot < 270) {
			weapon.flip();
			flipped = true;
		} else {
			flipped = false;
		}

		weapon.getGameItem().setRotation(0, 0, rot);

		if(!shot && allWe.fire(controlls.isShoot(player == 1), this,curWeapon,sound, rot)) {
			shot = true;
		} else if(shot) {
			shot = false;
			allWe.afterFire(this,curWeapon);
		}

		return false;
	}
	
	public void shoot(float shotDis, float rot){
		delta -= shotDis;
		delta2 -= shotDis;
		delta3 -= shotDis;
		if(curWeapon == 0) {
			level.addBullet(new Bullet(rot, 0.1f, weaponPos,bulShader,false,flames));
		} else if(curWeapon == 1) {
			level.addBullet(new Bullet(rot, 0.1f, weaponPos,bulShader,false,flames));
			ammoMG--;
			if(ammoMG == 0) {
				curWeapon = 0;
				changeWeapon();
			}
		}else if(curWeapon == 2) {
			level.addBullet(new Bullet(rot, 0.1f, weaponPos,bulShader,false,flames));
			ammoSG--;
			if(ammoSG == 0) {
				curWeapon = 0;
				changeWeapon();
			}
		}else if(curWeapon == 3) {
			level.addBullet(new Bullet(rot, 0.02f, weaponPos,bulShader,true,flames));
			ammoFT--;
			if(ammoFT == 0) {
				curWeapon = 0;
				changeWeapon();
			}
		}
	}
	
	public void afterShot(float shotDis){
		delta += shotDis;
		delta2 += shotDis;
		delta3 += shotDis;
	}
	
	
	private boolean shot;
	public int shootDelay = 0;
	public int coolDown = 0;

	public void changeWeapon(int i) {
		curWeapon = i;
		if(i == 1)
			ammoMG = 250;
		if(i == 2)
			ammoSG = 100;
		if(i == 3)
			ammoFT = 300;
		changeWeapon();
	}
	
	private void changeWeapon() {
		weapon = allWe.getWeapon(curWeapon,player - 1);
		delta = allWe.weaponDis[curWeapon];
		delta2 = allWe.hand1Dis[curWeapon];
		delta3 = allWe.hand2Dis[curWeapon];
	}

	public void setPosition(float x, float y, float z) {
		weaponPos.set(x, y, z);
		setWeaponPos();
	}

	public float getRot() {
		return weapon.getGameItem().getRotation().z;
	}

	private float delta;

	public void setWeaponPos() {
		weapon.getGameItem().setPosition(calcRotPos(delta));
	}

	private float delta2;

	public Vector3f getFirstHandPos() {
		return calcRotPos(delta2);
	}

	private float delta3;

	public Vector3f getSecondHandPos() {
		return calcRotPos(delta3);
	}

	private Vector3f calcRotPos(float delta) {
		Vector3f endPos = new Vector3f(0);
		endPos.add(weaponPos);
		float h = weapon.getGameItem().getScale().y * 2;
		float w = weapon.getGameItem().getScale().x * 2 - h;
		float rotation = -weapon.getGameItem().getRotation().z / 57.3f;
		endPos.add(new Vector3f(w * (float) Math.sin(rotation + 1.57) * delta,
				w * (float) Math.cos(rotation + 1.57) * delta, 0));
		
		return endPos;
	}

	public void cleanup() {
		weapon.cleanup();
	}
}
