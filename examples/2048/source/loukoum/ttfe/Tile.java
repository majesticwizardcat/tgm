package loukoum.ttfe;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

public class Tile {
	
	public static final Color BACKGROUND = new Color(13, 133, 233);

	private int number;

	private float animation;

	public Tile(int n) {
		number = n;
		animation = 0;
	}
	
	public void restartAnimation() {
		animation = 0;
	}

	public void render(int x, int y, int width, int height, Graphics g) {
		
		g.setColor(BACKGROUND);
		g.fillRect(x, y, width, height);

		int animationWidth = (int) (width * animation * 0.95f);
		int animationHeight = (int) (height * animation * 0.95f);

		g.setColor(TileColors.getTileColor(number));
		g.fillRect(x + (width - animationWidth) / 2, 
				y + (height - animationHeight) / 2, 
				animationWidth, animationHeight);

		if (number == 0) {
			return;
		}

		g.setColor(Color.black);
		g.setFont(new Font("TimesNewRoman", Font.PLAIN,
				(3 * animationWidth / 4) - ("" + number).length()
						* (3 * animationWidth / 4) / 10));

		int textWidth = g.getFontMetrics().stringWidth("" + number);
		int textHeight = g.getFontMetrics().getHeight();

		g.drawString("" + number, x + (width - textWidth) / 2, y
				+ (height + textHeight / 2) / 2);
	}

	public void update(float delta) {
		if (animation < 1) {
			animation += delta * 3f;
		}
		
		if (animation > 1) {
			animation = 1;
		}
	}

	public boolean equals(Tile other) {
		return number == other.number;
	}

	public int getValue() {
		return number;
	}
	
	public void setValue(int value) {
		this.number = value;
	}

}
