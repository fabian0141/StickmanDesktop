package stickman.logic;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ENTER;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import stickman.engine.Sound;
import stickman.figure.AllWeapons;
import stickman.figure.Stances;
import stickman.landscape.GameObject;
import stickman.landscape.ObjectList;
import stickman.level.Level;
import stickman.level.Level1;
import stickman.level.Level2;
import stickman.level.Level3;
import stickman.level.StartScreen;
import stickman.render.Renderer;
import stickman.render.ShaderProgram;
import stickman.render.Window;

public class StickmanGame implements IGameLogic {

	private final Renderer renderer;

	private Level levels[];
	private Level curLevel;
	private int level = 0;
	private Sound sound = new Sound();

	private boolean nextLevel;
	private ServerSocket server;
	private String[] controllText = new String[2];
	private Controlls controlls;

	private List<GameObject> gameObjects = new ArrayList<>();

	public StickmanGame() {
		renderer = new Renderer(sound);
		connect();
	}

	@Override
	public void init(Window window, ShaderProgram[] programs) throws Exception {
		ObjectList objList = new ObjectList(programs);
		Stances stances = new Stances();
		AllWeapons allWeapon = new AllWeapons(programs[2]);
		renderer.init(window);
		levels = new Level[] { 	new StartScreen(objList, stances, sound, allWeapon,programs),
				 				new Level1(objList, stances, sound, allWeapon,programs),
				 				new Level2(objList, stances, sound, allWeapon,programs), 
				 				new Level3(objList, stances, sound, allWeapon,programs)}; 
		controlls = new Controlls(window);
		curLevel = levels[0];
		sound.playMusic("/sounds/maintheme.ogg",1);
	}

	@Override
	public void input(Window window) {

		if (window.isKeyDown(GLFW_KEY_ENTER) || nextLevel) {
			level = ++level % 4;
			curLevel = levels[level];
			nextLevel = false;
			
			switch(level) {
			case 0:
				sound.playMusic("/sounds/maintheme.ogg",1);
				break;
			case 1:
				sound.playSound("/sounds/roundone.ogg", 1,3);
				sound.playMusic("/sounds/sound1.ogg",0.6f);
				break;
			case 2:
				sound.playSound("/sounds/roundtwo.ogg", 1,3);
				sound.playMusic("/sounds/sound2.ogg",0.6f);
				break;
			case 3:
				sound.playSound("/sounds/finalround.ogg", 1,2);
				sound.playMusic("/sounds/sound3.ogg",0.6f);
				break;
			}
		}
		controlls.setControlls(controllText);
		curLevel.input(controlls);
	}

	@Override
	public void update(float interval, float time) {
		renderer.update(time);
		curLevel.update(time);
		sound.update(time);
		nextLevel = curLevel.nextLevel();
	}

	@Override
	public void render(Window window) {
		renderer.render(window, curLevel);
	}

	@Override
	public void cleanup() {
		renderer.cleanup();
		for (GameObject gameObject : gameObjects) {
			gameObject.getGameItem().getMesh().cleanUp();
		}
		curLevel.cleanup();
		try {
			server.close();
			if(socket != null)
				socket.close();
			if(socket2 != null)
				socket2.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private Socket socket;
	private Socket socket2;

	private void connect() {
		try {
			server = new ServerSocket(34568);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				
				socket = null;
				socket2 = null;
				
				try {
					socket = server.accept();
					receive();					
					socket2 = server.accept();
					receive();	
				} catch (IOException e1) {
					
				}				
			}
		}).start();

	}

	private void receive(){
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {				
					boolean firstSocket = socket2 == null;
					BufferedReader rein = new BufferedReader(new InputStreamReader(firstSocket ? socket.getInputStream() : socket2.getInputStream()));
					PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(firstSocket ? socket.getOutputStream() : socket2.getOutputStream())),true);
					while (true) {
						if(rein.ready()) {
							controllText[firstSocket ? 0 : 1] = rein.readLine();
							out.println("");
							
							
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();

	}
}