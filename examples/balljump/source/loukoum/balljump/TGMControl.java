package loukoum.balljump;

import loukoum.tgm.parts.Control;

public class TGMControl extends Control {

	private World world;

	public TGMControl(World world) {
		this.world = world;
	}

	public void startGame() {
		world.startGame();
	}

	public float getScore() {
		return world.getScore();
	}
}
