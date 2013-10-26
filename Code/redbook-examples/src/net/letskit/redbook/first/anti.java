package net.letskit.redbook.first;

import java.awt.event.*;
import javax.swing.*;

import javax.media.opengl.*;
import javax.media.opengl.glu.*;
import com.sun.opengl.util.*;
import net.letskit.redbook.glskeleton;

/**
 * This program draws antialiased lines in RGBA mode.
 *
 * @author Kiet Le (Java conversion)
 */
public class anti//
        extends glskeleton//
        implements GLEventListener//
        , KeyListener //
{
    private GLU glu;
    private GLUT glut;
    
    //
    public anti() {
    }
    
    public static void main(String[] args) {
        GLCapabilities caps = new GLCapabilities();
        caps.setSampleBuffers(true);// enable sample buffers for aliasing
        caps.setNumSamples(caps.getNumSamples() * 2);
        
        GLJPanel canvas = new GLJPanel(caps);
        anti demo = new anti();
        canvas.addGLEventListener(demo);
        demo.setCanvas(canvas);
        demo.setDefaultListeners(demo);
        
//        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("anti");
        frame.setSize(400, 400);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        frame.getContentPane().add(canvas);
        frame.setVisible(true);
        canvas.requestFocusInWindow();
    }
    
    /*
     * Initialize antialiasing for RGBA mode, including alpha blending, hint,
     * and line width. Print out implementation specific info on line width
     * granularity and width.
     */
    public void init(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();
        glu = new GLU();
        glut = new GLUT();
        //
        float values[] = new float[2];
        gl.glGetFloatv(GL.GL_LINE_WIDTH_GRANULARITY, values, 0);
        System.out
                .println("GL.GL_LINE_WIDTH_GRANULARITY value is " + values[0]);
        
        gl.glGetFloatv(GL.GL_LINE_WIDTH_RANGE, values, 0);
        System.out.println("GL.GL_LINE_WIDTH_RANGE values are " //
                + values[0] + ", " + values[1]);
        
        gl.glEnable(GL.GL_LINE_SMOOTH);
        gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        gl.glHint(GL.GL_LINE_SMOOTH_HINT, GL.GL_DONT_CARE);
        gl.glLineWidth(1.5f);
        
        gl.glShadeModel(GL.GL_FLAT);
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        gl.glDepthFunc(GL.GL_LESS);
        gl.glEnable(GL.GL_DEPTH_TEST);
    }
    
    /*
     * display() draws an icosahedron with a large alpha value, 1.0.
     */
    public void display(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();
        //
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        glut.glutWireIcosahedron();
        gl.glFlush();
    }
    
    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
        GL gl = drawable.getGL();
        //
        gl.glViewport(0, 0, w, h);
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(45.0, (float) w / (float) h, 3.0, 5.0);
        
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadIdentity();
        gl.glTranslatef(0.0f, 0.0f, -4.0f); /* move object into view */
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
    
}
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