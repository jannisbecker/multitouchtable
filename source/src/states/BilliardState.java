package states;

import java.util.ArrayList;
import javax.vecmath.Vector3f;
import org.opencv.core.Point;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import geom.Kugel;
import geom.Tisch;
import managers.CalibrationManager;
import managers.MatrixManager;
import managers.ShaderManager;
import managers.StateManager;
import managers.TouchDetector;
import utils.Farbe;
import utils.State;
import utils.TouchPoint;

public class BilliardState implements State {
	
	private GL4 gl;
	
	private TouchDetector td = TouchDetector.getInstance();
	private ShaderManager shm = ShaderManager.getInstance();
	private MatrixManager mm = MatrixManager.getInstance();
	private CalibrationManager cm = CalibrationManager.getInstance();
	
	private ArrayList<TouchPoint> touchPoints = new ArrayList<TouchPoint>();
    
    private Tisch tisch;
    private Kugel weiss;
    private ArrayList<Kugel> kugeln = new ArrayList<Kugel>();
    
    private TouchPoint lastPoint;
    
    //Kugelgroesse (Radius)
    private float size = 0.03f;
    //Distanz zwischen Kugeln beim Start
    private float dist = 0.06f;
    
    //Minimale und maximaler Unterschied der Punkte zweier Frames,
    //der zum Schuss fuehrt
    private float minShotDelta = 15, maxShotDelta = 30;
    
    //Schuss Multiplier
    private float shotStrengthMuli = 2.5f;
    
    //Kamera Optionen
    private boolean cameraFixed;
    private int cameraUnfixDelay = 100, curUnfixDelay = 0;
    private Point camIdlePoint = new Point(640,512);
    
    private float 	camMultiX = 0.05f, 
    				camMultiY = 0.05f;
    
    private float 	camAngleX = 0.5f * (float)Math.PI,
			camAngleY =  0.75f * (float)Math.PI,
			camRadius = 1f;
    
    
	/* Spiel Variablen */

	//Kugel Anzahlen
	private int kugelnRot = 7, kugelnBlau = 7;

	//Aktueller Spieler
	private int aktSpieler = Farbe.ROT;
	private boolean naechsterSpieler = true;
	
	//Weisse Kugel außerhalb
	private boolean weissAus;
	
	//Falsche Kugel versenkt?
	private boolean rBlaueVersenkt;
	private boolean bRoteVersenkt;
	
	//In dieser Runde schon gestoßen?
	private boolean shot = false;
	
