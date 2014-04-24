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
import java.nio.IntBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLException;
import javax.media.opengl.fixedfunc.GLLightingFunc;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureCoords;
import com.jogamp.opengl.util.texture.TextureIO;

/**
 * 
 * This class contains the methods that draw the scene, 
 * assign textures and materials of objects, and
 * check for collision detection.
 * 
 * @author Nathaniel Schultz
 *
 */
public class Scene {

	GL2 gl;
	int[] displayList;
	boolean terrainBuilt;

	// for testing rotation
	float tempRotX;
	float tempRot;
	
	//should be the same as moveSpeed in Renderer
	float moveSpeed = 2.0f;

	Texture texture;

	// Texture image flips vertically. Shall use TextureCoords class to retrieve
	// the top, bottom, left and right coordinates.
	private float textureTop;
	private float textureBottom;
	private float textureLeft;
	private float textureRight;
	
	float whitish[] = {0.8f, 0.8f, 0.8f, 1};
	float white[] = {1, 1, 1, 1};
	float blackish[] = {0.2f, 0.2f, 0.2f, 1};
	float black[] = {0, 0, 0, 1};

	private String textureFileName;
	private String textureFileType;
	
	float selectedObject;
	public boolean collided;

	private static float[][] boxColors = { // Bright: Red, Orange, Yellow,
			// Green, Blue
			{ 1.0f, 0.0f, 0.0f }, { 1.0f, 0.5f, 0.0f }, { 1.0f, 1.0f, 0.0f },
			{ 0.0f, 1.0f, 0.0f }, { 0.0f, 1.0f, 1.0f } };
	
	IntBuffer vertexArray = IntBuffer.allocate(1);

