package stickman.figure;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GLUtil;

import stickman.engine.GameEngine;
import stickman.engine.GameItem;
import stickman.engine.Mesh;
import stickman.engine.Texture;
import stickman.engine.Transformation;
import stickman.render.ShaderProgram;

public class Part extends ShaderProgram{
	private GameItem gameItem;
	private String partName;
	private Vector3f color;
	private boolean enemy;
	
	public GameItem getGameItem() {
		return gameItem;
	}

	public Part(String partName, Stances stances, Vector3f color, boolean enemy, ShaderProgram program, Texture partTex) {
		//super();
		this.stances = stances;
		this.partName = partName;
		this.color = color;
		this.enemy = enemy;
		
		
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
				0, 1, 3, 3, 1, 2 };
		
        Mesh mesh = new Mesh(positions, textCoords, indices, partTex);
		gameItem = new GameItem(mesh);
		gameItem.setScale(0.05f,0.0125f,0);
				
		anim = stances.getStickAnims(partName, curMove, enemy);
		animTime = stances.getAnimLength(partName, curMove, enemy);
	}

	public void render(Matrix4f projectionMatrix, Transformation transformation, float width, float height) {
				
		bind();
		setUniform("projectionMatrix", projectionMatrix);
		Matrix4f worldMatrix = transformation.getWorldMatrix(gameItem.getPosition(), gameItem.getRotation(),
				gameItem.getScale());
		
		worldMatrix.translate(-1f + gameItem.getScale().y / gameItem.getScale().x,0,0);
		
		
		setUniform("worldMatrix", worldMatrix);
		setUniform("texture_sampler", 0);
		setUniform("col", color.x,color.y,color.z);

		gameItem.getMesh().render();

		unbind();
	}

	private float curRot;
	private float aimRot;
	private float timeToRot;
	private float timeBeginRot;
	private float[] anim;
	private float[] animTime;
	private int curAnimPos = 0;

	
	private boolean finishedSequence = true;
	private String curMove = "wl";
	private Stances stances;
	
	public void update(float time) {

		if(anim != null) {
			if(finishedSequence)
				takeAnimSequence(time);
			
			movePart(time);
		}
	}
	
	private void takeAnimSequence(float time) {	
		curRot = gameItem.getRotation().z;
		aimRot = anim[curAnimPos];
		timeToRot = animTime[curAnimPos];
		timeBeginRot = time;
		
		curAnimPos = (curAnimPos + 1) % anim.length;
		finishedSequence = false;
	}
	
	private void movePart(float time) {		
		float rot = easeInOutSin(time - timeBeginRot,timeToRot);
		gameItem.setRotation(0, 0, rot);
		
		if(time - timeBeginRot > timeToRot)
			finishedSequence = true;
	}
	
	private float easeInOutSin(float t, float dur) {
		return -(aimRot - curRot)/2 * ((float)Math.cos(Math.PI*t/dur) - 1) + curRot;
	}

	public Vector3f getEndPos() {
		Vector3f endPos = new Vector3f(0);
		endPos.add(gameItem.getPosition());
		float h = gameItem.getScale().y * 2;
		float w = gameItem.getScale().x * 2 - h;
		float rotation = -gameItem.getRotation().z / 57.3f;
		endPos.add(new Vector3f(-w * (float)Math.sin(rotation + 1.57),-w * (float)Math.cos(rotation + 1.57),0));
		return endPos;
	}
	
	public void changeMove(String move) {
		if(curMove == move)
			return;
		curMove = move;
		curAnimPos = 0;
		
		anim = stances.getStickAnims(partName, curMove, enemy);
		animTime = stances.getAnimLength(partName, curMove, enemy);
		finishedSequence = true;
	}
	
	public void rotPart(float rot) {
		gameItem.setRotation(0, 0, rot);
	}

}