	//Spiel zu Ende?
	private boolean gameEnd;

    	
	/**
	 * Initialisiert die Geometrieobjekte,
	 * die Kamera und das Licht
	 */
	@Override
	public void init(GLAutoDrawable drawable) {
		gl = drawable.getGL().getGL4();
		
		//Geometrie Objekte
		tisch = new Tisch(gl, shm.SHADER_DIFFUSE, 
				0,0,0,			//Ort
				1,1,1			//Groesse
		);
		   //				 GL  Shader						Ort							 Groesse  Farbe
		   weiss = new Kugel(gl, shm.SHADER_DIFFUSE,		0.0f, size, 1f,					size, Farbe.WEISS);	
		
		kugeln.add(new Kugel(gl, shm.SHADER_DIFFUSE,		0.0f, size, 0f,					size, Farbe.ROT));
		
		kugeln.add(new Kugel(gl, shm.SHADER_DIFFUSE,		-(dist/2), size, 0f-dist,		size, Farbe.ROT));
		kugeln.add(new Kugel(gl, shm.SHADER_DIFFUSE,		(dist/2), size, 0f-dist,		size, Farbe.BLAU));
		
		kugeln.add(new Kugel(gl, shm.SHADER_DIFFUSE,		-dist, size, 0f-2*dist,			size, Farbe.BLAU));
		kugeln.add(new Kugel(gl, shm.SHADER_DIFFUSE,		0.0f, size, 0f-2*dist,			size, Farbe.SCHWARZ));
		kugeln.add(new Kugel(gl, shm.SHADER_DIFFUSE,		dist, size, 0f-2*dist,			size, Farbe.ROT));
		
		kugeln.add(new Kugel(gl, shm.SHADER_DIFFUSE,		-(3*dist/2), size, 0f-3*dist,	size, Farbe.BLAU));
		kugeln.add(new Kugel(gl, shm.SHADER_DIFFUSE,		-(dist/2), size, 0f-3*dist,		size, Farbe.ROT));
		kugeln.add(new Kugel(gl, shm.SHADER_DIFFUSE,		(dist/2), size, 0f-3*dist,		size, Farbe.BLAU));
		kugeln.add(new Kugel(gl, shm.SHADER_DIFFUSE,		(3*dist/2), size, 0f-3*dist,	size, Farbe.BLAU));
		
		kugeln.add(new Kugel(gl, shm.SHADER_DIFFUSE,		-2*dist, size, 0f-4*dist,		size, Farbe.BLAU));
		kugeln.add(new Kugel(gl, shm.SHADER_DIFFUSE,		-dist, size, 0f-4*dist,			size, Farbe.ROT));
		kugeln.add(new Kugel(gl, shm.SHADER_DIFFUSE,		0.0f, size, 0f-4*dist,			size, Farbe.BLAU));
		kugeln.add(new Kugel(gl, shm.SHADER_DIFFUSE,		dist, size, 0f-4*dist,			size, Farbe.ROT));
		kugeln.add(new Kugel(gl, shm.SHADER_DIFFUSE,		2*dist, size, 0f-4*dist,		size, Farbe.ROT));
		
		// Projektionsmatrix erstellen
		float aspect =  16 / 9;
		mm.buildProjectionMatrix((float)Math.toRadians(60), aspect, 0.1f, 100);
		
		// Lichtinformationen an Shader uebergeben
		float[] lightPosition = {0,3,0};
		float[] lightIntensity = {1f};
		
		int lightPosLoc = gl.glGetUniformLocation(shm.SHADER_DIFFUSE, "lightPos");
		gl.glUniform3fv(lightPosLoc, 1, lightPosition, 0);	
		int lightIntLoc = gl.glGetUniformLocation(shm.SHADER_DIFFUSE, "lightInt");
		gl.glUniform1fv(lightIntLoc, 1, lightIntensity, 0);
	
//		mm.cameraLookAt(
//				new float[]{0,3,0},
//				new float[]{0,0,0},
//				new float[]{1,0,0}
//		);
		
		updateCamera(true);
	}
	
	
	
