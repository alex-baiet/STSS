package fr.silex.test;

/**
 * Permet de gérer plus facilement les positions des objets sur l'écran.
 * (Peu utilisé)
 */
public class Vector2 {
	private int x;
	private int y;

	public Vector2(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public void move(int dx, int dy) {
		x += dx;
		y += dy;
	}

	public int x() {
		return x;
	}
	
	public int y() {
		return y;
	}
}
