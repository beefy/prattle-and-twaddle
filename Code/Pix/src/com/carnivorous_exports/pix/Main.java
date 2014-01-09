package com.carnivorous_exports.pix;

import java.awt.*;
import java.awt.image.BufferedImage;

import javax.swing.*;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;

import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.util.FPSAnimator;

import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.awt.GLCanvas;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


@SuppressWarnings("serial")
public class Main extends Frame {
	// Define constants for the top-level container
	private static String TITLE = "Pix"; // window's title
	private static final int CANVAS_WIDTH = 640; // width of the drawable
	private static final int CANVAS_HEIGHT = 480; // height of the drawable
	private static final int FPS = 60; // animator's target frames per second
	private boolean fullScreen = true;
	private boolean cursorVisible = false;
	
	/** Constructor to setup the top-level container and animator */
	public Main() {
	
		//for GLWindow
		GLProfile glprofile = GLProfile.getDefault();
		GLProfile.initSingleton();
		
        //GLCapabilities glcapabilities = new GLCapabilities( glprofile );
        //final GLCanvas glcanvas = new GLCanvas( glcapabilities );
		
		// Run the GUI codes in the event-dispatching thread for thread safety
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
		    
				
				// Create the OpenGL rendering canvas
				// 		The renderer is also the canvas because
				// 		the rendering class extends GLCanvas
				//GLProfile glp = GLProfile.getDefault();
				//glp.initSingleton();
		        //GLCapabilities caps = new GLCapabilities(glp);
		        //caps.setBackgroundOpaque(false);
				
				GLProfile glp = GLProfile.getDefault();
				//glp.initSingleton();
		        GLCapabilities caps = new GLCapabilities(glp);
		        caps.setBackgroundOpaque(false);
				
				GLWindow window = GLWindow.create(caps);
				
				Renderer renderer = new Renderer(window);
				
				
				window.addGLEventListener(renderer);

				// Create a animator that drives canvas' display() at the
				// specified FPS.
				FPSAnimator animator = new FPSAnimator(window, FPS, true);

				//if(!fullScreen) renderer.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));
				
				//make cursor disappear
				Toolkit t = Toolkit.getDefaultToolkit();
				Image i = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
				Cursor noCursor = t.createCustomCursor(i, new Point(0, 0), "none");
				
				
				// Create the top-level container frame
				//Frame frame = new Frame(); // Swing's JFrame or AWT's Frame
				//frame.add( glcanvas );
				//if(!cursorVisible) frame.setCursor(noCursor);
				//frame.getContentPane().add(renderer);
				//frame.add(renderer);
				//frame.setUndecorated(true); // no decoration such as title bar
				//if(fullScreen) frame.setExtendedState(Frame.MAXIMIZED_BOTH); // full screen
																// mode
				//if(!fullScreen) frame.setTitle(TITLE); 
	            //if(!fullScreen) frame.pack();
				//window.requestFocus();
				
				if(!fullScreen) window.setSize(300,300);
				else window.setFullscreen(true);
				window.setVisible(true);
				
				//initiate the GLWindow
				//GLCapabilities caps = new GLCapabilities(GLProfile.get(GLProfile.GL2GL3));
		        //caps.setHardwareAccelerated(true);
		        
				animator.start(); // start the animation loop
			}
		});
	}

	public static void main(String[] args) {
		
		
		// Run the GUI codes in the event-dispatching thread for thread safety
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new Main(); // run the constructor
			}
		});
	}
}