	/**
	 * Haupt Update-Zyklus des Billard Spiels
	 */
	@Override
	public void update(float lastMillis) {
		float delta = lastMillis/1000;
		
		if(!gameEnd) {
			//Neueste Touchinformationen holen und alten Punkt speichern
			if(touchPoints.size() > 0) 
				lastPoint = touchPoints.get(0);
			
			touchPoints = td.getTouchPoints();
			
			//Ueberprüfe Kamera Rotation
			if(!cameraFixed) {
				if(touchPoints.size() > 0) {
					TouchPoint tp = touchPoints.get(0);
					int dx = (int) (tp.x - camIdlePoint.x);
					//int dy = (int) (tp.y - camIdlePoint.y);
					
					float nX = (float)dx / (float)cm.getScreenWidth();
					//float nY = (float)dy / (float)cm.getScreenHeight();
					
					camAngleX += nX * camMultiX;
					//camAngleY += nY * camMultiY;
					
					//Kamera updaten (Folge Weiß, wenn nicht geschossen wurde)
					updateCamera(!shot);
				}
			} else {
				//Nach Schuss für eine kurze Zeit keine Kameradrehung erlauben
				curUnfixDelay += lastMillis;
				if(curUnfixDelay >= cameraUnfixDelay) {
					cameraFixed = false;
					curUnfixDelay = 0;
				}					
			}
			
			
			//Auf nächsten Spieler wechseln
			//wenn sich die Kugeln (fast) nicht mehr bewegen
			if(shot) {
				boolean nextCheck = false;
				//Wenn sich die weisse Kugel fast nicht mehr bewegt oder gerade aus dem Feld fällt (seit mindestens dem decay der Kugel)
				if((weiss.getVelocity().length() < 0.01f || weiss.checkForDeletion(lastMillis))) {
					nextCheck = true;
					
					//Ueberpruefe, ob alle Kugeln sich ebenfalls nicht mehr bewegen
					for(Kugel k : kugeln){
						if(k.getVelocity().length() > 0.01f) {
							nextCheck = false;
							break;
						}
					}
				}
				if(nextCheck) {
					nextCheck = false;
					
					//Kugeln zum Halten bringen
					weiss.setVelocity(0,0,0);
					for(Kugel k : kugeln){
						k.setVelocity(0,0,0);
					}
					
					//Falls nötig, die weisse Kugel wieder an den Startpunkt legen
					if(weissAus) {
						weiss = new Kugel(gl,shm.SHADER_DIFFUSE,0.0f, size, 1f,size,Farbe.WEISS);	
					}
					
					//Kamera Updaten
					updateCamera(true);
					
					System.out.println("=================");
					
					//Naechsten Spieler setzen, falls nötig
					if(naechsterSpieler || weissAus) {
						weissAus = false;
						
						if(aktSpieler == Farbe.ROT) {
							aktSpieler = Farbe.BLAU;
							System.out.println("Nächster Spieler ist Blau");
						} else {
							aktSpieler = Farbe.ROT;
							System.out.println("Nächster Spieler ist Rot");
						}	
					}
					
					//Wenn in einer Runde etwas getroffen und daraufhin nichts mehr getroffen wurde,
					//soll der Spieler wieder gewechselt werden.
					naechsterSpieler = true;
					
					//Schuss beendet
					shot = false;
					lastPoint = null;
					
					rBlaueVersenkt = false;
					bRoteVersenkt = false;
					
				}
			} else {
				//Wenn noch nicht geschossen wurde, pruefe auf schnelle Wischgeste nach oben
				if(touchPoints.size() > 0 && lastPoint != null) {
					int dy = (int) (touchPoints.get(0).y - lastPoint.y);
					
					if(dy > minShotDelta) {
						//System.out.println("dy:"+dy);
						//Stärke des Schusses berechnen
						float strength = Math.min(((dy - minShotDelta) / (maxShotDelta - minShotDelta)), 1);
						//System.out.println("strength:"+strength);
						
						//Vektor in Kamera Richtung mit Länge strength*multiplier finden
						float x = strength*(float)Math.cos(camAngleX);
						float z = strength*(float)Math.sin(camAngleX);
	
						Vector3f shotVector = new Vector3f(x, 0, z);
						shotVector.scale(shotStrengthMuli);
						shotVector.negate();
						weiss.setVelocity(shotVector.x, 0, shotVector.z);
						//System.out.println("strength:"+strength+", vector length:"+shotVector.length());
						shot = true;
						
						cameraFixed = true;
					}
				} else {
					lastPoint = null;
				}
			}
		}
		
		//Pruefe auf Kollision aller farbigen Kugeln + Schwarz mit anderen Kugeln/Waenden 
		//und ueberpruefe ob sie geloescht werden muessen
		for (int i = 0; i < kugeln.size(); i++) {
			
			//Kollisionschecks
		    for (int k = i+1; k < kugeln.size(); k++) {
		        kugeln.get(i).circleCollision(kugeln.get(k));
		    }
		    kugeln.get(i).wallCollision();
		    
		    //Pruefe ob sich Kugel i im Feld befindet, wenn nicht, merke sie zum loeschen vor
		    if(!isBallInField(kugeln.get(i)) && !kugeln.get(i).isMarkedForDeletion()) {
		    	
		    	//Farbe der herausgerollten Kugel prüfen
		    	if(kugeln.get(i).getColour() == Farbe.BLAU) {
		    		//Blau
		    		kugelnBlau--;
		    		System.out.println("-> Spieler versenkte eine Blaue Kugel");
		    		if(aktSpieler == Farbe.BLAU && !bRoteVersenkt) {
		    			naechsterSpieler = false;
		    		} else {
		    			rBlaueVersenkt = true;
		    		}
		    	} else if(kugeln.get(i).getColour() == Farbe.ROT) {
		    		//Rot
		    		kugelnRot--;
		    		System.out.println("-> Spieler versenkte eine Rote Kugel");
		    		if(aktSpieler == Farbe.ROT && !rBlaueVersenkt) {
		    			naechsterSpieler = false;
		    		} else {
		    			bRoteVersenkt = true;
		    		}
		    			
		    	} else {
		    		//Schwarz
		    		if(aktSpieler == Farbe.ROT) {
		    			if(kugelnRot <= 0) {
		    				endGame(Farbe.ROT);
		    			} else endGame(Farbe.BLAU);
		    		} else 
		    			if(kugelnBlau <= 0) {
		    				endGame(Farbe.BLAU);
		    			} else endGame(Farbe.ROT);
		    	}
		    	
		    	//Zur baldigen Loeschung vormerken und fallen lassen
		    	kugeln.get(i).markForDeletion();
		    	kugeln.get(i).setVelocity(0, -1, 0);
		    }
		    
		    //Wenn die Zeit (decay) der Kugel i um ist, loesche sie aus dem Spiel
		    if(kugeln.get(i).checkForDeletion(lastMillis)) {
		    	kugeln.remove(i);
		    }
		}
		
		//Ueberpruefe, ob sich die weisse Kugel im Feld befindet
		if(!isBallInField(weiss)) {
			if(!weiss.isMarkedForDeletion()) {
				//Wenn nein, dann boolean setzen und Kugel fallen lassen
				weiss.setVelocity(0, -1, 0);
				weiss.markForDeletion();
				weissAus = true;
				System.out.println("-> Spieler versenkte die weiße Kugel!");
			}
		}
		
		//Weisse Kugel mit allen anderen + Waenden kollidieren lassen
		for(Kugel kugel : kugeln) {
			weiss.circleCollision(kugel);
		}
		weiss.wallCollision();
		
		//Die Frame-skalierte Geschwindigkeit/Richtung und Reibungswiderstand jeder Kugel anwenden
		weiss.applyVelocity(delta);
		for(Kugel kugel : kugeln) {
			kugel.applyVelocity(delta);
		}
	}


