package stickman.engine;

import stickman.logic.IGameLogic;
import stickman.render.ShaderProgram;
import stickman.render.Utils;
import stickman.render.Window;

public class GameEngine implements Runnable {

    public static final int TARGET_FPS = 60;

    public static final int TARGET_UPS = 60;

    private final Window window;

    private final Thread gameLoopThread;

    private final Timer timer;

    private final IGameLogic gameLogic;
    
    private String vShader;
    private String fHead;
    private String fPart;
    private String fObj;
    private String fGif;
    private String fBul;
    
    private ShaderProgram[] programs = new ShaderProgram[5];


    public GameEngine(String windowTitle, int width, int height, boolean vSync, IGameLogic gameLogic){
        gameLoopThread = new Thread(this, "GAME_LOOP_THREAD");
        window = new Window(windowTitle, width, height, vSync);
        this.gameLogic = gameLogic;
        timer = new Timer();
    }

    public void start() {
        String osName = System.getProperty("os.name");
        if ( osName.contains("Mac") ) {
            gameLoopThread.run();
        } else {
            gameLoopThread.start();
        }
    }

    @Override
    public void run() {
        try {
            init();
            gameLoop();
        } catch (Exception excp) {
            excp.printStackTrace();
        } finally {
            cleanup();
        }
    }

    protected void init() throws Exception {
    	vShader = Utils.loadResource("/shaders/vertex.vs");
    	fHead = Utils.loadResource("/shaders/fHead.fs");
    	fPart =  Utils.loadResource("/shaders/fStick.fs");
    	fObj =  Utils.loadResource("/shaders/fObj.fs");
    	fGif =  Utils.loadResource("/shaders/fGif.fs");
    	fBul =  Utils.loadResource("/shaders/fBullet.fs");
    	
    	

        window.init();
        
        //GLUtil.setupDebugMessageCallback();
        createBodyShader();
        createBulletShader();
        createObjectShader();
        createGifShader();
        
        timer.init();
        gameLogic.init(window,programs);
    }
    
    private void createGifShader() {
    	programs[3] = new ShaderProgram();
    	programs[3].createShader();
    	programs[3].createVertexShader(vShader);
    	programs[3].createFragmentShader(fGif);
    	programs[3].link();
    	programs[3].createUniform("projectionMatrix");
    	programs[3].createUniform("worldMatrix");
    	programs[3].createUniform("texture_sampler");
    	programs[3].createUniform("rowColAmount");
    	programs[3].createUniform("rowColPos");	
    	programs[3].createUniform("flip");
	}

	private void createObjectShader() {
    	programs[2] = new ShaderProgram();
    	programs[2].createShader();
    	programs[2].createVertexShader(vShader);
    	programs[2].createFragmentShader(fObj);
    	programs[2].link();
    	programs[2].createUniform("projectionMatrix");
    	programs[2].createUniform("worldMatrix");
    	programs[2].createUniform("texture_sampler");
    	programs[2].createUniform("flip");
	}

	private void createBulletShader() {
    	programs[4] = new ShaderProgram();
    	programs[4].createShader();
    	programs[4].createVertexShader(vShader);
    	programs[4].createFragmentShader(fBul);
    	programs[4].link();
    	programs[4].createUniform("projectionMatrix");
    	programs[4].createUniform("worldMatrix");
    	programs[4].createUniform("texture_sampler");
    	programs[4].createUniform("flames");
	}

	private void createBodyShader(){
		programs[0] = new ShaderProgram();
		programs[0].createShader();
		programs[0].createVertexShader(vShader);
		programs[0].createFragmentShader(fHead);
		programs[0].link();    	
		programs[0].createUniform("projectionMatrix");
    	programs[0].createUniform("worldMatrix");	
    	programs[0].createUniform("texture_sampler");
    	programs[0].createUniform("col");
    	
    	programs[1] = new ShaderProgram();
    	programs[1].createShader();
    	programs[1].createVertexShader(vShader);
    	programs[1].createFragmentShader(fPart);
    	programs[1].link();
    	programs[1].createUniform("projectionMatrix");
    	programs[1].createUniform("worldMatrix");
    	programs[1].createUniform("texture_sampler");
    	programs[1].createUniform("col");
    }

    protected void gameLoop() {
        float elapsedTime;
        float interval = 1f / 1;
        
        float time = 0;

        boolean running = true;
        while (running && !window.windowShouldClose()) {
        	
        	elapsedTime = timer.getElapsedTime();

        	time += elapsedTime;
            input();
            update(interval,time);  
            render();
            
            if ( !window.isvSync() ) {
                sync();
            }
        }
    }

    protected void cleanup() {
        gameLogic.cleanup();                
    }
    
    private void sync() {
        float loopSlot = 1f / TARGET_FPS;
        double endTime = timer.getLastLoopTime() + loopSlot;
        while (timer.getTime() < endTime) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException ie) {
            }
        }
    }

    protected void input() {
        gameLogic.input(window);
    }

    protected void update(float interval, float time) {
        gameLogic.update(interval,time);
    }

    protected void render() {
        gameLogic.render(window);
        window.update();
    }
}
