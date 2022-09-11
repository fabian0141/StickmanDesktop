package stickman.logic;

import stickman.render.Window;

public class Controlls {

	private boolean bTop1, bRight1, bBottom1, bLeft1, bShoot1;
	private boolean bTop2, bRight2, bBottom2, bLeft2, bShoot2;

	private float rot1, rot2;

	public Window window;

	public Controlls(Window window) {
		this.window = window;
	}

	public void setControlls(String[] controllText) {
		if (controllText[0] != null) {
			String[] controll = controllText[0].split(" ");

			bTop1 = Boolean.parseBoolean(controll[0]);
			bRight1 = Boolean.parseBoolean(controll[1]);
			bBottom1 = Boolean.parseBoolean(controll[2]);
			bLeft1 = Boolean.parseBoolean(controll[3]);
			bShoot1 = Boolean.parseBoolean(controll[4]);

			rot1 = Float.parseFloat(controll[5]);
		}
		
		if (controllText[1] != null) {
			String[] controll = controllText[1].split(" ");

			bTop2 = Boolean.parseBoolean(controll[0]);
			bRight2 = Boolean.parseBoolean(controll[1]);
			bBottom2 = Boolean.parseBoolean(controll[2]);
			bLeft2 = Boolean.parseBoolean(controll[3]);
			bShoot2 = Boolean.parseBoolean(controll[4]);

			rot2 = Float.parseFloat(controll[5]);
		}
	}

	public boolean isTop(boolean firstPlayer) {
		return firstPlayer ? bTop1 : bTop2;
	}

	public boolean isRight(boolean firstPlayer) {
		return firstPlayer ? bRight1 : bRight2;
	}

	public boolean isBottom(boolean firstPlayer) {
		return firstPlayer ? bBottom1 : bBottom2;
	}

	public boolean isLeft(boolean firstPlayer) {
		return firstPlayer ? bLeft1 : bLeft2;
	}

	public boolean isShoot(boolean firstPlayer) {
		return firstPlayer ? bShoot1 : bShoot2;
	}

	public float getRot(boolean firstPlayer) {
		return firstPlayer ? rot1 : rot2;
	}
}
