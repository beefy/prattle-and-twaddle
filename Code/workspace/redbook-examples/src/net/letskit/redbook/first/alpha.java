package net.letskit.redbook.first;
import net.letskit.redbook.glskeleton;

import javax.swing.*;

import java.awt.event.*;

import javax.media.opengl.*;
import javax.media.opengl.glu.*;

/**
 * This program draws several overlapping filled polygons to demonstrate the
 * effect order has on alpha blending results. Use the 't' key to toggle the
 * order of drawing polygons.
 *
 * @author Kiet Le (Java conversion)
 */
public class alpha//
        extends glskeleton//
        implements GLEventListener//
        , KeyListener //
{
    
    public alpha() {
    }
    
    /*
     * Main Loop Open window with initial window size, title bar, RGBA display
     * mode, and handle input events.
     */
    public static void main(String[] args) {
        GLCapabilities caps = new GLCapabilities();
        caps.setSampleBuffers(true);// enable sample buffers for aliasing
        caps.setNumSamples(caps.getNumSamples() * 2);
        
        GLJPanel canvas = new GLJPanel(caps);
        alpha demo = new alpha();
        canvas.addGLEventListener(demo);
        demo.setCanvas(canvas);
        demo.setDefaultListeners(demo);
        //
//        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("alpha");
        frame.setSize(500, 500);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        frame.getContentPane().add(canvas);
        frame.setVisible(true);
        canvas.requestFocusInWindow();
    }
    
    /*
     * Initialize alpha blending function.
     */
    public void init(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();
        //
        gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        gl.glShadeModel(GL.GL_FLAT);
        gl.glClearColor(0, 0, 0, 0);
    }
    
    public void display(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();
        //
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        gl.glColor4f(1.0f, 1.0f, 0.0f, 0.75f);
        gl.glRectf(0.0f, 0.0f, 0.5f, 1.0f);
        
        gl.glColor4f(0.0f, 1.0f, 1.0f, 0.75f);
        gl.glRectf(0.0f, 0.0f, 1.f, 0.5f);
        /* draw colored polygons in reverse order in upper right */
        gl.glColor4f(0.f, 1.0f, 1.0f, 0.75f);
        gl.glRectf(0.5f, 0.5f, 1.0f, 1.0f);
        
        gl.glColor4f(1.0f, 1.0f, 0.0f, 0.75f);
        gl.glRectf(0.5f, 0.5f, 1.0f, 1.0f);
        
        gl.glEnd();
        drawable.swapBuffers();
    }
    
    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
        GL gl = drawable.getGL();
        GLU glu = new GLU();
        //
        gl.glViewport(0, 0, w, h);
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        if (w <= h)
            glu.gluOrtho2D(0.0, 1.0, 0.0, 1.0 * h / w);
        else
            glu.gluOrtho2D(0.0, 1.0 * w / h, 0.0, 1.0);
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadIdentity();
    }
    
    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
            boolean deviceChanged) {
    }
    
    public void keyTyped(KeyEvent key) {
    }
    
    public void keyPressed(KeyEvent key) {
        switch (key.getKeyCode()) {
            case KeyEvent.VK_ESCAPE:
                super.runExit();
                break;
                
            default:
                break;
        }
    }
    
    public void keyReleased(KeyEvent key) {
    }
    
}//
/*
 *  For the software in this directory
 * (c) Copyright 1993, Silicon Graphics, Inc.
 * ALL RIGHTS RESERVED
 * Permission to use, copy, modify, and distribute this software for
 * any purpose and without fee is hereby granted, provided that the above
 * copyright notice appear in all copies and that both the copyright notice
 * and this permission notice appear in supporting documentation, and that
 * the name of Silicon Graphics, Inc. not be used in advertising
 * or publicity pertaining to distribution of the software without specific,
 * written prior permission.
 *
 * THE MATERIAL EMBODIED ON THIS SOFTWARE IS PROVIDED TO YOU "AS-IS"
 * AND WITHOUT WARRANTY OF ANY KIND, EXPRESS, IMPLIED OR OTHERWISE,
 * INCLUDING WITHOUT LIMITATION, ANY WARRANTY OF MERCHANTABILITY OR
 * FITNESS FOR A PARTICULAR PURPOSE.  IN NO EVENT SHALL SILICON
 * GRAPHICS, INC.  BE LIABLE TO YOU OR ANYONE ELSE FOR ANY DIRECT,
 * SPECIAL, INCIDENTAL, INDIRECT OR CONSEQUENTIAL DAMAGES OF ANY
 * KIND, OR ANY DAMAGES WHATSOEVER, INCLUDING WITHOUT LIMITATION,
 * LOSS OF PROFIT, LOSS OF USE, SAVINGS OR REVENUE, OR THE CLAIMS OF
 * THIRD PARTIES, WHETHER OR NOT SILICON GRAPHICS, INC.  HAS BEEN
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS, HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, ARISING OUT OF OR IN CONNECTION WITH THE
 * POSSESSION, USE OR PERFORMANCE OF THIS SOFTWARE.
 *
 * US Government Users Restricted Rights
 * Use, duplication, or disclosure by the Government is subject to
 * restrictions set forth in FAR 52.227.19(c)(2) or subparagraph
 * (c)(1)(ii) of the Rights in Technical Data and Computer Software
 * clause at DFARS 252.227-7013 and/or in similar or successor
 * clauses in the FAR or the DOD or NASA FAR Supplement.
 * Unpublished-- rights reserved under the copyright laws of the
 * United States.  Contractor/manufacturer is Silicon Graphics,
 * Inc., 2011 N.  Shoreline Blvd., Mountain View, CA 94039-7311.
 *
 * OpenGL(TM) is a trademark of Silicon Graphics, Inc.
 */