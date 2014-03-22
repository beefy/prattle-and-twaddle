package com.carnivorous_exports.pix;

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
import java.util.ArrayList;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL3;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLException;
import javax.media.opengl.GLRunnable;
import javax.media.opengl.fixedfunc.GLLightingFunc;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureCoords;
import com.jogamp.opengl.util.texture.TextureIO;

public class Scene {

	GL2 gl;
	int[] displayList;
	boolean terrainBuilt;

	// for testing rotation
	float tempRotX;
	float tempRot;

	Texture texture;

	private int xLength = 1;
	private int yLength = 1;
	private Quad[][] coords = new Quad[xLength][yLength];

	private float[] treeSize;

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
		int cubeDList = base;

		gl.glNewList(cubeDList, GL_COMPILE);

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

		return cubeDList;
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
	
	
	public void vertexArrayBuild(GL2 gl) {
		
		/*
		int vertices = 3;

		int vertex_size = 3; // X, Y, Z,
		int color_size = 3; // R, G, B,

		IntBuffer vertex_data = Buffers.newDirectIntBuffer(vertices * vertex_size);
		vertex_data.put(new int[] { -1, -1, 0, });
		vertex_data.put(new int[] { 1, -1, 0, });
		vertex_data.put(new int[] { 1, 1, 0, });
		vertex_data.flip();

		FloatBuffer color_data = Buffers.newDirectFloatBuffer(vertices * color_size);
		color_data.put(new float[] { 1f, 0f, 0f, });
		color_data.put(new float[] { 0f, 1f, 0f, });
		color_data.put(new float[] { 0f, 0f, 1f, });
		color_data.flip();

		int vbo_vertex_handle = gl.glGenBuffers(0, vertex_data);
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, vbo_vertex_handle);
		gl.glBufferData(GL2.GL_ARRAY_BUFFER, vbo_vertex_handle, vertex_data, GL2.GL_STATIC_DRAW);
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);

		int vbo_color_handle = gl.glGenBuffers();
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, vbo_color_handle);
		gl.glBufferData(GL2.GL_ARRAY_BUFFER, vbo_color_handle, color_data, GL2.GL_STATIC_DRAW);
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);
		
		
		
		// Create Vertex Array.
		gl.glGenVertexArrays(1, vertexArray);
		gl.glBindVertexArray(vertexArray.get(0));

		// Specify how data should be sent to the Program.

		// VertexAttribArray 0 corresponds with location 0 in the vertex shader.
		gl.glEnableVertexAttribArray(0);
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, buffers.get(0));
		gl.glVertexAttribPointer(0, 2, GL.GL_FLOAT, false, 0, 0);

		// VertexAttribArray 1 corresponds with location 1 in the vertex shader.
		gl.glEnableVertexAttribArray(1);
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, buffers.get(1));
		gl.glVertexAttribPointer(1, 3, GL.GL_FLOAT, false, 0, 0);
		*/
	}
	
	public void vertexArrayDraw() {
		
	}
	
