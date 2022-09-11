package stickman.figure;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import stickman.engine.GameItem;
import stickman.engine.Mesh;
import stickman.engine.Texture;
import stickman.engine.Transformation;
import stickman.level.Level;
import stickman.render.ShaderProgram;

public class Bullet extends ShaderProgram{

	private GameItem gameItem;
	private float rot;
	private float speed;
	private int lifeSpan = 100;
	public boolean dead = false;
	public boolean flames;
	
	public Bullet(float rot, float speed, Vector3f weaponPos, ShaderProgram program, boolean flames, Texture flame){
		this.speed = speed;
		this.rot = (float)Math.toRadians(rot);
		this.flames = flames;
		
		if(flames)
			lifeSpan = 50;
		
		fragmentShaderId = program.fragmentShaderId;
		vertexShaderId = program.vertexShaderId;
		programId = program.programId;
		uniforms = program.uniforms;
		
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
				0, 1, 3, 3, 1, 2, };
		
		if(flames){
			Mesh mesh = new Mesh(positions,textCoords, indices,flame);
			gameItem = new GameItem(mesh);
			gameItem.setPosition(new Vector3f(weaponPos.x + (float)Math.cos(this.rot) / 7,weaponPos.y + (float)Math.sin(this.rot) / 7,-0.1f - Level.objCount++ / 10000));
			gameItem.setScale(0.04f,0.04f * 43 / 30,0);
		} else {
			Mesh mesh = new Mesh(positions, textCoords, indices, null);
			gameItem = new GameItem(mesh);
			gameItem.setPosition(new Vector3f(weaponPos.x + (float)Math.cos(this.rot) / 7,weaponPos.y + (float)Math.sin(this.rot) / 7,-0.1f - Level.objCount++ / 10000));
			gameItem.setScale(0.01f,0.005f,0);
		}
		gameItem.setRotation(0, 0, rot);
	}
	
	public void render(Matrix4f projectionMatrix, Transformation transformation, float width, float height) {
		gameItem.getPosition().add(new Vector3f((float)Math.cos(rot) * speed, (float)Math.sin(rot) * speed,0));		
		Vector3f rotation = gameItem.getRotation();
		
		bind();
		setUniform("projectionMatrix", projectionMatrix);
		Matrix4f worldMatrix = transformation.getWorldMatrix(gameItem.getPosition(), rotation,
				gameItem.getScale());
		worldMatrix.translate(-1f + gameItem.getScale().y / gameItem.getScale().x,0,0);		
		setUniform("worldMatrix", worldMatrix);
		if(flames)
			setUniform("texture_sampler", 0);
		setUniform("flames", flames ? 1 : 0);
		
		gameItem.getMesh().render();

		unbind();
		
		lifeSpan--;		
		dead = lifeSpan == 0;
	}
	
	public Vector3f getPos() {
		return gameItem.getPosition();
	}
	
	public float getRot() {
		return rot;
	}
}
