package utils;

import org.opencv.core.Point;

public class TouchPoint extends Point {
	private int size;

	/**
	 * Erstellt einen Touchpunkt aus einem vorhandenen Punkt
	 * @param p Bereits vorhandener Punkt
	 * @param size Größe des Punktes
	 */
	public TouchPoint(Point p, int size) {
		super(p.x, p.y);
		this.size = size;
	}
	
	/**
	 * Erstellt einen Touchpunkt aus den Koordinaten
	 * @param x X-Koordinate des Punktes
	 * @param y Y-Koordinate des Punktes
	 * @param size Größe des Punktes
	 */
	public TouchPoint(int x, int y, int size) {
		super(x,y);
		this.size = size;
	}

	/**
	 * Gibt die Größe des Touchpunktes zurück
	 * @return Größe des Touchpunktes
	 */
	public int getSize() {
		return size;
	}
}
