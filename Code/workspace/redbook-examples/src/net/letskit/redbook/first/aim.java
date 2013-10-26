package net.letskit.redbook.first;

import java.awt.event.*;
import javax.swing.*;

import javax.media.opengl.*;
import javax.media.opengl.glu.*;
import com.sun.opengl.util.*;
import net.letskit.redbook.glskeleton;

/**
 * This program calculates the fovy (field of view angle in the y direction), by
 * using trigonometry, given the size of an object and its size.
 *
 * @author Kiet Le (Java port)
 */
public class aim//
        extends glskeleton//
        implements GLEventListener//
        , KeyListener //
{
    private GLU glu;
    private GLUT glut;
    
    //
    public aim() {
    }
    
    public static void main(String[] args) {
        GLCapabilities caps = new GLCapabilities();
        GLCanvas canvas = new GLCanvas(caps);
        aim demo = new aim();
        canvas.addGLEventListener(demo);
        demo.setCanvas(canvas);
        demo.setDefaultListeners(demo);
        //
//        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("aim");
        frame.setSize(500, 500);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        frame.getContentPane().add(canvas);
        frame.setVisible(true);
        canvas.requestFocusInWindow();
    }
    
    public void init(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();
        glu = new GLU();
        glut = new GLUT();
        //
        gl.glShadeModel(GL.GL_FLAT);
    }
    
    /*
     * Clear the screen. Set the current color to white. Draw the wire frame
     * cube and sphere.
     */
    public void display(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();
        //
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        gl.glColor3f(1.0f, 1.0f, 1.0f);
        
        gl.glLoadIdentity();
        /* glTranslatef() as viewing transformation */
        gl.glTranslatef(0.0f, 0.0f, -5.0f);
        glut.glutWireCube(2.0f);
        glut.glutWireSphere(1.0f, 10, 10);
        gl.glFlush();
    }
    
    /*
     * Called when the window is first opened and whenever the window is
     * reconfigured (moved or resized).
     */
    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
        GL gl = drawable.getGL();
        //
        double theta;
        
        gl.glViewport(0, 0, w, h);
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        theta = calculateAngle(2.0, 5.0);
        glu.gluPerspective(theta, (float) w / (float) h, 1.0, 20.0);
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadIdentity();
    }
    
    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
            boolean deviceChanged) {
    }
    
    /*
     * atan2 () is a system math routine which calculates the arctangent of an
     * angle, given length of the opposite and adjacent sides of a right
     * triangle. atan2 () is not an OpenGL routine.
     */
    private double calculateAngle(double size, double distance) {
        double radtheta, degtheta;
        
        radtheta = 2.0 * Math.atan2(size / 2.0, distance);
        degtheta = (180.0 * radtheta) / Math.PI;
        System.out.println("degtheta is " + degtheta);
        return ((double) degtheta);
    }
    
    public void keyTyped(KeyEvent key) {
        // TODO Auto-generated method stub
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