package com.carnivorous_exports.pix;

import static javax.media.opengl.GL.GL_LINEAR;
import static javax.media.opengl.GL.GL_TEXTURE_2D;
import static javax.media.opengl.GL.GL_TEXTURE_MAG_FILTER;
import static javax.media.opengl.GL.GL_TEXTURE_MIN_FILTER;
import static javax.media.opengl.GL2.GL_COMPILE;
import static javax.media.opengl.GL2GL3.GL_QUADS;

import java.io.IOException;

import javax.media.opengl.GL2;
import javax.media.opengl.GLException;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureCoords;
import com.jogamp.opengl.util.texture.TextureIO;

public class Terrain {

	// for testing rotation
	float tempRotX;

	Texture texture;

	private float[][] randArr;
	private float[][][][] randArr2;
	private boolean[][][][][] sizeArr = new boolean[10][4][12][12][12];

	private float[] treeSize;

	private boolean positionRecorded = false;

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

	public void buildTerrain(GL2 gl, int[] displayList, String scene) {

		if (scene == "checkTextures") {
			for (int i = 0; i < displayList.length; i++) {

				gl.glPushMatrix();

				gl.glTranslatef(i * 3f, 0.0f, -6.0f);

				// gl.glColor3fv(boxColors[2], 0);

				gl.glCallList(displayList[i]); // draw the cube
				gl.glPopMatrix();
			}
		} else if (scene == "checkFitting") {

			boolean[][] doesFit = new boolean[12][12];

			for (int i = 4; i >= 1; i--) { // loop for each box size
				for (int x = 0; x < 12; x++) { // x coords
					for (int y = 0; y < 12; y++) { // y coords

						boolean fitting = true;

						// to check for fitting
						// x2 and y2 are the width and height of the cube
						for (int x2 = (i - 1) + x; x2 >= x; x2--) {
							for (int y2 = (i - 1) + y; y2 >= y; y2--) {
								if (x2 < 12 && y2 < 12) {
									if (doesFit[x2][y2])
										fitting = false;
								} else
									fitting = false;
							}
						}

						if (fitting) {

							gl.glPushMatrix();

							gl.glScalef(0.25f * i, 0.25f * i, 0.25f * i);

							// move to the coordinate
							// old attempts:
							// gl.glTranslatef(0.25f * x, 0.25f * y, -6.0f - i);
							// gl.glTranslatef(((6-i)* 0.25f) * x, ((6-i)*
							// 0.25f) * y, -6.0f);
							if (i == 4) {
								gl.glTranslatef(.5f * x, .5f * y, -6.0f);
							} else if (i == 3) {
								gl.glTranslatef(0.666f * x - 0.33f,
										0.666f * y - 0.33f, -6.0f + 0.25f);
							} else if (i == 2) {
								gl.glTranslatef(1f * x - 1f, 1f * y - 1f,
										-6.0f - 0.15f);
							} else if (i == 1) {
								gl.glTranslatef(2f * x - 3f, 2f * y - 3f,
										-6.0f - 3.3f);
							}

							// draw the cube
							gl.glCallList(displayList[i]);

							// declare that area as occupied
							for (int x2 = (i - 1) + x; x2 >= x; x2--) {
								for (int y2 = (i - 1) + y; y2 >= y; y2--) {
									doesFit[x2][y2] = true;
								}
							}

							gl.glPopMatrix();
						}
					}
				}

				// reset doesFit, to check for fitting
				for (int x = 0; x < 12; x++) {
					for (int y = 0; y < 12; y++) {
						doesFit[x][y] = false;
					}
				}

			}
		} else if (scene == "checkQuad2D") {

			if (randArr == null)
				randArrInit();

			boolean[][] doesFit = new boolean[12][12];

			for (int i = 4; i >= 1; i--) { // loop for each box size
				for (int x = 0; x < 12; x++) { // x coords
					for (int y = 0; y < 12; y++) { // y coords

						boolean fitting = true;

						// to check for fitting
						// x2 and y2 are the width and height of the cube
						for (int x2 = (i - 1) + x; x2 >= x; x2--) {
							for (int y2 = (i - 1) + y; y2 >= y; y2--) {
								if (x2 < 12 && y2 < 12) {

									// determines if area is occupied
									if (doesFit[x2][y2])
										fitting = false;

									// randomizes
									if (i != 1 && randArr[x2][y2] < 0.1)
										fitting = false;
								} else
									fitting = false;
							}
						}

						if (fitting) {

							gl.glPushMatrix();

							gl.glScalef(0.25f * i, 0.25f * i, 0.25f * i);

							// move to the coordinate
							if (i == 4) {
								gl.glTranslatef(.5f * x, .5f * y, -6.0f);
							} else if (i == 3) {
								gl.glTranslatef(0.666f * x - 0.33f,
										0.666f * y - 0.33f, -8.333f);
							} else if (i == 2) {
								gl.glTranslatef(1f * x - .5f - 0.50f,
										1f * y - 1f, -13.0f);
							} else if (i == 1) {
								gl.glTranslatef(2f * x - .75f - 2.25f,
										2f * y - 3f, -27.0f);
							}

							// draw the cube
							gl.glCallList(displayList[0]);

							// declare that area as occupied
							for (int x2 = (i - 1) + x; x2 >= x; x2--) {
								for (int y2 = (i - 1) + y; y2 >= y; y2--) {
									doesFit[x2][y2] = true;
								}
							}

							gl.glPopMatrix();
						}
					}
				}
			}
		} else if (scene == "checkQuad3D") {

			randArrInit();
			sizeArrInit(9, 1, gl, displayList, 12f, 0f, -24f, 12, 6, 12);

			buildQuad(0, gl, displayList, 12f, 0f, -24f, 12, 6, 12);

			buildQuad(1, gl, displayList, -12f, 0f, -24f, 12, 6, 12);
			buildQuad(2, gl, displayList, 0f, 0f, -24f, 12, 6, 12);

			buildQuad(3, gl, displayList, 12f, 0f, -12f, 12, 6, 12);
			buildQuad(4, gl, displayList, -12f, 0f, -12f, 12, 6, 12);
			buildQuad(5, gl, displayList, 0f, 0f, -12f, 12, 6, 12);

			buildQuad(6, gl, displayList, 12f, 0f, -36f, 12, 6, 12);
			buildQuad(7, gl, displayList, -12f, 0f, -36f, 12, 6, 12);
			buildQuad(8, gl, displayList, 0f, 0f, -36f, 12, 6, 12);

		} else if (scene == "checkTree") {

			makeTree(gl, displayList, 0.2, 0f, 0f, -6f);

		}
	}

