package com.carnivorous_exports.pix;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.text.MessageFormat;
import java.util.List;

import javax.media.nativewindow.CapabilitiesChooser;
import javax.media.nativewindow.CapabilitiesImmutable;
import javax.media.nativewindow.NativeSurface;
import javax.media.nativewindow.NativeWindow;
import javax.media.nativewindow.NativeWindowException;
import javax.media.nativewindow.SurfaceUpdatedListener;
import javax.media.nativewindow.util.InsetsImmutable;
import javax.media.nativewindow.util.Point;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAnimatorControl;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;

import com.jogamp.newt.MonitorDevice;
import com.jogamp.newt.Screen;
import com.jogamp.newt.Window;
import com.jogamp.newt.event.GestureHandler;
import com.jogamp.newt.event.GestureHandler.GestureListener;
import com.jogamp.newt.event.InputEvent;
import com.jogamp.newt.event.MouseAdapter;
import com.jogamp.newt.event.NEWTEvent;
import com.jogamp.newt.event.WindowListener;
import com.jogamp.newt.event.awt.AWTKeyAdapter;
import com.jogamp.newt.event.awt.AWTMouseAdapter;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.util.Animator;

import static javax.media.opengl.GL.*; // GL constants
import static javax.media.opengl.GL2.*; // GL2 constants
import static javax.media.opengl.GL2ES1.GL_PERSPECTIVE_CORRECTION_HINT;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_COLOR_MATERIAL;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_LIGHT0;

