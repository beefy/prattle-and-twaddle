package net.letskit.redbook.first;

import java.awt.event.*;

import javax.swing.*;

import javax.media.opengl.*;
import javax.media.opengl.glu.*;
import com.sun.opengl.util.*;
import net.letskit.redbook.glskeleton;

/**
 * This program draws filled polygons with antialiased edges. The special
 * GL_SRC_ALPHA_SATURATE blending function is used. Pressing the left mouse
 * button turns the antialiasing on and off.
 *
 * @author Kiet Le (Java conversion)
 */
public class antipoly //
        extends glskeleton//
        implements GLEventListener//
        , KeyListener//
        , MouseListener //
{
    private GLU glu;
    private GLUT glut;
    private boolean polySmooth;
    
    
    public static void main(String[] args) {
        GLCapabilities caps = new GLCapabilities();
        caps.setSampleBuffers(true);// enable sample buffers for aliasing
        caps.setNumSamples(caps.getNumSamples() * 2);
        
        GLJPanel canvas = new GLJPanel(caps);
        antipoly demo = new antipoly();
        demo.setCanvas(canvas);
        canvas.addGLEventListener(demo);
        demo.setDefaultListeners(demo);
        
//        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("antipoly");
        frame.setSize(200, 200);
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
        float mat_ambient[] = { 0.0f, 0.0f, 0.0f, 1.00f };
        float mat_specular[] = { 1.0f, 1.0f, 1.0f, 1.00f };
        float mat_shininess[] = { 15.0f };
        
        gl.glMaterialfv(GL.GL_FRONT, GL.GL_AMBIENT, mat_ambient, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL.GL_SPECULAR, mat_specular, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL.GL_SHININESS, mat_shininess, 0);
        
        gl.glEnable(GL.GL_LIGHTING);
        gl.glEnable(GL.GL_LIGHT0);
        gl.glEnable(GL.GL_BLEND);
        gl.glCullFace(GL.GL_BACK);
        gl.glEnable(GL.GL_CULL_FACE);
        gl.glEnable(GL.GL_POLYGON_SMOOTH);
        polySmooth = true;
        
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    }
    
    /*
     * Note: polygons must be drawn from back to front for proper blending.
     */
    public void display(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();
        //
        float position[] = { 0.0f, 0.0f, 1.0f, 0.0f };
        float mat_cube1[] = { 0.75f, 0.75f, 0.0f, 1.0f };
        float mat_cube2[] = { 0.0f, 0.75f, 0.75f, 1.0f };
        
        if (polySmooth)
            gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        else
            gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        
        toggleSmooth(gl);
        
        gl.glPushMatrix();
        
        gl.glTranslatef(0.0f, 0.0f, -8.0f);
        gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, position, 0);
        
        gl.glBlendFunc(GL.GL_SRC_ALPHA_SATURATE, GL.GL_ONE);
        
        gl.glPushMatrix();
        gl.glRotatef(30.0f, 1.0f, 0.0f, 0.0f);
        gl.glRotatef(60.0f, 0.0f, 1.0f, 0.0f);
        gl.glMaterialfv(GL.GL_FRONT, GL.GL_DIFFUSE, mat_cube1, 0);
        glut.glutSolidCube(1.0f);
        gl.glPopMatrix();
        
        gl.glTranslatef(0.0f, 0.0f, -2.0f);
        gl.glMaterialfv(GL.GL_FRONT, GL.GL_DIFFUSE, mat_cube2, 0);
        gl.glRotatef(30.0f, 0.0f, 1.0f, 0.0f);
        gl.glRotatef(60.0f, 1.0f, 0.0f, 0.0f);
        glut.glutSolidCube(1.0f);
        gl.glPopMatrix();
        
//        gl.glFlush();
        gl.glFinish();
        //drawable.swapBuffers();
    }
    
    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
        GL gl = drawable.getGL();
        //
        gl.glViewport(0, 0, w, h);
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(30.0, (float) w / (float) h, 1.0, 20.0);
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadIdentity();
    }
    
    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
            boolean deviceChanged) {
    }
    
    private void toggleSmooth(GL gl) {
        if (polySmooth) {
            gl.glDisable(GL.GL_BLEND);
            gl.glDisable(GL.GL_POLYGON_SMOOTH);
            gl.glEnable(GL.GL_DEPTH_TEST);
        } else {
            gl.glEnable(GL.GL_BLEND);
            gl.glEnable(GL.GL_POLYGON_SMOOTH);
            gl.glDisable(GL.GL_DEPTH_TEST);
        }
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
    
    public void mouseClicked(MouseEvent mouse) {
    }
    
    public void mousePressed(MouseEvent mouse) {
        if (mouse.getButton() == MouseEvent.BUTTON1) {
            polySmooth = !polySmooth;
            System.out.println(polySmooth);
        }
        
        super.refresh();
    }
    
    public void mouseReleased(MouseEvent mouse) {
    }
    
    public void mouseEntered(MouseEvent mouse) {
    }
    
    public void mouseExited(MouseEvent mouse) {
    }
    
}
