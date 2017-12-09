package main;

import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;

import org.opencv.core.Core;

import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.util.FPSAnimator;

import managers.CalibrationManager;
import managers.MatrixManager;
import managers.ShaderManager;
import managers.StateManager;
import managers.TouchDetector;
import states.BilliardState;
import states.CalibrationState;
import utils.State;

public class Main implements GLEventListener {
	
	static {
		// Load the native OpenCV library
		System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
	}
	
	
	private long lastMillis;
	private TouchDetector td;
	private StateManager stm;
	private ShaderManager shm;
	private MatrixManager mm;
	
	/**
	 * Erstellt das Mainobjekt, die Managerobjekte
	 * und die benötigten States. Setzt außerdem den ersten
	 * aktiven State.
	 */
	public Main() {
		td = TouchDetector.getInstance();
		stm = StateManager.getInstance();
		shm = ShaderManager.getInstance();
		mm = MatrixManager.getInstance();
		
		//Hier wäre der CalibrationState gestartet worden, welcher dann von sich aus später
		//auf den BilliardState gewechselt hätte.
		
		//stm.addState(new CalibrationState());
		stm.addState(new BilliardState());
		//stm.setActiveState(0);
		stm.setActiveState(1);
		
	}
	
	/** 
	 * Main Methode
	 * Erstellt den Frame und grundlegende OpenGl Objekte
	 * @param args Argumente
	 */
	public static void main(String[] args) {
		GLProfile glp = GLProfile.getMaximum(true);
        GLCapabilities caps = new GLCapabilities(glp);
        GLCanvas canvas = new GLCanvas(caps);
        canvas.addGLEventListener(new Main());

        Animator an = new Animator(canvas);
        an.start();
        
        Frame frame = new Frame("MultiTouchDemo");     
         Rectangle b = GraphicsEnvironment.getLocalGraphicsEnvironment()
        		.getDefaultScreenDevice().getDefaultConfiguration().getBounds();
        
        frame.add(canvas);
        frame.setSize((int)b.getWidth(), (int)b.getHeight());
        frame.setUndecorated(true);
        frame.requestFocus();
        frame.setVisible(true);
	}
	
	/**
	 * Hauptprogrammzyklus
	 * Holt sich das aktuelle Kamerabild, wertet es aus
	 * und führt die update- und Rendermethoden des aktiven States aus.
	 * Berechnet außerdem die Framezeit.
	 */
	@Override
	public void display(GLAutoDrawable drawable) {
		long nanoTime = System.nanoTime();
		td.updateTouchPoints();
		State state = stm.getActiveState();
		if(state != null) {
			state.update(lastMillis);
			state.render(drawable);
		}
		lastMillis = (System.nanoTime() - nanoTime)/1000000;
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {}

	/**
	 * Initialisiert die States und aktiviert grundlegende OpenGL Optionen
	 */
	@Override
	public void init(GLAutoDrawable drawable) {
		GL4 gl = drawable.getGL().getGL4();	
		gl.glEnable(GL4.GL_DEPTH_TEST);
		
		CalibrationManager.getInstance().setResolution(1280, 1024);
		
		shm.setGL(gl);
		shm.buildShaders();
		mm.setGL(gl);
		stm.initStates(drawable);
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
	}
}
