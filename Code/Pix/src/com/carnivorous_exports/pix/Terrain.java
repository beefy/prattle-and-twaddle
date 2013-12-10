package com.carnivorous_exports.pix;

import static javax.media.opengl.GL.GL_LINEAR;
import static javax.media.opengl.GL.GL_TEXTURE_2D;
import static javax.media.opengl.GL.GL_TEXTURE_MAG_FILTER;
import static javax.media.opengl.GL.GL_TEXTURE_MIN_FILTER;
import static javax.media.opengl.GL2.GL_COMPILE;
import static javax.media.opengl.GL2GL3.GL_QUADS;

import java.io.IOException;
import java.util.ArrayList;

import javax.media.opengl.GL2;
import javax.media.opengl.GLException;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureCoords;
import com.jogamp.opengl.util.texture.TextureIO;

public class Terrain {

	// for testing rotation
	float tempRotX;

	Texture texture;

	//boolean[][][] doesNotFit = new boolean[12][12][12]; // is false when
														// [x][y][z] space is
														// occupied
	//private boolean[][][][] placement = new boolean[4][12][12][12];
	private ArrayList[][] coords = new ArrayList[3][3];
	
	boolean[][][][] place1;
	boolean[][][][] place2;
	boolean[][][][] place3;

	private float[] treeSize;

	// Texture image flips vertically. Shall use TextureCoords class to retrieve
	// the top, bottom, left and right coordinates.
	private float textureTop;
	private float textureBottom;
	private float textureLeft;
	private float textureRight;

	private String textureFileName;
	private String textureFileType;

	private static float[][] boxColors = { // Bright: Red, Orange, Yellow,
			// Green, Blue
			{ 1.0f, 0.0f, 0.0f }, { 1.0f, 0.5f, 0.0f }, { 1.0f, 1.0f, 0.0f },
			{ 0.0f, 1.0f, 0.0f }, { 0.0f, 1.0f, 1.0f } };

