package stickman.figure;

import java.util.ArrayList;
import java.util.List;
import stickman.engine.Sound;
import stickman.landscape.GameObject;
import stickman.render.ShaderProgram;

public class AllWeapons {

	private List<GameObject> weapons = new ArrayList<>();

	public AllWeapons(ShaderProgram program) throws Exception {
		GameObject gun = new GameObject("/textures/gun.png", program);
		gun.mulScale(0.03f);
		gun.mulScale(300f / 200, 1);
		weapons.add(gun);

		GameObject m4 = new GameObject("/textures/m4_cut.png", program);
		m4.mulScale(0.07f);
		m4.mulScale(302f / 167, 1);
		weapons.add(m4);

		GameObject shotgun = new GameObject("/textures/shotgun.png", program);
		shotgun.mulScale(0.04f);
		shotgun.mulScale(400f / 179, 1);
		weapons.add(shotgun);

		GameObject flamethrower = new GameObject("/textures/flamethrower.png", program);
		flamethrower.mulScale(0.05f);
		flamethrower.mulScale(520f / 200, 1);
		weapons.add(flamethrower);

		GameObject gun2 = new GameObject("/textures/gun.png", program);
		gun2.mulScale(0.03f);
		gun2.mulScale(300f / 200, 1);
		weapons.add(gun2);

		GameObject m42 = new GameObject("/textures/m4_cut.png", program);
		m42.mulScale(0.07f);
		m42.mulScale(302f / 167, 1);
		weapons.add(m42);

		GameObject shotgun2 = new GameObject("/textures/shotgun.png", program);
		shotgun2.mulScale(0.04f);
		shotgun2.mulScale(400f / 179, 1);
		weapons.add(shotgun2);

		GameObject flamethrower2 = new GameObject("/textures/flamethrower.png", program);
		flamethrower2.mulScale(0.05f);
		flamethrower2.mulScale(520f / 200, 1);
		weapons.add(flamethrower2);
	}

	public GameObject getWeapon(int i, int player) {
		return weapons.get(i + player * 4);
	}

	public float[] weaponDis = { 5f, 1, 1.2f, 1 };
	public float[] hand1Dis = { 7.7f, 1.7f, 1.7f, 1f };
	public float[] hand2Dis = { 7.5f, 2, 2.3f, 1.5f };

	public float[] shotDis = { 0.2f, 0.05f, 0.25f, 0 };

	public boolean fire(boolean shot, Weapon weapon, int curWepon, Sound sound, float rot) {
		switch (curWepon) {
		case 0:
			return fireGun(shot, weapon, sound, rot);
		case 1:
			return fireMG(shot, weapon, sound, rot);
		case 2:
			return fireSG(shot, weapon, sound, rot);
		case 3:
			return fireFT(shot, weapon, sound, rot);
		}
		return false;
	}

	public boolean fireGun(boolean shot, Weapon weapon, Sound sound, float rot) {
		boolean fired = false;
		weapon.coolDownGun++;
		if (shot) {
			if (weapon.coolDownGun > 50) {
				weapon.coolDownGun = 0;
				fired = true;
				weapon.shoot(shotDis[0], rot);
				sound.playSound("/sounds/shot2.ogg", 0.4f, 5);
			}
		}
		return fired;
	}

	public boolean fireMG(boolean shot, Weapon weapon, Sound sound, float rot) {
		boolean fired = false;
		if (shot) {
			if (weapon.shootDelay++ == 0) {
				weapon.coolDown++;
				if (weapon.coolDown > 5) {
					weapon.coolDown = weapon.coolDown % 10;
					fired = true;
					weapon.shoot(shotDis[1], rot);
					sound.playSound("/sounds/shot2.ogg", 0.4f, 5);
				}
			}
			weapon.shootDelay = weapon.shootDelay % 2;
		} else {
			weapon.shootDelay = 0;
			weapon.coolDown = 4;
		}

		return fired;
	}

	public boolean fireSG(boolean shot, Weapon weapon, Sound sound, float rot) {
		boolean fired = false;
		weapon.coolDownSG++;
		if (shot) {
			if (weapon.coolDownSG > 50) {
				weapon.coolDownSG = 0;
				fired = true;
				weapon.shoot(shotDis[2] / 5, rot - 5);
				weapon.shoot(shotDis[2] / 5, rot - 2.5f);
				weapon.shoot(shotDis[2] / 5, rot);
				weapon.shoot(shotDis[2] / 5, rot + 2.5f);
				weapon.shoot(shotDis[2] / 5, rot + 5);
				sound.playSound("/sounds/shotgun.ogg", 1f, 2);
			}
		}
		return fired;
	}

	private boolean fireFT(boolean shot, Weapon weapon, Sound sound, float rot) {
		boolean fired = false;

		if (shot) {
			fired = true;
			weapon.shoot(shotDis[3], rot + (float) (Math.random() * 10) - 5);
			if (weapon.coolDownFT++ > 2) {
				weapon.coolDownFT = 0;
				sound.playSound("/sounds/flame.ogg", 1f, 0.5f);
			}
		}
	return fired;

	}

	public void afterFire(Weapon weapon, int curWeapon) {
		weapon.afterShot(shotDis[curWeapon]);
	}

}
