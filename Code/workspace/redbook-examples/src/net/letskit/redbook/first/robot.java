package net.letskit.redbook.first;
import net.letskit.redbook.glskeleton;

import java.awt.event.*;

import javax.swing.*;

import javax.media.opengl.*;
import javax.media.opengl.glu.*;
import com.sun.opengl.util.*;

public class robot//
        extends glskeleton//
        implements GLEventListener//
        , KeyListener//
{
    private GLU glu;
    private GLUT glut;
    private static int shoulder = 0, elbow = 0;
    
    //
    /**
     * This program shows how to composite modeling transformations to draw
     * translated and rotated hierarchical models. Interaction: pressing the s
     * and e keys (shoulder and elbow) alters the rotation of the robot arm.
     *
     * @author Kiet Le (Java conversion)
     */
    public robot() {
        
    }
    
    public static void main(String[] args) {
        GLCapabilities caps = new GLCapabilities();
        GLJPanel canvas = new GLJPanel(caps);
        robot demo = new robot();
        canvas.addGLEventListener(demo);
        canvas.addKeyListener(demo);
        //
//        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("robot");
        frame.setSize(400, 400);
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
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        gl.glShadeModel(GL.GL_FLAT);
    }
    
    public void display(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();
        //
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        gl.glColor3f(1.0f, 1.0f, 1.0f);
        
        gl.glPushMatrix();
        gl.glTranslatef(-1.0f, 0.0f, 0.0f);
        gl.glRotatef((float) shoulder, 0.0f, 0.0f, 1.0f);
        gl.glTranslatef(1.0f, 0.0f, 0.0f);
        // gl.glPushMatrix();
        gl.glScalef(2.0f, 0.4f, 1.0f);
        glut.glutWireCube(1.0f);
        // gl.glPopMatrix();
        
        gl.glTranslatef(1.0f, 0.0f, 0.0f);
        gl.glRotatef((float) elbow, 0.0f, 0.0f, 1.0f);
        gl.glTranslatef(1.0f, 0.0f, 0.0f);
        // gl.glPushMatrix();
        gl.glScalef(2.0f, 0.4f, 1.0f);
        glut.glutWireCube(1.0f);
        // gl.glPopMatrix();
        
        gl.glPopMatrix();
        
        gl.glFlush();
    }
    
    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
        GL gl = drawable.getGL();
        //
        gl.glViewport(0, 0, w, h);
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(65.0, (float) w / (float) h, 1.0, 20.0);
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadIdentity();
        gl.glTranslatef(0.0f, 0.0f, -5.0f);
    }
    
    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
            boolean deviceChanged) {
    }
    
    private void elbowAdd() {
        elbow = (elbow + 5) % 360;
    }
    
    private void elbowSubtract() {
        elbow = (elbow - 5) % 360;
    }
    
    private void shoulderAdd() {
        shoulder = (shoulder + 5) % 360;
    }
    
    private void shoulderSubtract() {
        shoulder = (shoulder - 5) % 360;
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
                shoulderSubtract();
                break;
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D:
                shoulderAdd();
                break;
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:
                elbowAdd();
                break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
                elbowSubtract();
                break;
                
            default:
                break;
        }
        super.refresh();
    }
    
    public void keyReleased(KeyEvent key) {
    }
    
}
