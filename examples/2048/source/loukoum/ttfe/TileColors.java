package loukoum.ttfe;

import java.awt.Color;

public class TileColors {

	private static final Color DEFAULT = Color.lightGray;
	private static final Color TWO = Color.yellow;
	private static final Color FOUR = Color.GREEN;
	private static final Color EIGHT = Color.CYAN;
	private static final Color SIXTEEN = Color.yellow.darker();
	private static final Color THIRTY_TWO = Color.GREEN.darker();
	private static final Color SIXTY_FOUR = Color.BLUE.brighter();
	private static final Color HUNDRED28 = Color.pink;
	private static final Color TWO_HUNDRED56 = Color.orange;
	private static final Color FIVE_HUNDRED12 = Color.MAGENTA;
	private static final Color THOUSAND24 = Color.red;
	private static final Color TWO_THOUSAND48 = Color.gray;
	
	public static Color getTileColor(int n){
		switch (n){
		case (2):
			return TWO;
		case (4):
			return FOUR;
		case (8):
			return EIGHT;
		case (16):
			return SIXTEEN;
		case (32):
			return THIRTY_TWO;
		case (64):
			return SIXTY_FOUR;
		case (128):
			return HUNDRED28;
		case (256):
			return TWO_HUNDRED56;
		case (512):
			return FIVE_HUNDRED12;
		case (1024):
			return THOUSAND24;
		case (2048):
			return TWO_THOUSAND48;
		default:
			return DEFAULT;
		}
	}
	
}
