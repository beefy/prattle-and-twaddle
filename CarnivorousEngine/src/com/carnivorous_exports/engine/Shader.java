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
    		"#version 330 core\n"+

    		"layout(location = 0) in vec2 pos;\n"+
    		"layout(location = 1) in vec2 tex;\n"+

    		"out vec2 texCoords;\n"+

    		"void main()\n"+
    		"{\n"+
    		    "texCoords = tex;\n"+
    		    "gl_Position = vec4(pos, 0.0, 1.0);\n"+
    		"}";

    private String fragmentShaderString = 
    		"#version 330 core\n"+

    		"uniform sampler2D tex;\n"+

    		"in vec2 texCoords;\n"+
    		"out vec4 outColor;\n"+

    		"void main()\n"+
    		"{\n"+
    		    "outColor = texture(tex, texCoords);\n"+
    		"}";


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
	
    /**
     * Sets the uniforms in this shader
     * 
     * @param name    The name of the uniform
     * @param values  The values of the uniforms (Max 4)
     */
    public void setUniform(GL2ES2 gl, String name, float... values)
    {
        if (values.length > 4)
        {
            System.err.println("Uniforms cannot have more than 4 values");
            System.exit(1);
        }
        
        // Get the location of the uniform
        int location = gl.glGetUniformLocation(programID, name);
        
        // Set the uniform values
        switch (values.length)
        {
            case 1:
                gl.glUniform1f(location, values[0]);
                break;                
            case 2:
                gl.glUniform2f(location, values[0], values[1]);
                break;                
            case 3:
                gl.glUniform3f(location, values[0], values[1], values[2]);
                break;                
            case 4:
                gl.glUniform4f(location, values[0], values[1], values[2], values[3]);
                break;
        }
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