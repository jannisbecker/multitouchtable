package managers;

import java.util.ArrayList;
import java.util.HashMap;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;


import utils.TouchPoint;

public class CalibrationManager {
	private static CalibrationManager instance;
	public static CalibrationManager getInstance() {
		if(instance == null) {
			instance = new CalibrationManager();
		}
		return instance;
	}
	
	/**
	 * Erstellt einen Calibration Manager und fügt
	 * eine Standardkalibierung ein
	 */
	private CalibrationManager() {	
		/* Test */
		ArrayList<Point> camPoints = new ArrayList<Point>();
		camPoints.add(new Point(0,0));
		camPoints.add(new Point(640,0));
		camPoints.add(new Point(0,480));
		camPoints.add(new Point(640,480));
		
		ArrayList<Point> screenPoints = new ArrayList<Point>();
		screenPoints.add(new Point(0,0));
		screenPoints.add(new Point(1280,0));
		screenPoints.add(new Point(0,1024));
		screenPoints.add(new Point(1280,1024));
		
		this.setCalibrationPoints(camPoints, screenPoints);
	}

	private Mat camPointsMat;
	private Mat screenPointsMat;
	private Mat camToScreenMat;
	
	private int resX, resY;
	
	/**
	 * Ersetzt die aktuellen Kalibierungspunkte durch die angegebenen.
	 * @param camPoints Eine 4-Eintrags Point-Arraylist der Kamerakoordinatenpunkte
	 * @param screenPoints Eine 4-Eintrags Point-Arraylist der Bildschirmkoordinatenpunkte
	 */
	public void setCalibrationPoints(ArrayList<Point> camPoints, ArrayList<Point> screenPoints) {
		if(camPoints.size() >= 4 && screenPoints.size() >= 4) {
			camPointsMat = Converters.vector_Point2f_to_Mat(camPoints);
			screenPointsMat = Converters.vector_Point2f_to_Mat(screenPoints);
			
			calculateTransformMatrix();
		}
	}
	
	/**
	 * Errechnet die Bildschirmkoordinaten jedes Punktes in der angegebenen Liste
	 * @param touchPoints Die Liste der umzurechnenden Punkte
	 * @return Die aktualisierte Liste
	 */
	public ArrayList<TouchPoint> translateToScreenCoords(ArrayList<TouchPoint> touchPoints) {
		/* Wenn es Touchpunkte gibt */
		if(touchPoints.size() > 0) {
			ArrayList<TouchPoint> newTouchPoints = new ArrayList<TouchPoint>();
			
			/* Konvertiere Touchpunkte in Matrix */
			Mat touchPointsMat = Converters.vector_Point2f_to_Mat(new ArrayList<Point>(touchPoints));
			Mat newTouchPointsMat = new Mat();
			
			/* Transformiere Touchpunkte in Bildschirmkoordinaten */
			Core.perspectiveTransform(touchPointsMat, newTouchPointsMat, camToScreenMat);
			
			ArrayList<Point> newPoints = new ArrayList<Point>();
			
			/* Konvertiere Matrix zurück in Punkte */
			Converters.Mat_to_vector_Point2f(newTouchPointsMat, newPoints);
			
			/* Erstelle neue Touchpunkte an neuen Positionen mit alter Größeninformation */
			for(int i = 0; i < newPoints.size(); i++) {
				newTouchPoints.add(new TouchPoint(newPoints.get(i),touchPoints.get(i).getSize()));
			}
			//System.out.println(",Neu: "+newTouchPoints.get(0).x+","+newTouchPoints.get(0).y);
			return newTouchPoints;
		}
		return touchPoints;
	}
	
	/**
	 * Berechnet die Transformationsmatrix zur Überführung in Bildschirmkoordinaten neu
	 */
	private void calculateTransformMatrix() {	
		camToScreenMat = Imgproc.getPerspectiveTransform(camPointsMat, screenPointsMat);
	}
	
	/**
	 * Setzt die Bildschirmauflösung, diese Info wird von anderen Klassen verwendet
	 * @param x Neue Auflösungsbreite
	 * @param y Neue Auflösungshöhe
	 */
	public void setResolution(int x, int y) {
		resX = x;
		resY = y;
	}
	
	/**
	 * Gibt die Breite der Auflösung zurück
	 * @return Die Breite der Auflösung
	 */
	public int getScreenWidth() {
		return resX;
	}
	
	/**
	 * Gibt die Höhe der Auflösung zurück
	 * @return Die Höhe der Auflösung
	 */
	public int getScreenHeight() {
		return resY;
	}
}
