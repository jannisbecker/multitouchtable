package geom;

import java.nio.FloatBuffer;
import javax.vecmath.Vector3f;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.math.Matrix4;

public abstract class Geometry {
	protected GL4 gl;
	
	protected Matrix4 transform;
	protected Vector3f velocity, location, rotation, scale;
	protected float collisionRadius, mass, friction, colVelMulti;
	
	protected int shader_prog;
	
	/**
	 * Oberklassen Konstruktor um Geometrieobjekte zu beschreiben
	 * @param gl GL4 Objektreferenz
	 * @param shader_prog ID des zu benutzenden Shader Programms
	 * @param locX Position in X-Richtung
	 * @param locY Position in Y-Richtung
	 * @param locZ Position in Z-Richtung
	 * @param scaleX Skalierung in X-Richtung
	 * @param scaleY Skalierung in Y-Richtung
	 * @param scaleZ Skalierung in Z-Richtung
	 */
	protected Geometry(GL4 gl, int shader_prog, float locX, float locY, float locZ, float scaleX, float scaleY, float scaleZ) {
		this(gl,shader_prog,locX, locY, locZ, 0, 0, 0, scaleX, scaleY, scaleZ);
	}
	
	/**
	 * Oberklassen Konstruktor um Geometrieobjekte zu beschreiben
	 * @param gl GL4 Objektreferenz
	 * @param shader_prog ID des zu benutzenden Shader Programms
	 * @param locX Position in X-Richtung
	 * @param locY Position in Y-Richtung
	 * @param locZ Position in Z-Richtung
	 * @param rotX Rotation um X-Achse
	 * @param rotY Rotation um Y-Achse
	 * @param rotZ Rotation um Z-Achse
	 * @param scaleX Skalierung in X-Richtung
	 * @param scaleY Skalierung in Y-Richtung
	 * @param scaleZ Skalierung in Z-Richtung
	 */
	protected Geometry(GL4 gl, int shader_prog, float locX, float locY, float locZ, float rotX, float rotY, float rotZ, float scaleX, float scaleY, float scaleZ) {
		this.gl = gl;
		this.shader_prog = shader_prog;
		this.transform = new Matrix4();
		
		this.velocity = new Vector3f();
		this.location = new Vector3f();
		this.rotation = new Vector3f();
		this.scale = new Vector3f();
		
		this.location.x = locX;
		this.location.y = locY;
		this.location.z = locZ;
		this.rotation.x = rotX;
		this.rotation.y = rotY;
		this.rotation.z = rotZ;
		this.scale.x = scaleX;
		this.scale.y = scaleY;
		this.scale.z = scaleZ;
		
		this.rebuildMatrix();
	}

	/**
	 * Erstellt ein VAO und dazugehörige VBOs aus den gegebenen Arrays und gibt dessen ID zurück
	 * @param points Array von Vertex-Positionsdaten
	 * @param normals Array von Vertex-Normalendaten
	 * @param colors Array von Vertex-Farbdaten
	 * @return
	 */
	protected int[] buildVAO(float[] points, float[] normals, float[] colors) {
		int[] points_vbo = new int[1];
		gl.glGenBuffers(1, points_vbo, 0);
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, points_vbo[0]);
		gl.glBufferData(GL.GL_ARRAY_BUFFER, points.length *  Buffers.SIZEOF_FLOAT, FloatBuffer.wrap(points), GL.GL_STATIC_DRAW);
		
