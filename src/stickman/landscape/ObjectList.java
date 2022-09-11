package stickman.landscape;

import java.util.ArrayList;
import java.util.List;

import stickman.engine.GifTexture;
import stickman.render.ShaderProgram;

public class ObjectList {
	
	public enum Types {
		M4, PLAYER, ENEMY, SHOTGUN, FLAME, FLAMETHROWER
	};
	
	private List<GameObject> gameObjects = new ArrayList<>();

	public ObjectList(ShaderProgram[] programs) throws Exception{
		GameObject island = new GameObject("/textures/startisland.png",programs[2]);
		island.mulScale(1.2f);
		island.mulScale(800f / 594, 1);
		gameObjects.add(island); //0
		
		GameObject m4 = new GameObject("/textures/m4_cut.png",programs[2]);
		m4.mulScale(0.09f);
		m4.mulScale(302f / 167, 1);
		m4.setType(Types.M4);
		m4.addCollider(1, 1, 1, false);
		gameObjects.add(m4);	//1
		
		GameObject title = new GameObject("/textures/themtitle.png",programs[2]);
		title.mulScale(0.8f);
		title.mulScale(1280f / 720, 1);		
		gameObjects.add(title);//2
		
		GifTexture gif = new GifTexture("/textures/testgif.png", 27, 5,programs[3]);
		gif.mulScale(0.2f);
		gif.mulScale(248f / 240, 1);
		gameObjects.add(gif);//3
		
		GameObject crate = new GameObject("/textures/crate.png",programs[2]);
		crate.mulScale(0.15f);
		crate.mulScale(190f / 188, 1);
		crate.addCollider(1, 1, 5, true);
		gameObjects.add(crate);//4
		
		GifTexture gifNope = new GifTexture("/textures/nope.png", 6, 3, programs[3]);
		gifNope.mulScale(0.5f);
		gifNope.mulScale(476f / 253, 1);
		gameObjects.add(gifNope);//5
		
		GifTexture flame = new GifTexture("/textures/flame.png", 6, 5, programs[3]);
		flame.mulScale(0.2f);
		flame.mulScale(128f / 128, 1);
		flame.addCollider(1, 1, 4, false);
		flame.setType(Types.FLAME);
		gameObjects.add(flame);//6
		
		GifTexture dead = new GifTexture("/textures/nuke.png", 30, 10, programs[3]);
		dead.mulScale(2f);
		dead.mulScale(352 / 240, 1);
		gameObjects.add(dead);//7
		
		GameObject shotgun = new GameObject("/textures/shotgun.png",programs[2]);
		shotgun.mulScale(0.04f);
		shotgun.mulScale(400f / 179, 1);
		shotgun.setType(Types.SHOTGUN);
		shotgun.addCollider(1, 1, 1, false);
		gameObjects.add(shotgun);//8
		
		GameObject forest = new GameObject("/textures/forest.png",programs[2]);
		forest.mulScale(1.f);
		forest.mulScale(4782f / 547, 1);
		gameObjects.add(forest);//9
		
		GameObject tree1 = new GameObject("/textures/tree1.png",programs[2]);
		tree1.mulScale(0.5f);
		tree1.mulScale(470f / 500, 1);
		tree1.addCollider(0.3f, 1, 2, true);
		gameObjects.add(tree1);//10
		
		GameObject tree2 = new GameObject("/textures/tree2.png",programs[2]);
		tree2.mulScale(0.5f);
		tree2.mulScale(666f / 720, 1);
		tree2.addCollider(0.3f, 1, 2, true);
		gameObjects.add(tree2);//11
		
		GameObject field = new GameObject("/textures/field.png",programs[2]);
		field.mulScale(1f);
		field.mulScale(4000f / 700, 1);
		gameObjects.add(field);//12
		
		GameObject dessert = new GameObject("/textures/dessert.png",programs[2]);
		dessert.mulScale(0.5f);
		dessert.mulScale(3000f / 350, 1);
		gameObjects.add(dessert);//13
		
		GameObject tank = new GameObject("/textures/tank.png",programs[2]);
		tank.mulScale(0.5f);
		tank.mulScale(800f / 618, 1);
		tank.addCollider(1, 1, 4, true);
		gameObjects.add(tank);//14
		
		GameObject barrel = new GameObject("/textures/barrel.png",programs[2]);
		barrel.mulScale(0.2f);
		barrel.mulScale(800f / 800, 1);
		barrel.addCollider(1, 1, 2, true);
		gameObjects.add(barrel);//15
		
		GifTexture killstreak = new GifTexture("/textures/killstreak.png", 7, 5, programs[3]);
		killstreak.mulScale(0.5f);
		killstreak.mulScale(213 / 117, 1);
		gameObjects.add(killstreak);//16
		
		GameObject flamethrower = new GameObject("/textures/flamethrower.png",programs[2]);
		flamethrower.mulScale(0.05f);
		flamethrower.mulScale(520f / 200, 1);
		flamethrower.setType(Types.FLAMETHROWER);
		flamethrower.addCollider(1, 1, 1, false);
		gameObjects.add(flamethrower);//17
	}
	
	public GameObject[] getList() {
		return gameObjects.toArray(new GameObject[gameObjects.size()]);
	}
}
