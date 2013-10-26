package net.letskit.redbook.first;

import java.awt.event.*;
import javax.swing.*;

import java.nio.*;

import javax.media.opengl.*;
import javax.media.opengl.glu.*;
import com.sun.opengl.util.*;
import net.letskit.redbook.glskeleton;

/**
 * This program renders a wireframe (mesh) Bezier surface, using two-dimensional
 * evaluators.
 * 
 * @author Kiet Le (Java conversion)
 */
public class bezsurf//
extends glskeleton//
        implements GLEventListener//
        , KeyListener //
{
    // as from C version of file
    private static final float ctrlpoints[][][] = new float[][][] {
            { { -1.5f, -1.5f, 4.0f }, { -0.5f, -1.5f, 2.0f },
                    { 0.5f, -1.5f, -1.0f }, { 1.5f, -1.5f, 2.0f } },
            { { -1.5f, -0.5f, 1.0f }, { -0.5f, -0.5f, 3.0f },
                    { 0.5f, -0.5f, 0.0f }, { 1.5f, -0.5f, -1.0f } },
            { { -1.5f, 0.5f, 4.0f }, { -0.5f, 0.5f, 0.0f },
                    { 0.5f, 0.5f, 3.0f }, { 1.5f, 0.5f, 4.0f } },
            { { -1.5f, 1.5f, -2.0f }, { -0.5f, 1.5f, -2.0f },
                    { 0.5f, 1.5f, 0.0f }, { 1.5f, 1.5f, -1.0f } } };
    // need float buffer instead of n-dimensional array above
    private FloatBuffer ctrlpointsBuf = BufferUtil
            .newFloatBuffer(ctrlpoints.length * ctrlpoints[0].length
                    * ctrlpoints[0][0].length);
    {// SO copy 4x4x3 array above to float buffer
        for (int i = 0; i < ctrlpoints.length; i++) {
            // System.out.print(ctrlpoints.length+ " ");
            for (int j = 0; j < ctrlpoints[0].length; j++) {
                // System.out.println(ctrlpoints[0][0].length+" ");
                for (int k = 0; k < ctrlpoints[0][0].length; k++) {
                    ctrlpointsBuf.put(ctrlpoints[i][j][k]);
                    System.out.print(ctrlpoints[i][j][k] + " ");
                }
                System.out.println();
            }
        }
        // THEN rewind it before use
        ctrlpointsBuf.rewind();
    }

    //
    public bezsurf() {
    }

    public static void main(String[] args) {
        GLCapabilities caps = new GLCapabilities();
        GLJPanel canvas = new GLJPanel(caps);
        bezsurf demo = new bezsurf();
        canvas.addGLEventListener(demo);
        demo.setCanvas(canvas);
        demo.setDefaultListeners(demo);

//        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("bezsurf");
        frame.setSize(512, 256);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.getContentPane().add(canvas);
        frame.setVisible(true);
        canvas.requestFocusInWindow();
    }

    public void init(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();
        //
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        gl.glEnable(GL.GL_DEPTH_TEST);

        gl.glEnable(GL.GL_MAP2_VERTEX_3);
        gl.glEnable(GL.GL_AUTO_NORMAL);
        gl.glEnable(GL.GL_NORMALIZE);
        gl.glMap2f(GL.GL_MAP2_VERTEX_3, 0, 1, 3, 4, 0, 1, 12, 4, ctrlpointsBuf);
        gl.glMapGrid2f(20, 0.0f, 1.0f, 20, 0.0f, 1.0f);

        initlights(gl);
    }

    public void display(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();
        //
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        gl.glPushMatrix();
        gl.glRotatef(85.0f, 1.0f, 1.0f, 1.0f);
        gl.glEvalMesh2(GL.GL_FILL, 0, 20, 0, 20);
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
            gl.glOrtho(-4.0, 4.0, -4.0 * (float) h / (float) w, 4.0 * (float) h
                    / (float) w, -4.0, 4.0);
        else
            gl.glOrtho(-4.0 * (float) w / (float) h, 4.0 * (float) w
                    / (float) h, -4.0, 4.0, -4.0, 4.0);
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
            boolean deviceChanged) {
    }

    private void initlights(GL gl) {
        float ambient[] = { 0.2f, 0.2f, 0.2f, 1.0f };
        float position[] = { 0.0f, 0.0f, 2.0f, 1.0f };
        float mat_diffuse[] = { 0.6f, 0.6f, 0.6f, 1.0f };
        float mat_specular[] = { 1.0f, 1.0f, 1.0f, 1.0f };
        float mat_shininess[] = { 50.0f };

        gl.glEnable(GL.GL_LIGHTING);
        gl.glEnable(GL.GL_LIGHT0);

        gl.glLightfv(GL.GL_LIGHT0, GL.GL_AMBIENT, ambient, 0);
        gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, position, 0);

        gl.glMaterialfv(GL.GL_FRONT, GL.GL_DIFFUSE, mat_diffuse, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL.GL_SPECULAR, mat_specular, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL.GL_SHININESS, mat_shininess, 0);
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
