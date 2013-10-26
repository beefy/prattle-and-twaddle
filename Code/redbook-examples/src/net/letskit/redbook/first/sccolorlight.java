package net.letskit.redbook.first;
import net.letskit.redbook.glskeleton;

import java.awt.event.*;
import javax.swing.*;

import javax.media.opengl.*;
import com.sun.opengl.util.*;

/**
 * This program demonstrates the use of a colored (magenta, in this example)
 * light source. Objects are drawn using a grey material characteristic. A
 * single light source illuminates the objects.
 * 
 * @author Kiet Le (Java port)
 */
public class sccolorlight//
        extends glskeleton//
        implements GLEventListener//
        , KeyListener//
{
    private GLUT glut;

    //
    public sccolorlight() {
    }

    public static void main(String[] args) {
        GLCapabilities caps = new GLCapabilities();
        GLJPanel canvas = new GLJPanel(caps);
        sccolorlight demo = new sccolorlight();
        canvas.addGLEventListener(demo);
        demo.setDefaultListeners(demo);
        
        //
//        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("sccolorlight");
        frame.setSize(500, 500);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.getContentPane().add(canvas);
        frame.setVisible(true);
        canvas.requestFocusInWindow();
    }

    /*
     * Initialize material property and light source.
     */
    public void init(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();
        glut = new GLUT();
        //
        float light_ambient[] = { 0.0f, 0.0f, 0.0f, 1.0f };
        float light_diffuse[] = { 1.0f, 0.0f, 1.0f, 1.0f };
        float light_specular[] = { 1.0f, 0.0f, 1.0f, 1.0f };
        /* light_position is NOT default value */
        float light_position[] = { 1.0f, 1.0f, 1.0f, 0.0f };

        gl.glLightfv(GL.GL_LIGHT0, GL.GL_AMBIENT, light_ambient, 0);
        gl.glLightfv(GL.GL_LIGHT0, GL.GL_DIFFUSE, light_diffuse, 0);
        gl.glLightfv(GL.GL_LIGHT0, GL.GL_SPECULAR, light_specular, 0);
        gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, light_position, 0);

        gl.glEnable(GL.GL_LIGHTING);
        gl.glEnable(GL.GL_LIGHT0);
        gl.glDepthFunc(GL.GL_LESS);
        gl.glEnable(GL.GL_DEPTH_TEST);
    }

    public void display(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();
        //
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        gl.glPushMatrix();
        gl.glRotatef(20.0f, 1.0f, 0.0f, 0.0f);

        gl.glPushMatrix();
        gl.glTranslatef(-0.75f, 0.5f, 0.0f);
        gl.glRotatef(90.0f, 1.0f, 0.0f, 0.0f);

        glut.glutSolidTorus(0.275f, 0.85f, 20, 20);
        gl.glPopMatrix();

        gl.glPushMatrix();
        gl.glTranslatef(-0.75f, -0.5f, 0.0f);
        gl.glRotatef(270.0f, 1.0f, 0.0f, 0.0f);
        glut.glutSolidCone(1.0f, 2.0f, 20, 20);
        gl.glPopMatrix();

        gl.glPushMatrix();
        gl.glTranslatef(0.75f, 0.0f, -1.0f);
        glut.glutSolidSphere(1.0f, 20, 20);
        gl.glPopMatrix();

        gl.glPopMatrix();
        gl.glFlush();
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
        GL gl = drawable.getGL();
        //
        gl.glViewport(0, 0, w, h);
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        if (w <= h)
            gl.glOrtho(-2.5, 2.5, -2.5 * (float) h / (float) w, 2.5 * (float) h
                    / (float) w, -10.0, 10.0);
        else
            gl.glOrtho(-2.5 * (float) w / (float) h, 2.5 * (float) w
                    / (float) h, -2.5, 2.5, -10.0, 10.0);
        gl.glMatrixMode(GL.GL_MODELVIEW);
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
