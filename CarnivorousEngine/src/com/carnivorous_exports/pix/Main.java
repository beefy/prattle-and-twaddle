package com.carnivorous_exports.pix;

import javax.swing.*;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;

import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.util.FPSAnimator;

import java.awt.Frame;

/**
 * Starts the project by setting up the animator and window.
 * It also creates an instance of Renderer, the most important class
 * in this engine.
 * 
 * @author		Nathaniel Schultz
 * @see         Renderer
 */
public class Main extends Frame {
	
	private static final int CANVAS_WIDTH = 500; // width of the drawable
	private static final int CANVAS_HEIGHT = 500; // height of the drawable
	private static final int FPS = 50; // animator's target frames per second
	private boolean fullScreen = true;
	private boolean cursorVisible = false;
	
	/** Constructor to setup the top-level container and animator */
	public Main() {
	
		// Run the GUI codes in the event-dispatching thread for thread safety
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				
				GLProfile glp = GLProfile.getDefault();
				//glp.initSingleton();
		        GLCapabilities caps = new GLCapabilities(glp);
		        caps.setBackgroundOpaque(false);
		        caps.setDoubleBuffered(true);
		        caps.setSampleBuffers(true);
		        System.out.println("Hardware Accelerated: " + caps.getHardwareAccelerated());
				System.out.println("GLCapabilities: " + caps);
		        
				GLWindow window = GLWindow.create(caps);
				
				Renderer renderer = new Renderer(window);
				
				window.addGLEventListener(renderer);

				// Create a animator that drives canvas' display() at the
				// specified FPS.
				FPSAnimator animator = new FPSAnimator(window, FPS, true);

				if(!fullScreen) {
					window.setFullscreen(false);
					window.setSize(CANVAS_WIDTH,CANVAS_HEIGHT);
				}
				else window.setFullscreen(true);
				window.setVisible(true);
				
				if(cursorVisible) window.setPointerVisible(true);
				else window.setPointerVisible(false);
		        
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