	public void randArrInit() {

		randArr = new float[12][12];
		for (int x = 0; x < 12; x++) {
			for (int y = 0; y < 12; y++) {
				randArr[x][y] = (float) Math.random();
			}
		}

		randArr2 = new float[9][12][12][12];
		for (int pos = 0; pos < 9; pos++) {
			for (int x = 0; x < 12; x++) {
				for (int y = 0; y < 12; y++) {
					for (int z = 0; z < 12; z++) {
						randArr2[pos][x][y][z] = (float) Math.random();
					}
				}
			}
		}
	}

	public void buildQuad(int posNum, GL2 gl, int[] displayList, float posX,
			float posY, float posZ, int topX, int topY, int topZ) {
		// when topX,Y,Z are all 12, it is a large cube (made of cubes)

		boolean[][][] doesFit = new boolean[12][12][12];

		for (int i = 4; i >= 1; i--) { // loop for each box size
			for (int x = 0; x < topX; x++) { // x coords
				for (int y = 0; y < topY; y++) { // y coords
					for (int z = 0; z < topZ; z++) { // z coords

						if (sizeArr[posNum][i - 1][x][y][z]) {

							gl.glPushMatrix();

							gl.glScalef(0.25f * i, 0.25f * i, 0.25f * i);

							// move to the coordinate
							if (i == 4) {
								gl.glTranslatef(.5f * (x + posX),
										.5f * (y + posY), .5f * (z + posZ));
							} else if (i == 3) {
								gl.glTranslatef(0.666f * (x + posX) - 0.33f,
										0.666f * (y + posY) - 0.33f,
										0.666f * (z + posZ) - 0.33f);
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
	public void sizeArrInit(int numArrays, int posNum, GL2 gl,
			int[] displayList, float posX, float posY, float posZ, int topX,
			int topY, int topZ) {

		boolean[][][] doesFit = new boolean[12][12][12];

		for (int i = 4; i >= 1; i--) { // loop for each box size
			for (int x = 0; x < topX; x++) { // x coords
				for (int y = 0; y < topY; y++) { // y coords
					for (int z = 0; z < topZ; z++) { // z coords

						if (!positionRecorded) { // if the cubes are not
													// initialized

							boolean fitting = true;

							// to check for fitting
							// x2 is the width of the cube
							// y2 is the height of the cube
							// z2 is the length of the cube

							for (int n = numArrays; n > 0; n--) {
								for (int x2 = (i - 1) + x; x2 >= x; x2--) {
									for (int y2 = (i - 1) + y; y2 >= y; y2--) {
										for (int z2 = (i - 1) + z; z2 >= z; z2--) {
											if (x2 < 12 && y2 < 12 && z2 < 12) {

												// determines if area is
												// occupied
												if (doesFit[x2][y2][z2])
													fitting = false;

												// randomizes
												if (i != 1
														&& randArr2[n][x2][y2][z2] < 0.05)
													fitting = false;
											} else
												fitting = false;
										}
									}
								}
								// record the position
								if (fitting)
									sizeArr[n][i - 1][x][y][z] = true;
							}
						}
					}
				}
			}
		}
	}

	public void makeTree(GL2 gl, int[] displayList, double branchOdds, float x,
			float y, float z) {
		if (treeSize == null) {
			treeSize = new float[1];
			treeSize[0] = (float) Math.random() * 5f + 5f;
		}

		for (y = y; y < treeSize[0]; y++) {

			if (Math.random() < branchOdds)
				makeTree(gl, displayList, branchOdds - 0.1, x - 2f - 3f, y, z);
			if (Math.random() < branchOdds)
				makeTree(gl, displayList, branchOdds - 0.1, x + 2f - 3f, y, z);
			if (Math.random() < branchOdds)
				makeTree(gl, displayList, branchOdds - 0.1, x, y, z - 2f - 3f);
			if (Math.random() < branchOdds)
				makeTree(gl, displayList, branchOdds - 0.1, x, y, z + 2f - 3f);

			gl.glPushMatrix();

			gl.glScalef(0.25f, 0.25f, 0.25f);

			gl.glTranslatef(x, y + 2f * y - 3f, z);

			// draw the cube
			gl.glCallList(displayList[0]);

			gl.glPopMatrix();

		}
	}
}
