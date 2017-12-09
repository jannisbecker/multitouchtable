package managers;

import java.util.ArrayList;
import utils.TouchPoint;

public class TouchDetector {
	private static TouchDetector instance;
	public static TouchDetector getInstance() {
		if(instance == null) {
			instance = new TouchDetector();
		}
		return instance;
	}
	private TouchDetector() {
		touchPoints = new ArrayList<TouchPoint>();
		im = ImageManager.getInstance();
		cm = CalibrationManager.getInstance();	
	};
	
	
	private ImageManager im;
	private CalibrationManager cm;
	private ArrayList<TouchPoint> touchPoints;

	/**
	 * Gibt die aktuelle Liste der Touchpunkte zurück
	 * @return Die aktuelle Touchpunkt-Liste
	 */
	public ArrayList<TouchPoint> getTouchPoints() {
		return touchPoints;
	}
	
	/**
	 * Führt die wesentlichen Schritte zur Erkennung und
	 * Berechnung der Touchpunkte in einer bestimmten Reihenfolge aus
	 * und speichert diese in der ArrayList
	 */
	public void updateTouchPoints() {
		if(!im.captureFrame())
			return;
		im.cvtGrayscale();
		im.removeBackground();
		im.boxFilter(10);
		im.threshold(12);
		im.amplify();	
		im.binaryThreshold(40);
		touchPoints = im.calculateTouchPoints(50,3000);
		touchPoints = cm.translateToScreenCoords(touchPoints);
	}
}
