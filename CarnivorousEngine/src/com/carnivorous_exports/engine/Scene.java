package com.carnivorous_exports.engine;

import static javax.media.opengl.GL.GL_LINEAR;
import static javax.media.opengl.GL.GL_TEXTURE_2D;
import static javax.media.opengl.GL.GL_TEXTURE_MAG_FILTER;
import static javax.media.opengl.GL.GL_TEXTURE_MIN_FILTER;
import static javax.media.opengl.GL2.GL_COMPILE;
import static javax.media.opengl.GL2GL3.GL_QUADS;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_AMBIENT;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_SHININESS;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_SPECULAR;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLException;
import javax.media.opengl.fixedfunc.GLLightingFunc;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureCoords;
import com.jogamp.opengl.util.texture.TextureIO;

/**
 * 
 * This class contains the methods that draw the scene, assign textures and
 * materials of objects, and check for collision detection.
 * 
 * @author Nathaniel Schultz
 * 
 */
public class Scene {

	Shader shader;
	GL2 gl;
	int[] displayList;
	boolean terrainBuilt;

	// for collision
	boolean boxX;
	boolean boxY;
	boolean boxZ;

	// for testing rotation
	float tempRotX;
	float tempRot;

	// should be the same as moveSpeed in Renderer
	float moveSpeed = 2.0f;

	Texture texture;

	// Texture image flips vertically. Shall use TextureCoords class to retrieve
	// the top, bottom, left and right coordinates.
	private float textureTop;
	private float textureBottom;
	private float textureLeft;
	private float textureRight;

	float whitish[] = { 0.8f, 0.8f, 0.8f, 1 };
	float white[] = { 1, 1, 1, 1 };
	float blackish[] = { 0.2f, 0.2f, 0.2f, 1 };
	float black[] = { 0, 0, 0, 1 };

	private String textureFileName;
	private String textureFileType;

	float selectedObject;
	public boolean collided;

	private static float[][] boxColors = { // Bright: Red, Orange, Yellow,
			// Green, Blue
			{ 1.0f, 0.0f, 0.0f }, { 1.0f, 0.5f, 0.0f }, { 1.0f, 1.0f, 0.0f },
			{ 0.0f, 1.0f, 0.0f }, { 0.0f, 1.0f, 1.0f } };

	IntBuffer vertexArray = IntBuffer.allocate(1);
	int vboTextureCoordHandle;

