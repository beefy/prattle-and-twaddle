package net.letskit.redbook.first;
import net.letskit.redbook.glskeleton;

import java.awt.event.*;
import javax.swing.*;
import javax.media.opengl.*;
import javax.media.opengl.glu.*;
import com.sun.opengl.util.*;

/**
 * The planets (from planet.c) have been rotated so their polar regions are
 * north/south. Interaction: pressing the left, right, up, and down arrow keys
 * alters the rotation of the planet around the sun.
 *
 * @author Kiet Le (Java conversion)
 */
public class planetup//
        extends glskeleton//
        implements GLEventListener//
        , KeyListener//
{
    private GLU glu;
    private GLUT glut;
    private static int year = 0, day = 0;
    
    //
    public planetup() {
    }
    
    public static void main(String[] args) {
        
        GLCapabilities caps = new GLCapabilities();
        GLJPanel canvas = new GLJPanel(caps);
        planetup demo = new planetup();
        demo.setCanvas(canvas);
        canvas.addGLEventListener(demo);
        canvas.addKeyListener(demo);
        //
//        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("planetup");
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
    
    public void display(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();
        //
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        
        gl.glColor3f(1.0f, 1.0f, 1.0f);
        gl.glPushMatrix();
        /* draw sun */
        gl.glPushMatrix();
        gl.glRotatef(90.0f, 1.0f, 0.0f, 0.0f); /* rotate it upright */
        glut.glutWireSphere(1.0f, 10, 10);
        gl.glPopMatrix();
        /* draw smaller planet */
        gl.glRotatef((float) year, 0.0f, 1.0f, 0.0f);
        gl.glTranslatef(2.0f, 0.0f, 0.0f);
        gl.glRotatef((float) day, 0.0f, 1.0f, 0.0f);
        gl.glRotatef(90.0f, 1.0f, 0.0f, 0.0f); /* rotate it upright */
        glut.glutWireSphere(0.2f, 10, 10);
        gl.glPopMatrix();
        gl.glFlush();
    }
    
    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
        GL gl = drawable.getGL();
        //
        gl.glViewport(0, 0, w, h);
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(60.0, (float) w / (float) h, 1.0, 20.0);
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadIdentity();
        gl.glTranslatef(0.0f, 0.0f, -5.0f);
    }
    
    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
            boolean deviceChanged) {
    }
    
    private void dayAdd() {
        day = (day + 10) % 360;
    }
    
    private void daySubtract() {
        day = (day - 10) % 360;
    }
    
    private void yearAdd() {
        year = (year + 5) % 360;
    }
    
    private void yearSubtract() {
        year = (year - 5) % 360;
    }
    
    public void keyTyped(KeyEvent key) {
    }
    
    public void keyPressed(KeyEvent key) {
        switch (key.getKeyCode()) {
            case KeyEvent.VK_ESCAPE:
                super.runExit();
                break;
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A:
                yearSubtract();
                break;
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D:
                yearAdd();
                break;
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:
                dayAdd();
                break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
                daySubtract();
                break;
                
            default:
                break;
        }
        super.refresh();
    }
    
    public void keyReleased(KeyEvent key) {
    }
    
}
