package stickman;

import stickman.engine.GameEngine;
import stickman.logic.IGameLogic;
import stickman.logic.StickmanGame;

public class Main {

	public static void main(String[] args) {

		boolean vSync = true;
		IGameLogic gameLogic = new StickmanGame();
		GameEngine gameEng = new GameEngine("Stickman Revolution", 1400, 700, vSync, gameLogic);
		gameEng.start();

	}

}
