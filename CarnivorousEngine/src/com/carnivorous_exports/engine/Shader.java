package com.carnivorous_exports.engine;

import javax.media.opengl.GL2ES2;

public class Shader {

/*
public String vertexShaderString = 
"#if __VERSION__ >= 130\n" + // GLSL 130+ uses in and out
"  #define attribute in\n" + // instead of attribute and varying
"  #define varying out\n" +  // used by OpenGL 3 core and later.
"#endif\n" +

		"uniform Transformation {\n"+
			"mat4 projection_matrix;\n"+
			"mat4 modelview_matrix;\n"+
		"};\n"+
 
		"in vec3 vertex;\n"+
 
		"void main() {\n"+
			"gl_Position = projection_matrix * modelview_matrix * vec4(vertex, 1.0);\n"+
		"}";

private String fragmentShaderString = 
"#if __VERSION__ >= 130\n" + // GLSL 130+ uses in and out
"  #define attribute in\n" + // instead of attribute and varying
"  #define varying out\n" +  // used by OpenGL 3 core and later.
"#endif\n" +
 
		"out vec4 MyFragColor;\n"+
				
		"void main() {\n"+
			"MyFragColor = vec4(1.0, 0.0, 0.0, 1.0);\n"+
		"}";
*/
	//switch to #if __VERSION__ >= 130 b/c some drivers refuse to compile

/*	
	private String vertexShaderString = 
			"#version 330 core\n" +
			"layout(location = 0)" + "in vec3 vertex;\n"+
			"uniform Transformation {\n"+
				"mat4 projection_matrix;\n"+
				"mat4 modelview_matrix;\n"+
			"};\n"+
 
			"void main() {\n" +
			"gl_Position = projection_matrix * modelview_matrix * vec4(vertex, 1.0);\n" + "} ";
	
/*	
	private String vertexShaderString = 
			"#version 330 core\n" +
			"layout(location = 0) in vec2 vertex;\n"+
			"void main() {\n" +
			"gl_Position = vec4(vertex, 0.0, 1.0);\n" + "} ";

	private String fragmentShaderString = 
			"#version 330 core\n" +
			"out vec4 color;\n" +
			"void main() {\n" +
			"color = vec4(0.0, 0.0, 1.0, 1.0);\n" + "} ";
*/

	/*
	private String vertexShaderString = 
			"#version 330 core\n"+
			 
			"layout (std140) uniform Matrices {\n"+
			    "mat4 projModelViewMatrix;\n"+
			    "mat3 normalMatrix;\n"+
			"};\n"+
			 
			"in vec3 position;\n"+
			"in vec3 normal;\n"+
			"in vec2 texCoord;\n"+
			 
			"out VertexData {\n"+
			    "vec2 texCoord;\n"+
			    "vec3 normal;\n"+
			"} VertexOut;\n"+
			 
			"void main()\n"+
			"{\n"+
			    "VertexOut.texCoord = texCoord;\n"+
			    "VertexOut.normal = normalize(normalMatrix * normal);\n"+  
			    "gl_Position = projModelViewMatrix * vec4(position, 1.0);\n"+
			"}";
*/
	
	private String vertexShaderString =
			"#version 110\n"+

			"void main()\n"+
			"{\n"+
			    "gl_Position = gl_ProjectionMatrix * gl_ModelViewMatrix * gl_Vertex;\n"+
			"}";
	
	private String fragmentShaderString = 
			"#version 330 core\n" +
			"out vec4 color;\n" +
			"void main() {\n" +
			"color = vec4(0.0, 0.0, 1.0, 1.0);\n" + "} ";


	// ProgramID
	int programID;

	// Vertex Shader ID
	int vertexShaderID;

	// Fragment Shader ID
	int fragmentShaderID;

	public Shader(GL2ES2 gl) {
		programID = gl.glCreateProgram();
	}

