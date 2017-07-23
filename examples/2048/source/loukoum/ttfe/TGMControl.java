package loukoum.ttfe;

import loukoum.tgm.parts.Control;

public class TGMControl extends Control {
	
	private TTFE game;
	
	public TGMControl(TTFE game) {
		this.game = game;
	}

	protected void startGame() {
		game.startGame();
	}

	public float getScore() {
		return game.getPoints();
	}

}
