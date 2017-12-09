package managers;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.util.glsl.ShaderUtil;

public class ShaderManager {

	private static ShaderManager instance;
	public static ShaderManager getInstance() {
		if (instance == null) {
			instance = new ShaderManager();
		}
		return instance;
	}
	
	/**
	 * Erstellt einen Shader Manager
	 */
	private ShaderManager() {};
	
	private GL4 gl;
	
	/* Shader Programm 1 : Diffuser Farbshader */
	public int SHADER_DIFFUSE;
	
	String[] vertex_shader1 = {
			"#version 400\n"+
			"layout(location = 0) in vec3 vertex_position;"+
			"layout(location = 1) in vec3 vertex_colour;"+
			"layout(location = 2) in vec3 vertex_normal;"+
			"out vec3 frag_colour;"+
			"out vec3 frag_vertex;"+
			"out vec3 frag_normal;"+
			"uniform mat4 transf;"+
			"uniform mat4 projection;"+
			"uniform mat4 view;"+
			"void main () {"+
			"frag_colour = vertex_colour;"+
			"frag_normal = vertex_normal;"+
			"frag_vertex = vertex_position;"+
			"gl_Position = projection * view * transf * vec4(vertex_position, 1.0);"+
			"}"
	};
	
	String[] fragment_shader1 = {
			"#version 400\n"+
			"in vec3 frag_colour;"+
			"in vec3 frag_vertex;"+
			"in vec3 frag_normal;"+
			"uniform vec3 lightPos;"+
			"uniform float lightInt;"+
			"uniform mat4 transf;"+
			"out vec4 final_colour;"+
			"void main () {"+
			"mat3 normalMatrix = transpose(inverse(mat3(transf)));"+
		    "vec3 normal = normalize(normalMatrix * frag_normal);"+
		    "vec3 frag_position = vec3(transf * vec4(frag_vertex, 1));"+
		    "vec3 surfaceToLight = lightPos - frag_position;"+
		    "float brightness = dot(normal, surfaceToLight) / (length(surfaceToLight) * length(normal));"+
		    "brightness = clamp(brightness, 0, 1);"+
		    "final_colour = vec4(brightness * lightInt * frag_colour, 1.0);"+
			"}"
	};
	
	
	/**
	 * Setzt die GL4 Referenz
	 * @param gl Die zu setzende Referenz
	 */
	public void setGL(GL4 gl) {
		this.gl = gl;
	}
	
	/**
	 * Kompiliert die Shader und erstellt die benötigten Shader-Programme
	 */
	public void buildShaders() {
		/* Shader Programm 1 : Grundlegende Farbshader */
		int vs = gl.glCreateShader(GL4.GL_VERTEX_SHADER);
		gl.glShaderSource(vs, 1, vertex_shader1, Buffers.newDirectIntBuffer(new int[] {0}));
		gl.glCompileShader(vs);
		
		int fs = gl.glCreateShader (GL4.GL_FRAGMENT_SHADER);
		gl.glShaderSource(fs, 1, fragment_shader1, Buffers.newDirectIntBuffer(new int[] {0}));
		gl.glCompileShader(fs);

		SHADER_DIFFUSE = gl.glCreateProgram();
		gl.glAttachShader(SHADER_DIFFUSE, fs);
		gl.glAttachShader(SHADER_DIFFUSE, vs);
		gl.glLinkProgram(SHADER_DIFFUSE);
	}
}
