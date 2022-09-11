package stickman.landscape;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import stickman.engine.Collider;
import stickman.engine.GameEngine;
import stickman.engine.GameItem;
import stickman.engine.IGameObject;
import stickman.engine.Mesh;
import stickman.engine.Texture;
import stickman.engine.Transformation;
import stickman.figure.Bullet;
import stickman.landscape.ObjectList.Types;
import stickman.level.Level;
import stickman.logic.Controlls;
import stickman.render.ShaderProgram;
import stickman.render.Utils;
import stickman.render.Window;

public class GameObject extends ShaderProgram implements IGameObject {

	protected GameItem gameItem;
	protected int flip = 0;
	
	protected Collider col;
	protected Texture texture;
	
	protected boolean landscape = true;

	public GameItem getGameItem() {
		return gameItem;
	}
	
	public GameObject() {
		
	}

	public GameObject(String path, ShaderProgram program) throws Exception {
		fragmentShaderId = program.fragmentShaderId;
		vertexShaderId = program.vertexShaderId;
		programId = program.programId;
		uniforms = program.uniforms;
		
		texture = new Texture(path);
		init();
	}
	
	private void init() {

		float[] positions = new float[] {

				-1.0f, 1.0f, 0.f,
				-1.0f, -1.0f, 0.f,
				1.0f, -1.0f, 0.f,
				1.0f, 1.0f, 0.f,
				-1.0f, 1.0f, 0.f,
				1.0f, 1.0f, 0.f,
				-1.0f, -1.0f, 0.f,
				1.0f, -1.0f, 0.f };

		float[] textCoords = new float[] {

				0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f,

				0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f };

		int[] indices = new int[] {
				0, 1, 3, 3, 1, 2 };

		Mesh mesh = new Mesh(positions, textCoords, indices, texture);
		gameItem = new GameItem(mesh);
		gameItem.setPosition(0, 0, 0);
	}
	
	public GameObject(GameItem gameItem, Texture texture, Collider col){
		this.texture = texture;
		init();
				
		this.gameItem.setPosition(new Vector3f(gameItem.getPosition()));
		this.gameItem.setRotation(new Vector3f(gameItem.getRotation()));
		this.gameItem.setScale(new Vector3f(gameItem.getScale()));
		if(col != null)
			this.col = new Collider(col.getSize(),col.getSolid());
	}
	
	public void addCollider(float x, float y, float z, boolean solid) {
		Vector3f scale = gameItem.getScale();
		col = new Collider(scale.x * x, scale.y *  y, scale.z * z, solid);
	}


	public void mulScale(float d) {
		gameItem.setScale(gameItem.getScale().mul(d));
		
	}
	
	public void mulScale(float x, float y) {
		gameItem.setScale(gameItem.getScale().mul(x,y,1));
		
	}

	public void move(float x, float y, float z) {
		gameItem.addPos(new Vector3f(x,y,landscape ? z : y * -400));
		if(col != null)
			col.update(new Vector3f(gameItem.getPosition()));
	}

	public void rot(float f) {
		gameItem.rot(f);
		
	}

	@Override
	public void update(float time, Level level) {
		
	}

	@Override
	public void render(Matrix4f projectionMatrix, Transformation transformation, int width, int height) {
		bind();
		setUniform("projectionMatrix", projectionMatrix);
		Matrix4f worldMatrix = transformation.getWorldMatrix(gameItem.getPosition(), gameItem.getRotation(),
				gameItem.getScale());
		setUniform("worldMatrix", worldMatrix);
		setUniform("texture_sampler", 0);
		setUniform("flip", flip);
		flip = 0;
		gameItem.getMesh().render();
		unbind();
		
		if(col != null)
			col.render(projectionMatrix, transformation, width, height);
	}

	@Override
	public float getZPos() {
		return getGameItem().getPosition().z;
	}
	
	protected void setLandscape(boolean landscape) {
		this.landscape = landscape;
	}
	
	public GameObject newGObj(float x, float y, int i) {
		GameObject go = new GameObject(gameItem,texture,col);
		float z = 0;
		switch(i) {
			case -1: z = -900f;	break;
			case 0: z = calcZ(y);	break;
			case 1: z = -1f;	 	break; 
		}
		go.move(x, y, z - Level.objCount++ / 10000);
		go.setLandscape(i != 0);
		go.setType(type);
		
		go.fragmentShaderId = fragmentShaderId;
		go.vertexShaderId = vertexShaderId;
		go.programId = programId;
		go.uniforms = uniforms;
		return go;
	}
	
	protected float calcZ(float y) {
		float z = y + 1 - gameItem.getScale().y;
		z *= -400;
		
		return z - 50;
	}

	@Override
	public void move(float x, float y) {
		move(x, y, 0);
	}

	@Override
	public void input(Controlls controlls, float mouseX, float mouseY, Vector2f target) {
		
	}
	
	public void flip() {
		flip = 1;
	}

	@Override
	public float getXPos() {
		return getGameItem().getPosition().x;
	}
		
	@Override
	public float getYPos() {
		return getGameItem().getPosition().y;
	}
	
	@Override
	public void cleanup() {
        super.cleanup();
        gameItem.getMesh().cleanUp();
        
    }

	@Override
	public boolean checkMove(IGameObject[] objects) {		
		if(col != null && col.getSolid())
			for (IGameObject object : objects) {
				if(col.checkColliders(object.getCollider()))
					return true;
			}
		
		return false;
	}

	@Override
	public Collider getCollider() {
		return col;
	}

	@Override
	public boolean hit(Vector3f pos, float rot, boolean flames) {
		return false;
	}

	@Override
	public boolean disappeared() {
		return false;
	}

	protected Types type;
	
	public void setType(Types m4) {
		this.type = m4;
	}
	
	@Override
	public Types getType() {
		return type;
	}
	
}
