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
	
	//the collision reaction
	float reactionSpeed = 0.05f;
	
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
	 * @param i				the texture that the cube should be
	 */
	public void drawScene(GL2 gl, int[] displayList, int i) {
		
		
		//cube
		final int EVERYTHING = 0;
		final int CUBE = 1;
		final int SPHERE = 2;
		
		gl.glLoadName(EVERYTHING);
		
		gl.glPushName(CUBE);
		gl.glPushMatrix();
		
		//gl.glTranslatef(lightPos[0], lightPos[1], lightPos[2]);
		gl.glTranslatef(2,0,-4);
		
		//gl.glRotatef(180, 1, 0, 0);
		//gl.glRotatef(tempRot, 1.0f, 0.0f, 0);
		//tempRot++;
		
		// draw the cube
		gl.glCallList(displayList[i]);
		
		gl.glPopMatrix();
		gl.glPopName();
		
		//if (mode == GL2.GL_SELECT) 
			//gl.glPopName();
		
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
	}
	
	/**
	 * 
	 * Determines whether a cube has collided with another cube.
	 * 
	 * @param cube1			the 3D position of the first cube
	 * @param cube1Length	the length of one side of the first cube
	 * @param cube2			the 3D position of the second cube
	 * @param cube2Length	the length of one side of the second cube
	 * @return		An array with the respective reaction an object should take.
	 * 				If the objects did not collide, there is no reaction.
	 */
	public float[] hasCollided(float[] cube1, float cube1Length, float[] cube2, float cube2Length) {
		
		float[] out = new float[3];
		float diffx = cube1[0] - cube2[0];
		float diffy = cube1[1] - cube2[1];
		float diffz = cube1[2] - cube2[2];
		float cube1Length2 = cube1Length/2;
		float cube2Length2 = cube2Length/2;
		
		/*
		if(((cube1[0] > cube2[0] - cube2Length/2 && cube1[0] < cube2[0] + cube2Length/2)
				&& (cube1[1] > cube2[1] - cube2Length/2  && cube1[1] < cube2[1] + cube2Length/2)
				&& (cube1[2] > cube2[2] - cube2Length/2  && cube1[2] < cube2[2] + cube2Length/2))
			
				||
		
				((cube1[0] - cube2Length/2 > cube2[0] && cube1[0] + cube2Length/2 < cube2[0])
				&& (cube1[1] - cube2Length/2 > cube2[1] && cube1[1] + cube2Length/2 < cube2[1])
				&& (cube1[2] - cube2Length/2 > cube2[2] && cube1[2] + cube2Length/2 < cube2[2]))) {
		*/
		
														//You may only need one of the
														//two below depending on your situation.
		
		if((cube1[0] < cube2[0] + cube2Length2 &&		//<--1.) determine if the middle
				cube1[0] > cube2[0] - cube2Length2 &&	//of the first cube collides
				cube1[1] < cube2[1] + cube2Length2 &&	//with the second cube.
				cube1[1] > cube2[1] - cube2Length2 &&
				cube1[2] < cube2[2] + cube2Length2 &&
				cube1[2] > cube2[2] - cube2Length2) &&
				
			(cube2[0] < cube1[0] + cube1Length2 &&		//<--2.) determine if the middle
				cube2[0] > cube1[0] - cube1Length2 &&	//of the second cube collides
				cube2[1] < cube1[1] + cube1Length2 &&	//with the first cube.
				cube2[1] > cube1[1] - cube1Length2 &&
				cube2[2] < cube1[2] + cube1Length2 &&
				cube2[2] > cube1[2] - cube1Length2)) {
		
			//there is a problem here: when you ask for diffz before diffx,
			//the front side is perfect, but the right side is off. When you ask
			//for diffx before diffz, the right side is perfect, but the front side
			//is off.
			
			//Regardless, the right side moves back whereas the front side moves
			//perfectly when it collides correctly
			
			/*
			float x1 = diffx - diffz;
			float x2 = diffx - diffy;
			float z1 = diffz - diffx;
			float z2 = diffz - diffy;
			float y1 = diffy - diffx;
			float y2 = diffy - diffz;
			*/
			
			if(Math.abs(diffx) > Math.abs(diffz) && Math.abs(diffx) > Math.abs(diffy)) {
				if(cube1[0] < cube2[0]) {
					out[0] += reactionSpeed;
					System.out.println("LEFT");
				}
				else {
					out[0] -= reactionSpeed;
					System.out.println("RIGHT");
				}
			} else if(Math.abs(diffy) > Math.abs(diffz) && Math.abs(diffy) > Math.abs(diffx)) {
				if(cube1[1] < cube2[1]) out[1] += reactionSpeed;
				else out[1] -= reactionSpeed;
			} else if(Math.abs(diffz) > Math.abs(diffx) && Math.abs(diffz) > Math.abs(diffy)) {
				if(cube1[2] < cube2[2]) out[2] += reactionSpeed;
				else out[2] -= reactionSpeed;
			}
			
			/*
			if(cube1[0] > cube2[0] && diffx > diffz && diffx > diffy) {
				out[0] -= reactionSpeed;
			}
			
			if(cube1[1] > cube2[1] && diffy > diffz && diffy > diffx) {
				out[1] -= reactionSpeed;
			}
			
			if(cube1[2] > cube2[2] && diffz > diffx && diffz > diffy) {
				out[2] -= reactionSpeed;
			}
			*/
			
			/*
			if(diffx > diffz && diffx > diffy) {
				out[0] -= reactionSpeed;
			}
			
			//if(diffx < diffz && diffx < diffy) {
			//	out[0] += reactionSpeed;
			//}
			
			if(diffz > diffx && diffz > diffy) {
				out[2] -= reactionSpeed;
			}
			
			//if(diffz < diffx && diffz < diffy){
			//	out[2] += reactionSpeed;
			//}
			
			if(diffy > diffz && diffy > diffx) {
				out[1] -= reactionSpeed;
			}
			
			//if(diffy < diffz && diffy < diffx) {
			//	out[1] += reactionSpeed;
			//}
			*/
			
			
			/*
			if(cube1[0] > cube2[0] && diffx > diffy && diffx > diffz &&  		//collided with x
					diffy < cube2Length2 && diffz < cube2Length2) {
				out[0] += reactionSpeed;
				System.out.println("right side");
			//} else if(cube1[0] < cube2[0] && diffx > diffy && diffx > diffz &&
			} else if(cube1[0] < cube2[0] && diffx > diffy && diffx > diffz &&
					diffy < cube2Length2 && diffz < cube2Length2) {
				out[0] -= reactionSpeed;
				System.out.println("left side");
			} else if(cube1[1] > cube2[1] && diffy > diffx && diffy > diffz && //collided with y
					diffx < cube2Length2 && diffz < cube2Length2) {
				out[1] += reactionSpeed;
				System.out.println("bottom side");
			} else if(cube1[1] < cube2[1] && diffy > diffx && diffy > diffz && 
					diffx < cube2Length2 && diffz < cube2Length2) {
				out[1] -= reactionSpeed;
				System.out.println("top side");
			} else if(cube1[2] > cube2[2] && diffz > diffy && diffz > diffx && //collided with z
					diffy < cube2Length2 && diffx < cube2Length2) {
				out[2] += reactionSpeed;
				System.out.println("front side");
			} else if(cube1[2] < cube2[2] && diffz > diffy && diffz > diffx &&  
					diffy < cube2Length2 && diffx < cube2Length2) {
				out[2] -= reactionSpeed;
				System.out.println("back side");
			}
			*/
		}
			
		return out;
	}
	
	/**
	 * 
	 * @param movex		the X position of the user
	 * @param movey		the Y position of the user
	 * @param movez		the Z position of the user
	 * @param moveSpeed	the same as the moveSpeed variable in Renderer
	 * @return			the new 3D position of the user after they collided
	 */
	public float[] checkCollisions(float movex, float movey, float movez, float moveSpeed) {
		
		this.moveSpeed = moveSpeed;
		float[] cube1 = { -movex, -movey, -movez };
		float[] cube2 = { 2,0,-4 };
		
		float cube2Length = 4f;
		
		float[] out1 = hasCollided(cube1, 4f, cube2, cube2Length);
		//movex += out1[0];
		//movey += out1[1];
		//movez += out1[2];
		
		boolean[] collision = {false, false, false, false, false, false};
		if(out1[0] > 0f) collision[0] = true;
		if(out1[0] < 0f) collision[1] = true;
		if(out1[1] > 0f) collision[2] = true;
		if(out1[1] < 0f) collision[3] = true;
		if(out1[2] > 0f) collision[4] = true;
		if(out1[2] < 0f) collision[5] = true;
			
		
		float[] out2 = processHits(movex, movey, movez, collision, cube2, cube2Length/2);
		
		float[] out = {out2[0], out2[1], out2[2]};
		return out;
	}
	
	// A^2 + B^2 = C^2 solving for A\
	public float[] processHits(float movex, float movey, float movez, 
			boolean[] collided, float [] cubePos, float cubeLength) {
		
		//moves back too far
		//System.out.println("collision x");
		//movex = (float) Math.sqrt(Math.pow(moveSpeed, 2) - Math.pow(reactionSpeed, 2));
		
		
		if(collided[0]) {
			movex = cubePos[0]+cubeLength-(cubeLength*cubePos[0]);
			System.out.println("Left side");
		}
		
		if(collided[1]) {
			movex = cubePos[0]-cubeLength-(cubeLength*cubePos[0]);
			System.out.println("Right side");
		}
		
		if(collided[2]) {
			movey = cubePos[1]+cubeLength-(cubeLength*cubePos[1]);
			System.out.println("Bottom side");
		}
		
		if(collided[3]) {
			movey = cubePos[1]-cubeLength-(cubeLength*cubePos[1]);
			System.out.println("Top side");
		}
		
		if(collided[4]) {
			movez = cubePos[2]+cubeLength-(cubeLength*cubePos[2]);
			System.out.println("Back side");
		}
		
		if(collided[5]) {
			movez = cubePos[2]-cubeLength-(cubeLength*cubePos[2]);
			System.out.println("Front side");
		}
		
		
		/*
		if(collidedy) {
			//System.out.println("collision y");
			//movey = (float) Math.sqrt(Math.pow(moveSpeed, 2) - Math.pow(reactionSpeed, 2));
			movey = cubePos[1];
		}
		
		if(collidedz) {
			//sometimes z collisions are thrown when they should be x collisions
			//System.out.println("collision z");
			//movez = (float) Math.sqrt(Math.pow(moveSpeed, 2) - Math.pow(reactionSpeed, 2));
			movez = cubePos[2];
		}
		*/
		
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
