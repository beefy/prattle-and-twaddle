package com.carnivorous_exports.engine;

import javax.media.opengl.GL2ES2;

/**
 * Shaders call the graphics card directly.  
 * vertexShaderString and fragmentShaderString are in GLSL.
 * 
 * @author nschultz14
 *
 */
public class Shader {

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
				"color = vec4(0.0, 0.0, 1.0, 1.0);\n" + 
			"} ";


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