@SuppressWarnings("serial")
public class Renderer extends GLCanvas implements GLEventListener, KeyListener,
		MouseListener, MouseMotionListener {

	private GLU glu; // for the GL Utility
	private int [] cubeList; // display list for cube
	private Terrain terrain = new Terrain();

	Robot robot;
	int width;
	int height;

	// public boolean initialized;
	private boolean mouseRButtonDown;
	private boolean mouseInMiddle = false;
	private int prevMouseX;
	private int prevMouseY;
	private int mouseX;
	private int mouseY;
	private float view_rotx;
	private float view_roty;
	private float view_rotz;
	private float movex;
	private float movey;
	private float movez;
	private float mouseSensitivity = 0.75f;

	// for (arrow) key movement
	private boolean upPressed;
	private boolean downPressed;
	private boolean rightPressed;
	private boolean leftPressed;

	private boolean forwardMove;
	private boolean strifeMove;
	int moveDirForward;
	int moveDirStrife;

	/** Constructor to setup the GUI for this Component */
	public Renderer() {

		this.addGLEventListener(this);
		this.addKeyListener(this); // for Handling KeyEvents
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.setFocusable(true);
		this.requestFocus();
	}

	// for user movement
	public void running() {

		if (forwardMove) { // moving forward or back
			movez += Math.cos(180 - view_roty * (Math.PI / 180) + 40) * 0.1
					* -moveDirForward;
			movex -= Math.sin(180 - view_roty * (Math.PI / 180) + 40) * 0.1
					* -moveDirForward;
		}

		if (strifeMove) { // moving right or left
			movez -= Math.cos(180 - view_roty * (Math.PI / 180) + 40 + 80.1
					* -moveDirStrife) * 0.1;
			movex += Math.sin(180 - view_roty * (Math.PI / 180) + 40 + 80.1
					* -moveDirStrife) * 0.1;
		}
	}

	// ------ Implement methods declared in GLEventListener ------

	/**
	 * Called back immediately after the OpenGL context is initialized. Can be
	 * used to perform one-time initialization. Run only once.
	 */
	@Override
	public void init(GLAutoDrawable drawable) {

		width = getWidth();
		height = getHeight();

		// move the mouse to the center
		try {
			robot = new Robot();
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		robot.mouseMove(getWidth() / 2, getHeight() / 2);
		mouseInMiddle = true;

		GL2 gl = drawable.getGL().getGL2(); // get the OpenGL graphics context
		glu = new GLU(); // get GL Utilities
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f); // set background (clear) color
		gl.glClearDepth(1.0f); // set clear depth value to farthest
		gl.glEnable(GL_DEPTH_TEST); // enables depth testing
		gl.glDepthFunc(GL_LEQUAL); // the type of depth test to do
		gl.glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST); // best
																// perspective
																// correction
		gl.glShadeModel(GL_SMOOTH); // blends colors nicely, and smoothes out
									// lighting

		// Enable LIGHT0, which is pre-defined on most video cards.
		gl.glEnable(GL_LIGHT0);
		// gl.glEnable(GL_LIGHTING);

		// Add colors to texture maps, so that glColor3f(r,g,b) takes effect.
		gl.glEnable(GL_COLOR_MATERIAL);

		// We want the best perspective correction to be done
		gl.glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);

		// ----- Your OpenGL initialization code here -----
		cubeList = new int[7];
		cubeList [0] = terrain.getCubeList(gl, "terrainTextures/Layer Rock.jpeg", ".jpeg");
		cubeList [1] = terrain.getCubeList(gl, "terrainTextures/MMud Texture.jpeg", ".jpeg");
		cubeList [2] = terrain.getCubeList(gl, "terrainTextures/Night Sky-.jpeg", ".jpeg");
		cubeList [3] = terrain.getCubeList(gl, "terrainTextures/Rock Texture-.jpeg", ".jpeg");
		cubeList [4] = terrain.getCubeList(gl, "terrainTextures/Sand Texture 1.jpeg", ".jpeg");
		cubeList [5] = terrain.getCubeList(gl, "terrainTextures/Water Texture 1.jpeg", ".jpeg");
		cubeList [6] = terrain.getCubeList(gl, "terrainTextures/White Water Texture.jpeg", ".jpeg");
	}

	/**
	 * Call-back handler for window re-size event. Also called when the drawable
	 * is first set to visible.
	 */
	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {
		GL2 gl = drawable.getGL().getGL2(); // get the OpenGL 2 graphics context

		if (height == 0)
			height = 1; // prevent divide by zero
		float aspect = (float) width / height;

		// Set the view port (display area) to cover the entire window
		gl.glViewport(0, 0, width, height);

		// Setup perspective projection, with aspect ratio matches viewport
		gl.glMatrixMode(GL_PROJECTION); // choose projection matrix
		gl.glLoadIdentity(); // reset projection matrix
		glu.gluPerspective(45.0, aspect, 0.1, 100.0); // fovy, aspect, zNear,
														// zFar

		// Enable the model-view transform
		gl.glMatrixMode(GL_MODELVIEW);
		gl.glLoadIdentity(); // reset
	}

	/**
	 * Called back by the animator to perform rendering.
	 */
	@Override
	public void display(GLAutoDrawable drawable) {
		
		GL2 gl = drawable.getGL().getGL2(); // get the OpenGL 2 graphics context
		gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear color
																// and depth
																// buffers

		gl.glPushMatrix();

		// rotate around wherever the user points the mouse
		gl.glRotatef(-view_rotx, 1.0f, 0.0f, 0.0f);
		gl.glRotatef(-view_roty, 0.0f, 1.0f, 0.0f);
		gl.glRotatef(-view_rotz, 0.0f, 0.0f, 1.0f);

		gl.glTranslatef(movex, movey, movez);

		// --------- Rendering Code
		terrain.buildTerrain(gl, cubeList, "checkFitting");

		gl.glPopMatrix();
		running();
	}

	/**
	 * Called back before the OpenGL context is destroyed. Release resource such
	 * as buffers.
	 */
	@Override
	public void dispose(GLAutoDrawable drawable) {
	}

	@Override
	public void keyPressed(KeyEvent e) {

		// press esc to quit
		int keyCode = e.getKeyCode();
		switch (keyCode) {
		case KeyEvent.VK_ESCAPE: // quit
			// Use a dedicate thread to run the stop() to ensure that the
			// animator stops before program exits.
			new Thread() {
				@Override
				public void run() {
					GLAnimatorControl animator = getAnimator();
					if (animator.isStarted())
						animator.stop();
					System.exit(0);
				}
			}.start();
			break;
		}

		// to move
		if (keyCode == KeyEvent.VK_LEFT)
			leftPressed = true;
		if (keyCode == KeyEvent.VK_RIGHT)
			rightPressed = true;
		if (keyCode == KeyEvent.VK_UP)
			upPressed = true;
		if (keyCode == KeyEvent.VK_DOWN)
			downPressed = true;

		checkKeysPressed();
	}

	@Override
	public void keyReleased(KeyEvent e) {

		int kc = e.getKeyCode();
		if (kc == KeyEvent.VK_LEFT)
			leftPressed = false;
		else if (kc == KeyEvent.VK_RIGHT)
			rightPressed = false;
		else if (kc == KeyEvent.VK_UP)
			upPressed = false;
		else if (kc == KeyEvent.VK_DOWN)
			downPressed = false;

		checkKeysPressed();
	}

	public void checkKeysPressed() {

		if (upPressed && !downPressed) {
			moveDirForward = -1;
		} else if (downPressed && !upPressed) {
			moveDirForward = +1;
		}

		if (leftPressed && !rightPressed) {
			moveDirStrife = +1;
		} else if (rightPressed && !leftPressed) {
			moveDirStrife = -1;
		}

		if (!leftPressed && !rightPressed) {
			strifeMove = false;
		}

		if (!upPressed && !downPressed) {
			forwardMove = false;
		}

		if (upPressed || downPressed) {
			forwardMove = true;
		}

		if (rightPressed || leftPressed) {
			strifeMove = true;
		}

		if (upPressed && downPressed) {
			forwardMove = false;
		}

		if (rightPressed && leftPressed) {
			strifeMove = false;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	// class Cubes2MouseAdapter extends MouseAdapter implements MouseListener {

	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void mousePressed(MouseEvent e) {
		// prevMouseX = e.getX();
		// prevMouseY = e.getY();
		// if ((e.getModifiers() & e.BUTTON3_MASK) != 0) {
		// mouseRButtonDown = true;
		// }
	}

	public void mouseReleased(MouseEvent e) {
		// if ((e.getModifiers() & e.BUTTON3_MASK) != 0) {
		// mouseRButtonDown = false;
		// }
	}

	public void mouseDragged(MouseEvent e) {

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		/**
		 * To prevent the mouse from hitting the edge of the screen we have to
		 * move the mouse to the center of the screen every other iteration and
		 * keep track of the mouse position ourselves
		 * 
		 * mouseInMiddle == false when we are iterating just to keep the mouse
		 * in the middle
		 */

		if (!mouseInMiddle)
			return;

		prevMouseY = height / 2; // because the mouse is always in the middle of
		prevMouseX = width / 2; // the screen, the prevMouseX/Y is also always
								// the middle of the screen
		float thetaY;
		float thetaX;

		mouseY = prevMouseY - e.getY();
		mouseX = prevMouseX - e.getX();

		thetaY = 360.0f * ((float) (mouseX) / (float) width);
		thetaX = 360.0f * ((float) (mouseY) / (float) height);

		prevMouseX = mouseX;
		prevMouseY = mouseY;

		//restricting x rotation movement to make physical sense
			//DOESNT WORK
		if(view_rotx + thetaX*mouseSensitivity < 180 && 
				view_rotx + thetaX*mouseSensitivity > -180) {
			view_rotx += thetaX * mouseSensitivity;
		}
		
		view_roty += thetaY * mouseSensitivity;

		view_roty = view_roty % 360;

		mouseInMiddle = false;
		robot.mouseMove(width / 2, height / 2);
		mouseInMiddle = true;
	}
}