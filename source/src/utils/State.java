package utils;

import com.jogamp.opengl.GLAutoDrawable;

public interface State {
	/**
	 * Initmethode des States, wird anfangs einmalig aufgerufen
	 * @param drawable Drawable Referenz
	 */
	public void init(GLAutoDrawable drawable);
	
	/**
	 * Updatezyklus des States, wird von der Main Klasse kontinuierlich aufgerufen
	 * @param lastMillis Zeit des letzten Frames
	 */
	public void update(float lastMillis);
	
	/**
	 * Renderzyklus des States, wird nach der Updatemethode ebenfalls kontinuierlich aufgerufen
	 * @param drawable Drawable Referenz
	 */
	public void render(GLAutoDrawable drawable);
	
	/**
	 * Gibt die ID des States zurück
	 * @return ID des States
	 */
	public Integer getId();
}
