package geom;

import com.jogamp.opengl.GL4;

public class Tisch extends Geometry {
	
	private int[] vao;
	
	private float[] vertices = {
			//Grundfläche
			-0.635f, 0.0f, 1.27f,
			0.635f, 0.0f, 1.27f, 
			0.635f, 0.0f, -1.27f,
			-0.635f, 0.0f, -1.27f,
			//Vertikale Seitenkanten
				//Kante unten links
				-0.635f, 0.0f, 1.155f,
				-0.635f, 0.0f, 0.065f,
				-0.635f, 0.038f, 0.065f,
				-0.635f, 0.038f, 1.155f,
				//Kante oben links
				-0.635f, 0.0f, -0.065f,
				-0.635f, 0.0f, -1.155f,
				-0.635f, 0.038f, -1.155f,
				-0.635f, 0.038f, -0.065f,
				//Kante oben mitte
				-0.52f, 0.0f, -1.27f,
				0.52f, 0.0f, -1.27f,
				0.52f, 0.038f, -1.27f,
				-0.52f, 0.038f, -1.27f,
				//Kante oben rechts
				0.635f, 0.0f, -1.155f,
				0.635f, 0.0f, -0.065f,
				0.635f, 0.038f, -0.065f,
				0.635f, 0.038f, -1.155f,
				//Kante unten rechts
				0.635f, 0.0f, 0.065f,
				0.635f, 0.0f, 1.155f,
				0.635f, 0.038f, 1.155f,
				0.635f, 0.038f, 0.065f,
				//Kante oben mitte
				0.52f, 0.0f, 1.27f,
				-0.52f, 0.0f, 1.27f,
				-0.52f, 0.038f, 1.27f,
				0.52f, 0.038f, 1.27f,
				
			//Horizontale Seitenkanten
				//Kante unten links
				-0.635f, 0.038f, 1.155f,
				-0.635f, 0.038f, 0.065f,
				-0.735f, 0.038f, 0.065f,
				-0.735f, 0.038f, 1.155f,
				//Kante oben links
				-0.635f, 0.038f, -0.065f,
				-0.635f, 0.038f, -1.155f,
				-0.735f, 0.038f, -1.155f,
				-0.735f, 0.038f, -0.065f,
				//Kante oben mitte
				-0.52f, 0.038f, -1.27f,
				0.52f, 0.038f, -1.27f,
				0.52f, 0.038f, -1.37f,
				-0.52f, 0.038f, -1.37f,
				//Kante oben rechts
				0.635f, 0.038f, -1.155f,
				0.635f, 0.038f, -0.065f,
				0.735f, 0.038f, -0.065f,
				0.735f, 0.038f, -1.155f,
				//Kante unten rechts
				0.635f, 0.038f, 0.065f,
				0.635f, 0.038f, 1.155f,
				0.735f, 0.038f, 1.155f,
				0.735f, 0.038f, 0.065f,
				//Kante unten mitte
				0.52f, 0.038f, 1.27f,
				-0.52f, 0.038f, 1.27f,
				-0.52f, 0.038f, 1.37f,
				0.52f, 0.038f, 1.37f					
	};
	
	private float[] normals = {
			//Grundfläche
			0.0f, 1.0f, 0.0f,
			0.0f, 1.0f, 0.0f, 
			0.0f, 1.0f, 0.0f,
			0.0f, 1.0f, 0.0f,
			//Vertikale Seitenkanten
				//Kante unten links
				1.0f, 0.0f, 0.0f,
				1.0f, 0.0f, 0.0f,
				1.0f, 0.0f, 0.0f,
				1.0f, 0.0f, 0.0f,
				//Kante oben links
				1.0f, 0.0f, 0.0f,
				1.0f, 0.0f, 0.0f,
				1.0f, 0.0f, 0.0f,
				1.0f, 0.0f, 0.0f,
				//Kante oben mitte
				0.0f, 0.0f, 1.0f,
				0.0f, 0.0f, 1.0f,
				0.0f, 0.0f, 1.0f,
				0.0f, 0.0f, 1.0f,
				//Kante oben rechts
				-1.0f, 0.0f, 0.0f,
				-1.0f, 0.0f, 0.0f,
				-1.0f, 0.0f, 0.0f,
				-1.0f, 0.0f, 0.0f,
				//Kante unten rechts
				-1.0f, 0.0f, 0.0f,
				-1.0f, 0.0f, 0.0f,
				-1.0f, 0.0f, 0.0f,
				-1.0f, 0.0f, 0.0f,
				//Kante oben mitte
				0.0f, 0.0f, -1.0f,
				0.0f, 0.0f, -1.0f,
				0.0f, 0.0f, -1.0f,
				0.0f, 0.0f, -1.0f,
				
			//Horizontale Seitenkanten
				//Kante unten links
				0.0f, 1.0f, 0.0f,
				0.0f, 1.0f, 0.0f,
				0.0f, 1.0f, 0.0f,
				0.0f, 1.0f, 0.0f,
				//Kante oben links
				0.0f, 1.0f, 0.0f,
				0.0f, 1.0f, 0.0f,
				0.0f, 1.0f, 0.0f,
				0.0f, 1.0f, 0.0f,
				//Kante oben mitte
				0.0f, 1.0f, 0.0f,
				0.0f, 1.0f, 0.0f,
				0.0f, 1.0f, 0.0f,
				0.0f, 1.0f, 0.0f,
				//Kante oben rechts
				0.0f, 1.0f, 0.0f,
				0.0f, 1.0f, 0.0f,
				0.0f, 1.0f, 0.0f,
				0.0f, 1.0f, 0.0f,
				//Kante unten rechts
				0.0f, 1.0f, 0.0f,
				0.0f, 1.0f, 0.0f,
				0.0f, 1.0f, 0.0f,
				0.0f, 1.0f, 0.0f,
				//Kante unten mitte
				0.0f, 1.0f, 0.0f,
				0.0f, 1.0f, 0.0f,
				0.0f, 1.0f, 0.0f,
				0.0f, 1.0f, 0.0f					
	};
	
