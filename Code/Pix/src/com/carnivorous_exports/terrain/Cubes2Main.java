package com.carnivorous_exports.terrain;

import java.awt.*;
import javax.swing.*;
import javax.media.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;

/**
 * JOGL 2.0 Program Template (GLCanvas) This is the top-level "Container", which
 * allocates and add GLCanvas ("Component") and animator.
 */
@SuppressWarnings("serial")
public class Cubes2Main extends JFrame {
	// Define constants for the top-level container
	//private static String TITLE = "JOGL 2.0 Setup (GLCanvas)"; // window's title
	//private static final int CANVAS_WIDTH = 640; // width of the drawable
	//private static final int CANVAS_HEIGHT = 480; // height of the drawable
	private static final int FPS = 60; // animator's target frames per second

	/** Constructor to setup the top-level container and animator */
	public Cubes2Main() {
		// Run the GUI codes in the event-dispatching thread for thread safety
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				// Create the OpenGL rendering canvas
				GLCanvas canvas = new Cubes2Renderer();
				Cubes2Renderer renderer = new Cubes2Renderer();
				canvas.addGLEventListener(renderer);

				// Create a animator that drives canvas' display() at the
				// specified FPS.
				FPSAnimator animator = new FPSAnimator(canvas, FPS, true);

				// Create the top-level container frame
				JFrame frame = new JFrame(); // Swing's JFrame or AWT's Frame
				frame.getContentPane().add(canvas);
				frame.setUndecorated(true); // no decoration such as title bar
				frame.setExtendedState(Frame.MAXIMIZED_BOTH); // full screen
																// mode
				frame.setVisible(true);
				animator.start(); // start the animation loop
			}
		});
	}

	public static void main(String[] args) {
		// Run the GUI codes in the event-dispatching thread for thread safety
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new Cubes2Main(); // run the constructor
			}
		});
	}
}
