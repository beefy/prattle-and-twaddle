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

				if(!fullScreen) window.setSize(300,300);
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
