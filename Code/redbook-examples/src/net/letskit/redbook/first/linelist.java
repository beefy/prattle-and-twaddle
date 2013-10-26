package net.letskit.redbook.first;
import net.letskit.redbook.glskeleton;

import java.awt.event.*;
import javax.swing.*;

import javax.media.opengl.*;
import com.sun.opengl.util.*;

/**
 * This program demonstrates using display lists to call different line
 * stipples.
 * 
 * @author Kiet Le (Java conversion)
 */
public class linelist extends glskeleton//
        implements GLEventListener//
        , KeyListener//
{ 
    private int offset;

    //
    public linelist() {
    }

    public static void main(String[] args) {
        GLCapabilities caps = new GLCapabilities();
        GLJPanel canvas = new GLJPanel(caps);
        linelist demo  = new linelist();
        canvas.addGLEventListener(demo);
        demo.setCanvas(canvas);
        demo.setDefaultListeners(demo);
         
//        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("linelist");
        frame.setSize(400, 150);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.getContentPane().add(canvas);
        frame.setVisible(true);
        canvas.requestFocusInWindow();
    }

    public void init(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();
        //
        /* background to be cleared to black */
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        gl.glShadeModel(GL.GL_FLAT);

        offset = gl.glGenLists(3);
        gl.glNewList(offset, GL.GL_COMPILE);
        gl.glDisable(GL.GL_LINE_STIPPLE);
        gl.glEndList();
        gl.glNewList(offset + 1, GL.GL_COMPILE);
        gl.glEnable(GL.GL_LINE_STIPPLE);
        gl.glLineStipple(1, (short) 0x0F0F);
        gl.glEndList();
        gl.glNewList(offset + 2, GL.GL_COMPILE);
        gl.glEnable(GL.GL_LINE_STIPPLE);
        gl.glLineStipple(1, (short) 0x1111);
        gl.glEndList();
    }

    public void display(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();
        //
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);

        /* draw all lines in white */
        gl.glColor3f(1.0f, 1.0f, 1.0f);

        gl.glCallList(offset);
        drawOneLine(gl, 50.0f, 125.0f, 350.0f, 125.0f);
        gl.glCallList(offset + 1);
        drawOneLine(gl, 50.0f, 100.0f, 350.0f, 100.0f);
        gl.glCallList(offset + 2);
        drawOneLine(gl, 50.0f, 75.0f, 350.0f, 75.0f);
        gl.glCallList(offset + 1);
        drawOneLine(gl, 50.0f, 50.0f, 350.f, 50.0f);
        gl.glCallList(offset);
        drawOneLine(gl, 50.0f, 25.0f, 350.0f, 25.0f);
        gl.glFlush();
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
        GL gl = drawable.getGL();
        //
        gl.glViewport(0, 0, w, h);
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrtho(0, 400, 0, 200, -1, 1);
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
            boolean deviceChanged) {
    }

    private void drawOneLine(GL gl, float x1, float y1, float x2, float y2) {
        gl.glBegin(GL.GL_LINES);
        gl.glVertex2f(x1, y1);
        gl.glVertex2f(x2, y2);
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
