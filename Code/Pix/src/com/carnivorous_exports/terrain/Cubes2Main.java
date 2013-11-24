package com.carnivorous_exports.terrain;

import java.awt.*;
import java.awt.image.BufferedImage;

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
	private boolean fullScreen = true;
	private boolean cursorVisible = true;
	
	/** Constructor to setup the top-level container and animator */
	public Cubes2Main() {
		// Run the GUI codes in the event-dispatching thread for thread safety
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				// Create the OpenGL rendering canvas
				// 		The renderer is also the canvas because
				// 		the rendering class extends GLCanvas
				Cubes2Renderer renderer = new Cubes2Renderer();
				renderer.addGLEventListener(renderer);

				// Create a animator that drives canvas' display() at the
				// specified FPS.
				FPSAnimator animator = new FPSAnimator(renderer, FPS, true);

				if(!fullScreen) renderer.setPreferredSize(new Dimension(500, 500));
				
				//make cursor disappear
				Toolkit t = Toolkit.getDefaultToolkit();
				Image i = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
				Cursor noCursor = t.createCustomCursor(i, new Point(0, 0), "none");
				
				
				// Create the top-level container frame
				JFrame frame = new JFrame(); // Swing's JFrame or AWT's Frame
				if(!cursorVisible) frame.setCursor(noCursor);
				frame.getContentPane().add(renderer);
				frame.setUndecorated(true); // no decoration such as title bar
				if(fullScreen) frame.setExtendedState(Frame.MAXIMIZED_BOTH); // full screen
																// mode
				if(!fullScreen) frame.setTitle("Pix"); 
	            if(!fullScreen) frame.pack();		  
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