	/**
	 * This method returns a display list for a cube with a specific texture.
	 * Display lists can let you replace all the vertex calls for an object with
	 * one line. It's especially useful if you have to draw many of the same
	 * kind of primitive object.
	 * 
	 * @param gl
	 *            the current GL
	 * @param textureFileName
	 *            the path to the texture file
	 * @param textureFileType
	 *            the type of file that the texture file is (.jpg, .png, etc)
	 * @return the display list
	 */
	public int getCubeList(GL2 gl, String textureFileName,
			String textureFileType) {

		// Set material properties.
		float[] rgba = { 1f, 1f, 1f }; // white
		gl.glMaterialfv(GL.GL_FRONT, GL_AMBIENT, rgba, 0);
		gl.glMaterialfv(GL.GL_FRONT, GLLightingFunc.GL_DIFFUSE, rgba, 0);
		gl.glMaterialfv(GL.GL_FRONT, GL_SPECULAR, rgba, 0);
		gl.glMaterialf(GL.GL_FRONT, GL_SHININESS, 0.5f);

		this.textureFileName = textureFileName;
		this.textureFileType = textureFileType;

		loadTexture(gl);

		int base = gl.glGenLists(1);

		// Create a new list for box (with open-top), pre-compile for efficiency
		int cubeList = base;

		gl.glNewList(cubeList, GL_COMPILE);

		// Enables this texture's target in the current GL context's state.
		texture.enable(gl); // same as gl.glEnable(texture.getTarget());

		// gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE,
		// GL.GL_REPLACE);

		// Binds this texture to the current GL context.
		texture.bind(gl); // same as gl.glBindTexture(texture.getTarget(),
							// texture.getTextureObject());

		gl.glMaterialfv(GL.GL_FRONT, GL_AMBIENT, new float[] { 0.5f, 0.5f,
				0.5f, 1.0f }, 0);
		gl.glMaterialfv(GL.GL_FRONT, GLLightingFunc.GL_DIFFUSE, white, 0);
		gl.glMaterialfv(GL.GL_FRONT, GL_SPECULAR, white, 0);
		gl.glMaterialfv(GL.GL_FRONT, GL_SHININESS, white, 0);

		gl.glBegin(GL_QUADS);

		// Front Face
		gl.glNormal3f(0.0f, 0.0f, 1.0f);
		gl.glTexCoord2f(textureLeft, textureBottom);
		gl.glVertex3f(-1.0f, -1.0f, 1.0f); // bottom-left of the texture and
											// quad
		gl.glTexCoord2f(textureRight, textureBottom);
		gl.glVertex3f(1.0f, -1.0f, 1.0f); // bottom-right of the texture and
											// quad
		gl.glTexCoord2f(textureRight, textureTop);
		gl.glVertex3f(1.0f, 1.0f, 1.0f); // top-right of the texture and quad
		gl.glTexCoord2f(textureLeft, textureTop);
		gl.glVertex3f(-1.0f, 1.0f, 1.0f); // top-left of the texture and quad

		// Back Face
		gl.glNormal3f(0.0f, 0.0f, -1.0f);
		gl.glTexCoord2f(textureRight, textureBottom);
		gl.glVertex3f(-1.0f, -1.0f, -1.0f);
		gl.glTexCoord2f(textureRight, textureTop);
		gl.glVertex3f(-1.0f, 1.0f, -1.0f);
		gl.glTexCoord2f(textureLeft, textureTop);
		gl.glVertex3f(1.0f, 1.0f, -1.0f);
		gl.glTexCoord2f(textureLeft, textureBottom);
		gl.glVertex3f(1.0f, -1.0f, -1.0f);

		// Top Face
		gl.glNormal3f(0.0f, 1.0f, 0.0f);
		gl.glTexCoord2f(textureLeft, textureTop);
		gl.glVertex3f(-1.0f, 1.0f, -1.0f);
		gl.glTexCoord2f(textureLeft, textureBottom);
		gl.glVertex3f(-1.0f, 1.0f, 1.0f);
		gl.glTexCoord2f(textureRight, textureBottom);
		gl.glVertex3f(1.0f, 1.0f, 1.0f);
		gl.glTexCoord2f(textureRight, textureTop);
		gl.glVertex3f(1.0f, 1.0f, -1.0f);

		// Bottom Face
		gl.glNormal3f(0.0f, -1.0f, 0.0f);
		gl.glTexCoord2f(textureRight, textureTop);
		gl.glVertex3f(-1.0f, -1.0f, -1.0f);
		gl.glTexCoord2f(textureLeft, textureTop);
		gl.glVertex3f(1.0f, -1.0f, -1.0f);
		gl.glTexCoord2f(textureLeft, textureBottom);
		gl.glVertex3f(1.0f, -1.0f, 1.0f);
		gl.glTexCoord2f(textureRight, textureBottom);
		gl.glVertex3f(-1.0f, -1.0f, 1.0f);

		// Right face
		gl.glNormal3f(1.0f, 0.0f, 0.0f);
		gl.glTexCoord2f(textureRight, textureBottom);
		gl.glVertex3f(1.0f, -1.0f, -1.0f);
		gl.glTexCoord2f(textureRight, textureTop);
		gl.glVertex3f(1.0f, 1.0f, -1.0f);
		gl.glTexCoord2f(textureLeft, textureTop);
		gl.glVertex3f(1.0f, 1.0f, 1.0f);
		gl.glTexCoord2f(textureLeft, textureBottom);
		gl.glVertex3f(1.0f, -1.0f, 1.0f);

		// Left Face
		gl.glNormal3f(-1.0f, 0.0f, 0.0f);
		gl.glTexCoord2f(textureLeft, textureBottom);
		gl.glVertex3f(-1.0f, -1.0f, -1.0f);
		gl.glTexCoord2f(textureRight, textureBottom);
		gl.glVertex3f(-1.0f, -1.0f, 1.0f);
		gl.glTexCoord2f(textureRight, textureTop);
		gl.glVertex3f(-1.0f, 1.0f, 1.0f);
		gl.glTexCoord2f(textureLeft, textureTop);
		gl.glVertex3f(-1.0f, 1.0f, -1.0f);

		gl.glEnd();

		// gl.glEnd();
		gl.glEndList();

		return cubeList;
	}

