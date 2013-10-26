package net.letskit.redbook.first;
import net.letskit.redbook.glskeleton;

import java.awt.event.*;
import javax.swing.*;

import java.nio.*;

import javax.media.opengl.*;
import javax.media.opengl.glu.*;
import com.sun.opengl.util.*;

/**
 * This program renders a wireframe (mesh) Bezier surface, using two-dimensional
 * evaluators.
 * 
 * @author Kiet Le (Java conversion)
 */
public class bezmesh//
        extends glskeleton//
        implements GLEventListener//
        , KeyListener//
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
    public bezmesh() {
    }

    public static void main(String[] args) {
        GLCapabilities caps = new GLCapabilities();
        bezmesh demo = new bezmesh();

        GLJPanel canvas = new GLJPanel(caps);
        canvas.addGLEventListener(demo);
        canvas.addKeyListener(demo);
        //
//        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("bezmesh");
        frame.getContentPane().add(canvas);
        frame.setSize(500, 500);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.getContentPane().add(canvas);
        frame.setVisible(true);
        canvas.requestFocusInWindow();
    }

    public void init(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();
        //
        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        gl.glMap2f(GL.GL_MAP2_VERTEX_3, 0, 1, 3, 4, 0, 1, 12, 4, ctrlpointsBuf);
        gl.glEnable(GL.GL_MAP2_VERTEX_3);
        gl.glMapGrid2f(20, 0.0f, 1.0f, 20, 0.0f, 1.0f);
        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glShadeModel(GL.GL_FLAT);
    }

    public void display(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();
        //
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        gl.glColor3f(1.0f, 1.0f, 1.0f);
        gl.glPushMatrix();
        gl.glRotatef(85.0f, 1.0f, 1.0f, 1.0f);
        for (int j = 0; j <= 8; j++) {
            gl.glBegin(GL.GL_LINE_STRIP);
            for (int i = 0; i <= 30; i++)
                gl.glEvalCoord2f((float) i / 30.0f, (float) j / 8.0f);
            gl.glEnd();
            gl.glBegin(GL.GL_LINE_STRIP);
            for (int i = 0; i <= 30; i++)
                gl.glEvalCoord2f((float) j / 8.0f, (float) i / 30.0f);
            gl.glEnd();
        }
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
