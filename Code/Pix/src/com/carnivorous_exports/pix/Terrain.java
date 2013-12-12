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
	
	Quad place1;
	Quad place2;
	Quad place3;

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
		
		place1 = new Quad(12, 8, 12, 0.5f);
		place2 = new Quad(12, 8, 12, 0.5f);
		place3 = new Quad(12, 8, 12, 0.5f);
		
	}

	public void refreshTerrain(GL2 gl, int[] displayList) {

		/*
		for(int x = 0; x < 3; x++) {
			for(int y = 0; y < 3; y++) {
				refreshQuad(gl, displayList, coords.toArray(), 12, 8, 12 * x, 0, 0, 12 * y);
			}
		}
		*/
		
		
		place1.refreshQuad(gl, displayList, 12, 8, 12, 12, 0, -24);
		place2.refreshQuad(gl, displayList, 12, 8, 12, 0, 0, -24);
		place3.refreshQuad(gl, displayList, 12, 8, 12, -12, 0, -24);
		
		/*
		refreshQuad(gl, displayList, place1, 12, 8, 12, 0, 0, -12);
		refreshQuad(gl, displayList, place2, 12, 8, 12, 12, 0, -12);
		refreshQuad(gl, displayList, place3, 12, 8, 12, -12, 0, -12);
		
		refreshQuad(gl, displayList, place1, 12, 8, 12, 0, 0, -36);
		refreshQuad(gl, displayList, place2, 12, 8, 12, 12, 0, -36);
		refreshQuad(gl, displayList, place3, 12, 8, 12, -12, 0, -36);
		*/
		
		
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