	public void loadTexture(GL2 gl) {
		// Load texture from image
		try {
			// Create a OpenGL Texture object from (URL, mipmap, file suffix)
			// Use URL so that can read from JAR and disk file.
			texture = TextureIO.newTexture(getClass().getClassLoader()
					.getResource(textureFileName), // relative to project root
					false, textureFileType);

			// Use linear filter for texture if image is larger than the
			// original texture
			gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
			// Use linear filter for texture if image is smaller than the
			// original texture
			gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);

			// Texture image flips vertically. Shall use TextureCoords class to
			// retrieve
			// the top, bottom, left and right coordinates, instead of using
			// 0.0f and 1.0f.
			TextureCoords textureCoords = texture.getImageTexCoords();
			
			textureTop = textureCoords.top();
			textureBottom = textureCoords.bottom();
			textureLeft = textureCoords.left();
			textureRight = textureCoords.right();

		} catch (GLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

    private FloatBuffer vertices;
    private ShortBuffer indices;
    private int VBOVertices;
    private int VBOIndices;
	float x = -0.5f; //the length/width/height of the cube
    
    public void initVBO(GL2 gl, String textureFileName, String textureFileType) {

    	float[] vertexArray = {
    			 0, 0, 0,
    			
    			-x, x, x,
    			 x, x, x,
    			-x,-x, x, 
    			 x,-x, x,
    			 
    			-x, x,-x,
    			 x, x,-x,
    			-x,-x,-x, 
    			 x,-x,-x,
    			 
    	};
		vertices = Buffers.newDirectFloatBuffer(vertexArray.length);
		vertices.put(vertexArray);
		vertices.flip();

		short[] indexArray = {
			1, 2, 4,
			1, 4, 3, 
			
			1, 7, 5,
			1, 3, 7,
			
			1, 5, 2, 
			5, 6, 2, 
			
			7, 8, 5,
			5, 8, 6,
			
			2, 6, 8,
			2, 8, 4,
			
			4, 8, 7,
			4, 7, 3,
		};
		indices = Buffers.newDirectShortBuffer(indexArray.length);
		indices.put(indexArray);
		indices.flip();
		
		FloatBuffer textureData = Buffers.newDirectFloatBuffer(12);
	    textureData.put(textureLeft);
	    textureData.put(textureRight);
	    textureData.put(textureBottom);
	    textureData.put(textureTop);
	    textureData.flip();

		int[] temp = new int[3];
		gl.glGenBuffers(3, temp, 0);

		VBOVertices = temp[0];
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, VBOVertices);
		gl.glBufferData(GL.GL_ARRAY_BUFFER, vertices.capacity()
				* Buffers.SIZEOF_FLOAT, vertices, GL.GL_STATIC_DRAW);
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);

