package managers;

import javax.vecmath.Vector3f;

import com.jogamp.opengl.GL4;
import com.jogamp.opengl.math.FloatUtil;
import com.jogamp.opengl.math.Matrix4;

public class MatrixManager {
	private static MatrixManager instance;
	public static MatrixManager getInstance() {
		if (instance == null) {
			instance = new MatrixManager();
		}
		return instance;
	}
	
	/**
	 * Erstellt einen Matrix Manager und initialiesiert die Matrizen
	 */
	private MatrixManager() {
		viewMat = FloatUtil.makeIdentity(new float[16]);
		projectionMat = FloatUtil.makeIdentity(new float[16]);
	};
	
	private GL4 gl;
	private float[] viewMat, projectionMat;
	private ShaderManager shm = ShaderManager.getInstance();
	
	/**
	 * Erstellt eine neue View Matrix und aktualisiert die Uniform Variablen
	 * @param eye Vektor für den Augenpunkt
	 * @param center Vektor des anzuschauenden Punktes
	 * @param up Up-Vektor
	 */
	public void cameraLookAt(float[] eye, float[] center, float[] up) {
		viewMat = FloatUtil.makeLookAt(viewMat, 0, eye, 0, center, 0, up, 0, new Matrix4().getMatrix());
		updateUniforms();
	}
	
	/**
	 * Erstellt eine neue perspektivische Projektionsmatrix und aktualisiert die Uniform Variablen
	 * @param fieldOfView Das zu verwendende Field of View (in Radialwerten)
	 * @param aspectRatio Das Seitenverhältnis des Bildschirms
	 * @param nearPlane Die Entfernung der Near-Plane
	 * @param farPlane Die Entfernung der Far-Plane
	 */
	public void buildProjectionMatrix(float fieldOfView, float aspectRatio, float nearPlane, float farPlane) {
		projectionMat = FloatUtil.makePerspective(projectionMat, 0, true, fieldOfView, aspectRatio, nearPlane, farPlane);
		updateUniforms();
	}
	
	/**
	 * Lädt die aktuellen Matrizen als Uniform Variablen in den VRAM der Grafikkarte
	 */
	private void updateUniforms() {
		gl.glUseProgram(shm.SHADER_DIFFUSE);
		gl.glUniformMatrix4fv(gl.glGetUniformLocation(shm.SHADER_DIFFUSE, "view"), 1, false, viewMat,0);
		gl.glUniformMatrix4fv(gl.glGetUniformLocation(shm.SHADER_DIFFUSE, "projection"), 1, false, projectionMat,0);
	}
	
	/**
	 * Setzt die GL4 Referenz
	 * @param gl Die zu setzende Referenz
	 */
	public void setGL(GL4 gl) {
		this.gl = gl;
	}
	
	/**
	 * Wandelt einen Vektor in ein Float-Array um
	 * @param v Der umzuwandelnde Vektor
	 * @return Das entstandene Float-Array
	 */
	public float[] vec2float(Vector3f v) {
		return new float[]{v.x,v.y,v.z};
	}
}
