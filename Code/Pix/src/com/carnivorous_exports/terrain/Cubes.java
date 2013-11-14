package com.carnivorous_exports.terrain;

import static javax.media.opengl.GL2GL3.GL_QUADS;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;

import com.jogamp.newt.Window;
import com.jogamp.newt.event.KeyAdapter;
import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.newt.event.MouseAdapter;
import com.jogamp.newt.event.MouseEvent;
import com.jogamp.newt.event.MouseListener;
import com.jogamp.newt.event.awt.AWTKeyAdapter;
import com.jogamp.newt.event.awt.AWTMouseAdapter;

public class Cubes implements GLEventListener {

	private float view_rotx = 20.0f, view_roty = 30.0f, view_rotz = 0.0f;
	private int gear1 = 0, gear2 = 0, gear3 = 0;
	private float angle = 0.0f;
	private int swapInterval;

	private boolean mouseRButtonDown = false;
	private int prevMouseX, prevMouseY;

	public static void main(String[] args) {
		java.awt.Frame frame = new java.awt.Frame("Gear Demo");
		frame.setSize(300, 300);
		frame.setLayout(new java.awt.BorderLayout());
		GLCanvas canvas = new GLCanvas();

		final Cubes cube = new Cubes();
		canvas.addGLEventListener(cube);

		frame.add(canvas, java.awt.BorderLayout.CENTER);
		frame.validate();

		frame.setVisible(true);
	}

	public void cube(GL2 gl) {
		// ----- Render the Color Cube -----
		gl.glLoadIdentity(); // reset the current model-view matrix
		gl.glTranslatef(1.6f, 0.0f, -7.0f); // translate right and into the
											// screen

		gl.glBegin(GL_QUADS); // of the color cube

		// Top-face
		gl.glColor3f(0.0f, 1.0f, 0.0f); // green
		gl.glVertex3f(1.0f, 1.0f, -1.0f);
		gl.glVertex3f(-1.0f, 1.0f, -1.0f);
		gl.glVertex3f(-1.0f, 1.0f, 1.0f);
		gl.glVertex3f(1.0f, 1.0f, 1.0f);

		// Bottom-face
		gl.glColor3f(1.0f, 0.5f, 0.0f); // orange
		gl.glVertex3f(1.0f, -1.0f, 1.0f);
		gl.glVertex3f(-1.0f, -1.0f, 1.0f);
		gl.glVertex3f(-1.0f, -1.0f, -1.0f);
		gl.glVertex3f(1.0f, -1.0f, -1.0f);

		// Front-face
		gl.glColor3f(1.0f, 0.0f, 0.0f); // red
		gl.glVertex3f(1.0f, 1.0f, 1.0f);
		gl.glVertex3f(-1.0f, 1.0f, 1.0f);
		gl.glVertex3f(-1.0f, -1.0f, 1.0f);
		gl.glVertex3f(1.0f, -1.0f, 1.0f);

		// Back-face
		gl.glColor3f(1.0f, 1.0f, 0.0f); // yellow
		gl.glVertex3f(1.0f, -1.0f, -1.0f);
		gl.glVertex3f(-1.0f, -1.0f, -1.0f);
		gl.glVertex3f(-1.0f, 1.0f, -1.0f);
		gl.glVertex3f(1.0f, 1.0f, -1.0f);

		// Left-face
		gl.glColor3f(0.0f, 0.0f, 1.0f); // blue
		gl.glVertex3f(-1.0f, 1.0f, 1.0f);
		gl.glVertex3f(-1.0f, 1.0f, -1.0f);
		gl.glVertex3f(-1.0f, -1.0f, -1.0f);
		gl.glVertex3f(-1.0f, -1.0f, 1.0f);

		// Right-face
		gl.glColor3f(1.0f, 0.0f, 1.0f); // magenta
		gl.glVertex3f(1.0f, 1.0f, -1.0f);
		gl.glVertex3f(1.0f, 1.0f, 1.0f);
		gl.glVertex3f(1.0f, -1.0f, 1.0f);
		gl.glVertex3f(1.0f, -1.0f, -1.0f);

		gl.glEnd(); // of the color cube
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();

		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

		// Special handling for the case where the GLJPanel is translucent
		// and wants to be composited with other Java 2D content
		if (GLProfile.isAWTAvailable()
				&& (drawable instanceof javax.media.opengl.awt.GLJPanel)
				&& !((javax.media.opengl.awt.GLJPanel) drawable).isOpaque()
				&& ((javax.media.opengl.awt.GLJPanel) drawable)
						.shouldPreserveColorBufferIfTranslucent()) {
			gl.glClear(GL2.GL_DEPTH_BUFFER_BIT);
		} else {
			gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		}
		
		/*
		
		// Rotate the entire assembly of gears based on how the user
	    // dragged the mouse around
	    gl.glPushMatrix();
	    gl.glRotatef(view_rotx, 1.0f, 0.0f, 0.0f);
	    gl.glRotatef(view_roty, 0.0f, 1.0f, 0.0f);
	    gl.glRotatef(view_rotz, 0.0f, 0.0f, 1.0f);
	            
	    // Place the first gear and call its display list
	    gl.glPushMatrix();
	    gl.glTranslatef(-3.0f, -2.0f, 0.0f);
	    gl.glRotatef(angle, 0.0f, 0.0f, 1.0f);
	    gl.glCallList(gear1);
	    gl.glPopMatrix();
	            
	    // Place the second gear and call its display list
	    gl.glPushMatrix();
	    gl.glTranslatef(3.1f, -2.0f, 0.0f);
	    gl.glRotatef(-2.0f * angle - 9.0f, 0.0f, 0.0f, 1.0f);
	    gl.glCallList(gear2);
	    gl.glPopMatrix();
	            
	    // Place the third gear and call its display list
	    gl.glPushMatrix();
	    gl.glTranslatef(-3.1f, 4.2f, 0.0f);
	    gl.glRotatef(-2.0f * angle - 25.0f, 0.0f, 0.0f, 1.0f);
	    gl.glCallList(gear3);
	    gl.glPopMatrix();
	            
	    // Remember that every push needs a pop; this one is paired with
	    // rotating the entire gear assembly
	    gl.glPopMatrix();
	    
	    */
	}

