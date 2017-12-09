package states;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;

import org.opencv.core.Point;

import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.util.awt.Overlay;

import managers.CalibrationManager;
import managers.MatrixManager;
import managers.StateManager;
import managers.TouchDetector;
import utils.State;
import utils.TouchPoint;

public class CalibrationState implements State {

	/* War ein Versuch, aber das Overlay funktioniert aufgrund einer
	 * GLException nicht. Aber das Billardspiel funktioniert auch ohne eine exakte Kalibrierung,
	 * die wäre nur bei z.B. einem Malprogramm von Nöten gewesen.
	 */
	
	GL4 gl;
	
	Overlay overlay;
	Graphics2D oGraphics;	
	CalibCircle[] calibCircles = new CalibCircle[4];
	
	TouchDetector td;
	StateManager sm;
	MatrixManager mm;
	CalibrationManager cm;
	
	ArrayList<TouchPoint> touchPoints;
	TouchPoint[] camPoints = new TouchPoint[4];
	
	float delay = 1000;
	
	/**
	 * Initialisiert die Grafiken zur Kalibrierung
	 * sowie das AWT Overlay
	 */
	@Override
	public void init(GLAutoDrawable drawable) {
		gl = drawable.getGL().getGL4();
		td = TouchDetector.getInstance();
		sm = StateManager.getInstance();
		mm = MatrixManager.getInstance();
		cm = CalibrationManager.getInstance();
		
		//Erstellt ein Overlay über dem Canvas um 2D Zeichnung zu vereinfachen
		overlay = new Overlay(drawable);
		oGraphics = overlay.createGraphics();

		System.out.println("blubb");
		
		//Erstelle Kreise
		calibCircles[0] = new CalibCircle(50, 50, 50, 50);
		calibCircles[1] = new CalibCircle(cm.getScreenWidth()-50, 50, 50, 50);
		calibCircles[2] = new CalibCircle(50, cm.getScreenHeight()-50, 50, 50);
		calibCircles[3] = new CalibCircle(cm.getScreenWidth()-50, cm.getScreenHeight()-50, 50, 50);
	}

	/**
	 * Updatezyklus des Kalibrierungsstates
	 */
	@Override
	public void update(float lastMillis) {
		touchPoints = td.getTouchPoints();
		
		// Wenn Touchpunkte gefunden wurden,
		// dann rufe die Update Methode der Kreise auf
		if(touchPoints.size() > 0) {
			TouchPoint tp = touchPoints.get(0);
			
			int i = 0;
			for(CalibCircle circle : calibCircles) {
				circle.update(lastMillis, tp);
				if(circle.isCalibrated())
					camPoints[i] = tp;
				i++;
			}
		}
		
		//Überprüfe ob alle Kreise kalibriert wurden
		boolean completeCheck = true;
		for(int i = 0; i < 4; i++) {
			if(camPoints[i] == null) {
				completeCheck = false;
				break;
			}
		}
		
		//Wenn alle kalibriert sind, dann stelle die Punktepaarlisten für den
		//Calibration Manager auf, übergebe diese und wechsel auf den Billard State
		if(completeCheck) {
			ArrayList<Point> camPointList = new ArrayList<Point>();
			ArrayList<Point> screenPointList = new ArrayList<Point>();
			
			for(int i = 0; i < 4; i++) {
				camPointList.add(camPoints[i]);
				screenPointList.add(new Point(calibCircles[i].getX(),calibCircles[i].getY()));
			}
			cm.setCalibrationPoints(camPointList, screenPointList);
			sm.setActiveState(1);
		}
	}

	/**
	 * Rendere alle Kalibrierungskreise
	 */
	@Override
	public void render(GLAutoDrawable drawable) {
		for(CalibCircle circle : calibCircles) {
			//oGraphics.setColor(circle.getColor());
			//System.out.println(oGraphics);
			//oGraphics.draw(circle);
		}
	}

	@Override
	public Integer getId() {
		return 0;
	}
	
	
	
	private class CalibCircle extends Ellipse2D.Float {
		private float currentTime = 0;
		private boolean calibrated;
		
		/** 
		 * Erstellt einen Kalibrierungskreis
		 * @param x X-Position des Kreises
		 * @param y Y-Position des Kreises
		 * @param w Breite des Kreises
		 * @param h Höhe des Kreises
		 */
		public CalibCircle(int x, int y, int w, int h) {
			super(x,y,w,h);
		}

		/**
		 * Updatezyklus des Kreises,
		 * verrechnet die Framezeit und überprüft den Fortschritt der
		 * Kalibrierung
		 * @param delta Framezeit
		 * @param tp Übergabe des Touchpunktes
		 */
		public void update(float delta, TouchPoint tp) {
			if(currentTime < delay && !calibrated) {
				if(contains(tp.x, tp.y)) {
					currentTime += delta;
				} else {
					currentTime = 0;
				}
			} else {
				calibrated = true;
				
			}
		}
		
		/**
		 * Gibt eine Farbe zurück, welche den Fortschritt wiederspiegelt
		 * @return Farbe
		 */
		public Color getColor() {
			int r = 0;
			int g = Math.min((int) ((currentTime/delay)*255),255);
			int b = Math.min((int) (1-((currentTime/delay)*255)),255);
			return new Color(r,g,b);
		}
		
		/**
		 * Gibt zurück, ob der Kreis kalibriert ist
		 * @return Boolean ob der Kreis kalibriert ist
		 */
		public boolean isCalibrated() {
			return calibrated;
		}
	}

}
