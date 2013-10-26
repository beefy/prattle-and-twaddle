package net.letskit.redbook.first;
import net.letskit.redbook.glskeleton;

import java.awt.event.*;
import javax.swing.*;

import javax.media.opengl.*;
import javax.media.opengl.glu.*;
import com.sun.opengl.util.*;

/**
 * This program demonstrates smooth shading. A smooth shaded polygon is drawn in
 * a 2-D projection.
 * 
 * @author Kiet Le (java port)
 */
public class smooth//
        extends glskeleton//
        implements GLEventListener//
        , KeyListener//
{
    private GLU glu;

    //
    public smooth() {
    }

    public static void main(String[] args) {
        GLCapabilities caps = new GLCapabilities();
        GLJPanel canvas = new GLJPanel(caps);
        smooth demo = new smooth();
        canvas.addGLEventListener(demo);
        canvas.addKeyListener(demo);
        //
//        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("smooth");
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
        //
        // gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        gl.glShadeModel(GL.GL_SMOOTH);
    }

    public void display(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();
        //
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        triangle(gl);
        gl.glFlush();
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
        GL gl = drawable.getGL();
        //
        gl.glViewport(0, 0, w, h);
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        if (w <= h)
            glu.gluOrtho2D(0.0, 30.0, 0.0, 30.0 * (float) h / (float) w);
        else
            glu.gluOrtho2D(0.0, 30.0 * (float) w / (float) h, 0.0, 30.0);
        gl.glMatrixMode(GL.GL_MODELVIEW);
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
            boolean deviceChanged) {
    }

    private void triangle(GL gl) {
        gl.glBegin(GL.GL_TRIANGLES);
        gl.glColor3f(1.0f, 0.0f, 0.0f);
        gl.glVertex2f(5.0f, 5.0f);
        gl.glColor3f(0.0f, 1.0f, 0.0f);
        gl.glVertex2f(25.0f, 5.0f);
        gl.glColor3f(0.0f, 0.0f, 1.0f);
        gl.glVertex2f(5.0f, 25.0f);
        gl.glEnd();
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