	@Override
	public void init(GLAutoDrawable drawable) {

		GL2 gl = drawable.getGL().getGL2();

		float pos[] = { 5.0f, 5.0f, 10.0f, 0.0f };
		float red[] = { 0.8f, 0.1f, 0.0f, 0.7f };
		float green[] = { 0.0f, 0.8f, 0.2f, 0.7f };
		float blue[] = { 0.2f, 0.2f, 1.0f, 0.7f };

		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, pos, 0);
		gl.glEnable(GL2.GL_CULL_FACE);
		gl.glEnable(GL2.GL_LIGHTING);
		gl.glEnable(GL2.GL_LIGHT0);
		gl.glEnable(GL2.GL_DEPTH_TEST);


		// MAKE CUBES
		gl.glNewList(0, GL2.GL_COMPILE);

		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE, red, 0);
		cube(gl);

		gl.glEndList();

		MouseListener gearsMouse = new GearsMouseAdapter();
		KeyListener gearsKeys = new GearsKeyAdapter();

		if (drawable instanceof Window) {
			Window window = (Window) drawable;
			window.addMouseListener(gearsMouse);
			window.addKeyListener(gearsKeys);
		} else if (GLProfile.isAWTAvailable()
				&& drawable instanceof java.awt.Component) {
			java.awt.Component comp = (java.awt.Component) drawable;
			new AWTMouseAdapter(gearsMouse).addTo(comp);
			new AWTKeyAdapter(gearsKeys).addTo(comp);
		}

	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {
		
		System.err.println("Gears: Reshape "+x+"/"+y+" "+width+"x"+height);

		GL2 gl = drawable.getGL().getGL2();

		gl.setSwapInterval(swapInterval);

		float h = (float) height / (float) width;

		gl.glMatrixMode(GL2.GL_PROJECTION);

		gl.glLoadIdentity();
		gl.glFrustum(-1.0f, 1.0f, -h, h, 5.0f, 60.0f);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glTranslatef(0.0f, 0.0f, -40.0f);

	}

	@Override
	public void dispose(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub

	}

	class GearsKeyAdapter extends KeyAdapter {
		public void keyPressed(KeyEvent e) {
			int kc = e.getKeyCode();
			if (KeyEvent.VK_LEFT == kc) {
				view_roty -= 1;
			} else if (KeyEvent.VK_RIGHT == kc) {
				view_roty += 1;
			} else if (KeyEvent.VK_UP == kc) {
				view_rotx -= 1;
			} else if (KeyEvent.VK_DOWN == kc) {
				view_rotx += 1;
			}
		}
	}

	class GearsMouseAdapter extends MouseAdapter {
		public void mousePressed(MouseEvent e) {
			prevMouseX = e.getX();
			prevMouseY = e.getY();
			if ((e.getModifiers() & e.BUTTON3_MASK) != 0) {
				mouseRButtonDown = true;
			}
		}

		public void mouseReleased(MouseEvent e) {
			if ((e.getModifiers() & e.BUTTON3_MASK) != 0) {
				mouseRButtonDown = false;
			}
		}

		public void mouseDragged(MouseEvent e) {
			int x = e.getX();
			int y = e.getY();
			int width = 0, height = 0;
			Object source = e.getSource();
			if (source instanceof Window) {
				Window window = (Window) source;
				width = window.getWidth();
				height = window.getHeight();
			} else if (GLProfile.isAWTAvailable()
					&& source instanceof java.awt.Component) {
				java.awt.Component comp = (java.awt.Component) source;
				width = comp.getWidth();
				height = comp.getHeight();
			} else {
				throw new RuntimeException(
						"Event source neither Window nor Component: " + source);
			}
			float thetaY = 360.0f * ((float) (x - prevMouseX) / (float) width);
			float thetaX = 360.0f * ((float) (prevMouseY - y) / (float) height);

			prevMouseX = x;
			prevMouseY = y;

			view_rotx += thetaX;
			view_roty += thetaY;
		}
	}
}
