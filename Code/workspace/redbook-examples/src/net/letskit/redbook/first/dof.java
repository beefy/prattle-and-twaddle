package net.letskit.redbook.first;
import net.letskit.redbook.glskeleton;
import net.letskit.redbook.jitter;
import net.letskit.redbook.jitter.jitter_point;

import java.awt.event.*;
import javax.swing.*;

import javax.media.opengl.*;
import javax.media.opengl.glu.*;
import com.sun.opengl.util.*;

/**
 * This program demonstrates use of the accumulation buffer to create an
 * out-of-focus depth-of-field effect. The teapots are drawn several times into
 * the accumulation buffer. The viewing volume is jittered, except at the focal
 * point, where the viewing volume is at the same position, each time. In this
 * case, the gold teapot remains in focus.
 *
 * @author Kiet Le (java port)
 */
public class dof//
        extends glskeleton//
        implements GLEventListener//
        , KeyListener//
{
    private GLUT glut;
    //
    private int teapotList;
    
    public dof() {
    }
    
    public static void main(String[] args) {
        
        // Be certain you request an accumulation buffer.
        GLCapabilities caps = new GLCapabilities();
        caps.setAccumAlphaBits(16);
        caps.setAccumBlueBits(16);
        caps.setAccumGreenBits(16);
        caps.setAccumRedBits(16);
        GLJPanel canvas = new GLJPanel(caps);
        dof demo = new dof();
        demo.setCanvas(canvas);
        canvas.addGLEventListener(demo);
        demo.setDefaultListeners(demo);
        
//        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("dof");
        frame.setSize(400, 400);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        frame.getContentPane().add(canvas);
        frame.setVisible(true);
        canvas.requestFocusInWindow();
    }
    
    public void init(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();
        glut = new GLUT();
        //
        float ambient[] = new float[] { 0.0f, 0.0f, 0.0f, 1.0f };
        float diffuse[] = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };
        float specular[] = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };
        float position[] = new float[] { 0.0f, 3.0f, 3.0f, 0.0f };
        
        float lmodel_ambient[] = new float[] { 0.2f, 0.2f, 0.2f, 1.0f };
        float local_view[] = new float[] { 0.0f };
        
        gl.glLightfv(GL.GL_LIGHT0, GL.GL_AMBIENT, ambient, 0);
        gl.glLightfv(GL.GL_LIGHT0, GL.GL_DIFFUSE, diffuse, 0);
        gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, position, 0);
        
        gl.glLightModelfv(GL.GL_LIGHT_MODEL_AMBIENT, lmodel_ambient, 0);
        gl.glLightModelfv(GL.GL_LIGHT_MODEL_LOCAL_VIEWER, local_view, 0);
        
        gl.glFrontFace(GL.GL_CW);
        gl.glEnable(GL.GL_LIGHTING);
        gl.glEnable(GL.GL_LIGHT0);
        gl.glEnable(GL.GL_AUTO_NORMAL);
        gl.glEnable(GL.GL_NORMALIZE);
        gl.glEnable(GL.GL_DEPTH_TEST);
        
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        gl.glClearAccum(0.0f, 0.0f, 0.0f, 0.0f);
        /* make teapot display list */
        teapotList = gl.glGenLists(1);
        gl.glNewList(teapotList, GL.GL_COMPILE);
        glut.glutSolidTeapot(0.5f);
        gl.glEndList();
    }
    
    public void display(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();
        //
        int jit;
        int viewport[] = new int[4];
        
        gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);
        gl.glClear(GL.GL_ACCUM_BUFFER_BIT);
        
        for (jit = 0; jit < 8; jit++) {
            gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
            accPerspective(gl, 45.0, (double) viewport[2]
                    / (double) viewport[3], 1.0, 15.0, 0.0, 0.0,
                    0.33 * jitter.j8[jit].x, 0.33 * jitter.j8[jit].y, 5.0);
            
            /* ruby, gold, silver, emerald, and cyan teapots */
            renderTeapot(gl, -1.1f, -0.5f, -4.5f, 0.1745f, 0.01175f, 0.01175f,
                    0.61424f, 0.04136f, 0.04136f, 0.727811f, 0.626959f,
                    0.626959f, 0.6f);
            renderTeapot(gl, -0.5f, -0.5f, -5.0f, 0.24725f, 0.1995f, 0.0745f,
                    0.75164f, 0.60648f, 0.22648f, 0.628281f, 0.555802f,
                    0.366065f, 0.4f);
            renderTeapot(gl, 0.2f, -0.5f, -5.5f, 0.19225f, 0.19225f, 0.19225f,
                    0.50754f, 0.50754f, 0.50754f, 0.508273f, 0.508273f,
                    0.508273f, 0.4f);
            renderTeapot(gl, 1.0f, -0.5f, -6.0f, 0.0215f, 0.1745f, 0.0215f,
                    0.07568f, 0.61424f, 0.07568f, 0.633f, 0.727811f, 0.633f,
                    0.6f);
            renderTeapot(gl, 1.8f, -0.5f, -6.5f, 0.0f, 0.1f, 0.06f, 0.0f,
                    0.50980392f, 0.50980392f, 0.50196078f, 0.50196078f,
                    0.50196078f, .25f);
            gl.glAccum(GL.GL_ACCUM, 0.125f);
        }
        gl.glAccum(GL.GL_RETURN, 1.0f);
        gl.glFlush();
    }
    
    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
        GL gl = drawable.getGL();
        //
        gl.glViewport(0, 0, w, h);
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
    private void accPerspective(GL gl, double fovy, double aspect, double near,
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
    
    private void renderTeapot(GL gl, float x, float y, float z, float ambr,
            float ambg, float ambb, float difr, float difg, float difb,
            float specr, float specg, float specb, float shine) {
        float mat[] = new float[4];
        
        gl.glPushMatrix();
        gl.glTranslatef(x, y, z);
        mat[0] = ambr;
        mat[1] = ambg;
        mat[2] = ambb;
        mat[3] = 1.0f;
        gl.glMaterialfv(GL.GL_FRONT, GL.GL_AMBIENT, mat, 0);
        mat[0] = difr;
        mat[1] = difg;
        mat[2] = difb;
        gl.glMaterialfv(GL.GL_FRONT, GL.GL_DIFFUSE, mat, 0);
        mat[0] = specr;
        mat[1] = specg;
        mat[2] = specb;
        gl.glMaterialfv(GL.GL_FRONT, GL.GL_SPECULAR, mat, 0);
        gl.glMaterialf(GL.GL_FRONT, GL.GL_SHININESS, shine * 128.0f);
        gl.glCallList(teapotList);
        gl.glPopMatrix();
    }
    
    /*
     * display() draws 5 teapots into the accumulation buffer several times;
     * each time with a jittered perspective. The focal point is at z = 5.0, so
     * the gold teapot will stay in focus. The amount of jitter is adjusted by
     * the magnitude of the accPerspective() jitter; in this example, 0.33. In
     * this example, the teapots are drawn 8 times. See jitter.h
     */
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
