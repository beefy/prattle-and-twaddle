package com.carnivorous_exports.pix;

import static javax.media.opengl.GL.GL_COLOR_BUFFER_BIT;
import static javax.media.opengl.GL.GL_DEPTH_BUFFER_BIT;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;

public class Quad {

	private boolean[][][][] placement = new boolean[4][12][12][12];
	int[] displayList;
	int coordX;
	int coordY;

	// for initQuad
	int topX;
	int topY;
	int topZ;
	float variety;

	// for refreshQuad
	int posX;
	int posY;
	int posZ;
	
	public boolean collided = false;

	Renderer renderer;
	GLAutoDrawable drawable;

	public Quad(GLAutoDrawable drawable, Renderer renderer, int topX, int topY,
			int topZ, float variety, GL2 gl, int[] displayList, int coordX,
			int coordY) {

		this.topX = topX;
		this.topY = topY;
		this.topZ = topZ;

		this.variety = variety;
		this.drawable = drawable;
		this.displayList = displayList;

		this.coordX = coordX;
		this.coordY = coordY;

		// position of this Quad
		posX = 12 * coordY;
		posY = 2 * coordX + 2 * coordY; // to make a hill
		posZ = 12 * coordX - 40;

		this.renderer = renderer;

		initQuad();
	}

	public void initQuad() {
		// topX, Y, and Z are the boundaries of the quad, when they are 12 it is
		// a cube
		// variety is the variety of cube sizes:
		// 0.0 is entirely size 1 cubes, 1.0 is entirely size 4 cubes

		boolean[][][] doesNotFit = new boolean[12][12][12]; // is false when
															// [x][y][z] space
															// is
															// occupied

		boolean fitting = true; // is true when the block will be placed

		for (int i = 4; i >= 1; i--) { // loop for each box size
			for (int x = 0; x < topX; x++) { // x coords
				for (int y = 0; y < topY; y++) { // y coords
					for (int z = 0; z < topZ; z++) { // z coords

						// randomize
						if (i != 1 && (float) Math.random() < variety) {
							fitting = false;
						}

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
						} // else placement[i - 1][x][y][z] = false;

						// reset fitting
						fitting = true;
					}
				}
			}
		}
	}

	public void refreshQuad(GL2 gl, float selectedObject) {

		for (int i = 4; i >= 1; i--) { // loop for each box size
			for (int x = 0; x < topX; x++) { // x coords
				for (int y = 0; y < topY; y++) { // y coords
					for (int z = 0; z < topZ; z++) { // z coords

						// if a box is placed there
						if (placement[i - 1][x][y][z]) {

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
							if(this.hashCode() != selectedObject) {
								gl.glCallList(displayList[0]);
								//System.out.println("Hash: " + this.hashCode() + 
								//		"\nObj: " + selectedObject);
							} else {
								gl.glCallList(displayList[1]);
							}
							
							gl.glPopMatrix();
							checkCollision(posX, posY, posZ, i);
						}
					}
				}
			}
		}
	}
	
	public void checkCollision(int x, int y, int z, int i) {
		
		float length;
		
		if(i==4) { 
			length = 1f;
			
			if((.5f * (x + posX) > 0 && .5f * (x + posX) + length < 0)
					&& (.5f * (x + posY) > 0 && .5f * (x + posY) + length < 0)
					&& (.5f * (x + posZ) > 0 && .5f * (x + posZ) + length < 0)) { 
						collided = true;
						System.out.println("\n\n\n\ncollsion coord: (" + .5f * (x + posX) + ", " +
								.5f * (x + posY) + ", " + .5f * (x + posZ) +
								")\n\n\n\n");
			}
			
			//System.out.println("				" + .5f * (x + posX));
			
		} else if(i==3) { 
			length = 0.75f;
		} else if(i==2) { 
			length = 0.5f;
		} else if(i==1) {
			length = 0.25f;
		}
		
		
	}
}