	/**
	 * This method returns a display list for a cube with a specific texture.
	 * Display lists can let you replace all the vertex calls for an object with one line.
	 * It's especially useful if you have to draw many of the same kind of primitive object.
	 * 
	 * @param gl				the current GL
	 * @param textureFileName	the path to the texture file
	 * @param textureFileType	the type of file that the texture file is 
	 * 							(.jpg, .png, etc)
	 * @return		the display list
	 */
	public int getCubeList(GL2 gl, String textureFileName,
			String textureFileType) {
		
		// Set material properties.
        float[] rgba = {1f, 1f, 1f};	//white
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
		
		
		gl.glMaterialfv(GL.GL_FRONT, GL_AMBIENT , new float[]{0.5f, 0.5f, 0.5f, 1.0f}, 0);
		gl.glMaterialfv(GL.GL_FRONT, GLLightingFunc.GL_DIFFUSE, white,0);
		gl.glMaterialfv(GL.GL_FRONT, GL_SPECULAR, white,0);
		gl.glMaterialfv(GL.GL_FRONT, GL_SHININESS, white,0);

		gl.glBegin(GL_QUADS); 
		
	      // Front Face
	      gl.glNormal3f(0.0f, 0.0f, 1.0f);
	      gl.glTexCoord2f(textureLeft, textureBottom);
	      gl.glVertex3f(-1.0f, -1.0f, 1.0f); // bottom-left of the texture and quad
	      gl.glTexCoord2f(textureRight, textureBottom);
	      gl.glVertex3f(1.0f, -1.0f, 1.0f);  // bottom-right of the texture and quad
	      gl.glTexCoord2f(textureRight, textureTop);
	      gl.glVertex3f(1.0f, 1.0f, 1.0f);   // top-right of the texture and quad
	      gl.glTexCoord2f(textureLeft, textureTop);
	      gl.glVertex3f(-1.0f, 1.0f, 1.0f);  // top-left of the texture and quad

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

		//gl.glEnd();
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
	
	/**
	 * 
	 * This method lays out everything in the scene. In this case, it's
	 * only a cube and a sphere. If the objects have to be picked, they
	 * also have to be named too.
	 * 
	 * @param gl			the current GL
	 * @param displayList	an array of the display lists 
	 * 						(of cubes with different textures)
	 * @param cube			the position of the cube
	 * @param i				the texture that the cube should be
	 */
	public void drawScene(GL2 gl, int[] displayList, float[][] cube, int i) {
		
		
		final int EVERYTHING = 0;
		final int CUBE = 1;
		final int CUBE2 = 2;
		final int SPHERE = 3;
		final int FLOOR = 4;
		
		gl.glLoadName(EVERYTHING);
		
		//cube1
		gl.glPushName(CUBE);
		gl.glPushMatrix();
		
		gl.glTranslatef(cube[0][0], cube[0][1], cube[0][2]);
		
		// draw the cube
		gl.glCallList(displayList[i]);
		
		gl.glPopMatrix();
		gl.glPopName();
		
		////////////////////////////
		
		//cube2
		gl.glPushName(CUBE2);
		gl.glPushMatrix();
		
		gl.glTranslatef(cube[1][0], cube[1][1], cube[1][2]);
		
		// draw the cube
		gl.glCallList(displayList[2]);
		
		gl.glPopMatrix();
		gl.glPopName();
		
		////////////////////////////
		
		//sphere
		gl.glPushName(SPHERE);
		gl.glPushMatrix();
		
		gl.glTranslatef(-2f, 0f, -4f);
		
		gl.glRotatef(tempRot, 0.5f, 0.5f, 0);
		
		//draw the sphere
		GLU glu = new GLU();
		GLUquadric quad = glu.gluNewQuadric();
		glu.gluSphere(quad, 2, 10, 15);
		glu.gluDeleteQuadric(quad);
		
		gl.glPopMatrix();
		gl.glPopName();
		
		////////////////////////////
		
		//floor
		gl.glPushName(FLOOR);
		gl.glPushMatrix();
		
		gl.glTranslatef(0f, -5f, 0f);
		gl.glScalef(30f, 1f, 30f);
		
		gl.glCallList(displayList[4]);
		
		gl.glPopMatrix();
		gl.glPopName();
	}
	
	
	/**
	 * This is the main collision detection method. The other collision
	 * detection methods(hasCollided() and processCollision()) are only 
	 * called from within this method.
	 * 
	 * @param movex		the X position of the user
	 * @param movey		the Y position of the user
	 * @param movez		the Z position of the user
	 * @param cube2Pos	the position of the collision box(s)
	 * @param moveSpeed	the same as the moveSpeed variable in Renderer
	 * @return			the new 3D position of the user after they collided
	 */
	public float[] checkCollisions(float movex, float movey, float movez, float[][] cubePos, float moveSpeed) {
		
		this.moveSpeed = moveSpeed;
		float[] userPos = { -movex, -movey, -movez };
		float[] userCubeScale = {4f, 8f, 4f};
		float[] cubeScale = {4f, 4f, 4f};
		float[] floorPos = {0f, -5f, 0f};
		float[] floorScale = {30f, 1f, 30f};
		
		float[] out1 = new float[6];	//from hasCollided
		float[] out2 = new float[3];	//from ProcessCollision
		float[] out3 = new float[3];	//to be returned
		
		//////////////////////////
		
		out1 = hasCollided(userPos, userCubeScale, cubePos[0], cubeScale);
		out2 = processCollision(movex, movey, movez, out1, cubePos[0], cubeScale);
		out3[0] += out2[0]-movex;
		out3[1] += out2[1]-movey;
		out3[2] += out2[2]-movez;
		
		
		out1 = hasCollided(userPos, userCubeScale, cubePos[1], cubeScale);
		out2 = processCollision(movex, movey, movez, out1, cubePos[1], cubeScale);
		out3[0] += out2[0]-movex;
		out3[1] += out2[1]-movey;
		out3[2] += out2[2]-movez;
		
		
		out1 = hasCollided(userPos, userCubeScale, floorPos, floorScale);
		out2 = processCollision(movex, movey, movez, out1, floorPos, floorScale);
		out3[0] += out2[0]-movex;
		out3[1] += out2[1]-movey;
		out3[2] += out2[2]-movez;
		
		///////////////////////////////////
		
		out3[0] += movex;
		out3[1] += movey;
		out3[2] += movez;
		return out3;
	}
	
	/**
	 * 
	 * Determines whether a cube has collided with another cube.
	 * 
	 * @param cube1Pos		the 3D position of the first cube
	 * @param cube1Scale	the length of the sides of the first cube
	 * @param cube2Pos		the 3D position of the second cube
	 * @param cube2Scale	the length of the sides of the second cube
	 * @return		An array with the respective reaction an object should take.
	 * 				If the objects did not collide, there is no reaction.
	 */
	public float[] hasCollided(float[] cube1Pos, float[] cube1Scale, 
			float[] cube2Pos, float[] cube2Scale) {
		
		float x = 0.05f;
		float[] out = new float[3];
		float diffx = cube1Pos[0] - cube2Pos[0];
		float diffy = cube1Pos[1] - cube2Pos[1];
		float diffz = cube1Pos[2] - cube2Pos[2];
				
														//You may only need one of the
														//two below depending on your situation.
		
		if((cube1Pos[0] < cube2Pos[0] + cube2Scale[0]/2 &&			//<--1.) determine if the middle
				cube1Pos[0] > cube2Pos[0] - cube2Scale[0]/2 &&		//of the first cube collides
				cube1Pos[1] < cube2Pos[1] + cube2Scale[1]/2 &&		//with the second cube.
				cube1Pos[1] > cube2Pos[1] - cube2Scale[1]/2 &&
				cube1Pos[2] < cube2Pos[2] + cube2Scale[2]/2 &&
				cube1Pos[2] > cube2Pos[2] - cube2Scale[2]/2) &&
				
			(cube2Pos[0] < cube1Pos[0] + cube1Scale[0]/2 &&		//<--2.) determine if the middle
				cube2Pos[0] > cube1Pos[0] - cube1Scale[0]/2 &&		//of the second cube collides
				cube2Pos[1] < cube1Pos[1] + cube1Scale[1]/2 &&		//with the first cube.
				cube2Pos[1] > cube1Pos[1] - cube1Scale[1]/2 &&
				cube2Pos[2] < cube1Pos[2] + cube1Scale[2]/2 &&
				cube2Pos[2] > cube1Pos[2] - cube1Scale[2]/2)) {
			
			if(Math.abs(diffx) > Math.abs(diffz) && Math.abs(diffx) > Math.abs(diffy)) {
				if(cube1Pos[0] < cube2Pos[0]) out[0] += x;
				else out[0] -= x;
			} else if(Math.abs(diffy) > Math.abs(diffz) && Math.abs(diffy) > Math.abs(diffx)) {
				if(cube1Pos[1] < cube2Pos[1]) out[1] += x;
				else out[1] -= x;
			} else if(Math.abs(diffz) > Math.abs(diffx) && Math.abs(diffz) > Math.abs(diffy)) {
				if(cube1Pos[2] < cube2Pos[2]) out[2] += x;
				else out[2] -= x;
			}
		}
			
		return out;
	}
	
	/**
	 * 
	 * This method processes the collision that the user had. The movex, movey,
	 * or movez is changed to make it look like you collided with the cube.
	 * 
	 * @param movex			the x position of the user
	 * @param movey			the y position of the user
	 * @param movez			the z position of the user
	 * @param out1			some simple data that can be used to determine which
	 * 						side of the cube has been collided with.		
	 * @param cubePos		The position of the cube that the user is colliding with.
	 * @param cubeLength	the width of the cube at each side. 
	 * @return
	 */
	public float[] processCollision(float movex, float movey, float movez, 
			float[] out1, float [] cubePos, float[] cubeLength) {
		
		
		boolean[] collision = new boolean[6];	//this array corresponds
		if(out1[0] > 0f) collision[0] = true;	//to the sides of the collision cube
		if(out1[0] < 0f) collision[1] = true;
		if(out1[1] > 0f) collision[2] = true;
		if(out1[1] < 0f) collision[3] = true;
		if(out1[2] > 0f) collision[4] = true;
		if(out1[2] < 0f) collision[5] = true;
		
		if(collision[0] || collision[1] || collision[2] || collision[3] ||
				collision[4] || collision[5]) {
			//left side
			if(collision[0]) 
				movex = cubePos[0]+cubeLength[0]/2-(cubeLength[0]/2*cubePos[0]);
			//right side
			if(collision[1]) 
				movex = cubePos[0]-cubeLength[0]/2-(cubeLength[0]/2*cubePos[0]);
			//bottom side
			if(collision[2]) 
				movey = cubePos[1]+cubeLength[1]/2-(cubeLength[1]/2*cubePos[1]);
			//top side
			if(collision[3]) 
				movey = cubePos[1]-cubeLength[1]/2-(cubeLength[1]/2*cubePos[1]);
			//back side
			if(collision[4]) 
				movez = cubePos[2]+cubeLength[2]/2-(cubeLength[2]/2*cubePos[2]);
			//front side
			if(collision[5]) 
				movez = cubePos[2]-cubeLength[2]/2-(cubeLength[2]/2*cubePos[2]);
		}
		float[] out = {movex, movey, movez};
		
		return out;
	}
	
	/**
	 * 
	 * @param x		the X position of the sphere
	 * @param y		the Y position of the sphere
	 * @param z		the Z position of the sphere
	 */
	public void drawSphere(double x, double y, double z) {
		
		gl.glPushMatrix();
		
		gl.glTranslated(x, y, z);
		
		// Set material properties.
        float[] rgba = {0f, 1f, 1f};	//light blue
        gl.glMaterialfv(GL.GL_FRONT, GL_AMBIENT, rgba, 0);
        gl.glMaterialfv(GL.GL_FRONT, GLLightingFunc.GL_DIFFUSE, rgba, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL_SPECULAR, rgba, 0);
        gl.glMaterialf(GL.GL_FRONT, GL_SHININESS, 0.5f);
		
		//draw the sphere
		GLU glu = new GLU();
		GLUquadric quad = glu.gluNewQuadric();
		glu.gluSphere(quad, 0.5, 10, 15);
		glu.gluDeleteQuadric(quad);
		
		gl.glPopMatrix();
	}

	/**
	 * This method draws a box where the light should be.
	 * 
	 * @param gl		the current GL
	 * @param lightPos	the position of the Light
	 */
	public void testLight(GL2 gl, float[] lightPos) {
		// draw a cube where the light is
		gl.glPushMatrix();

		gl.glTranslatef(lightPos[0], lightPos[1], lightPos[2]);
		//gl.glScalef(10, 10, 1);
		gl.glCallList(displayList[0]);

		gl.glPopMatrix();
	}
}
