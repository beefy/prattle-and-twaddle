package net.letskit.redbook.first;

import java.awt.event.*;

import javax.swing.*;

import javax.media.opengl.*;
import com.sun.opengl.util.*;
import net.letskit.redbook.glskeleton;
import net.letskit.redbook.jitter;
import net.letskit.redbook.jitter.jitter_point;

/**
 * Use the accumulation buffer to do full-scene antialiasing on a scene with
 * perspective projection.
 *
 * @author Kiet Le (Java port)
 */
public class accpersp//
        extends glskeleton//
        implements GLEventListener//
        , KeyListener//
{
    private GLUT glut;
    //
    private static final int ACSIZE = 8;
    
    //
    public accpersp() {
    }
    
    public static void main(String[] args) {
        GLCapabilities caps = new GLCapabilities();
        caps.setAccumBlueBits(16);
        caps.setAccumGreenBits(16);
        caps.setAccumRedBits(16);
        caps.setAccumAlphaBits(16);
        GLCanvas canvas = new GLCanvas(caps);
        accpersp demo = new accpersp();
        canvas.addGLEventListener(demo);
        demo.setCanvas(canvas);
        demo.setDefaultListeners(demo);
        //
//        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("accpersp");
        frame.setSize(250, 250);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        frame.getContentPane().add(canvas);
        frame.pack();
        frame.setVisible(true);
        canvas.requestFocusInWindow();
    }
    
    public void init(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();
        glut = new GLUT();
        //
        float mat_ambient[] = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };
        float mat_specular[] = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };
        float light_position[] = new float[] { 0.0f, 0.0f, 10.0f, 1.0f };
        float lm_ambient[] = new float[] { 0.2f, 0.2f, 0.2f, 1.0f };
        
        gl.glMaterialfv(GL.GL_FRONT, GL.GL_AMBIENT, mat_ambient, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL.GL_SPECULAR, mat_specular, 0);
        gl.glMaterialf(GL.GL_FRONT, GL.GL_SHININESS, 50.0f);
        gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, light_position, 0);
        gl.glLightModelfv(GL.GL_LIGHT_MODEL_AMBIENT, lm_ambient, 0);
        
        gl.glEnable(GL.GL_LIGHTING);
        gl.glEnable(GL.GL_LIGHT0);
        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glShadeModel(GL.GL_FLAT);
        
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        gl.glClearAccum(0.0f, 0.0f, 0.0f, 0.0f);
    }
    
    public void display(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();
        //
        int viewport[] = new int[4];
        int jit;
        
        gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);
        
        gl.glClear(GL.GL_ACCUM_BUFFER_BIT);
        for (jit = 0; jit < ACSIZE; jit++) {
            gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
            accPerspective(gl, 50.0, (double) viewport[2]
                    / (double) viewport[3], 1.0, 15.0, jitter.j8[jit].x,
                    jitter.j8[jit].y, 0.0, 0.0, 1.0);
            displayObjects(gl);
            gl.glAccum(GL.GL_ACCUM, 1.0f / ACSIZE);
        }
        gl.glAccum(GL.GL_RETURN, 1.0f);
        gl.glFlush();
    }
    
    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
        GL gl = drawable.getGL();
        //
        gl.glViewport(x, y, w, h);
    }
    
    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
            boolean deviceChanged) {
        
    }
    
    /*
     * accFrustum() The first 6 arguments are identical to the glFrustum() call.
     * pixdx and pixdy are anti-alias jitter in pixels. Set both equal to 0.0
     * for no anti-alias jitter. eyedx and eyedy are depth-of field jitter in
     * pixels. Set both equal to 0.0 for no depth of field effects. focus is
     * distance from eye to plane in focus. focus must be greater than, but not
     * equal to 0.0. Note that accFrustum() calls glTranslatef(). You will
     * probably want to insure that your ModelView matrix has been initialized
     * to identity before calling accFrustum().
     */
    private void accFrustum(GL gl, double left, double right, double bottom,
            double top, double near, double far, double pixdx, double pixdy,
            double eyedx, double eyedy, double focus) {
        double xwsize, ywsize;
        double dx, dy;
        int viewport[] = new int[4];
        
        gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);
        
        xwsize = right - left;
        ywsize = top - bottom;
        
        dx = -(pixdx * xwsize / (double) viewport[2] + eyedx * near / focus);
        dy = -(pixdy * ywsize / (double) viewport[3] + eyedy * near / focus);
        
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glFrustum(left + dx, right + dx, bottom + dy, top + dy, near, far);
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadIdentity();
        gl.glTranslatef((float) -eyedx, (float) -eyedy, 0.0f);
    }
    
    /*
     * accPerspective() The first 4 arguments are identical to the
     * gluPerspective() call. pixdx and pixdy are anti-alias jitter in pixels.
     * Set both equal to 0.0 for no anti-alias jitter. eyedx and eyedy are
     * depth-of field jitter in pixels. Set both equal to 0.0 for no depth of
     * field effects. focus is distance from eye to plane in focus. focus must
     * be greater than, but not equal to 0.0. Note that accPerspective() calls
     * accFrustum().
     */
    void accPerspective(GL gl, double fovy, double aspect, double near,
            double far, double pixdx, double pixdy, double eyedx, double eyedy,
            double focus) {
        double fov2, left, right, bottom, top;
        
        fov2 = ((fovy * Math.PI) / 180.0) / 2.0;
        
        top = near / (Math.cos(fov2) / Math.sin(fov2));
        bottom = -top;
        
        right = top * aspect;
        left = -right;
        
        accFrustum(gl, left, right, bottom, top, near, far, pixdx, pixdy,
                eyedx, eyedy, focus);
    }
    
    private void displayObjects(GL gl) {
        float torus_diffuse[] = new float[] { 0.7f, 0.7f, 0.0f, 1.0f };
        float cube_diffuse[] = new float[] { 0.0f, 0.7f, 0.7f, 1.0f };
        float sphere_diffuse[] = new float[] { 0.7f, 0.0f, 0.7f, 1.0f };
        float octa_diffuse[] = new float[] { 0.7f, 0.4f, 0.4f, 1.0f };
        
        gl.glPushMatrix();
        gl.glTranslatef(0.0f, 0.0f, -5.0f);
        gl.glRotatef(30.0f, 1.0f, 0.0f, 0.0f);
        
        gl.glPushMatrix();
        gl.glTranslatef(-0.80f, 0.35f, 0.0f);
        gl.glRotatef(100.0f, 1.0f, 0.0f, 0.0f);
        gl.glMaterialfv(GL.GL_FRONT, GL.GL_DIFFUSE, torus_diffuse, 0);
        glut.glutSolidTorus(0.275, 0.85, 16, 16);
        gl.glPopMatrix();
        
        gl.glPushMatrix();
        gl.glTranslatef(-0.75f, -0.50f, 0.0f);
        gl.glRotatef(45.0f, 0.0f, 0.0f, 1.0f);
        gl.glRotatef(45.0f, 1.0f, 0.0f, 0.0f);
        gl.glMaterialfv(GL.GL_FRONT, GL.GL_DIFFUSE, cube_diffuse, 0);
        glut.glutSolidCube(1.5f);
        gl.glPopMatrix();
        
        gl.glPushMatrix();
        gl.glTranslatef(0.75f, 0.60f, 0.0f);
        gl.glRotatef(30.0f, 1.0f, 0.0f, 0.0f);
        gl.glMaterialfv(GL.GL_FRONT, GL.GL_DIFFUSE, sphere_diffuse, 0);
        glut.glutSolidSphere(1.0, 16, 16);
        gl.glPopMatrix();
        
        gl.glPushMatrix();
        gl.glTranslatef(0.70f, -0.90f, 0.25f);
        gl.glMaterialfv(GL.GL_FRONT, GL.GL_DIFFUSE, octa_diffuse, 0);
        glut.glutSolidOctahedron();
        gl.glPopMatrix();
        
        gl.glPopMatrix();
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