	public void attachVertexShader(GL2ES2 gl) {
		
		// Create GPU shader handles
        // OpenGL ES retuns a index id to be stored for future reference.
        vertexShaderID = gl.glCreateShader(GL2ES2.GL_VERTEX_SHADER);
		
		// Load and compile the source
		String[] vlines = new String[] { vertexShaderString };
		int[] vlengths = new int[] { vlines[0].length() };
		gl.glShaderSource(vertexShaderID, vlines.length, vlines, vlengths, 0);
		gl.glCompileShader(vertexShaderID);
		
		//Check compile status for errors
        int[] compiled = new int[1];
        gl.glGetShaderiv(vertexShaderID, GL2ES2.GL_COMPILE_STATUS, compiled,0);
        if(compiled[0]!=0){System.out.println("Horray! vertex shader compiled");}
        else {
            int[] logLength = new int[1];
            gl.glGetShaderiv(vertexShaderID, GL2ES2.GL_INFO_LOG_LENGTH, logLength, 0);

            byte[] log = new byte[logLength[0]];
            gl.glGetShaderInfoLog(vertexShaderID, logLength[0], (int[])null, 0, log, 0);

            System.err.println("Error compiling the vertex shader: " + new String(log));
            System.exit(1);
        }
        
        //Attach Shader
        gl.glAttachShader(programID, vertexShaderID);
	}

	public void attachFragmentShader(GL2ES2 gl) {

		// Create GPU shader handles
        // OpenGL ES retuns a index id to be stored for future reference.
        fragmentShaderID = gl.glCreateShader(GL2ES2.GL_FRAGMENT_SHADER);
		
		// Load and compile the source
		String[] vlines = new String[] { fragmentShaderString };
		int[] vlengths = new int[] { vlines[0].length() };
		gl.glShaderSource(fragmentShaderID, vlines.length, vlines, vlengths, 0);
		gl.glCompileShader(fragmentShaderID);
		
		//Check compile status for errors
        int[] compiled = new int[1];
        gl.glGetShaderiv(fragmentShaderID, GL2ES2.GL_COMPILE_STATUS, compiled,0);
        if(compiled[0]!=0){System.out.println("Horray! fragment shader compiled");}
        else {	
            int[] logLength = new int[1];
            gl.glGetShaderiv(fragmentShaderID, GL2ES2.GL_INFO_LOG_LENGTH, logLength, 0);

            byte[] log = new byte[logLength[0]];
            gl.glGetShaderInfoLog(fragmentShaderID, logLength[0], (int[])null, 0, log, 0);

            System.err.println("Error compiling the fragment shader: " + new String(log));
            System.exit(1);
        }
        
        //Attach Shader
        gl.glAttachShader(programID, fragmentShaderID);
		
	}
	
	public void link(GL2ES2 gl) {
		
		//link
		gl.glLinkProgram(programID);
		
		
		//check for errors
		int[] compiled = new int[1];
        gl.glGetProgramiv(programID, GL2ES2.GL_LINK_STATUS, compiled,0);
        if(compiled[0]!=0){System.out.println("Horray! Shader program linked!");}
        else {
            int[] logLength = new int[1];
            gl.glGetProgramiv(programID, GL2ES2.GL_INFO_LOG_LENGTH, logLength, 0);

            byte[] log = new byte[logLength[0]];
            gl.glGetProgramInfoLog(programID, logLength[0], (int[])null, 0, log, 0);

            System.err.println("Error linking the program: " + new String(log));
            System.exit(1);
        }
        
	}

	public void bind(GL2ES2 gl) {
        //Associate attribute ids with the attribute names inside
        //the vertex shader.
        gl.glBindAttribLocation(programID, 0, "attribute_Position");
        gl.glBindAttribLocation(programID, 1, "attribute_Color");
		
		gl.glUseProgram(programID);
	}

	public void unbind(GL2ES2 gl) {
		gl.glUseProgram(0);
	}

	public void dispose(GL2ES2 gl) {
        gl.glUseProgram(0);

        gl.glDetachShader(programID, vertexShaderID);
        gl.glDeleteShader(vertexShaderID);
        gl.glDetachShader(programID, fragmentShaderID);
        gl.glDeleteShader(fragmentShaderID);
        gl.glDeleteProgram(programID);
	}

	public int getID() {
		return programID;
	}
}