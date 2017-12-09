package geom;

import java.util.ArrayList;
import com.jogamp.opengl.GL4;

import utils.Farbe;

public class Kugel extends Geometry {
	
	private int colour;
	private boolean deletionMarked;
	private int decay = 1000, curDecay = 0;
	
	private int[] vao;
	
	private static float X = 0.525731112119133606f;
	private static float Z = 0.850650808352039932f;
	
	private float[] vertices;
	private ArrayList<Float> tempVertices;
	
	private float[][] icovertices = {
		{-X,0.0f,Z},
		{X,0.0f,Z}, 
		{-X,0.0f,-Z},
		{X,0.0f,-Z},
		{0.0f,Z,X},
		{0.0f,Z,-X},
		{0.0f,-Z,X},
		{0.0f,-Z,-X},
		{Z,X,0.0f},
		{-Z,X,0.0f},
		{Z,-X,0.0f},
		{-Z,-X,0.0f}
	};
	
	private int[][] icoindices = { 
		{ 0, 4, 1 }, 
		{ 0, 9, 4 },
		{ 9, 5, 4 },
        { 4, 5, 8 },
        { 4, 8, 1 },
        { 8, 10, 1 },
        { 8, 3, 10 },
        { 5, 3, 8 },
        { 5, 2, 3 },
        { 2, 7, 3 },
        { 7, 10, 3 },
        { 7, 6, 10 },
        { 7, 11, 6 },
        { 11, 0, 6 },
        { 0, 1, 6 },
        { 6, 1, 10 },
        { 9, 0, 11 },
        { 9, 11, 2 },
        { 9, 2, 5 },
        { 7, 2, 11 } 
    };
	
	private float[] colors = {};
	
	public Kugel(GL4 gl, int shader_prog, float locX, float locY, float locZ, float scale, int color) {
		super(gl, shader_prog, locX, locY, locZ, scale, scale, scale);
		buildSphere(2);
		
		
		this.colour = color;
		colors = new float[vertices.length];
		switch(color) {
			case Farbe.WEISS:
				fillColors(colors, 255, 255, 255); break;
			case Farbe.SCHWARZ:
				fillColors(colors, 20, 20, 20); break;
			case Farbe.ROT:
				fillColors(colors, 248, 45, 39); break;
			case Farbe.BLAU:
				fillColors(colors, 53, 64, 231); break;
		}
		vao = buildVAO(vertices, vertices, colors);
		
		this.collisionRadius = scale;
		this.mass = 4;
		this.friction = 0.35f;
		this.colVelMulti = 0.8f;
	}
	
	/**
	 * Trianguliert die ursprünglichen 12 Flächen des Ikosaeder,
	 * wodurch eine ideale Kugelform weiter angenähert wird
	 * @param depth Rekursionstiefe für die Triangulierung
	 */
	private void buildSphere(int depth) {
		tempVertices = new ArrayList<Float>();
		
        for (int i = 0; i < 20; ++i) {
            subdivide(
              icovertices[icoindices[i][0]], 
              icovertices[icoindices[i][1]],
              icovertices[icoindices[i][2]],
              depth);
        }
        
        vertices = new float[tempVertices.size()]; 
        for(int i = 0; i < tempVertices.size(); i++) {
        	vertices[i] = tempVertices.get(i);
        }
	}
	
	/**
	 * Unterteilt ein gegebenes Dreieck 
	 * @param v1 Vertex 1 der Fläche
	 * @param v2 Vertex 2 der Fläche
	 * @param v3 Vertex 3 der Fläche
	 * @param depth Rekursionstiefe
	 */
	private void subdivide(float v1[], float v2[], float v3[], int depth) {    
		float[] v12 = new float[3];
		float[] v23 = new float[3];
		float[] v31 = new float[3];
		 
		if (depth==0) {
			  tempVertices.add(v1[0]);
			  tempVertices.add(v1[1]);
			  tempVertices.add(v1[2]);
			  
			  tempVertices.add(v2[0]);
			  tempVertices.add(v2[1]);
			  tempVertices.add(v2[2]);
			  
			  tempVertices.add(v3[0]);
			  tempVertices.add(v3[1]);
			  tempVertices.add(v3[2]);
		      return;
		}
		for (int i=0; i<3; i++) {
			v12[i] = (v1[i]+v2[i])/2.0f;
			v23[i] = (v2[i]+v3[i])/2.0f;
			v31[i] = (v3[i]+v1[i])/2.0f;
		}
		 
		v12 = normalize(v12);
		v23 = normalize(v23);
		v31 = normalize(v31);
		 
		subdivide(v2,v23,v12,depth-1);
		subdivide(v1,v12,v31,depth-1);
		subdivide(v3,v31,v23,depth-1);
		subdivide(v12,v23,v31,depth-1);
	}
	
	/**
	 * Normiert den Vektor
	 * @param v Der zu normierende Vektor
	 * @return Der neue normierte Vektor
	 */
	private float[] normalize(float v[]){
         float len = 0;     
         for(int i = 0; i < 3; ++i){
                 len += v[i] *  v[i];
         }     
         len = (float) Math.sqrt(len); 
         for(int i = 0; i < 3; ++i){
                 v[i] /= len;
         }  
         return v;
	 }

	/**
	 * Gibt die Farbe der Kugel zurück (Die Farbe im Spiel,
	 * entspricht in diesem Fall den Vertexfarben)
	 * @return Die Konstante, welche die Farbe der Kugel angibt
	 */
	public int getColour() {
		return colour;
	}
	
	/**
	 * Merke eine Kugel zur Löschung vor
	 */
	public void markForDeletion() {
		deletionMarked = true;
	}
	
	/**
	 * Überprüft, ob die aktuelle Lebenszeit nach der Vermerkung 
	 * die erlaubte Lebenszeit überschritten hat
	 * @param lastMillis Die aktuelle Framezeit
	 * @return Boolean, der angibt, ob das Objekt gelöscht werden soll
	 */
	public boolean checkForDeletion(float lastMillis) {
		if(deletionMarked) {
			curDecay += lastMillis;
			if(curDecay >= decay) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Rendert die Kugel
	 */
	@Override
	public void render() {
		gl.glUseProgram(shader_prog);
			int transLoc = gl.glGetUniformLocation(shader_prog, "transf");
		gl.glUniformMatrix4fv(transLoc, 1, false, transform.getMatrix(),0);
		gl.glBindVertexArray(vao[0]);
		gl.glDrawArrays(GL4.GL_TRIANGLES, 0, vertices.length/3);
	}

	/**
	 * Gibt an, ob die Kugel zum Löschen vorgemerkt wurde
	 * @return Boolean, ob dies zutrifft
	 */
	public boolean isMarkedForDeletion() {
		return deletionMarked;
	}

	/**
	 * Setzt die gezählte Lebenszeit zurück
	 */
	public void resetDecay() {
		curDecay = 0;
	}
}
