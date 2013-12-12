package com.carnivorous_exports.pix;

import javax.media.opengl.GL2;

public class Quad {
	
	private boolean[][][][] placement = new boolean[4][12][12][12];

	public Quad(int topX, int topY, int topZ, float variety) {
			//topX, Y, and Z are the boundaries of the quad, when they are 12 it is a cube
			//variety is the variety of cube sizes: 
			//		0.0 is entirely size 1 cubes, 1.0 is entirely size 4 cubes
			
			boolean[][][] doesNotFit = new boolean[12][12][12]; // is false when
																// [x][y][z] space is
																// occupied
			
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
	}
	
	public void refreshQuad(GL2 gl, int[] displayList,
			int topX, int topY, int topZ, int posX, int posY, int posZ) {

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
							gl.glCallList(displayList[0]);

							gl.glPopMatrix();
						}
					}
				}
			}
		}

	}
}
