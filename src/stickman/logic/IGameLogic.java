package stickman.logic;

import stickman.render.ShaderProgram;
import stickman.render.Window;

public interface IGameLogic {

    void init(Window window, ShaderProgram[] programs) throws Exception;
    
    void input(Window window);

    void update(float interval, float time);
    
    void render(Window window);
    
    void cleanup();
}