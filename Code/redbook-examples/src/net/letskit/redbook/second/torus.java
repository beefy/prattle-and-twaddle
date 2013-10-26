package net.letskit.redbook.second;
import net.letskit.redbook.glskeleton;

import javax.swing.*;
import javax.media.opengl.*;
import javax.media.opengl.glu.*;
import java.awt.event.*;

/**
 * This program demonstrates the creation of a display list.
 * 
 * @author Kiet Le (java port)
 */
public class torus//
        extends glskeleton//
        implements GLEventListener//
        , KeyListener//
{ 
    private int theTorus;
    private KeyEvent key;
    private float angleX = 30f;
    private float angleY = 30f;

    public torus() {
    }

    public static void main(String[] args) {
        GLCapabilities caps = new GLCapabilities();
        GLJPanel canvas = new GLJPanel(caps);

        torus demo = new torus();
        demo.setCanvas(canvas);
        canvas.addGLEventListener(demo);
        demo.setDefaultListeners(demo);

//        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("torus");
        frame.setSize(512, 512);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.getContentPane().add(canvas);
        frame.setVisible(true);
        canvas.requestFocusInWindow();
    }

    private void drawTorus(GL gl, int numc, int numt) {
        double s, t, x, y, z, twopi = 2 * Math.PI;
        for (int i = 0; i < numc; i++) {
            gl.glBegin(GL.GL_QUAD_STRIP);
            for (int j = 0; j <= numt; j++) {
                for (int k = 1; k >= 0; k--) {
                    s = (i + k) % numc + 0.5;
                    t = j % numt;
                    x = (1 + 0.1 * Math.cos(s * twopi / numc))
                            * Math.cos(t * twopi / numt);
                    y = (1 + 0.1 * Math.cos(s * twopi / numc))
                            * Math.sin(t * twopi / numt);
                    z = 0.1 * Math.sin(s * twopi / numc);
                    gl.glVertex3d(x, y, z);
                }// k
            }// j
            gl.glEnd();
        }// i
    }

    public void init(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();
        //
        theTorus = gl.glGenLists(1);
        gl.glNewList(theTorus, GL.GL_COMPILE);
        drawTorus(gl, 8, 25);
        gl.glEndList();
        //
        gl.glShadeModel(GL.GL_FLAT);
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    }

    public void display(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();
        GLU glu = new GLU();
        //
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        //
        if (key != null) {
            switch (key.getKeyCode()) {
            case KeyEvent.VK_X:
                gl.glRotated(angleX, 1f, 0f, 0f);
                break;
            case KeyEvent.VK_Y:
                gl.glRotated(angleY, 0f, 1f, 0f);
                break;
            case KeyEvent.VK_I:
                gl.glLoadIdentity();
                glu.gluLookAt(0, 0, 10, 0, 0, 0, 0, 1, 0);
                break;
            default:
                break;
            }
        }

        gl.glColor3f(1.0f, 1.0f, 1.0f);
        gl.glCallList(theTorus);
        gl.glPopMatrix();
        gl.glFlush();
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int width,
            int height) {
        GL gl = drawable.getGL();
        GLU glu = new GLU();
        //
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(30, (float) width / (float) height, 1.0, 100.0);
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadIdentity();
        glu.gluLookAt(0, 0, 10, 0, 0, 0, 0, 1, 0);
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
            boolean deviceChanged) {
    }

    public void keyTyped(KeyEvent key) {
    }

    public void keyPressed(KeyEvent key) {
        this.key = key;
        switch (key.getKeyCode()) {
        case KeyEvent.VK_ESCAPE:
            System.exit(0);
            break;

        default:
            break;
        }
        super.refresh();
    }

    public void keyReleased(KeyEvent key) {
    }
}