		VBOIndices = temp[1];
		gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, VBOIndices);
		gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, indices.capacity()
				* Buffers.SIZEOF_SHORT, indices, GL.GL_STATIC_DRAW);
		gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, 0);
		
		vboTextureCoordHandle = temp[2];
	    gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, vboTextureCoordHandle);
	    gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, vboTextureCoordHandle, textureData, GL.GL_STATIC_DRAW);
	    gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, 0);
	    
	    shader = new Shader();
	}
	
    public void drawCubeVBO(GL2 gl, String textureFileName, String textureFileType) {
		
    	//shader
        gl.glEnable(GL.GL_TEXTURE_2D);           

        //I'm not sure if this belongs in this method or in initVBO
    	this.textureFileName = textureFileName;
		this.textureFileType = textureFileType;
		loadTexture(gl);
		texture.enable(gl);
		texture.bind(gl);
    	

		
    	gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
    	gl.glEnableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
    	
    	/* Setup Position Pointer */
    	gl.glBindBuffer    (GL.GL_ARRAY_BUFFER, VBOVertices);
    	gl.glVertexPointer (3, GL.GL_FLOAT, 0, 0);
    	
    	/* Setup Texture Coordinate Pointer */
    	gl.glBindBuffer      (GL.GL_ARRAY_BUFFER, vboTextureCoordHandle);
    	gl.glTexCoordPointer (2, GL.GL_FLOAT, 0, 0);
    	
        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, VBOIndices);
        gl.glDrawElements(GL.GL_TRIANGLES, indices.capacity(), GL.GL_UNSIGNED_SHORT, 0);
        
        gl.glDisableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
        gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
        
    }

	/**
	 * 
	 * This method lays out everything in the scene. In this case, it's only a
	 * cube and a sphere. If the objects have to be picked, they also have to be
	 * named too.
	 * 
	 * @param gl
	 *            the current GL
	 * @param displayList
	 *            an array of the display lists (of cubes with different
	 *            textures)
	 * @param cube
	 *            the position of the cube
	 * @param i
	 *            the texture that the cube should be
	 */
	public void drawScene(GL2 gl, int[] displayList, float[][] cube, int i,
			int y) {

		final int EVERYTHING = 0;
		final int CUBE = 1;
		final int CUBE2 = 2;
		final int CUBE3 = 3;
		final int SPHERE = 4;
		final int FLOOR = 5;

		gl.glLoadName(EVERYTHING);

		// cube1
		gl.glPushName(CUBE);
		gl.glPushMatrix();

		gl.glTranslatef(cube[0][0], cube[0][1], cube[0][2]);

		// draw the cube
		gl.glCallList(displayList[i]);

		gl.glPopMatrix();
		gl.glPopName();

		// //////////////////////////

		// cube2 (slightly larger cube)
		gl.glPushName(CUBE2);
		gl.glPushMatrix();

		gl.glScalef(1.25f, 1.25f, 1.25f);

		gl.glTranslatef(cube[1][0] - (cube[1][0] / 4), cube[1][1]
				- (cube[1][1] / 4), cube[1][2] - (cube[1][2] / 4));

		// draw the cube
		gl.glCallList(displayList[y]);

		gl.glPopMatrix();
		gl.glPopName();

		// //////////////////////////

		// cube3 (rectangle)
		gl.glPushName(CUBE3);
		gl.glPushMatrix();

		float scalex = 2f;
		float scaley = 3f;
		float scalez = 3f;
		gl.glScalef(scalex, scaley, scalez);

		gl.glTranslatef(cube[2][0] - (cube[2][0] / 4),// -(scalex*1.5f),
				cube[2][1] - (cube[2][1] / 4) - 0.25f,// -(scaley*1.5f),
				cube[2][2] - (cube[2][2] / 4) + (scalez * 1.5f) - 0.25f);

		// draw the cube
		gl.glCallList(displayList[3]);

		gl.glPopMatrix();
		gl.glPopName();

		// //////////////////////////

		// cube4 (almost the same as cube1)
		gl.glPushName(CUBE2);
		gl.glPushMatrix();

		gl.glTranslatef(cube[3][0], cube[3][1], cube[3][2]);

		// draw the cube
		gl.glCallList(displayList[y]);

		gl.glPopMatrix();
		gl.glPopName();

		// //////////////////////////

		// sphere
		gl.glPushName(SPHERE);
		gl.glPushMatrix();

		gl.glTranslatef(-2f, 0f, -4f);

		gl.glRotatef(tempRot, 0.5f, 0.5f, 0);

		// draw the sphere
		GLU glu = new GLU();
		GLUquadric quad = glu.gluNewQuadric();
		glu.gluSphere(quad, 2, 10, 15);
		glu.gluDeleteQuadric(quad);

		gl.glPopMatrix();
		gl.glPopName();
		
		// //////////////////////////

		// cube drawn with VBO
		gl.glPushName(SPHERE);
		gl.glPushMatrix();

		gl.glTranslatef(-5f, 0f, -4f);

		drawCubeVBO(gl, "terrainTextures/Layer Rock.jpeg", ".jpeg");

		gl.glPopMatrix();
		gl.glPopName();

		// //////////////////////////

		// floor
		gl.glPushName(FLOOR);
		gl.glPushMatrix();

		float scalex2 = 30f;
		float scaley2 = 30f;
		float scalez2 = 30f;
		float floorx = 0f;// -(scalex2*1.5f);
		float floory = -5 - (scaley2 * 1.5f);
		float floorz = 0f;// -(scalez2*1.5f);
		gl.glTranslatef(floorx - (floorx / 4), floory - (floory / 4), floorz
				- (floorz / 4));
		gl.glScalef(scalex2, scaley2, scalez2);

		gl.glCallList(displayList[4]);

		gl.glPopMatrix();
		gl.glPopName();
	}

	/**
	 * This is the main collision detection method. The other collision
	 * detection methods(hasCollided() and processCollision()) are only called
	 * from within this method.
	 * 
	 * Each box in the display list is made to be a 4f by 4f by 4f cube, so that
	 * is why there are many "4"s in these methods. Keep in mind when altering
	 * that the scale of a box is different from the length of each side. For
	 * example, when scaling a box to (1, 1, 1), the length of each side is (4,
	 * 4, 4).
	 * 
	 * @param movex
	 *            the X position of the user
	 * @param movey
	 *            the Y position of the user
	 * @param movez
	 *            the Z position of the user
	 * @param cube2Pos
	 *            the position of the collision box(s)
	 * @param moveSpeed
	 *            the same as the moveSpeed variable in Renderer
	 * @return the new 3D position of the user after they collided
	 */
	public float[] checkCollisions(float movex, float movey, float movez,
			float[][] cubePos, float moveSpeed) {

		this.moveSpeed = moveSpeed;
		float[] userPos = { -movex, -movey, -movez };
		float[] userCubeLength = { 4f, 8f, 4f };
		float[] cubeLength = { 4f, 4f, 4f };
		float[] cubeLength2 = { 5f, 5f, 5f };
		float[] cubeLength3 = { 4f * 2f, 4f * 3f, 4f * 3f };
		float[] floorPos = { 0f, -35f, 0f };
		float[] floorLength = { 4f * 30f, 4f * 30f, 4f * 30f };

		float[] out1 = new float[6]; // from hasCollided
		float[] out2 = new float[3]; // from ProcessCollision
		float[] out3 = new float[3]; // to be returned

		// ////////////////////////

		// cube1

		out1 = hasCollided(userPos, userCubeLength, cubePos[0], cubeLength);
		out2 = processCollision(movex, movey, movez, out1, cubePos[0],
				cubeLength, userCubeLength);
		if (out3[0] == 0)
			out3[0] += out2[0] - movex;
		if (out3[1] == 0)
			out3[1] += out2[1] - movey;
		if (out3[2] == 0)
			out3[2] += out2[2] - movez;

		// cube2
		out1 = hasCollided(userPos, userCubeLength, cubePos[1], cubeLength2);
		out2 = processCollision(movex, movey, movez, out1, cubePos[1],
				cubeLength2, userCubeLength);
		if (out3[0] == 0)
			out3[0] += out2[0] - movex;
		if (out3[1] == 0)
			out3[1] += out2[1] - movey;
		if (out3[2] == 0)
			out3[2] += out2[2] - movez;

		// cube3
		out1 = hasCollided(userPos, userCubeLength, cubePos[2], cubeLength3);
		out2 = processCollision(movex, movey, movez, out1, cubePos[2],
				cubeLength3, userCubeLength);
		if (out3[0] == 0)
			out3[0] += out2[0] - movex;
		if (out3[1] == 0)
			out3[1] += out2[1] - movey;
		if (out3[2] == 0)
			out3[2] += out2[2] - movez;

		// cube4
		out1 = hasCollided(userPos, userCubeLength, cubePos[3], cubeLength);
		out2 = processCollision(movex, movey, movez, out1, cubePos[3],
				cubeLength, userCubeLength);
		if (out3[0] == 0)
			out3[0] += out2[0] - movex;
		if (out3[1] == 0)
			out3[1] += out2[1] - movey;
		if (out3[2] == 0)
			out3[2] += out2[2] - movez;

		// floor
		out1 = hasCollided(userPos, userCubeLength, floorPos, floorLength);
		out2 = processCollision(movex, movey, movez, out1, floorPos,
				floorLength, userCubeLength);
		if (out3[0] == 0)
			out3[0] += out2[0] - movex;
		if (out3[1] == 0)
			out3[1] += out2[1] - movey;
		if (out3[2] == 0)
			out3[2] += out2[2] - movez;

		// /////////////////////////////////

		out3[0] += movex;
		out3[1] += movey;
		out3[2] += movez;
		return out3;
	}

	/**
	 * 
	 * Determines whether a cube has collided with another cube and which side
	 * it collided with.
	 * 
	 * @param cube1Pos
	 *            the 3D position of the first cube
	 * @param cube1Length
	 *            the length of the sides of the first cube
	 * @param cube2Pos
	 *            the 3D position of the second cube
	 * @param cube2Length
	 *            the length of the sides of the second cube
	 * @return An array with the respective reaction an object should take. If
	 *         the objects did not collide, there is no reaction.
	 */
	public float[] hasCollided(float[] cube1Pos, float[] cube1Length,
			float[] cube2Pos, float[] cube2Length) {

		float i = 0.05f;
		float[] out = new float[3];
		float diffx = Math.abs(cube1Pos[0] - cube2Pos[0]) - cube2Length[0] / 2
				- cube1Length[0] / 2;
		float diffy = Math.abs(cube1Pos[1] - cube2Pos[1]) - cube2Length[1] / 2
				- cube1Length[1] / 2;
		float diffz = Math.abs(cube1Pos[2] - cube2Pos[2]) - cube2Length[2] / 2
				- cube1Length[2] / 2;

		if (cube1Pos[0] < cube2Pos[0] + (cube2Length[0] / 4) + 0.75f
				+ (cube1Length[0] - 4)
				&& cube1Pos[0] > cube2Pos[0] - (cube2Length[0] / 4) - 0.75f
						- (cube1Length[0] - 4)
				&& cube1Pos[1] < cube2Pos[1] + (cube2Length[1] / 4)
						+ (cube1Length[1] - 4)
				&& cube1Pos[1] > cube2Pos[1] - (cube2Length[1] / 4)
						- (cube1Length[1] - 4)
				&& cube1Pos[2] < cube2Pos[2] + (cube2Length[2] / 4)
						+ (cube1Length[2] - 4)
				&& cube1Pos[2] > cube2Pos[2] - (cube2Length[2] / 4)
						- (cube1Length[2] - 4)) {
			boxX = true;
		}

		if (cube1Pos[0] < cube2Pos[0] + (cube2Length[0] / 4)
				+ (cube1Length[0] - 4)
				&& cube1Pos[0] > cube2Pos[0] - (cube2Length[0] / 4)
						- (cube1Length[0] - 4)
				&& cube1Pos[1] < cube2Pos[1] + (cube2Length[1] / 4) + 0.75f
						+ (cube1Length[1] - 4)
				&& cube1Pos[1] > cube2Pos[1] - (cube2Length[1] / 4) - 0.75f
						- (cube1Length[1] - 4)
				&& cube1Pos[2] < cube2Pos[2] + (cube2Length[2] / 4)
						+ (cube1Length[2] - 4)
				&& cube1Pos[2] > cube2Pos[2] - (cube2Length[2] / 4)
						- (cube1Length[2] - 4)) {
			boxY = true;
		}

		if (cube1Pos[0] < cube2Pos[0] + (cube2Length[0] / 4)
				+ (cube1Length[0] - 4)
				&& cube1Pos[0] > cube2Pos[0] - (cube2Length[0] / 4)
						- (cube1Length[0] - 4)
				&& cube1Pos[1] < cube2Pos[1] + (cube2Length[1] / 4)
						+ (cube1Length[1] - 4)
				&& cube1Pos[1] > cube2Pos[1] - (cube2Length[1] / 4)
						- (cube1Length[1] - 4)
				&& cube1Pos[2] < cube2Pos[2] + (cube2Length[2] / 4) + 0.75f
						+ (cube1Length[2] - 4)
				&& cube1Pos[2] > cube2Pos[2] - (cube2Length[2] / 4) - 0.75f
						- (cube1Length[2] - 4)) {
			boxZ = true;
		}

		/*
		 * //determine if the middle of the first cube collides with the second
		 * cube if((cube1Pos[0] < cube2Pos[0]
		 * +(cube2Length[0]/4)+0.75f+(cube1Length[0]-4)&& cube1Pos[0] >
		 * cube2Pos[0]-(cube2Length[0]/4)-0.75f-(cube1Length[0]-4)&& cube1Pos[1]
		 * < cube2Pos[1]+(cube2Length[1]/4)+0.75f+(cube1Length[1]-4)&&
		 * cube1Pos[1] >
		 * cube2Pos[1]-(cube2Length[1]/4)-0.75f-(cube1Length[1]-4)&& cube1Pos[2]
		 * < cube2Pos[2]+(cube2Length[2]/4)+0.75f+(cube1Length[2]-4)&&
		 * cube1Pos[2] >
		 * cube2Pos[2]-(cube2Length[2]/4)-0.75f-(cube1Length[2]-4))){
		 */
		/*
		 * //determine if the middle of the second cube collides with the first
		 * cube (cube2Pos[0] <
		 * cube1Pos[0]+(cube1Length[0]/4)+0.75f+(cube2Length[0]-4)&& cube2Pos[0]
		 * > cube1Pos[0]-(cube1Length[0]/4)-0.75f-(cube2Length[0]-4)&&
		 * cube2Pos[1] <
		 * cube1Pos[1]+(cube1Length[1]/4)+0.75f+(cube2Length[1]-4)&& cube2Pos[1]
		 * > cube1Pos[1]-(cube1Length[1]/4)-0.75f-(cube2Length[1]-4)&&
		 * cube2Pos[2] <
		 * cube1Pos[2]+(cube1Length[2]/4)+0.75f+(cube2Length[2]-4)&& cube2Pos[2]
		 * > cube1Pos[2]-(cube1Length[2]/4)-0.75f-(cube2Length[2]-4))){
		 */

		if (boxX || boxY || boxZ) {
			if (diffx > diffz && diffx > diffy) {
				if (cube1Pos[0] < cube2Pos[0])
					out[0] += i;
				else
					out[0] -= i;
			} else if (diffy > diffz && diffy > diffx) {
				if (cube1Pos[1] < cube2Pos[1])
					out[1] += i;
				else
					out[1] -= i;
			} else if (diffz > diffx && diffz > diffy) {
				if (cube1Pos[2] < cube2Pos[2])
					out[2] += i;
				else
					out[2] -= i;
			}
		}

		return out;
	}

	/**
	 * 
	 * This method processes the collision that the user had and reacts
	 * appropriately. The movex, movey, or movez is changed to make it look like
	 * you collided with the cube.
	 * 
	 * @param movex
	 *            the x position of the user
	 * @param movey
	 *            the y position of the user
	 * @param movez
	 *            the z position of the user
	 * @param out1
	 *            some simple data that can be used to determine which side of
	 *            the cube has been collided with.
	 * @param cubePos
	 *            The position of the cube that the user is colliding with.
	 * @param cubeLength
	 *            the width of the cube at each side.
	 * @return
	 */
	public float[] processCollision(float movex, float movey, float movez,
			float[] out1, float[] cubePos, float[] cubeLength,
			float[] cubeLengthTemp) {

		boolean[] collision = new boolean[6]; // this array corresponds
		if (out1[0] > 0f)
			collision[0] = true; // to the sides of the collision cube
		if (out1[0] < 0f)
			collision[1] = true;
		if (out1[1] > 0f)
			collision[2] = true;
		if (out1[1] < 0f)
			collision[3] = true;
		if (out1[2] > 0f)
			collision[4] = true;
		if (out1[2] < 0f)
			collision[5] = true;

		if (collision[0] || collision[1] || collision[2] || collision[3]
				|| collision[4] || collision[5]) {
			float xPlus = 0;
			float yPlus = 0;
			float zPlus = 0;

			if (boxX)
				xPlus = 0.75f;
			if (boxY)
				yPlus = 0.75f;
			if (boxZ)
				zPlus = 0.75f;

			// left side
			if (collision[0])
				movex = -cubePos[0] + (cubeLength[0] / 4)
						+ (cubeLengthTemp[0] - 4) + xPlus;
			// right side
			if (collision[1])
				movex = -cubePos[0] - (cubeLength[0] / 4)
						- (cubeLengthTemp[0] - 4) - xPlus;
			// bottom side
			if (collision[2])
				movey = -cubePos[1] + (cubeLength[1] / 4)
						+ (cubeLengthTemp[1] - 4) + yPlus;
			// top side
			if (collision[3])
				movey = -cubePos[1] - (cubeLength[1] / 4)
						- (cubeLengthTemp[1] - 4) - yPlus;
			// back side
			if (collision[4])
				movez = -cubePos[2] + (cubeLength[2] / 4)
						+ (cubeLengthTemp[2] - 4) + zPlus;
			// front side
			if (collision[5])
				movez = -cubePos[2] - (cubeLength[2] / 4)
						- (cubeLengthTemp[2] - 4) - zPlus;
		}
		float[] out = { movex, movey, movez };

		boxX = false;
		boxY = false;
		boxZ = false;
		return out;
	}

	/**
	 * 
	 * @param x
	 *            the X position of the sphere
	 * @param y
	 *            the Y position of the sphere
	 * @param z
	 *            the Z position of the sphere
	 */
	public void drawSphere(double x, double y, double z) {

		gl.glPushMatrix();

		gl.glTranslated(x, y, z);

		// Set material properties.
		float[] rgba = { 0f, 1f, 1f }; // light blue
		gl.glMaterialfv(GL.GL_FRONT, GL_AMBIENT, rgba, 0);
		gl.glMaterialfv(GL.GL_FRONT, GLLightingFunc.GL_DIFFUSE, rgba, 0);
		gl.glMaterialfv(GL.GL_FRONT, GL_SPECULAR, rgba, 0);
		gl.glMaterialf(GL.GL_FRONT, GL_SHININESS, 0.5f);

		// draw the sphere
		GLU glu = new GLU();
		GLUquadric quad = glu.gluNewQuadric();
		glu.gluSphere(quad, 0.5, 10, 15);
		glu.gluDeleteQuadric(quad);

		gl.glPopMatrix();
	}

	/**
	 * This method draws a box where the light should be.
	 * 
	 * @param gl
	 *            the current GL
	 * @param lightPos
	 *            the position of the Light
	 */
	public void testLight(GL2 gl, float[] lightPos) {
		// draw a cube where the light is
		gl.glPushMatrix();

		gl.glTranslatef(lightPos[0], lightPos[1], lightPos[2]);
		// gl.glScalef(10, 10, 1);
		gl.glCallList(displayList[0]);

		gl.glPopMatrix();
	}
}