	public void testLightCube(GL2 gl, int[] displayList, float[] lightPos, int i) {
		
		
		//if(mode == GL2.GL_SELECT) gl.glRenderMode(GL2.GL_RENDER);
		//if (mode == GL2.GL_SELECT) gl.glLoadName(0);
		//if (mode == GL2.GL_SELECT) 
		//	gl.glPushName(1);
		//cube
		final int EVERYTHING = 0;
		final int CUBE = 1;
		final int SPHERE = 2;
		
		gl.glLoadName(EVERYTHING);
		
		gl.glPushName(CUBE);
		gl.glPushMatrix();
		
		//gl.glTranslatef(lightPos[0], lightPos[1], lightPos[2]);
		gl.glTranslatef(2f, 0f, -4f);
		
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
	
	public float[] hasCollided(float[] cube1, float cube1Length, float[] cube2, float cube2Length) {
		
		
		/*
		 * out = 1 --> right side collision
		 *  =2 --> left side collision
		 *  =3 --> top collision
		 *  =4 --> bottom collision
		 *  =5 --> front collision
		 *  =6 --> back collision
		 */
		float[] out = new float[3];
		int num = -1;
		
		/*
			if(cube1[0] > cube2[0] - cube2Length && cube1[0] < cube2[0] + cube2Length && cube1[2] > cube2[2] - cube2Length
					&& cube1[2] > cube2[2] - cube2Length  && cube1[2] < cube2[2] + cube2Length)
				//front collided
				num = 5;
			if(cube1[0] > cube2[0] - cube2Length && cube1[0] < cube2[0] + cube2Length && cube1[2] > cube2[2] + cube2Length
					&& cube1[2] > cube2[2] - cube2Length  && cube1[2] < cube2[2] + cube2Length)
				//back collided
				num = 6;
			
			if(cube1[2] > cube2[2] - cube2Length  && cube1[2] < cube2[2] + cube2Length && cube1[0] > cube2[0] - cube2Length
					&& cube1[0] > cube2[0] - cube2Length && cube1[0] < cube2[0] + cube2Length)
				//right collided
				num = 1;
			if(cube1[2] > cube2[2] - cube2Length  && cube1[2] < cube2[2] + cube2Length && cube1[0] > cube2[0] + cube2Length
					&& cube1[0] > cube2[0] - cube2Length && cube1[0] < cube2[0] + cube2Length)
				//left collided
				num = 2;
			*/
			
			//if(cube1[0] > cube2[0] - cube2Length && cube1[0] < cube2[0] + cube2Length )
			
			
			if(cube2[0]-cube2Length < cube1[0] && cube2[0]+cube2Length > cube1[0]
					&& cube2[1]-cube2Length < cube1[1] && cube2[1]+cube2Length > cube1[1]
					&& cube2[2]-cube2Length < cube1[2]) {
				
				out[0] -= 0.1f;
				
			} else if(cube2[0]-cube2Length < cube1[0] && cube2[0]+cube2Length > cube1[0]
					&& cube2[1]-cube2Length < cube1[1] && cube2[1]+cube2Length > cube1[1]
					&& cube2[2]+cube2Length < cube1[2]) {
				
				out[0] += 0.1f;
				
			} else if(cube2[2]-cube2Length < cube1[2] && cube2[2]+cube2Length > cube1[2]
					&& cube2[1]-cube2Length < cube1[1] && cube2[1]+cube2Length > cube1[1]
					&& cube2[0]-cube2Length < cube1[0]) {
				
				out[2] -= 0.1f;
				
			} else if(cube2[2]-cube2Length < cube1[2] && cube2[2]+cube2Length > cube1[2]
					&& cube2[1]-cube2Length < cube1[1] && cube2[1]+cube2Length > cube1[1]
					&& cube2[0]+cube2Length < cube1[0]) {
				
				out[2] += 0.1f;
				
			} else if(cube2[2]-cube2Length < cube1[2] && cube2[2]+cube2Length > cube1[2]
					&& cube2[0]-cube2Length < cube1[0] && cube2[0]+cube2Length > cube1[0]
					&& cube2[1]-cube2Length < cube1[1]) {
				
				out[1] -= 0.1f;
				
			} else if(cube2[2]-cube2Length < cube1[2] && cube2[2]+cube2Length > cube1[2]
					&& cube2[0]-cube2Length < cube1[0] && cube2[1]+cube2Length > cube1[0]
					&& cube2[1]+cube2Length < cube1[1]) {
				
				out[1] -= 0.1f;
				
			}
		
			
			//collision between 2 cubes
			/*
			if(((cube1[0] > cube2[0] - cube2Length && cube1[0] < cube2[0] + cube2Length)
				&& (cube1[1] > cube2[1] - cube2Length  && cube1[1] < cube2[1] + cube2Length)
				&& (cube1[2] > cube2[2] - cube2Length  && cube1[2] < cube2[2] + cube2Length))
			
				||
		
				((cube1[0] - cube2Length > cube2[0] && cube1[0] + cube2Length < cube2[0])
				&& (cube1[1] - cube2Length > cube2[1] && cube1[1] + cube2Length < cube2[1])
				&& (cube1[2] - cube2Length > cube2[2] && cube1[2] + cube2Length < cube2[2])))
				out = true;
			*/
			
		return out;
	}
	
	public float[] checkCollisions(float movex, float movey, float movez, 
			float oldmovex, float oldmovey, float oldmovez) {
		
		//just adding to the movex, movey or movez to simulate sliding along the side?
		
		float[] cube1 = { -movex, -movey, -movez };
		float[] cube2 = { 2f, 0f, -4f };
		
		int topX = 12;
		int topY = 8;
		int topZ = 12;
		
		// position of this Quad
		int posX = 12 * 0;
		int posY = 2 * 0 + 2 * 0; // to make a hill
		int posZ = 12 * 0 - 40;

		boolean collision = false;
		
		for (float i = 4; i >= 1; i--) { // loop for each box size
			for (int x = 0; x < topX; x++) { // x coords
				for (int y = 0; y < topY; y++) { // y coords
					for (int z = 0; z < topZ; z++) { // z coords

						// if a box is placed there
						if (coords[0][0].placement[(int)(i - 1)][x][y][z]) {
							
							float q = 2/i;
							float[] in = {(float)(q*(x+posX) - (q*(i-4)*-0.5)), 
									(float)(q*(y+posY) - (q*(i-4)*-0.5)), 
									(float)(q*(z+posZ) - (q*(i-4)*-0.5))};
							float[] out = hasCollided(in, 0.25f * i, cube1, 2f);
								//collision = true;
							movex += out[0];
							movey += out[1];
							movez += out[2];
						}
					}
				}
			}
		}
		
		/*
		if(!collision) {
			oldmovex = movex;
			oldmovey = movey;
			oldmovez = movez;
		} else {
			movex = oldmovex;
			movey = oldmovey;
			movez = oldmovez;
		}
		*/
		
		/*
		if (!hasCollided(cube1, 1.5f, cube2, 2f)) {
			
			oldmovex = movex;
			oldmovey = movey;
			oldmovez = movez;
		} else {

			movex = oldmovex;
			movey = oldmovey;
			movez = oldmovez;
		}
		*/
		
		float[] out = {movex, movey, movez, oldmovex, oldmovey, oldmovez};
		return out;
	}
	
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

	public void buildScene(GLAutoDrawable drawable, Renderer renderer,
			GL2 gl, int[] displayList) {

		this.gl = gl;
		this.displayList = displayList;

		for (int x = 0; x < xLength; x++) {
			for (int y = 0; y < yLength; y++) {
				coords[x][y] = new Quad(drawable, renderer, 12, 8, 12, 0.5f,  //0.5f
						gl, displayList, x, y);
			}
		}

		// terrainBuilt = true;
		System.out.println("terrain built");
	}

	public void drawScene(GL2 gl) {
		//this.selectedObject = selectedObject;
		for (int x = 0; x < xLength; x++) {
			for (int y = 0; y < yLength; y++) {
				coords[x][y].refreshQuad(gl, selectedObject);
				if(coords[x][y].collided) collided = true;
			}
		}
		
		for (int x = 0; x < xLength; x++) {
			for (int y = 0; y < yLength; y++) {
				coords[x][y].collided = false;
			}
		}
	}

	public void testLight(GL2 gl, float[] lightPos) {
		// draw a cube where the light is
		gl.glPushMatrix();

		gl.glTranslatef(lightPos[0], lightPos[1], lightPos[2]);
		//gl.glScalef(10, 10, 1);
		gl.glCallList(displayList[0]);

		gl.glPopMatrix();
	}

	public void makeTree(GL2 gl, int[] displayList, double branchOdds, float x,
			float y, float z) {
		if (treeSize == null) {
			treeSize = new float[1];
			treeSize[0] = (float) Math.random() * 5f + 5f;
		}

		for (int y2 = (int) y; y2 < treeSize[0]; y2++) {

			if (Math.random() < branchOdds)
				makeTree(gl, displayList, branchOdds - 0.1, x - 2f - 3f, y2, z);
			if (Math.random() < branchOdds)
				makeTree(gl, displayList, branchOdds - 0.1, x + 2f - 3f, y2, z);
			if (Math.random() < branchOdds)
				makeTree(gl, displayList, branchOdds - 0.1, x, y2, z - 2f - 3f);
			if (Math.random() < branchOdds)
				makeTree(gl, displayList, branchOdds - 0.1, x, y2, z + 2f - 3f);

			gl.glPushMatrix();

			gl.glScalef(0.25f, 0.25f, 0.25f);

			gl.glTranslatef(x, y + 2f * y2 - 3f, z);

			// draw the cube
			gl.glCallList(displayList[0]);

			gl.glPopMatrix();

		}
	}
}