	public int getCubeList(GL2 gl, String textureFileName,
			String textureFileType) {
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

		gl.glBegin(GL_QUADS);

		// Front Face
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
		gl.glTexCoord2f(textureRight, textureBottom);
		gl.glVertex3f(-1.0f, -1.0f, -1.0f);
		gl.glTexCoord2f(textureRight, textureTop);
		gl.glVertex3f(-1.0f, 1.0f, -1.0f);
		gl.glTexCoord2f(textureLeft, textureTop);
		gl.glVertex3f(1.0f, 1.0f, -1.0f);
		gl.glTexCoord2f(textureLeft, textureBottom);
		gl.glVertex3f(1.0f, -1.0f, -1.0f);

		// Top Face
		gl.glTexCoord2f(textureLeft, textureTop);
		gl.glVertex3f(-1.0f, 1.0f, -1.0f);
		gl.glTexCoord2f(textureLeft, textureBottom);
		gl.glVertex3f(-1.0f, 1.0f, 1.0f);
		gl.glTexCoord2f(textureRight, textureBottom);
		gl.glVertex3f(1.0f, 1.0f, 1.0f);
		gl.glTexCoord2f(textureRight, textureTop);
		gl.glVertex3f(1.0f, 1.0f, -1.0f);

		// Bottom Face
		gl.glTexCoord2f(textureRight, textureTop);
		gl.glVertex3f(-1.0f, -1.0f, -1.0f);
		gl.glTexCoord2f(textureLeft, textureTop);
		gl.glVertex3f(1.0f, -1.0f, -1.0f);
		gl.glTexCoord2f(textureLeft, textureBottom);
		gl.glVertex3f(1.0f, -1.0f, 1.0f);
		gl.glTexCoord2f(textureRight, textureBottom);
		gl.glVertex3f(-1.0f, -1.0f, 1.0f);

		// Right face
		gl.glTexCoord2f(textureRight, textureBottom);
		gl.glVertex3f(1.0f, -1.0f, -1.0f);
		gl.glTexCoord2f(textureRight, textureTop);
		gl.glVertex3f(1.0f, 1.0f, -1.0f);
		gl.glTexCoord2f(textureLeft, textureTop);
		gl.glVertex3f(1.0f, 1.0f, 1.0f);
		gl.glTexCoord2f(textureLeft, textureBottom);
		gl.glVertex3f(1.0f, -1.0f, 1.0f);

		// Left Face
		gl.glTexCoord2f(textureLeft, textureBottom);
		gl.glVertex3f(-1.0f, -1.0f, -1.0f);
		gl.glTexCoord2f(textureRight, textureBottom);
		gl.glVertex3f(-1.0f, -1.0f, 1.0f);
		gl.glTexCoord2f(textureRight, textureTop);
		gl.glVertex3f(-1.0f, 1.0f, 1.0f);
		gl.glTexCoord2f(textureLeft, textureTop);
		gl.glVertex3f(-1.0f, 1.0f, -1.0f);

		gl.glEnd();
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

	public void buildTerrain() {

		/*
		for(int x = 0; x < 3; x++) {
			for(int y = 0; y < 3; y++) {
				coords[x][y].add(initQuad(12, 8, 12, 0.8f));
			}
		}
		*/
		
			place1 = (initQuad(12, 8, 12, 0.8f, new boolean[12][12][12]));
			place2 = (initQuad(12, 8, 12, 0.8f, new boolean[12][12][12]));
			place3 = (initQuad(12, 8, 12, 0.8f, new boolean[12][12][12]));
		
	}

	public void refreshTerrain(GL2 gl, int[] displayList) {

		/*
		for(int x = 0; x < 3; x++) {
			for(int y = 0; y < 3; y++) {
				refreshQuad(gl, displayList, coords.toArray(), 12, 8, 12 * x, 0, 0, 12 * y);
			}
		}
		*/
		
		
		refreshQuad(gl, displayList, place1, 12, 8, 12, 0, 0, -24);
		refreshQuad(gl, displayList, place2, 12, 8, 12, 12, 0, -24);
		refreshQuad(gl, displayList, place3, 12, 8, 12, -12, 0, -24);
		
		/*
		refreshQuad(gl, displayList, place1, 12, 8, 12, 0, 0, -12);
		refreshQuad(gl, displayList, place2, 12, 8, 12, 12, 0, -12);
		refreshQuad(gl, displayList, place3, 12, 8, 12, -12, 0, -12);
		
		refreshQuad(gl, displayList, place1, 12, 8, 12, 0, 0, -36);
		refreshQuad(gl, displayList, place2, 12, 8, 12, 12, 0, -36);
		refreshQuad(gl, displayList, place3, 12, 8, 12, -12, 0, -36);
		*/
		
		
	}
	
	public boolean[][][][] initQuad(int topX, int topY, int topZ, float variety, boolean[][][] doesNotFit) {
		//topX, Y, and Z are the boundaries of the quad, when they are 12 it is a cube
		//variety is the variety of cube sizes: 
		//		0.0 is entirely size 1 cubes, 1.0 is entirely size 4 cubes
		
		//boolean[][][] doesNotFit = new boolean[12][12][12]; 	// is false when
												// [x][y][z] space is
												// occupied
		
		boolean[][][][] placement = new boolean[4][12][12][12];
		
		boolean fitting = true; // is true when the block will be placed

		for (int i = 4; i >= 1; i--) { // loop for each box size
			for (int x = 0; x < topX; x++) { // x coords
				for (int y = 0; y < topY; y++) { // y coords
					for (int z = 0; z < topZ; z++) { // z coords

						// randomize
						if (i != 1 && (float) Math.random() < variety) {
							fitting = false;
						} else {

							// determine if that area is occupied
							for (int x2 = (i - 1) + x; x2 >= x; x2--) {
								for (int y2 = (i - 1) + y; y2 >= y; y2--) {
									for (int z2 = (i - 1) + z; z2 >= z; z2--) {

										// this if statement lets some blocks
										// pop out of the top
										if (x2 < 12 && y2 < 12 && z2 < 12) {
											if (doesNotFit[x2][y2][z2])
												fitting = false;

										} else
											fitting = false;
									}
								}
							}

						}

						if (fitting) {

							// declare that area as occupied
							for (int x2 = (i - 1) + x; x2 >= x; x2--) {
								for (int y2 = (i - 1) + y; y2 >= y; y2--) {
									for (int z2 = (i - 1) + z; z2 >= z; z2--) {
										doesNotFit[x2][y2][z2] = true;
									}
								}
							}

							// remember placement
							placement[i - 1][x][y][z] = true;
						} //else placement[i - 1][x][y][z] = false;

						// reset fitting
						fitting = true;
					}
				}
			}
		}
		
		return placement;
	}
	
	public void refreshQuad(GL2 gl, int[] displayList, boolean[][][][] place, 
			int topX, int topY, int topZ, int posX, int posY, int posZ) {

		for (int i = 4; i >= 1; i--) { // loop for each box size
			for (int x = 0; x < topX; x++) { // x coords
				for (int y = 0; y < topY; y++) { // y coords
					for (int z = 0; z < topZ; z++) { // z coords

						// if a box is placed there
						if (place[i - 1][x][y][z]) {

							// draw the box
							gl.glPushMatrix();

							gl.glScalef(0.25f * i, 0.25f * i, 0.25f * i);

							// move to the coordinate
							if (i == 4) {
								gl.glTranslatef(.5f * (x + posX),
										.5f * (y + posY), .5f * (z + posZ));
							} else if (i == 3) {
								gl.glTranslatef(0.6666f * (x + posX) - 0.3333f,
										0.6666f * (y + posY) - 0.3333f,
										0.6666f * (z + posZ) - 0.3333f);
							} else if (i == 2) {
								gl.glTranslatef(1f * (x + posX) - 1f,
										1f * (y + posY) - 1f,
										1f * (z + posZ) - 1f);
							} else if (i == 1) {
								gl.glTranslatef(2f * (x + posX) - 3f,
										2f * (y + posY) - 3f,
										2f * (z + posZ) - 3f);
							}

							// draw the cube
							gl.glCallList(displayList[0]);

							gl.glPopMatrix();
						}
					}
				}
			}
		}
	}

	// remove posNum if numArrays works
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
