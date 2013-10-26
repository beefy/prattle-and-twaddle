package net.letskit.redbook.third;
import net.letskit.redbook.glskeleton;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLJPanel;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;
import javax.swing.JFrame;

/**
 * This program demonstrates the use of some of the gluQuadric* routines.
 * Quadric objects are created with some quadric properties and the callback
 * routine to handle errors. Note that the cylinder has no top or bottom and the
 * circle has a hole in it.
 *
 * @author Kiet Le (java port)
 */

public class quadric //
        extends glskeleton//
        implements GLEventListener//
        , KeyListener //
{
    private GLU glu;
    
    private int startList;
    
    //
    public quadric() {
    }
    
    public static void main(String[] args) {
        GLCapabilities caps = new GLCapabilities();
        GLJPanel canvas = new GLJPanel(caps);
        
        quadric demo = new quadric();
        canvas.addGLEventListener(demo);
        demo.setCanvas(canvas);
        demo.setDefaultListeners(demo);
        
//        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("quadric");
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
        GLUquadric qobj;
        float mat_ambient[] = { 0.5f, 0.5f, 0.5f, 1.0f };
        float mat_specular[] = { 1.0f, 1.0f, 1.0f, 1.0f };
        float mat_shininess[] = { 50.0f };
        float light_position[] = { 1.0f, 1.0f, 1.0f, 0.0f };
        float model_ambient[] = { 0.5f, 0.5f, 0.5f, 1.0f };
        
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        
        gl.glMaterialfv(GL.GL_FRONT, GL.GL_AMBIENT, mat_ambient, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL.GL_SPECULAR, mat_specular, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL.GL_SHININESS, mat_shininess, 0);
        gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, light_position, 0);
        gl.glLightModelfv(GL.GL_LIGHT_MODEL_AMBIENT, model_ambient, 0);
        
        gl.glEnable(GL.GL_LIGHTING);
        gl.glEnable(GL.GL_LIGHT0);
        gl.glEnable(GL.GL_DEPTH_TEST);
        
        /*
         * Create 4 display lists, each with a different quadric object.
         * Different drawing styles and surface normal specifications are
         * demonstrated.
         */
        startList = gl.glGenLists(4);
        qobj = glu.gluNewQuadric();
        
        /*
         * glu.gluQuadricCallback(qobj, GLU.GLU_ERROR, errorCallback); <br>
         * Quadric call backs have yet been implemented in JOGL. But this
         * program still work.
         */
        glu.gluQuadricDrawStyle(qobj, GLU.GLU_FILL); /* smooth shaded */
        glu.gluQuadricNormals(qobj, GLU.GLU_SMOOTH);
        gl.glNewList(startList, GL.GL_COMPILE);
        glu.gluSphere(qobj, 0.75, 15, 10);
        gl.glEndList();
        
        glu.gluQuadricDrawStyle(qobj, GLU.GLU_FILL); /* flat shaded */
        glu.gluQuadricNormals(qobj, GLU.GLU_FLAT);
        gl.glNewList(startList + 1, GL.GL_COMPILE);
        glu.gluCylinder(qobj, 0.5, 0.3, 1.0, 15, 5);
        gl.glEndList();
        
        glu.gluQuadricDrawStyle(qobj, GLU.GLU_LINE); /*
         * all polygons
         * wireframe
         */
        glu.gluQuadricNormals(qobj, GLU.GLU_NONE);
        gl.glNewList(startList + 2, GL.GL_COMPILE);
        glu.gluDisk(qobj, 0.25, 1.0, 20, 4);
        gl.glEndList();
        
        glu.gluQuadricDrawStyle(qobj, GLU.GLU_SILHOUETTE); /* boundary only */
        glu.gluQuadricNormals(qobj, GLU.GLU_NONE);
        gl.glNewList(startList + 3, GL.GL_COMPILE);
        glu.gluPartialDisk(qobj, 0.0, 1.0, 20, 4, 0.0, 225.0);
        gl.glEndList();
        
    }
    
    public void display(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();
        
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        gl.glPushMatrix();
        
        gl.glEnable(GL.GL_LIGHTING);
        gl.glShadeModel(GL.GL_SMOOTH);
        gl.glTranslatef(-1.0f, -1.0f, 0.0f);
        gl.glCallList(startList);
        
        gl.glShadeModel(GL.GL_FLAT);
        gl.glTranslatef(0.0f, 2.0f, 0.0f);
        gl.glPushMatrix();
        gl.glRotatef(300.0f, 1.0f, 0.0f, 0.0f);
        gl.glCallList(startList + 1);
        gl.glPopMatrix();
        
        gl.glDisable(GL.GL_LIGHTING);
        gl.glColor3f(0.0f, 1.0f, 1.0f);
        gl.glTranslatef(2.0f, -2.0f, 0.0f);
        gl.glCallList(startList + 2);
        
        gl.glColor3f(1.0f, 1.0f, 0.0f);
        gl.glTranslatef(0.0f, 2.0f, 0.0f);
        gl.glCallList(startList + 3);
        
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
                
            default: break;
        }
    }
    
    public void keyReleased(KeyEvent key) {
    }
    
}
