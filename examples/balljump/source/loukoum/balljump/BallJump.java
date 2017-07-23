package loukoum.balljump;

import loukoum.tgm.TGM;

public class BallJump {
	
	public static final int TGM_TRAIN_SESSIONS = 250;
	
	public static TGM tgm;

	public BallJump() {
		World world = new World();
		TGMControl ctrl = new TGMControl(world);
		world.setControl(ctrl);

		tgm = new TGM("Balljump_TGM", World.TGM_INS, World.TGM_MOVES, ctrl, TGM_TRAIN_SESSIONS);
		new Renderer(world);
	}

	public static void main(String args[]) {
		new BallJump();
	}

}
