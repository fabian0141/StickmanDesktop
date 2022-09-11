package stickman.landscape;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import stickman.engine.GameItem;
import stickman.engine.Mesh;
import stickman.engine.Sound;
import stickman.engine.Transformation;
import stickman.render.ShaderProgram;
import stickman.render.Utils;

public class Sky extends ShaderProgram {

	private GameItem gameItem;
	private float time;
	private final Sound sound;

	public Sky(Sound sound) throws Exception {
		createShader();
		this.sound = sound;
		createVertexShader(Utils.loadResource("/shaders/vertex.vs"));
		createFragmentShader(Utils.loadResource("/shaders/fSky.fs"));
		link();

		createUniform("projectionMatrix");
		createUniform("worldMatrix");
		createUniform("iResolution");
		createUniform("iTime");
		
		float[] positions = new float[] {
				-1.0f, 1.0f, 0.f,
				-1.0f, -1.0f, 0.f,
				1.0f, -1.0f, 0.f,
				1.0f, 1.0f, 0.f,
				-1.0f, 1.0f, 0.f,
				1.0f, 1.0f, 0.f,
				-1.0f, -1.0f, 0.f,
				1.0f, -1.0f, 0.f };

		int[] indices = new int[] {
				0, 1, 3, 3, 1, 2 };

        Mesh mesh = new Mesh(positions, indices);
		gameItem = new GameItem(mesh);
		gameItem.setPosition(0, 0, -999);
	}

	public void render(Matrix4f projectionMatrix, Transformation transformation, float width, float height) {
		bind();
		
		gameItem.setScale(new Vector3f(width / height,1f,1));
		
		setUniform("projectionMatrix", projectionMatrix);
		Matrix4f worldMatrix = transformation.getWorldMatrix(gameItem.getPosition(), gameItem.getRotation(),
				gameItem.getScale());
		setUniform("worldMatrix", worldMatrix);
		setUniform("iResolution", width, height);
		setUniform("iTime", time);
		
		gameItem.getMesh().render();
		unbind();
		
		if((int)(Math.random() * 2000) == 1) {
			sound.playSound("/sounds/thunder.ogg", (float)Math.random() / 2 + 0.5f,21);
		}
		
		if((int)(Math.random() * 4000) == 1) {
			sound.playSound("/sounds/thunder2.ogg", (float)Math.random() / 2 + 0.5f,86);
		}
		
		if((int)(Math.random() * 1000) == 1) {
			sound.playSound("/sounds/thunder3.ogg", (float)Math.random() / 2 + 0.5f,25);
		}
		
		if((int)(Math.random() * 2000) == 1) {
			sound.playSound("/sounds/thunder4.ogg", (float)Math.random() / 2 + 0.5f,27);
		}
	}

	public void update(float time) {
		this.time = time;
	}
}