	private float[] colors = {
			//Grundfläche
			0.024f, 0.643f, 0.302f,
			0.024f, 0.643f, 0.302f,
			0.024f, 0.643f, 0.302f,
			0.024f, 0.643f, 0.302f,
			//Vertikale Seitenkanten
				//Kante unten links
				0.024f, 0.643f, 0.302f,
				0.024f, 0.643f, 0.302f,
				0.024f, 0.643f, 0.302f,
				0.024f, 0.643f, 0.302f,
				//Kante oben links
				0.024f, 0.643f, 0.302f,
				0.024f, 0.643f, 0.302f,
				0.024f, 0.643f, 0.302f,
				0.024f, 0.643f, 0.302f,
				//Kante oben mitte
				0.024f, 0.643f, 0.302f,
				0.024f, 0.643f, 0.302f,
				0.024f, 0.643f, 0.302f,
				0.024f, 0.643f, 0.302f,
				//Kante oben rechts
				0.024f, 0.643f, 0.302f,
				0.024f, 0.643f, 0.302f,
				0.024f, 0.643f, 0.302f,
				0.024f, 0.643f, 0.302f,
				//Kante unten rechts
				0.024f, 0.643f, 0.302f,
				0.024f, 0.643f, 0.302f,
				0.024f, 0.643f, 0.302f,
				0.024f, 0.643f, 0.302f,
				//Kante oben mitte
				0.024f, 0.643f, 0.302f,
				0.024f, 0.643f, 0.302f,
				0.024f, 0.643f, 0.302f,
				0.024f, 0.643f, 0.302f,		
			//Horizontale Seitenkanten
				//Kante unten links
				0.216f, 0.066f, 0.066f,
				0.216f, 0.066f, 0.066f,
				0.216f, 0.066f, 0.066f,
				0.216f, 0.066f, 0.066f,
				//Kante oben links
				0.216f, 0.066f, 0.066f,
				0.216f, 0.066f, 0.066f,
				0.216f, 0.066f, 0.066f,
				0.216f, 0.066f, 0.066f,
				//Kante oben mitte
				0.216f, 0.066f, 0.066f,
				0.216f, 0.066f, 0.066f,
				0.216f, 0.066f, 0.066f,
				0.216f, 0.066f, 0.066f,
				//Kante oben rechts
				0.216f, 0.066f, 0.066f,
				0.216f, 0.066f, 0.066f,
				0.216f, 0.066f, 0.066f,
				0.216f, 0.066f, 0.066f,
				//Kante unten rechts
				0.216f, 0.066f, 0.066f,
				0.216f, 0.066f, 0.066f,
				0.216f, 0.066f, 0.066f,
				0.216f, 0.066f, 0.066f,
				//Kante unten mitte
				0.216f, 0.066f, 0.066f,
				0.216f, 0.066f, 0.066f,
				0.216f, 0.066f, 0.066f,
				0.216f, 0.066f, 0.066f
	};
	
	public Tisch(GL4 gl, int shader_prog, float locX, float locY, float locZ, float scaleX, float scaleY, float scaleZ) {
		super(gl, shader_prog, locX, locY, locZ, scaleX, scaleY, scaleZ);
		vao = buildVAO(vertices, normals, colors);
	}
	                                     
	public Tisch(GL4 gl, int shader_prog, float locX, float locY, float locZ, float rotX, float rotY, float rotZ, float scaleX, float scaleY, float scaleZ) {
		super(gl, shader_prog, locX, locY, locZ, rotX, rotY, rotZ, scaleX, scaleY, scaleZ);
		vao = buildVAO(vertices, normals, colors);
	}

	/** 
	 * Rendert den Tisch
	 */
	@Override
	public void render() {
		gl.glUseProgram(shader_prog);
			int transLoc = gl.glGetUniformLocation(shader_prog, "transf");
		gl.glUniformMatrix4fv(transLoc, 1, false, transform.getMatrix(),0);
		gl.glBindVertexArray(vao[0]);
		gl.glDrawArrays(GL4.GL_QUADS, 0, 84);	
	}

}