	/**
	 * Renderzyklus des Billardspiels
	 * Hier werden alle Objekte gerendert
	 */
	@Override
	public void render(GLAutoDrawable drawable) {
		gl.glClear(GL4.GL_COLOR_BUFFER_BIT | GL4.GL_DEPTH_BUFFER_BIT);
		gl.glClearColor(0.2f, 0.2f, 0.2f, 1.0f);
		
		//Tisch und alle Kugeln rendern
		tisch.render();
		weiss.render();
		for(Kugel k : kugeln) {
			k.render();
		}
		
	}

	/**
	 * Überprüft, ob die gegebene Kugel sich auf dem Spielfeld befindet
	 * @param k Die zu überprüfende Kugel
	 * @return Boolean, ob sich die Kugel auf dem Feld befindet
	 */
	public boolean isBallInField(Kugel k) {
		if(Math.abs(k.getLocation().x) >= 0.635f + size/2 || Math.abs(k.getLocation().z) >= 1.27f + size/2) {
			return false;
		}
		return true;
	}
	
	/**
	 * Löscht die Kugel aus der Liste und damit aus dem Spiel
	 * @param k Die zu löschende Kugel
	 */
	public void removeBall(Kugel k) {
		if(kugeln.contains(k)) {
			kugeln.remove(k);
		}
	}
	
	//Kamera in einer Bounding Sphere mit r = camRadius um die weiße Kugel platzieren und mit camAngleX/Y ausrichten
	/**
	 * Richtet die Kamera aus, entweder am Ursprung oder an der weißen Kugel.
	 * Dabei werden die aktuellen Rotationsinformationen verwendet
	 * @param followWhite Boolean, ob der weißen Kugel gefolgt werden soll
	 */
	private void updateCamera(boolean followWhite) {
		Vector3f loc;
		if(followWhite) {
			loc = weiss.getLocation();
		} else {
			loc = new Vector3f(0,0,0);
		}
		
		mm.cameraLookAt(
				new float[]{loc.x+camRadius*(float)Math.cos(camAngleX),camRadius*(float)Math.sin(camAngleY),loc.z+camRadius*(float)Math.sin(camAngleX)},
				mm.vec2float(loc),
				new float[]{0,1,0}
		);
	}
	
	/**
	 * Beendet das Spiel mit dem angegebenen Gewinner
	 * @param winner Farbkonstante, welche den gewinnenden Spieler symbolisiert
	 */
	private void endGame(int winner) {
		System.out.println("Spieler "+(winner == Farbe.ROT?"ROT":"BLAU")+" hat gewonnen!");
		gameEnd = true;
	}
	
	@Override
	public Integer getId() {
		return 1;
	}
}
