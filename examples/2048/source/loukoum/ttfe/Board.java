package loukoum.ttfe;

import java.awt.Graphics;
import java.util.Random;

public class Board {
	
	public static final int BOARD_SIZE = Renderer.WIDTH;
	public static final int TILE_SIZE = BOARD_SIZE / 4;

	private Random rGen = new Random();
	private Tile[][] tiles;
	private int tilesX;
	private int tilesY;

	public Board(int tilesX, int tilesY) {
		this.tilesX = tilesX;
		this.tilesY = tilesY;
		restart();
	}

	public void restart() {
		tiles = new Tile[4][4];
		for (int x = 0; x < tilesX; ++x) {
			for (int y = 0; y < tilesY; ++y) {
				tiles[y][x] = new Tile(0);
			}
		}
		setupTable();
	}
	
	public static final float MAX_VALUE = 2048f * 2f;
	
	public float[] getTGMVision() {
		float vision[] = new float[4 * 4];
		
		for (int i = 0; i < 4; ++i) {
			for (int j = 0; j < 4; ++j) {
				vision[j + 4 * i] = (float) ((float)tiles[j][i].getValue() / MAX_VALUE);
			}
		}
		
		return vision;
	}

	private void setupTable() {
		createNewTile();
		createNewTile();
	}

	private void createNewTile() {
		int x = rGen.nextInt(tilesX);
		int y = rGen.nextInt(tilesY);

		while (tiles[y][x].getValue() != 0) {
			x = rGen.nextInt(tilesX);
			y = rGen.nextInt(tilesY);
		}

		if (rGen.nextDouble() < 0.9) {
			tiles[y][x].setValue(2);
		}

		else {
			tiles[y][x].setValue(4);
		}
		
		tiles[y][x].restartAnimation();
	}
	
	private void swap(Tile t0, Tile t1) {
		int v0 = t0.getValue();
		int v1 = t1.getValue();
		t0.setValue(v1);
		t1.setValue(v0);
	}
	
	private int merge(Tile from, Tile to) {
		from.setValue(0);
		to.setValue(2 * to.getValue());
		return to.getValue();
	}
	
	public int moveLeft() {
		int points = 0;
		boolean moved = false;
		
		for (int y = 0; y < tilesY; ++y) {
			for (int x = 0; x < tilesX; ++x) {

				for (int i = x + 1; i < tilesX; ++i) {
					if (tiles[y][i].getValue() != 0) {
						if (tiles[y][x].getValue() == 0) {
							swap(tiles[y][i], tiles[y][x]);
							moved = true;
							continue;
						}
						
						else if (tiles[y][x].getValue() == tiles[y][i].getValue()) {
							points += merge(tiles[y][i], tiles[y][x]);
							moved = true;
						}
						
						break;
					}
				}
			}
		}
		
		if (moved) {
			createNewTile();
		}

		return points;
	}
	
	public int moveRight() {
		int points = 0;
		boolean moved = false;
		
		for (int y = 0; y < tilesY; ++y) {
			for (int x = tilesX - 1; x >= 0; --x) {

				for (int i = x - 1; i >= 0; --i) {
					if (tiles[y][i].getValue() != 0) {
						if (tiles[y][x].getValue() == 0) {
							swap(tiles[y][i], tiles[y][x]);
							moved = true;
							continue;
						}
						
						else if (tiles[y][x].getValue() == tiles[y][i].getValue()) {
							points += merge(tiles[y][i], tiles[y][x]);
							moved = true;
						}
						
						break;
					}
				}
			}
		}
		
		if (moved) {
			createNewTile();
		}

		return points;
	}

	public int moveUp() {
		int points = 0;
		boolean moved = false;
		
		for (int y = 0; y < tilesY; ++y) {
			for (int x = 0; x < tilesX; ++x) {

				for (int i = y + 1; i < tilesY; ++i) {
					if (tiles[i][x].getValue() != 0) {
						if (tiles[y][x].getValue() == 0) {
							swap(tiles[i][x], tiles[y][x]);
							moved = true;
							continue;
						}
						
						else if (tiles[y][x].getValue() == tiles[i][x].getValue()) {
							points += merge(tiles[i][x], tiles[y][x]);
							moved = true;
						}
						
						break;
					}
				}
			}
		}
		
		if (moved) {
			createNewTile();
		}

		return points;
	}
	
	public int moveDown() {
		int points = 0;
		boolean moved = false;
		
		for (int y = tilesY - 1; y >= 0; --y) {
			for (int x = 0; x < tilesX; ++x) {

				for (int i = y - 1; i >= 0; --i) {
					if (tiles[i][x].getValue() != 0) {
						if (tiles[y][x].getValue() == 0) {
							swap(tiles[i][x], tiles[y][x]);
							moved = true;
							continue;
						}
						
						else if (tiles[y][x].getValue() == tiles[i][x].getValue()) {
							points += merge(tiles[i][x], tiles[y][x]);
							moved = true;
						}
						
						break;
					}
				}
			}
		}
		
		if (moved) {
			createNewTile();
		}

		return points;
	}
	
	private boolean canMerge(int x, int y) {
		if (tiles[y][x].getValue() == 0) {
			return false;
		}

		if (x - 1 >= 0
			&& tiles[y][x - 1].getValue() == tiles[y][x].getValue()) {
			return true;
		}

		if (x + 1 < tilesX
			&& tiles[y][x + 1].getValue() == tiles[y][x].getValue()) {
			return true;
		}

		if (y - 1 >= 0
			&& tiles[y - 1][x].getValue() == tiles[y][x].getValue()) {
			return true;
		}

		if (y + 1 < tilesY
			&& tiles[y + 1][x].getValue() == tiles[y][x].getValue()) {
			return true;
		}
		
		return false;
	}
	
	public boolean isFull() {
		for (int y = 0; y < tilesY; ++y) {
			for (int x = 0; x < tilesX; ++x) {
				if (tiles[y][x].getValue() == 0
					|| canMerge(x, y)) {
					return false;
				}
			}
		}
		
		return true;
	}

	public void update(float delta) {

		for (int y = 0; y < tilesY; ++y) {
			for (int x = 0; x < tilesX; ++x) {
				tiles[y][x].update(delta);
			}
		}
	}

	public void render(Graphics g) {
		for (int y = 0; y < tilesY; ++y) {
			for (int x = 0; x < tilesX; ++x) {
				tiles[y][x].render(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE, g);
			}
		}
	}
}
