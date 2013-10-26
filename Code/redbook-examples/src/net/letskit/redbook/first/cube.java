package net.letskit.redbook.first;
import net.letskit.redbook.glskeleton;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLJPanel;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;

import com.sun.opengl.util.GLUT;

/**
 * This program demonstrates a single modeling transformation, glScalef() and a
 * single viewing transformation, gluLookAt(). A wireframe cube is rendered.
 *
 * @author Kiet Le (Java conversion)
 */
public class cube //
        extends glskeleton//
        implements GLEventListener//
        , KeyListener //
{
    private GLU glu;
    private GLUT glut;
    
    public cube() {
    }
    
    public static void main(String[] args) {
        GLCapabilities caps = new GLCapabilities();
        GLJPanel canvas = new GLJPanel(caps);
        cube demo = new cube();
        canvas.addGLEventListener(demo);
        demo.setCanvas(canvas);
        demo.setDefaultListeners(demo);
        
//        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("cube");
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
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        gl.glShadeModel(GL.GL_FLAT);
    }
    
    /*
     * Clear the screen. Set the current color to white. Draw the wire frame
     * cube.
     */
    public void display(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();
        //
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        gl.glColor3f(1.0f, 1.0f, 1.0f);
        gl.glLoadIdentity(); /* clear the matrix */
        /* viewing transformation */
        glu.gluLookAt(0.0, 0.0, 5.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0);
        gl.glScalef(1.0f, 2.0f, 1.0f); /* modeling transformation */
        glut.glutWireCube(1.0f);
        gl.glFlush();
        
    }
    
    /*
     * Called when the window is first opened and whenever the window is
     * reconfigured (moved or resized).
     */
    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
        GL gl = drawable.getGL();
        //
        gl.glMatrixMode(GL.GL_PROJECTION); /* prepare for and then */
        gl.glLoadIdentity(); /* define the projection */
        gl.glFrustum(-1.0, 1.0, -1.0, 1.0, 1.5, 20.0); /* transformation */
        gl.glMatrixMode(GL.GL_MODELVIEW); /* back to modelview matrix */
        gl.glViewport(0, 0, w, h); /* define the viewport */
    }
    
    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
            boolean deviceChanged) {
    }
    
    public void keyTyped(KeyEvent arg0) {
    }
    
    public void keyPressed(KeyEvent key) {
        switch (key.getKeyCode()) {
            case KeyEvent.VK_ESCAPE:
                super.runExit();
            default:
                break;
        }
    }
    
    public void keyReleased(KeyEvent arg0) {
    }
    
}
