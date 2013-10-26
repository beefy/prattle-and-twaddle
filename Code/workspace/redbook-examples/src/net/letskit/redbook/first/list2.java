package net.letskit.redbook.first;
import net.letskit.redbook.glskeleton;

import java.awt.event.*;
import javax.swing.*;

import javax.media.opengl.*;
import javax.media.opengl.glu.*;

/**
 * This program demonstrates glGenList() and glPushAttrib(). The matrix and
 * color are restored, before the line is drawn.
 *
 * @author Kiet Le (java port)
 */
public class list2//
        extends glskeleton//
        implements GLEventListener//
        , KeyListener//
{
    private GLU glu;
    private int listName;
    
    //
    public list2() {
    }
    
    public static void main(String[] args) {
        GLCapabilities caps = new GLCapabilities();
        GLJPanel canvas = new GLJPanel(caps);
        list2 demo = new list2();
        canvas.addGLEventListener(demo);
        demo.setCanvas(canvas);
        demo.setDefaultListeners(demo);
        
//        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("list2");
        frame.setSize(400, 50);
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
        float color_vector[] = { 1.0f, 0.0f, 0.0f };
        
        listName = gl.glGenLists(1);
        gl.glNewList(listName, GL.GL_COMPILE);
        gl.glPushAttrib(GL.GL_CURRENT_BIT);
        gl.glColor3fv(color_vector, 0);
        gl.glBegin(GL.GL_TRIANGLES);
        gl.glVertex2f(0.0f, 0.0f);
        gl.glVertex2f(1.0f, 0.0f);
        gl.glVertex2f(0.0f, 1.0f);
        gl.glEnd();
        gl.glTranslatef(1.5f, 0.0f, 0.0f);
        gl.glPopAttrib();
        gl.glEndList();
        gl.glShadeModel(GL.GL_FLAT);
    }
    
    public void display(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();
        //
        float new_color[] = { 0.0f, 1.0f, 0.0f };
        
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        gl.glColor3fv(new_color, 0);
        gl.glPushMatrix();
        for (int i = 0; i < 10; i++)
            gl.glCallList(listName);
        gl.glPopMatrix();
        drawLine(gl);
        gl.glFlush();
    }
    
    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
        GL gl = drawable.getGL();
        //
        gl.glViewport(0, 0, w, h);
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        if (w <= h)
            glu.gluOrtho2D(0.0, 2.0, -0.5 * (float) h / (float) w, 1.5
                    * (float) h / (float) w);
        else
            glu.gluOrtho2D(0.0, 2.0 * (float) w / (float) h, -0.5, 1.5);
        gl.glMatrixMode(GL.GL_MODELVIEW);
    }
    
    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
            boolean deviceChanged) {
    }
    
    private void drawLine(GL gl) {
        gl.glBegin(GL.GL_LINES);
        gl.glVertex2f(0.0f, 0.5f);
        gl.glVertex2f(15.0f, 0.5f);
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
