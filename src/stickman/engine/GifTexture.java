package stickman.engine;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import stickman.landscape.GameObject;
import stickman.level.Level;
import stickman.render.ShaderProgram;
import stickman.render.Utils;

public class GifTexture extends GameObject{

	private int x = 0,y = 0;
	private int rows,columns;
	private int changeImg = 0;

	public GifTexture(String path, int rows, int columns, ShaderProgram program) throws Exception {
		fragmentShaderId = program.fragmentShaderId;
		vertexShaderId = program.vertexShaderId;
		programId = program.programId;
		uniforms = program.uniforms;
		
		texture = new Texture(path);
		this.rows = rows;
		this.columns = columns;
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
	
	public GifTexture(GameItem gameItem, int rows, int columns, Texture texture, Collider col){		
		this.texture = texture;
		init();
		this.gameItem.setPosition(new Vector3f(gameItem.getPosition()));
		this.gameItem.setRotation(new Vector3f(gameItem.getRotation()));
		this.gameItem.setScale(new Vector3f(gameItem.getScale()));
		this.rows = rows;
		this.columns = columns;
		
		if(col != null)
			this.col = new Collider(col.getSize(),col.getSolid());
	}

	@Override
	public void render(Matrix4f projectionMatrix, Transformation transformation, int width, int height) {
		bind();
		setUniform("projectionMatrix", projectionMatrix);
		Matrix4f worldMatrix = transformation.getWorldMatrix(gameItem.getPosition(), gameItem.getRotation(),
				gameItem.getScale());
		setUniform("worldMatrix", worldMatrix);
		setUniform("texture_sampler", 0);
		setUniform("rowColAmount", columns, rows);
		setUniform("rowColPos", x, y);		
		setUniform("flip", flip);	
		flip = 0;

		changeImg++;
		if(changeImg == 5) {
			changeImg = 0;
			
			x++;
			if(x == columns) {
				x = 0;
				y = ++y % rows;
			}
		}

		
		gameItem.getMesh().render();
		unbind();
		
	}
	
	public GameObject newGObj(float x, float y, int i) {
		GifTexture go = new GifTexture(gameItem, rows, columns,texture,col);
		float z = 0;
		switch(i) {
			case -1: z = -900f;		break;
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
}