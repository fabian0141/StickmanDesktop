package stickman.render;

import org.joml.Matrix4f;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;

import static org.lwjgl.openal.ALC10.alcCloseDevice;
import static org.lwjgl.openal.ALC10.alcCreateContext;
import static org.lwjgl.openal.ALC10.alcDestroyContext;
import static org.lwjgl.openal.ALC10.alcMakeContextCurrent;
import static org.lwjgl.openal.ALC10.alcOpenDevice;
import static org.lwjgl.openal.EXTThreadLocalContext.alcSetThreadContext;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import stickman.engine.Sound;
import stickman.engine.Transformation;
import stickman.landscape.Sky;
import stickman.level.Level;
import stickman.render.ShaderProgram;
import stickman.render.Window;

public class Renderer {

    /**
     * Field of View in Radians
     */
    private static final float FOV = (float) Math.toRadians(60.0f);

    private static final float Z_NEAR = 0.01f;

    private static final float Z_FAR = 1000.f;

    private final Transformation transformation;

    private ShaderProgram shaderProgram;
    private Sky shaderSky;
    
    private long device;
    private long context;

	private Sound sound;


    public Renderer(Sound sound) {
        transformation = new Transformation();
        this.sound = sound;
    }

    public void init(Window window) throws Exception {
    	device = alcOpenDevice((ByteBuffer) null);
		ALCCapabilities deviceCaps = ALC.createCapabilities(device);

		context = alcCreateContext(device, (IntBuffer) null);
		alcSetThreadContext(context);
		AL.createCapabilities(deviceCaps);
		
    	shaderSky = new Sky(sound);
    }


    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void render(Window window, Level curLevel) {    
        clear();
        int width = window.getWidth();
        int height = window.getHeight();
        
        if ( window.isResized() ) {
            glViewport(0, 0, width, height);
            window.setResized(false);
        }
        
        Matrix4f projectionMatrix = transformation.getProjectionMatrix(FOV, window.getWidth(), window.getHeight(), Z_NEAR, Z_FAR);

        shaderSky.render(projectionMatrix, transformation, width, height);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        curLevel.render(projectionMatrix, transformation, width, height);
        glDisable(GL_ALPHA);
        glDisable(GL_BLEND);
    }

    public void cleanup() {
        if (shaderProgram != null) {
            shaderProgram.cleanup();
        }
        alcMakeContextCurrent(NULL);
		alcDestroyContext(context);
		alcCloseDevice(device);
    }

	public void update(float time) {
		shaderSky.update(time);
	}
}
