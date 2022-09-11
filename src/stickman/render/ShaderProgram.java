package stickman.render;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;
import org.joml.Matrix4f;
import static org.lwjgl.opengl.GL20.*;
import org.lwjgl.system.MemoryStack;

public class ShaderProgram {

    public int programId;

    public int vertexShaderId;

    public int fragmentShaderId;

    public Map<String, Integer> uniforms;

    public ShaderProgram() {
        
    }
    
    public void createShader(){
    	programId = glCreateProgram();
        uniforms = new HashMap<>();
    }

    public void createUniform(String uniformName) {
        int uniformLocation = glGetUniformLocation(programId, uniformName);
        uniforms.put(uniformName, uniformLocation);
    }

    public void setUniform(String uniformName, Matrix4f value) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer fb = stack.mallocFloat(16);
            value.get(fb);
            glUniformMatrix4fv(uniforms.get(uniformName), false, fb);
        }
    }

    public void setUniform(String uniformName, boolean value) {
        glUniform1i(uniforms.get(uniformName), value ? 1 : 0);
    }
    
    public void setUniform(String uniformName, int value) {
        glUniform1i(uniforms.get(uniformName), value);
    }
    
    public void setUniform(String uniformName, float value) {
        glUniform1f(uniforms.get(uniformName), value);
    }
    
	public void setUniform(String uniformName, float val1, float val2) {
        glUniform2f(uniforms.get(uniformName), val1, val2);
	}
	
	public void setUniform(String uniformName, float x, float y , float z) {
        glUniform3f(uniforms.get(uniformName), x,y,z);
	}

    public void createVertexShader(String shaderCode) {
        vertexShaderId = createShader(shaderCode, GL_VERTEX_SHADER);
    }

    public void createFragmentShader(String shaderCode) {
        fragmentShaderId = createShader(shaderCode, GL_FRAGMENT_SHADER);
    }

    protected int createShader(String shaderCode, int shaderType) {
        int shaderId = glCreateShader(shaderType);

        glShaderSource(shaderId, shaderCode);
        glCompileShader(shaderId);
        glAttachShader(programId, shaderId);

        return shaderId;
    }

    public void link(){
        glLinkProgram(programId);

        if (vertexShaderId != 0) {
            glDetachShader(programId, vertexShaderId);
        }
        if (fragmentShaderId != 0) {
            glDetachShader(programId, fragmentShaderId);
        }

        glValidateProgram(programId);
    }

    public void bind() {
        glUseProgram(programId);
    }

    public void unbind() {
        glUseProgram(0);
    }

    public void cleanup() {
        unbind();
        if (programId != 0) {
            glDeleteProgram(programId);
        }
    }
}