		int[] colours_vbo = new int[1];
		gl.glGenBuffers(1, colours_vbo, 0);
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, colours_vbo[0]);
		gl.glBufferData(GL.GL_ARRAY_BUFFER, colors.length *  Buffers.SIZEOF_FLOAT, FloatBuffer.wrap(colors), GL.GL_STATIC_DRAW);
		
		int[] normals_vbo = new int[1];
		gl.glGenBuffers(1, normals_vbo, 0);
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, normals_vbo[0]);
		gl.glBufferData(GL.GL_ARRAY_BUFFER, normals.length *  Buffers.SIZEOF_FLOAT, FloatBuffer.wrap(normals), GL.GL_STATIC_DRAW);
		
		int[] vao = new int[1];
		gl.glGenVertexArrays(1, vao, 0);
		gl.glBindVertexArray(vao[0]);
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, points_vbo[0]);
		gl.glVertexAttribPointer (0, 3, GL.GL_FLOAT, false, 0, 0);
		gl.glBindBuffer (GL.GL_ARRAY_BUFFER, colours_vbo[0]);
		gl.glVertexAttribPointer (1, 3, GL.GL_FLOAT, false, 0, 0);
		gl.glBindBuffer (GL.GL_ARRAY_BUFFER, normals_vbo[0]);
		gl.glVertexAttribPointer (2, 3, GL.GL_FLOAT, true, 0, 0);
		
		gl.glEnableVertexAttribArray (0);
		gl.glEnableVertexAttribArray (1);
		gl.glEnableVertexAttribArray (2);
		
		return vao;
	}
	
	/**
	 * Hilfsmethode, um ein Vertex-Farbarray einheitlich mit einer Farbe zu füllen
	 * @param colors Zu füllendes Array
	 * @param r Rotanteil (0-255)
	 * @param g Grünanteil (0-255)
	 * @param b Blauanteil (0-255)
	 */
	protected void fillColors(float[] colors, int r, int g, int b) {
		for(int i = 0; i < colors.length; i+=3) {
			colors[i]= (float)r/255f;
			colors[i+1]= (float)g/255f;
			colors[i+2]= (float)b/255f;
		}
	}
	
	/**
	 * Rendert das Objekt. Diese Methode muss in der Unterklasse realisiert werden
	 */
	public abstract void render();
	
	/**
	 * Methode um die Transformationsmatrix nach einer Transformation neu aufzubauen
	 */
	protected void rebuildMatrix() {
		transform.loadIdentity();
		transform.translate(location.x, location.y, location.z);
		transform.rotate(rotation.x, 1, 0, 0);
		transform.rotate(rotation.y, 0, 1, 0);
		transform.rotate(rotation.z, 0, 0, 1);	
		transform.scale(scale.x, scale.y, scale.z);
	}
	
	/**
	 * Überprüft ob dieses Objekt mit dem gegebenen anderen Geometrieobjekt g kollidiert,
	 * dies wird anhand einer Kreiskollision berechnet
	 * @param g Das andere zu kollidierende Geometrieobjekt
	 */
	public void circleCollision(Geometry g) {
		Vector3f thisLoc = new Vector3f(location);
		Vector3f otherLoc = new Vector3f(g.getLocation());
		
		Vector3f thisVel = new Vector3f(velocity);
		Vector3f otherVel = new Vector3f(g.getVelocity());
		
		//thisLoc is now a vector from this ball to the other ball
		thisLoc.sub(otherLoc);
		
		//They Collide
		if(thisLoc.length() <= collisionRadius + g.getCollisionRadius()) {        
			
			Vector3f normal = new Vector3f(g.location);
            normal.sub(location);
            normal.normalize();
            
            Vector3f rVel = new Vector3f(otherVel);
            rVel.sub(thisVel);
            
            float nVel = rVel.dot(normal);
			float a1 = thisVel.dot(normal);
			float a2 = otherVel.dot(normal);
		
			//Prevent "sticking" (constant recollision every frame) of objects
			if(nVel <= 0) {			
				//Calculate new velocity vectors
				float p = (float)(2.0 * (a1 - a2)) / (this.mass + g.mass);
	
				velocity.x -= p * g.mass * normal.x * colVelMulti;
				velocity.z -= p * g.mass * normal.z * colVelMulti;

				g.velocity.x += p * this.mass * normal.x * colVelMulti;
				g.velocity.z += p * this.mass * normal.z * colVelMulti;
			}
		}
	}

	
	/**
	 * Überprüft, ob dieses Objekt mit einer der Wände kollidiert
	 * und kehrt den Bewegungsvektor um, falls dies der Fall ist
	 */
	public void wallCollision() {
		// Kugel befindet sich am Rand des Spielfelds? (links, rechts)
		if(Math.abs(location.x) + scale.x >= 0.635f) {
			//Kugel prallt vor Wand (links, rechts)
			if(Math.abs(location.z) < scale.z/2 + 1.155f) {
				//Kugel rollt nicht ins mittlere Loch
				if(Math.abs(location.z) > 0.065f - scale.z/2)  {
					//Wenn Kugel am rechten Rand
					if(location.x > 0) {
						velocity.x = -Math.abs(velocity.x);
					} 
					//Sonst
					else {
						velocity.x = Math.abs(velocity.x);
					}
				}
			}
		// Kugel befindet sich am Rand des Spielfelds? (oben, unten)
		} else if(Math.abs(location.z) + scale.z >= 1.27f) {
			//Kugel prallt vor Wand? (oben, unten)
			if (Math.abs(location.x) < scale.x/2 + 0.52f) {
				//Wenn Kugel am unteren Rand
				if(location.z > 0) {
					velocity.z = -Math.abs(velocity.z);
				} 
				//Sonst
				else {
					velocity.z = Math.abs(velocity.z);
				}
			}
		}
	}
	
	
	/**
	 * Gibt die Position des Objekts zurück
	 * @return Position des Objekts
	 */
	public Vector3f getLocation() {
		return location;
	}
	
	/**
	 * Gibt die Rotation des Objekts zurück
	 * @return Rotation des Objekts
	 */
	public Vector3f getRotation() {
		return rotation;
	}
	
	/**
	 * Gibt die Skalierung des Objekts zurück
	 * @return Skalierung des Objekts
	 */
	public Vector3f getScale() {
		return scale;
	}
	
	/**
	 * Gibt den Bewegungsvektor des Objekts zurück
	 * @return Bewegungsvektor des Objekts
	 */
	public Vector3f getVelocity() {
		return velocity;
	}
	
	/**
	 * Gibt den Kollisionsradius zurück
	 * @return Kollisionsradius
	 */
	public float getCollisionRadius() {
		return collisionRadius;
	}

	/**
	 * Setzt den Kollisionsradius auf einen gegebenen Wert
	 * @param collisionRadius Neuer Kollisionsradius
	 */
	public void setCollisionRadius(float collisionRadius) {
		this.collisionRadius = collisionRadius;
	}

	/**
	 * Gibt die Masse des Objekts zurück
	 * @return Masse des Objekts
	 */
	public float getMass() {
		return mass;
	}

	/**
	 * Setzt die Masse auf einen neuen Wert
	 * @param mass Neue Masse des Objekts
	 */
	public void setMass(float mass) {
		this.mass = mass;
	}

	/**
	 * Bewegt das Objekt in einer flüssigen Bewegung um die angegebenen Werte (pro Sekunde)
	 * @param locX Bewegung in X-Richtung pro Sekunde
	 * @param locY Bewegung in Y-Richtung pro Sekunde
	 * @param locZ Bewegung in Z-Richtung pro Sekunde
	 * @param delta Framezeit für die flüssige Animation
	 */
	public void move(float locX, float locY, float locZ, float delta) {
		this.location.x += locX * delta;
		this.location.y += locY * delta;
		this.location.z += locZ * delta;
		this.rebuildMatrix();
	}
	
	/**
	 * Bewegt das Objekt direkt in einem Sprung zum angegebenen Ort
	 * @param locX Neue X-Koordinate
	 * @param locY Neue Y-Koordinate
	 * @param locZ Neue Z-Koordinate
	 */
	public void moveTo(float locX, float locY, float locZ) {
		this.location.x = locX;
		this.location.y = locY;
		this.location.z = locZ;
		this.rebuildMatrix();
	}
	
	/**
	 * Rotiert das Objekt in einer flüssigen Bewegung um die angegebenen Werte (pro Sekunde)
	 * @param rotX Rotation um X-Achse pro Sekunde
	 * @param rotY Rotation um Y-Achse pro Sekunde
	 * @param rotZ Rotation um Z-Achse pro Sekunde
	 * @param delta Framezeit für die flüssige Animation
	 */
	public void rotate(float rotX, float rotY, float rotZ, float delta) {
		this.rotation.x += (rotX * delta);
		this.rotation.y += (rotY * delta);
		this.rotation.z += (rotZ * delta);
		this.rebuildMatrix();
	}
	
	/**
	 * Rotiert das Objekt direkt zu den angegebenen absoluten Werten
	 * @param rotX Neue Rotation um X-Achse
	 * @param rotY Neue Rotation um Y-Achse
	 * @param rotZ Neue Rotation um Z-Achse
	 */
	public void rotateTo(float rotX, float rotY, float rotZ) {
		this.rotation.x = rotX;
		this.rotation.y = rotY;
		this.rotation.z = rotZ;
		this.rebuildMatrix();
	}
	
	/**
	 * Skaliert das Objekt in einer flüssigen Animation um die angegebenen Werte
	 * @param scaleX Skalierung in X-Richtung pro Sekunde
	 * @param scaleY Skalierung in Y-Richtung pro Sekunde
	 * @param scaleZ Skalierung in Z-Richtung pro Sekunde
	 * @param delta Framezeit für die flüssige Animation
	 */
	public void scale(float scaleX, float scaleY, float scaleZ, float delta) {
		this.scale.x += scaleX * delta;
		this.scale.y += scaleY * delta;
		this.scale.z += scaleZ * delta;
		this.rebuildMatrix();
	}
	
	/**
	 * Skaliert das Objekt direkt zu der angegebenen absoluten Skalierung
	 * @param scaleX Neue Skalierung in X-Richtung
	 * @param scaleY Neue Skalierung in Y-Richtung
	 * @param scaleZ Neue Skalierung in Z-Richtung
	 */
	public void scaleTo(float scaleX, float scaleY, float scaleZ) {
		this.scale.x = scaleX;
		this.scale.y = scaleY;
		this.scale.z = scaleZ;
		this.rebuildMatrix();
	}
	
	/**
	 * Setzt den Bewegungsvektor neu
	 * @param velocity Neuer Bewegungsvektor
	 */
	public void setVelocity(Vector3f velocity) {
		this.velocity = velocity;
	}
	
	/**
	 * Setzt den Bewegungsvektor neu
	 * @param velX Neue Bewegungsgeschwindigkeit in X-Richtung
	 * @param velY Neue Bewegungsgeschwindigkeit in Y-Richtung
	 * @param velZ Neue Bewegungsgeschwindigkeit in Z-Richtung
	 */
	public void setVelocity(float velX, float velY, float velZ) {
		this.velocity.x = velX;
		this.velocity.y = velY;
		this.velocity.z = velZ;
	}
	
	/**
	 * Addiert den angegebenen Vektor mit dem aktuellen Geschwindigkeitsvektor
	 * @param velocity Der zu Addierende Geschwindigkeitsvektor
	 */
	public void addVelocity(Vector3f velocity) {
		this.velocity.add(velocity);
	}
	
	/**
	 * Addiert den angegebenen Vektor mit dem aktuellen Geschwindigkeitsvektor
	 * @param velX Die zu addierende Bewegungsgeschwindigkeit in X-Richtung
	 * @param velY Die zu addierende Bewegungsgeschwindigkeit in Y-Richtung
	 * @param velZ Die zu addierende Bewegungsgeschwindigkeit in Z-Richtung
	 */
	public void addVelocity(float velX, float velY, float velZ) {
		this.velocity.x += velX;
		this.velocity.y += velY;
		this.velocity.z += velZ;
	}
	
	/**
	 * Wendet den Bewegungsvektor des Objekts auf seine Position an,
	 * womit sich das Objekt pro Sekunde um die im Vektor gegebenen Werte bewegt
	 * @param delta Framezeit für die flüssige Animation
	 */
	public void applyVelocity(float delta) {
		//Calculate per-frame friction and per-frame velocity
		//Friction is a percent value applied over a time of one second
		Vector3f frictionVector = new Vector3f(velocity);
		frictionVector.scale(delta);
		Vector3f scaledVel = new Vector3f(frictionVector);
		frictionVector.scale(friction);
		
		//Subtract friction from object velocity
		velocity.sub(frictionVector);
		
		//Apply per-frame velocity to current location
		this.location.add(scaledVel);
		this.rebuildMatrix();
	}
}
