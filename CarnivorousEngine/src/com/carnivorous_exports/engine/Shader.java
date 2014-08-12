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
    		/*
    		"#version 330\n"+
    		 
    		"layout (std140) uniform Matrices {\n"+
    		    "mat4 m_pvm;\n"+
    		    "mat4 m_viewModel;\n"+
    		    "mat3 m_normal;\n"+
    		"};\n"+
    		 
    		"layout (std140) uniform Lights {\n"+
    		    "vec3 l_dir;    // camera space\n"+
    		"};\n"+
    		 
    		"in vec4 position;   // local space\n"+
    		"in vec3 normal;     // local space\n"+
    		"in vec2 texCoord;\n"+
    		 
    		// the data to be sent to the fragment shader
    		"out Data {\n"+
    		    "vec3 normal;\n"+
    		    "vec4 eye;\n"+
    		    "vec2 texCoord;\n"+
    		"} DataOut;\n"+
    		 
    		"void main () {\n"+
    		 
    		    "DataOut.normal = normalize(m_normal * normal);\n"+
    		    "DataOut.eye = -(m_viewModel * position);\n"+
    		    "DataOut.texCoord = texCoord;\n"+
    		 
    		    "gl_Position = m_pvm * position; \n"+
    		"}";
    		*/
    		"#version 330 core\n"+
    		 
    		// Input vertex data, different for all executions of this shader.
    		"layout(location = 0) in vec3 vertexPosition_modelspace;\n"+
    		"layout(location = 1) in vec2 vertexUV;\n"+
    		 
    		// Output data ; will be interpolated for each fragment.
    		"out vec2 UV;\n"+
    		 
    		// Values that stay constant for the whole mesh.
    		"uniform mat4 MVP;\n"+
    		
    		"void main(){\n"+
    		 
    		    // Output position of the vertex, in clip space : MVP * position
    		    "gl_Position =  MVP * vec4(vertexPosition_modelspace,1);\n"+
    		 
    		    // UV of the vertex. No special space for this one.
    		    "UV = vertexUV;\n"+
    		"}";

    private String fragmentShaderString = 
    		/*
    		"#version 330\n"+
    		 
    		"layout (std140) uniform Material {\n"+
    		    "vec4 diffuse;\n"+
    		    "vec4 ambient;\n"+
    		    "vec4 specular;\n"+
    		    "float shininess;\n"+
    		"};\n"+
    		 
    		"layout (std140) uniform Lights {\n"+
    		    "vec3 l_dir;    // camera space\n"+
    		"};\n"+
    		 
    		"in Data {\n"+
    		    "vec3 normal;\n"+
    		    "vec4 eye;\n"+
    		    "vec2 texCoord;\n"+
    		"} DataIn;\n"+
    		 
    		"uniform sampler2D texUnit;\n"+
    		 
    		"out vec4 colorOut;\n"+
    		 
    		"void main() {\n"+
    		 
    		    // set the specular term to black
    		    "vec4 spec = vec4(0.0);\n"+
    		 
    		    // normalize both input vectors
    		    "vec3 n = normalize(DataIn.normal);\n"+
    		    "vec3 e = normalize(vec3(DataIn.eye));\n"+
    		 
    		    "float intensity = max(dot(n,l_dir), 0.0);\n"+
    		 
    		    // if the vertex is lit compute the specular color
    		    "if (intensity > 0.0) {\n"+
    		        // compute the half vector
    		        "vec3 h = normalize(l_dir + e);\n"+  
    		        // compute the specular term into spec
    		        "float intSpec = max(dot(h,n), 0.0);\n"+
    		        "spec = specular * pow(intSpec,shininess);\n"+
    		    "}\n"+
    		    "vec4 texColor = texture(texUnit, DataIn.texCoord);\n"+
    		    "vec4 diffColor = intensity *  diffuse * texColor;\n"+
    		    "vec4 ambColor = ambient * texColor;\n"+
    		 
    		    "colorOut = max(diffColor + spec, ambColor);\n"+
    		"}";
    		*/
    		/*
    		"#version 330 core\n"+
    		 
    		// Interpolated values from the vertex shaders
    		"in vec2 UV;\n"+
    		 
    		// Ouput data
    		"out vec3 color;\n"+
    		 
    		// Values that stay constant for the whole mesh.
    		"uniform sampler2D myTextureSampler;\n"+
    		 
    		"void main(){\n"+
    		 
    		    // Output color = color of the texture at the specified UV
    		    "color = texture( myTextureSampler, UV ).rgb;\n"+
    		"}";
    		*/
    		
    		"uniform sampler2D mytexture;\n"+

    		"void main()\n"+
    		"{\n"+
    				"vec4 color = texture2D(mytexture, gl_TexCoord[0].xy);\n"+
    				"gl_FragColor = color;\n"+
    				//"gl_FragColor =vec4(1,1,1,1);\n"+
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