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
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAnimatorControl;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.GLRunnable;
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
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_AMBIENT;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_COLOR_MATERIAL;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_DIFFUSE;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_LIGHT0;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_LIGHT1;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_POSITION;

@SuppressWarnings("serial")
public class Renderer implements GLEventListener,
		com.jogamp.newt.event.MouseListener, com.jogamp.newt.event.KeyListener {

	private static GLWindow window;
	private GLU glu; // for the GL Utility
	private int[] cubeList; // display list for cube
	private Terrain terrain = new Terrain();
	private boolean initiated = false;
	GLAutoDrawable drawable;

	// for lighting
	//float[] lightPos = { 5, 7, 5, 1 }; // light position
	float[] noAmbient = { 0.2f, 0.2f, 0.2f, 1f }; // low ambient light
	float[] diffuse = { 1f, 1f, 1f, 1f }; // full diffuse colour

	float cameraPos[] = { 5.0f, 5.0f, 10.0f, 0.0f };
	
	float[] colorWhite  = {1.0f,1.0f,1.0f,1.0f};

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
	public float view_rotx;
	public float view_roty;
	public float view_rotz;
	public float oldRotX;
	public float oldRotY;
	public float oldRotZ;
	public float movex;
	public float movey;
	public float movez;
	private float mouseSensitivity = 0.75f;

	// for (arrow) key movement
	private boolean upPressed;
	private boolean downPressed;
	private boolean rightPressed;
	private boolean leftPressed;

	private boolean flyUpPressed;
	private boolean flyDownPressed;

	private boolean forwardMove;
	private boolean strifeMove;

	private boolean flyUpMove;
	private boolean flyDownMove;

	int moveDirForward;
	int moveDirStrife;
	int moveSpeed = 1;

	public Renderer(GLWindow window) {
		this.window = window;
		window.addGLEventListener(this);
		window.addMouseListener(this);
		window.addKeyListener(this);
		//window.
		// turn off key auto repeat
		window.requestFocus();
	}

	// for user movement
	public void running() {

		if (forwardMove) { // moving forward or back
			movez += Math.cos(180 - view_roty * (Math.PI / 180) + 40) * 0.1
					* -moveDirForward * moveSpeed;
			movex -= Math.sin(180 - view_roty * (Math.PI / 180) + 40) * 0.1
					* -moveDirForward * moveSpeed;

		}

		if (strifeMove) { // moving right or left
			movez -= Math.cos(180 - view_roty * (Math.PI / 180) + 40 + 80.1
					* -moveDirStrife) * 0.1 * moveSpeed;
			movex += Math.sin(180 - view_roty * (Math.PI / 180) + 40 + 80.1
					* -moveDirStrife) * 0.1 * moveSpeed;
		}

		if (flyUpMove)
			movey -= 0.1;
		if (flyDownMove)
			movey += 0.1;
	}

	// ------ Implement methods declared in GLEventListener ------

	/**
	 * Called back immediately after the OpenGL context is initialized. Can be
	 * used to perform one-time initialization. Run only once.
	 */
	@Override
	public void init(GLAutoDrawable drawable) {
		
		drawable.getAnimator().setUpdateFPSFrames(3, null);
		drawable.setAutoSwapBufferMode(true);
		
		width = window.getWidth();
		height = window.getHeight();

		// move the mouse to the center
		try {
			robot = new Robot();
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		robot.mouseMove(window.getWidth() / 2, window.getHeight() / 2);
		mouseInMiddle = true;

		GL2 gl = drawable.getGL().getGL2(); // get the OpenGL graphics context
		glu = new GLU(); // get GL Utilities
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f); // set background (clear) color
		gl.glClearDepth(1.0f); // set clear depth value to farthest
		
		//for anti-aliasing
		gl.glEnable(GL.GL_LINE_SMOOTH);
		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		gl.glHint(GL.GL_LINE_SMOOTH_HINT, GL.GL_DONT_CARE);
		

		// for lighting
		// Set up the lighting for Light-1
		// Ambient light does not come from a particular direction. Need some
		// ambient
		// light to light up the scene. Ambient's value in RGBA
		// float[] lightAmbientValue = {0.5f, 0.5f, 0.5f, 1.0f};
		// Diffuse light comes from a particular location. Diffuse's value in
		// RGBA
		// float[] lightDiffuseValue = {1.0f, 1.0f, 1.0f, 1.0f};
		// Diffuse light location xyz (in front of the screen).
		// float lightDiffusePosition[] = {0.0f, 0.0f, 2.0f, 1.0f};

		// gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, cameraPos, 0);
		// gl.glLightf(0, GL_SPOT_EXPONENT, 0);
		// gl.glLightfv(GL_LIGHT1, GL_AMBIENT, lightAmbientValue, 0);
		// gl.glLightfv(GL_LIGHT1, GL_DIFFUSE, lightDiffuseValue, 0);
		// gl.glLightfv(GL_LIGHT1, GL_POSITION, lightDiffusePosition, 0);

		// gl.glEnable(GL_LIGHTING);
		// gl.glEnable(GL_LIGHT0);
		// gl.glLightfv(GL_LIGHT0, GL_AMBIENT, noAmbient, 0);
		// gl.glLightfv(GL_LIGHT0, GL_DIFFUSE, diffuse, 0);
		// gl.glLightfv(GL_LIGHT0, GL_POSITION,lightPos, 0);

		// gl.glEnable(GL2.GL_CULL_FACE);
		// gl.glEnable(GL2.GL_LIGHTING);
		// gl.glEnable(GL2.GL_LIGHT0);
		
		//gl.glEnable(GL2.GL_LIGHTING);
		//gl.glEnable(GL2.GL_LIGHT0);
		//gl.glDepthFunc(GL.GL_LESS);
		//gl.glEnable(GL.GL_DEPTH_TEST);

		gl.glEnable(GL_DEPTH_TEST); // enables depth testing
		gl.glDepthFunc(GL_LEQUAL); // the type of depth test to do
		gl.glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST); // best
																// perspective
																// correction
		gl.glShadeModel(GL_SMOOTH); // blends colors nicely, and smoothes out
									// lighting

		// Enable LIGHT0, which is pre-defined on most video cards.
		gl.glEnable(GL_LIGHT1);
		gl.glEnable(GL_LIGHTING);

		// Add colors to texture maps, so that glColor3f(r,g,b) takes effect.
		gl.glEnable(GL_COLOR_MATERIAL);

		// We want the best perspective correction to be done
		gl.glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);

		// ----- Your OpenGL initialization code here -----
		cubeList = new int[7];
		cubeList[0] = terrain.getCubeList(gl,
				"terrainTextures/Layer Rock.jpeg", ".jpeg");
		cubeList[1] = terrain.getCubeList(gl,
				"terrainTextures/MMud Texture.jpeg", ".jpeg");
		cubeList[2] = terrain.getCubeList(gl,
				"terrainTextures/Night Sky-.jpeg", ".jpeg");
		cubeList[3] = terrain.getCubeList(gl,
				"terrainTextures/Rock Texture-.jpeg", ".jpeg");
		cubeList[4] = terrain.getCubeList(gl,
				"terrainTextures/Sand Texture 1.jpeg", ".jpeg");
		cubeList[5] = terrain.getCubeList(gl,
				"terrainTextures/Water Texture 1.jpeg", ".jpeg");
		cubeList[6] = terrain.getCubeList(gl,
				"terrainTextures/White Water Texture.jpeg", ".jpeg");

		// start the terrain thread: refresh the terrain once it's built
		// if(!initiatedThread) terrain.t.start();
		// initiatedThread = true;

		if (!initiated)
			terrain.buildTerrain(drawable, this, gl, cubeList);
		initiated = true;
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
		long startNanos = System.nanoTime();
		
		this.drawable = drawable;

		GL2 gl = drawable.getGL().getGL2(); // get the OpenGL 2 graphics context
		gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear color
																// and depth
																// buffers

		float light_ambient[] = { 0.0f, 0.0f, 0.0f, 1.0f };
		float light_diffuse[] = { 1.0f, 1.0f, 1.0f, 1.0f };
		float light_specular[] = { 1.0f, 1.0f, 1.0f, 1.0f };
		float light_position[] = { 1.0f, 1.0f, 1.0f, 0.0f };
		
		gl.glPushMatrix();

		// Prepare light parameters.
        float SHINE_ALL_DIRECTIONS = 1;
        float[] lightPos = {-30, 0, 0, SHINE_ALL_DIRECTIONS};
        float[] lightColorAmbient = {0.2f, 0.2f, 0.2f, 1f};
        float[] lightColorSpecular = {0.8f, 0.8f, 0.8f, 1f};

        // Set light parameters.
        gl.glLightfv(GL_LIGHT1, GL_POSITION, lightPos, 0);
        gl.glLightfv(GL_LIGHT1, GL_AMBIENT, lightColorAmbient, 0);
        gl.glLightfv(GL_LIGHT1, GL_SPECULAR, lightColorSpecular, 0);

        // Enable lighting in GL.
        gl.glEnable(GL_LIGHT1);
        gl.glEnable(GL_LIGHTING);
		
        // Set material properties.
        float[] rgba = {1f, 1f, 1f};	//white
        gl.glMaterialfv(GL.GL_FRONT, GL_AMBIENT, rgba, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL_SPECULAR, rgba, 0);
        gl.glMaterialf(GL.GL_FRONT, GL_SHININESS, 0.5f);
        
     // rotate around wherever the user points the mouse
     		gl.glRotatef(-view_rotx, 1.0f, 0.0f, 0.0f);
     		gl.glRotatef(-view_roty, 0.0f, 1.0f, 0.0f);
     		gl.glRotatef(-view_rotz, 0.0f, 0.0f, 1.0f);
        
		gl.glTranslatef(movex, movey, movez);

		// --------- Rendering Code
		terrain.refreshTerrain(gl);

		//terrain.testLight(gl, light_position);
		
		
		//
		// Light 1.
		//
		// Position and direction (spotlight)
		//float posLight1[] = { 1.0f, 1.f, 1.f, 0.0f };
		//float spotDirection[] = { -1.0f, -1.0f, 0.f };
		//gl.glLightfv( GL_LIGHT1, GL_AMBIENT, colorWhite, 0 );
	     // gl.glLightfv( GL_LIGHT1, GL_DIFFUSE, colorWhite, 0 );
	     // gl.glLightfv( GL_LIGHT1, GL_SPECULAR, colorWhite, 0 );
	     // gl.glLightf( GL_LIGHT1, GL_CONSTANT_ATTENUATION, 0.2f );

		//gl.glLightfv(GL_LIGHT1, GL_POSITION,lightPos, 0);

		

		//gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, light_ambient, 0);
		//gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, light_diffuse, 0);
		//gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, light_specular, 0);
		//gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, light_position, 0);

		gl.glPopMatrix();

		// gl.glPopMatrix();
		running();
		// lightRefresh();

		oldRotX = view_rotx;
		oldRotY = view_roty;
		oldRotZ = view_rotz;
		
		long drawNanos = System.nanoTime() - startNanos;
		//System.out.println("drawn in " + drawNanos);

		System.out.println(drawable.getAnimator().getLastFPS());
		
		//gl.glFlush();
		drawable.swapBuffers();
		gl.glFlush();
	}

	/**
	 * Called back before the OpenGL context is destroyed. Release resource such
	 * as buffers.
	 */
	@Override
	public void dispose(GLAutoDrawable drawable) {

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

		if (flyUpPressed && !flyDownPressed) {
			flyUpMove = true;
		}

		if (flyDownPressed && !flyUpPressed) {
			flyDownMove = true;
		}

		if (!flyUpPressed) {
			flyUpMove = false;
		}

		if (!flyDownPressed) {
			flyDownMove = false;
		}
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

	@Override
	public void mouseClicked(com.jogamp.newt.event.MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseDragged(com.jogamp.newt.event.MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(com.jogamp.newt.event.MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(com.jogamp.newt.event.MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseMoved(com.jogamp.newt.event.MouseEvent e) {
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

		// restricting x rotation movement to make physical sense
		// DOESNT WORK
		if (view_rotx + thetaX * mouseSensitivity < 180
				&& view_rotx + thetaX * mouseSensitivity > -180) {
			view_rotx += thetaX * mouseSensitivity;
		}

		view_roty += thetaY * mouseSensitivity;

		view_roty = view_roty % 360;

		mouseInMiddle = false;
		robot.mouseMove(width / 2, height / 2);
		mouseInMiddle = true;
	}

	@Override
	public void mousePressed(com.jogamp.newt.event.MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(com.jogamp.newt.event.MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseWheelMoved(com.jogamp.newt.event.MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyPressed(com.jogamp.newt.event.KeyEvent e) {

		// press esc to quit
		int keyCode = e.getKeyCode();
		switch (keyCode) {
		case com.jogamp.newt.event.KeyEvent.VK_ESCAPE: // quit
			// Use a dedicate thread to run the stop() to ensure that the
			// animator stops before program exits.
			new Thread() {
				@Override
				public void run() {
					GLAnimatorControl animator = window.getAnimator();
					if (animator.isStarted())
						animator.stop();
					System.exit(0);
				}
			}.start();
			break;
		}

		// to move
		if (keyCode == com.jogamp.newt.event.KeyEvent.VK_LEFT
				|| keyCode == com.jogamp.newt.event.KeyEvent.VK_A)
			leftPressed = true;
		if (keyCode == com.jogamp.newt.event.KeyEvent.VK_RIGHT
				|| keyCode == com.jogamp.newt.event.KeyEvent.VK_D)
			rightPressed = true;
		if (keyCode == com.jogamp.newt.event.KeyEvent.VK_UP
				|| keyCode == com.jogamp.newt.event.KeyEvent.VK_W)
			upPressed = true;
		if (keyCode == com.jogamp.newt.event.KeyEvent.VK_DOWN
				|| keyCode == com.jogamp.newt.event.KeyEvent.VK_S)
			downPressed = true;

		// flying up and down (for debugging)
		if (keyCode == com.jogamp.newt.event.KeyEvent.VK_SHIFT) {
			flyUpPressed = true;
		}

		if (keyCode == com.jogamp.newt.event.KeyEvent.VK_CONTROL) {
			flyDownPressed = true;
		}

		checkKeysPressed();
	}

	@Override
	public void keyReleased(com.jogamp.newt.event.KeyEvent e) {

		if (e.isAutoRepeat())
			return;

		int kc = e.getKeyCode();
		if (kc == com.jogamp.newt.event.KeyEvent.VK_LEFT
				|| kc == com.jogamp.newt.event.KeyEvent.VK_A)
			leftPressed = false;
		else if (kc == com.jogamp.newt.event.KeyEvent.VK_RIGHT
				|| kc == com.jogamp.newt.event.KeyEvent.VK_D)
			rightPressed = false;
		else if (kc == com.jogamp.newt.event.KeyEvent.VK_UP
				|| kc == com.jogamp.newt.event.KeyEvent.VK_W)
			upPressed = false;
		else if (kc == com.jogamp.newt.event.KeyEvent.VK_DOWN
				|| kc == com.jogamp.newt.event.KeyEvent.VK_S)
			downPressed = false;

		// flying up and down (for debugging)
		if (kc == com.jogamp.newt.event.KeyEvent.VK_SHIFT) {
			flyUpPressed = false;
		} else if (kc == com.jogamp.newt.event.KeyEvent.VK_CONTROL) {
			flyDownPressed = false;
		}

		checkKeysPressed();

	}
}